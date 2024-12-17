package net.ohmnibus.grimorium.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import net.ohmnibus.grimorium.R;


public class Spell implements Parcelable, Comparable<Spell> {

	@IntDef({TYPE_ALL, TYPE_WIZARDS, TYPE_CLERICS})
	@Retention(RetentionPolicy.SOURCE)
	public @interface SpellType{}

	private static final int BITFIELD_UNDEFINED = 0xffffffff;
	private static final int BITFIELD_ALL = 0x0fffffff;
	private static final int BITFIELD_NONE = 0x00000000;

	public static final int TYPE_ALL = BITFIELD_ALL;
	public static final int TYPE_WIZARDS = 0x00000001;
	public static final int TYPE_CLERICS = 0x00000002;

	public static final int LEVEL_ALL = BITFIELD_ALL;
	public static final int LEVEL_CANTRIP = 0;
//	public static final int LEVEL_01 = 1;
//	public static final int LEVEL_02 = 2;
//	public static final int LEVEL_03 = 3;
//	public static final int LEVEL_04 = 4;
//	public static final int LEVEL_05 = 5;
//	public static final int LEVEL_06 = 6;
//	public static final int LEVEL_07 = 7;
//	public static final int LEVEL_08 = 8;
//	public static final int LEVEL_09 = 9;
//	public static final int LEVEL_10 = 10;
	public static final int LEVEL_QUEST = 11;
	
	public static final int SCHOOL_NONE = BITFIELD_NONE;
	public static final int SCHOOL_ABJURATION = 0x00000001;
	public static final int SCHOOL_ALTERATION = 0x00000002;
	public static final int SCHOOL_CONJURATION = 0x00000004;
	public static final int SCHOOL_DIVINATION = 0x00000008;
	public static final int SCHOOL_ENCHANTMENT = 0x00000010;
	public static final int SCHOOL_EVOCATION = 0x00000020;
	public static final int SCHOOL_ILLUSION = 0x00000040;
	public static final int SCHOOL_NECROMANCY = 0x00000080;
	public static final int SCHOOL_AIR = 0x00000100;
	public static final int SCHOOL_EARTH = 0x00000200;
	public static final int SCHOOL_FIRE = 0x00000400;
	public static final int SCHOOL_WATER = 0x00000800;
	public static final int SCHOOL_DIMENSIONAL = 0x00001000;
	public static final int SCHOOL_FORCE = 0x00002000;
	public static final int SCHOOL_SHADOW = 0x00004000;
	public static final int SCHOOL_ALCHEMY = 0x00008000;
	public static final int SCHOOL_ARTIFICE = 0x00010000;
	public static final int SCHOOL_GEOMETRY = 0x00020000;
	public static final int SCHOOL_SONG = 0x00040000;
	public static final int SCHOOL_WILD = 0x00080000;
	public static final int SCHOOL_MENTALISM = 0x00100000;
	public static final int SCHOOL_ALL = BITFIELD_ALL;

	public static final int SPHERE_NONE = BITFIELD_NONE;
	public static final int SPHERE_ANIMAL = 0x00000001;
	public static final int SPHERE_ASTRAL = 0x00000002;
	public static final int SPHERE_CHARM = 0x00000004;
	public static final int SPHERE_COMBAT = 0x00000008;
	public static final int SPHERE_CREATION = 0x00000010;
	public static final int SPHERE_DIVINATION = 0x00000020;
	public static final int SPHERE_ELEMENTAL_AIR = 0x00000040;
	public static final int SPHERE_ELEMENTAL_EARTH = 0x00000080;
	public static final int SPHERE_ELEMENTAL_FIRE = 0x00000100;
	public static final int SPHERE_ELEMENTAL_WATER = 0x00000200;
	public static final int SPHERE_GUARDIAN = 0x00000400;
	public static final int SPHERE_HEALING = 0x00000800;
	public static final int SPHERE_NECROMANTIC = 0x00001000;
	public static final int SPHERE_PLANT = 0x00002000;
	public static final int SPHERE_PROTECTION = 0x00004000;
	public static final int SPHERE_SUMMONING = 0x00008000;
	public static final int SPHERE_SUN = 0x00010000;
	public static final int SPHERE_WEATHER = 0x00020000;
	public static final int SPHERE_CHAOS = 0x00040000;
	public static final int SPHERE_LAW = 0x00080000;
	public static final int SPHERE_NUMBERS = 0x00100000;
	public static final int SPHERE_THOUGHT = 0x00200000;
	public static final int SPHERE_TIME = 0x00400000;
	public static final int SPHERE_TRAVELERS = 0x00800000;
	public static final int SPHERE_WAR = 0x01000000;
	public static final int SPHERE_WARDS = 0x02000000;
	public static final int SPHERE_ALL = BITFIELD_ALL;

