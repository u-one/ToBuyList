package com.backflip270bb.android.tobuylist4ics;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class PlaceCursorAdapter extends CursorAdapter {
	LayoutInflater mInflater;
	Geocoder mGeocoder;
	
	class ViewHolder {
		TextView nameText;
		TextView areaText;
		TextView radiusText;
	}
	public PlaceCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mGeocoder = new Geocoder(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.place_list_item, null);
		ViewHolder holder = new ViewHolder();
		holder.nameText = (TextView)view.findViewById(R.id.textView1);
		holder.areaText = (TextView)view.findViewById(R.id.textView2);
		holder.radiusText = (TextView)view.findViewById(R.id.textView3);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder)view.getTag();
		
		String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.NAME_COLUMN));
		holder.nameText.setText(name);
		
		double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LAT_COLUMN));
		double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LON_COLUMN));
		try {
			List<Address> addresses = mGeocoder.getFromLocation(lat, lon, 1);
			if (!addresses.isEmpty()) {
				Address address = addresses.get(0);
				String areaName = address.getLocality() + address.getSubLocality();
				String areaString = context.getString(R.string.area_text, areaName);
				holder.areaText.setText(areaString);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		int distance = cursor.getInt(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.DISTANCE_COLUMN));
		String radiusString = context.getString(R.string.radius_text, distance);
		holder.radiusText.setText(radiusString);
	}

	@Override
	public long getItemId(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.ROW_ID));
		return id;
	}
}
