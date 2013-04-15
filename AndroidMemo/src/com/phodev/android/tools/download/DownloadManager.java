package com.phodev.android.tools.download;

import java.util.List;

/**
 * 下载任务管理器
 * 
 * @author skg
 * 
 */
public interface DownloadManager {

	/**
	 * 启动下载
	 * 
	 * @param url
	 * @return
	 */
	public boolean start(String url);

	/**
	 * 开启所有下载任务
	 * 
	 * @return
	 */
	public boolean startAll();

	/**
	 * 停止下载
	 * 
	 * @param url
	 * @return
	 */
	public boolean stop(String url);

	/**
	 * 停止所有下载任务
	 * 
	 * @return
	 */
	public boolean stopAll();

	/**
	 * 删除下载任务
	 * 
	 * @param url
	 * @param removeLoadedFile
	 *            是否要删除下载好的文件
	 * @return
	 */
	public boolean remove(String url, boolean removeLoadedFile);

	/**
	 * 移除所有下载任务
	 * 
	 * @param removeLoadedFile
	 * @return
	 */
	public boolean removeAll(boolean removeLoadedFile);

	/**
	 * 获取正在下载中的文件
	 * 
	 * @return
	 */
	public List<DownloadFile> getLoadingFiles();

	/**
	 * 获取已经加载好的文件
	 * 
	 * @return
	 */
	public List<DownloadFile> getLoadedFiles();

	/**
	 * 注册监听器
	 * 
	 * @param l
	 */
	public void registerDownloadListener(DownloadListener l);

	/**
	 * 取消注册监听器
	 * 
	 * @param l
	 */
	public void unregisterDownloadListener(DownloadListener l);

	public interface DownloadListener {
		/**
		 * 下载任务的Add,Remove,Start,Stop...
		 * 
		 * @param taskId
		 * @param newStatus
		 */
		public void onStatusChanged(long taskId, int newStatus);

		/**
		 * 下载任务进度的变化
		 * 
		 * @param taskId
		 * @param newProgrees
		 */
		public void onProgressChanged(long taskId, int newProgrees, int newSpeed);
	}
}