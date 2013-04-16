package com.phodev.android.tools.download;

/**
 * 下载文件描述信息
 */
public class DownloadFile {
	public final static int status_download_complete = 1;
	public final static int status_download_unfinished = 2;
	private String sourceUrl;
	private String fileName;
	private int status;
	private long fileSize;

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}
