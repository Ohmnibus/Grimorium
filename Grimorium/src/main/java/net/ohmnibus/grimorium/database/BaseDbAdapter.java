package net.ohmnibus.grimorium.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Ohmnibus on 10/09/2016.
 */
public class BaseDbAdapter {

	private static Context mContext = null;
	private static GrimoriumDbHelper mDbHelper = null;
	private static SQLiteDatabase mDb = null;

	public static void init(Context context) {
		if (mDbHelper == null) {
			mContext = context;
			mDbHelper = new GrimoriumDbHelper(context);
			mDb = mDbHelper.getWritableDatabase();
		}
	}

	public static void dispose() {
		if (mDbHelper != null) {
			mDb.close();
			mDbHelper.close();
		}
	}

	public static void beginTransaction() {
		mDb.beginTransaction();
	}

	public static void setTransactionSuccessful() {
		mDb.setTransactionSuccessful();
	}

	public static void endTransaction() {
		mDb.endTransaction();
	}

	protected Context getContext() {
		return mContext;
	}

	protected void handleQueryException(Exception ex) {
		throw new RuntimeException(ex);
	}

	protected static SQLiteDatabase getDb() {
		return mDb;
	}

	protected long getRowId(Cursor c) {
		return getLong(c, BaseColumns._ID);
	}

	protected long getLong(Cursor c, String column) {
		return c.getLong(c.getColumnIndexOrThrow(column));
	}

	protected int getInt(Cursor c, String column) {
		return c.getInt(c.getColumnIndexOrThrow(column));
	}

	protected String getString(Cursor c, String column) {
		return c.getString(c.getColumnIndexOrThrow(column));
	}

	protected boolean getBoolean(Cursor c, String column) {
		return c.getInt(c.getColumnIndexOrThrow(column)) != 0;
	}
}
