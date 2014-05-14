package com.backflip270bb.android.tobuylist4ics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ProximityAlertManager {
	public static final String DATABASE_NAME = "alerts.db";
	public static final int DATABASE_VERSION = 1;
	
	private static final String PRIMARY_KEY_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
	private static final String CREATE_ALERT_TABLE_SQL = "CREATE TABLE "
			+ Alert.TABLE_NAME + "("
			+ Alert.ROW_ID + " " + PRIMARY_KEY_TYPE + ","
			+ Alert.PLACEID_COLUMN + " INTEGER"
			+ ")";

	public static final class Alert {
		public static final String TABLE_NAME = "ProximityAlerts";
		public static final String ROW_ID = BaseColumns._ID;
		public static final String PLACEID_COLUMN = "place_id";
	}
	
	private static final String TAG = "ProximityAlertManager";
	
	private SQLiteOpenHelper mDBHelper;
	private Context mContext;

	public ProximityAlertManager(Context context) {
		mDBHelper = new ProximityAlertDBHelper(context);
		mContext = context;
	}

	private Object getSystemService(String name) {
		return mContext.getSystemService(name);
	}

	private ContentResolver getContentResolver() {
		return mContext.getContentResolver();
	}

	public void update() {
		clearAlerts();
		
		List<Long> placeIds = getNotificationRequestedPlaceIds();
		Cursor cursor = queryPlaces(placeIds);
		if (cursor.moveToFirst()) {
			do {
				long id = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.ROW_ID));
				double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LAT_COLUMN));
				double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LON_COLUMN));
				int distance = cursor.getInt(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.DISTANCE_COLUMN));
				if (!has(id)) {
					registerAlert(lat, lon, distance, id);
				} else {
					Log.d(TAG, "already registered. id:"+id);
					DebugUtil.debugToast(mContext, "already registered. id:"+id);
				}
			} while (cursor.moveToNext());
		} else {
			Log.w(TAG, "place(s) not found.");
		}
	}

	public void notify(long placeId, boolean shouldRemoveAlert) {
		Cursor cursor = queryPlace(placeId);
		if (cursor.moveToFirst()) {
			String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.NAME_COLUMN));
			Log.i(TAG, "place name:" + name);

			showNotifications(placeId, name);
			
			if (shouldRemoveAlert) {
				removeAlert(placeId);
				Log.w(TAG, "removed alert");
			}
		} else {
			Log.w(TAG, "No item to notify.");
		}
	}

	private void registerAlert(double lat, double lon, int distance, long placeId) {
		register(placeId);
		LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locMgr.addProximityAlert(lat, lon, distance, -1, createIntent(placeId));
		Log.d(TAG, "registered alert. id:" + placeId);
	}

	private void removeAlert(long placeId) {
		LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locMgr.removeProximityAlert(createIntent(placeId));
		remove(placeId);
		Log.d(TAG, "removed alert. id:" + placeId);
	}

	public void clearAlerts() {
		List<Long> placeIds = getNotificationRequestedPlaceIds();
		for (Long placeId : placeIds) {
			removeAlert(placeId);
		}
	}

	
	private PendingIntent createIntent(long id) {
		//FIXME: ProximityNotificationService.ACTION_NOTIFY
		Intent intent = new Intent(ProximityNotificationService.ACTION_NOTIFY);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ProximityNotificationService.KEY_PLACEID, id);
		PendingIntent pintent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		return pintent;
	}
	
	private void showNotifications(long placeId, String placeName) {
		Cursor cursor = queryItems(placeId);
		Log.d(TAG, "showNotifications num:"+cursor.getCount());
		while(cursor.moveToNext()) {
			long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.ROW_ID));
			String itemName = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.NAME_COLUMN));
			showNotification(mContext, R.drawable.ic_launcher, "To Buy Item", itemName, "tap to show the list", placeName, itemId);
		}
	}

	private void showNotification(Context context, int iconID, String ticker, String title, String message, String contentInfo, Long itemId) {
		PendingIntent pintent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
		Notification notification = new Notification.Builder(context)
			.setContentTitle(title)
			.setContentText(message)
			.setContentInfo(contentInfo)
			.setSmallIcon(iconID)
			.setLights(0xff, 0xff, 0x00)
			.setTicker(ticker)
			.setWhen(Calendar.getInstance().getTimeInMillis())
			.setContentIntent(pintent)
			.setVibrate(new long[] {0, 500,500, 500,500, 500,500})
			.setAutoCancel(true)
			.getNotification();
			
		NotificationManager nmgr;
		nmgr= (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nmgr.notify(itemId.hashCode(), notification);
		
		// TODO: setting vibe, sound, light, show test
	}

	
	private List<Long> getNotificationRequestedPlaceIds() {
		List<Long> ids = new ArrayList<Long>();
		Cursor cursor = queryNotificationRequestedPlaces();
		if (cursor.moveToFirst()) {
			do {
				Long placeId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.PLACEID_COLUMN));
				ids.add(placeId);
			} while (cursor.moveToNext());
		} else {
			Log.w(TAG, "no place to register for proximity.");
		}
		return ids;
	}

	private String getNotificationRequestedPlaceIdsString() {
		String ids = "";
		Cursor cursor = queryNotificationRequestedPlaces();
		if (cursor.moveToFirst()) {
			StringBuilder builder = new StringBuilder();
			do {
				Long placeId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.PLACEID_COLUMN));
				builder.append(placeId);
				if (!cursor.isLast()) {
					builder.append(',');
				}
			} while (cursor.moveToNext());
			ids = builder.toString();
		} else {
			Log.w(TAG, "no place to register for proximity.");
		}
		return ids;
	}
	
	private Cursor queryNotificationRequestedPlaces() {
		String selection = ItemProviderContract.Item.SHOULDNOTIFY_COLUMN + "=?";
		String[] selectionArgs = { "1" };
		Cursor cursor = getContentResolver().query(
				ItemProviderContract.ITEM_CONTENTURI,
				new String[] { "DISTINCT "
						+ ItemProviderContract.Item.PLACEID_COLUMN },
				selection, selectionArgs, null);
		return cursor;
	}

	private Cursor queryPlaces(List<Long> placeIds) {
		String selection = new String(ItemProviderContract.Place.ROW_ID + " IN (" + buildPlaceIdsString(placeIds) + ")");
		Cursor cursor = getContentResolver().query(
				ItemProviderContract.PLACE_CONTENTURI, null, selection, null, null);
		return cursor;
	}
	
	private Cursor queryItems(long placeId) {
		String selection = ItemProviderContract.Item.PLACEID_COLUMN + "=?";
		String[] selectionArgs = { Long.toString(placeId) };
		Cursor cursor = getContentResolver().query(
				ItemProviderContract.ITEM_CONTENTURI, null, selection, selectionArgs, null);
		return cursor;
	}

	private Cursor queryPlace(long placeId) {
		Uri uri = Uri.parse(ItemProviderContract.PLACE_CONTENTURI + "/"	+ placeId);
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		return cursor;
	}

	
	private String buildPlaceIdsString(List<Long> placeIds) {
		String ids = "";
		StringBuilder builder = new StringBuilder();
		for (int i=0; i< placeIds.size(); i++) {
			builder.append(placeIds.get(i));
			if (i < placeIds.size() - 1) {
				builder.append(',');
			}
		}
		ids = builder.toString();
		Log.d(TAG, "place ids:" + ids);
		return ids;
	}

	
	public boolean register(long placeId) {
		if (!has(placeId)) {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(Alert.PLACEID_COLUMN, placeId);
			return db.update(Alert.TABLE_NAME, values, null, null) > 0 ? true : false;
		}
		return false;
	}

	public boolean has(long placeId) {
		Cursor cursor = query(placeId);
		return (cursor.getCount() > 0);
	}

	public boolean remove(long placeId) {
		if (has(placeId)) {
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			String selection = Alert.PLACEID_COLUMN + "=?";
			String[] selectionArgs = {Long.toString(placeId)};
			return db.delete(Alert.TABLE_NAME, selection, selectionArgs) > 0 ? true : false;
		}
		return false;
	}
	
	private Cursor query(long placeId) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String selection = Alert.PLACEID_COLUMN + "=?";
		String[] selectionArgs = {Long.toString(placeId)};
		Cursor cursor = db.query(Alert.TABLE_NAME, null, selection, selectionArgs, null, null, null);
		return cursor;
	}

	public static class ProximityAlertDBHelper extends SQLiteOpenHelper {
		public ProximityAlertDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_ALERT_TABLE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}