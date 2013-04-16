package com.phodev.android.tools.download.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import com.phodev.android.tools.download.Constants;
import com.phodev.android.tools.download.Utils;

/**
 * 文件分段加载器
 * 
 * @author skg
 * 
 */
public class BlockDownloader implements Runnable {
	private String TAG = "BlockDownloader";
	private final static int buffer_size = Constants.block_read_buffer_size;
	private final static String out_file_mode = "rwd";
	private DownloadBlock mBlock;
	private BlockRunnerListener mListener;
	private boolean interrupt = false;

	public BlockDownloader(BlockRunnerListener listener, DownloadBlock block) {
		mBlock = block;
		mListener = listener;
	}

	public void interrupt() {
		interrupt = true;
	}

	public boolean isInterrupt() {
		return interrupt;
	}

	@Override
	public void run() {
		if (Constants.DEBUG) {
			log("block start load");
		}
		// check--------------
		if (mBlock == null) {
			makeTerminationOnError(BlockRunnerListener.ERROR_INVALID_BALOCK);
			return;
		}
		long startPos = mBlock.getStart();
		long endPos = mBlock.getEnd();
		if (startPos < 0 || endPos < 0 || endPos < startPos) {
			makeTerminationOnError(BlockRunnerListener.ERROR_INVALID_BALOCK);
			if (Constants.DEBUG) {
				log("invalid range:" + startPos + " endPos:" + endPos);
			}
			return;
		}
		final String sourceUrl = mBlock.getSourceUrl();
		//
		File file = mBlock.getOutFile();
		if (file == null) {
			return;
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				makeTerminationOnError(BlockRunnerListener.ERROR_INVALID_BALOCK);
				return;
			}
		}
		URL url;
		try {
			url = new URL(sourceUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			makeTerminationOnError(BlockRunnerListener.ERROR_INVALID_BALOCK);
			return;
		}
		// check end--------------
		//
		try {
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			Utils.configBlockHeader(http, sourceUrl, startPos, endPos);
			if (Constants.DEBUG) {
				log("set block range:" + startPos + "-" + endPos);
			}
			RandomAccessFile outfile = new RandomAccessFile(file, out_file_mode);
			outfile.seek(startPos);
			//
			int read = 0;
			byte[] buffer = new byte[buffer_size];
			InputStream is = http.getInputStream();
			Utils.debugPrintResponseHeader(http, TAG);
			Log.e(TAG, "content encoding:" + http.getContentEncoding());
			while (!interrupt && (read = is.read(buffer, 0, buffer_size)) != -1) {
				if (interrupt) {
					break;
				}
				outfile.write(buffer, 0, read);
				mBlock.updateBlock(mBlock.getLoadedSize() + read);
				// if (Constants.DEBUG) {
				// log("block Increase current:" + mBlock.getCurrent());
				// }
				// 通知下载进度
				if (mListener != null) {
					mListener.onBlockIncrease(mBlock, read);
				}
			}
			outfile.close();
			is.close();
			if (Constants.DEBUG) {
				log("block load finish interrupt:" + interrupt);
			}
			if (!interrupt && mListener != null) {
				mListener.onBlockLoadDone(mBlock);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			makeTerminationOnError(BlockRunnerListener.ERROR_NET_WORK_BAK);
		} catch (IOException e) {
			e.printStackTrace();
			makeTerminationOnError(BlockRunnerListener.ERROR_NET_WORK_BAK);
		} catch (Exception e) {
			e.printStackTrace();
			makeTerminationOnError(BlockRunnerListener.ERROR_NET_WORK_BAK);
		}
	}

	private void makeTerminationOnError(int errorCode) {
		if (mListener != null) {
			mListener.onBlockLoadFailed(mBlock, errorCode);
		}
		interrupt();
	}

	public interface BlockRunnerListener {
		public final static int ERROR_INVALID_BALOCK = 1;
		public final static int ERROR_NET_WORK_BAK = 2;

		public void onBlockIncrease(DownloadBlock block, int increase);

		public void onBlockLoadDone(DownloadBlock block);

		public void onBlockLoadFailed(DownloadBlock block, int errorCode);
	}

	void log(Object msg) {
		String blockId = null;
		String url = null;
		if (mBlock != null) {
			blockId = mBlock.getId();
			url = mBlock.getSourceUrl();
		}
		Log.d(TAG, "block--id:" + blockId + ",url:" + url);
		Log.d(TAG, "block--msg:" + msg);
	}
}
