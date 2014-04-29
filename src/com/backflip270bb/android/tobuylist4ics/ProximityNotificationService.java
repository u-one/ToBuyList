package com.backflip270bb.android.tobuylist4ics;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ProximityNotificationService extends IntentService {
	private static final String TAG = "ProximityNotificationService";

	public ProximityNotificationService() {
		super("ProximityNotificationService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "onHandleIntent");
		/*
		String selection = ItemProviderContract.Item.SHOULDNOTIFY_COLUMN + "=? GROUP BY place_id";
		String[] selectionArgs = { "1", ItemProviderContract.Item.PLACEID_COLUMN};
		*/
		String selection = ItemProviderContract.Item.SHOULDNOTIFY_COLUMN + "=?";
		String[] selectionArgs = { "1"};
		Cursor cursor = getContentResolver().query(ItemProviderContract.ITEM_CONTENTURI, new String[] {"DISTINCT "+ItemProviderContract.Item.PLACEID_COLUMN}, selection, selectionArgs, null);
		if (cursor.moveToFirst()) {
			StringBuilder builder = new StringBuilder();
			do {
				Long placeId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.PLACEID_COLUMN));
				builder.append(placeId);
				if (!cursor.isLast()) {
					builder.append(',');
				}
				Log.i(TAG, "placeId:" + placeId);
			} while(cursor.moveToNext());
			Log.i(TAG, "places:" + builder.toString());
			cursor = getPlaces(builder.toString());
			if (cursor.moveToFirst()) {
				do {
					String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.NAME_COLUMN));
					double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LAT_COLUMN));
					double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LON_COLUMN));
					int distance = cursor.getInt(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.DISTANCE_COLUMN));
					Log.i(TAG, "place name:" + name);
					/*
					LocationManager locMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
					locMgr.addProximityAlert(lat, lon, distance, -1, intent);	
					*/
				} while(cursor.moveToNext());
			}
		} else {
			
		}
	}
	
	private Cursor getPlaces(String ids) {
		String selection = ItemProviderContract.Place.ROW_ID + "=?";
		String[] selectionArgs = {ids};
		return getContentResolver().query(ItemProviderContract.PLACE_CONTENTURI, null, selection, selectionArgs, null);
	}
	
	private void t() {
		
	}

}
