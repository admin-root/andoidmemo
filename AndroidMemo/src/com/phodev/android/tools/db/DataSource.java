package com.phodev.android.tools.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public interface DataSource {
	public SQLiteDatabase openSQLiteDatabase(Context context);

	public void askCloseDatabase(SQLiteDatabase db);
}
