package net.ohmnibus.grimorium.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ohmnibus on 10/09/2016.
 */
public class Source {

	@SerializedName("id")
	long mId;
	@SerializedName("name")
	String mName;
	@SerializedName("nameSpace")
	String mNameSpace;
	@SerializedName("format")
	int mFormat;
	@SerializedName("version")
	int mVersion;
	@SerializedName("url")
	String mUrl;
	@SerializedName("description")
	String mDesc;

	public boolean isValidId() {
		return mId > 0;
	}

	public long getId() {
		return mId;
	}

	public void setId(long mId) {
		this.mId = mId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getNameSpace() {
		return mNameSpace;
	}

	public void setNameSpace(String mNameSpace) {
		this.mNameSpace = mNameSpace;
	}

	public int getVersion() {
		return mVersion;
	}

	public void setVersion(int mVersion) {
		this.mVersion = mVersion;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String desc) {
		this.mDesc = desc;
	}
}
