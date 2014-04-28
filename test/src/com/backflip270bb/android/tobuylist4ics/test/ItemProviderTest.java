package com.backflip270bb.android.tobuylist4ics.test;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.util.Log;

import com.backflip270bb.android.tobuylist4ics.model.ItemProvider;
import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ItemProviderTest extends ProviderTestCase2<ItemProvider> {
	Context mMockContext;
	public ItemProviderTest() {
		super(ItemProvider.class, ItemProviderContract.AUTHORITY);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mMockContext = this.getMockContext();
	}

	private void deleteDatabase() {
		mMockContext.deleteDatabase(ItemProviderContract.DATABASE_NAME);
	}

	public void testInsertItem() {
		deleteDatabase();
		Uri uri = insertItem("name","memo", 0, 0);
		assertEquals("content://com.backflip270bb.android.tobuylist4ics/ToBuyItems/1", uri.toString());
	}
	
	public void testInsertPlace() {
		deleteDatabase();
		Uri uri = insertPlace("place", 0, 0, 0);
		Log.i("testInsertPlace2", uri.toString());
		assertEquals("content://com.backflip270bb.android.tobuylist4ics/PlaceItems/1", uri.toString());
	}

	public void testQueryItem() {
		deleteDatabase();
		insertItem("name","memo", 0, 0);
		insertItem("name2","memo2", 1, 1);
		Uri uri = Uri.parse("content://com.backflip270bb.android.tobuylist4ics/ToBuyItems/2");
		Cursor cursor = getMockContentResolver().query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertTrue(cursor.moveToFirst());
		assertEquals(1, cursor.getCount());
		try {
			int index = cursor.getColumnIndexOrThrow(ItemProviderContract.Item.ROW_ID);
			int id = cursor.getInt(index);
			assertEquals(2, id);
		} catch (IllegalArgumentException e) {
			fail();
		}
	}
	
	public void testQueryItem2() {
		deleteDatabase();
		insertItem("name","memo", 0, 0);
		insertItem("name2","memo2", 1, 1);
		String selectionClause = null;
		String[] selectionArgs = null;
		Cursor cursor = getMockContentResolver().query(
				ItemProviderContract.ITEM_CONTENTURI,
				null, selectionClause, selectionArgs, null);

		assertEquals(2, cursor.getCount());
	}
	
	public void testQueryItem3() {
		deleteDatabase();
		insertItem("name","memo", 0, 0);
		insertItem("name2","memo2", 1, 1);
		String selectionClause = ItemProviderContract.Item.ROW_ID + " = ?";
		String[] selectionArgs = {"2"};
		Cursor cursor = getMockContentResolver().query(
				ItemProviderContract.ITEM_CONTENTURI,
				null, selectionClause, selectionArgs, null);
		cursor.moveToFirst();
		assertEquals(1, cursor.getCount());
		try {
			int index = cursor.getColumnIndexOrThrow(ItemProviderContract.Item.ROW_ID);
			int id = cursor.getInt(index);
			assertEquals(2, id);
		} catch (IllegalArgumentException e) {
			fail();
		}
	}
	
	public void testQueryPlace() {
		deleteDatabase();
		insertPlace("place", 0, 0, 0);
		insertPlace("place2", 100, 100, 100);
		
		Uri uri = Uri.parse("content://com.backflip270bb.android.tobuylist4ics/PlaceItems/2");
		Cursor cursor = getMockContentResolver().query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertTrue(cursor.moveToFirst());
		assertEquals(1, cursor.getCount());
		try {
			int index = cursor.getColumnIndexOrThrow(ItemProviderContract.Place.ROW_ID);
			int id = cursor.getInt(index);
			assertEquals(2, id);
		} catch (IllegalArgumentException e) {
			fail();
		}
	}
	
	public void testUpdateItem() {
		deleteDatabase();
		Uri uri = insertItem("name","memo", 0, 0);
		Log.i("testUpdateItem", uri.toString());
		int num = updateItem(1, "name1","memo1", 1, 1);
		assertEquals(1, num);
	}
	
	public void testUpdatePlace() {
		deleteDatabase();
		Uri uri = insertPlace("place", 0, 0, 0);
		Log.i("testQueryPlace", uri.toString());
		int num = updatePlace(1, "place1", 100, 100, 100);
		assertEquals(1, num);
	}

	public void testQueryAllItem() {
		deleteDatabase();
		insertItem("name","memo", 0, 0);
		insertItem("name2","memo2", 1, 1);
		
		Cursor cursor = queryAllItem();
		assertNotNull(cursor);
		assertTrue(cursor.moveToFirst());
		try {
			int index = cursor.getColumnIndexOrThrow(ItemProviderContract.Item.NAME_COLUMN);
			String name = cursor.getString(index);
			assertEquals("name", name);
			assertTrue(cursor.moveToNext());
			assertEquals("name2", cursor.getString(index));
		} catch (IllegalArgumentException e) {
			fail();
		}
	}
	
	public void testQueryAllPlace() {
		deleteDatabase();
		insertPlace("place1", 0, 0, 0);
		insertPlace("place2", 0, 0, 0);
		
		Cursor cursor = queryAllPlace();
		assertNotNull(cursor);
		assertTrue(cursor.moveToFirst());
		try {
			int index = cursor.getColumnIndexOrThrow(ItemProviderContract.Place.NAME_COLUMN);
			String name = cursor.getString(index);
			assertEquals("place1", name);
			assertTrue(cursor.moveToNext());
			assertEquals("place2", cursor.getString(index));
		} catch (IllegalArgumentException e) {
			fail();
		}
		deleteAllPlace();
	}

	public void testDeleteItem() {
		deleteDatabase();
		insertItem("name","memo", 0, 0);
		insertItem("name2","memo2", 1, 1);
		
		Uri uri = Uri.parse("content://com.backflip270bb.android.tobuylist4ics/ToBuyItems/1");
		deleteItem(uri);
		Cursor cursor = queryAllItem();
		assertNotNull(cursor);
		assertTrue(cursor.moveToFirst());
		try {
			int index = cursor.getColumnIndexOrThrow(ItemProviderContract.Item.NAME_COLUMN);
			assertEquals("name2", cursor.getString(index));
		} catch (IllegalArgumentException e) {
			fail();
		}
	}
	
	public void testDeletePlace() {
		deleteDatabase();
		insertPlace("place1", 0, 0, 0);
		insertPlace("place2", 0, 0, 0);
		
		Uri uri = Uri.parse("content://com.backflip270bb.android.tobuylist4ics/PlaceItems/1");
		deletePlace(uri);
		Cursor cursor = queryAllPlace();
		assertNotNull(cursor);
		assertTrue(cursor.moveToFirst());
		try {
			int index = cursor.getColumnIndexOrThrow(ItemProviderContract.Place.NAME_COLUMN);
			assertEquals("place2", cursor.getString(index));
		} catch (IllegalArgumentException e) {
			fail();
		}
	}
	
	
	private ContentValues createItemValue(Integer id, String name, String memo, int shoudNotify, int done) {
		ContentValues values = new ContentValues();
		if (id != null) {
			values.put(ItemProviderContract.Item.ROW_ID, id);
		}
		values.put(ItemProviderContract.Item.NAME_COLUMN, name);
		values.put(ItemProviderContract.Item.DATE_COLUMN, Calendar.getInstance().getTimeInMillis());
		values.put(ItemProviderContract.Item.MEMO_COLUMN, memo);
		return values;
	}

	private ContentValues createPlaceValue(Integer id, String name, double lat, double lon, int distance) {
		ContentValues values = new ContentValues();
		if (id != null) {
			values.put(ItemProviderContract.Place.ROW_ID, id);
		}
		values.put(ItemProviderContract.Place.NAME_COLUMN, name);
		values.put(ItemProviderContract.Place.LAT_COLUMN, lat);
		values.put(ItemProviderContract.Place.LON_COLUMN, lon);
		values.put(ItemProviderContract.Place.DISTANCE_COLUMN, distance);
		return values;
	}

	private Uri insertItem(String name, String memo, int shoudNotify, int done) {
		ContentValues values = createItemValue(null, name, memo, shoudNotify, done);
		return getMockContentResolver().insert(ItemProviderContract.ITEM_CONTENTURI, values);
	}

	private Uri insertPlace(String name, double lat, double lon, int distance) {
		ContentValues values = createPlaceValue(null, name, lat, lon, distance);
		return getMockContentResolver().insert(ItemProviderContract.PLACE_CONTENTURI, values);
	}

	private int updateItem(Integer id, String name, String memo, int shoudNotify, int done) {
		ContentValues values = createItemValue(id, name, memo, shoudNotify, done);
		return getMockContentResolver().update(ItemProviderContract.ITEM_CONTENTURI, values, null, null);
	}
	
	private int updatePlace(Integer id, String name, long lat, long lon, int distance) {
		ContentValues values = createPlaceValue(id, name, lat, lon, distance);
		return getMockContentResolver().update(ItemProviderContract.PLACE_CONTENTURI, values, null, null);
	}

	private Cursor queryAllItem() {
		return getMockContentResolver().query(
				ItemProviderContract.ITEM_CONTENTURI, null, null, null, null); 
	}

	private Cursor queryAllPlace() {
		return getMockContentResolver().query(
				ItemProviderContract.PLACE_CONTENTURI, null, null, null, null);
	}

	private int deleteAllItem() {
		return getMockContentResolver().delete(ItemProviderContract.ITEM_CONTENTURI, null, null);
	}

	private int deleteAllPlace() {
		return getMockContentResolver().delete(ItemProviderContract.PLACE_CONTENTURI, null, null);
	}

	private int deleteItem(Uri uri) {
		return getMockContentResolver().delete(uri, null, null);
	}

	private int deletePlace(Uri uri) {
		return getMockContentResolver().delete(uri, null, null);
	}
}
