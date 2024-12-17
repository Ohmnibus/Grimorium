package net.ohmnibus.grimorium.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.entity.Profile;
import net.ohmnibus.grimorium.entity.SpellFilter;
import net.ohmnibus.grimorium.database.GrimoriumContract.ProfileTable;
import net.ohmnibus.grimorium.helper.Utils;

/**
 * Created by Ohmnibus on 16/09/2016.
 */
public class ProfileDbAdapter extends BaseDbAdapter {

	private static final String TAG = "ProfileDbAdapter";

	//public static final String PROFILE_ID_DEFAULT = "DefaultProfile";
	private static final String PROFILE_ID_FMT = "PRF_%s";

	private static final String WHERE_AND = "AND ";
	private static final String WHERE_OR = "OR ";
	private static final String SORT_DEFAULT =
			ProfileTable.COLUMN_NAME_NAME + " Asc ";

	private static final ProfileDbAdapter mInstance = new ProfileDbAdapter();

	public static ProfileDbAdapter getInstance() {
		return mInstance;
	}

	protected ProfileDbAdapter() {
		super();
	}

	public Cursor getCursor() {
		Cursor c = getDb().query(
				ProfileTable.TABLE_NAME,
				null,
				null,
				null,
				null,
				null,
				SORT_DEFAULT
		);
		return c;
	}

	public Profile get(long id) {
		Profile retVal;

		Cursor c = getDb().query(
				ProfileTable.TABLE_NAME,
				null,
				ProfileTable._ID + " = ?",
				new String[] { Long.toString(id) },
				null,
				null,
				null);

		if (c.moveToFirst()) {
			retVal = get(c);
		} else {
			retVal = null;
		}

		c.close();

		return retVal;
	}

	public Profile get(String key) {
		Profile retVal;

		Cursor c = getDb().query(
				ProfileTable.TABLE_NAME,
				null,
				ProfileTable.COLUMN_NAME_KEY + " = ?",
				new String[] { key },
				null,
				null,
				null);

		if (c.moveToFirst()) {
			retVal = get(c);
		} else {
			retVal = null;
		}

		c.close();

		if (retVal == null && Profile.PROFILE_KEY_DEFAULT.equals(key)) {
			retVal = initDefaultProfile();
		}

		return retVal;
	}

	public Profile get(Cursor c) {
		if (c.isClosed() || c.isBeforeFirst() || c.isAfterLast())
			return null;

		Profile retVal = new Profile();

		retVal.setId(getRowId(c));
		retVal.setKey(getString(c, ProfileTable.COLUMN_NAME_KEY));
		retVal.setName(getString(c, ProfileTable.COLUMN_NAME_NAME));
		retVal.setSys(getBoolean(c, ProfileTable.COLUMN_NAME_SYS));

		SpellFilter filter = new SpellFilter();

		filter.setTypes(getInt(c, ProfileTable.COLUMN_NAME_TYPES));
		filter.setLevels(getInt(c, ProfileTable.COLUMN_NAME_LEVELS));
		filter.setBooks(getInt(c, ProfileTable.COLUMN_NAME_BOOKS));
		filter.setSchools(getInt(c, ProfileTable.COLUMN_NAME_SCHOOLS));
		filter.setSpheres(getInt(c, ProfileTable.COLUMN_NAME_SPHERES));
		filter.setComponents(getInt(c, ProfileTable.COLUMN_NAME_COMPONENTS));
		filter.setStarredOnly(getBoolean(c, ProfileTable.COLUMN_NAME_STARRED_ONLY));

		//region Filtered Sources
		String rawFilteredSources = getString(c, ProfileTable.COLUMN_NAME_FILTERED_SOURCES);
		if (! TextUtils.isEmpty(rawFilteredSources)) {
			String[] hexFilteredSources = rawFilteredSources.split(",");
			long[] filteredSources = new long[hexFilteredSources.length];
			for (int i = 0; i < hexFilteredSources.length; i++) {
				try {
					filteredSources[i] = Long.parseLong(hexFilteredSources[i], 16);
				} catch (Exception ex) {
					filteredSources[i] = -1;
					Log.w(TAG, "get: Ignored " + hexFilteredSources[i]);
				}
			}

			filter.setFilteredSources(filteredSources);
		} else {
			filter.setFilteredSources(null);
		}
		//endregion

		retVal.setSpellFilter(filter);

		return retVal;
	}

