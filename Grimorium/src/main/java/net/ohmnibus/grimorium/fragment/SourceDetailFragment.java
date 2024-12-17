package net.ohmnibus.grimorium.fragment;

import android.content.Context;
import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.ohmnibus.grimorium.GrimoriumApp;
import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.controller.SourceManager;
import net.ohmnibus.grimorium.database.DbManager;
import net.ohmnibus.grimorium.databinding.FragmentSourceDetailBinding;
import net.ohmnibus.grimorium.entity.Source;
import net.ohmnibus.grimorium.helper.Utils;

public class SourceDetailFragment extends Fragment
	implements SourceManager.OnSourceImportedListener,
		DialogInterface.OnDismissListener {

	private static final String TAG = "SourceDetailFragment";

	public static final long ID_SOURCE_NONE = -1;

	private static final String ARG_SOURCE_ID = "ARG_SOURCE_ID";
	private static final String ARG_SHOW_TITLE = "ARG_SHOW_TITLE";

	private static final String BUNDLE_IS_LOADING = "BUNDLE_IS_LOADING";

	private long mSourceId;
	private Source mSource;
	private boolean mShowTitle;
	private FragmentSourceDetailBinding mBinding;
	private SourceManager mSourceManager;
	private boolean mIsLoading;

	private OnFragmentInteractionListener mListener;

	public SourceDetailFragment() {
		// Required empty public constructor
	}

	public static SourceDetailFragment newInstance(long sourceId, boolean showTitle) {
		SourceDetailFragment fragment = new SourceDetailFragment();
		Bundle args = new Bundle();
		args.putLong(ARG_SOURCE_ID, sourceId);
		args.putBoolean(ARG_SHOW_TITLE, showTitle);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_IS_LOADING)) {
			mIsLoading = savedInstanceState.getBoolean(BUNDLE_IS_LOADING);
		}

		setHasOptionsMenu(true);

		if (getArguments() != null) {
			mSourceId = getArguments().getLong(ARG_SOURCE_ID);
			mShowTitle = getArguments().getBoolean(ARG_SHOW_TITLE);
		} else {
			mSourceId = ID_SOURCE_NONE;
			mShowTitle = false;
		}

		mSourceManager = new SourceManager(this, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_source_detail, container, false);

		initViews(root);

		return root;
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.menu_source_detail, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.mnuSourceDelete:
				deleteSource();
				break;
			case R.id.mnuSourceUpdate:
				updateSource();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(BUNDLE_IS_LOADING, mIsLoading);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		//Log.i(TAG, "onDetach");
	}

	private void initViews(View root) {

		mBinding = DataBindingUtil.bind(root);

		setSource(mSourceId, true);
	}

	public void setSourceId(long sourceId) {
		Bundle args = new Bundle();
		args.putLong(ARG_SOURCE_ID, sourceId);
		args.putBoolean(ARG_SHOW_TITLE, mShowTitle);
		setArguments(args);
	}

	public long getSourceId() {
		return mSourceId;
	}

	private void setSource(long sourceId, boolean forceRefresh) {
		if (!forceRefresh && sourceId == mSourceId) {
			//No change
			return;
		}
		mSourceId = sourceId;

		mSource = null;
		int spellCount = 0;

		if (mSourceId != ID_SOURCE_NONE) {
			DbManager dbManager = GrimoriumApp.getInstance().getDbManager();
			mSource = dbManager.getSourceDbAdapter().get(mSourceId);
			spellCount = dbManager.getSpellProfileDbAdapter().getCountBySource(mSourceId);
		}

		if (mSource != null) {
			mBinding.getRoot().setVisibility(View.VISIBLE);
			if (mShowTitle) {
				mBinding.tvTitle.setVisibility(View.VISIBLE);
				mBinding.tvTitle.setText(mSource.getName());
				mBinding.tvTitle.setSelected(true); //For marquee effect
			} else {
				mBinding.tvTitle.setVisibility(View.GONE);
			}

			mBinding.tvVersion.setText(Integer.toString(mSource.getVersion()));
			mBinding.tvNamespace.setText(mSource.getNameSpace());
			mBinding.tvNamespace.setSelected(true); //For marquee effect
			mBinding.tvUri.setText(mSource.getUrl());
			mBinding.tvUri.setSelected(true); //For marquee effect
			mBinding.tvElements.setText(Integer.toString(spellCount));
			Utils.initWebView(getContext(), mBinding.wvBody);
			mBinding.wvBody.loadDataWithBaseURL(
					"",
					"<p>" + mSource.getDesc() + "</p>",
					"text/html",
					"UTF-8",
					"");

			if (mListener != null) {
				mListener.onSourceLoadCompleted(mSource);
			}
		} else {
			//No data
			mBinding.getRoot().setVisibility(View.GONE);
		}
	}

	private void deleteSource() {
		if (mSource == null)
			return;

		new AlertDialog.Builder(getContext())
				.setTitle(R.string.lbl_source_remove_title)
				.setMessage(getString(R.string.lbl_source_remove_message, mSource.getName()))
				.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						//TODO: Make Async
						DbManager dbManager = GrimoriumApp.getInstance().getDbManager();
						int affected = dbManager.getSourceDbAdapter().delete(mSourceId);
						if (mListener != null) {
							mListener.onSourceDeleted(mSourceId, affected > 1 ? affected - 1 : 0);
						}
					}
				})
				.setNegativeButton(R.string.lbl_cancel, null)
				.show();
	}

	private void updateSource() {
		if (mSource == null)
			return;

		showProgressDialog();

		mIsLoading = true;
		mSourceManager.startDownload(mSource.getUrl());
	}

	ProgressFragmentDialog mProgressDialog = null;

	private static final String TAG_PROGRESS_DIALOG = "ProgressDialog";

	private void showProgressDialog() {
		mProgressDialog = (ProgressFragmentDialog) getFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
		if (mProgressDialog == null) {
//			mProgressDialog = ProgressDialog.show(
//					getContext(),
//					getString(R.string.lbl_source_update),
//					getString(R.string.lbl_source_loading),
//					true,
//					false);
			mProgressDialog = ProgressFragmentDialog.showMessageDialog(
					this.getActivity(),
					R.string.lbl_source_update,
					R.string.lbl_source_loading,
					true,
					true,
					false,
					TAG_PROGRESS_DIALOG
			);
		}
	}

	private void hideProgressDialog() {
		mProgressDialog = (ProgressFragmentDialog) getFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	private void updateProgressDialog(int progress) {
		if (mProgressDialog == null) {
			showProgressDialog();
		}
		if (mProgressDialog != null) {
			mProgressDialog.setMessage(getString(R.string.lbl_source_loading_progress, progress));
		}
	}

	//region SourceManager.OnSourceImportedListener interface

	@Override
	public void onSourceImported(int created, int updated, int skipped, int deleted) {
		hideProgressDialog();

		mIsLoading = false;

		setSource(mSourceId, true);

		mListener.onSourceUpdated();
	}

	@Override
	public void onSourceImportProgress(int itemReadSoFar) {
		updateProgressDialog(itemReadSoFar);
	}

	@Override
	public void onSourceImportFailed(int errorCode) {
		hideProgressDialog();

		mIsLoading = false;
		SourceManager.getErrorDialog(getContext(), errorCode).show();
	}

	//endregion

	@Override
	public void onDismiss(DialogInterface dialogInterface) {
		if (mIsLoading) {
			mSourceManager.cancelDownload();
			Toast.makeText(getActivity(), R.string.lbl_source_load_canceled, Toast.LENGTH_SHORT).show();
		}
	}

	public interface OnFragmentInteractionListener {
		/** Used to refresh container activity title (if needed) */
		void onSourceLoadCompleted(Source source);
		void onSourceUpdated();
		void onSourceDeleted(long deletedSourceId, int deletedSpellCount);
	}
}
