package net.ohmnibus.grimorium.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.databinding.FragmentSourceInsertBinding;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SourceInsertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SourceInsertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SourceInsertFragment extends DialogFragment
implements DialogInterface.OnClickListener {

	private static final String ARG_PARAM = "param";

	private String mParam;

	private OnFragmentInteractionListener mListener;

	private FragmentSourceInsertBinding mBinding;
	private SrcAdapter mAdapter;

	public SourceInsertFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param Parameter 1 (unused).
	 * @return A new instance of fragment SourceInsertFragment.
	 */
	public static SourceInsertFragment newInstance(String param) {
		SourceInsertFragment fragment = new SourceInsertFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM, param);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam = getArguments().getString(ARG_PARAM);
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder bld = new AlertDialog.Builder(getContext());

		bld.setView(createView(getActivity().getLayoutInflater(), null, null));
		bld.setTitle(R.string.lbl_source_add);

		bld.setPositiveButton(R.string.lbl_ok, this);
		bld.setNegativeButton(R.string.lbl_cancel, this);

		return bld.create();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View retVal;
		if (getShowsDialog()) {
			retVal = super.onCreateView(inflater, container, savedInstanceState);
		} else {
			retVal = createView(inflater, container, savedInstanceState);
		}
		return retVal;
	}

	private View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_source_insert, container, false);

		mBinding = DataBindingUtil.bind(view);
		mAdapter = new SrcAdapter(
				getContext(),
				R.array.sources_default_url,
				R.array.sources_default_lbl);
		mBinding.rvList.setAdapter(mAdapter);

		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		if (getTargetFragment() instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) getTargetFragment();
			//mRequestCode = getTargetRequestCode();
		} else if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
			//mRequestCode = -1;
		} else {
			if (getTargetFragment() == null) {
				throw new RuntimeException(context.toString()
						+ " must implement OnListFragmentInteractionListener -or- define a target fragment");
			} else {
				throw new RuntimeException(getTargetFragment().toString()
						+ " must implement OnListFragmentInteractionListener");
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int whichButton) {
		if (whichButton == Dialog.BUTTON_POSITIVE) {
			//Verify if disclaimer is defined
			final String urlEntered = mBinding.tvUri.getText().toString();
			final OnFragmentInteractionListener listener = mListener;
			String disclaimer = null;
			String[] urlList = getResources().getStringArray(R.array.sources_default_url);
			String[] disclaimerList = getResources().getStringArray(R.array.sources_default_disclaimer);
			for (int i = 0; i < urlList.length; i++) {
				if (urlList[i].startsWith(urlEntered)) {
					disclaimer = disclaimerList[i];
					break;
				}
			}
			if (!TextUtils.isEmpty(disclaimer)) {
				//Show disclaimer
				new AlertDialog.Builder(getActivity())
						.setTitle(R.string.lbl_disclaimer_title)
						.setMessage(disclaimer)
						.setPositiveButton(R.string.lbl_disclaimer_agree, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//notifyChoiche(urlEntered);
								if (listener != null) {
									listener.onFragmentInteraction(urlEntered);
								}
							}
						})
						.setNegativeButton(R.string.lbl_disclaimer_disagree, null)
						.create()
						.show();
			} else {
				//No disclaimer
				notifyChoiche(urlEntered);
			}
		}
	}

	private void notifyChoiche(String url) {
		if (mListener != null) {
			mListener.onFragmentInteraction(url);
		}
	}

	private class SrcAdapter extends RecyclerView.Adapter<SrcAdapter.SrcViewHolder> {

		Context mContext;
		LayoutInflater mLayoutInflater;
		String[] mUriList;
		String[] mLabelList;
		int mBackgroundResource;

		public SrcAdapter(Context context, int uriRes, int labelRes) {
			super();
			mContext = context;
			mLayoutInflater = LayoutInflater.from(mContext);
			mUriList = mContext.getResources().getStringArray(uriRes);
			mLabelList = mContext.getResources().getStringArray(labelRes);
			if (mUriList == null || mLabelList == null || mUriList.length != mLabelList.length) {
				throw new IllegalArgumentException("Array resources must be of equal lenght.");
			}
			TypedValue outValue = new TypedValue();
			context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
			mBackgroundResource = outValue.resourceId;
		}

		@Override
		public SrcViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
			return new SrcViewHolder(view);
		}

		@Override
		public void onBindViewHolder(SrcViewHolder holder, int position) {
			holder.bind(position);
		}

		@Override
		public int getItemCount() {
			return mUriList.length;
		}

		protected class SrcViewHolder extends RecyclerView.ViewHolder
				implements View.OnClickListener{
			TextView mName;
			TextView mUri;

			public SrcViewHolder(View itemView) {
				super(itemView);
				mName = (TextView) itemView.findViewById(android.R.id.text1);
				mUri = (TextView) itemView.findViewById(android.R.id.text2);

				itemView.setOnClickListener(this);

				itemView.setBackgroundResource(mBackgroundResource);
			}

			public void bind(int position) {
				mName.setText(mLabelList[position]);
				mUri.setText(mUriList[position]);
			}

			@Override
			public void onClick(View view) {
				mBinding.tvUri.setText(mUriList[getAdapterPosition()]);
			}
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
		void onFragmentInteraction(String uri);
	}
}
