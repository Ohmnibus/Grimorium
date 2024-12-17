package net.ohmnibus.grimorium.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.text.TextUtils;
import android.util.Log;

import net.ohmnibus.grimorium.entity.Spell;
import net.ohmnibus.grimorium.entity.SpellFilter;
import net.ohmnibus.grimorium.database.GrimoriumContract.SpellTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ohmnibus on 10/09/2016.
 */
public class SpellDbAdapter extends BaseDbAdapter {

	private static final String TAG = "SpellDbAdapter";

	protected static final String WHERE_SEP = "AND ";
	protected static final String SORT_DEFAULT =
			SpellTable.COLUMN_NAME_TYPE + " Asc, " +
			SpellTable.COLUMN_NAME_LEVEL + " Asc, " +
			SpellTable.COLUMN_NAME_NAME + " Asc ";

	private static final SpellDbAdapter mInstance = new SpellDbAdapter();

	public static SpellDbAdapter getInstance() {
		return mInstance;
	}

	protected SpellDbAdapter() {
		super();
	}

	public Cursor getCursor() {
		Cursor c = getDb().query(
				SpellTable.TABLE_NAME,
				null,
				null, //SpellTable._ID + " = ?",
				null, //new String[] { Long.toString(spellId) },
				null,
				null,
				SORT_DEFAULT
		);
		return c;
	}

	@Deprecated
	public Cursor getCursor(String nameFilter, SpellFilter spellFilter) {
		String whereClause = "";
		String whereSep = "";
		List<String> whereArgs = new ArrayList<>();

		if (! TextUtils.isEmpty(nameFilter)) {
			whereClause += whereSep + SpellTable.COLUMN_NAME_NAME + " LIKE ? ";
			whereSep = WHERE_SEP;
			whereArgs.add(nameFilter);
		}

		if (spellFilter != null && ! spellFilter.isEmpty()) {
			whereClause += whereSep + getBitTest(
					SpellTable.COLUMN_NAME_TYPE,
					spellFilter.getTypes()
			);
			whereSep = WHERE_SEP;
			whereClause += whereSep + getBitTest(
					SpellTable.COLUMN_NAME_BOOK_BITFIELD,
					spellFilter.getBooks(),
					Spell.BOOK_ALL
			);
			whereClause += whereSep + getBitTest(
					SpellTable.COLUMN_NAME_LEVEL,
					spellFilter.getLevels()
			);
			whereClause += whereSep + getBitTest(
					SpellTable.COLUMN_NAME_SCHOOLS_BITFIELD,
					spellFilter.getSchools(),
					Spell.BOOK_ALL
			);
			whereClause += whereSep + getBitTest(
					SpellTable.COLUMN_NAME_SPHERES_BITFIELD,
					spellFilter.getSpheres(),
					Spell.BOOK_ALL
			);

			if (spellFilter.isComponent(Spell.COMP_VERBAL)) {
				whereClause += whereSep + getCompoTest(
						SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD,
						Spell.COMP_VERBAL
				);
			}
			if (spellFilter.isComponent(Spell.COMP_SOMATIC)) {
				whereClause += whereSep + getCompoTest(
						SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD,
						Spell.COMP_SOMATIC
				);
			}
			if (spellFilter.isComponent(Spell.COMP_MATERIAL)) {
				whereClause += whereSep + getCompoTest(
						SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD,
						Spell.COMP_VERBAL
				);
			}
		}


		Cursor c;
		if (TextUtils.isEmpty(whereClause)) {
			c = getCursor();
		} else {
			c = getDb().query(
					SpellTable.TABLE_NAME,
					null,
					whereClause,
					whereArgs.toArray(new String[whereArgs.size()]),
					null,
					null,
					SORT_DEFAULT
			);
		}
		return c;
	}

	protected String getCompoTest(String field, int component) {
		return String.format("((%1$s & %2$d) = 0) ",
				field, component);
	}

