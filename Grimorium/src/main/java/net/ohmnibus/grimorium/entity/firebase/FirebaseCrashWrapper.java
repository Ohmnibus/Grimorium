package net.ohmnibus.grimorium.entity.firebase;

import android.util.Log;

/**
 * Created by Ohmnibus on 05/11/2017.
 */

public class FirebaseCrashWrapper {

	private static final String TAG = "FirebaseCrashWrapper";

	public static void log(String message) {
		Log.i(TAG, message);
	}

	public static void report(Throwable throwable) {
		Log.w(TAG, throwable);
	}
}
