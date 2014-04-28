package com.backflip270bb.android.tobuylist4ics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;


public class PlaceDetailActivity extends FragmentActivity {
	
	public static void start(Activity activity) {
		start(activity, null);
	}

	public static void start(Activity activity, Long id) {
		Intent intent = new Intent();
		intent.setClass(activity, PlaceDetailActivity.class);
		if (id != null) {
			Bundle bundle = new Bundle();
			bundle.putLong(ItemDetailFragment.KEY_ID, id);
			intent.putExtra(ItemDetailFragment.KEY_ARG, bundle);
		}
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_placedetail);
		
		Intent intent = getIntent();
		Bundle bundle = null;
		if (intent.hasExtra(PlaceDetailFragment.KEY_ARG)) {
			bundle = intent.getBundleExtra(PlaceDetailFragment.KEY_ARG);
		}

		Fragment fragment = new PlaceDetailFragment();
		if (bundle != null) {
			fragment.setArguments(bundle);
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container, fragment);
		transaction.commit();
	}
	
	@Override
	public void onBackPressed() {
		/*
		if (currentPos == null) {
			DialogFragment newFragment = new PositionAlertDialogFragment();
			newFragment.show(getSupportFragmentManager(), "positionUndef");
			return;
		}
		if (true) {
			DialogFragment newFragment = new SaveConfirmDialogFragment();
			newFragment.show(getSupportFragmentManager(), "saveconfirm");
		}
		*/
		super.onBackPressed();
	}
}
