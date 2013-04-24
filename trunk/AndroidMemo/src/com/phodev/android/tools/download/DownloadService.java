package com.phodev.android.tools.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import com.phodev.android.tools.download.impl.DownloadManagerImpl;

/**
 * Service包装的下载管理
 * 
 */
public class DownloadService extends Service {
	private IDownloadManager.Stub downloadManager;

	@Override
	public void onCreate() {
		super.onCreate();
		downloadManager = new DownloadManagerImpl(getApplicationContext(),
				Looper.myLooper());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return downloadManager;
	}

}
