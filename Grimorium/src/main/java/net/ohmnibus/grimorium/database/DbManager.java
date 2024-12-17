package net.ohmnibus.grimorium.database;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Ohmnibus on 28/09/2016.
 */

public class DbManager {

	private Context mContext;
	private SourceDbAdapter mSourceDbAdapter;
	private ProfileDbAdapter mProfileDbAdapter;
	private SpellProfileDbAdapter mSpellProfileDbAdapter;

	//region Static methods

	public static void init(Context context, OnDbInitializedListener listener) {
		new DbManager(context).init(listener);
	}

	public static void dispose() {
		BaseDbAdapter.dispose();
	}

	//endregion

	private DbManager(Context context) {
		mContext = context;
	}

	public SourceDbAdapter getSourceDbAdapter() {
		return mSourceDbAdapter;
	}

	public ProfileDbAdapter getProfileDbAdapter() {
		return mProfileDbAdapter;
	}

	public SpellProfileDbAdapter getSpellProfileDbAdapter() {
		return mSpellProfileDbAdapter;
	}

	//region private/protected methods

	private void init(OnDbInitializedListener listener) {
		new DataBaseInitializer(listener).execute();
	}

	private void init() {
		BaseDbAdapter.init(mContext);
		mSourceDbAdapter = SourceDbAdapter.getInstance();
		mProfileDbAdapter = ProfileDbAdapter.getInstance();
		mSpellProfileDbAdapter = SpellProfileDbAdapter.getInstance();
	}

	//endregion

	protected class DataBaseInitializer extends AsyncTask<Void, Void, Void> {
		OnDbInitializedListener mListener;

		public DataBaseInitializer(OnDbInitializedListener listener) {
			mListener = listener;
		}

		@Override
		protected Void doInBackground(Void... voids) {
			init();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mListener != null) {
				mListener.onDbInitialized(DbManager.this);
			}
		}
	}

	public interface OnDbInitializedListener {
		void onDbInitialized(DbManager dbManager);
	}
}
