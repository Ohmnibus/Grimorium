package net.ohmnibus.grimorium.adapter;


import android.content.Context;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import net.ohmnibus.grimorium.database.BaseCursorLoader;
import net.ohmnibus.grimorium.database.GrimoriumContract.SpellTable;
import net.ohmnibus.grimorium.database.GrimoriumContract.StarTable;
import net.ohmnibus.grimorium.database.SpellProfileDbAdapter;
import net.ohmnibus.grimorium.databinding.ItemSpellBinding;
import net.ohmnibus.grimorium.entity.Spell;
import net.ohmnibus.grimorium.helper.SpellFormatter;

/**
 * CursorAdapter for spells
 * Created by Ohmnibus on 27/09/2016.
 */

public class SpellCursorAdapter extends CursorRecyclerViewAdapter<SpellCursorAdapter.SpellViewHolder>
		implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_SPELL_LIST = 0x00aa;

	private Fragment mFragment;
	private long mProfileId;
	private int mResId;
	private LayoutInflater mInflater = null;
	private CharSequence mQuery;

	private OnItemEventListener mOnItemEventListener = null;

	public SpellCursorAdapter(Fragment fragment, long profileId, int resourceId) {
		super(fragment.getContext(), null);
		mFragment = fragment;
		mProfileId = profileId;
		mResId = resourceId;
		mInflater = LayoutInflater.from(fragment.getContext());
		mQuery = "";
		initLoader();
	}

	public void setOnItemEventListener(OnItemEventListener itemEventListener) {
		mOnItemEventListener = itemEventListener;
	}

	@Override
	public void onBindViewHolder(SpellViewHolder viewHolder, int position, Cursor cursor) {
		viewHolder.bindAllViews(cursor);
	}

	@Override
	public SpellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		SpellViewHolder retVal;

		View convertView = mInflater.inflate(mResId, parent, false);
		retVal = new SpellViewHolder(convertView);

		return retVal;
	}

	public void setProfile(long profileId) {
		if (mProfileId != profileId) {
			mProfileId = profileId;
			notifyProfileChanged();
		}
	}

	public void applyFilter(String query) {
		if (!TextUtils.equals(mQuery, query)) {
			mQuery = query;
			refresh();
		}
	}

	/**
	 * Notify that the profile details are changed
	 * and a refresh is required.
	 */
	public void notifyProfileChanged() {
		refresh();
	}

	private void initLoader() {
		mFragment.getLoaderManager().initLoader(LOADER_SPELL_LIST, null, this);
	}

	private void refresh() {
		mFragment.getLoaderManager().restartLoader(LOADER_SPELL_LIST, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new SpellCursorLoader(mFragment.getContext(), mProfileId, mQuery);
	}

	private Handler mHandler = new Handler();

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

		changeCursor(data);

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mOnItemEventListener != null) {
					mOnItemEventListener.onDataReady(SpellCursorAdapter.this);
				}
			}
		});
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		//NOOP
	}

	private void onViewHolderClick(View view, int position, long id) {
		if (mOnItemEventListener != null) {
			mOnItemEventListener.onItemClick(view, position, id);
		}
	}

	private void onViewHolderCheckedChanged(View view, int position, long id, boolean isChecked) {
		SpellProfileDbAdapter.getInstance().setStar(mProfileId, id, isChecked);

		notifyProfileChanged();

		if (mOnItemEventListener != null) {
			mOnItemEventListener.onItemCheckedChanged(view, position, id, isChecked);
		}
	}

	private static final String[] COLUMNS = new String[] {
			SpellTable.TABLE_NAME + "." + SpellTable._ID,
			SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_TYPE,
			SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_LEVEL,
			SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_NAME,
			SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_REVERSIBLE,
			SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_SCHOOLS_BITFIELD,
			SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_SPHERES_BITFIELD,
			SpellTable.TABLE_NAME + "." + SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD,
			StarTable.TABLE_NAME + "." + StarTable.COLUMN_NAME_STARRED
	};

	private static final int IDX_SPELL_ID = 0;
	private static final int IDX_SPELL_TYPE = 1;
	private static final int IDX_SPELL_LEVEL = 2;
	private static final int IDX_SPELL_NAME = 3;
	private static final int IDX_SPELL_REVERSIBLE = 4;
	private static final int IDX_SPELL_SCHOOL = 5;
	private static final int IDX_SPELL_SPHERE = 6;
	private static final int IDX_SPELL_COMPOS = 7;
	private static final int IDX_SPELL_STAR = 8;

	class SpellViewHolder extends RecyclerView.ViewHolder
			implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

		long mId;
		ItemSpellBinding mBinding;

		SpellViewHolder(View baseView) {
			this(baseView, false);
		}

		SpellViewHolder(View baseView, boolean jumpConstructor) {
			super(baseView);
			if (! jumpConstructor) {
				//findAllViews(baseView);

				mBinding = DataBindingUtil.bind(baseView);

				baseView.setOnClickListener(this);
			}
		}

		void bindAllViews(Cursor cursor) {
			mId = cursor.getLong(IDX_SPELL_ID);

			Context ctx = itemView.getContext();
			//SpellFormatter spellFmt = new SpellFormatter(ctx, data);

			mBinding.lblLevel.setText(
					SpellFormatter.getLevel(ctx, cursor.getInt(IDX_SPELL_LEVEL))
			);
			mBinding.lblLevel.getBackground().setLevel(
					SpellFormatter.getTypeLevel(ctx, cursor.getInt(IDX_SPELL_TYPE))
			);

			mBinding.cbxPreferred.setOnCheckedChangeListener(null);
			mBinding.cbxPreferred.setChecked(
					cursor.getInt(IDX_SPELL_STAR) != 0
			);
			mBinding.cbxPreferred.setOnCheckedChangeListener(this);

			String star = cursor.getInt(IDX_SPELL_REVERSIBLE) == 1 ? "*" : "";
			mBinding.lblName.setText(
					cursor.getString(IDX_SPELL_NAME) + star
			);

			if (cursor.getInt(IDX_SPELL_TYPE) == Spell.TYPE_CLERICS) {
				mBinding.lblSchool.setText(
						SpellFormatter.getSphere(ctx, cursor.getInt(IDX_SPELL_SPHERE))
				);
			} else {
				mBinding.lblSchool.setText(
						SpellFormatter.getSchool(ctx, cursor.getInt(IDX_SPELL_SCHOOL), false)
				);
			}

			mBinding.lblCompos.setText(
					SpellFormatter.getCompos(ctx, cursor.getInt(IDX_SPELL_COMPOS))
			);
		}

		@Override
		public void onClick(View view) {
			onViewHolderClick(
					view,
					getAdapterPosition(),
					mId //getItemId()
			);
		}

		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
			onViewHolderCheckedChanged(
					compoundButton,
					getAdapterPosition(),
					mId, //getItemId(),
					isChecked);
		}
	}

	private static class SpellCursorLoader extends BaseCursorLoader {

		private CharSequence mQuery;
		private long mProfileId;

		private SpellCursorLoader(Context context, long profileId, CharSequence query) {
			super(context);
			mProfileId = profileId;
			mQuery = query;
		}

		@Override
		public Cursor loadCursor() {
			return SpellProfileDbAdapter.getInstance().getCursor(COLUMNS, mProfileId, mQuery);
		}
	}

	public interface OnItemEventListener {
		void onDataReady(SpellCursorAdapter adapter);
		void onItemClick(View view, int position, long id);
		void onItemCheckedChanged(View view, int position, long id, boolean isCheched);
	}
}
