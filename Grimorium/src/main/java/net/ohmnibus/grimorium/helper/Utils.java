package net.ohmnibus.grimorium.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.danielstone.materialaboutlibrary.util.OpenSourceLicense;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.aboutbox.AboutConfig;
import net.ohmnibus.grimorium.aboutbox.activity.AboutActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	private static final String TAG = "Utils";

	public static int getToolbarHeight(Context context) {
		final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
				new int[]{R.attr.actionBarSize});
		int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
		styledAttributes.recycle();

		return toolbarHeight;
	}

//	public static int getTabsHeight(Context context) {
//		return (int) context.getResources().getDimension(R.dimen.tabsHeight);
//	}

	public static String serializeStringList(List<String> values) {
		String retVal = null;
		try {
			final JSONArray a = new JSONArray();

			for (int i = 0; i < values.size(); i++) {
				a.put(values.get(i));
			}

			if (!values.isEmpty()) {
				retVal = a.toString();
			}
		} catch (RuntimeException e) {
			Log.e(TAG, "serializeStringList()", e);
		}
		return retVal;
	}

	public static List<String> deserializeStringList(String serializedValues, List<String> defaultValues) {
		List<String> retVal = null;

		if (serializedValues != null) {
			try {
				final JSONArray a = new JSONArray(serializedValues);
				if (a.length() > 0) {
					//values = new HashSet<String>();
					retVal = new ArrayList<>(a.length());
				}
				for (int i = 0; i < a.length(); i++) {
					final String value = a.optString(i);

					if (value != null) {
						retVal.add(value);
					}
				}
			} catch (JSONException e) {
				Log.e(TAG, "deserializeStringList", e);
				retVal = null;
			}
		}

		return (retVal != null) ? retVal : defaultValues;
	}

	public static void initWebView(Context context, WebView webView) {
		WebSettings webSettings = webView.getSettings();

		float size = context.getResources().getDimension(R.dimen.detail_text_size);

		webSettings.setDefaultFontSize((int) pixelToDp(context, (int)size));
		//webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//webSettings.setAppCacheEnabled(false);
		webSettings.setBlockNetworkImage(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setGeolocationEnabled(false);
		webSettings.setNeedInitialFocus(false);
		webSettings.setSaveFormData(false);

		webView.setBackgroundColor(0x00000000);
	}

	public static float pixelToDp(Context context, int pixel){
		float densityDpi = context.getResources().getDisplayMetrics().densityDpi;
		float dp = pixel / (densityDpi / DisplayMetrics.DENSITY_DEFAULT);
		return dp;
	}

	public static String join(long[] array, String separator) {
		return join(array, separator, 10);
	}

	public static String join(long[] array, String separator, int radix) {
		if (array == null)
			return null;
//		if (array.length == 0)
//			return "";
		String retVal = "";
		String sep = "";
		for (long item : array) {
			retVal += sep + Long.toString(item, radix);
			sep = separator;
		}
		return retVal;
	}

	public static void showAboutBox(AppCompatActivity activity) {
		AboutConfig aboutConfig = AboutConfig.getInstance();

		aboutConfig.appName = activity.getString(R.string.app_name);
		aboutConfig.appIcon = R.drawable.ic_launcher;
		try {
			PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			aboutConfig.version = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			aboutConfig.version = "";
			e.printStackTrace();
		}
		aboutConfig.aboutLabelTitle = activity.getString(R.string.about_title, activity.getString(R.string.app_author));
		aboutConfig.packageName = activity.getApplicationContext().getPackageName();
		aboutConfig.buildType = true /* google */ ? AboutConfig.BuildType.GOOGLE : AboutConfig.BuildType.AMAZON;

		aboutConfig.facebookUserName = activity.getString(R.string.app_facebook);
		aboutConfig.twitterUserName = activity.getString(R.string.app_twitter);
		aboutConfig.webHomePage = activity.getString(R.string.app_website);

		// app publisher for "Try Other Apps" item
		aboutConfig.appPublisher = activity.getString(R.string.app_author);

		aboutConfig.shareMessage = activity.getString(R.string.app_share,
				activity.getString(R.string.app_name),
				aboutConfig.packageName);
		aboutConfig.sharingTitle = activity.getString(R.string.app_name);
		// if pages are stored locally, then you need to override aboutConfig.dialog to be able use custom WebView
		aboutConfig.companyHtmlPath = activity.getString(R.string.company_website);
		aboutConfig.privacyHtmlPath = activity.getString(R.string.app_privacy);
		aboutConfig.acknowledgmentHtmlPath = activity.getString(R.string.app_acknowledgment);

//		aboutConfig.dialog = new IDialog() {
//			@Override
//			public void open(AppCompatActivity appCompatActivity, String url, String tag) {
//				// handle custom implementations of WebView. It will be called when user click to web items. (Example: "Privacy", "Acknowledgments" and "About")
//			}
//		};

//		aboutConfig.analytics = new IAnalytic() {
//			@Override
//			public void logUiEvent(String s, String s1) {
//				// handle log events.
//			}
//
//			@Override
//			public void logException(Exception e, boolean b) {
//				// handle exception events.
//			}
//		};
//		// set it only if aboutConfig.analytics is defined.
//		aboutConfig.logUiEventName = "Log";

		// Contact Support email details
		aboutConfig.emailAddress = activity.getString(R.string.app_email);
		aboutConfig.emailSubject = activity.getString(R.string.app_email_subject);
		//aboutConfig.emailBody = EMAIL_BODY;

		//Licenses
		aboutConfig.addLicense("material-about-library", "2017", "Daniel Stone", OpenSourceLicense.APACHE_2);
		aboutConfig.addLicense("android-about-box", "2017", "Egghead GAMES", OpenSourceLicense.MIT);
		aboutConfig.addLicense("google-gson", "2008", "Google Inc", OpenSourceLicense.APACHE_2);
		aboutConfig.addLicense("Android Support Library", "2017", "Google Inc", OpenSourceLicense.APACHE_2);

		AboutActivity.launch(activity);
	}
}