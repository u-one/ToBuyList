package com.backflip270bb.android.tobuylist4ics.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

public class ItemProvider extends ContentProvider {
	
	public static final int ITEM_QUERY = 1;
	public static final int ITEM_ID_QUERY = 2;
	public static final int PLACE_QUERY = 3;
	public static final int PLACE_ID_QUERY = 4;
	public static final int INVALID_URI = -1;

	private static final String PRIMARY_KEY_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";

	private static final String CREATE_ITEM_TABLE_SQL = "CREATE TABLE "
			+ ItemProviderContract.Item.TABLE_NAME + "("
			+ ItemProviderContract.Item.ROW_ID + " " + PRIMARY_KEY_TYPE + ","
			+ ItemProviderContract.Item.NAME_COLUMN + " TEXT,"
			+ ItemProviderContract.Item.DATE_COLUMN + " DATE NOT NULL,"
			+ ItemProviderContract.Item.MEMO_COLUMN + " TEXT,"
			+ ItemProviderContract.Item.PLACEID_COLUMN + " INTEGER,"
			+ ItemProviderContract.Item.SHOULDNOTIFY_COLUMN + " BOOLEAN NOT NULL DEFAULT FALSE,"
			+ ItemProviderContract.Item.DONE_COLUMN + " BOOLEAN NOT NULL DEFAULT FALSE"
			+ ")";

	private static final String CREATE_PLACE_TABLE_SQL = "CREATE TABLE "
			+ ItemProviderContract.Place.TABLE_NAME + "("
			+ ItemProviderContract.Place.ROW_ID + " " + PRIMARY_KEY_TYPE + ","
			+ ItemProviderContract.Place.NAME_COLUMN + " TEXT NOT NULL,"
			+ ItemProviderContract.Place.LAT_COLUMN + " DOUBLE NOT NULL,"
			+ ItemProviderContract.Place.LON_COLUMN + " DOUBLE NOT NULL,"
			+ ItemProviderContract.Place.DISTANCE_COLUMN + " INTEGER NOT NULL DEFAULT 0"
			+ ")";

	private static final String TAG = "ItemProvider";

	private static final UriMatcher sUriMatcher;
	private static final SparseArray<String> sMimeTypes;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(ItemProviderContract.AUTHORITY, ItemProviderContract.Item.TABLE_NAME, ITEM_QUERY);
		sUriMatcher.addURI(ItemProviderContract.AUTHORITY, ItemProviderContract.Item.TABLE_NAME + "/#", ITEM_ID_QUERY);
		sUriMatcher.addURI(ItemProviderContract.AUTHORITY, ItemProviderContract.Place.TABLE_NAME, PLACE_QUERY);
		sUriMatcher.addURI(ItemProviderContract.AUTHORITY, ItemProviderContract.Place.TABLE_NAME + "/#", PLACE_ID_QUERY);
		
