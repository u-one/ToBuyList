package com.backflip270bb.android.tobuylist4ics;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String KEY_ARG = "arguments";
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	private static final int TOBUYITEM_LOADER = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getLoaderManager().destroyLoader(TOBUYITEM_LOADER);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(TOBUYITEM_LOADER, getArguments(), this);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		bundle = getArguments();
		Uri uri = Uri.parse(ItemProviderContract.ITEM_CONTENTURI + "/" + bundle.getLong(KEY_ID));
		return new CursorLoader(getActivity(), uri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor.moveToFirst()) {
			String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.NAME_COLUMN));
			String memo = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.MEMO_COLUMN));
			long date = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.DATE_COLUMN));
			Long placeId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.PLACEID_COLUMN));
			boolean done = cursor.getInt(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.DONE_COLUMN)) == 0 ? false : true;
			
			EditText editTextName = (EditText)getView().findViewById(R.id.editTextName);
			editTextName.getEditableText().append(name);
			EditText editTextMemo = (EditText)getView().findViewById(R.id.editTextMemo);
			editTextMemo.getEditableText().append(memo);
			SimpleDateFormat format = new SimpleDateFormat("yyy/MM/dd HH:mm:ss");
			TextView textViewDate = (TextView)getView().findViewById(R.id.textViewDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(date);
			textViewDate.setText(format.format(calendar.getTime()));

		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}