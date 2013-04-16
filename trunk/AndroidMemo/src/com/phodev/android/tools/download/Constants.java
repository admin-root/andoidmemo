package com.phodev.android.tools.download;

public class Constants {
	private Constants() {
	}

	public static final boolean DEBUG = true;
	public static final int TIME_OUT = 1000 * 20;
	public static final String Accept = "image/gif, "//
			+ "image/jpeg, "//
			+ "image/pjpeg, "//
			+ "image/pjpeg, "//
			+ "application/x-shockwave-flash, "//
			+ "application/xaml+xml, "//
			+ "application/vnd.ms-xpsdocument, "//
			+ "application/x-ms-xbap, "//
			+ "application/x-ms-application, "//
			+ "application/vnd.ms-excel, "//
			+ "application/vnd.ms-powerpoint, "//
			+ "application/msword, "//
			+ "*/*";//
	public static final String Accept_Language = "*";
	public static final String User_Agent = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
	public static final int block_read_buffer_size = 1024 * 10 * 20;
	public static final int thread_count = 3;
	public static final String relative_download_path = "testd";
}
