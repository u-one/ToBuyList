package com.backflip270bb.android.tobuylist4ics;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String KEY_ARG = "arguments";
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	private static final int TOBUYITEM_LOADER = 0;
	private static final int PLACE_LOADER = 1;
	
	private static final String TAG = "ItemDetailFragment";
	
	Long id = null;
	Long time = null;
	Switch switchNotification;
	Long placeId = null;
	Spinner placeSpinner;
	PlaceSpinnerCursorAdapter placeAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		
		placeSpinner = (Spinner)view.findViewById(R.id.spinnerPlace);
		placeSpinner.setFocusable(false);
		placeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "onItemSelected positon:" + position + " id:" + id);
				if (!parent.isFocusable()) {
					parent.setFocusable(true);
					return;
				}
				placeId = id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.d(TAG, "onNothingSelected");
			}
		});
		switchNotification = (Switch)view.findViewById(R.id.switchNotification);
		switchNotification.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG, "onCheckedChanged:" + isChecked);
				placeSpinner.setEnabled(isChecked);
				if (isChecked) {
					if (placeId == null) {
						placeId = placeSpinner.getItemIdAtPosition(0);
					}
					placeSpinner.setVisibility(View.VISIBLE);
				} else {
					placeId = null;
					placeSpinner.setVisibility(View.GONE);
				}
			}
		});
		
		Button buttonCancel = (Button)view.findViewById(R.id.buttonCancel); 
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "onClick cancel");
				getActivity().finish();
			}
		});
		Button buttonSave = (Button)view.findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "onClick save");
				EditText nameEditText = (EditText)getView().findViewById(R.id.editTextName);
				EditText memoEditText = (EditText)getView().findViewById(R.id.editTextMemo);
				boolean notify = switchNotification.isChecked();
				
				ContentValues values  =createContentValues(id, nameEditText.getEditableText().toString(),
						time, memoEditText.getEditableText().toString(), notify, placeSpinner.getSelectedItemId());
				getActivity().getContentResolver().insert(ItemProviderContract.ITEM_CONTENTURI, values);
				
				Intent intent = new Intent(ProximityNotificationService.ACTION_UPDATE);
				intent.setClass(getActivity(), ProximityNotificationService.class);
				getActivity().startService(intent);
				
				getActivity().finish();
			}
		});
		return view;
	}

	private ContentValues createContentValues(Long id, String name, long time, String memo, boolean notify, Long placeId) {
		Log.d(TAG, "createContentValues id:" +id + " name:" + name + " time:" + time + " notify:" + notify + " place:" + placeId + "memo:" + memo);
		ContentValues values = new ContentValues();
		if (id != null) {
			values.put(ItemProviderContract.Item.ROW_ID, id);
		}
		values.put(ItemProviderContract.Item.NAME_COLUMN, name);
		values.put(ItemProviderContract.Item.DATE_COLUMN, time);
		values.put(ItemProviderContract.Item.MEMO_COLUMN, memo);
		values.put(ItemProviderContract.Item.SHOULDNOTIFY_COLUMN, notify);
		if (notify) {
			values.put(ItemProviderContract.Item.PLACEID_COLUMN, placeId);
		}
		return values;
	}

	@Override
	public void onDestroyView() {
		Log.d(TAG, "onDestroyView");
		super.onDestroyView();
		getLoaderManager().destroyLoader(TOBUYITEM_LOADER);
		getLoaderManager().destroyLoader(PLACE_LOADER);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	
		placeAdapter = new PlaceSpinnerCursorAdapter(getActivity(), null, true);
		placeSpinner.setAdapter(placeAdapter);   	
		
		time = Calendar.getInstance().getTimeInMillis();
		setCurrentTime(time);
		
		getLoaderManager().initLoader(PLACE_LOADER, null, this);
		
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		Log.d(TAG, "onCreateLoader " + loaderId);
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
		Log.d(TAG, "onLoadFinished");
		if (cursor.moveToFirst()) {
			switch(loader.getId()) {
			case PLACE_LOADER:
				placeAdapter.swapCursor(cursor);
				placeAdapter.notifyDataSetChanged();
				getLoaderManager().initLoader(TOBUYITEM_LOADER, getArguments(), this);
				break;
			case TOBUYITEM_LOADER:
				id = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.ROW_ID));
				String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.NAME_COLUMN));
				String memo = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.MEMO_COLUMN));
				time = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.DATE_COLUMN));
				placeId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.PLACEID_COLUMN));
				Integer position = placeAdapter.getPositionFromId(placeId);
				boolean notify = cursor.getInt(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.SHOULDNOTIFY_COLUMN)) == 0 ? false : true;
				//boolean done = cursor.getInt(cursor.getColumnIndexOrThrow(ItemProviderContract.Item.DONE_COLUMN)) == 0 ? false : true;

				if (notify && position != null) {
					switchNotification.setChecked(true);
					placeSpinner.setVisibility(View.VISIBLE);
					placeSpinner.setSelection(position);
				} else {
					switchNotification.setChecked(false);
					placeSpinner.setVisibility(View.GONE);
				}

				EditText editTextName = (EditText)getView().findViewById(R.id.editTextName);
				editTextName.setText(name);
				if (memo != null) {
					EditText editTextMemo = (EditText)getView().findViewById(R.id.editTextMemo);
					editTextMemo.getEditableText().append(memo);
				}
				setCurrentTime(time);
				
				break;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, "onLoaderReset");
	}
	
	private void setCurrentTime(long timeInMillis) {
		SimpleDateFormat format = new SimpleDateFormat("yyy/MM/dd HH:mm:ss");
		TextView textViewDate = (TextView)getView().findViewById(R.id.textViewDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		textViewDate.setText(format.format(calendar.getTime()));
	}
}