	public long insert(Profile profile) {
		return insert(profile, false);
	}

	public long insert(Profile profile, boolean inTransaction) {
		long retVal;

		int count = 0;
		String key = getIdFromName(profile.getName());
		profile.setKey(String.format(PROFILE_ID_FMT, key));
		while (get(profile.getKey()) != null) {
			profile.setKey(String.format(PROFILE_ID_FMT, key + (++count)));
		}

		retVal = innerInsert(profile, inTransaction);

		return retVal;
	}

	public int updateHeader(Profile profile) {
		return updateHeader(profile, false);
	}

	public int updateHeader(Profile profile, boolean inTransaction) {
		return updateHeader(profile.getId(), profile, inTransaction);
	}

	public int updateHeader(long profileId, Profile profile) {
		return updateHeader(profileId, profile, false);
	}

	public int updateHeader(long profileId, Profile profile, boolean inTransaction) {

		String whereClause = ProfileTable._ID + " = ?";

		String[] whereArgs = { Long.toString(profileId) };

		return updateHeader(profile, whereClause, whereArgs, inTransaction);
	}

	public int update(Profile profile) {
		return update(profile, false);
	}

	public int update(Profile profile, boolean inTransaction) {
		return update(profile.getId(), profile, inTransaction);
	}

	public int update(long profileId, Profile profile) {
		return update(profileId, profile, false);
	}

	public int update(long profileId, Profile profile, boolean inTransaction) {

		String whereClause = ProfileTable._ID + " = ?";

		String[] whereArgs = { Long.toString(profileId) };

		return update(profile, whereClause, whereArgs, inTransaction);
	}

	public int update(String key, Profile profile) {
		return update(key, profile, false);
	}

	public int update(String key, Profile profile, boolean inTransaction) {

		String whereClause = ProfileTable.COLUMN_NAME_KEY + " = ?";

		String[] whereArgs = { key };

		return update(profile, whereClause, whereArgs, inTransaction);
	}

	public int delete(long profileId) {
		return delete(profileId, false);
	}

