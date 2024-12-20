package net.ohmnibus.grimorium.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Set;

/**
 * Handle a set of filters to apply to the spell list.
 * Created by Ohmnibus on 31/07/2016.
 */
public class SpellFilter implements Parcelable {

	private static final int SPELL_STARRED_ONLY = 1;
	private static final int SPELL_ALL = 0;
	private static final long[] SOURCES_NO_FILTER = new long[0];

	private int types = Spell.TYPE_ALL;
	private int levels = Spell.LEVEL_ALL; //Bit 0: Cantrip; Bit 1: lvl 1
	private int books = Spell.BOOK_ALL;
	private int schools = Spell.SCHOOL_ALL;
	private int spheres = Spell.SPHERE_ALL;
	private int components = Spell.COMP_ALL;
	private int starredOnly = SPELL_ALL;
	private long[] filteredSources = SOURCES_NO_FILTER;

	public SpellFilter() {}

	public void reset() {
		types = Spell.TYPE_ALL;
		levels = Spell.LEVEL_ALL;
		books = Spell.BOOK_ALL;
		schools = Spell.SCHOOL_ALL;
		spheres = Spell.SPHERE_ALL;
		components = Spell.COMP_ALL;
		starredOnly = SPELL_ALL;
		filteredSources = SOURCES_NO_FILTER;
	}

	public boolean isEmpty() {
		return types == Spell.TYPE_ALL
				&& levels == Spell.LEVEL_ALL
				&& books == Spell.BOOK_ALL
				&& schools == Spell.SCHOOL_ALL
				&& spheres == Spell.SPHERE_ALL
				&& components == Spell.COMP_ALL
				&& starredOnly == SPELL_ALL
				&& filteredSources.length == 0;
	}

	public int getTypes() {
		return this.types;
	}

	public void setTypes(int types) {
		this.types = types;
	}

	public void setType(int type, boolean value) {
		this.types = setBits(this.types, type, value);
	}

	/**
	 * Tell if given type is accepted.
	 * @param type Either {@link Spell#TYPE_WIZARDS} or {@link Spell#TYPE_CLERICS}.
	 * @return True if given type is supported, False otherwise.
	 */
	public boolean isType(int type) {
		return (this.types & type) != 0;
	}

	public int getBooks() {
		return this.books;
	}

	public void setBooks(int books) {
		this.books = books;
	}

	@SuppressWarnings("unused")
	public void setBook(int book, boolean value) {
		this.books = setBits(this.books, book, value);
	}

	@SuppressWarnings("unused")
	public boolean isBook(int book) {
		return isBits(this.books, book, Spell.BOOK_NONE);
	}

	public String[] getBooksLabels() {
		return Spell.getBooksLabels(books);
	}

	public void setBooksLabels(Set<String> labels) {
		this.books = Spell.getBookCode(mergeLabels(labels));
	}

