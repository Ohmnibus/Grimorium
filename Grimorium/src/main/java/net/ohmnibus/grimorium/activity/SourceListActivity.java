package net.ohmnibus.grimorium.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.ohmnibus.grimorium.GrimoriumApp;
import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.controller.SourceManager;
import net.ohmnibus.grimorium.entity.Source;
import net.ohmnibus.grimorium.fragment.ProgressFragmentDialog;
import net.ohmnibus.grimorium.fragment.SourceDetailFragment;
import net.ohmnibus.grimorium.fragment.SourceInsertFragment;
import net.ohmnibus.grimorium.fragment.SourceListFragment;

public class SourceListActivity extends AppCompatActivity
implements View.OnClickListener,
		SourceListFragment.OnFragmentInteractionListener,
		SourceDetailFragment.OnFragmentInteractionListener,
		SourceInsertFragment.OnFragmentInteractionListener,
		SourceManager.OnSourceImportedListener,
		DialogInterface.OnDismissListener {

	private static final String TAG = "SourceListActivity";

	public static final String ACTION_MANAGE = "ACTION_MANAGE";
	public static final String EXTRA_SOURCE_CHANGED = "SourceChanged";
	public static final int REQUEST_SOURCE_DETAIL = 1;
	public static final int RESULT_SOURCE_CHANGED = RESULT_FIRST_USER;

	public static final String BUNDLE_IS_LOADING = "BUNDLE_IS_LOADING";

	private GrimoriumApp mApp;
	private boolean twoPane;
	private Toolbar mToolbar;
	private ActionBar mActionBar;
	private FloatingActionButton mFab;
	private SourceListFragment mSourceListFragment;
	private boolean mSourceChanged = false;
	private SourceManager mSourceManager;
	private boolean mIsLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_IS_LOADING)) {
			mIsLoading = savedInstanceState.getBoolean(BUNDLE_IS_LOADING);
		}

		initViews();

		mSourceManager = new SourceManager(this, this);
	}

	protected void initViews() {
		setContentView(R.layout.activity_source_list);

		mApp = GrimoriumApp.getInstance();

		if (! mApp.isDbManagerInitialized()) {
			//Something went wrong
			finish();
			return;
		}

		twoPane = findViewById(R.id.fragment_container) != null;

		initToolbar();

		initTitle();

		mFab = (FloatingActionButton) findViewById(R.id.fab);
		mFab.setOnClickListener(this);

		initSourceList();

	}

	private void initToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		mActionBar = getSupportActionBar();
		if (mActionBar != null) {
			mActionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	private void initTitle() {
		setTitle(getString(R.string.lbl_source_manager));
	}

	private void initSourceList() {

		mSourceListFragment = SourceListFragment.newInstance();

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_list, mSourceListFragment)
				.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra(EXTRA_SOURCE_CHANGED, mSourceChanged);
		setResult(RESULT_CANCELED, data);
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SOURCE_DETAIL) {
			if (resultCode == SourceDetailActivity.RESULT_DELETED) {
				if (data != null) {
					long deletedSourceId = data.getLongExtra(SourceDetailActivity.EXTRA_DELETED_SOURCE_ID, -1);
					int deletedSpellCount = data.getIntExtra(SourceDetailActivity.EXTRA_DELETED_SPELL_COUNT, -1);
					onSourceDeleted(deletedSourceId, deletedSpellCount);
					return;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(BUNDLE_IS_LOADING, mIsLoading);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.fab) {
			//Add source
			addSource();
		}
	}

	@Override
	public void onRequestForNewSources() {
		addSource();
	}

	@Override
	public void onSourceClick(long sourceId) {
		showSourceDetails(sourceId);
	}

	private void addSource() {

		SourceInsertFragment newFragment = SourceInsertFragment.newInstance("");

		newFragment.show(getSupportFragmentManager(), "SourceAddFragment");
	}

	private void importSource(String rawUrl) {
		showProgressDialog();

		mIsLoading = true;
		mSourceManager.startDownload(rawUrl);
	}

	@Override
	public void onSourceImported(int created, int updated, int skipped, int deleted) {
		hideProgressDialog();

		mIsLoading = false;
		mSourceChanged = true;
		mSourceListFragment.refresh();
	}

	@Override
	public void onSourceImportProgress(int itemReadSoFar) {
		updateProgressDialog(itemReadSoFar);
	}

	@Override
	public void onSourceImportFailed(int errorCode) {
		hideProgressDialog();

		mIsLoading = false;
		SourceManager.getErrorDialog(this, errorCode).show();
	}

	ProgressFragmentDialog mProgressDialog = null;

	private static final String TAG_PROGRESS_DIALOG = "ProgressDialog";

	private void showProgressDialog() {
		mProgressDialog = (ProgressFragmentDialog) getSupportFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
		if (mProgressDialog == null) {
			mProgressDialog = ProgressFragmentDialog.showMessageDialog(
					this,
					R.string.lbl_source_add,
					R.string.lbl_source_loading,
					true,
					true,
					false,
					TAG_PROGRESS_DIALOG
			);
		}
	}

	private void hideProgressDialog() {
		mProgressDialog = (ProgressFragmentDialog) getSupportFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG);
		if (mProgressDialog != null) {
			//mProgressDialog.dismiss();
			mProgressDialog.dismissAllowingStateLoss();
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

	private void showSourceDetails(long sourceId) {
		if (twoPane) {
			showSourceDetailsFragment(sourceId);
		} else {
			showSourceDetailsActivity(sourceId);
		}
	}

	Fragment[] sourceFragmentCache = new Fragment[2];
	int sourceFragmentCacheNum = 0;

	protected void showSourceDetailsFragment(long sourceId) {
		sourceFragmentCacheNum = (sourceFragmentCacheNum + 1) % sourceFragmentCache.length;

		if (sourceFragmentCache[sourceFragmentCacheNum] != null
				&& ! sourceFragmentCache[sourceFragmentCacheNum].isRemoving()) {

			((SourceDetailFragment)sourceFragmentCache[sourceFragmentCacheNum]).setSourceId(sourceId);
			Log.i(TAG, "Recycling fragment.");
		} else {
			sourceFragmentCache[sourceFragmentCacheNum] = SourceDetailFragment.newInstance(sourceId, true);
		}
		Fragment fragment = sourceFragmentCache[sourceFragmentCacheNum];

		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
				.replace(R.id.fragment_container, fragment)
				.commit();
	}

	/**
	 * Remove the detail source fragment.
	 */
	protected void removeSourceDetailsFragment() {
		removeSourceDetailsFragment(-1);
	}

	/**
	 * Remove the detail source fragment if is related to the specified source id.
	 * @param sourceId Source Id
	 */
	protected void removeSourceDetailsFragment(long sourceId) {
		Fragment fragment = sourceFragmentCache[sourceFragmentCacheNum];
		if (fragment instanceof SourceDetailFragment) {
			if (sourceId != -1 && ((SourceDetailFragment)fragment).getSourceId() == sourceId) {
				getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
						.remove(fragment)
						.commit();
			}
		}
	}

	protected void showSourceDetailsActivity(long sourceId) {
		Intent i = new Intent(this, SourceDetailActivity.class);
		i.setAction(SourceDetailActivity.ACTION_DETAIL);
		i.putExtra(SourceDetailActivity.EXTRA_SOURCE_ID, sourceId);
		startActivityForResult(i, REQUEST_SOURCE_DETAIL);
	}

	@Override
	public void onSourceLoadCompleted(Source source) {
		//NOOP
	}

	@Override
	public void onSourceUpdated() {
		mSourceChanged = true;
	}

	@Override
	public void onSourceDeleted(long deletedSourceId, int deletedSpellCount) {
		mSourceChanged = true;
		mSourceListFragment.refresh();
		removeSourceDetailsFragment(deletedSourceId);
	}

	@Override
	public void onFragmentInteraction(String uri) {
		if (! TextUtils.isEmpty(uri)) {
			importSource(uri);
		}
	}

	@Override
	public void onDismiss(DialogInterface dialogInterface) {
		if (mIsLoading) {
			mSourceManager.cancelDownload();
			Toast.makeText(this, R.string.lbl_source_load_canceled, Toast.LENGTH_SHORT).show();
		}
	}
}
