package net.ohmnibus.grimorium.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.ohmnibus.grimorium.R;

public class ProfileDetailFragment extends Fragment {

	private static final String ARG_PROFILE_ID = "ARG_PROFILE_ID";

	private long mProfileId;

	private OnFragmentInteractionListener mListener;

	public ProfileDetailFragment() {
		// Required empty public constructor
	}

	public static ProfileDetailFragment newInstance(long profileId) {
		ProfileDetailFragment fragment = new ProfileDetailFragment();
		Bundle args = new Bundle();
		args.putLong(ARG_PROFILE_ID, profileId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mProfileId = getArguments().getLong(ARG_PROFILE_ID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_profile_detail, container, false);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}
}
