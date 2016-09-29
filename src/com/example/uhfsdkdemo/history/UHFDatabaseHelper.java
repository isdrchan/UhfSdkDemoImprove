package com.example.uhfsdkdemo.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UHFDatabaseHelper extends SQLiteOpenHelper {
	
	// 数据库版本号
    private static final int DATABASE_VERSION = 1;
    // 数据库名
    private static final String DATABASE_NAME = "UHFDB.db";
    // 数据表名
    public static final String TABLE_NAME = "history";

	public UHFDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("CREATE TABLE [" + TABLE_NAME + "] (");
        sBuffer.append("[id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append("[epc] VARCHAR(32),");
        sBuffer.append("[pic_path] TEXT,");
        sBuffer.append("[time] INTEGER)");

        // 执行创建表的SQL语句
        db.execSQL(sBuffer.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
