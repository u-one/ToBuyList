package com.backflip270bb.android.tobuylist4ics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class ItemDetailActivity extends FragmentActivity {
	private static final int ID_INVALID = -1;
	
	public static void start(Activity activity) {
		Intent intent = new Intent();
		intent.setClass(activity, ItemDetailActivity.class);
		activity.startActivity(intent);
	}

	public static void start(Activity activity, String name) {
		Intent intent = new Intent();
		intent.setClass(activity, ItemDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ItemDetailFragment.KEY_NAME, name);
		intent.putExtra(ItemDetailFragment.KEY_ARG, bundle);
		activity.startActivity(intent);
	}
	
	public static void start(Activity activity, Long id) {
		Intent intent = new Intent();
		intent.setClass(activity, ItemDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(ItemDetailFragment.KEY_ID, id);
		intent.putExtra(ItemDetailFragment.KEY_ARG, bundle);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_detail);
		
		Intent intent = getIntent();
		Bundle bundle = null;
		if (intent.hasExtra(ItemDetailFragment.KEY_ARG)) {
			bundle = intent.getBundleExtra(ItemDetailFragment.KEY_ARG);
		}
		

		ItemDetailFragment fragment = new ItemDetailFragment();
		if (bundle != null) {
			fragment.setArguments(bundle);
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container, fragment);
		transaction.commit();
	}
}
