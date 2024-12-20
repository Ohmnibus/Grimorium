package net.ohmnibus.grimorium.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import net.ohmnibus.grimorium.database.GrimoriumContract.SourceTable;
import net.ohmnibus.grimorium.database.GrimoriumContract.SpellTable;
import net.ohmnibus.grimorium.database.GrimoriumContract.StarTable;
import net.ohmnibus.grimorium.entity.Profile;
import net.ohmnibus.grimorium.entity.Spell;
import net.ohmnibus.grimorium.entity.SpellFilter;
import net.ohmnibus.grimorium.entity.SpellProfile;
import net.ohmnibus.grimorium.helper.Utils;

import java.util.Locale;

/**
 * Created by Ohmnibus on 16/09/2016.
 */
public class SpellProfileDbAdapter extends SpellDbAdapter {

	private static final String TAG = "SpellProfileDbAdapter";

	private static final String QUERY_DEFAULT =
			"Select Spell.*, " +
					"Coalesce(Star." + StarTable.COLUMN_NAME_STARRED + ", 0) As " + StarTable.COLUMN_NAME_STARRED + ", " +
					"? As " + StarTable.COLUMN_NAME_PROFILE_ID + " " +
					"From " + SpellTable.TABLE_NAME + " As Spell " +
					"Left Join " + StarTable.TABLE_NAME + " As Star " +
					"On Spell." + SpellTable._ID + " = Star." + StarTable.COLUMN_NAME_SPELL_ID + " " +
					"And Star." + StarTable.COLUMN_NAME_PROFILE_ID + " = ? ";

	private static final String SORT_DEFAULT =
			SpellTable.COLUMN_NAME_TYPE + " Asc, " +
					SpellTable.COLUMN_NAME_LEVEL + " Asc, " +
					SpellTable.COLUMN_NAME_NAME + " Asc ";

	private static final SpellProfileDbAdapter mInstance = new SpellProfileDbAdapter();

	public static SpellProfileDbAdapter getInstance() {
		return mInstance;
	}

	protected SpellProfileDbAdapter() {
		super();
	}

	public Cursor getCursor(String[] columns, long profileId, CharSequence nameFilter) {
		String whereClause = "";
		String whereSep = "";
		String[] whereArgs = null;

		if (! TextUtils.isEmpty(nameFilter)) {
			whereClause += whereSep + SpellTable.COLUMN_NAME_NAME + " LIKE ? ";
			whereSep = WHERE_SEP;
			whereArgs = new String[] { "%" + nameFilter + "%" };
		}

		String spellFilterWhereClause = getSelectionBySpellFilter(profileId);
		if (spellFilterWhereClause.length() > 0) {
			whereClause += whereSep + spellFilterWhereClause;
			whereSep = WHERE_SEP;
		}

		String fieldList;
		if (columns == null || columns.length == 0) {
			fieldList = "*";
		} else {
			fieldList = TextUtils.join(", ", columns);
		}

		String sql =
				"Select " + fieldList + " " +
						"From " + SpellTable.TABLE_NAME + " As Spell " +
						"Left Join " + StarTable.TABLE_NAME + " As Star " +
						"On Spell." + SpellTable._ID + " = Star." + StarTable.COLUMN_NAME_SPELL_ID + " " +
						"And Star." + StarTable.COLUMN_NAME_PROFILE_ID + " = " + profileId + " ";
		if (whereClause.length() > 0) {
			sql += "Where " + whereClause + " ";
		}
		sql += "Order By " + SORT_DEFAULT + " ";

		Log.d(TAG, sql);
		if (whereArgs != null) {
			Log.d(TAG, TextUtils.join(";", whereArgs));
		}

		return getDb().rawQuery(sql, whereArgs);
	}

