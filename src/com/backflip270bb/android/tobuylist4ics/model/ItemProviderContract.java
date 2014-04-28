package com.backflip270bb.android.tobuylist4ics.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class ItemProviderContract implements BaseColumns {

	public static final String SCHEME = "content";
	public static final String AUTHORITY = "com.backflip270bb.android.tobuylist4ics";

	public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);
	public static final Uri ITEM_CONTENTURI = Uri.withAppendedPath(CONTENT_URI, Item.TABLE_NAME);
	public static final Uri PLACE_CONTENTURI = Uri.withAppendedPath(CONTENT_URI, Place.TABLE_NAME);

	public static final String MIME_TYPE_ROWS = "vnd.android.cursor.dir/vnd.com.backflip270bb.android.tobuylist4ics";
	public static final String MIME_TYPE_SINGLE_ROW = "vnd.android.cursor.item/vnd.backflip270bb.tobuylist4ics";

	public static final String DATABASE_NAME = "tobuylist.db";
	public static final int DATABASE_VERSION = 1;

	public static final class Item {
		public static final String TABLE_NAME = "ToBuyItems";
		public static final String ROW_ID = BaseColumns._ID;
		public static final String NAME_COLUMN = "name";
		public static final String DATE_COLUMN = "date";
		public static final String MEMO_COLUMN = "memo";
		public static final String PLACEID_COLUMN = "place_id";
		public static final String SHOULDNOTIFY_COLUMN = "should_notify";
		public static final String DONE_COLUMN = "done";
	}

	public static final class Place {
		public static final String TABLE_NAME = "PlaceItems";
		public static final String ROW_ID = BaseColumns._ID;
		public static final String NAME_COLUMN = "name";
		public static final String LAT_COLUMN = "lat";
		public static final String LON_COLUMN = "lon";
		public static final String DISTANCE_COLUMN = "distance";
	}
}