	protected String getBitTest(String field, long filterValue) {
		return String.format("((%1$s & %2$d) <> 0) ",
				field, filterValue);
	}

	protected String getBitTest(String field, long filterValue, long defaultValue) {
		return String.format("(%1$s = %3$d Or (%1$s & %2$d) <> 0) ",
				field, filterValue, defaultValue);
	}

	public Spell get(long sourceId, int spellUid) {
		Spell retVal;

		Cursor c = getDb().query(
				SpellTable.TABLE_NAME,
				null,
				SpellTable.COLUMN_NAME_SOURCE + " = ? " +
						"And " + SpellTable.COLUMN_NAME_UID + " = ?",
				new String[] { Long.toString(sourceId), Integer.toString(spellUid) },
				null,
				null,
				null);

		if (c.moveToFirst()) {
			retVal = get(c);
		} else {
			retVal = null;
		}

		c.close();

		return retVal;
	}

	public Spell get(long spellId) {
		Spell retVal;

		Cursor c = getDb().query(
				SpellTable.TABLE_NAME,
				null,
				SpellTable._ID + " = ?",
				new String[] { Long.toString(spellId) },
				null,
				null,
				null);

		if (c.moveToFirst()) {
			retVal = get(c);
		} else {
			retVal = null;
		}

		c.close();

		return retVal;
	}

	public Spell get(Cursor c) {
		return fill(null, c);
	}

	protected Spell fill(Spell retVal, Cursor c) {
		if (c.isClosed() || c.isBeforeFirst() || c.isAfterLast())
			return retVal;

		if (retVal == null)
			retVal = new Spell();

		retVal.setId(getLong(c, SpellTable._ID));
		retVal.setSourceId(getLong(c, SpellTable.COLUMN_NAME_SOURCE));
		retVal.setUid(getInt(c, SpellTable.COLUMN_NAME_UID));
		retVal.setType(getInt(c, SpellTable.COLUMN_NAME_TYPE));
		retVal.setLevel(getInt(c, SpellTable.COLUMN_NAME_LEVEL));
		retVal.setName(getString(c, SpellTable.COLUMN_NAME_NAME));
		retVal.setBookTxt(getString(c, SpellTable.COLUMN_NAME_BOOK));
		retVal.setBook(getInt(c, SpellTable.COLUMN_NAME_BOOK_BITFIELD));
		retVal.setReversible(1 == getInt(c, SpellTable.COLUMN_NAME_REVERSIBLE));
		retVal.setSchoolsTxt(getString(c, SpellTable.COLUMN_NAME_SCHOOLS));
		retVal.setSchools(getInt(c, SpellTable.COLUMN_NAME_SCHOOLS_BITFIELD));
		retVal.setSpheresTxt(getString(c, SpellTable.COLUMN_NAME_SPHERES));
		retVal.setSpheres(getInt(c, SpellTable.COLUMN_NAME_SPHERES_BITFIELD));
		retVal.setRange(getString(c, SpellTable.COLUMN_NAME_RANGE));
		retVal.setComposTxt(getString(c, SpellTable.COLUMN_NAME_COMPONENTS));
		retVal.setCompos(getInt(c, SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD));
		retVal.setDuration(getString(c, SpellTable.COLUMN_NAME_DURATION));
		retVal.setCastingTime(getString(c, SpellTable.COLUMN_NAME_CAST_TIME));
		retVal.setArea(getString(c, SpellTable.COLUMN_NAME_AOE));
		retVal.setSaving(getString(c, SpellTable.COLUMN_NAME_SAVING));
		retVal.setDesc(getString(c, SpellTable.COLUMN_NAME_DESCRIPTION));
		retVal.setAuthor(getString(c, SpellTable.COLUMN_NAME_AUTHOR));

		return retVal;
	}

	/**
	 * Insert or update a spell.
	 * @param spell Spell to insert/update
	 * @return New or existent spell ID.
	 */
	public long set(Spell spell) {
		return set(spell, false);
	}