	public static final int BOOK_NONE = BITFIELD_NONE;
	public static final int BOOK_BASE = 0x00000001;
	public static final int BOOK_TOME = 0x00000002;
	public static final int BOOK_WIZARDS = 0x00000004;
	public static final int BOOK_PRIESTS = 0x00000008;
	public static final int BOOK_SPELLS = 0x00000010;
	public static final int BOOK_ADVENTURES = 0x00000020;
	public static final int BOOK_CUSTOM = 0x00000040;
	public static final int BOOK_CARNAL = 0x00000080;
	public static final int BOOK_ALCOHOL = 0x00000100;
	public static final int BOOK_ALL = BITFIELD_ALL;

	public static final int COMP_NONE = BITFIELD_NONE;
	public static final int COMP_VERBAL = 0x00000001;
	public static final int COMP_SOMATIC = 0x00000002;
	public static final int COMP_MATERIAL = 0x00000004;
	public static final int COMP_ALL = BITFIELD_ALL;

	private static final int IDX_CODE = 0;
	private static final int IDX_LABEL = 1;
	private static final int IDX_RES_ID = 2;

	private static final String LEVEL_LBL_CANTRIP = "C";
	private static final String LEVEL_LBL_QUEST = "Q";

	private static final String SCHOOL_LBL_ALL = "All Schools";
	private static final String SCHOOL_LBL_ABJURATION = "Abjuration";
	private static final String SCHOOL_LBL_ALTERATION = "Alteration";
	private static final String SCHOOL_LBL_CONJURATION = "Conjuration";
	private static final String SCHOOL_LBL_DIVINATION = "Divination";
	private static final String SCHOOL_LBL_ENCHANTMENT = "Enchantment";
	private static final String SCHOOL_LBL_EVOCATION = "Evocation";
	private static final String SCHOOL_LBL_ILLUSION = "Illusion";
	private static final String SCHOOL_LBL_NECROMANCY = "Necromancy";
	private static final String SCHOOL_LBL_AIR = "Air";
	private static final String SCHOOL_LBL_EARTH = "Earth";
	private static final String SCHOOL_LBL_FIRE = "Fire";
	private static final String SCHOOL_LBL_WATER = "Water";
	private static final String SCHOOL_LBL_DIMENSIONAL = "Dimensional";
	private static final String SCHOOL_LBL_FORCE = "Force";
	private static final String SCHOOL_LBL_SHADOW = "Shadow";
	private static final String SCHOOL_LBL_ALCHEMY = "Alchemy";
	private static final String SCHOOL_LBL_ARTIFICE = "Artifice";
	private static final String SCHOOL_LBL_GEOMETRY = "Geometry";
	private static final String SCHOOL_LBL_SONG = "Song";
	private static final String SCHOOL_LBL_WILD = "Wild";
	private static final String SCHOOL_LBL_MENTALISM = "Mentalism";
	private static final Object[][] SCHOOL = new Object[][]{
			{SCHOOL_ALL, SCHOOL_LBL_ALL, R.string.lbl_school_all},
			{SCHOOL_ABJURATION, SCHOOL_LBL_ABJURATION, R.string.lbl_school_abjuration},
			{SCHOOL_ALTERATION, SCHOOL_LBL_ALTERATION, R.string.lbl_school_alteration},
			{SCHOOL_CONJURATION, SCHOOL_LBL_CONJURATION, R.string.lbl_school_conjuration},
			{SCHOOL_DIVINATION, SCHOOL_LBL_DIVINATION, R.string.lbl_school_divination},
			{SCHOOL_ENCHANTMENT, SCHOOL_LBL_ENCHANTMENT, R.string.lbl_school_enchantment},
			{SCHOOL_ILLUSION, SCHOOL_LBL_ILLUSION, R.string.lbl_school_illusion},
			{SCHOOL_EVOCATION, SCHOOL_LBL_EVOCATION, R.string.lbl_school_evocation},
			{SCHOOL_NECROMANCY, SCHOOL_LBL_NECROMANCY, R.string.lbl_school_necromancy},
			{SCHOOL_DIMENSIONAL, SCHOOL_LBL_DIMENSIONAL, R.string.lbl_school_dimensional},
			{SCHOOL_AIR, SCHOOL_LBL_AIR, R.string.lbl_school_air},
			{SCHOOL_EARTH, SCHOOL_LBL_EARTH, R.string.lbl_school_earth},
			{SCHOOL_FIRE, SCHOOL_LBL_FIRE, R.string.lbl_school_fire},
			{SCHOOL_WATER, SCHOOL_LBL_WATER, R.string.lbl_school_water},
			{SCHOOL_FORCE, SCHOOL_LBL_FORCE, R.string.lbl_school_force},
			{SCHOOL_MENTALISM, SCHOOL_LBL_MENTALISM, R.string.lbl_school_mentalism},
			{SCHOOL_SHADOW, SCHOOL_LBL_SHADOW, R.string.lbl_school_shadow},
			{SCHOOL_ALCHEMY, SCHOOL_LBL_ALCHEMY, R.string.lbl_school_alchemy},
			{SCHOOL_ARTIFICE, SCHOOL_LBL_ARTIFICE, R.string.lbl_school_artifice},
			{SCHOOL_GEOMETRY, SCHOOL_LBL_GEOMETRY, R.string.lbl_school_geometry},
			{SCHOOL_SONG, SCHOOL_LBL_SONG, R.string.lbl_school_song},
			{SCHOOL_WILD, SCHOOL_LBL_WILD, R.string.lbl_school_wild}
	};
	