	public Cursor getStarredCursor(String[] columns, long profileId) {

		String fieldList;
		if (columns == null || columns.length == 0) {
			fieldList = "*";
		} else {
			fieldList = TextUtils.join(", ", columns);
		}

		String sql =
				"Select " + fieldList + " " +
					"From " + SpellTable.TABLE_NAME + " As Spell " +
					"Left Join " + StarTable.TABLE_NAME + " As Star " +
					"On Spell." + SpellTable._ID + " = Star." + StarTable.COLUMN_NAME_SPELL_ID + " " +
					"And Star." + StarTable.COLUMN_NAME_PROFILE_ID + " = " + profileId + " " +
				"Where " + StarTable.TABLE_NAME + "." + StarTable.COLUMN_NAME_STARRED + " = 1 " +
				"Order By " + SORT_DEFAULT + " ";

		Log.d(TAG, sql);

		return getDb().rawQuery(sql, null);
	}

	private String getSelectionBySpellFilter(long profileId) {
		Profile profile = ProfileDbAdapter.getInstance().get(profileId);
		SpellFilter spellFilter = profile == null ? null : profile.getSpellFilter();
		return getSelectionBySpellFilter(spellFilter);
	}

	private String getSelectionBySpellFilter(SpellFilter spellFilter) {
		String whereClause = "";
		String whereSep = "";
		if (spellFilter != null && ! spellFilter.isEmpty()) {
			if (spellFilter.getTypes() != Spell.TYPE_ALL) {
				whereClause += whereSep + getBitTest(
						SpellTable.COLUMN_NAME_TYPE,
						spellFilter.getTypes()
				);
				whereSep = WHERE_SEP;
			}
			long[] filteredSources = spellFilter.getFilteredSources();
			if (filteredSources.length > 0) {
				whereClause += whereSep + SpellTable.COLUMN_NAME_SOURCE + " " +
					"Not In (" + Utils.join(filteredSources, ",") + ") ";
				whereSep = WHERE_SEP;
			}
			if (spellFilter.getBooks() != Spell.BOOK_ALL) {
				whereClause += whereSep + getBitTest(
						SpellTable.COLUMN_NAME_BOOK_BITFIELD,
						spellFilter.getBooks(),
						Spell.BOOK_ALL
				);
				whereSep = WHERE_SEP;
			}
			if (spellFilter.getLevels() != Spell.LEVEL_ALL) {
				whereClause += whereSep + getBitTest(
						"(1 << " + SpellTable.COLUMN_NAME_LEVEL + ")",
						spellFilter.getLevels()
				);
				whereSep = WHERE_SEP;
			}
			if (spellFilter.getSchools() != Spell.SCHOOL_ALL) {
				whereClause += whereSep + getBitTest(
						SpellTable.COLUMN_NAME_SCHOOLS_BITFIELD,
						spellFilter.getSchools(),
						Spell.BOOK_ALL
				);
				whereSep = WHERE_SEP;
			}
			if (spellFilter.getSpheres() != Spell.SCHOOL_ALL) {
				whereClause += whereSep + getBitTest(
						SpellTable.COLUMN_NAME_SPHERES_BITFIELD,
						spellFilter.getSpheres(),
						Spell.BOOK_ALL
				);
				whereSep = WHERE_SEP;
			}

			if (! spellFilter.isComponent(Spell.COMP_VERBAL)) {
				whereClause += whereSep + getCompoTest(
						SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD,
						Spell.COMP_VERBAL
				);
				whereSep = WHERE_SEP;
			}
			if (! spellFilter.isComponent(Spell.COMP_SOMATIC)) {
				whereClause += whereSep + getCompoTest(
						SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD,
						Spell.COMP_SOMATIC
				);
				whereSep = WHERE_SEP;
			}
			if (! spellFilter.isComponent(Spell.COMP_MATERIAL)) {
				whereClause += whereSep + getCompoTest(
						SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD,
						Spell.COMP_MATERIAL
				);
				whereSep = WHERE_SEP;
			}
			if (spellFilter.isStarredOnly()) {
				whereClause += whereSep + StarTable.TABLE_NAME + "." + StarTable.COLUMN_NAME_STARRED + " = 1";
				whereSep = WHERE_SEP;
			}
		}
		return whereClause;
	}

	private String getCompoTest(String field, int component) {
		return String.format(Locale.US, "((%1$s & %2$d) = 0) ",
				field, component);
	}

	private String getBitTest(String field, long filterValue) {
		return String.format(Locale.US, "((%1$s & %2$d) <> 0) ",
				field, filterValue);
	}

