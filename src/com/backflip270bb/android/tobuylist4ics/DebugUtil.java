package com.backflip270bb.android.tobuylist4ics;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class DebugUtil {

	public static void debugToast(Context context, String message) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean isDebug = pref.getBoolean(PreferenceActivity.KEY_DEBUG, false);
		if (isDebug) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	}
}