	private static final String SPHERE_LBL_ALL = "All";
	private static final String SPHERE_LBL_ANIMAL = "Animal";
	private static final String SPHERE_LBL_ASTRAL = "Astral";
	private static final String SPHERE_LBL_CHARM = "Charm";
	private static final String SPHERE_LBL_COMBAT = "Combat";
	private static final String SPHERE_LBL_CREATION = "Creation";
	private static final String SPHERE_LBL_DIVINATION = "Divination";
	private static final String SPHERE_LBL_ELEMENTAL_AIR = "Elemental (Air)";
	private static final String SPHERE_LBL_ELEMENTAL_EARTH = "Elemental (Earth)";
	private static final String SPHERE_LBL_ELEMENTAL_FIRE = "Elemental (Fire)";
	private static final String SPHERE_LBL_ELEMENTAL_WATER = "Elemental (Water)";
	private static final String SPHERE_LBL_GUARDIAN = "Guardian";
	private static final String SPHERE_LBL_HEALING = "Healing";
	private static final String SPHERE_LBL_NECROMANTIC = "Necromantic";
	private static final String SPHERE_LBL_PLANT = "Plant";
	private static final String SPHERE_LBL_PROTECTION = "Protection";
	private static final String SPHERE_LBL_SUMMONING = "Summoning";
	private static final String SPHERE_LBL_SUN = "Sun";
	private static final String SPHERE_LBL_WEATHER = "Weather";
	private static final String SPHERE_LBL_CHAOS = "Chaos";
	private static final String SPHERE_LBL_LAW = "Law";
	private static final String SPHERE_LBL_NUMBERS = "Numbers";
	private static final String SPHERE_LBL_THOUGHT = "Thought";
	private static final String SPHERE_LBL_TIME = "Time";
	private static final String SPHERE_LBL_TRAVELERS = "Travelers";
	private static final String SPHERE_LBL_WAR = "War";
	private static final String SPHERE_LBL_WARDS = "Wards";
	private static final Object[][] SPHERE = new Object[][]{
			{SPHERE_ALL, SPHERE_LBL_ALL, R.string.lbl_sphere_all},
			{SPHERE_ASTRAL, SPHERE_LBL_ASTRAL, R.string.lbl_sphere_astral},
			{SPHERE_CHARM, SPHERE_LBL_CHARM, R.string.lbl_sphere_charm},
			{SPHERE_COMBAT, SPHERE_LBL_COMBAT, R.string.lbl_sphere_combat},
			{SPHERE_CREATION, SPHERE_LBL_CREATION, R.string.lbl_sphere_creation},
			{SPHERE_DIVINATION, SPHERE_LBL_DIVINATION, R.string.lbl_sphere_divination},
			{SPHERE_GUARDIAN, SPHERE_LBL_GUARDIAN, R.string.lbl_sphere_guardian},
			{SPHERE_HEALING, SPHERE_LBL_HEALING, R.string.lbl_sphere_healing},
			{SPHERE_NECROMANTIC, SPHERE_LBL_NECROMANTIC, R.string.lbl_sphere_necromantic},
			{SPHERE_PROTECTION, SPHERE_LBL_PROTECTION, R.string.lbl_sphere_protection},
			{SPHERE_SUMMONING, SPHERE_LBL_SUMMONING, R.string.lbl_sphere_summoning},
			{SPHERE_ANIMAL, SPHERE_LBL_ANIMAL, R.string.lbl_sphere_animal},
			{SPHERE_ELEMENTAL_AIR, SPHERE_LBL_ELEMENTAL_AIR, R.string.lbl_sphere_elemental_air},
			{SPHERE_ELEMENTAL_EARTH, SPHERE_LBL_ELEMENTAL_EARTH, R.string.lbl_sphere_elemental_earth},
			{SPHERE_ELEMENTAL_FIRE, SPHERE_LBL_ELEMENTAL_FIRE, R.string.lbl_sphere_elemental_fire},
			{SPHERE_ELEMENTAL_WATER, SPHERE_LBL_ELEMENTAL_WATER, R.string.lbl_sphere_elemental_water},
			{SPHERE_PLANT, SPHERE_LBL_PLANT, R.string.lbl_sphere_plant},
			{SPHERE_SUN, SPHERE_LBL_SUN, R.string.lbl_sphere_sun},
			{SPHERE_WEATHER, SPHERE_LBL_WEATHER, R.string.lbl_sphere_weather},
			{SPHERE_CHAOS, SPHERE_LBL_CHAOS, R.string.lbl_sphere_chaos},
			{SPHERE_LAW, SPHERE_LBL_LAW, R.string.lbl_sphere_law},
			{SPHERE_NUMBERS, SPHERE_LBL_NUMBERS, R.string.lbl_sphere_numbers},
			{SPHERE_THOUGHT, SPHERE_LBL_THOUGHT, R.string.lbl_sphere_thought},
			{SPHERE_TIME, SPHERE_LBL_TIME, R.string.lbl_sphere_time},
			{SPHERE_TRAVELERS, SPHERE_LBL_TRAVELERS, R.string.lbl_sphere_travelers},
			{SPHERE_WAR, SPHERE_LBL_WAR, R.string.lbl_sphere_war},
			{SPHERE_WARDS, SPHERE_LBL_WARDS, R.string.lbl_sphere_wards}
	};
	
