package com.phodev.android.tools.download;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.LinkedHashMap;
import java.util.Map;

import android.util.Log;

public class Utils {
	private Utils() {
	}

	public static void configBlockHeader(HttpURLConnection conn,
			String referer, long startPos, long endPos) {
		if (conn == null) {
			return;
		}
		configCommonHeader(conn, referer);
		conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
	}

	public static void configCommonHeader(HttpURLConnection conn, String referer) {
		if (conn == null) {
			return;
		}
		conn.setConnectTimeout(Constants.TIME_OUT);
		try {
			conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		conn.setRequestProperty("Accept", Constants.Accept);
		conn.setRequestProperty("Accept-Language", Constants.Accept_Language);
		if (referer != null) {
			conn.setRequestProperty("Referer", referer);
		}
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("User-Agent", Constants.User_Agent);
		conn.setRequestProperty("Connection", "Keep-Alive");
	}

	public static Map<String, String> getResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null)
				break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}

	public static void debugPrintResponseHeader(HttpURLConnection http,
			String tag) {
		if (Constants.DEBUG) {
			Map<String, String> header = getResponseHeader(http);
			for (Map.Entry<String, String> e : header.entrySet()) {
				Log.d(tag, "key-value:" + e.getKey() + ":" + e.getValue());
			}
		}
	}
}
