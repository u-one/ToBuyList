package com.backflip270bb.android.tobuylist4ics;

import android.content.Context;
import android.widget.Toast;

public class DebugUtil {

	public static void debugToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
