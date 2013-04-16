package com.phodev.android.tools.download.impl;

import java.io.File;

import com.phodev.android.tools.download.DownloadFile;

/**
 * 下载块
 * 
 * @author skg
 * 
 */
public class DownloadBlock {
	private String id;
	private DownloadFile downloadFile;
	private String sourceUrl;
	private File outFile;
	private long start;
	private long end;
	private long loadedSize;

	public DownloadBlock(DownloadFile dFile, String sourceUrl, File outFile,
			long start, long end, long loadedSize) {
		this.downloadFile = dFile;
		this.sourceUrl = sourceUrl;
		this.outFile = outFile;
		this.start = start;
		this.end = end;
		this.loadedSize = loadedSize;
	}

	protected void updateBlock(long loadedSize) {
		// 更新自己的进度
		this.loadedSize = loadedSize;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public DownloadFile getDownloadFile() {
		return downloadFile;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public File getOutFile() {
		return outFile;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getLoadedSize() {
		return loadedSize;
	}

}
