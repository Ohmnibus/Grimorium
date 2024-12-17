package net.ohmnibus.grimorium.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import net.ohmnibus.grimorium.database.BaseCursorLoader;
import net.ohmnibus.grimorium.database.ProfileDbAdapter;

/**
 * Created by Ohmnibus on 14/01/2017.
 */

public abstract class BaseProfileCursorAdapter<VH extends RecyclerView.ViewHolder> extends CursorRecyclerViewAdapter<VH>
		implements LoaderManager.LoaderCallbacks<Cursor>{

	LoaderManager mLoaderManager;

	public BaseProfileCursorAdapter(Context context, LoaderManager loaderManager, Cursor cursor) {
		super(context, cursor);

		mLoaderManager = loaderManager;
	}

	protected abstract Context getContext();

	//region AsyncTaskLoader

	protected LoaderManager getLoaderManager() {
		return mLoaderManager;
	}

	protected abstract int getLoaderID();

	private void initLoader() {
		getLoaderManager().initLoader(getLoaderID(), null, this);
	}

	private void restartLoader() {
		getLoaderManager().restartLoader(getLoaderID(), null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new ProfileCursorLoader(getContext());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		//NOOP
	}

	private static class ProfileCursorLoader extends BaseCursorLoader {

		private ProfileCursorLoader(Context context) {
			super(context);
		}

		@Override
		public Cursor loadCursor() {
			return ProfileDbAdapter.getInstance().getCursor();
		}
	}

	//endregion
}
