package com.phodev.android.tools.download.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.phodev.android.tools.download.Constants;
import com.phodev.android.tools.download.DownloadFile;
import com.phodev.android.tools.download.DownloadManager;
import com.phodev.android.tools.download.impl.DownloadTask.DownloadTaskListener;

/**
 * 任务下载的具体实现
 * 
 */
public class DownloadManagerImpl implements DownloadManager {
	private Context mContext;
	private Map<String, DownloadTask> tasks = new ConcurrentHashMap<String, DownloadTask>();
	private List<DownloadFile> loadingFiles = new ArrayList<DownloadFile>();
	private List<DownloadFile> loadCompleteFiles = new ArrayList<DownloadFile>();
	private DownloadRecorder recorder;
	//
	public static final int MAX_CORE_POOL_SIZE = Constants.thread_count + 1;// 一直保留的线程数
	public static final int MAX_POOL_SIZE = 50;
	public static final int KEEP_ALIVE_TIME = 60;// s//允许空闲线程时间
	private final static ThreadPoolExecutor executor = new ThreadPoolExecutor(
			MAX_CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
			TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	public DownloadManagerImpl(Context context) {
		mContext = context;
		recorder = DownloadRecorder.getInstance();
		createTask(loadCompleteFiles, tasks);
	}

	/**
	 * 创建Task
	 * 
	 * @param files
	 * @param out
	 */
	private void createTask(List<DownloadFile> files,
			Map<String, DownloadTask> out) {
		if (files == null || files.isEmpty() || out == null) {
			return;
		}
		for (DownloadFile f : files) {
			if (f != null) {
				String k = f.getSourceUrl();
				DownloadTask v = new DownloadTask(mContext, executor, f,
						taskListener);
				if (k != null && v != null) {
					out.put(k, v);
				}
			}
		}
	}

	@Override
	public boolean start(String url) {
		return false;
	}

	@Override
	public boolean startAll() {
		return false;
	}

	@Override
	public boolean stop(String url) {
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

	private DownloadTaskListener taskListener = new DownloadTaskListener() {

		@Override
		public void onDownloadIncrease(DownloadFile file, long loadedSize,
				long speed) {
		}

		@Override
		public void onDownloadDone(DownloadFile file) {
		}

		@Override
		public void onDownloadFailed(DownloadFile file) {
		}

	};

	class DownloadFileMap {
		private List<DownloadFile> loadingFiles = new ArrayList<DownloadFile>();
		private List<DownloadFile> loadCompleteFiles = new ArrayList<DownloadFile>();
	}
}
