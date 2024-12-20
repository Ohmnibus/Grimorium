package net.ohmnibus.grimorium.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.database.BaseCursorLoader;
import net.ohmnibus.grimorium.database.ProfileDbAdapter;
import net.ohmnibus.grimorium.entity.Profile;

/**
 * Created by Ohmnibus on 29/09/2016.
 */

public class ProfileListCursorAdapter extends CursorRecyclerViewAdapter<ProfileListCursorAdapter.ProfileViewHolder>
		implements LoaderManager.LoaderCallbacks<Cursor> {

	//public static final long ID_PROFILE_NONE = -2;
	public static final long ID_PROFILE_DEFAULT = -1;

	private static final long ID_PROFILE_ADD = -3;

	private static final int TYPE_PROFILE = 0;
	private static final int TYPE_ADD_PROFILE = 1;

	private static final int LOADER_PROFILE_LIST = 0x00bb;

	private final Fragment mFragment;
	private final int mItemResId;
	private final int mAddResId;
	private final LayoutInflater mInflater;
	private final ProfileDbAdapter mDbAdapter;
	private long mCurrentProfileId;

	private OnItemEventListener mOnItemEventListener = null;

	public ProfileListCursorAdapter(Fragment fragment, int itemResId, int addResId) {
		super(fragment.getContext(), null);

		mFragment = fragment;
		mItemResId = itemResId;
		mAddResId = addResId;
		mInflater = LayoutInflater.from(fragment.getContext());
		mDbAdapter = ProfileDbAdapter.getInstance();

		initLoader();
	}

	@NonNull
	@Override
	public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		ProfileViewHolder retVal;

		if (viewType == TYPE_PROFILE) {
			View convertView = mInflater.inflate(mItemResId, parent, false);
			retVal = new ProfileViewHolder(convertView);
		} else {
			View convertView = mInflater.inflate(mAddResId, parent, false);
			retVal = new ProfileAddViewHolder(convertView);
		}

		return retVal;
	}

	@Override
	public void onBindViewHolder(@NonNull ProfileViewHolder viewHolder, int position) {
		//noinspection StatementWithEmptyBody
		if (isAddItem(position)) {
			//NOOP
		} else {
			super.onBindViewHolder(viewHolder, position);
		}
	}

	@Override
	public void onBindViewHolder(ProfileViewHolder viewHolder, int position, Cursor cursor) {
		Profile profile = mDbAdapter.get(cursor);
		viewHolder.bindAllViews(profile, position);
	}

	public Profile getItem(int position) {
		return mDbAdapter.get(getItemId(position));
	}

	public Profile getItem(String key) {
		return mDbAdapter.get(key);
	}

	@Override
	public int getItemCount() {
		return super.getItemCount() + 1;
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

	public void setOnItemEventListener(OnItemEventListener listener) {
		mOnItemEventListener = listener;
	}

	public void setCurrentProfile(long currentProfileId) {
		if (mCurrentProfileId != currentProfileId) {
			int oldPos = -1;
			int newPos = -1;

			for (int i=0; i < getItemCount(); i++) {
				long id = getItemId(i);
				if (id == mCurrentProfileId) {
					newPos = i;
				} else if (id == currentProfileId) {
					oldPos = i;
				}
				if (oldPos != -1 && newPos != -1) {
					break;
				}
			}
			mCurrentProfileId = currentProfileId;

			if (oldPos != -1) {
				notifyItemChanged(oldPos);
			}
			if (newPos != -1) {
				notifyItemChanged(newPos);
			}
		}
	}

	public void addNew(Profile profile) {
		mDbAdapter.insert(profile);
		//Reload cursor async
		restartLoader();
	}

	/**
	 * Updates the header of the profile, not the filter.
	 * @param profile Profile to update.
	 */
	public void update(Profile profile) {
		mDbAdapter.updateHeader(profile);
		//Reload cursor async
		restartLoader();
	}

	public void remove(long profileId) {
		mDbAdapter.delete(profileId);
		//Reload cursor async
		restartLoader();
	}

	private boolean isAddItem(int position) {
		return position == super.getItemCount();
	}

	//region Item event handling

	private void onItemClickListener(View view, int position, long profileId, String key) {
		if (mOnItemEventListener != null) {
			mOnItemEventListener.onItemClick(this, view, position, profileId, key);
		}
	}
	private void onAddItemClickListener(View view) {
		if (mOnItemEventListener != null) {
			mOnItemEventListener.onAddItemClick(this, view);
		}
	}

	private void onEditItemClickListener(View view, int position, long profileId, String key) {
		if (mOnItemEventListener != null) {
			mOnItemEventListener.onEditItemClick(this, view, position, profileId, key);
		}
	}

	private void onRemoveItemClickListener(View view, int position, long profileId, String key) {
		if (mOnItemEventListener != null) {
			mOnItemEventListener.onRemoveItemClick(this, view, position, profileId, key);
		}
	}

	//endregion

	//region Viewholders

	protected class ProfileViewHolder extends RecyclerView.ViewHolder
			implements View.OnClickListener {

		Profile mProfile;
		int mPosition;
		View mRoot;
		TextView mName;
		ImageButton mEdit;
		ImageButton mDelete;

		public ProfileViewHolder(View itemView) {
			super(itemView);
			findAllViews(itemView);
		}

		protected void findAllViews(View baseView) {
			mRoot = baseView;
			mRoot.setOnClickListener(this);
			mName = baseView.findViewById(R.id.name);
			mEdit = baseView.findViewById(R.id.edit);
			mEdit.setOnClickListener(this);
			mDelete = baseView.findViewById(R.id.remove);
			mDelete.setOnClickListener(this);
		}

		protected void bindAllViews(Profile item, int position) {
			mProfile = item;
			mPosition = position;
			mName.setText(item.getName());
			if (item.isSys()) {
				mEdit.setVisibility(View.GONE);
				mDelete.setVisibility(View.GONE);
			} else {
				mEdit.setVisibility(View.VISIBLE);
				mDelete.setVisibility(View.VISIBLE);
			}
			boolean isSelected = mCurrentProfileId == mProfile.getId();
			isSelected = isSelected || mCurrentProfileId == ID_PROFILE_DEFAULT && mProfile.getKey().equals(Profile.PROFILE_KEY_DEFAULT);
			itemView.setSelected(isSelected);
		}

		@Override
		public void onClick(View view) {
			long viewId = view.getId();
			if (viewId == R.id.edit) {
				onEditItemClickListener(view, mPosition, mProfile.getId(), mProfile.getKey());
			} else if (viewId == R.id.remove) {
				onRemoveItemClickListener(view, mPosition, mProfile.getId(), mProfile.getKey());
			} else {
				//Item selected
				setCurrentProfile(mProfile.getId());
				onItemClickListener(view, mPosition, mProfile.getId(), mProfile.getKey());
			}
		}
	}

	protected class ProfileAddViewHolder extends ProfileViewHolder
			implements View.OnClickListener {

		@SuppressWarnings("FieldCanBeLocal")
		private View mRoot;

		public ProfileAddViewHolder(View itemView) {
			super(itemView);
			findAllViews(itemView);
		}

		protected void findAllViews(View baseView) {
			mRoot = baseView;
			mRoot.setOnClickListener(this);
		}

		protected void bindAllViews(Profile item, int position) {
			//NOOP
		}

		@Override
		public void onClick(View view) {
			onAddItemClickListener(view);
		}
	}

	//endregion

	//region AsyncTaskLoader

	private void initLoader() {
		LoaderManager.getInstance(mFragment).initLoader(LOADER_PROFILE_LIST, null, this);
	}

	private void restartLoader() {
		LoaderManager.getInstance(mFragment).restartLoader(LOADER_PROFILE_LIST, null, this);
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new ProfileCursorLoader(mFragment.getContext());
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

	public interface OnItemEventListener {
		void onItemClick(ProfileListCursorAdapter adapter, View view, int position, long profileId, String key);
		void onAddItemClick(ProfileListCursorAdapter adapter, View view);
		void onEditItemClick(ProfileListCursorAdapter adapter, View view, int position, long profileId, String key);
		void onRemoveItemClick(ProfileListCursorAdapter adapter, View view, int position, long profileId, String key);
	}

}
