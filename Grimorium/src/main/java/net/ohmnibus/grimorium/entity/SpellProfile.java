package net.ohmnibus.grimorium.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ohmnibus on 16/09/2016.
 */
public class SpellProfile extends Spell {

	long mProfileId;
	boolean mStarred;

	public SpellProfile() {
		super();
	}

	public long getProfileId() {
		return mProfileId;
	}

	public void setProfileId(long profileId) {
		this.mProfileId = profileId;
	}

	public boolean isStarred() {
		return mStarred;
	}

	public void setStarred(boolean starred) {
		this.mStarred = starred;
	}

	//region Parcelable

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);

		dest.writeLong(mProfileId);
		dest.writeInt(mStarred ? 1 : 0);
	}

	private SpellProfile(Parcel in) {
		super(in);

		mProfileId = in.readLong();
		mStarred = in.readInt() != 0;
	}

	public static final Parcelable.Creator<SpellProfile> CREATOR = new Parcelable.Creator<SpellProfile>() {
		public SpellProfile createFromParcel(Parcel in) {
			return new SpellProfile(in);
		}

		public SpellProfile[] newArray(int size) {
			return new SpellProfile[size];
		}
	};

	//endregion
}
