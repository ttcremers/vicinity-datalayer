package com.theodoorthomas.vicinity.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class VicinityOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "vicinity";
	
	private static final String ID_COLUMN = "id INTEGER PRIMARY KEY AUTOINCREMENT,";
	private static final String UID_COLUMN = "uid INTEGER NOT NULL UNIQUE,";
	
    private static final String NOTIFICATIONS_TABLE_NAME = "notifications";
    private static final String NOTIFICATIONS_TABLE_CREATE =
                "CREATE TABLE " + NOTIFICATIONS_TABLE_NAME + " (" +
                ID_COLUMN +	
                UID_COLUMN +
                "distance INTEGER DEFAULT 0, " +
                "isNotified INTEGER DEFAULT 0, " +
                "timestamp INTEGER DEFAULT 0);";

	public VicinityOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(NOTIFICATIONS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
