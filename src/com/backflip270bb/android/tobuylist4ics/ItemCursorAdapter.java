package com.backflip270bb.android.tobuylist4ics;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ItemCursorAdapter extends CursorAdapter {
	LayoutInflater mInflater;
	
	class ViewHolder {
		TextView nameText;
		TextView notifText;
		TextView placeText;
	}
	public ItemCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_item, null);
		ViewHolder holder = new ViewHolder();
		holder.nameText = (TextView)view.findViewById(R.id.textView1);
		holder.notifText = (TextView)view.findViewById(R.id.textView2);
		holder.placeText = (TextView)view.findViewById(R.id.textView3);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder)view.getTag();
		
		String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.NAME_COLUMN));
		holder.nameText.setText(name);
		
		long time = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.DATE_COLUMN));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String dateStr = format.format(cal.getTime());
		holder.notifText.setText(dateStr);
		
		int notif = cursor.getInt(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.SHOULDNOTIFY_COLUMN));
		String notifText = context.getString(R.string.notif_text, notif == 0 ? "OFF" : "ON");
		holder.placeText.setText(notifText);
	}

	@Override
	public long getItemId(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.ROW_ID));
		return id;
	}
}
