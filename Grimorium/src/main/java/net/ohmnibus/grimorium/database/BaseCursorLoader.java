package net.ohmnibus.grimorium.database;

import android.content.Context;
import android.database.Cursor;
import androidx.loader.content.AsyncTaskLoader;
import androidx.core.os.CancellationSignal;
import androidx.core.os.OperationCanceledException;

/**
 * Created by Ohmnibus on 16/06/2017.
 */

public abstract class BaseCursorLoader extends AsyncTaskLoader<Cursor> {

	private Cursor mCursor;
	private CancellationSignal mCancellationSignal;

	public BaseCursorLoader(Context context) {
		super(context);
	}

	public abstract Cursor loadCursor();

	/* Runs on a worker thread */
	@Override
	public Cursor loadInBackground() {
		synchronized (this) {
			if (isLoadInBackgroundCanceled()) {
				throw new OperationCanceledException();
			}
			mCancellationSignal = new CancellationSignal();
		}
		try {
			mCancellationSignal.throwIfCanceled();
			Cursor cursor = loadCursor();
			if (cursor != null) {
				try {
					// Ensure the cursor window is filled.
					cursor.getCount();
					//cursor.registerContentObserver(mObserver);
				} catch (RuntimeException ex) {
					cursor.close();
					throw ex;
				}
			}
			return cursor;
		} finally {
			synchronized (this) {
				mCancellationSignal = null;
			}
		}
	}

	@Override
	public void cancelLoadInBackground() {
		super.cancelLoadInBackground();
		synchronized (this) {
			if (mCancellationSignal != null) {
				mCancellationSignal.cancel();
			}
		}
	}

	/* Runs on the UI thread */
	@Override
	public void deliverResult(Cursor cursor) {
		if (isReset()) {
			// An async query came in while the loader is stopped
			if (cursor != null) {
				cursor.close();
			}
			return;
		}
		Cursor oldCursor = mCursor;
		mCursor = cursor;
		if (isStarted()) {
			super.deliverResult(cursor);
		}
		if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
			oldCursor.close();
		}
	}

	/**
	 * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
	 * will be called on the UI thread. If a previous load has been completed and is still valid
	 * the result may be passed to the callbacks immediately.
	 *
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading() {
		if (mCursor != null) {
			deliverResult(mCursor);
		}
		if (takeContentChanged() || mCursor == null) {
			forceLoad();
		}
	}

	/**
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}
	@Override
	public void onCanceled(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();
		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		mCursor = null;
	}

//	@Override
//	public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
//		super.dump(prefix, fd, writer, args);
//		writer.print(prefix); writer.print("mUri="); writer.println(mUri);
//		writer.print(prefix); writer.print("mProjection=");
//		writer.println(Arrays.toString(mProjection));
//		writer.print(prefix); writer.print("mSelection="); writer.println(mSelection);
//		writer.print(prefix); writer.print("mSelectionArgs=");
//		writer.println(Arrays.toString(mSelectionArgs));
//		writer.print(prefix); writer.print("mSortOrder="); writer.println(mSortOrder);
//		writer.print(prefix); writer.print("mCursor="); writer.println(mCursor);
//		writer.print(prefix); writer.print("mContentChanged="); writer.println(mContentChanged);
//	}
}
