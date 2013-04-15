package com.phodev.android.tools.download.impl;

import java.io.File;
import java.net.URL;

/**
 * 下载块
 * 
 * @author skg
 * 
 */
public class DownloadBlock {
	private int id;
	private DownloadTask downloadTask;
	private URL sourceURL;
	private String sourceUrl;
	private File outFile;
	private long start;
	private long end;
	private long current;

	public DownloadBlock(int blockId, DownloadTask task, URL sourceURL,
			String sourceUrl, File outFile, long start, long end, long current) {
		this.id = blockId;
		this.downloadTask = task;
		this.sourceURL = sourceURL;
		this.sourceUrl = sourceUrl;
		this.outFile = outFile;
		this.start = start;
		this.end = end;
		this.current = current;
	}

	protected void updateBlock(long current) {
		// 更新自己的进度
		this.current = current;
	}

	public int getId() {
		return id;
	}

	public DownloadTask getDownloadTask() {
		return downloadTask;
	}

	public URL getSourceURL() {
		return sourceURL;
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

	public long getCurrent() {
		return current;
	}

}
