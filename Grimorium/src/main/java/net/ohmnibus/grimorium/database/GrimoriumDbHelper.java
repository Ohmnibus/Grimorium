package net.ohmnibus.grimorium.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.ohmnibus.grimorium.database.GrimoriumContract.SourceTable;
import net.ohmnibus.grimorium.database.GrimoriumContract.SpellTable;
import net.ohmnibus.grimorium.database.GrimoriumContract.ProfileTable;
import net.ohmnibus.grimorium.database.GrimoriumContract.StarTable;

/**
 * Created by Ohmnibus on 10/09/2016.
 */
public class GrimoriumDbHelper extends SQLiteOpenHelper {

	public static final String TAG = "GrimoriumDbHelper";
	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "Grimorium.db";

	private final Context mContext;

	public GrimoriumDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	private static final String CMD_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
	private static final String CMD_CREATE_INDEX = "CREATE INDEX IF NOT EXISTS ";
	private static final String CMD_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX IF NOT EXISTS ";

	private static final String CMD_DROP_TABLE = "DROP TABLE IF EXISTS ";
	private static final String CMD_DROP_INDEX = "DROP INDEX IF EXISTS ";

	private static final String ON = " ON ";
	private static final String NULL = "NULL";
	private static final String ALLOW_NULL = " " + NULL;
	private static final String NOT_NULL = " NOT " + NULL;

	private static final String TYPE_TABLE_ID = " INTEGER PRIMARY KEY";
	private static final String TYPE_TEXT = " TEXT COLLATE NOCASE";
	private static final String TYPE_NUMBER = " NUMERIC";

	private static final String SEP_DOT = ".";
	private static final String SEP_COMMA = ",";
	private static final String SEP_SEMICOLON = ";";

	private static final String PAR_OPEN = " (";
	private static final String PAR_CLOSE = ") ";

	private static final String SQL_CREATE_SOURCES =
			CMD_CREATE_TABLE + SourceTable.TABLE_NAME + PAR_OPEN +
					SourceTable._ID + TYPE_TABLE_ID + SEP_COMMA +
					SourceTable.COLUMN_NAME_NAME + TYPE_TEXT + SEP_COMMA +
					SourceTable.COLUMN_NAME_NAMESPACE + TYPE_TEXT + SEP_COMMA +
					SourceTable.COLUMN_NAME_VERSION + TYPE_NUMBER + SEP_COMMA +
					SourceTable.COLUMN_NAME_DESCRIPTION + TYPE_TEXT + SEP_COMMA +
					SourceTable.COLUMN_NAME_URL + TYPE_TEXT + //SEP_COMMA +
					PAR_CLOSE;

	private static final String SQL_CREATE_SPELLS =
			CMD_CREATE_TABLE + SpellTable.TABLE_NAME + PAR_OPEN +
					SpellTable._ID + TYPE_TABLE_ID + SEP_COMMA +
					SpellTable.COLUMN_NAME_SOURCE + TYPE_NUMBER + SEP_COMMA +
					SpellTable.COLUMN_NAME_UID + TYPE_NUMBER + SEP_COMMA +
					SpellTable.COLUMN_NAME_TYPE + TYPE_NUMBER + SEP_COMMA +
					SpellTable.COLUMN_NAME_LEVEL + TYPE_NUMBER + SEP_COMMA +
					SpellTable.COLUMN_NAME_NAME + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_REVERSIBLE + TYPE_NUMBER + SEP_COMMA +
					SpellTable.COLUMN_NAME_SCHOOLS + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_SCHOOLS_BITFIELD + TYPE_NUMBER + SEP_COMMA +
					SpellTable.COLUMN_NAME_SPHERES + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_SPHERES_BITFIELD + TYPE_NUMBER + SEP_COMMA +
					SpellTable.COLUMN_NAME_RANGE + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_COMPONENTS + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD + TYPE_NUMBER + SEP_COMMA +
					SpellTable.COLUMN_NAME_DURATION + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_CAST_TIME + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_AOE + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_SAVING + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_BOOK + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_BOOK_BITFIELD + TYPE_NUMBER + SEP_COMMA +
					SpellTable.COLUMN_NAME_AUTHOR + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_DESCRIPTION + TYPE_TEXT + SEP_COMMA +
					SpellTable.COLUMN_NAME_UPDATED + TYPE_NUMBER +
					PAR_CLOSE;

