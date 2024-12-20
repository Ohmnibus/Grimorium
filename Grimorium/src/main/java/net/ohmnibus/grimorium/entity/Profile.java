package net.ohmnibus.grimorium.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ohmnibus on 28/08/2016.
 */
public class Profile implements Parcelable {

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
	}

	protected Profile(Parcel in) {
		mId = in.readLong();
		mKey = in.readString();
		mName = in.readString();
		mSys = in.readInt() != 0;
		mFilter = in.readParcelable(SpellFilter.class.getClassLoader());
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

}