	private String getBitTest(String field, long filterValue, long defaultValue) {
		return String.format(Locale.US, "(%1$s = %3$d Or (%1$s & %2$d) <> 0) ",
				field, filterValue, defaultValue);
	}

	public SpellProfile get(long profileId, long spellId) {
		SpellProfile retVal;

		Cursor c = query(
				profileId,
				SpellTable.TABLE_NAME + "." + SpellTable._ID + " = ? ",
				new String[] {
						Long.toString(spellId)
				},
				null,
				null,
				null
		);

		if (c.moveToFirst()) {
			retVal = get(c);
		} else {
			retVal = null;
		}

		c.close();

		return retVal;
	}

	public SpellProfile get(Cursor c) {
		if (c.isClosed() || c.isBeforeFirst() || c.isAfterLast())
			return null;

		SpellProfile retVal = new SpellProfile();

		retVal.setProfileId(getLong(c, StarTable.COLUMN_NAME_PROFILE_ID));
		retVal.setStarred(getBoolean(c, StarTable.COLUMN_NAME_STARRED));

		super.fill(retVal, c);

		return retVal;
	}

	public long setStar(long profileId, long spellId, boolean isStarred) {
		return setStar(profileId, spellId, isStarred, false);
	}

	public long setStar(long profileId, long spellId, boolean isStarred, boolean inTransaction) {
		long retVal;

		if (! inTransaction) getDb().beginTransaction();

		try {
			ContentValues values = new ContentValues();

			values.put(StarTable.COLUMN_NAME_STARRED, isStarred ? 1 : 0);

			retVal = getDb().update(
					StarTable.TABLE_NAME,
					values,
					StarTable.COLUMN_NAME_PROFILE_ID + " = ? And " + StarTable.COLUMN_NAME_SPELL_ID + " = ?",
					new String[] { Long.toString(profileId), Long.toString(spellId)});

			if (retVal == 0) {
				//Record does not exists.
				//Create record.

				retVal = insertStar(profileId, spellId, isStarred, inTransaction);
			}

			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "setStar", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	/**
	 * Remove useless star records.<br />
	 * Remove unstarred reference to deleted spells.<br />
	 * @param inTransaction <c>true</c> if inside a previously opened transaction.
	 * @return Number of record removed.
	 */
	public int deleteUnused(boolean inTransaction) {

		int retVal;

		if (! inTransaction) getDb().beginTransaction();

		try {

			retVal = getDb().delete(
					StarTable.TABLE_NAME,
					StarTable.COLUMN_NAME_SPELL_ID + " Not In (" +
							"Select " + SpellTable._ID + " " +
							"From " + SpellTable.TABLE_NAME + " " +
							") " +
							"And " + StarTable.COLUMN_NAME_STARRED + " = ?",
					new String[] { "0" }
			);

			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "deleteBySource", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	//region Private/protected methods

	private long insertStar(long profileId, long spellId, boolean isStarred, boolean inTransaction) {
		long retVal;

		String sql = "Insert Into " + StarTable.TABLE_NAME + " (" +
				StarTable.COLUMN_NAME_PROFILE_ID + ", " +
				StarTable.COLUMN_NAME_SPELL_ID + ", " +
				StarTable.COLUMN_NAME_STARRED + ", " +
				StarTable.COLUMN_NAME_SOURCE_NS + ", " +
				StarTable.COLUMN_NAME_SPELL_UID + " " +
				") Select " +
				profileId + ", " +
				SpellTable.TABLE_NAME + "." + SpellTable._ID + ", " +
				(isStarred ? "1" : "0") + ", " +
				SourceTable.TABLE_NAME + "." + SourceTable.COLUMN_NAME_NAMESPACE + ", " +
				SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_UID + " " +
				"From " + SpellTable.TABLE_NAME + " Inner Join " + SourceTable.TABLE_NAME + " " +
				"On " + SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_SOURCE +
				" = " + SourceTable.TABLE_NAME + "." + SourceTable._ID + " " +
				"Where " + SpellTable.TABLE_NAME + "." + SpellTable._ID + " = " + spellId + " ";

		if (! inTransaction) getDb().beginTransaction();

		try {
			synchronized (TAG) {

				getDb().execSQL(sql);

				sql = "Select last_insert_rowid()";

				Cursor c = getDb().rawQuery(sql, null);

				if (c.moveToFirst()) {
					retVal = c.getLong(0);
				} else {
					retVal = -1;
				}

				c.close();
			}
			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "insertStar", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (!inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	/**
	 * Synchronized star data of re-imported library.<br />
	 * Once a library is removed, all star data are kept. If said library is imported again,
	 * pre-existing star data are stnchronized with the new import, provided that the library
	 * namespace and the spell uid aren't changed.
	 * @param spellUid Spell uid, defined inside the library.
	 * @param sourceNameSpace Library namespace.
	 * @param spellId Newly imported spell Id.
	 * @param inTransaction <c>true</c> if inside a previously opened transaction.
	 * @return Number of stars synchronized.
	 */
	public int syncStar(int spellUid, String sourceNameSpace, long spellId, boolean inTransaction) {
		int retVal;

		if (! inTransaction) getDb().beginTransaction();

		try {
			//Update spell_id to avoid conflicts
			String sql = "Update " + StarTable.TABLE_NAME + " " +
					"Set " + StarTable.COLUMN_NAME_SPELL_ID + " = (" +
					"Select Max(" + StarTable.COLUMN_NAME_SPELL_ID + ") + 1 " +
					"From " + StarTable.TABLE_NAME + " " +
					") " +
					"Where " + StarTable.COLUMN_NAME_SPELL_ID + " = ? ";

			getDb().execSQL(sql, new Object[] {spellId});

			//Synchronize new spell id
			ContentValues values = new ContentValues();

			values.put(StarTable.COLUMN_NAME_SPELL_ID, spellId);

			retVal = getDb().update(
					StarTable.TABLE_NAME,
					values,
					StarTable.COLUMN_NAME_SPELL_UID + " = ? And " + StarTable.COLUMN_NAME_SOURCE_NS + " = ?",
					new String[] {
							Integer.toString(spellUid),
							sourceNameSpace
					});

			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "syncStar", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	/**
	 * Perform a query on a join between {@link SpellTable} and {@link StarTable}.
	 * @param profileId Reference profile.
	 * @param selection A filter declaring which rows to return, formatted as an
	 *                  SQL WHERE clause (excluding the WHERE itself). Passing
	 *                  null will return all rows for the given table.
	 * @param selectionArgs You may include ?s in selection, which will be
	 *                      replaced by the values from selectionArgs, in order
	 *                      that they appear in the selection. The values will be
	 *                      bound as Strings.
	 * @param groupBy A filter declaring how to group rows, formatted as an SQL
	 *                GROUP BY clause (excluding the GROUP BY itself). Passing
	 *                null will cause the rows to not be grouped.
	 * @param having A filter declare which row groups to include in the cursor,
	 *               if row grouping is being used, formatted as an SQL HAVING
	 *               clause (excluding the HAVING itself). Passing null will cause
	 *               all row groups to be included, and is required when row
	 *               grouping is not being used.
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause
	 *                (excluding the ORDER BY itself). Passing null will use the
	 *                default sort order, which may be unordered.
	 * @return A {@link Cursor} object, which is positioned before the first entry.
	 * 			Note that {@link Cursor}s are not synchronized, see the
	 * 			documentation for more details.
	 */
	private Cursor query(
			long profileId,
			String selection,
			String[] selectionArgs,
			@SuppressWarnings("unused") String groupBy,
			@SuppressWarnings("unused") String having,
			String orderBy) {

		String sql = QUERY_DEFAULT;

		if (! TextUtils.isEmpty(selection)) {
			sql += "Where " + selection + " ";
		}

		//TODO: group by, having

		if (! TextUtils.isEmpty(orderBy)) {
			sql += "Order By " + orderBy + " ";
		}

		String[] args;
		if (selectionArgs == null || selectionArgs.length == 0) {
			args = new String[2];
		} else {
			args = new String[selectionArgs.length + 2];
			System.arraycopy(selectionArgs, 0, args, 2, selectionArgs.length);
		}
		args[0] = Long.toString(profileId);
		args[1] = Long.toString(profileId);

		return getDb().rawQuery(sql, args);
	}

	//endregion
}
