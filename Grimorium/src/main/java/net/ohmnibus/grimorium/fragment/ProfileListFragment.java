package net.ohmnibus.grimorium.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.adapter.DividerItemDecoration;
import net.ohmnibus.grimorium.adapter.ProfileListCursorAdapter;
import net.ohmnibus.grimorium.entity.Profile;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ProfileListFragment extends DialogFragment
	implements ProfileListCursorAdapter.OnItemEventListener,
		InputTextFragmentDialog.OnFragmentInteractionListener {

	private static final String ARG_PROFILE_ID = "ARG_PROFILE_ID";

	private long mCurrentProfileId;
	private ProfileListCursorAdapter mAdapter;
	private OnFragmentInteractionListener mListener;

	public static ProfileListFragment newInstance(long currentProfileId) {
		ProfileListFragment fragment = new ProfileListFragment();
		if (currentProfileId > 0) {
			Bundle args = new Bundle();
			args.putLong(ARG_PROFILE_ID, currentProfileId);
			fragment.setArguments(args);
		}
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ProfileListFragment() {
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context
					+ " must implement OnListFragmentInteractionListener");
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mCurrentProfileId = getArguments().getLong(ARG_PROFILE_ID);
		} else {
			mCurrentProfileId = ProfileListCursorAdapter.ID_PROFILE_DEFAULT;
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//return super.onCreateDialog(savedInstanceState);
		FragmentActivity activity = requireActivity();
		AlertDialog.Builder bld = new AlertDialog.Builder(activity);
		bld.setIcon(R.drawable.ic_launcher);
		bld.setTitle(R.string.lbl_profiles);
		bld.setNegativeButton(R.string.lbl_cancel, null);
		bld.setView(createView(activity.getLayoutInflater(), null, null));
		return bld.create();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view;
		if (getShowsDialog()) {
			//If this is a dialog, no need of this view
			view = new View(getContext());
		} else {
			view = createView(inflater, container, savedInstanceState);
		}
		return view;
	}

	public void setCurrentProfile(long currentProfileId) {
		if (mCurrentProfileId != currentProfileId) {
			mCurrentProfileId = currentProfileId;
			mAdapter.setCurrentProfile(mCurrentProfileId);
		}
	}
	protected View createView(LayoutInflater inflater, ViewGroup container, @SuppressWarnings("unused") Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_list, container, false);

		//Init the adapter
		mAdapter = new ProfileListCursorAdapter(this, R.layout.item_profile, R.layout.item_profile_add);
		mAdapter.setOnItemEventListener(this);

		mAdapter.setCurrentProfile(mCurrentProfileId);

		Context context = view.getContext();
		RecyclerView recyclerView = view.findViewById(R.id.list);
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
		recyclerView.setAdapter(mAdapter);

		return view;
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onItemClick(ProfileListCursorAdapter adapter, View view, int position, long profileId, String key) {
		setCurrentProfile(profileId);
		mListener.onProfileSelected(profileId, key);
		if (getShowsDialog()) {
			dismiss();
		}
	}

	//region ProfileListCursorAdapter.OnItemEventListener interface

	private static final int REQUEST_ADD = 1;
	private static final int REQUEST_EDIT = 2;

	@Override
	public void onAddItemClick(ProfileListCursorAdapter adapter, View view) {
		InputTextFragmentDialog newFragment = InputTextFragmentDialog.newInstance(
				R.string.lbl_profile_add,
				0,
				R.string.lbl_profile_hint,
				null
		);
		newFragment.setTargetFragment(this, REQUEST_ADD);
		if (isAdded()) {
			newFragment.show(getParentFragmentManager(), "InputTextFragmentDialog");
		}
	}

	private Profile targetProfile;
	@Override
	public void onEditItemClick(ProfileListCursorAdapter adapter, View view, int position, long profileId, String key) {
		targetProfile = adapter.getItem(position);
		InputTextFragmentDialog newFragment = InputTextFragmentDialog.newInstance(
				R.string.lbl_profile_edit,
				0,
				R.string.lbl_profile_hint,
				targetProfile.getName()
		);
		newFragment.setTargetFragment(this, REQUEST_EDIT);
		if (isAdded()) {
			newFragment.show(getParentFragmentManager(), "InputTextFragmentDialog");
		}
	}

	@Override
	public void onRemoveItemClick(ProfileListCursorAdapter adapter, View view, int position, long profileId, String key) {
		Context ctx = getContext();
		if (ctx == null) {
			ctx = view.getContext();
		}
		targetProfile = adapter.getItem(position);
		new AlertDialog.Builder(ctx)
				.setTitle(R.string.lbl_profiles)
				.setMessage(getString(R.string.lbl_profile_remove_msg, targetProfile.getName()))
				.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						mAdapter.remove(targetProfile.getId());
						mListener.onProfileDeleted(targetProfile.getId(), targetProfile.getKey());
						if (targetProfile.getId() == mCurrentProfileId) {
							//Current profile was deleted.
							//Select default
							Profile defaultProfile = mAdapter.getItem(Profile.PROFILE_KEY_DEFAULT);
							setCurrentProfile(defaultProfile.getId());
							mListener.onProfileSelected(defaultProfile.getId(), defaultProfile.getKey());
						}
					}
				})
				.setNegativeButton(R.string.lbl_cancel, null)
				.show();
	}

	//endregion

	@Override
	public void onTextEntered(int action, String text) {
		switch (action) {
			case REQUEST_ADD:
				if (! TextUtils.isEmpty(text)) {
					Profile profile = new Profile(text);
					mAdapter.addNew(profile);
					mListener.onProfileAdded(profile.getId(), profile.getKey());
				}
				break;
			case REQUEST_EDIT:
				if (! TextUtils.isEmpty(text)) {
					targetProfile.setName(text);
					mAdapter.update(targetProfile);
					mListener.onProfileHeaderChanged(targetProfile.getId(), targetProfile.getKey());
				}
				break;
		}
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		void onProfileSelected(long profileId, String key);
		void onProfileDeleted(long profileId, String key);
		void onProfileAdded(long profileId, String key);
		void onProfileHeaderChanged(long profileId, String key);
	}
}
