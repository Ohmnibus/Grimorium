package net.ohmnibus.grimorium.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.ohmnibus.grimorium.entity.Source;
import net.ohmnibus.grimorium.database.GrimoriumContract.SourceTable;

/**
 * Created by Ohmnibus on 10/09/2016.
 */
public class SourceDbAdapter extends BaseDbAdapter {

	private static final String TAG = "SourceDbAdapter";

	private static final String SORT_DEFAULT =
			SourceTable.COLUMN_NAME_NAME + " Asc ";

	private static final SourceDbAdapter mInstance = new SourceDbAdapter();

	public static SourceDbAdapter getInstance() {
		return mInstance;
	}

	protected SourceDbAdapter() {
		super();
	}

	public Cursor getCursor() {
		return getDb().query(
				SourceTable.TABLE_NAME,
				null,
				null,
				null,
				null,
				null,
				SORT_DEFAULT
		);
	}

	public Source get(long id) {
		Source retVal;

		Cursor c = getDb().query(
				SourceTable.TABLE_NAME,
				null,
				SourceTable._ID + " = ?",
				new String[] { Long.toString(id) },
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

	/**
	 * Return the ID of the given {@link Source},
	 * eventually creating a new record.
	 * @param source Reference Source
	 * @return The Source ID, or -1 if an error occurred during creation.
	 */
	@SuppressWarnings("unused")
	public long getSourceId(Source source) {
		long retVal;

		if (source.isValidId()) {
			retVal = source.getId();
		} else {
			Source dbSource = getByNamespace(source.getNameSpace());
			if (dbSource != null) {
				retVal = dbSource.getId();
			} else {
				retVal = insert(source);
			}
		}

		return retVal;
	}

	public Source getByNamespace(String nameSpace) {
		Source retVal;

		Cursor c = getDb().query(
				SourceTable.TABLE_NAME,
				null,
				SourceTable.COLUMN_NAME_NAMESPACE + " = ? COLLATE NOCASE",
				new String[] {nameSpace},
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

	public Source get(Cursor c) {
		if (c.isClosed() || c.isBeforeFirst() || c.isAfterLast())
			return null;

		Source retVal = new Source();

		retVal.setId(getLong(c, SourceTable._ID));
		retVal.setName(getString(c, SourceTable.COLUMN_NAME_NAME));
		retVal.setNameSpace(getString(c, SourceTable.COLUMN_NAME_NAMESPACE));
		retVal.setVersion(getInt(c, SourceTable.COLUMN_NAME_VERSION));
		retVal.setDesc(getString(c, SourceTable.COLUMN_NAME_DESCRIPTION));
		retVal.setUrl(getString(c, SourceTable.COLUMN_NAME_URL));

		return retVal;
	}

	public long insert(Source source) {
		return insert(source, false);
	}

	public long insert(Source source, boolean inTransaction) {
		long retVal;

		if (! inTransaction) getDb().beginTransaction();

		try {
			retVal = getDb().insert(
					SourceTable.TABLE_NAME,
					null,
					getContentValues(source));

			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "insert", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	public int update(String nameSpace, Source source) {
		return update(nameSpace, source, false);
	}

	public int update(String nameSpace, Source source, boolean inTransaction) {
		int retVal;

		ContentValues values = getContentValues(source);
		values.remove(SourceTable.COLUMN_NAME_NAMESPACE);

		if (! inTransaction) getDb().beginTransaction();

		try {
			retVal = getDb().update(
					SourceTable.TABLE_NAME,
					values,
					SourceTable.COLUMN_NAME_NAMESPACE + " = ?",
					new String[] { nameSpace }
			);

			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "update", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	public int delete(long sourceId) {
		return delete(sourceId, false);
	}

	public int delete(long sourceId, boolean inTransaction) {
		int retVal = 0;
		int affected;

		if (! inTransaction) getDb().beginTransaction();

		try {
			//Remove source
			affected = getDb().delete(
					SourceTable.TABLE_NAME,
					SourceTable._ID + " = ?",
					new String[] { Long.toString(sourceId) }
			);
			Log.i(TAG, "Deleted " + affected + " source(s)");
			retVal = retVal + affected;

			//Remove spells belonging to source
			SpellDbAdapter.getInstance().deleteBySource(sourceId, true);
			Log.i(TAG, "Deleted " + affected + " spell(s) in source(s)");
			retVal = retVal + affected;

			//Remove useless star records
			affected = SpellProfileDbAdapter.getInstance().deleteUnused(true);
			Log.i(TAG, "Deleted " + affected + " useless stars(s) in spell(s)");

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

	protected ContentValues getContentValues(Source source) {
		ContentValues values = new ContentValues();
		values.put(SourceTable.COLUMN_NAME_NAME, source.getName());
		values.put(SourceTable.COLUMN_NAME_NAMESPACE, source.getNameSpace());
		values.put(SourceTable.COLUMN_NAME_VERSION, source.getVersion());
		values.put(SourceTable.COLUMN_NAME_DESCRIPTION, source.getDesc());
		values.put(SourceTable.COLUMN_NAME_URL, source.getUrl());
		return values;

	}
}
