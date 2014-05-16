package com.backflip270bb.android.tobuylist4ics;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

public class ProximityNotificationService extends IntentService {
	private static final String TAG = "ProximityNotificationService";
	
	public static final String ACTION_UPDATE = "com.backflip270bb.android.tobuylist.update";
	public static final String ACTION_NOTIFY = "com.backflip270bb.android.tobuylist.notify";
	
	public static final String KEY_PLACEID = "com.backflip270bb.android.tobuylist.placeid";

	private ProximityAlertManager mAlertManager;

	public ProximityNotificationService() {
		super("ProximityNotificationService");
		mAlertManager = new ProximityAlertManager(this);
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
			long id = intent.getLongExtra(KEY_PLACEID, -1);
			handleNotifyAction(id);
		} else {
			handleUpdateAction();
		}
	}

	private void handleUpdateAction() {
		Log.i(TAG, "action:update");
		mAlertManager.update();
	}

	private void handleNotifyAction(long placeId) {
		Log.i(TAG, "action:nofity placeId:" + placeId);
		
		if (placeId != -1) {
			mAlertManager.notify(placeId);
		} else {
			Log.e(TAG, "invalid placeId");
		}
	}

	@SuppressWarnings("unused")
	private void vibrate(Context context) {
		long[] pattern = {0, 500,500};
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(pattern,5);
	}
}
