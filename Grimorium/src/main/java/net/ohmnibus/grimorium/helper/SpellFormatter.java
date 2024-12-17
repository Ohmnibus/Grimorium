package net.ohmnibus.grimorium.helper;

import android.content.Context;
import android.hardware.camera2.params.StreamConfigurationMap;

import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.entity.Spell;

/**
 * Created by Ohmnibus on 27/09/2016.
 */

public class SpellFormatter {

	Context mContext;
	Spell mSpell;

	public SpellFormatter(Context context, Spell spell) {
		mContext = context;
		mSpell = spell;
	}

	public void setSpell(Spell spell) {
		mSpell = spell;
	}

	public String getName() {
		return getName(false);
	}

	public String getName(boolean markReversible) {
		if (markReversible && mSpell.isReversible()) {
			return getName(mContext, mSpell) + "*";
		}
		return getName(mContext, mSpell);
	}

	public String getType() {
		return getType(mContext, mSpell);
	}

	public int getTypeLevel() {
		return getTypeLevel(mContext, mSpell);
	}

	public String getLevel() {
		return getLevel(mContext, mSpell);
	}

	public String getLevelLong() {
		return getLevelLong(mContext, mSpell);
	}

	public String getSchool() {
		return getSchool(mContext, mSpell, false);
	}

	public String getSchool(boolean format) {
		return getSchool(mContext, mSpell, format);
	}

	public String getRange() {
		return mSpell.getRange();
	}

	public String getCompos() {
		return getCompos(mContext, mSpell);
	}

	public String getDuration() {
		return mSpell.getDuration();
	}

	public String getCastingTime() {
		return mSpell.getCastingTime();
	}

	public String getArea() {
		return mSpell.getArea();
	}

	public String getSaving() {
		return mSpell.getSaving();
	}

	public String getDescription() {
		return getDescription(mContext, mSpell);
	}

	public String getReversible() {
		return getReversible(mContext, mSpell);
	}

	public String getSphere() {
		return getSphere(mContext, mSpell);
	}

	public String getBook() {
		return getBook(mContext, mSpell, false);
	}

	public String getBook(boolean format) {
		return getBook(mContext, mSpell, format);
	}

	public String getAuthor() {
		return getAuthor(mContext, mSpell, false);
	}

	public String getAuthor(boolean format) {
		return getAuthor(mContext, mSpell, format);
	}

	//region Static methods

	public static String getName(Context ctx, Spell spell) {
		return spell.getName();
	}

	public static String getType(Context ctx, Spell spell) {
		int res;
		switch (spell.getType()) {
			case Spell.TYPE_WIZARDS:
				res = R.string.lbl_type_m;
				break;
			case Spell.TYPE_CLERICS:
				res = R.string.lbl_type_c;
				break;
			default:
				res = R.string.lbl_type_u;
				break;
		}
		return ctx.getString(res);
	}

	public static int getTypeLevel(Context ctx, Spell spell) {
//		int retVal;
//		switch (spell.getType()) {
//			case Spell.TYPE_WIZARDS:
//				retVal = 1;
//				break;
//			case Spell.TYPE_CLERICS:
//				retVal = 2;
//				break;
//			default:
//				retVal = 0;
//				break;
//		}
//		return retVal;
		return getTypeLevel(ctx, spell.getType());
	}

	public static int getTypeLevel(Context ctx, int type) {
		int retVal;
		switch (type) {
			case Spell.TYPE_WIZARDS:
				retVal = 1;
				break;
			case Spell.TYPE_CLERICS:
				retVal = 2;
				break;
			default:
				retVal = 0;
				break;
		}
		return retVal;
	}

	public static String getLevel(Context ctx, Spell spell) {
		return getLevel(ctx, spell.getLevel());
	}

	public static String getLevel(Context ctx, int level) {
		if (level == Spell.LEVEL_QUEST) {
			return "Q";
		} else if (level == Spell.LEVEL_CANTRIP) {
			return "C";
		}
		return Integer.toString(level) + "°";
		//return ctx.getResources().getQuantityString(R.plurals.lbl_level, spell.getLevel(), spell.getLevel());
	}

	public static String getLevelLong(Context ctx, Spell spell) {
		return getLevelLong(ctx, spell.getType(), spell.getLevel());
	}

	public static String getLevelLong(Context ctx, int type, int level) {
		String retVal;
		String levelText = "";
		if (level > 0) {
			String[] levelsText = ctx.getResources().getStringArray(R.array.LevelsList);
			if (level >= levelsText.length) {
				level = levelsText.length - 1;
			}
			levelText = levelsText[level];
		}
		if (type == Spell.TYPE_CLERICS) {
			if (level <= 0) {
				retVal = ctx.getString(R.string.lbl_spell_level_orison);
			} else {
				retVal = ctx.getString(R.string.lbl_spell_level_prayer, levelText);
			}
		} else {
			if (level <= 0) {
				retVal = ctx.getString(R.string.lbl_spell_level_cantrip);
			} else {
				retVal = ctx.getString(R.string.lbl_spell_level_spell, levelText);
			}
		}
		return retVal;
	}