	public int delete(long profileId, boolean inTransaction) {
		int retVal;

		if (! inTransaction) getDb().beginTransaction();

		try {
			String whereClause;

			String[] whereArgs;

			retVal = 0;

			whereClause = ProfileTable._ID + " = ?";

			whereArgs = new String[] { Long.toString(profileId) };

			retVal += getDb().delete(
					ProfileTable.TABLE_NAME,
					whereClause,
					whereArgs);

			whereClause = GrimoriumContract.StarTable.COLUMN_NAME_PROFILE_ID + " = ?";

			whereArgs = new String[] { Long.toString(profileId) };

			retVal += getDb().delete(
					GrimoriumContract.StarTable.TABLE_NAME,
					whereClause,
					whereArgs);

			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "delete", ex);
			retVal = -1;
			handleQueryException(ex);
		} finally {
			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	/**
	 * Used to initialize the database.
	 * @param context Context
	 * @return {@link ContentValues} collection defining the default profile.
	 */
	public static ContentValues getDefaultProfileCV(Context context) {
		return getProfileCV(context, getDefaultProfileInstance(context));
	}

	/**
	 * Used to initialize the database vith legacy data.
	 * @param context Context
	 * @param profile Profile to convert to {@link ContentValues}
	 * @return {@link ContentValues} collection defining the profile.
	 */
	public static ContentValues getProfileCV(Context context, Profile profile) {
		return getContentValues(profile);
	}

	//region Private/protected methods

	private static Profile getDefaultProfileInstance(Context context) {
		Profile profile = new Profile();

		profile.setKey(Profile.PROFILE_KEY_DEFAULT);
		profile.setName(context.getString(R.string.lbl_profile_default));
		profile.setSys(true);
		profile.setSpellFilter(new SpellFilter());
		profile.getSpellFilter().reset();

		return profile;
	}

	private Profile initDefaultProfile() {
		Profile profile = getDefaultProfileInstance(getContext());
		innerInsert(profile, false);
		return profile;
	}

	private long innerInsert(Profile profile, boolean inTransaction) {
		long retVal;

//		if (! inTransaction) getDb().beginTransaction();

		try {
			ContentValues values = getContentValues(profile);

			retVal = getDb().insert(
					ProfileTable.TABLE_NAME,
					null,
					values);

			profile.setId(retVal);

//			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "innerInsert", ex);
			retVal = -1;
			handleQueryException(ex);
//		} finally {
//			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	protected int updateHeader(Profile profile, String whereClause, String[] whereArgs, boolean inTransaction) {

		ContentValues values = getHeaderContentValues(profile);

		return update(values, whereClause, whereArgs, inTransaction);
	}

	protected int update(Profile profile, String whereClause, String[] whereArgs, boolean inTransaction) {

		ContentValues values = getContentValues(profile);

		return update(values, whereClause, whereArgs, inTransaction);
	}

	protected int update(ContentValues values, String whereClause, String[] whereArgs, boolean inTransaction) {
		int retVal;

//		if (! inTransaction) getDb().beginTransaction();

		try {
			retVal = getDb().update(
					ProfileTable.TABLE_NAME,
					values,
					whereClause,
					whereArgs);

//			if (! inTransaction) getDb().setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(TAG, "update", ex);
			retVal = -1;
			handleQueryException(ex);
//		} finally {
//			if (! inTransaction) getDb().endTransaction();
		}

		return retVal;
	}

	private String getIdFromName(String name) {
		StringBuilder retVal = new StringBuilder(name.length());
		for (int i=0; i < name.length(); i++) {
			char c = name.charAt(i);
			if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
				retVal.append(Character.toUpperCase(c));
			} else {
				retVal.append('_');
			}
		}
		return retVal.toString();
	}

	protected static ContentValues getHeaderContentValues(Profile profile) {
		ContentValues retVal = new ContentValues();

		retVal.put(ProfileTable.COLUMN_NAME_KEY, profile.getKey());
		retVal.put(ProfileTable.COLUMN_NAME_NAME, profile.getName());
		retVal.put(ProfileTable.COLUMN_NAME_SYS, profile.isSys() ? 1 : 0);

		return retVal;
	}

	protected static ContentValues getContentValues(Profile profile) {

		ContentValues retVal = getHeaderContentValues(profile);

		SpellFilter filter = profile.getSpellFilter();

		retVal.put(ProfileTable.COLUMN_NAME_TYPES, filter.getTypes());
		retVal.put(ProfileTable.COLUMN_NAME_LEVELS, filter.getLevels());
		retVal.put(ProfileTable.COLUMN_NAME_BOOKS, filter.getBooks());
		retVal.put(ProfileTable.COLUMN_NAME_SCHOOLS, filter.getSchools());
		retVal.put(ProfileTable.COLUMN_NAME_SPHERES, filter.getSpheres());
		retVal.put(ProfileTable.COLUMN_NAME_COMPONENTS, filter.getComponents());
		retVal.put(ProfileTable.COLUMN_NAME_STARRED_ONLY, filter.isStarredOnly() ? 1 : 0);

		//region Filtered Sources

		String rawFilteredSources = Utils.join(filter.getFilteredSources(), ",", 16);
		retVal.put(ProfileTable.COLUMN_NAME_FILTERED_SOURCES, rawFilteredSources);

		//endregion

		return retVal;
	}

	//endregion
}
