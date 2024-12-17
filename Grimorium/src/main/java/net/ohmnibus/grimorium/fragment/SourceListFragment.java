package net.ohmnibus.grimorium.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.adapter.DividerItemDecoration;
import net.ohmnibus.grimorium.adapter.SourceCursorAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SourceListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SourceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SourceListFragment extends DialogFragment
implements SourceCursorAdapter.OnItemEventListener, View.OnClickListener {

	private OnFragmentInteractionListener mListener;

	private SourceCursorAdapter mAdapter;
	private RecyclerView mRecyclerView = null;
	private ViewGroup mErrorContent;
	private TextView mErrorMessage;

	public SourceListFragment() {
		// Required empty public constructor
	}

	public static SourceListFragment newInstance() {
		SourceListFragment fragment = new SourceListFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//return super.onCreateDialog(savedInstanceState);
		AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
		bld.setIcon(R.drawable.ic_launcher);
		bld.setTitle(R.string.lbl_profiles);
		bld.setNegativeButton(R.string.lbl_cancel, null);
		bld.setView(createView(getActivity().getLayoutInflater(), null, null));
		return bld.create();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view;
		if (getShowsDialog()) {
			//If this is a dialog, no need of this view
			//view = new View(getContext());
			view = super.onCreateView(inflater, container, savedInstanceState);
		} else {
			view = createView(inflater, container, savedInstanceState);
		}
		//View view = inflater.inflate(R.layout.item_profile, container, false);
		return view;
	}

	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_source_list, container, false);

		//Init the adapter
		//mAdapter = new SourceCursorAdapter(getContext(), R.layout.item_source, R.layout.item_profile_add);
		mAdapter = new SourceCursorAdapter(this, R.layout.item_source); //No "add item": use FAB
		mAdapter.setOnItemEventListener(this);

		Context context = view.getContext();
		mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
		mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setVisibility(View.VISIBLE);

		mErrorContent = (ViewGroup) view.findViewById(R.id.vg_no_data_container);
		mErrorContent.setVisibility(View.GONE);
		mErrorMessage = (TextView) view.findViewById(R.id.tv_no_data_message);

		return view;
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

	@Override
	public void onDataReady(SourceCursorAdapter adapter) {
		if (adapter.getItemCount() > 0) {
			mRecyclerView.setVisibility(View.VISIBLE);
			mErrorContent.setVisibility(View.GONE);
		} else {
			mRecyclerView.setVisibility(View.GONE);
			mErrorContent.setVisibility(View.VISIBLE);
			mErrorContent.setOnClickListener(this);
			mErrorMessage.setText(R.string.lbl_source_no_data);
			//mErrorMessage.setOnClickListener(this);
			if (mListener != null) {
				mListener.onRequestForNewSources();
			}
		}
	}

	@Override
	public void onItemClick(SourceCursorAdapter adapter, View view, int position, long sourceId, String key) {
		mListener.onSourceClick(sourceId);
	}

	@Override
	public void onAddItemClick(SourceCursorAdapter adapter, View view) {

	}

	public void refresh() {
		mAdapter.refresh();
	}

	@Override
	public void onClick(View view) {
		mListener.onRequestForNewSources();
	}

	public interface OnFragmentInteractionListener {
		void onRequestForNewSources();
		void onSourceClick(long sourceId);
	}
}
