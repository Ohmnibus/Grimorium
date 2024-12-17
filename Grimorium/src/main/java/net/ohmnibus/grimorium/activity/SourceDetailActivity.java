package net.ohmnibus.grimorium.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.entity.Source;
import net.ohmnibus.grimorium.fragment.SourceDetailFragment;

/**
 * Created by Ohmnibus on 01/11/2016.
 */

public class SourceDetailActivity extends AppCompatActivity
implements SourceDetailFragment.OnFragmentInteractionListener{

	public static final String ACTION_DETAIL = "ACTION_DETAIL";
	public static final String EXTRA_SOURCE_ID = "EXTRA_SOURCE_ID";
	public static final String EXTRA_DELETED_SOURCE_ID = "DeletedSourceId";
	public static final String EXTRA_DELETED_SPELL_COUNT = "DeletedSpellCount";

	public static final int RESULT_DELETED = RESULT_FIRST_USER;

	private Toolbar mToolbar;
	long mSourceId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handleIntent(getIntent());

		setContentView(R.layout.activity_source_detail);

		initToolbar();

		initViews();
	}

	private void initToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	private void initViews() {
		Fragment fragment = SourceDetailFragment.newInstance(mSourceId, false);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		//transaction.add(R.id.fragment_container, fragment);
		transaction.replace(R.id.fragment_container, fragment);

		transaction.commit();
	}

	private void handleIntent(Intent intent) {
		if (ACTION_DETAIL.equals(intent.getAction()) && intent.hasExtra(EXTRA_SOURCE_ID)) {
			mSourceId = intent.getLongExtra(EXTRA_SOURCE_ID, -1);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				setResult(RESULT_CANCELED);
				this.finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSourceLoadCompleted(Source source) {
		setTitle(source.getName());
	}

	@Override
	public void onSourceUpdated() {

	}

	@Override
	public void onSourceDeleted(long deletedSourceId, int deletedSpellCount) {
		Intent data = new Intent();
		data.putExtra(EXTRA_DELETED_SOURCE_ID, deletedSourceId);
		data.putExtra(EXTRA_DELETED_SPELL_COUNT, deletedSpellCount);
		setResult(RESULT_DELETED, data);
		finish();
	}
}