	private static final String COMP_LBL_VERBAL = "V";
	private static final String COMP_LBL_SOMATIC = "S";
	private static final String COMP_LBL_MATERIAL = "M";
	private static final Object[][] COMP = new Object[][] {
		{COMP_VERBAL, COMP_LBL_VERBAL, R.string.lbl_compo_v},
		{COMP_SOMATIC, COMP_LBL_SOMATIC, R.string.lbl_compo_s},
		{COMP_MATERIAL, COMP_LBL_MATERIAL, R.string.lbl_compo_m}
	};

	private static final String BOOK_LBL_BASE = "Base";
	private static final String BOOK_LBL_TOME = "Tome";
	private static final String BOOK_LBL_WIZARDS = "Wizards";
	private static final String BOOK_LBL_PRIESTS = "Priests";
	private static final String BOOK_LBL_SPELLS = "Spells";
	private static final String BOOK_LBL_ADVENTURES = "Adventures";
	private static final String BOOK_LBL_CUSTOM = "Custom";
	private static final String BOOK_LBL_CARNAL = "Carnal";
	private static final String BOOK_LBL_ALCOHOL = "Alcohol";
	private static final Object[][] BOOK = new Object[][] {
			{BOOK_BASE, BOOK_LBL_BASE, R.string.lbl_book_base},
			{BOOK_TOME, BOOK_LBL_TOME, R.string.lbl_book_tome},
			{BOOK_WIZARDS, BOOK_LBL_WIZARDS, R.string.lbl_book_wizards},
			{BOOK_PRIESTS, BOOK_LBL_PRIESTS, R.string.lbl_book_priests},
			{BOOK_SPELLS, BOOK_LBL_SPELLS, R.string.lbl_book_spells},
			{BOOK_ADVENTURES, BOOK_LBL_ADVENTURES, R.string.lbl_book_adventures},
			{BOOK_CUSTOM, BOOK_LBL_CUSTOM, R.string.lbl_book_custom},
			{BOOK_CARNAL, BOOK_LBL_CARNAL, R.string.lbl_book_carnal},
			{BOOK_ALCOHOL, BOOK_LBL_ALCOHOL, R.string.lbl_book_alcohol}
	};

	
	long mId;
	long mSourceId;
	@Expose @SerializedName("uid")
	int mUid;
	@Expose @SerializedName("type")
	int mType;
	@Expose @SerializedName("lvl")
	int mLevel;
	@Expose @SerializedName("name")
	String mName;
	@Expose @SerializedName("book")
	String mBookTxt;
	int mBook;
	@Expose @SerializedName("rev")
	boolean mReversible;
	@Expose @SerializedName("schools")
	String mSchoolsTxt;
	int mSchools;
	@Expose @SerializedName("spheres")
	String mSpheresTxt;
	int mSpheres;
	@Expose @SerializedName("rng")
	String mRange;
	@Expose @SerializedName("compo")
	String mComposTxt;
	int mCompos;
	@Expose @SerializedName("dur")
	String mDuration;
	@Expose @SerializedName("castime")
	String mCastingTime;
	@Expose @SerializedName("aoe")
	String mArea;
	@Expose @SerializedName("saving")
	String mSaving;
	@Expose @SerializedName("body")
	String mDesc;
	@Expose @SerializedName("author")
	String mAuthor;

