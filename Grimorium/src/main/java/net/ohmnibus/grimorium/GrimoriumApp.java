package net.ohmnibus.grimorium;

import net.ohmnibus.grimorium.database.DbManager;
import net.ohmnibus.grimorium.entity.Profile;
import net.ohmnibus.grimorium.entity.ads.MobileAdsWrapper;
import net.ohmnibus.grimorium.entity.firebase.FirebaseAppWrapper;
import net.ohmnibus.grimorium.entity.firebase.FirebaseCrashWrapper;
import net.ohmnibus.grimorium.helper.SourceCache;
import net.ohmnibus.grimorium.helper.iab.BillingClientHelper;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

public class GrimoriumApp extends Application
	implements DbManager.OnDbInitializedListener {

	private static final String TAG = "GrimoriumApp";

	private static final String KEY_CURRENT_PROFILE_ID = "CurrentProfile";

	private static GrimoriumApp mInstance = null;

	private DbManager mDbManager;
	private SharedPreferences mPref;
	private String mCurrentProfileKey = null;
	private final ArrayList<DbManager.OnDbInitializedListener> mOnDbInitializedListeners = new ArrayList<>();
	private final Object mSyncToken = new Object();

	@Override
	public void onCreate() {
		super.onCreate();

		FirebaseAppWrapper.initialize(this);
		MobileAdsWrapper.initialize(this);
		DbManager.init(this, this);
		BillingClientHelper.initialize(this);

		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		mCurrentProfileKey = mPref.getString(KEY_CURRENT_PROFILE_ID, Profile.PROFILE_KEY_DEFAULT);
		mInstance = this;
	}
	
	public static GrimoriumApp getInstance() {
		return mInstance;
	}

	public void getPurchasesAsync(BillingClientHelper.PurchaseStatusListener listener) {
		BillingClientHelper.getPurchasesAsync(listener);
	}

	public void launchDisableAdsPurchaseFlow(@NonNull Activity activity, @NonNull BillingClientHelper.PurchaseStatusListener listener) {
		BillingClientHelper.launchDisableAdsPurchaseFlow(activity, listener);
	}

	@Override
	public void onDbInitialized(DbManager dbManager) {
		Log.d(TAG, "onDbInitialized: Begin");
		synchronized (mSyncToken) {
			mDbManager = dbManager;
			refreshSourceCache();
			synchronized (mOnDbInitializedListeners) {
				while (mOnDbInitializedListeners.size() > 0) {
					DbManager.OnDbInitializedListener listener = mOnDbInitializedListeners.remove(0);
					listener.onDbInitialized(dbManager);
					Log.d(TAG, "onDbInitialized: Sent notification.");
				}
			}
			mSyncToken.notifyAll();
		}
		Log.d(TAG, "onDbInitialized: End");
	}


	public DbManager getDbManager() {
		synchronized (mSyncToken) {
			if (!isDbManagerInitialized()) {
				//Wait initialization to complete
				try {
					Log.i(TAG, "getDbManager: Waiting for initialization.");
					mSyncToken.wait();
				} catch (InterruptedException e) {
					//e.printStackTrace();
					FirebaseCrashWrapper.log("Exception waiting for DBManager to initialize.");
					FirebaseCrashWrapper.report(e);
				}
			}
		}
		return mDbManager;
	}

	public boolean isDbManagerInitialized() {
		return mDbManager != null; // && mDbManager.isInitialized();
	}

	public void initDbManager(final DbManager.OnDbInitializedListener listener) {

		if (listener == null)
			return;

		synchronized (mOnDbInitializedListeners) {
			if (!isDbManagerInitialized()) {
				//Initialization isn't completed.
				//Add listener to queue
				Log.i(TAG, "initDbManager: Enqueued listener.");
				mOnDbInitializedListeners.add(listener);
			} else {
				//Initialization completed.
				//Notify immediately.
				Log.i(TAG, "initDbManager: Direct call to listener.");
				listener.onDbInitialized(mDbManager);
			}
		}
	}

	private SourceCache mSourceCache = new SourceCache();

	public void refreshSourceCache() {
		Cursor c = mDbManager.getSourceDbAdapter().getCursor();
		if (c == null) {
			mSourceCache.clear();
		} else {
			mSourceCache.refresh(c);
			c.close();
		}
	}

	public int getSourceCount() {
		//return mSourceCount;
		return mSourceCache.size();
	}

	public SourceCache getSourceCache() {
		return mSourceCache;
	}

	public String getCurrentProfileKey() {
		return mCurrentProfileKey;
	}

	public void setCurrentProfileKey(String key) {
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString(KEY_CURRENT_PROFILE_ID, key);
		editor.apply();
		mCurrentProfileKey = key;
	}

}