	private static final String SQL_CREATE_INDEX_SPELLS =
			CMD_CREATE_INDEX + SpellTable.INDEX_NAME + ON + SpellTable.TABLE_NAME + PAR_OPEN +
					SpellTable.COLUMN_NAME_TYPE + SEP_COMMA +
					SpellTable.COLUMN_NAME_LEVEL + SEP_COMMA +
					SpellTable.COLUMN_NAME_NAME +
					PAR_CLOSE;

	private static final String SQL_CREATE_PROFILES =
			CMD_CREATE_TABLE + ProfileTable.TABLE_NAME + PAR_OPEN +
					ProfileTable._ID + TYPE_TABLE_ID + SEP_COMMA +
					ProfileTable.COLUMN_NAME_KEY + TYPE_TEXT + SEP_COMMA +
					ProfileTable.COLUMN_NAME_NAME + TYPE_TEXT + SEP_COMMA +
					ProfileTable.COLUMN_NAME_SYS + TYPE_NUMBER + SEP_COMMA +
					ProfileTable.COLUMN_NAME_TYPES + TYPE_NUMBER + SEP_COMMA +
					ProfileTable.COLUMN_NAME_LEVELS + TYPE_NUMBER + SEP_COMMA +
					ProfileTable.COLUMN_NAME_BOOKS + TYPE_NUMBER + SEP_COMMA +
					ProfileTable.COLUMN_NAME_SCHOOLS + TYPE_NUMBER + SEP_COMMA +
					ProfileTable.COLUMN_NAME_SPHERES + TYPE_NUMBER + SEP_COMMA +
					ProfileTable.COLUMN_NAME_COMPONENTS + TYPE_NUMBER + SEP_COMMA +
					ProfileTable.COLUMN_NAME_STARRED_ONLY + TYPE_NUMBER + SEP_COMMA +
					ProfileTable.COLUMN_NAME_FILTERED_SOURCES + TYPE_TEXT +
					PAR_CLOSE;

	private static final String SQL_CREATE_INDEX_PROFILES =
			CMD_CREATE_UNIQUE_INDEX + ProfileTable.INDEX_NAME + ON + ProfileTable.TABLE_NAME + PAR_OPEN +
					ProfileTable.COLUMN_NAME_KEY +
					PAR_CLOSE;

	private static final String SQL_CREATE_STARS =
			CMD_CREATE_TABLE + StarTable.TABLE_NAME + PAR_OPEN +
					StarTable._ID + TYPE_TABLE_ID + SEP_COMMA +
					StarTable.COLUMN_NAME_PROFILE_ID + TYPE_NUMBER + SEP_COMMA +
					StarTable.COLUMN_NAME_SPELL_ID + TYPE_NUMBER + SEP_COMMA +
					StarTable.COLUMN_NAME_STARRED + TYPE_NUMBER + SEP_COMMA +
					StarTable.COLUMN_NAME_SOURCE_NS + TYPE_TEXT + SEP_COMMA +
					StarTable.COLUMN_NAME_SPELL_UID + TYPE_NUMBER +
					PAR_CLOSE;

	private static final String SQL_CREATE_INDEX_STARS = //TODO: Update if exists
			CMD_CREATE_UNIQUE_INDEX + StarTable.INDEX_NAME + ON + StarTable.TABLE_NAME + PAR_OPEN +
					StarTable.COLUMN_NAME_PROFILE_ID + SEP_COMMA +
					StarTable.COLUMN_NAME_SPELL_ID +
					PAR_CLOSE;

	private static final String[] SQL_CREATE = new String[] {
			SQL_CREATE_SOURCES,
			SQL_CREATE_SPELLS,
			SQL_CREATE_INDEX_SPELLS,
			SQL_CREATE_PROFILES,
			SQL_CREATE_INDEX_PROFILES,
			SQL_CREATE_STARS,
			SQL_CREATE_INDEX_STARS
	};

