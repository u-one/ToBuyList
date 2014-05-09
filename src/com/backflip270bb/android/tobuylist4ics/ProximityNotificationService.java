package com.backflip270bb.android.tobuylist4ics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ProximityNotificationService extends IntentService {
	private static final String TAG = "ProximityNotificationService";
	
	public static final String ACTION_NOTIFY = "com.backflip270bb.android.tobuylist.notify";

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
		
		String action = intent.getAction();
		if (action.equals(ACTION_NOTIFY)) {
			long id = intent.getLongExtra(ItemProviderContract.Place.ROW_ID, -1);
			handleNotifyAction(id);
		} else {
			handleRegisterAction();
		}
	}

	private void handleRegisterAction() {
		Log.i(TAG, "action:register");
		List<Long> placeIds = getNotificationRequestedPlaceIds();
		Cursor cursor = queryPlaces(placeIds);
		if (cursor.moveToFirst()) {
			do {
				long id = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.ROW_ID));
				double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LAT_COLUMN));
				double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LON_COLUMN));
				int distance = cursor.getInt(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.DISTANCE_COLUMN));
				LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				//FIXME: fix to register ProximityAlert only once
				locMgr.addProximityAlert(lat, lon, distance, -1, createIntent(id));
			} while (cursor.moveToNext());
		} else {
			Log.w(TAG, "place(s) not found.");
		}
	}

	private void handleNotifyAction(long placeId) {
		Log.i(TAG, "action:nofity placeId:" + placeId);
		if (placeId != -1) {
			Cursor cursor = queryPlace(placeId);
			if (cursor.moveToFirst()) {
				String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.NAME_COLUMN));
				Log.i(TAG, "place name:" + name);

				showNotifications(placeId, name);
			} else {
				Log.w(TAG, "No item to notify.");
			}
		} else {
			Log.e(TAG, "invalid placeId");
		}
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

	private Cursor queryPlace(long placeId) {
		Uri uri = Uri.parse(ItemProviderContract.PLACE_CONTENTURI + "/"	+ placeId);
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		return cursor;
	}

	private void showNotifications(long placeId, String name) {
		showNotification(this, R.drawable.ic_launcher, "To buy item","tap to show the list", name);
	}

	private void showNotification(Context context, int iconID, String ticker, String title, String message) {
		PendingIntent pintent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
		Notification notification = new Notification.Builder(context)
			.setContentTitle(title)
			.setContentText(message)
			.setContentInfo("contentInfo")
			.setSmallIcon(iconID)
			.setLights(0xff, 0xff, 0x00)
			.setTicker(ticker)
			.setWhen(Calendar.getInstance().getTimeInMillis())
			.setContentIntent(pintent)
			.setVibrate(new long[] {0, 500,500, 500,500, 500,500})
			.getNotification();
			
		NotificationManager nmgr;
		nmgr= (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nmgr.notify(0, notification);
		
		// TODO: setting vibe, sound, light, show test
	}

	private PendingIntent createIntent(long id) {
		Intent intent = new Intent(ACTION_NOTIFY);
		intent.putExtra(ItemProviderContract.Place.ROW_ID, id);
		PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent, 0);
		return pintent;
	}

	@SuppressWarnings("unused")
	private void vibrate(Context context) {
		long[] pattern = {0, 500,500};
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern,5);
	}
}
