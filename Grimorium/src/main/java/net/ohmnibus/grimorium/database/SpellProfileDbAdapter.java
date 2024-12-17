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

	@Deprecated
	public Cursor getCursor(long profileId) {
		return getCursor(profileId, null);
	}

	@Deprecated
	public Cursor getCursor(long profileId, CharSequence nameFilter) {
		String whereClause = "";
		String whereSep = "";
		//List<String> whereArgs = new ArrayList<>();
		String[] whereArgs = null;

		Profile profile = ProfileDbAdapter.getInstance().get(profileId);
		SpellFilter spellFilter = profile == null ? null : profile.getSpellFilter();

		if (! TextUtils.isEmpty(nameFilter)) {
			whereClause += whereSep + SpellTable.COLUMN_NAME_NAME + " LIKE ? ";
			whereSep = WHERE_SEP;
			//whereArgs.add(nameFilter);
			whereArgs = new String[] { "%" + nameFilter.toString() + "%" };
		}

		String spellFilterWhereClause = getSelectionBySpellFilter(profileId);
		if (spellFilterWhereClause.length() > 0) {
			whereClause += whereSep + spellFilterWhereClause;
			whereSep = WHERE_SEP;
		}

		String sql =
				"Select Spell.*, " +
						"Coalesce(Star." + StarTable.COLUMN_NAME_STARRED + ", 0) As " + StarTable.COLUMN_NAME_STARRED + ", " +
						"? As " + StarTable.COLUMN_NAME_PROFILE_ID + " " +
						"From " + SpellTable.TABLE_NAME + " As Spell " +
						"Left Join " + StarTable.TABLE_NAME + " As Star " +
						"On Spell." + SpellTable._ID + " = Star." + StarTable.COLUMN_NAME_SPELL_ID + " " +
						"And Star." + StarTable.COLUMN_NAME_PROFILE_ID + " = ? ";

		Cursor c = query(
				profileId,
				whereClause,
				whereArgs,
				null,
				null,
				SORT_DEFAULT
		);

		return c;
	}

	public Cursor getCursor(String[] columns, long profileId, CharSequence nameFilter) {
		String whereClause = "";
		String whereSep = "";
		String[] whereArgs = null;

		if (! TextUtils.isEmpty(nameFilter)) {
			whereClause += whereSep + SpellTable.COLUMN_NAME_NAME + " LIKE ? ";
			whereSep = WHERE_SEP;
			whereArgs = new String[] { "%" + nameFilter.toString() + "%" };
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

		//SpellDbAdapter adapter = new SpellDbAdapter();

		//adapter.fill(retVal, c);
		super.fill(retVal, c);

		return retVal;
	}

	public long insert(SpellProfile spellProfile) {
		return insert(spellProfile, false);
	}

	public long insert(SpellProfile spellProfile, boolean inTransaction) {
		long retVal;
		Spell duplicate = null;

		if (spellProfile.getId() > 0) {
			duplicate = super.get(spellProfile.getId());
		}
		if (duplicate == null) {
			duplicate = super.get(spellProfile.getSourceId(), spellProfile.getUid());
		}

		if (! inTransaction) getDb().beginTransaction();

		try {
			if (duplicate != null) {
				super.update(spellProfile.getId(), spellProfile, true);
			} else {
				super.insert(spellProfile, true);
			}

			//ContentValues values = getContentValues(spellProfile);

			//retVal = getDb().insert(StarTable.TABLE_NAME, null, values);

			retVal = insertStar(
					spellProfile.getProfileId(),
					spellProfile.getId(),
					spellProfile.isStarred());
		} catch (Exception ex) {
			Log.e(TAG, "insert", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

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

//				SpellProfile spellProfile = new SpellProfile();
//				spellProfile.setProfileId(profileId);
//				spellProfile.setId(spellId);
//				spellProfile.setStarred(isStarred);
//				values = getContentValues(spellProfile);
//
//				retVal = getDb().insert(StarTable.TABLE_NAME, null, values);

				retVal = insertStar(profileId, spellId, isStarred);
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

//	public int deleteProfile(long profileId, boolean inTransaction) {
//		int retVal;
//
//		if (! inTransaction) getDb().beginTransaction();
//
//		try {
//			String whereClause;
//
//			String[] whereArgs;
//
//			whereClause = StarTable.COLUMN_NAME_PROFILE_ID + " = ?";
//
//			whereArgs = new String[] { Long.toString(profileId) };
//
//			retVal = getDb().delete(
//					StarTable.TABLE_NAME,
//					whereClause,
//					whereArgs);
//
//			if (! inTransaction) getDb().setTransactionSuccessful();
//		} catch (Exception ex) {
//			Log.e(TAG, "delete", ex);
//			retVal = -1;
//			handleQueryException(ex);
//		} finally {
//			if (! inTransaction) getDb().endTransaction();
//		}
//
//		return retVal;
//	}

	//region Private/protected methods

	private long insertStar(long profileId, long spellId, boolean isStarred) {
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

		return retVal;
	}

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

//	public int purgeDuplicate(long sourceId, boolean inTransaction) {
//		int retVal;
//
//		if (! inTransaction) getDb().beginTransaction();
//
//		try {
//
//			//Delete duplicete stars
////			Cursor cursor = getDb().query(
////					GrimoriumContract.StarTable.TABLE_NAME,
////					new String[] {"Max(" + GrimoriumContract.StarTable._ID + ")"},
////					whereClause
////			)
//			String sql = "Delete From " + StarTable.TABLE_NAME + " " +
//					"Where " + StarTable._ID + " in (" +
//					"Select Min(" + StarTable.TABLE_NAME + "." + StarTable._ID + "), " +
//					SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_SOURCE + ", " +
//					SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_UID + " " +
//					"From " + StarTable.TABLE_NAME + " " +
//					"Inner Join " + SpellTable.TABLE_NAME + " " +
//					"On " + SpellTable.TABLE_NAME + "." + SpellTable._ID + " = " +
//					StarTable.TABLE_NAME + "." + StarTable.COLUMN_NAME_SPELL_ID + " " +
//					"Group By " + SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_SOURCE + ", " +
//					SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_UID + " " +
//					"Having Count(" + StarTable.TABLE_NAME + "." + StarTable._ID + ") > 1 " +
//					") ";
//
//			getDb().rawQuery(sql, null);
//
//			if (! inTransaction) getDb().setTransactionSuccessful();
//		} catch (Exception ex) {
//			Log.e(TAG, "initSource", ex);
//			retVal = -1;
//			handleQueryException(ex);
//		} finally {
//			if (! inTransaction) getDb().endTransaction();
//		}
//
//		return retVal;
//	}


//	protected ContentValues getContentValues(SpellProfile spellProfile) {
//		ContentValues retVal = new ContentValues();
//
//		retVal.put(StarTable.COLUMN_NAME_PROFILE_ID, spellProfile.getProfileId());
//		retVal.put(StarTable.COLUMN_NAME_SPELL_ID, spellProfile.getId());
//		retVal.put(StarTable.COLUMN_NAME_STARRED, spellProfile.isStarred() ? 1 : 0);
//
//		return retVal;
//	}

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
			String groupBy,
			String having,
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

		//Log.d(TAG, sql);
		//Log.d(TAG, TextUtils.join(";", args));

		return getDb().rawQuery(sql, args);
	}

	//endregion
}
