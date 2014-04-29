package com.backflip270bb.android.tobuylist4ics;

import java.util.Calendar;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class DebugFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_debug, container, false);
		
		Button buttonAddTestData = (Button)rootView.findViewById(R.id.buttonAddTestData);
		buttonAddTestData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addTestData();
			}
		});
		Button buttonClear = (Button)rootView.findViewById(R.id.buttonClear);
		buttonClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				clear();
			}
		});
		return rootView;
	}
	
	private void addTestData() {
		/*
		ToBuyItems model = new ToBuyItems(this.getActivity());
		ToBuyItem item = new ToBuyItem("test1", Calendar.getInstance().getTimeInMillis() , "memo", 0, false);
		model.save(item);
		item = new ToBuyItem("test2", Calendar.getInstance().getTimeInMillis(), "memo", 0, false);
		model.save(item);
		item = new ToBuyItem("test3", Calendar.getInstance().getTimeInMillis(), "memo", 0, false);
		model.save(item);
		
		PlaceItems places = new PlaceItems(this.getActivity());
		PlaceItem placeItem = new PlaceItem("test", 100,100,100);
		places.save(placeItem);
		placeItem = new PlaceItem("test2", 100,100,100);
		places.save(placeItem);
		placeItem = new PlaceItem("test3", 100,100,100);
		places.save(placeItem);
		*/
		ContentValues values = new ContentValues();
		values.put(ItemProviderContract.Item.NAME_COLUMN, "test");
		values.put(ItemProviderContract.Item.DATE_COLUMN, Calendar.getInstance().getTimeInMillis());
		values.put(ItemProviderContract.Item.MEMO_COLUMN, "memo");
		getActivity().getContentResolver().insert(ItemProviderContract.ITEM_CONTENTURI, values);

		values = new ContentValues();
		values.put(ItemProviderContract.Place.NAME_COLUMN, "test");
		values.put(ItemProviderContract.Place.LAT_COLUMN, 35.42);
		values.put(ItemProviderContract.Place.LON_COLUMN, 139.46);
		values.put(ItemProviderContract.Place.DISTANCE_COLUMN, 100);
		getActivity().getContentResolver().insert(ItemProviderContract.PLACE_CONTENTURI, values);
	}
	
	private void clear() {
		getActivity().getContentResolver().delete(ItemProviderContract.ITEM_CONTENTURI, null, null);
		getActivity().getContentResolver().delete(ItemProviderContract.PLACE_CONTENTURI, null, null);
	}
}
