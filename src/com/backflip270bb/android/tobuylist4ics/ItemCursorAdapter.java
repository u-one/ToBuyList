package com.backflip270bb.android.tobuylist4ics;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ItemCursorAdapter extends CursorAdapter {
	LayoutInflater mInflater;
	
	class ViewHolder {
		TextView nameText;
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
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder)view.getTag();
		
		String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.NAME_COLUMN));
		holder.nameText.setText(name);
	}

	@Override
	public long getItemId(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.ROW_ID));
		return id;
	}
}