	public static final int SET_CREATED = 1;
	public static final int SET_UPDATED = 0;
	public static final int SET_ERROR = -1;
	public static final int SET_SKIPPED = -1;

	/**
	 * Insert or update a spell.<br />
	 * The new recrod id will be set inside the {@code spell} parameter.
	 * @param spell Spell to insert/update
	 * @param inTransaction Set to true id the call is inside a transaction.
	 * @return Either {@link #SET_CREATED}, {@link #SET_UPDATED}, {@link #SET_SKIPPED} or {@link #SET_ERROR}.
	 */
	public int set(Spell spell, boolean inTransaction) {
		int retVal;
		Spell duplicate = null;

		if (spell.getId() > 0) {
			//Check by ID
			duplicate = get(spell.getId());
		}
		if (duplicate == null) {
			//Check by source+UID
			duplicate = get(spell.getSourceId(), spell.getUid());
		}

		if (! inTransaction) getDb().beginTransaction();

		try {
			if (duplicate != null) {
				int affected = update(duplicate.getId(), spell, true);
				//retVal = duplicate.getId();
				if (affected > 0) {
					spell.setId(duplicate.getId());
					retVal = SET_UPDATED;
				} else {
					retVal = SET_SKIPPED;
				}
			} else {
//				retVal = insert(spell, true);
//				spell.setId(retVal);
				long newId = insert(spell, true);
				if (newId > 0) {
					spell.setId(newId);
					retVal = SET_CREATED;
				} else {
					retVal = SET_SKIPPED;
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "set", ex);
			//retVal = -1;
			retVal = SET_ERROR;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	public long insert(Spell spell) {
		return insert(spell, false);
	}

	public long insert(Spell spell, boolean inTransaction) {
		long retVal;

//		if (! inTransaction) getDb().beginTransaction();

		try {
			ContentValues values = getContentValues(spell);

			retVal = getDb().insert(SpellTable.TABLE_NAME, null, values);

			//if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "insert", ex);
			retVal = -1;
			handleQueryException(ex);
//		} finally {
//			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	public int update(Spell spell) {
		return update(spell.getId(), spell);
	}

	public int update(long spellId, Spell spell) {
		return update(spellId, spell, false);
	}

	public int update(long spellId, Spell spell, boolean inTransaction) {
		int retVal;

//		if (! inTransaction) getDb().beginTransaction();

		try {
			ContentValues values = getContentValues(spell);

			String whereClause = SpellTable._ID + " = ?";

			String[] whereArgs = { Long.toString(spellId) };

			retVal = getDb().update(
					SpellTable.TABLE_NAME,
					values,
					whereClause,
					whereArgs);

//			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "insert", ex);
			retVal = -1;
			handleQueryException(ex);
//		} finally {
//			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	public int delete(long spellId) {
		return delete(spellId, false);
	}

	public int delete(long spellId, boolean inTransaction) {
		int retVal;

		if (! inTransaction) getDb().beginTransaction();

		try {
			String whereClause = SpellTable._ID + " = ?";

			String[] whereArgs = { Long.toString(spellId) };

			retVal = getDb().delete(
					SpellTable.TABLE_NAME,
					whereClause,
					whereArgs);

			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "delete", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	/**
	 * Mark all spells from a given source as "not updated".<br />
	 * All spell "not updated" afted an import should be deleted.<br />
	 * See {@link #purgeDeletedBySource(long, boolean)}.
	 * @param sourceId Source ID
	 * @param inTransaction Set to true id the call is inside a transaction.
	 * @return Number of record affected.
	 */
	public int initSource(long sourceId, boolean inTransaction) {
		int retVal;

		if (! inTransaction) getDb().beginTransaction();

		try {

			ContentValues values = new ContentValues();

			values.put(SpellTable.COLUMN_NAME_UPDATED, 0);

			String whereClause = SpellTable.COLUMN_NAME_SOURCE + " = ?";

			String[] whereArgs = { Long.toString(sourceId) };

			retVal = getDb().update(
					SpellTable.TABLE_NAME,
					values,
					whereClause,
					whereArgs);

			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "initSource", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	/**
	 * Delete all the spells marked as "not updated".<br />
	 * See {@link #initSource(long, boolean)}.
	 * @param sourceId Source ID
	 * @param inTransaction Set to true id the call is inside a transaction.
	 * @return Number of record purged.
	 */
	public int purgeDeletedBySource(long sourceId, boolean inTransaction) {
		int retVal;

		if (! inTransaction) getDb().beginTransaction();

		try {

			String whereClause = SpellTable.COLUMN_NAME_SOURCE + " = ? " +
					"And " + SpellTable.COLUMN_NAME_UPDATED + " = ? ";

			String[] whereArgs = {
					Long.toString(sourceId),
					"0"
			};

			retVal = getDb().delete(
					SpellTable.TABLE_NAME,
					whereClause,
					whereArgs);

			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "initSource", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}


	public int deleteBySource(long sourceId) {
		String whereClause = SpellTable.COLUMN_NAME_SOURCE + " = ?";

		String[] whereArgs = { Long.toString(sourceId) };

		return getDb().delete(
				SpellTable.TABLE_NAME,
				whereClause,
				whereArgs);
	}

	public int getCountBySource(long sourceId) {
		int retVal;

		retVal = (int)DatabaseUtils.queryNumEntries(
				getDb(),
				SpellTable.TABLE_NAME,
				SpellTable.COLUMN_NAME_SOURCE + " = ?",
				new String[] { Long.toString(sourceId) }
		);

		return retVal;
	}

	protected ContentValues getContentValues(Spell spell) {
		ContentValues retVal = new ContentValues();

		retVal.put(SpellTable.COLUMN_NAME_SOURCE, spell.getSourceId());
		retVal.put(SpellTable.COLUMN_NAME_UID, spell.getUid());
		retVal.put(SpellTable.COLUMN_NAME_TYPE, spell.getType());
		retVal.put(SpellTable.COLUMN_NAME_LEVEL, spell.getLevel());
		retVal.put(SpellTable.COLUMN_NAME_NAME, spell.getName());
		retVal.put(SpellTable.COLUMN_NAME_BOOK, spell.getBookTxt());
		retVal.put(SpellTable.COLUMN_NAME_BOOK_BITFIELD, spell.getBook());
		retVal.put(SpellTable.COLUMN_NAME_REVERSIBLE, spell.isReversible() ? 1 : 0);
		retVal.put(SpellTable.COLUMN_NAME_SCHOOLS, spell.getSchoolsTxt());
		retVal.put(SpellTable.COLUMN_NAME_SCHOOLS_BITFIELD, spell.getSchools());
		retVal.put(SpellTable.COLUMN_NAME_SPHERES, spell.getSpheresTxt());
		retVal.put(SpellTable.COLUMN_NAME_SPHERES_BITFIELD, spell.getSpheres());
		retVal.put(SpellTable.COLUMN_NAME_RANGE, spell.getRange());
		retVal.put(SpellTable.COLUMN_NAME_COMPONENTS, spell.getComposTxt());
		retVal.put(SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD, spell.getCompos());
		retVal.put(SpellTable.COLUMN_NAME_DURATION, spell.getDuration());
		retVal.put(SpellTable.COLUMN_NAME_CAST_TIME, spell.getCastingTime());
		retVal.put(SpellTable.COLUMN_NAME_AOE, spell.getArea());
		retVal.put(SpellTable.COLUMN_NAME_SAVING, spell.getSaving());
		retVal.put(SpellTable.COLUMN_NAME_DESCRIPTION, spell.getDesc());
		retVal.put(SpellTable.COLUMN_NAME_AUTHOR, spell.getAuthor());
		retVal.put(SpellTable.COLUMN_NAME_UPDATED, 1);

		return retVal;
	}

}