	public int getLevels() {
		return this.levels;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	public void setLevel(int level, boolean value) {
		int levelBit = 0x01 << level;
		this.levels = setBits(this.levels, levelBit, value);
	}

	@SuppressWarnings("unused")
	public boolean isLevel(int level) {
		int levelBit = 0x01 << level;
		return (this.levels & levelBit) != 0;
	}

	public void setLevelsLabels(Set<String> labels) {
		this.levels = Spell.getLevelsCode(mergeLabels(labels));
	}

	public String[] getLevelsLabels() {
		return Spell.getLevelsLabels(levels);
	}

	public int getSchools() {
		return schools;
	}

	public void setSchools(int schools) {
		this.schools = schools;
	}

	@SuppressWarnings("unused")
	public void setSchool(int school, boolean value) {
		this.schools = setBits(this.schools, school, value);
	}

	@SuppressWarnings("unused")
	public boolean isSchool(int school) {
		return isBits(this.schools, school, Spell.SCHOOL_NONE);
	}

	public void setSchoolsLabels(Set<String> labels) {
		this.schools = Spell.getSchoolsCode(mergeLabels(labels));
	}

	public String[] getSchoolsLabels() {
		return Spell.getSchoolsLabels(schools);
	}

	public int getSpheres() {
		return spheres;
	}

	public void setSpheres(int spheres) {
		this.spheres = spheres;
	}

	@SuppressWarnings("unused")
	public void setSphere(int sphere, boolean value) {
		this.spheres = setBits(this.spheres, sphere, value);
	}

	@SuppressWarnings("unused")
	public boolean isSphere(int sphere) {
		return isBits(this.spheres, sphere, Spell.SPHERE_NONE);
	}

	public void setSphereLabels(Set<String> labels) {
		this.spheres = Spell.getSpheresCode(mergeLabels(labels));
	}

	public String[] getSpheresLabels() {
		return Spell.getSpheresLabels(spheres);
	}

	public int getComponents() {
		return this.components;
	}

	public void setComponents(int components) {
		this.components = components;
	}

	public void setComponent(int component, boolean value) {
		this.components = setBits(this.components, component, value);
	}

	public boolean isComponent(int component) {
		return isBits(this.components, component, Spell.COMP_NONE);
	}

	public void setStarredOnly(boolean value) {
		starredOnly = value ? SPELL_STARRED_ONLY : SPELL_ALL;
	}

	public boolean isStarredOnly() {
		return starredOnly == SPELL_STARRED_ONLY;
	}

	public void setFilteredSources(long[] value) {
		filteredSources = value;
		if (filteredSources == null) {
			filteredSources = SOURCES_NO_FILTER;
		}
	}

	public long[] getFilteredSources() {
		return filteredSources;
	}

	/**
	 * Set/unset bits from bitfield.
	 * @param original Original bitfield configuration
	 * @param affected Bits to set/unset
	 * @param value New value for affected bit.
	 * @return New bitfield configuration.
	 */
	private int setBits(int original, int affected, boolean value) {
		if (value) {
			original |= affected;
		} else {
			original &= ~affected;
		}
		return original;
	}

	/**
	 * Tell if at least one of the {@code bitToTest} bits are set in {@code bitField}.
	 * @param bitField Bit field.
	 * @param bitToTest Bits to test.
	 * @return {@code True} if at least one of the {@code bitToTest} bits are set in {@code bitField}.
	 */
	private boolean isBits(int bitField, int bitToTest) {
		return (bitField & bitToTest) != 0;
	}

	/**
	 * Tell if at least one of the {@code bitToTest} bits are set in {@code bitField}.
	 * @param bitField Bit field.
	 * @param bitToTest Bits to test.
	 * @param specialValue Special value. If {@code bitToTest} is equal
	 *                     to {@code specialValue} the method will return {@code true}
	 * @return {@code True} if at least one of the {@code bitToTest} bits are set in {@code bitField}.
	 */
	private boolean isBits(int bitField, int bitToTest, int specialValue) {
		return bitToTest == specialValue || isBits(bitField, bitToTest);
	}

	private String mergeLabels(Set<String> labels) {
		final StringBuilder labelList = new StringBuilder();
		String sep = "";
		for (String label : labels) {
			labelList.append(sep).append(label);
			sep = ",";
		}
		return labelList.toString();
	}

	//region Parcelable methods

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(types);
		parcel.writeInt(levels);
		parcel.writeInt(books);
		parcel.writeInt(schools);
		parcel.writeInt(spheres);
		parcel.writeInt(components);
		parcel.writeInt(starredOnly);
		parcel.writeLongArray(filteredSources);
	}

	private SpellFilter(Parcel in) {
		types = in.readInt();
		levels = in.readInt();
		books = in.readInt();
		schools = in.readInt();
		spheres = in.readInt();
		components = in.readInt();
		starredOnly = in.readInt();
		filteredSources = in.createLongArray();
	}

	public static final Parcelable.Creator<SpellFilter> CREATOR = new Parcelable.Creator<SpellFilter>() {
		public SpellFilter createFromParcel(Parcel in) {
			return new SpellFilter(in);
		}

		public SpellFilter[] newArray(int size) {
			return new SpellFilter[size];
		}
	};

	//endregion

}
