package com.backflip270bb.android.tobuylist4ics.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ToBuyItemsDBAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_PLACE = "place_id";
    public static final String KEY_NOTIFY = "notify";
    public static final String KEY_DONE = "done";
    
    public static final String KEY_PLACE_ID = "_id";
    public static final String KEY_PLACE_NAME = "name";
    public static final String KEY_PLACE_LAT = "lat";
    public static final String KEY_PLACE_LON = "lon";
    public static final String KEY_PLACE_DISTANCE = "distance";

    private static final String TAG = "ToBuyItemsDBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TOBUYITEMS_TABLE = "to_buy_items";
    private static final String DATABASE_PLACE_TABLE = "place_items";
    
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TOBUYITEMS_TABLE + " (_id integer primary key autoincrement, "
                    + "title text not null, date integer not null, body text not null, place_id integer, notify integer, done integer);";

    private static final String PLACE_TABLE_CREATE = 
    	    "create table " + DATABASE_PLACE_TABLE + " (_id integer primary key autoincrement, "
                    + "name text not null, lat integer, lon integer, distance integer);";

    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
            db.execSQL(PLACE_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TOBUYITEMS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_PLACE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public ToBuyItemsDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the to_buy_items database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ToBuyItemsDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new item using the title and body provided. If the item is
     * successfully created return the new rowId for that item, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the item 
     * @param body the body of the item 
     * @return rowId or -1 if failed
     */
    public long createToBuyItem(String title, String body, long place_id, int notify) {
    	if (place_id < 1) {
    		notify = 0;
    	}
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DATE, System.currentTimeMillis());
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_PLACE, place_id);
        initialValues.put(KEY_NOTIFY, notify);      
        initialValues.put(KEY_DONE, 0);

        return mDb.insert(DATABASE_TOBUYITEMS_TABLE, null, initialValues);
    }
    
    public long createPlaceItem(String name, int lat, int lon, int distance) {
    	ContentValues initial_values = new ContentValues();
    	initial_values.put(KEY_PLACE_NAME, name);
    	initial_values.put(KEY_PLACE_LAT, lat);
    	initial_values.put(KEY_PLACE_LON, lon);
    	initial_values.put(KEY_PLACE_DISTANCE, distance);
    	
    	return mDb.insert(DATABASE_PLACE_TABLE, null, initial_values);
    }

    /**
     * Delete the item with the given rowId
     * 
     * @param rowId id of item to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteToBuyItem(long rowId) {

        return mDb.delete(DATABASE_TOBUYITEMS_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean deletePlaceItem(long place_id) {
    	return mDb.delete(DATABASE_PLACE_TABLE, KEY_PLACE_ID + "=" + place_id, null) > 0;
    }

    /**
     * Return a Cursor over the list of all items in the database
     * 
     * @return Cursor over all items 
     */
    public Cursor fetchAllToBuyItems() {
    	String columns[] = new String[] {KEY_ROWID, KEY_DATE, KEY_TITLE, KEY_BODY, KEY_PLACE, KEY_NOTIFY, KEY_DONE};

        return mDb.query(DATABASE_TOBUYITEMS_TABLE, columns, null, null, null, null, null);
    }
    
    public Cursor fetchAllPlaceItems() {
    	String columns[] = new String[] {KEY_PLACE_ID, KEY_PLACE_NAME, KEY_PLACE_LAT, KEY_PLACE_LON, KEY_PLACE_DISTANCE};
    	
    	return mDb.query(DATABASE_PLACE_TABLE, columns, null, null, null, null, null);
    }
    
    public Cursor fetchAllNotificationItems() {
    	String columns[] = new String[] {KEY_ROWID, KEY_DATE, KEY_TITLE, KEY_BODY, KEY_PLACE, KEY_NOTIFY, KEY_DONE};
    	String selection = KEY_NOTIFY + "=" + 1;
    	return mDb.query(DATABASE_TOBUYITEMS_TABLE, columns, selection, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the item that matches the given rowId
     * 
     * @param rowId id of item to retrieve
     * @return Cursor positioned to matching item, if found
     * @throws SQLException if item could not be found/retrieved
     */
    public Cursor fetchToBuyItem(long row_id) throws SQLException {
    	String[] columns = new String[] {KEY_ROWID, KEY_DATE, KEY_TITLE, KEY_BODY, KEY_PLACE, KEY_NOTIFY, KEY_DONE};
    	String selection = KEY_ROWID + "=" + row_id; 
    	;
        Cursor cursor = mDb.query(true, DATABASE_TOBUYITEMS_TABLE, columns, selection, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    
    public Cursor fetchPlaceItem(long place_id) throws SQLException {
    	String[] columns = new String[] {KEY_PLACE_ID, KEY_PLACE_NAME, KEY_PLACE_LAT, KEY_PLACE_LON, KEY_PLACE_DISTANCE};
    	String selection = KEY_PLACE_ID + "=" + place_id;
    	
    	Cursor cursor = mDb.query(true, DATABASE_PLACE_TABLE, columns, selection, null, null, null, null, null);
    	if (cursor != null) {
    		cursor.moveToFirst();
    	}
    	return cursor;
    }

    /**
     * Update the item using the details provided. The item to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of item to update
     * @param title value to set item title to
     * @param body value to set item body to
     * @return true if the item was successfully updated, false otherwise
     */
    public boolean updateToBuyItem(long rowId, String title, String body, long place_id, int notify) {
    	if (place_id < 1) {
    		notify = 0;
    	}
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_DATE, System.currentTimeMillis());
        args.put(KEY_BODY, body);
        args.put(KEY_PLACE, place_id);
        args.put(KEY_NOTIFY, notify);
        args.put(KEY_DONE, 0);

        return mDb.update(DATABASE_TOBUYITEMS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
   public boolean updatePlaceItem(long id, String name, int lat, int lon, int distance) {
	  ContentValues args = new ContentValues();
	  args.put(KEY_PLACE_NAME, name);
	  args.put(KEY_PLACE_LAT, lat);
	  args.put(KEY_PLACE_LON, lon);
	  args.put(KEY_PLACE_DISTANCE, distance);
	  
	  return mDb.update(DATABASE_PLACE_TABLE, args, KEY_PLACE_ID + "=" + id, null) > 0;
   }
}
