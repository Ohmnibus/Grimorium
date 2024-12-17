package net.ohmnibus.grimorium.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.database.BaseCursorLoader;
import net.ohmnibus.grimorium.database.SourceDbAdapter;
import net.ohmnibus.grimorium.entity.Source;

/**
 * Created by Ohmnibus on 28/10/2016.
 */

public class SourceCursorAdapter extends CursorRecyclerViewAdapter<SourceCursorAdapter.SourceViewHolder>
		implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final long ID_SOURCE_ADD = -3;

	private static final int NO_RES_ID = android.R.id.empty;

	private static final int TYPE_SOURCE = 0;
	private static final int TYPE_ADD_SOURCE = 1;

	private static final int LOADER_SOURCE_LIST = 0x00ab;

	private final Fragment mFragment;
	private final int mItemResId;
	private final int mAddResId;
	private final boolean mUseAddItem;
	private final LayoutInflater mInflater;
	private final SourceDbAdapter mDbAdapter;

	private OnItemEventListener mOnItemEventListener = null;

	public SourceCursorAdapter(Fragment fragment, int itemResId) {
		this(fragment, itemResId, NO_RES_ID);
	}

	public SourceCursorAdapter(Fragment fragment, int itemResId, int addResId) {
		super(fragment.getContext(), null);

		mFragment = fragment;
		mItemResId = itemResId;
		mAddResId = addResId;
		mUseAddItem = mAddResId != NO_RES_ID;
		mInflater = LayoutInflater.from(fragment.getContext());
		mDbAdapter = SourceDbAdapter.getInstance();

		initLoader();
	}

	@Override
	public SourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		SourceViewHolder retVal;

		if (viewType == TYPE_SOURCE) {
			View convertView = mInflater.inflate(mItemResId, parent, false);
			retVal = new SourceViewHolder(convertView);
		} else {
			View convertView = mInflater.inflate(mAddResId, parent, false);
			retVal = new SourceAddViewHolder(convertView);
		}

		return retVal;
	}

	@Override
	public void onBindViewHolder(SourceViewHolder viewHolder, int position) {
		if (isAddItem(position)) {
			//NOOP
		} else {
			super.onBindViewHolder(viewHolder, position);
		}
	}

	@Override
	public void onBindViewHolder(SourceViewHolder viewHolder, int position, Cursor cursor) {
		Source item = mDbAdapter.get(cursor);
		viewHolder.bindAllViews(item, position);
	}

	public Source getItem(int position) {
		return mDbAdapter.get(getItemId(position));
	}

	@Override
	public int getItemCount() {
		return mUseAddItem ? super.getItemCount() + 1 : super.getItemCount();
	}

	@Override
	public int getItemViewType(int position) {
		return isAddItem(position) ? TYPE_ADD_SOURCE: TYPE_SOURCE;
	}

	@Override
	public long getItemId(int position) {
		if (isAddItem(position)) {
			return ID_SOURCE_ADD;
		}
		return super.getItemId(position);
	}


	public void setOnItemEventListener(OnItemEventListener listener) {
		mOnItemEventListener = listener;
	}

	public void refresh() {
		restartLoader();
	}

	private boolean isAddItem(int position) {
		return mUseAddItem && position == super.getItemCount();
	}

	//region Loaders

	private void initLoader() {
		mFragment.getLoaderManager().initLoader(LOADER_SOURCE_LIST, null, this);
	}

	private void restartLoader() {
		mFragment.getLoaderManager().restartLoader(LOADER_SOURCE_LIST, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new SourceCursorLoader(mFragment.getContext());
	}

	Handler mHandler = new Handler();

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

		changeCursor(data);

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mOnItemEventListener != null) {
					mOnItemEventListener.onDataReady(SourceCursorAdapter.this);
				}
			}
		});
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		//NOOP
	}

	private static class SourceCursorLoader extends BaseCursorLoader {

		private SourceCursorLoader(Context context) {
			super(context);
		}

		@Override
		public Cursor loadCursor() {
			return SourceDbAdapter.getInstance().getCursor();
		}
	}

	//endregion

	//region Item event handling

	private void onItemClickListener(View view, int position, long sourceId, String key) {
		if (mOnItemEventListener != null) {
			mOnItemEventListener.onItemClick(this, view, position, sourceId, key);
		}
	}
	private void onAddItemClickListener(View view) {
		if (mOnItemEventListener != null) {
			mOnItemEventListener.onAddItemClick(this, view);
		}
	}

	//endregion

	//region ViewHolders

	protected class SourceViewHolder extends RecyclerView.ViewHolder
			implements View.OnClickListener {

		Source mSource;
		int mPosition;
		View mRoot;
		TextView mName;

		private SourceViewHolder(View itemView) {
			super(itemView);
			findAllViews(itemView);
		}

		protected void findAllViews(View baseView) {
			mRoot = baseView;
			mRoot.setOnClickListener(this);
			mName = (TextView) baseView.findViewById(R.id.name);
		}

		protected void bindAllViews(Source item, int position) {
			mSource = item;
			mPosition = position;
			mName.setText(item.getName());
		}

		@Override
		public void onClick(View view) {
			//Item selected
			onItemClickListener(view, mPosition, mSource.getId(), mSource.getNameSpace());
		}
	}

	protected class SourceAddViewHolder extends SourceViewHolder
			implements View.OnClickListener {

		View mRoot;
		//ImageButton mAdd;

		private SourceAddViewHolder(View itemView) {
			super(itemView);
			findAllViews(itemView);
		}

		protected void findAllViews(View baseView) {
			mRoot = baseView;
			//mAdd = (ImageButton)baseView.findViewById(R.id.add);
			//mAdd.setOnClickListener(this);
			mRoot.setOnClickListener(this);
		}

		protected void bindAllViews(Source item, int position) {
			//NOOP
		}

		@Override
		public void onClick(View view) {
			onAddItemClickListener(view);
		}
	}

	//endregion

	public interface OnItemEventListener {
		void onDataReady(SourceCursorAdapter adapter);
		void onItemClick(SourceCursorAdapter adapter, View view, int position, long sourceId, String key);
		void onAddItemClick(SourceCursorAdapter adapter, View view);
	}
}