	private static final String SQL_DELETE_SOURCES =
			CMD_DROP_TABLE + SourceTable.TABLE_NAME;

	private static final String SQL_DELETE_SPELLS =
			CMD_DROP_TABLE + SpellTable.TABLE_NAME;

	private static final String SQL_DELETE_INDEX_SPELLS =
			CMD_DROP_INDEX + SpellTable.INDEX_NAME;

	private static final String SQL_DELETE_PROFILES =
			CMD_DROP_TABLE + ProfileTable.TABLE_NAME;

	private static final String SQL_DELETE_INDEX_PROFILES =
			CMD_DROP_INDEX + ProfileTable.INDEX_NAME;

	private static final String SQL_DELETE_STARS =
			CMD_DROP_TABLE + StarTable.TABLE_NAME;

	private static final String SQL_DELETE_INDEX_STARS =
			CMD_DROP_INDEX + StarTable.INDEX_NAME;

	private static final String[] SQL_DELETE = new String[] {
			SQL_DELETE_INDEX_SPELLS,
			SQL_DELETE_INDEX_PROFILES,
			SQL_DELETE_INDEX_STARS,

			SQL_DELETE_SOURCES,
			SQL_DELETE_SPELLS,
			SQL_DELETE_PROFILES,
			SQL_DELETE_STARS
	};

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String query : SQL_CREATE) {
			db.execSQL(query);
		}
		//Create default records

		//Default profile
		ContentValues values = ProfileDbAdapter.getDefaultProfileCV(mContext);
		db.insert(
				ProfileTable.TABLE_NAME,
				null,
				values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql;
		if (oldVersion < 2) {
			//Upgrade from version 1 to 2
			sql = "Alter Table " + ProfileTable.TABLE_NAME + " " +
					"Add Column " + ProfileTable.COLUMN_NAME_FILTERED_SOURCES + TYPE_TEXT;
			db.execSQL(sql);
		}
		if (oldVersion < 3) {
			//Upgrade from version 2 to 3
			sql = "Alter Table " + SpellTable.TABLE_NAME + " " +
					"Add Column " + SpellTable.COLUMN_NAME_UPDATED + TYPE_NUMBER;
			db.execSQL(sql);
			sql = "Alter Table " + StarTable.TABLE_NAME + " " +
					"Add Column " + StarTable.COLUMN_NAME_SOURCE_NS + TYPE_TEXT;
			db.execSQL(sql);
			sql = "Alter Table " + StarTable.TABLE_NAME + " " +
					"Add Column " + StarTable.COLUMN_NAME_SPELL_UID + TYPE_NUMBER;
			db.execSQL(sql);
			sql = "Update " + StarTable.TABLE_NAME + " " +
					"Set " + StarTable.COLUMN_NAME_SOURCE_NS + " = (" +
					"Select " + SourceTable.TABLE_NAME + "." + SourceTable.COLUMN_NAME_NAMESPACE + " " +
					"From " + SpellTable.TABLE_NAME + " Inner Join " + SourceTable.TABLE_NAME + " " +
					"On " + SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_SOURCE +
					" = " + SourceTable.TABLE_NAME + "." + SourceTable._ID + " " +
					"Where " + SpellTable.TABLE_NAME + "." + SpellTable._ID + " = " +
					StarTable.TABLE_NAME + "." + StarTable.COLUMN_NAME_SPELL_ID + " " +
					") ";
			db.execSQL(sql);
			sql = "Update " + StarTable.TABLE_NAME + " " +
					"Set " + StarTable.COLUMN_NAME_SPELL_UID + " = (" +
					"Select " + SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_UID + " " +
					"From " + SpellTable.TABLE_NAME + " " +
					"Where " + SpellTable.TABLE_NAME + "." + SpellTable._ID + " = " +
					StarTable.TABLE_NAME + "." + StarTable.COLUMN_NAME_SPELL_ID + " " +
					") ";
			db.execSQL(sql);
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