		sMimeTypes = new SparseArray<String>();
		sMimeTypes.put(ITEM_QUERY, "vnd.android.cursor.dir/vnd." + ItemProviderContract.AUTHORITY + "." + ItemProviderContract.Item.TABLE_NAME);
		sMimeTypes.put(ITEM_ID_QUERY, "vnd.android.cursor.dir/item." + ItemProviderContract.AUTHORITY + "." + ItemProviderContract.Item.TABLE_NAME);
		sMimeTypes.put(PLACE_QUERY, "vnd.android.cursor.dir/vnd." + ItemProviderContract.AUTHORITY + "." + ItemProviderContract.Place.TABLE_NAME);
		sMimeTypes.put(PLACE_ID_QUERY, "vnd.android.cursor.dir/vnd." + ItemProviderContract.AUTHORITY + "." + ItemProviderContract.Place.TABLE_NAME);
	}

	private SQLiteOpenHelper mDBHelper;

	@Override
	public boolean onCreate() {
		mDBHelper = new ItemDBHelper(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		return sMimeTypes.get(sUriMatcher.match(uri));
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = mDBHelper.getReadableDatabase();

		Cursor cursor;
		switch (sUriMatcher.match(uri)) {
			case ITEM_QUERY:
				cursor = db.query(ItemProviderContract.Item.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				return cursor;
			case ITEM_ID_QUERY:
			{
				String localSelection = ItemProviderContract.Item.ROW_ID + "=?";
				String[] localSelectionArgs = { uri.getPathSegments().get(1)};
				cursor = db.query(ItemProviderContract.Item.TABLE_NAME, null, localSelection, localSelectionArgs, null, null, null);
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				return cursor;
			}
			case PLACE_QUERY:
				cursor = db.query(ItemProviderContract.Place.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				return cursor;
			case PLACE_ID_QUERY:
			{
				String localSelection = ItemProviderContract.Place.ROW_ID + "=?";
				String[] localSelectionArgs = { uri.getPathSegments().get(1)};
				cursor = db.query(ItemProviderContract.Place.TABLE_NAME, null, localSelection, localSelectionArgs, null, null, null);
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				return cursor;
			}
			default:
				throw new IllegalArgumentException("Invalid URL:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (values == null) {
			values = new ContentValues();
		}
		
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		switch (sUriMatcher.match(uri)) {
		case ITEM_QUERY:
		{
			long id = db.replace(ItemProviderContract.Item.TABLE_NAME, "NULL", values);
			if (id > 0) {
				Log.d(TAG, "inserted:"+id);
				Uri newUri = ContentUris.withAppendedId(ItemProviderContract.ITEM_CONTENTURI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			throw new SQLException("insert failed: " + uri);
		}
		case PLACE_QUERY:
		{
			long id = db.replace(ItemProviderContract.Place.TABLE_NAME, "NULL", values);
			if (id > 0) {
				Log.d(TAG, "inserted:"+id);
				Uri newUri = ContentUris.withAppendedId(ItemProviderContract.PLACE_CONTENTURI, id);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			throw new SQLException("insert failed: " + uri);
		}	
		case ITEM_ID_QUERY:
		case PLACE_ID_QUERY:
		default:
			throw new IllegalArgumentException("Invalid URL:" + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
			case ITEM_QUERY:
				count = db.delete(ItemProviderContract.Item.TABLE_NAME, " " + ItemProviderContract.Item.ROW_ID + " like '%'", null);
				break;
			case ITEM_ID_QUERY:
			{
				String localSelection = ItemProviderContract.Item.ROW_ID + "=?";
				String[] localSelectionArgs = { uri.getPathSegments().get(1)};
				count = db.delete(ItemProviderContract.Item.TABLE_NAME, localSelection, localSelectionArgs);
				break;
			}
			case PLACE_QUERY:
				count = db.delete(ItemProviderContract.Place.TABLE_NAME, " " + ItemProviderContract.Place.ROW_ID + " like '%'", null);
				break;
			case PLACE_ID_QUERY:
			{
				String localSelection = ItemProviderContract.Place.ROW_ID + "=?";
				String[] localSelectionArgs = { uri.getPathSegments().get(1)};
				count = db.delete(ItemProviderContract.Place.TABLE_NAME, localSelection, localSelectionArgs);
				break;
			}
			default:
				throw new IllegalArgumentException("Invalid URL:" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
			case ITEM_QUERY:
				count = db.update(ItemProviderContract.Item.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ITEM_ID_QUERY:
			{
				String localSelection = ItemProviderContract.Item.ROW_ID + "=?";
				String[] localSelectionArgs = { uri.getPathSegments().get(1)};
				count = db.update(ItemProviderContract.Item.TABLE_NAME, values, localSelection, localSelectionArgs);
				break;
			}
			case PLACE_QUERY:
				count = db.update(ItemProviderContract.Place.TABLE_NAME, values, selection, selectionArgs);
				break;
			case PLACE_ID_QUERY:
			{
				String localSelection = ItemProviderContract.Place.ROW_ID + "=?";
				String[] localSelectionArgs = { uri.getPathSegments().get(1)};
				count = db.update(ItemProviderContract.Place.TABLE_NAME, values, localSelection, localSelectionArgs);
				break;
			}
			default:
				throw new IllegalArgumentException("Invalid URL:" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	public static class ItemDBHelper extends SQLiteOpenHelper {
		public ItemDBHelper(Context context) {
			super(context, ItemProviderContract.DATABASE_NAME, null, ItemProviderContract.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_ITEM_TABLE_SQL);
			db.execSQL(CREATE_PLACE_TABLE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
