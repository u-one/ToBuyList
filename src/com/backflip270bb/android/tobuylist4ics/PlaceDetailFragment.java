package com.backflip270bb.android.tobuylist4ics;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class PlaceDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final String KEY_ARG = "arguments";
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";

	private static final int PLACE_LOADER = 0;
	
	private static final int RADIUS_STEP = 50;
	private static final int RADIUS_MAX = 1000;
	private static final int RADIUS_MIN = 0;
	private static final int RADIUS_DEFAULT = 100;
	private static final int ZOOM_DEFAULT = 15;
	
	EditText nameEditText;

	GoogleMap map;
	NumberPicker numberPikcer;
	Long id = null;
	LatLng currentPos;
	Location myLocation = null;
	int currentRadius = RADIUS_DEFAULT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_placedetail, container, false);
		nameEditText = (EditText)view.findViewById(R.id.editTextName);
		numberPikcer = (NumberPicker)view.findViewById(R.id.numberPicker);

		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getLoaderManager().destroyLoader(PLACE_LOADER);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			getLoaderManager().initLoader(PLACE_LOADER, getArguments(), this);
		}
		
		initNumberPicker();
		
		SupportMapFragment fragment = (SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
		map = fragment.getMap();
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
				debugToast("onMyLocationChange:"+location.toString());
				myLocation = location;
				
				if (currentPos == null) {
					currentPos = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
					CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentPos, ZOOM_DEFAULT);
					if (update != null) {
						map.moveCamera(update);
					}
				}
			}
		});
		map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
			@Override
			public boolean onMyLocationButtonClick() {
				debugToast("onMyLocationButtonClick");
				if (myLocation != null) {
					currentPos = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
					setPos(currentPos);
				}
				return false;
			}
		});
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng pos) {
				currentPos = pos;
				setPos(pos);
			}
		});
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		bundle = getArguments();
		Uri uri = Uri.parse(ItemProviderContract.PLACE_CONTENTURI + "/" + bundle.getLong(KEY_ID));
		return new CursorLoader(getActivity(), uri, null, null, null, null);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor.moveToFirst()) {
			id = cursor.getLong(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.ROW_ID));
			String name = cursor.getString(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.NAME_COLUMN));
			double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LAT_COLUMN));
			double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.LON_COLUMN));
			int distance = cursor.getInt(cursor.getColumnIndexOrThrow(ItemProviderContract.Place.DISTANCE_COLUMN));
			
			currentPos = new LatLng(lat, lon);
			currentRadius = distance;

			updatePos();
			nameEditText.setText(name);
			numberPikcer.setValue(currentRadius/RADIUS_STEP);
			
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentPos, ZOOM_DEFAULT);
			if (update != null) {
				map.moveCamera(update);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
	
	private void save() {
		ContentValues values = new ContentValues();
		if (id != null) {
			values.put(ItemProviderContract.Place.ROW_ID, id);
		}
		values.put(ItemProviderContract.Place.NAME_COLUMN, nameEditText.getEditableText().toString());
		values.put(ItemProviderContract.Place.LAT_COLUMN, currentPos.latitude);
		values.put(ItemProviderContract.Place.LON_COLUMN, currentPos.longitude);
		values.put(ItemProviderContract.Place.DISTANCE_COLUMN, currentRadius);

		getActivity().getContentResolver().insert(ItemProviderContract.PLACE_CONTENTURI, values);
	}
	
	private void debugToast(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}
	
	private void updatePos() {
		setPos(currentPos);
	}

	private void setPos(LatLng pos) {
		CircleOptions options = new CircleOptions();
		options.strokeColor(0xffff8800);
		options.fillColor(0x88ff8800);
		options.center(pos);
		options.radius(currentRadius);
		map.clear();
		map.addCircle(options);
	}
	
	private void initNumberPicker() {
		numberPikcer.setMaxValue(RADIUS_MAX/RADIUS_STEP);
		numberPikcer.setMinValue(RADIUS_MIN/RADIUS_STEP);
		numberPikcer.setValue(currentRadius/RADIUS_STEP);
		numberPikcer.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				currentRadius = newVal * RADIUS_STEP;
				updatePos();
			}
		});
		
		int steps = RADIUS_MAX/RADIUS_STEP+1;
		String displayedValues[] = new String[steps];
		
		for (int i=0; i < steps; i++) {
			int val = RADIUS_MIN + RADIUS_STEP * i;
			displayedValues[i] = String.valueOf(val);
		}
		numberPikcer.setDisplayedValues(displayedValues);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_save:
			if (currentPos == null) {
				DialogFragment newFragment = new PositionAlertDialogFragment();
				newFragment.show(getActivity().getSupportFragmentManager(), "positionUndef");
			} else {
				save();
				Toast.makeText(getActivity(), R.string.saving_done, Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class SaveConfirmDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.dialog_saveconfirm);
			builder.setCancelable(true);
			builder.setPositiveButton(R.string.dialog_saveconfirm_yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
				}
			});
			builder.setNegativeButton(R.string.dialog_saveconfirm_no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
				}
			});
			return builder.create();
		}
	}

	public class PositionAlertDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.dialog_positionunset);
			builder.setCancelable(true);
			builder.setPositiveButton(R.string.dialog_ok,  new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					dialogInterface.dismiss();
				}
			});
			return builder.create();
		}
	}
}
