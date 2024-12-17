package net.ohmnibus.grimorium.database;

import android.provider.BaseColumns;

/**
 * Data-Contract for Grimorium Database
 * Created by Ohmnibus on 10/09/2016.
 */
@SuppressWarnings("WeakerAccess")
public final class GrimoriumContract {

	private GrimoriumContract() {}

	public static class SourceTable implements BaseColumns {
		public static final String TABLE_NAME = "source";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_NAMESPACE = "namespace";
		public static final String COLUMN_NAME_VERSION = "version";
		public static final String COLUMN_NAME_DESCRIPTION = "description";
		public static final String COLUMN_NAME_URL = "url";
	}

	public static class SpellTable implements BaseColumns {
		public static final String TABLE_NAME = "spell";
		public static final String COLUMN_NAME_SOURCE = "source";
		public static final String COLUMN_NAME_UID = "uid";
		public static final String COLUMN_NAME_TYPE = "type";
		public static final String COLUMN_NAME_LEVEL = "level";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_REVERSIBLE = "rev";
		public static final String COLUMN_NAME_SCHOOLS = "schools";
		public static final String COLUMN_NAME_SCHOOLS_BITFIELD = "schools_bf";
		public static final String COLUMN_NAME_SPHERES = "spheres";
		public static final String COLUMN_NAME_SPHERES_BITFIELD = "spheres_bf";
		public static final String COLUMN_NAME_RANGE = "range";
		public static final String COLUMN_NAME_COMPONENTS = "components";
		public static final String COLUMN_NAME_COMPONENTS_BITFIELD = "components_bf";
		public static final String COLUMN_NAME_DURATION = "duration";
		public static final String COLUMN_NAME_CAST_TIME = "cast_time";
		public static final String COLUMN_NAME_AOE = "aoe";
		public static final String COLUMN_NAME_SAVING = "saving";
		public static final String COLUMN_NAME_BOOK = "book";
		public static final String COLUMN_NAME_BOOK_BITFIELD = "book_bf";
		public static final String COLUMN_NAME_AUTHOR = "author";
		public static final String COLUMN_NAME_DESCRIPTION = "desc";
		public static final String COLUMN_NAME_UPDATED = "updated";
		public static final String INDEX_NAME = TABLE_NAME + COLUMN_NAME_TYPE + COLUMN_NAME_LEVEL + COLUMN_NAME_NAME;
	}

	public static class ProfileTable implements BaseColumns {
		public static final String TABLE_NAME = "profile";
		public static final String COLUMN_NAME_KEY = "key";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_SYS = "sys";
		public static final String COLUMN_NAME_TYPES = "types";
		public static final String COLUMN_NAME_LEVELS = "levels";
		public static final String COLUMN_NAME_BOOKS = "books";
		public static final String COLUMN_NAME_SCHOOLS = "schools";
		public static final String COLUMN_NAME_SPHERES = "spheres";
		public static final String COLUMN_NAME_COMPONENTS = "components";
		public static final String COLUMN_NAME_STARRED_ONLY = "starred_only";
		public static final String COLUMN_NAME_FILTERED_SOURCES = "filtered_sources";
		public static final String INDEX_NAME = TABLE_NAME + COLUMN_NAME_KEY;
	}

	public static class StarTable implements BaseColumns {
		public static final String TABLE_NAME = "star";
		public static final String COLUMN_NAME_PROFILE_ID = "profile_id";
		public static final String COLUMN_NAME_SPELL_ID = "spell_id";
		public static final String COLUMN_NAME_STARRED = "starred";
		public static final String COLUMN_NAME_SOURCE_NS = "source_ns";
		public static final String COLUMN_NAME_SPELL_UID = "spell_uid";
		public static final String INDEX_NAME = TABLE_NAME + COLUMN_NAME_PROFILE_ID + COLUMN_NAME_SPELL_ID;
	}
}
