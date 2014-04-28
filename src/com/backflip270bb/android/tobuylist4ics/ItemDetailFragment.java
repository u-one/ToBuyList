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
import android.widget.Spinner;
import android.widget.TextView;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String KEY_ARG = "arguments";
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	private static final int TOBUYITEM_LOADER = 0;
	private static final int PLACE_LOADER = 1;
	
	ItemCursorAdapter placeAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getLoaderManager().destroyLoader(TOBUYITEM_LOADER);
		getLoaderManager().destroyLoader(PLACE_LOADER);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	
		Spinner spinner = (Spinner)getView().findViewById(R.id.spinnerPlace);
		placeAdapter = new ItemCursorAdapter(getActivity(), null, true);
		spinner.setAdapter(placeAdapter);   	
		
		getLoaderManager().initLoader(PLACE_LOADER, null, this);
		getLoaderManager().initLoader(TOBUYITEM_LOADER, getArguments(), this);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		Loader<Cursor> loader = null;
		switch(loaderId) {
		case PLACE_LOADER:
			loader = new CursorLoader(getActivity(), ItemProviderContract.PLACE_CONTENTURI, null, null, null, null);
			break;
		case TOBUYITEM_LOADER:
			bundle = getArguments();
			Uri uri = Uri.parse(ItemProviderContract.ITEM_CONTENTURI + "/" + bundle.getLong(KEY_ID));
			loader = new CursorLoader(getActivity(), uri, null, null, null, null);
			break;
		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor.moveToFirst()) {
			switch(loader.getId()) {
			case PLACE_LOADER:
				placeAdapter.swapCursor(cursor);
				placeAdapter.notifyDataSetChanged();
				break;
			case TOBUYITEM_LOADER:
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
				break;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}