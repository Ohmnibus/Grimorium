package net.ohmnibus.grimorium.activity;

import net.ohmnibus.grimorium.GrimoriumApp;
import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.adapter.ProfileSpinnerCursorAdapter;
import net.ohmnibus.grimorium.database.DbManager;
import net.ohmnibus.grimorium.database.SpellProfileDbAdapter;
import net.ohmnibus.grimorium.entity.Profile;
import net.ohmnibus.grimorium.entity.SpellFilter;
import net.ohmnibus.grimorium.entity.SpellProfile;
import net.ohmnibus.grimorium.fragment.InputTextFragmentDialog;
import net.ohmnibus.grimorium.fragment.ProfileListFragment;
import net.ohmnibus.grimorium.fragment.SpellDetailFragment;
import net.ohmnibus.grimorium.fragment.SpellFilterFragment;
import net.ohmnibus.grimorium.fragment.SpellListFragment;
import net.ohmnibus.grimorium.helper.Utils;
import net.ohmnibus.grimorium.helper.iab.BillingClientHelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

public class SpellListActivity extends AppCompatActivity
		implements DbManager.OnDbInitializedListener,
		SearchView.OnQueryTextListener,
		SpellListFragment.OnFragmentInteractionListener,
		SpellDetailFragment.OnFragmentInteractionListener,
		SpellFilterFragment.OnFilterChangedListener,
		ProfileListFragment.OnFragmentInteractionListener,
		View.OnClickListener, AdapterView.OnItemSelectedListener,
		InputTextFragmentDialog.OnFragmentInteractionListener,
		ProfileSpinnerCursorAdapter.OnDataReadyListener,
		NavigationView.OnNavigationItemSelectedListener {

	private static final String TAG = "SpellListActivity";

	private static final int REQUEST_EDIT_FILTER = 0x000A;
	private static final int REQUEST_MANAGE_SOURCE = 0x000B;
	private static final int REQUEST_SPELL_DETAILS = 0x000C;

	private static final int ID_SPELL_NONE = -1;

	private GrimoriumApp mApp;
	private boolean mIsTwoPane;
	private Toolbar mToolbar;
	private SpellListFragment mSpellListFragment;
	private Profile mProfile;
	private Spinner mProfileSpinner = null;
	private boolean mProfileSpinnerListenerEnabled;
	private ProfileSpinnerCursorAdapter mProfileSpinnerAdapter;

	private boolean mIsFilterShowing = false;
	private long mShowingSpellId = ID_SPELL_NONE;

	/** Does the user have the no-ads upgrade? */
	boolean mIsDisableAds = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_spell_list);

		mApp = GrimoriumApp.getInstance();

		initViews();

		enableIabMenu(false); //Disable IAB menu until ready

		mApp.getPurchasesAsync(mOnPurchasesInitListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void initViews() {

		mIsTwoPane = findViewById(R.id.fragment_container) != null;

		mProfile = null;

		initToolbar();

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		//Initialization other than toolbar
		//is made only when the DB is ready

		mApp.initDbManager(this);
	}

	//region DbManager.OnDbInitializedListener

	@Override
	public void onDbInitialized(DbManager dbManager) {
		String key = mApp.getCurrentProfileKey();
		mProfile = dbManager.getProfileDbAdapter().get(key);

		initTitle(); //-> onDataReady -> setTitle

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(this);

		initSpellList();
	}

	//endregion

	private void initToolbar() {
		mToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayShowTitleEnabled(false); //Hide title in order to show spinner
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setIcon(R.drawable.ic_launcher);
		}
	}

	private void initTitle() {
		mProfileSpinnerAdapter = new ProfileSpinnerCursorAdapter(
				this,
				R.layout.item_profile_spinner, //Shown as the title
				android.R.layout.simple_spinner_dropdown_item, //Shown in the list
				this
		);
		//getSupportLoaderManager().initLoader(LOADER_PROFILE_SPINNER, null, mProfileSpinnerAdapter);
	}

	private void setTitle(Profile profile) {
		if (profile == null) {
			profile = mApp.getDbManager().getProfileDbAdapter().get(Profile.PROFILE_KEY_DEFAULT);
		}
		setTitle(profile.getId());
	}

	private void setTitle(long id) {
		int position = mProfileSpinnerAdapter.getPosition(id);
		//Tell onItemSelected event to do nothing
		mProfileSpinnerListenerEnabled = false;
		//mProfileSpinner.setSelection(position, false); //This overload update the spinner only if position is changed
		mProfileSpinner.setSelection(position); //This overload always update the spinner
		Log.i(TAG, "setTitle: " + id + "(" + position + ")");
	}

	private void initSpellList() {
		synchronized(this) {
			mSpellListFragment = SpellListFragment.newInstance(mProfile.getId(), mIsDisableAds);
		}

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_spell_list, mSpellListFragment)
				//.commit();
				.commitAllowingStateLoss();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);

		final MenuItem item = menu.findItem(R.id.mnuMainSearch);
		final SearchView searchView = (SearchView) item.getActionView();
		searchView.setOnQueryTextListener(this);

		return true;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		// Here is where we are going to implement our filter logic
		mSpellListFragment.applyFilter(query);

		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.fab) {
			//Edit filter
			editFilter();
		}
	}

	//region Handling messages from Fragments

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_EDIT_FILTER) {
			if (resultCode == RESULT_OK && data != null) {
				SpellFilter filter = data.getParcelableExtra(SpellFilterActivity.EXTRA_FILTER);

				onFilterChanged(filter);
			}
		} else if (requestCode == REQUEST_SPELL_DETAILS) {
			//Always refresh, unless the spell aren't changed
			if (data == null || data.getBooleanExtra(SpellDetailActivity.EXTRA_STAR_CHANGED, true)) {
				if (mSpellListFragment != null) { //DB may have not been initialized, yet
					mSpellListFragment.notifyProfileChanged();
				}
			}
		} else if (requestCode == REQUEST_MANAGE_SOURCE) {
			//Always refresh, unless the sources aren't changed
			if (data == null || data.getBooleanExtra(SourceListActivity.EXTRA_SOURCE_CHANGED, true)) {
				GrimoriumApp.getInstance().refreshSourceCache();
				mSpellListFragment.notifyProfileChanged();
				if (mIsTwoPane && mIsFilterShowing) {
					//Refresh filter
					showEditFilterFragment();
				}
			}
		}
	}

	@Override
	public void onRequestForNewSources() {
		manageSources();
	}

	@Override
	public void onSpellSelected(long profileId, long spellId) {
		showSpellDetails(spellId);
	}

	@Override
	public void onSpellStarred(long profileId, long spellId, boolean isStarred) {
		Fragment fragment = spellFragmentCache[spellFragmentCacheNum];

		if (mIsTwoPane
				&& mShowingSpellId == spellId
				&& fragment instanceof SpellDetailFragment
				&& fragment.isAdded()) {

			((SpellDetailFragment)fragment).showStar(isStarred);
		}
	}

	@Override
	public void onSpellDetailStarred(long profileId, long spellId, boolean isStarred) {
		//Refresh list
		mSpellListFragment.notifyProfileChanged();
	}

	@Override
	public void onFilterChanged(SpellFilter filter) {
		updateFilter(filter, true);
	}

	@Override
	public void onProfileSelected(long profileId, String key) {
		selectProfile(key, true);
	}

	@Override
	public void onProfileHeaderChanged(long profileId, String key) {
		if (profileId == mProfile.getId()) {
			mProfile = mApp.getDbManager().getProfileDbAdapter().get(profileId);
			setTitle(mProfile);
		}
	}

	@Override
	public void onProfileDeleted(long profileId, String key) {
		//Check if current profile was deleted
		if (mProfile.getKey().equals(key)) {
			//Select default profile
			selectProfile(Profile.PROFILE_KEY_DEFAULT, false);
		}
		//Update spinner & set title (on callback)
		mProfileSpinnerAdapter.refresh();
	}

	@Override
	public void onProfileAdded(long profileId, String key) {
		//Update spinner & set title (on callback)
		mProfileSpinnerAdapter.refresh();
	}

	//endregion

	/**
	 * Update current {@link Profile} filter.
	 * @param filter New {@link SpellFilter}
	 * @param applyFilter True to apply changes to current Spell List,
	 *                    False otherwise.
	 */
	protected void updateFilter(SpellFilter filter, boolean applyFilter) {
		mProfile.setSpellFilter(filter);
		mApp.getDbManager().getProfileDbAdapter().update(mProfile);
		if (applyFilter) {
			mSpellListFragment.notifyProfileChanged();
		}
	}

	protected void selectProfile(String key, boolean updateTitle) {
		selectProfile(
				mApp.getDbManager().getProfileDbAdapter().get(key),
				updateTitle);
	}

	private void selectProfile(Profile profile, boolean updateTitle) {
		if (profile == null) {
			profile = mApp.getDbManager().getProfileDbAdapter().get(Profile.PROFILE_KEY_DEFAULT);
		}

		mApp.setCurrentProfileKey(profile.getKey());

		mProfile = profile;

		mSpellListFragment.setProfile(mProfile.getId());

		if (updateTitle) {
			setTitle(mProfile);
		}

		if (mIsTwoPane && mIsFilterShowing) {
			//Refresh filter
			showEditFilterFragment();
		} else if (mIsTwoPane && mShowingSpellId != ID_SPELL_NONE) {
			//Refresh spell (mostly the star)
			showSpellDetails(mShowingSpellId);
		}
	}

	protected void editFilter() {
		mIsFilterShowing = true;
		mShowingSpellId = ID_SPELL_NONE;

		if (mIsTwoPane) {
			showEditFilterFragment();
		} else {
			showEditFilterActivity();
		}
	}

	protected void showEditFilterFragment() {
		SpellFilterFragment fragment = SpellFilterFragment.newInstance(mProfile);

		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
				.replace(R.id.fragment_container, fragment)
				.commit();
	}

	protected void showEditFilterActivity() {
		Intent i = SpellFilterActivity.getIntentToStart(this, mProfile);
		startActivityForResult(i, REQUEST_EDIT_FILTER);
	}

	protected void showSpellDetails(long id) {
		mShowingSpellId = id;
		mIsFilterShowing = false;

		if (mIsTwoPane) {
			showSpellDetailsFragment(id);
		} else {
			showSpellDetailsActivity(id);
		}
	}

	private final Fragment[] spellFragmentCache = new Fragment[2];
	private int spellFragmentCacheNum = 0;

	protected void showSpellDetailsFragment(long id) {

		SpellProfile spell = SpellProfileDbAdapter.getInstance().get(mProfile.getId(), id);
		spellFragmentCacheNum = (spellFragmentCacheNum + 1) % spellFragmentCache.length;
		if (spellFragmentCache[spellFragmentCacheNum] != null
				&& ! spellFragmentCache[spellFragmentCacheNum].isRemoving()) {
			((SpellDetailFragment)spellFragmentCache[spellFragmentCacheNum]).setSpell(spell);
			Log.i(TAG, "Recycling fragment.");
		} else {
			spellFragmentCache[spellFragmentCacheNum] = SpellDetailFragment.newInstance(spell, true, !mIsDisableAds);
		}

		Fragment fragment = spellFragmentCache[spellFragmentCacheNum];

		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
				.replace(R.id.fragment_container, fragment)
				.commit();
	}

	protected void showSpellDetailsActivity(long id) {
		SpellProfile spell = SpellProfileDbAdapter.getInstance().get(mProfile.getId(), id);
		Intent intent = SpellDetailActivity.getIntentToStart(this, spell, !mIsDisableAds);
		startActivityForResult(intent, REQUEST_SPELL_DETAILS);
	}

	protected void manageSources() {
		Intent intent = new Intent(this, SourceListActivity.class);
		intent.setAction(SourceListActivity.ACTION_MANAGE);
		startActivityForResult(intent, REQUEST_MANAGE_SOURCE);
	}

	private void buyDisableAds() {
		mApp.launchDisableAdsPurchaseFlow(this, mOnPurchaseFinishedListener);
	}

	protected void pickProfile() {
		DialogFragment newFragment = ProfileListFragment.newInstance(mProfile.getId());
		newFragment.show(getSupportFragmentManager(), "ProfileListFragment");
	}

	protected void addProfile() {
		InputTextFragmentDialog newFragment = InputTextFragmentDialog.newInstance(
				R.string.lbl_profile_add,
				0,
				R.string.lbl_profile_hint,
				null
		);
		newFragment.show(getSupportFragmentManager(), "InputTextFragmentDialog");
	}

	@Override
	public void onTextEntered(int requestCode, String text) {
		if (! TextUtils.isEmpty(text)) {
			Log.d(TAG, "Adding profile " + text);
			//Add new profile
			Profile profile = new Profile(text);
			mApp.getDbManager().getProfileDbAdapter().insert(profile);
			//Switch to profile
			selectProfile(profile, false);
			//Refresh spinner & update title (on callback)
			mProfileSpinnerAdapter.refresh();
		}
	}

	//region AdapterView.OnItemSelectedListener

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
		if (! mProfileSpinnerListenerEnabled) {
			//User has not touched the spinner: ignore
			Log.i(TAG, "onItemSelected: Ignored " + id + " (" + position + ")");
			return;
		}
		Log.i(TAG, "onItemSelected: " + id + " (" + position + ")");
		if (id == ProfileSpinnerCursorAdapter.ID_PROFILE_ADD) {
			//Set previous title
			setTitle(mProfile);
			//Launch "Add Profile" intent
			addProfile();
		} else {
			Profile profile = mProfileSpinnerAdapter.getItem(position);
			selectProfile(profile.getKey(), true);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
		Log.i(TAG, "onNothingSelected");
	}

	//endregion

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onDataReady() {
		//The data in the spinner is ready
		if (mProfileSpinner == null) {
			mProfileSpinner = findViewById(R.id.spnProfiles);

			mProfileSpinner.setAdapter(mProfileSpinnerAdapter);
			mProfileSpinner.setOnItemSelectedListener(this);
			mProfileSpinnerListenerEnabled = false;
			mProfileSpinner.setOnTouchListener((view, motionEvent) -> {
				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					//Handle spinner events only on user action
					mProfileSpinnerListenerEnabled = true;
				}
				return false;
			});
		}
		Log.d(TAG, "Data ready, updating title");
		setTitle(mProfile);
	}

	//region NavigationView.OnNavigationItemSelectedListener

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {

		long itemId = item.getItemId();
		if (itemId == R.id.mnuDrawerProfiles) {
			pickProfile();
		} else if (itemId == R.id.mnuDrawerSources) {
			manageSources();
		} else if (itemId == R.id.mnuDrawerIab) {
			buyDisableAds();
		} else if (itemId == R.id.mnuDrawerAbout) {
			Utils.showAboutBox(this);
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	//endregion

	//region In-App billing interfaces and callbacks

	/**
	 * Billing Client Helper initialization listener
	 */
	BillingClientHelper.PurchaseStatusListener mOnPurchasesInitListener = (status, products) -> {

		Log.d(TAG, "Purchases list initialized (" +  status + ", " + products + ")");

		switch (status) {
			case BillingClientHelper.PurchaseStatusListener.ERROR_NONE:
				//Ok
				mIsDisableAds = products.isDisabledAds();

				enableIabMenu(true);

				applyPurchase();

				break;
			case BillingClientHelper.PurchaseStatusListener.ERROR_BUSY:
				//Another operation is pending
				alert(getString(R.string.msg_iab_error_busy));

				enableIabMenu(true);

				applyPurchase();

				break;
			case BillingClientHelper.PurchaseStatusListener.ERROR_NOT_AVAILABLE:
				//alert(getString(R.string.msg_iab_error_not_available));
				Toast.makeText(this, R.string.msg_iab_error_not_available, Toast.LENGTH_LONG)
						.show();
				showIabMenu(false);
				break;
			case BillingClientHelper.PurchaseStatusListener.ERROR_SETUP:
			default:
				//Cannot initialize service.
				//Disable drawer option.
				alert(getString(R.string.msg_iab_error_inventory));
				showIabMenu(false);
				break;
		}
	};


	/**
	 * Billing Client Helper purchase listener
	 */
	BillingClientHelper.PurchaseStatusListener mOnPurchaseFinishedListener = (status, products) -> {

		Log.d(TAG, "Purchase finished (" +  status + ", " + products + ")");

		switch (status) {
			case BillingClientHelper.PurchaseStatusListener.ERROR_NONE:
			case BillingClientHelper.PurchaseStatusListener.ERROR_ITEM_OWNED: //Oh?

				mIsDisableAds = products.isDisabledAds();

				if (! mIsDisableAds) {
					//Purchase completed but item not available
					//Check again later
					alert(getString(R.string.msg_iab_warning_wait));
				}

				applyPurchase();
				break;
			case BillingClientHelper.PurchaseStatusListener.ERROR_CANCELED:
				//Not an error
				//NOOP
				break;
			case BillingClientHelper.PurchaseStatusListener.ERROR_NETWORK:
				alert(getString(R.string.msg_iab_error_network));
				break;
			case BillingClientHelper.PurchaseStatusListener.ERROR_BUSY:
				alert(getString(R.string.msg_iab_error_busy));
				break;
			case BillingClientHelper.PurchaseStatusListener.ERROR_NOT_SUPPORTED:
				//API not supported or item unavailable
				//Update the app and try again
				alert(getString(R.string.msg_iab_error_unsupported));
				break;
			default:
				//Fatal error.
				//Can't be done. Contact developer
				alert(getString(R.string.msg_iab_error_fatal_refund));
				break;
		}
	};

	public void applyPurchase() {
		if (mIsDisableAds) {
			showIabMenu(false);
		}
		synchronized(this) {
			if (mSpellListFragment != null) {
				//Update the IsDisableAds parameter
				mSpellListFragment.setDisabledAds(mIsDisableAds);
			} //Otherwise it will be set when created
		}
	}

	private void enableIabMenu(boolean enabled) {
		NavigationView navigationView = findViewById(R.id.nav_view);
		Menu menu = navigationView.getMenu();
		MenuItem item = menu.findItem(R.id.mnuDrawerPurchase);
		item.setEnabled(enabled);
		Menu subMenu = item.getSubMenu();
		for (int i=0; i < subMenu.size(); i++) {
			subMenu.getItem(i).setEnabled(enabled);
		}
	}

	private void showIabMenu(boolean shown) {
		NavigationView navigationView = findViewById(R.id.nav_view);
		Menu menu = navigationView.getMenu();
		menu.findItem(R.id.mnuDrawerPurchase).setVisible(shown);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton(R.string.lbl_ok, null);
		bld.create().show();
	}

	//endregion

}
