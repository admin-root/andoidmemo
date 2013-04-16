package com.phodev.android.tools.download.impl;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

import com.phodev.android.tools.download.Constants;
import com.phodev.android.tools.download.DownloadFile;
import com.phodev.android.tools.download.Utils;
import com.phodev.android.tools.download.impl.BlockDownloader.BlockRunnerListener;

/**
 * 管理一个下载任务
 * 
 * @author skg
 * 
 */
public class DownloadTask {
	private List<DownloadBlock> blocks = new ArrayList<DownloadBlock>();
	private ExecutorService executor;
	private DownloadFile downloadFile;
	private DownloadTaskListener taskListener;
	private boolean isRunning = false;
	private Future<TaskRunner> curTaskFuture;
	private Context context;

	public DownloadTask(Context context, ExecutorService executor,
			DownloadFile df, DownloadTaskListener l) {
		this.context = context;
		this.executor = executor;
		downloadFile = df;
		taskListener = l;
	}

	public synchronized boolean start() {
		// 如果已经启动了，则直接返回,否则开启任务
		if (isRunning) {
			return true;
		}
		if (executor == null || executor.isShutdown()) {
			return false;
		}
		TaskRunner taskRunner = new TaskRunner();
		curTaskFuture = executor.submit(taskRunner, taskRunner);
		return true;
	}

	public synchronized boolean stop() {
		// 停止任务,并取消和线程的绑定,并移除block runner，再次启动的时候创建新的block runner
		if (!isRunning) {
			return true;
		}
		if (curTaskFuture != null) {
			try {
				TaskRunner runner = curTaskFuture.get();
				if (runner != null) {
					runner.stop();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			if (!curTaskFuture.isCancelled()) {
				curTaskFuture.cancel(true);
			}
			isRunning = false;
		}
		return true;
	}

	/**
	 * 判断是否正在运行
	 * 
	 * @return
	 */
	public synchronized boolean isRunning() {
		return isRunning;
	}

	private int blockTotalIncrease = 0;
	private BlockRunnerListener blockListener = new BlockRunnerListener() {

		@Override
		public synchronized void onBlockLoadFailed(DownloadBlock block,
				int errorCode) {
			// Block下载失败,判断原因看是否需要开启线程继续下载
			// 1,如果是网络部可用，则停止全部下载,并通知外部监听
			// 2,如果是连接超时则尝试重新加载
		}

		@Override
		public synchronized void onBlockLoadDone(DownloadBlock block) {
			// 块下载完成，通常情况下这个时候不需要做特殊处理
		}

		@Override
		public synchronized void onBlockIncrease(DownloadBlock block,
				int increase) {
			// 块下载任务进度增加,累加当符合条件的时候同意向外通知进度
			blockTotalIncrease += increase;
		}
	};

	class TaskRunner implements Runnable {
		List<Future<BlockDownloader>> loaders = new ArrayList<Future<BlockDownloader>>();
		private boolean interrupt = false;

		/**
		 * 取消任务
		 */
		public void stop() {
			if (interrupt) {
				return;
			}
			interrupt = true;
			for (Future<BlockDownloader> f : loaders) {
				if (f != null && !f.isCancelled() && !f.isDone()) {
					try {
						f.get().interrupt();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					f.cancel(true);
				}
			}
			loaders.clear();
		}

		@Override
		public void run() {
			String sourceUrl = downloadFile.getSourceUrl();
			URL url = null;
			try {
				url = new URL(sourceUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				// error
				return;
			}
			synchronized (blocks) {
				// loadBlocksFromCache
				DownloadRecorder dr = DownloadRecorder.getInstance();
				dr.getBocks(context, blocks, downloadFile);
				//
				if (blocks.size() <= 0) {
					createBlockFromNetFile(downloadFile, url);
					if (blocks.size() <= 0) {
						// error
						return;
					}
				}
			}
			//
			for (DownloadBlock block : blocks) {
				if (executor.isShutdown()) {
					// error
					break;
				}
				BlockDownloader bder = new BlockDownloader(blockListener, block);
				Future<BlockDownloader> f = executor.submit(bder, bder);
				loaders.add(f);
			}
			//
			loopUpdateDownloadInfo();
		}

		/**
		 * 从网络加载分段信息
		 * 
		 * @param file
		 * @param url
		 */
		private void createBlockFromNetFile(DownloadFile file, URL url) {
			if (url == null) {
				return;
			}
			try {
				// 分段
				final int threadCount = Constants.thread_count;
				long perBlockSize = file.getFileSize() / threadCount;
				blocks.clear();
				//
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				Utils.configCommonHeader(c, file.getSourceUrl());
				c.connect();
				//
				if (c.getResponseCode() == 200) {
					file.setFileSize(c.getContentLength());
				} else {
					return;
				}
				String filename = getFileName(c, file.getSourceUrl());
				file.setFileName(filename);
				File outFile = Utils.createDownloadOutFile(filename);
				if (!outFile.exists()) {
					outFile.createNewFile();
				}
				// RandomAccessFile randOut = new RandomAccessFile(outFile,
				// "rw");
				// randOut.setLength(file.getFileSize());
				//
				int lastThread = threadCount - 1;
				final String sourceUrl = file.getSourceUrl();
				for (int i = 0; i < threadCount; i++) {
					long start = i * perBlockSize;
					long end;
					if (i == lastThread) {
						end = file.getFileSize();
					} else {
						end = (i + 1) * perBlockSize;
					}
					DownloadBlock b = new DownloadBlock(file, sourceUrl,
							outFile, start, end, 0);
					blocks.add(b);
				}
				// 保存分段记录到数据库
				if (!blocks.isEmpty()) {
					DownloadRecorder.getInstance().addBlocks(context, blocks,
							file.getSourceUrl());
				}
			} catch (IOException e) {
				e.printStackTrace();
				blocks.clear();
				// 保存分段记录到数据库
			}
		}

		private void loopUpdateDownloadInfo() {
			while (!interrupt) {
				// 更新进度和速度//block进度的缓存
				try {
					Thread.sleep(900);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				final int speed = blockTotalIncrease;
				blockTotalIncrease = 0;
				int loadedSize = 0;
				for (DownloadBlock block : blocks) {
					loadedSize += block.getLoadedSize();
				}
				int tt = 1024 * 1024;
				long totalSize = downloadFile.getFileSize() / tt;
				Log.e("ttt", (loadedSize / tt) + "/" + totalSize + " speed:"
						+ speed / 1024 + "kb/s");

				// 控制优化
				DownloadRecorder.getInstance().updateBlockProgress(context,
						blocks);
			}
		}
	}

	private String getFileName(HttpURLConnection conn, String url) {
		String filename = null;
		if (url != null) {
			filename = url.substring(url.lastIndexOf('/') + 1);
		}
		if (filename == null || "".equals(filename.trim())) {
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null)
					break;
				if ("content-disposition".equals(conn.getHeaderFieldKey(i)
						.toLowerCase(Locale.getDefault()))) {
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(
							mine.toLowerCase(Locale.getDefault()));
					if (m.find())
						return m.group(1);
				}
			}
			filename = UUID.randomUUID() + ".tmp";
		}
		return filename;
	}

	public interface DownloadTaskListener {
		public void onDownloadIncrease(DownloadFile file, long loadedSize,
				long speed);

		public void onDownloadDone(DownloadFile file);

		public void onDownloadFailed(DownloadFile file);
	}

}
