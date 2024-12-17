package net.ohmnibus.grimorium.helper;

import android.database.Cursor;

import net.ohmnibus.grimorium.GrimoriumApp;
import net.ohmnibus.grimorium.database.GrimoriumContract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Ohmnibus on 18/05/2017.
 */

public class SourceCache implements Iterable<SourceCache.SourceHeader> {

	public static class SourceHeader {
		long mId;
		String mName;

		public SourceHeader(long id, String name) {
			mId = id;
			mName = name;
		}

		public long getId() {
			return mId;
		}

		public void setId(long id) {
			this.mId = id;
		}

		public String getName() {
			return mName;
		}

		public void setName(String name) {
			this.mName = name;
		}
	}

	private ArrayList<SourceHeader> mCache = new ArrayList<>();

	public void refresh() {
		Cursor cursor = GrimoriumApp.getInstance().getDbManager().getSourceDbAdapter().getCursor();
		refresh(cursor);
		cursor.close();
	}

	public void refresh(Cursor cursor) {
		mCache.clear();
		int IDX_ID = cursor.getColumnIndex(GrimoriumContract.SourceTable._ID);
		int IDX_NAME = cursor.getColumnIndex(GrimoriumContract.SourceTable.COLUMN_NAME_NAME);
		while (cursor.moveToNext()) {
			mCache.add(new SourceHeader(
					cursor.getLong(IDX_ID),
					cursor.getString(IDX_NAME)
			));
		}
	}

	public void clear() {
		mCache.clear();
	}

	public int size() {
		return mCache.size();
	}

	public SourceHeader get(int index) {
		return mCache.get(index);
	}

	@Override
	public Iterator<SourceHeader> iterator() {
		return new SourceHeaderIterator();
	}


	private class SourceHeaderIterator implements Iterator<SourceHeader> {

		int index;

		public SourceHeaderIterator() {
			index = 0;
		}

		@Override
		public boolean hasNext() {
			return index < mCache.size();
		}

		@Override
		public SourceHeader next() {
			if (! this.hasNext()) {
				throw new NoSuchElementException();
			}
			return mCache.get(index++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
