package com.phodev.android.tools.download.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.phodev.android.tools.download.DownloadFile;
import com.phodev.android.tools.download.DownloadManager;
import com.phodev.android.tools.download.impl.DownloadTask.DownloadTaskListener;

/**
 * 任务下载的具体实现
 * 
 * @author skg
 * 
 */
public class DownloadManagerImpl implements DownloadManager {

	private DownloadRecorder mRecordManager;

	public DownloadManagerImpl(DownloadRecorder recordManager) {
		mRecordManager = recordManager;
	}

	private Map<Long, DownloadTask> tasks = new ConcurrentHashMap<Long, DownloadTask>();

	@Override
	public boolean start(String url) {
		return false;
	}

	@Override
	public boolean startAll() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopAll() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(String url, boolean removeLoadedFile) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(boolean removeLoadedFile) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DownloadFile> getLoadingFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DownloadFile> getLoadedFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerDownloadListener(DownloadListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDownloadListener(DownloadListener l) {
		// TODO Auto-generated method stub

	}

	private DownloadTaskListener downloadTaskListener = new DownloadTaskListener() {

		@Override
		public void onDownloadIncrease(DownloadTask task, long fileSize,
				long loadedSize, long speed) {
			// 下载任务进度变化，通常更新UI回调
		}

		@Override
		public void onDownloadDone(DownloadTask task) {

		}

		@Override
		public void onDownloadFailed(DownloadTask task) {

		}

	};
}
