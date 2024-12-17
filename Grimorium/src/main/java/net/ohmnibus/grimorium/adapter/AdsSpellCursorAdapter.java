package net.ohmnibus.grimorium.adapter;

import androidx.fragment.app.Fragment;

/**
 * Created by Ohmnibus on 01/06/2017.
 */

public class AdsSpellCursorAdapter extends SpellCursorAdapter {

	private static final String TAG = "AdsSpellCursorAdapter";

	public AdsSpellCursorAdapter(Fragment fragment, long profileId, int resourceId, int startSpam, int stepSpam) {
		super(fragment, profileId, resourceId);
	}

	public AdsSpellCursorAdapter(Fragment fragment, long profileId, int resourceId) {
		this(fragment, profileId, resourceId, 3, 20);
	}
}