	public Spell() { }

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		this.mId = id;
	}

	/**
	 * Identifier of the source of this spell.<br>
	 * See {@link Source}.
	 * @return Row ID.
	 */
	public long getSourceId() {
		return mSourceId;
	}

	/**
	 * Identifier of the source of this spell<br>
	 * See {@link Source}.
	 * @param sourceId Identifier of the source of this spell
	 */
	public void setSourceId(long sourceId) {
		this.mSourceId = sourceId;
	}

	/**
	 * Unique identifier relative to the source.<br>
	 * See {@link #getSourceId()}.
	 * @return Unique identifier relative to the source.
	 */
	public int getUid() {
		return mUid;
	}

	/**
	 * Unique identifier relative to the source.<br>
	 * See {@link #getSourceId()}
	 * @param uid Unique identifier relative to the source.
	 */
	public void setUid(int uid) {
		this.mUid = uid;
	}

	@SpellType
	public int getType() {
		return mType;
	}

	public void setType(int type) {
		this.mType = type;
	}

	public int getLevel() {
		return mLevel;
	}

	public void setLevel(int level) {
		this.mLevel = level;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public boolean isReversible() {
		return mReversible;
	}

	public void setReversible(boolean reversible) {
		this.mReversible = reversible;
	}

	public String getSchoolsTxt() {
		return mSchoolsTxt;
	}

	public void setSchoolsTxt(String schoolsTxt) {
		this.mSchoolsTxt = schoolsTxt;
	}

	public int getSchools() {
		return mSchools;
	}

	public void setSchools(int schools) {
		this.mSchools = schools;
	}

	public String getSpheresTxt() {
		return mSpheresTxt;
	}

	public void setSpheresTxt(String spheresTxt) {
		this.mSpheresTxt = spheresTxt;
	}

	public int getSpheres() {
		return mSpheres;
	}

	public void setSpheres(int spheres) {
		this.mSpheres = spheres;
	}

	public String getRange() {
		return mRange;
	}

	public void setRange(String range) {
		this.mRange = range;
	}

	public String getComposTxt() {
		return mComposTxt;
	}

	public void setComposTxt(String composTxt) {
		this.mComposTxt = composTxt;
	}

	public int getCompos() {
		return mCompos;
	}

	public void setCompos(int compos) {
		this.mCompos = compos;
	}

	public String getDuration() {
		return mDuration;
	}

	public void setDuration(String duration) {
		this.mDuration = duration;
	}

	public String getCastingTime() {
		return mCastingTime;
	}

	public void setCastingTime(String castingTime) {
		this.mCastingTime = castingTime;
	}

	public String getArea() {
		return mArea;
	}

	public void setArea(String area) {
		this.mArea = area;
	}

	public String getSaving() {
		return mSaving;
	}

	public void setSaving(String saving) {
		this.mSaving = saving;
	}

	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String desc) {
		this.mDesc = desc;
	}

	public String getBookTxt() {
		return mBookTxt;
	}

	public void setBookTxt(String bookTxt) {
		this.mBookTxt = bookTxt;
	}

	public int getBook() {
		return mBook;
	}

	public void setBook(int book) {
		this.mBook = book;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public void setAuthor(String author) {
		this.mAuthor = author;
	}

	public void initBitfields() {
		mSchools = getSchoolsCode(mSchoolsTxt);
		mSpheres = getSpheresCode(mSpheresTxt);
		mCompos = getComposCode(mComposTxt);
		mBook = getBookCode(mBookTxt);
	}

	public int getBookResId() {
		int[] resIdList = getResIdList(mBook, BOOK);
		if (resIdList.length == 0) {
			return R.string.lbl_book_unknown;
		}
		return resIdList[0];
	}

	public int[] getSchoolsResIdList() {
		return getSchoolsResIdList(mSchools); //getResIdList(mSchools, SCHOOL);
	}

	public int[] getSpheresResIdList() {
		return getSpheresResIdList(mSpheres); //getResIdList(mSpheres, SPHERE);
	}

	public int[] getComposResIdList() {
		return getComposResIdList(mCompos); //getResIdList(mCompos, COMP);
	}

	public static int[] getSchoolsResIdList(int schools) {
		return getResIdList(schools, SCHOOL);
	}

	public static int[] getSpheresResIdList(int spheres) {
		return getResIdList(spheres, SPHERE);
	}

	public static int[] getComposResIdList(int compos) {
		return getResIdList(compos, COMP);
	}

	private static int[] getResIdList(int bitField, Object[][] map) {
		ArrayList<Integer> retVal = new ArrayList<>();
		
		for (Object[] couple : map) {
			if (bitField == BITFIELD_ALL) {
				if ((Integer)couple[IDX_CODE] == BITFIELD_ALL) {
					retVal.add((Integer)couple[IDX_RES_ID]);
					break;
				}
			} else {
				if ((Integer)couple[IDX_CODE] == BITFIELD_ALL) {
					continue;
				}
				if ((bitField & (Integer)couple[IDX_CODE]) != 0) {
					retVal.add((Integer)couple[IDX_RES_ID]);
				}
			}
		}

		int[] ret = new int[retVal.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = retVal.get(i);
		}
		return ret;
	}

	public static String[] getBooksLabels(int books) {
		return getLabelList(books, BOOK);
	}

	public static String[] getLevelsLabels(long levels) {
		ArrayList<String> retVal = new ArrayList<>();
		for (int i = 0; i <= LEVEL_QUEST; i++) {
			long levelBit = 0x01L << (i);
			if ((levels & levelBit) != 0) {
				switch (i) {
					case LEVEL_CANTRIP:
						retVal.add(LEVEL_LBL_CANTRIP);
						break;
					case LEVEL_QUEST:
						retVal.add(LEVEL_LBL_QUEST);
						break;
					default:
						retVal.add(Integer.toString(i));
						break;
				}
			}
		}
		return retVal.toArray(new String[retVal.size()]);
	}

	public static String[] getSchoolsLabels(int schools) {
		return getLabelList(schools, SCHOOL);
	}

	public static String[] getSpheresLabels(int spheres) {
		return getLabelList(spheres, SPHERE);
	}

		@Override
	public String toString() {
		String t = (mType & Spell.TYPE_WIZARDS) != 0 ? "M" : "C";
		return mName + " (" + t + ", " + mLevel + ")";
	}

	public static int getLevelsCode(String levels) {
		int retVal = BITFIELD_NONE;
		if (levels != null) {
			int level;
			int levelBit;
			String[] tmp = levels.split(",");
			for (String s : tmp) {
				s = s.trim();
				if (LEVEL_LBL_CANTRIP.equals(s)) {
					level = LEVEL_CANTRIP;
				} else if (LEVEL_LBL_QUEST.equals(s)) {
					level = LEVEL_QUEST;
				} else {
					try {
						level = Integer.parseInt(s);
					} catch (NumberFormatException ex) {
						ex.printStackTrace();
						continue;
					}
				}
				levelBit = 0x01 << (level);
				retVal |= levelBit;
			}
		}
		return retVal;
	}

	public static int getSchoolsCode(String scools) {
		return getCode(scools, SCHOOL, SCHOOL_NONE);
	}

	public static int getSpheresCode(String spheres) {
		return getCode(spheres, SPHERE, SPHERE_NONE);
	}

	public static int getComposCode(String compos) {
		return getCode(compos, COMP, COMP_NONE);
	}

	public static int getBookCode(String book) {
		return getCode(book, BOOK, BOOK_NONE);
	}

	//region Private/protected methods

	protected static String[] getLabelList(int bitField, Object[][] map) {
		ArrayList<String> retVal = new ArrayList<>();

		for (Object[] couple : map) {
			int code = (Integer)couple[IDX_CODE];
//			if (code == BITFIELD_ALL || (bitField & code) != 0) {
//				retVal.add((String)couple[IDX_LABEL]);
//			}
			if (code == BITFIELD_ALL) {
				//Ignore the "ALL" label
				continue;
			}
			if ((bitField & code) != 0) {
				retVal.add((String)couple[IDX_LABEL]);
			}
		}

		return retVal.toArray(new String[retVal.size()]);
	}

	private static int getCode(String rawLabel, Object[][] map, int defValue) {
		int retVal = defValue;
		if (rawLabel != null) {
			String[] tmp = rawLabel.split(",");
			for (String s : tmp) {
				s = s.trim();
				for (Object[] couple : map) {
					if (s.equalsIgnoreCase((String) couple[IDX_LABEL])) {
						retVal |= (Integer) couple[IDX_CODE];
						break;
					}
				}
			}
		}
		return retVal;
	}

	//endregion

	//region Parcelable

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeLong(mId);
		parcel.writeLong(mSourceId);
		parcel.writeInt(mUid);
		parcel.writeInt(mType);
		parcel.writeInt(mLevel);
		parcel.writeString(mName);
		parcel.writeString(mBookTxt);
		parcel.writeInt(mBook);
		parcel.writeInt(mReversible ? 1 : 0);
		parcel.writeString(mSchoolsTxt);
		parcel.writeInt(mSchools);
		parcel.writeString(mSpheresTxt);
		parcel.writeInt(mSpheres);
		parcel.writeString(mRange);
		parcel.writeString(mComposTxt);
		parcel.writeInt(mCompos);
		parcel.writeString(mDuration);
		parcel.writeString(mCastingTime);
		parcel.writeString(mArea);
		parcel.writeString(mSaving);
		parcel.writeString(mDesc);
		parcel.writeString(mAuthor);
	}

	protected Spell(Parcel in) {
		mId = in.readLong();
		mSourceId = in.readLong();
		mUid = in.readInt();
		mType = in.readInt();
		mLevel = in.readInt();
		mName = in.readString();
		mBookTxt = in.readString();
		mBook = in.readInt();
		mReversible = (in.readInt() != 0);
		mSchoolsTxt = in.readString();
		mSchools = in.readInt();
		mSpheresTxt = in.readString();
		mSpheres = in.readInt();
		mRange = in.readString();
		mComposTxt = in.readString();
		mCompos = in.readInt();
		mDuration = in.readString();
		mCastingTime = in.readString();
		mArea = in.readString();
		mSaving = in.readString();
		mDesc = in.readString();
		mAuthor = in.readString();
	}

	public static final Parcelable.Creator<Spell> CREATOR = new Parcelable.Creator<Spell>() {
		public Spell createFromParcel(Parcel in) {
			return new Spell(in);
		}

		public Spell[] newArray(int size) {
			return new Spell[size];
		}
	};

	//endregion

	//region Comparable

	@Override
	public int compareTo(@Nullable Spell spell) {
		int retVal;
		if (spell == null) {
			retVal = 1;
		} else {
			retVal = compareType(getType(), spell.getType());

			if (retVal == 0) {
				retVal = compare(getLevel(), spell.getLevel());
			}

			if (retVal == 0) {
				retVal = compare(getName(), spell.getName());
			}
		}
		return retVal;
	}

	public int compareType(int first, int second) {
		//This work if there are only two types
		boolean isFirstWizard = (first & Spell.TYPE_WIZARDS) != 0;
		boolean isSecondWizard = (second & Spell.TYPE_WIZARDS) != 0;
		if (isFirstWizard == isSecondWizard)
			return 0;
		else if (isFirstWizard)
			return -1;
		return 1;
	}

	public int compare(int first, int second) {
		if (first == second)
			return 0;
		else if (first > second)
			return 1;
		return -1;
	}

	public int compare(String first, String second) {
		return first.compareToIgnoreCase(second);
	}

	//endregion
}
