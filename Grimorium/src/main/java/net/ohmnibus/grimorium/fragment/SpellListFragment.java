package net.ohmnibus.grimorium.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.ohmnibus.grimorium.BuildConfig;
import net.ohmnibus.grimorium.GrimoriumApp;
import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.adapter.AdsSpellCursorAdapter;
import net.ohmnibus.grimorium.adapter.DividerItemDecoration;
import net.ohmnibus.grimorium.adapter.SpellCursorAdapter;

public class SpellListFragment extends Fragment
		implements SpellCursorAdapter.OnItemEventListener, View.OnClickListener {

	private static final String ARG_PROFILE_ID = "ARG_PROFILE_ID";
	private static final String ARG_DISABLED_ADS = "ARG_DISABLED_ADS";

	private long mProfileId;
	private boolean mIsDisableAds = false;
	private boolean mExplicitIsDisableAds = false;
	private OnFragmentInteractionListener mListener;

	private RecyclerView mRecyclerView;
	private SpellCursorAdapter mAdapter;
	private ViewGroup mErrorContent;
	private TextView mErrorMessage;

	public SpellListFragment() {
		// Required empty public constructor
	}

	public static SpellListFragment newInstance(long profileId, boolean disableAds) {
		SpellListFragment fragment = new SpellListFragment();
		if (profileId > 0) {
			Bundle args = new Bundle();
			args.putLong(ARG_PROFILE_ID, profileId);
			args.putBoolean(ARG_DISABLED_ADS, disableAds);
			fragment.setArguments(args);
		}
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean disableAds;
		if (getArguments() != null) {
			mProfileId = getArguments().getLong(ARG_PROFILE_ID);
			disableAds = getArguments().getBoolean(ARG_DISABLED_ADS);
		} else {
			mProfileId = -1;
			disableAds = false;
		}

		synchronized (this) {
			if (! mExplicitIsDisableAds) {
				mIsDisableAds = disableAds;
			}

			initAdapter(mIsDisableAds);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_spell_list, container, false);

		initViews(root);

		return root;
	}

	public void setDisabledAds(boolean isDisabledAds) {
		synchronized (this) {
			if (isAdded() && getActivity() != null) {
				if (BuildConfig.ADS_ON_LIST && mAdapter != null && mIsDisableAds != isDisabledAds) {
					//Update adapter
					destroyAdapter();
					initAdapter(isDisabledAds);
				}

				if (mRecyclerView != null) {
					mRecyclerView.setAdapter(mAdapter);
				}
			}

			mIsDisableAds = isDisabledAds;
			mExplicitIsDisableAds = true;
		}
	}

	public void setProfile(long profileId) {
		if (mProfileId != profileId) {
			mProfileId = profileId;
			mAdapter.setProfile(mProfileId);
		}
	}

	public void applyFilter(String query) {
		mAdapter.applyFilter(query);
	}

	/**
	 * Notify that the details of current profile are changed
	 * and a refresh is required.
	 */
	public void notifyProfileChanged() {
		mAdapter.notifyProfileChanged();
	}

	protected void initViews(View root) {

		initRecyclerView(root);
	}

	private void initAdapter(boolean disableAds) {
		if (! BuildConfig.ADS_ON_LIST || disableAds) {
			mAdapter = new SpellCursorAdapter(this, mProfileId, R.layout.item_spell);
		} else {
			mAdapter = new AdsSpellCursorAdapter(this, mProfileId, R.layout.item_spell);
		}
		mAdapter.setOnItemEventListener(this);
	}

	private void destroyAdapter() {
		mAdapter.setOnItemEventListener(null);
		mAdapter = null;
	}

	private void initRecyclerView(View root) {

		RecyclerView.ItemDecoration itemDecoration = new
				DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL_LIST);

		synchronized (this) {
			mRecyclerView = root.findViewById(R.id.recyclerView);
			mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
			mRecyclerView.addItemDecoration(itemDecoration);
			mRecyclerView.setAdapter(mAdapter);
			mRecyclerView.setVisibility(View.VISIBLE);
		}

		mErrorContent = root.findViewById(R.id.vg_no_data_container);
		mErrorContent.setVisibility(View.GONE);
		mErrorMessage = root.findViewById(R.id.tv_no_data_message);
		//mErrorMessage.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.getClass().getSimpleName()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onDataReady(SpellCursorAdapter adapter) {
		if (adapter.getItemCount() > 0) {
			mRecyclerView.setVisibility(View.VISIBLE);
			mErrorContent.setVisibility(View.GONE);
		} else {
			mRecyclerView.setVisibility(View.GONE);
			mErrorContent.setVisibility(View.VISIBLE);
			if (GrimoriumApp.getInstance().getSourceCount() > 0) {
				mErrorMessage.setText(R.string.lbl_spell_no_match);
				//mErrorMessage.setOnClickListener(null);
				mErrorContent.setOnClickListener(null);
			} else {
				mErrorMessage.setText(R.string.lbl_spell_no_data);
				//mErrorMessage.setOnClickListener(this);
				mErrorContent.setOnClickListener(this);
			}
		}
	}

	@Override
	public void onItemClick(View view, int position, long id) {
		mListener.onSpellSelected(mProfileId, id);
	}

	@Override
	public void onItemCheckedChanged(View view, int position, long id, boolean isCheched) {
		mListener.onSpellStarred(mProfileId, id, isCheched);
	}

	@Override
	public void onClick(View view) {
		mListener.onRequestForNewSources();
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {

		void onRequestForNewSources();

		void onSpellSelected(long profileId, long spellId);

		void onSpellStarred(long profileId, long spellId, boolean isStarred);

	}
}
