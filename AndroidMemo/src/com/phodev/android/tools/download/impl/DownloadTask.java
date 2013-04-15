package com.phodev.android.tools.download.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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

import android.annotation.SuppressLint;
import android.os.Environment;

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
	private List<DownloadBlock> mBlocks;
	private List<Future<BlockDownloader>> mLoaders = new ArrayList<Future<BlockDownloader>>();
	private ExecutorService mExecutor;
	private DownloadFile mDownloadFile;
	private DownloadTaskListener mDownloadTaskListener;
	private boolean isRunning = false;
	private Future<TaskRunner> curTaskFuture;

	public DownloadTask(ExecutorService executor, DownloadFile df,
			DownloadTaskListener l) {
		mExecutor = executor;
		mDownloadFile = df;
		mDownloadTaskListener = l;
	}

	public synchronized boolean start() {
		// 如果已经启动了，则直接返回,否则开启任务
		if (isRunning) {
			return true;
		}
		if (mExecutor == null || mExecutor.isShutdown()) {
			return false;
		}
		TaskRunner taskRunner = new TaskRunner();
		curTaskFuture = mExecutor.submit(taskRunner, taskRunner);
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
					runner.cancel();
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

	private BlockRunnerListener blockListener = new BlockRunnerListener() {

		@Override
		public void onBlockLoadFailed(DownloadBlock block, int errorCode) {

		}

		@Override
		public void onBlockLoadDone(DownloadBlock block) {

		}

		@Override
		public void onBlockIncrease(DownloadBlock block) {

		}
	};

	public interface DownloadTaskListener {
		public void onDownloadIncrease(DownloadTask task, long fileSize,
				long loadedSize, long speed);

		public void onDownloadDone(DownloadTask task);

		public void onDownloadFailed(DownloadTask task);
	}

	//
	class TaskRunner implements Runnable {
		private boolean interrupt = false;

		/**
		 * 取消任务
		 */
		public void cancel() {
			interrupt = true;
		}

		@Override
		public void run() {
			// 1,查询是否已经缓存过Block信息
			// 1-a)如果没有，则连接分析source创建并缓存block
			String sourceUrl = mDownloadFile.getSourceUrl();
			URL url = null;
			try {
				url = new URL(sourceUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			createBlockFromNetFile(mDownloadFile, url);
			// 1-b)加载blocks
			if (mBlocks == null || mBlocks.size() <= 0) {
				// 如果依然没有block信息，返回错误
				return;
			}
			for (DownloadBlock block : mBlocks) {
				if (mExecutor.isShutdown()) {
					// error
					break;
				}
				BlockDownloader bder = new BlockDownloader(blockListener, block);
				Future<BlockDownloader> f = mExecutor.submit(bder, bder);
				mLoaders.add(f);
			}
			// while (!interrupt) {
			// // 更新进度和速度//block进度的缓存
			// //
			// }
		}

		private void createBlockFromNetFile(DownloadFile file, URL url) {
			if (url == null) {
				return;
			}
			try {
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				Utils.configCommonHeader(c, file.getSourceUrl());
				c.connect();
				//
				if (c.getResponseCode() == 200) {
					file.setFileSize(c.getContentLength());
				}
				// 分段
				final int thradCount = Constants.thread_count;
				long perBlockSize = file.getFileSize() / thradCount;
				if (mBlocks == null) {
					mBlocks = new ArrayList<DownloadBlock>();
				} else {
					mBlocks.clear();
				}
				String filename = getFileName(c, file.getSourceUrl());
				file.setFileName(filename);
				String path = Environment.getExternalStorageDirectory()
						+ "/testd/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdir();
				}
				File outFile = new File(path + filename);
				if (!outFile.exists()) {
					outFile.createNewFile();
				}
				// RandomAccessFile randOut = new RandomAccessFile(outFile,
				// "rw");
				// randOut.setLength(file.getFileSize());

				for (int i = 0; i < thradCount; i++) {
					long end = i == (thradCount - 1) ? file.getFileSize()
							: (i + 1) * perBlockSize;
					DownloadBlock block = new DownloadBlock(i,
							DownloadTask.this, url, file.getSourceUrl(),
							outFile, i * perBlockSize, end, 0);
					mBlocks.add(block);
				}
			} catch (IOException e) {
				e.printStackTrace();
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
}
