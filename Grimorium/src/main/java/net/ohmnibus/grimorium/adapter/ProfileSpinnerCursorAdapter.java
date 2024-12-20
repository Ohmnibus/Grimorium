package net.ohmnibus.grimorium.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.database.BaseCursorLoader;
import net.ohmnibus.grimorium.database.GrimoriumContract;
import net.ohmnibus.grimorium.database.ProfileDbAdapter;
import net.ohmnibus.grimorium.entity.Profile;

/**
 * Created by Ohmnibus on 14/01/2017.
 */

public class ProfileSpinnerCursorAdapter extends SimpleCursorAdapter
		implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "ProfileSpinnerAdapter";

	public static final long ID_PROFILE_ADD = -3;

	private static final int TYPE_PROFILE = 0;
	private static final int TYPE_ADD_PROFILE = 1;

	private static final int LOADER_PROFILE_SPINNER = 0x000a;

	private final AppCompatActivity mActivity;
	private final LayoutInflater mInflater;
	private final ProfileDbAdapter mDbAdapter;
	private int mLayout;
	private int mDropDownLayout;
	private final OnDataReadyListener mDataReadyListener;

	public ProfileSpinnerCursorAdapter(AppCompatActivity activity, int layout, int dropDownLayout, OnDataReadyListener dataReadyListener) {
		super(
				activity,
				layout,
				null,
				new String[] {GrimoriumContract.ProfileTable.COLUMN_NAME_NAME},
				new int[] {android.R.id.text1},
				0
		);
		mActivity = activity;
		mInflater = LayoutInflater.from(activity);
		setViewResource(layout);
		setDropDownViewResource(dropDownLayout);
		mDbAdapter = ProfileDbAdapter.getInstance();
		mDataReadyListener = dataReadyListener;
		initLoader();
	}

	/**
	 * <p>Sets the layout resource of the item views.</p>
	 * <p>This is the layout used for the single selected item shown
	 * when the spinner is closed.</p>
	 *
	 * @param layout the layout resources used to create item views
	 */
	@Override
	public void setViewResource(int layout) {
		super.setViewResource(layout);
		mLayout = layout;
	}

	/**
	 * <p>Sets the layout resource of the drop down views.</p>
	 * <p>This is the layout used to represent each element when
	 * the spinner is open.</p>
	 *
	 * @param dropDownLayout the layout resources used to create drop down views
	 */
	@Override
	public void setDropDownViewResource(int dropDownLayout) {
		super.setDropDownViewResource(dropDownLayout);
		mDropDownLayout = dropDownLayout;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View retVal;
		if (getItemViewType(position) == TYPE_PROFILE) {
			retVal = super.getView(position, convertView, parent);
		} else {
			//This should never happen
			retVal = getAddView(position, convertView, parent, mLayout);
		}
		return retVal;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View retVal;
		if (getItemViewType(position) == TYPE_PROFILE) {
			retVal = super.getDropDownView(position, convertView, parent);
		} else {
			retVal = getAddView(position, convertView, parent, mDropDownLayout);
		}
		return retVal;
	}

	public Profile getItem(int position) {
		return mDbAdapter.get(getItemId(position));
	}

	@Override
	public int getCount() {
		return super.getCount() + 1;
	}

	@Override
	public int getItemViewType(int position) {
		return isAddItem(position) ? TYPE_ADD_PROFILE : TYPE_PROFILE;
	}

	@Override
	public long getItemId(int position) {
		if (isAddItem(position)) {
			return ID_PROFILE_ADD;
		}
		return super.getItemId(position);
	}

	@Override
	public void changeCursor(Cursor cursor) {
		super.changeCursor(cursor);
		if (mDataReadyListener != null) {
			mDataReadyListener.onDataReady();
		}
	}

	private boolean isAddItem(int position) {
		return position == super.getCount();
	}

	private View getAddView(@SuppressWarnings("unused") int position, View convertView, ViewGroup parent, int layout) {
		View retVal;

		if (convertView == null) {
			retVal = mInflater.inflate(layout, parent, false);
		} else {
			retVal = convertView;
		}

		//Bind
		TextView text = retVal.findViewById(android.R.id.text1);
		text.setText(R.string.lbl_profile_spinner_add);

		return retVal;
	}

	public int getPosition(long id) {
		int retVal;
		Cursor cursor = getCursor();
		//int oriPos = cursor.getPosition();
		int idIndex = cursor.getColumnIndex(GrimoriumContract.SpellTable._ID);
		retVal = cursor.getCount(); //Position of the "Add profile" item
		for (int i=0;i<retVal;i++){
			if (cursor.moveToPosition(i)) {
				if (cursor.getLong(idIndex) == id){
					retVal = i;
					break;
				}
			}
		}
		//cursor.moveToPosition(oriPos);
		return retVal;
	}

	public void refresh() {
		Log.d(TAG, "Refreshing...");
		restartLoader();
	}

	//region AsynkTaskLoader

	private void initLoader() {
		Log.d(TAG, "Initializing...");
		LoaderManager.getInstance(mActivity).initLoader(LOADER_PROFILE_SPINNER, null, this);
	}

	private void restartLoader() {
		LoaderManager.getInstance(mActivity).restartLoader(LOADER_PROFILE_SPINNER, null, this);
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new ProfileCursorLoader(mActivity);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
		changeCursor(data);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
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

	public interface OnDataReadyListener {
		void onDataReady();
	}
}
