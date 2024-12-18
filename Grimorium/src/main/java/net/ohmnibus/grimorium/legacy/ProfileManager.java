package net.ohmnibus.grimorium.legacy;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import net.ohmnibus.grimorium.entity.Profile;
import net.ohmnibus.grimorium.entity.Source;
import net.ohmnibus.grimorium.entity.SpellFilter;
import net.ohmnibus.grimorium.helper.StaticBitSet;
import net.ohmnibus.grimorium.helper.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ohmnibus on 18/04/2017.
 */

public class ProfileManager {

	private static final String TAG = "ProfileManager";

	public static String PROFILE_PREF_NAME = "Profiles";

	private static final String KEY_PROFILES_ID_LIST = "KEY_PROFILES_ID_LIST";

	public static @NonNull Profile[] getLegacyProfiles(Context context) {
		SharedPreferences mPreferences = context
				.getApplicationContext()
				.getSharedPreferences(PROFILE_PREF_NAME, Context.MODE_PRIVATE);

		List<String> mIdProfiles = Utils.deserializeStringList(
				mPreferences.getString(KEY_PROFILES_ID_LIST, null),
				new ArrayList<>()
		);
		ArrayList<Profile> mProfiles = new ArrayList<>();
		for (String profileId : mIdProfiles) {
			String rawProfile = mPreferences.getString(profileId, null);
			Profile p = deserializeProfile(rawProfile);
			if (p != null)
				mProfiles.add(p);
		}

		return mProfiles.toArray(new Profile[0]);
	}

	private static final String[] LEGACY_SOURCES = new String[] {
			"official.wizards.all",
			"official.priests.all"
	};

	public static boolean isLegacySource(@SuppressWarnings("unused") Context context, Source source) {
		boolean isLegacySource = false;

		for (String src : LEGACY_SOURCES) {
			if (src.equalsIgnoreCase(source.getNameSpace())) {
				isLegacySource = true;
				break;
			}
		}

		return isLegacySource;
	}

	public static StaticBitSet getLegacyStars(Context context, Profile profile) {
		StaticBitSet retVal;
//		boolean isLegacySource = false;
//		for (String src : LEGACY_SOURCES) {
//			if (src.equalsIgnoreCase(source.getNameSpace())) {
//				isLegacySource = true;
//				break;
//			}
//		}
//
//		if (! isLegacySource)
//			return null;

		SharedPreferences mPreferences = context
				.getApplicationContext()
				.getSharedPreferences(PROFILE_PREF_NAME, Context.MODE_PRIVATE);

		String rawProfile = mPreferences.getString(profile.getKey(), null);

		retVal = deserializeStarred(rawProfile);

		if (retVal == null)
			retVal = new StaticBitSet();

		return retVal;
	}

	private static final String FIELD_KEY = "id";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_FILTER = "filter";

	private static Profile deserializeProfile(String profile) {

		if (TextUtils.isEmpty(profile))
			return null;

		Profile retVal;

		try {
			final JSONObject obj = new JSONObject(profile);
			retVal = new Profile();
			retVal.setKey(obj.getString(FIELD_KEY));
			retVal.setName(obj.getString(FIELD_NAME));
			retVal.setSys(Profile.PROFILE_KEY_DEFAULT.equals(retVal.getKey()));
			retVal.setSpellFilter(deserializeFilter(obj.getString(FIELD_FILTER)));
		} catch (Exception e) {
			Log.e(TAG, "deserializeProfile: " + profile, e);
			retVal = null;
		}

		return retVal;
	}

	private static SpellFilter deserializeFilter(String filter) {

		if (TextUtils.isEmpty(filter))
			return null;

		String[] fields = filter.split("\\|");

		SpellFilter retVal = new SpellFilter();

		retVal.setTypes(Integer.parseInt(fields[0], 16));
		retVal.setLevels((int)Long.parseLong(fields[1], 16));
		retVal.setBooks(Integer.parseInt(fields[2], 16));
		retVal.setSchools(Integer.parseInt(fields[3], 16));
		retVal.setSpheres(Integer.parseInt(fields[4], 16));
		retVal.setComponents(Integer.parseInt(fields[5], 16));
		if (fields.length > 7) {
			retVal.setStarredOnly(Integer.parseInt(fields[7], 16) == 1); //1 = SPELL_STARRED_ONLY
		}

		return retVal;
	}

	private static StaticBitSet deserializeStarred(String profile) {

		if (TextUtils.isEmpty(profile))
			return null;

		StaticBitSet retVal;

		try {
			final JSONObject obj = new JSONObject(profile);
			retVal = new StaticBitSet();

			String spellFilter = obj.getString(FIELD_FILTER);

			String[] fields = spellFilter.split("\\|");

			if (fields.length > 6 && fields[6].length() > 0) {
				fields = fields[6].split(",");
				for (String field : fields) {
					retVal.set(Integer.parseInt(field, 16));
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "deserializeStarred: " + profile, e);
			retVal = null;
		}

		return retVal;
	}
}
