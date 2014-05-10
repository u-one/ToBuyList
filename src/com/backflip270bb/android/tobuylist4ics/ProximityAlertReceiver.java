package com.backflip270bb.android.tobuylist4ics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ProximityAlertReceiver extends BroadcastReceiver {
	private static final String TAG = "ProximityAlertReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		Boolean entering = intent.getBooleanExtra(key, false);
		Long id = intent.getLongExtra(ItemProviderContract.Place.ROW_ID, -1);
		
		if (entering) {
			Log.d(TAG, "entering id=" + id);
			DebugUtil.debugToast(context, "entering id=" + id);
			
			startProximityNotificationService(context, id);
		} else {
			Log.d(TAG, "exiting");
			DebugUtil.debugToast(context, "exiting");
		}
	}
	
	private void startProximityNotificationService(Context context, long id) {
		Intent intent = new Intent(ProximityNotificationService.ACTION_NOTIFY);
		intent.putExtra(ItemProviderContract.Place.ROW_ID, id);
		intent.setClass(context, ProximityNotificationService.class);
		context.startService(intent);
	}
}
