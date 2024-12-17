package net.ohmnibus.grimorium.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import net.ohmnibus.grimorium.helper.StaticBitSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Ohmnibus on 28/08/2016.
 */
public class Profile implements Parcelable {

	private static final String TAG = "Profile";

	public static final String PROFILE_KEY_DEFAULT = "DefaultProfile";

	long mId;
	String mKey;
	String mName;
	boolean mSys;
	SpellFilter mFilter;

	public Profile() { }

	public Profile(String name) {
		this(name, new SpellFilter());
	}

	public Profile(String name, SpellFilter filter) {
		mName = name;
		mFilter = filter;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getKey() {
		return mKey;
	}

	public void setKey(String key) {
		mKey = key;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	/**
	 * Tell if this profile is a system profile.<br>
	 * Sys profiles cannot be deleted
	 * @return {@code true} if is a system profile.
	 */
	public boolean isSys() {
		return mSys;
	}

	/**
	 * Set or reset the system attribute.<br>
	 * Sys profiles cannot be deleted
	 * @param sys {@code true} to set this profile as a system one.
	 */
	public void setSys(boolean sys) {
		this.mSys = sys;
	}

	public SpellFilter getSpellFilter() {
		return mFilter;
	}

	public void setSpellFilter(SpellFilter spellFilter) {
		mFilter = spellFilter;
	}

	//region Parcelable methods

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeLong(mId);
		parcel.writeString(mKey);
		parcel.writeString(mName);
		parcel.writeInt(mSys ? 1 : 0);
		parcel.writeParcelable(mFilter, i);
		//parcel.writeIntArray(starred.toArray());
	}

	protected Profile(Parcel in) {
		mId = in.readLong();
		mKey = in.readString();
		mName = in.readString();
		mSys = in.readInt() != 0;
		mFilter = in.readParcelable(SpellFilter.class.getClassLoader());
		//starred.set(in.createIntArray());
	}

	public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
		public Profile createFromParcel(Parcel in) {
			return new Profile(in);
		}

		public Profile[] newArray(int size) {
			return new Profile[size];
		}
	};

	//endregion

	//region Serialization

	@Deprecated
	public String serialize() {
		return serialize(this);
	}

	private static final String FIELD_ID = "id";
	private static final String FIELD_KEY = "key";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_SYS = "sys";
	private static final String FIELD_FILTER = "filter";
	@Deprecated
	private static final String FIELD_STARRED = "starred";

	@Deprecated
	public static String serialize(Profile profile) {
		if (profile == null)
			return null;

		String retVal = null;

		final JSONObject obj = new JSONObject();
		try {
			obj.put(FIELD_ID, profile.mId);
			obj.put(FIELD_KEY, profile.mKey);
			obj.put(FIELD_NAME, profile.mName);
			obj.put(FIELD_SYS, profile.mSys);
			obj.put(FIELD_FILTER, profile.mFilter.serialize());
			//obj.put(FIELD_STARRED, new JSONArray(Arrays.asList(profile.starred.toArray())));

			retVal = obj.toString();
		} catch (JSONException e) {
			Log.e(TAG, "serialize()", e);
			retVal = null;
		}

		return retVal;
	}

	@Deprecated
	public static Profile deserialize(String profile) {
		if (profile == null)
			return null;

		Profile retVal = null;

		try {
			final JSONObject obj = new JSONObject(profile);
			retVal = new Profile();
			retVal.mId = obj.getLong(FIELD_ID);
			retVal.mKey = obj.getString(FIELD_KEY);
			retVal.mName = obj.getString(FIELD_NAME);
			retVal.mSys = obj.getBoolean(FIELD_SYS);
			retVal.mFilter = SpellFilter.deserialize(obj.getString(FIELD_FILTER));
			JSONArray stars = obj.getJSONArray(FIELD_STARRED);
//			retVal.starred.clear();
//			for (int i = 0; i < stars.length(); i++) {
//				retVal.starred.set(stars.getInt(i));
//			}
		} catch (JSONException e) {
			Log.e(TAG, "deserialize()", e);
			retVal = null;
		}

		return retVal;
	}

	//endregion
}
