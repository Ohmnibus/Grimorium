package net.ohmnibus.grimorium.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import net.ohmnibus.grimorium.GrimoriumApp;
import net.ohmnibus.grimorium.R;
import net.ohmnibus.grimorium.database.GrimoriumContract;
import net.ohmnibus.grimorium.database.SpellProfileDbAdapter;
import net.ohmnibus.grimorium.entity.Profile;
import net.ohmnibus.grimorium.entity.Spell;
import net.ohmnibus.grimorium.entity.SpellFilter;
import net.ohmnibus.grimorium.helper.SourceCache;
import net.ohmnibus.grimorium.helper.SpellFormatter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link PreferenceFragmentCompat} subclass to edit filtering rules.
 * Use the {@link SpellFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpellFilterFragment extends PreferenceFragmentCompat
implements Preference.OnPreferenceChangeListener {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.spell_filter);
	}

	private static final String ARG_SPELL_FILTER = "ARG_SPELL_FILTER";
	private static final String ARG_PROFILE_ID = "ARG_PROFILE_ID";
	private static final String ARG_PROFILE_NAME = "ARG_PROFILE_NAME";

	private static final String KEY_TYPE_WIZARDS = "KEY_TYPE_WIZARDS";
	private static final String KEY_TYPE_PRIESTS = "KEY_TYPE_PRIESTS";
	private static final String KEY_BOOKS = "KEY_BOOKS";
	private static final String KEY_STARRED = "KEY_STARRED";
	private static final String KEY_STARRED_SHARE = "KEY_STARRED_SHARE";
	private static final String KEY_LEVEL = "KEY_LEVEL";
	private static final String KEY_SCHOOLS = "KEY_SCHOOLS";
	private static final String KEY_SPHERES = "KEY_SPHERES";
	private static final String KEY_COMPO_VERBAL = "KEY_COMPO_VERBAL";
	private static final String KEY_COMPO_SOMATIC = "KEY_COMPO_SOMATIC";
	private static final String KEY_COMPO_MATERIAL = "KEY_COMPO_MATERIAL";
	private static final String KEY_RESET = "KEY_RESET";
	private static final String KEY_SOURCES = "KEY_SOURCES";

	private static final String[] KEY_TYPE = new String[] {
			KEY_TYPE_WIZARDS,
			KEY_TYPE_PRIESTS
	};

	private static final String[] KEY_COMPO = new String[] {
			KEY_COMPO_VERBAL,
			KEY_COMPO_SOMATIC,
			KEY_COMPO_MATERIAL
	};

	private OnFilterChangedListener mListener;
	private SpellFilter mSpellFilter;
	private long mProfileId;
	private String mProfileName;
	private SourceCache mSourceCache;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 * @param profile Reference profile
	 * @return A new instance of fragment SpellFilterFragment.
	 */
	public static SpellFilterFragment newInstance(@NonNull Profile profile) {
		return newInstance(profile.getSpellFilter(), profile.getId(), profile.getName());
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param spellFilter Contain al the filtering values.
	 * @param profileId Reference profile ID
	 * @param profileName Reference profile name
	 * @return A new instance of fragment SpellFilterFragment.
	 */
	public static SpellFilterFragment newInstance(SpellFilter spellFilter, long profileId, String profileName) {
		SpellFilterFragment fragment = new SpellFilterFragment();
		if (spellFilter != null) {
			Bundle args = new Bundle();
			args.putParcelable(ARG_SPELL_FILTER, spellFilter);
			args.putLong(ARG_PROFILE_ID, profileId);
			args.putString(ARG_PROFILE_NAME, profileName);
			fragment.setArguments(args);
		}
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSourceCache = GrimoriumApp.getInstance().getSourceCache();

		if (getArguments() != null) {
			mSpellFilter = getArguments().getParcelable(ARG_SPELL_FILTER);
			mProfileId = getArguments().getLong(ARG_PROFILE_ID);
			mProfileName = getArguments().getString(ARG_PROFILE_NAME);
		} else {
			mSpellFilter = new SpellFilter();
			mProfileId = 0;
			mProfileName = "";
		}

		initValues(mSpellFilter);
	}

	@Override
	public void onAttach(@NonNull Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnFilterChangedListener) {
			mListener = (OnFilterChangedListener) activity;
		} else {
			throw new RuntimeException(activity.getClass().getSimpleName()
					+ " must implement OnFilterChangedListener");
		}
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (context instanceof OnFilterChangedListener) {
			mListener = (OnFilterChangedListener) context;
		} else {
			throw new RuntimeException(context.getClass().getSimpleName()
					+ " must implement OnFilterChangedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	private void initValues(SpellFilter filter) {
		if (filter != null) {
			initCheckBox(KEY_TYPE_WIZARDS, filter.isType(Spell.TYPE_WIZARDS));
			initCheckBox(KEY_TYPE_PRIESTS, filter.isType(Spell.TYPE_CLERICS));
			initCheckBox(KEY_STARRED, filter.isStarredOnly());

			initMultiSelectList(KEY_BOOKS, Spell.getBooksLabels(Spell.BOOK_ALL), filter.getBooksLabels());
			initMultiSelectList(KEY_LEVEL, Spell.getLevelsLabels(Spell.LEVEL_ALL), filter.getLevelsLabels());
			initMultiSelectList(KEY_SCHOOLS, Spell.getSchoolsLabels(Spell.SCHOOL_ALL), filter.getSchoolsLabels());
			initMultiSelectList(KEY_SPHERES, Spell.getSpheresLabels(Spell.SPHERE_ALL), filter.getSpheresLabels());
			initMultiSelectSourceList(filter);

			initCheckBox(KEY_COMPO_VERBAL, filter.isComponent(Spell.COMP_VERBAL));
			initCheckBox(KEY_COMPO_SOMATIC, filter.isComponent(Spell.COMP_SOMATIC));
			initCheckBox(KEY_COMPO_MATERIAL, filter.isComponent(Spell.COMP_MATERIAL));
		}
		findPreference(KEY_STARRED_SHARE).setOnPreferenceChangeListener(this);
		findPreference(KEY_RESET).setOnPreferenceChangeListener(this);
		//setSummary();
	}

	private void initMultiSelectSourceList(SpellFilter filter) {
		String[] names = new String[mSourceCache.size()];
		String[] ids = new String[mSourceCache.size()];
		HashSet<String> values = new HashSet<>();

		int idx = 0;
		for (SourceCache.SourceHeader sourceHeader : mSourceCache) {
			names[idx] = sourceHeader.getName();
			ids[idx] = Long.toString(sourceHeader.getId(), 16);
			values.add(ids[idx]);
			idx++;
		}

		long[] filtered = filter.getFilteredSources();
		for (long idToRemove : filtered) {
			values.remove(Long.toString(idToRemove, 16));
		}

		MultiSelectListPreference pref = (MultiSelectListPreference) findPreference(KEY_SOURCES);
		pref.setEntries(names);
		pref.setEntryValues(ids);
		pref.setValues(values);
		pref.setOnPreferenceChangeListener(this);
		setSummary(KEY_SOURCES, pref.getValues());
	}

	private void initCheckBox(String key, boolean value) {
		CheckBoxPreference pref = (CheckBoxPreference) findPreference(key);
		pref.setChecked(value);
		pref.setOnPreferenceChangeListener(this);
		setSummary(key, value);
	}

	private void initMultiSelectList(String key, String[] entryValues, String[] values) {
		MultiSelectListPreference pref = (MultiSelectListPreference) findPreference(key);
		pref.setEntryValues(entryValues);
		pref.setValues(new HashSet<>(Arrays.asList(values)));
		pref.setOnPreferenceChangeListener(this);
		setSummary(key, pref.getValues());
	}

	/**
	 * Set the right summary for the preference identified by {@code key}.<br />
	 * This method change the summary for those settings based on a selection,
	 * reflecting the value selected.
	 * @param key Identifier for the setting.
	 * @param newValue New value.
	 */
	private void setSummary(String key, Object newValue) {
		switch (key) {
			case KEY_TYPE_WIZARDS:
				//Wizards
				setCheckBoxSummary(key, newValue, R.string.pref_wizards_on, R.string.pref_wizards_off);
				break;
			case KEY_TYPE_PRIESTS:
				//Priests
				setCheckBoxSummary(key, newValue, R.string.pref_priests_on, R.string.pref_priests_off);
				break;
			case KEY_SOURCES:
				//Sources
				setMultiSelectListSummary(key, newValue, R.string.pref_sources);
				break;
			case KEY_BOOKS:
				//Books
				setMultiSelectListSummary(key, newValue, R.string.pref_books);
				break;
			case KEY_STARRED:
				//Star
				setCheckBoxSummary(key, newValue, R.string.pref_starred_on, R.string.pref_starred_off);
				break;
			case KEY_LEVEL:
				//Levels
				setMultiSelectListSummary(key, newValue, R.string.pref_levels);
				break;
			case KEY_SCHOOLS:
				//Schools
				setMultiSelectListSummary(key, newValue, R.string.pref_schools);
				break;
			case KEY_SPHERES:
				//Spheres
				setMultiSelectListSummary(key, newValue, R.string.pref_spheres);
				break;
			case KEY_COMPO_VERBAL:
				setCheckBoxSummary(key, newValue, R.string.pref_verbal_on, R.string.pref_verbal_off);
				break;
			case KEY_COMPO_SOMATIC:
				setCheckBoxSummary(key, newValue, R.string.pref_somatic_on, R.string.pref_somatic_off);
				break;
			case KEY_COMPO_MATERIAL:
				setCheckBoxSummary(key, newValue, R.string.pref_material_on, R.string.pref_material_off);
				break;
		}
	}

	private void setCheckBoxSummary(String key, Object newValue, int trueSummary, int falseSummary) {
		CheckBoxPreference pref = (CheckBoxPreference)findPreference(key);
		//pref.setSummary(getString(pref.isChecked() ? trueSummary : falseSummary));
		pref.setSummary(getString((boolean)newValue ? trueSummary : falseSummary));
	}

	private void setMultiSelectListSummary(String key, Object newValue, int summary) {
		MultiSelectListPreference pref = (MultiSelectListPreference)findPreference(key);
		int total = pref.getEntryValues().length;
		//int selected = pref.getValues().size();
		int selected = ((Set<String>)newValue).size();
		pref.setSummary(getString(summary, total, selected));
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (KEY_RESET.equals(preference.getKey())) { //if (preference instanceof ResetPreference) {
			mSpellFilter.reset();
			//setSummary();
			initValues(mSpellFilter);
		} else if (KEY_STARRED_SHARE.equals(preference.getKey())) { //if (preference instanceof ButtonPreference) {
			shareStarredSpellList();
			return false;
		} else {
			setSummary(preference.getKey(), newValue);
			updateFilter(mSpellFilter, preference.getKey(), newValue);
		}
		mListener.onFilterChanged(mSpellFilter);
		return true;
	}

	private void updateFilter(SpellFilter filter, String key, Object newValue) {
		switch (key) {
			case KEY_TYPE_WIZARDS:
				//Wizards
				if (allTrue(key, newValue, KEY_TYPE)) {
					filter.setTypes(Spell.TYPE_ALL);
				} else {
					filter.setType(Spell.TYPE_WIZARDS, (boolean) newValue);
				}
				break;
			case KEY_TYPE_PRIESTS:
				//Priests
				if (allTrue(key, newValue, KEY_TYPE)) {
					filter.setTypes(Spell.TYPE_ALL);
				} else {
					filter.setType(Spell.TYPE_CLERICS, (boolean) newValue);
				}
				break;
			case KEY_SOURCES:
				//Sources
				if (allSelected(key, newValue)) {
					filter.setFilteredSources(null);
				} else {
					HashSet<String> values = (HashSet<String>) newValue;

					long[] filteredSources = new long[mSourceCache.size() - values.size()];

					int cacheIdx = 0;
					int filterIdx = 0;
					while (filterIdx < filteredSources.length && cacheIdx < mSourceCache.size()) {
						SourceCache.SourceHeader source = mSourceCache.get(cacheIdx++);
						long id = source.getId();
						if (!values.contains(Long.toString(id, 16))) {
							filteredSources[filterIdx++] = id;
						}
					}
					filter.setFilteredSources(filteredSources);
				}
				break;
			case KEY_BOOKS:
				//Books
				if (allSelected(key, newValue)) {
					filter.setBooks(Spell.BOOK_ALL);
				} else {
					filter.setBooksLabels((Set<String>) newValue);
				}
				break;
			case KEY_STARRED:
				//Star
				filter.setStarredOnly((boolean) newValue);
				break;
			case KEY_LEVEL:
				//Levels
				if (allSelected(key, newValue)) {
					filter.setLevels(Spell.LEVEL_ALL);
				} else {
					filter.setLevelsLabels((Set<String>) newValue);
				}
				break;
			case KEY_SCHOOLS:
				//Schools
				if (allSelected(key, newValue)) {
					filter.setSchools(Spell.SCHOOL_ALL);
				} else {
					filter.setSchoolsLabels((Set<String>) newValue);
				}
				break;
			case KEY_SPHERES:
				//Spheres
				if (allSelected(key, newValue)) {
					filter.setSpheres(Spell.SPHERE_ALL);
				} else {
					filter.setSphereLabels((Set<String>) newValue);
				}
				break;
			case KEY_COMPO_VERBAL:
				if (allTrue(key, newValue, KEY_COMPO)) {
					filter.setComponents(Spell.COMP_ALL);
				} else {
					filter.setComponent(Spell.COMP_VERBAL, (boolean) newValue);
				}
				break;
			case KEY_COMPO_SOMATIC:
				if (allTrue(key, newValue, KEY_COMPO)) {
					filter.setComponents(Spell.COMP_ALL);
				} else {
					filter.setComponent(Spell.COMP_SOMATIC, (boolean) newValue);
				}
				break;
			case KEY_COMPO_MATERIAL:
				if (allTrue(key, newValue, KEY_COMPO)) {
					filter.setComponents(Spell.COMP_ALL);
				} else {
					filter.setComponent(Spell.COMP_MATERIAL, (boolean) newValue);
				}
				break;
		}
	}

	private boolean allSelected(String key, Object newValue) {
		Set<String> set = (Set<String>)newValue;
		MultiSelectListPreference pref = (MultiSelectListPreference)findPreference(key);
		return (pref.getEntryValues().length == set.size());
	}

	private boolean allTrue(String key, Object newValue, String[] setKeys) {
		boolean retVal = (boolean)newValue;
		for (String setKey : setKeys) {
//			if (setKey.equals(key)) {
//				continue;
//			}
			retVal = retVal && (setKey.equals(key) || ((CheckBoxPreference)findPreference(setKey)).isChecked());
		}
		return retVal;
	}

	private static final String[] COLUMNS = new String[] {
			GrimoriumContract.SpellTable.TABLE_NAME + "." + GrimoriumContract.SpellTable._ID,
			GrimoriumContract.SpellTable.TABLE_NAME + "." + GrimoriumContract.SpellTable.COLUMN_NAME_TYPE,
			GrimoriumContract.SpellTable.TABLE_NAME + "." + GrimoriumContract.SpellTable.COLUMN_NAME_LEVEL,
			GrimoriumContract.SpellTable.TABLE_NAME + "." + GrimoriumContract.SpellTable.COLUMN_NAME_NAME,
			GrimoriumContract.SpellTable.TABLE_NAME + "." + GrimoriumContract.SpellTable.COLUMN_NAME_REVERSIBLE,
			GrimoriumContract.SpellTable.TABLE_NAME + "." + GrimoriumContract.SpellTable.COLUMN_NAME_SCHOOLS_BITFIELD,
			GrimoriumContract.SpellTable.TABLE_NAME + "." + GrimoriumContract.SpellTable.COLUMN_NAME_SPHERES_BITFIELD,
			GrimoriumContract.SpellTable.TABLE_NAME + "." + GrimoriumContract.SpellTable.COLUMN_NAME_COMPONENTS_BITFIELD,
			GrimoriumContract.StarTable.TABLE_NAME + "." + GrimoriumContract.StarTable.COLUMN_NAME_STARRED
	};

	private static final int IDX_SPELL_ID = 0;
	private static final int IDX_SPELL_TYPE = 1;
	private static final int IDX_SPELL_LEVEL = 2;
	private static final int IDX_SPELL_NAME = 3;
	private static final int IDX_SPELL_REVERSIBLE = 4;
	private static final int IDX_SPELL_SCHOOL = 5;
	private static final int IDX_SPELL_SPHERE = 6;
	private static final int IDX_SPELL_COMPOS = 7;
	private static final int IDX_SPELL_STAR = 8;

	private static final String LIST_OPEN = "<ul>";
	private static final String LIST_CLOSE = "</ul>";

	private void shareStarredSpellList() {

		GrimoriumApp.getInstance().getDbManager().getSourceDbAdapter().getCursor();

		Cursor cursor = SpellProfileDbAdapter.getInstance().getStarredCursor(COLUMNS, mProfileId);
		StringBuilder sb = new StringBuilder();
		int lastType = -1;
		int lastLevel = -1;
		int counter = 0;

		String openList = LIST_OPEN;
		String closeList = "";

		while (cursor.moveToNext()) {
			if (cursor.getInt(IDX_SPELL_STAR) == 0) {
				continue;
			}
			if (cursor.getInt(IDX_SPELL_TYPE) != lastType) {
				sb.append(closeList);
				sb.append("<h1>");
				lastType = cursor.getInt(IDX_SPELL_TYPE);
				if (lastType == Spell.TYPE_WIZARDS) {
					sb.append(getString(R.string.lbl_wizards_spells));
				} else {
					sb.append(getString(R.string.lbl_priests_spells));
				}
				sb.append("</h1>");
				openList = LIST_OPEN;
				closeList = "";
			}
			if (cursor.getInt(IDX_SPELL_LEVEL) != lastLevel) {
				lastLevel = cursor.getInt(IDX_SPELL_LEVEL);
				sb.append(closeList)
						.append("<h2>")
						.append(SpellFormatter.getLevelLong(getContext(), lastType, lastLevel))
						.append("</h2>");
				openList = LIST_OPEN;
				closeList = "";
			}
			String star = cursor.getInt(IDX_SPELL_REVERSIBLE) == 1 ? "*" : "";
			sb.append(openList)
					.append("<li>- ")
					.append(cursor.getString(IDX_SPELL_NAME))
					.append(star)
					.append("</li>");
			openList = "";
			closeList = LIST_CLOSE;

			counter++;
		}
		sb.append(closeList);

		if (counter > 0) {
			String fullBook = sb.toString();
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.lbl_profile_spellbook, mProfileName));
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.lbl_profile_spellbook, mProfileName));
			sendIntent.putExtra(Intent.EXTRA_TEXT, HtmlCompat.fromHtml(fullBook, HtmlCompat.FROM_HTML_MODE_COMPACT));
			sendIntent.putExtra(Intent.EXTRA_HTML_TEXT, fullBook);
			sendIntent.setType("text/html");

			Intent shareIntent = Intent.createChooser(sendIntent, null);
			startActivity(shareIntent);
		} else {
			Toast.makeText(getContext(), R.string.msg_pref_no_starred, Toast.LENGTH_LONG).show();
		}
	}

	public interface OnFilterChangedListener {
		void onFilterChanged(SpellFilter filter);
	}

}
