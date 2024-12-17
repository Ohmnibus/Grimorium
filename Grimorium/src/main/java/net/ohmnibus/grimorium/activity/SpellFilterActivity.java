package net.ohmnibus.grimorium.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.entity.Profile;
import net.ohmnibus.grimorium.entity.SpellFilter;
import net.ohmnibus.grimorium.fragment.SpellFilterFragment;

/**
 * Created by Ohmnibus on 31/07/2016.
 */
public class SpellFilterActivity extends AppCompatActivity
implements SpellFilterFragment.OnFilterChangedListener {

	public static final String EXTRA_FILTER = "EXTRA_FILTER";
	private static final String EXTRA_PROFILE_ID = "EXTRA_PROFILE_ID";
	private static final String EXTRA_PROFILE_NAME = "EXTRA_PROFILE_NAME";
	private Toolbar mToolbar;
	private SpellFilter mSpellFilter;
	private long mProfileId;
	private String mProfileName;
	private boolean mFilterUpdated;
	private SpellFilterFragment mFragment;

	public static Intent getIntentToStart(@NonNull Context context, @NonNull Profile profile) {
		return getIntentToStart(context, profile.getSpellFilter(), profile.getId(), profile.getName());
	}

	public static Intent getIntentToStart(@NonNull Context context, SpellFilter spellFilter, long profileId, String profileName) {
		Intent i = new Intent(context, SpellFilterActivity.class);
		i.putExtra(SpellFilterActivity.EXTRA_FILTER, spellFilter);
		i.putExtra(SpellFilterActivity.EXTRA_PROFILE_ID, profileId);
		i.putExtra(SpellFilterActivity.EXTRA_PROFILE_NAME, profileName);
		return i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_spell_filter);

		handleIntent(getIntent());

		initToolbar();

		initViews(savedInstanceState);
	}

	private void handleIntent(Intent intent) {
		//if (Intent.ACTION_EDIT.equals(intent.getAction())) {
			mSpellFilter = intent.getParcelableExtra(EXTRA_FILTER);
			mProfileId = intent.getLongExtra(EXTRA_PROFILE_ID, 0);
			mProfileName = intent.getStringExtra(EXTRA_PROFILE_NAME);
		//}
		mFilterUpdated = false;
	}

	private void initToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		setTitle(getString(R.string.lbl_spell_filter));
		//mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			//actionBar.setDisplayShowHomeEnabled(true);
			//actionBar.setIcon(R.drawable.ic_launcher);
		}
	}

	private void initViews(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			mFragment = SpellFilterFragment.newInstance(mSpellFilter, mProfileId, mProfileName);

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, mFragment)
					.commit();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
		super.onSaveInstanceState(outState, outPersistentState);
		outState.putParcelable(EXTRA_FILTER, mSpellFilter);
		outState.putBoolean("Updated", mFilterUpdated);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mSpellFilter = savedInstanceState.getParcelable(EXTRA_FILTER);
		mFilterUpdated = savedInstanceState.getBoolean("Updated");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				prepareResponse();
				this.finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		prepareResponse();
		super.onBackPressed();
	}

	private void prepareResponse() {
		if (mFilterUpdated) {
			Intent mIntent = new Intent();
			mIntent.putExtra(EXTRA_FILTER, mSpellFilter);
			setResult(RESULT_OK, mIntent);
		} else {
			setResult(RESULT_CANCELED);
		}
	}

	@Override
	public void onFilterChanged(SpellFilter filter) {
		mSpellFilter = filter;
		mFilterUpdated = true;
	}
}
