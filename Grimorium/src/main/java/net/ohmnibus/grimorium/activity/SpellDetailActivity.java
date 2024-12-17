package net.ohmnibus.grimorium.activity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.entity.SpellProfile;
import net.ohmnibus.grimorium.fragment.SpellDetailFragment;

public class SpellDetailActivity extends AppCompatActivity
implements SpellDetailFragment.OnFragmentInteractionListener {

	public static final String ACTION_DETAIL = "detail";
	public static final String EXTRA_SPELL = "spellProfile";
	public static final String EXTRA_SHOW_ADS = "showAds";
	public static final String EXTRA_STAR_CHANGED = "starChanged";

	private Toolbar mToolbar;
	private SpellProfile mSpellProfile = null;
	private boolean mShowAds = false;
	private boolean mStarChanged = false;

	public static Intent getIntentToStart(@NonNull Context context, SpellProfile spell, boolean showAds) {
		Intent i = new Intent(context, SpellDetailActivity.class);
		i.setAction(SpellDetailActivity.ACTION_DETAIL);
		i.putExtra(SpellDetailActivity.EXTRA_SPELL, spell);
		i.putExtra(SpellDetailActivity.EXTRA_SHOW_ADS, showAds);
		return i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handleIntent(getIntent());

		setContentView(R.layout.activity_spell_detail);

		initToolbar();

		initViews();
	}

	private void initToolbar() {
		mToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	private void initViews() {

		Fragment fragment = SpellDetailFragment.newInstance(mSpellProfile, false, mShowAds);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.executePendingTransactions();
		fragmentManager.beginTransaction()
				.replace(R.id.fragment_container, fragment)
				.commit();

		if (mSpellProfile != null) {
			setTitle(mSpellProfile.getName());
		} else {
			setTitle(R.string.msg_not_found);
		}
	}

	private void handleIntent(Intent intent) {
		if (ACTION_DETAIL.equals(intent.getAction())) {
			mSpellProfile = intent.getParcelableExtra(EXTRA_SPELL);
			mShowAds = intent.getBooleanExtra(EXTRA_SHOW_ADS, false);
//			if (mSpellProfile != null) {
//				mOriginalStarState = mSpellProfile.isStarred();
//			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				//this.finish();
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra(EXTRA_STAR_CHANGED, mStarChanged);
		setResult(RESULT_CANCELED, data);
		super.onBackPressed();
	}

	@Override
	public void onSpellDetailStarred(long profileId, long spellId, boolean isStarred) {
		mStarChanged = isStarred != mSpellProfile.isStarred();
	}
}
