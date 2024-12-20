package net.ohmnibus.grimorium.entity.ads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ohmnibus on 11/08/2016.
 */
public class MobileAdsWrapper {

	/**
	 * Banner with fixed size
	 */
	public static final int ADVIEW_TYPE_FIXED = 0;
	/**
	 * Banner fill available horizontal space and compute vertical accordingly
	 */
	public static final int ADVIEW_TYPE_AUTO = 1;

	public static void initialize(Context context) {
		//NOOP
	}

	/**
	 * Creates and set an AD view as the only child of the provided ViewGroup
	 * @param container ViewGroup to fill with banner.
	 * @param viewType Aspect of the banner.
	 */
	public static void setAdView(ViewGroup container, int viewType) {
		container.setVisibility(View.GONE);
	}

	/**
	 * Creates and set an AD view as the only child of the provided ViewGroup
	 * @param container ViewGroup to fill with banner.
	 * @param viewType Aspect of the banner.
	 * @param autoLoad {@code true} to load automatically the AD banner when view is shown.
	 */
	public static void setAdView(ViewGroup container, int viewType, boolean autoLoad) {
		container.setVisibility(View.GONE);
	}

	public static void loadAd(View adView) {
		//NOOP
	}

}
