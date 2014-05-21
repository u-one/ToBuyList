package com.backflip270bb.android.tobuylist4ics;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferenceActivity extends Activity {
	public static final String KEY_ITEMLIST_ADD_IMMEDIATELY = "itemlist_add_immediately";
	public static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
	public static final String KEY_NOTIFICATION_VIBE = "notification_vibe";
	public static final String KEY_NOTIFICATION_SOUND = "notification_sound";
	public static final String KEY_NOTIFICATION_RINGTONE = "notification_ringtone";
	public static final String KEY_NOTIFICATION_EACHITEM = "notification_eachitem";
	public static final String KEY_NOTIFICATION_ONCE = "notification_once";
	public static final String KEY_DEBUG = "debug";
	public static final String KEY_SPLITACTIONBAR = "split_actionbar";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new AppPreferenceFragment())
				.commit();
	}

	public static class AppPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
	}
}