	public static String getSchool(Context ctx, Spell spell, boolean format) {
//		int[] resIdList;
//		String retVal;
//		String sep;
//
//		resIdList = spell.getSchoolsResIdList();
//		retVal = "";
//		sep = "";
//		for(int resId : resIdList) {
//			retVal += sep + ctx.getString(resId);
//			sep = ", ";
//		}
//		if (format && retVal.length() > 0) {
//			retVal = ctx.getString(R.string.lbl_spell_school, retVal);
//		}
//		return retVal;
		return getSchool(ctx, spell.getSchools(), format);
	}

	public static String getSchool(Context ctx, int schools, boolean format) {
		int[] resIdList;
		String retVal;
		String sep;

		//resIdList = spell.getSchoolsResIdList();
		resIdList = Spell.getSchoolsResIdList(schools);
		retVal = "";
		sep = "";
		for(int resId : resIdList) {
			retVal += sep + ctx.getString(resId);
			sep = ", ";
		}
		if (format && retVal.length() > 0) {
			retVal = ctx.getString(R.string.lbl_spell_school, retVal);
		}
		return retVal;
	}

	public static String getSphere(Context ctx, Spell spell) {
		return getSphere(ctx, spell.getSpheres());
	}

	public static String getSphere(Context ctx, int spheres) {
		int[] resIdList;
		String retVal;
		String sep;

		//resIdList = spell.getSpheresResIdList();
		resIdList = Spell.getSpheresResIdList(spheres);
		retVal = "";
		sep = "";
		for(int resId : resIdList) {
			retVal += sep + ctx.getString(resId);
			sep = ", ";
		}
		return retVal;
	}

	public static String getCompos(Context ctx, Spell spell) {
//		int[] resIdList;
//		String compos;
//		String sep;
//
//		resIdList = spell.getComposResIdList();
//		compos = "";
//		sep = "";
//		for(int resId : resIdList) {
//			compos += sep + ctx.getString(resId);
//			sep = ", ";
//		}
//		return compos;
		return getCompos(ctx, spell.getCompos());
	}

	public static String getCompos(Context ctx, int compos) {
		int[] resIdList;
		String retVal;
		String sep;

		resIdList = Spell.getComposResIdList(compos);
		retVal = "";
		sep = "";
		for(int resId : resIdList) {
			retVal += sep + ctx.getString(resId);
			sep = ", ";
		}
		return retVal;
	}

	public static String getReversible(Context ctx, Spell spell) {
		return spell.isReversible() ? ctx.getString(R.string.lbl_spell_reversible) : "";
	}

	public static String getDescription(Context ctx, Spell spell) {
//		String[] resStringList;
//		switch (spell.getType()) {
//			case Spell.TYPE_WIZARDS:
//				resStringList = ctx.getResources().getStringArray(R.array.mages_spell_body_ref);
//				break;
//			case Spell.TYPE_CLERICS:
//				resStringList = ctx.getResources().getStringArray(R.array.clerics_spell_body_ref);
//				break;
//			default:
//				resStringList = null;
//				break;
//		}
//		if (resStringList == null || spell.getDescResId() < 0 || spell.getDescResId() >= resStringList.length) {
//			return "[Not found]";
//		}
//		return formatBody(resStringList[spell.getDescResId()]);
		return formatBody(spell.getDesc());
	}

	private static final String indent = "&nbsp; &nbsp; ";

	private static String formatBody(String rawBody) {
		if (rawBody == null) {
			return "";
		}
		StringBuilder retVal = new StringBuilder(rawBody);

		//Add indentation after every <br>
		retVal.insert(0, indent);

		int index = 0;
		do {
			index = retVal.indexOf("<br", index);
			if (index >= 0) {
				index = retVal.indexOf(">", index);
			}
			if (index >= 0) {
				if (retVal.charAt(index + 1) != '<') {
					retVal.insert(index + 1, indent);
				}
			}
		} while (index >= 0);

		return retVal.toString();
	}

	public static String getBook(Context ctx, Spell spell, boolean format) {
		String retVal = ctx.getString(spell.getBookResId());
		if (format) {
			retVal = ctx.getString(R.string.lbl_spell_book, retVal);
		}
		return retVal;
	}

	public static String getAuthor(Context ctx, Spell spell, boolean format) {
		String retVal = spell.getAuthor();
		if (format) {
			retVal = ctx.getString(R.string.lbl_spell_author, spell.getAuthor());
		}
		return retVal;
	}

	//endregion
}
