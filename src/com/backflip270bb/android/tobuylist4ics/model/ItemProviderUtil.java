package com.backflip270bb.android.tobuylist4ics.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class ItemProviderUtil {
	
	private static final String APP_PATH = "ToBuyList";
	private static final String ITEMS_CSVFILENAME = "items.csv";
	private static final String PLACES_CSVFILENAME = "places.csv";
	
	private static final String TAG = "ItemProviderUtil";

	public static boolean exportItems(ContentResolver resolver) {
		Log.i(TAG, "exportItems");
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ '/' + APP_PATH + '/' + ITEMS_CSVFILENAME;
		File file = new File(filePath);
		file.getParentFile().mkdir();

		BufferedWriter writer = null;
		try {
			FileWriter fwriter = new FileWriter(file);
			writer = new BufferedWriter(fwriter);

			Cursor cursor = resolver.query(
					ItemProviderContract.ITEM_CONTENTURI, null, null, null,
					null);
			while (cursor.moveToNext()) {
				try {
					long id = cursor
							.getLong(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Item.ROW_ID));
					String name = cursor
							.getString(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Item.NAME_COLUMN));
					String memo = cursor
							.getString(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Item.MEMO_COLUMN));
					long time = cursor
							.getLong(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Item.DATE_COLUMN));
					long placeId = cursor
							.getLong(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Item.PLACEID_COLUMN));
					boolean notify = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Item.SHOULDNOTIFY_COLUMN)) == 0 ? false
											: true;
					String line = Long.toString(id) + ',' + name + ',' + memo + ',' + time + ',' + placeId + ',' + notify;
					writer.write(line);
					writer.newLine();
					writer.flush();
				} catch(IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
			}
		}
		return true;
	}

	public static boolean exportPlaces(ContentResolver resolver) {
		Log.i(TAG, "exportPlaces");
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ '/' + APP_PATH + '/' + PLACES_CSVFILENAME;
		File file = new File(filePath);
		file.getParentFile().mkdir();

		BufferedWriter writer = null;
		try {
			FileWriter fwriter = new FileWriter(file);
			writer = new BufferedWriter(fwriter);

			Cursor cursor = resolver.query(
					ItemProviderContract.PLACE_CONTENTURI, null, null, null,
					null);
			while (cursor.moveToNext()) {
				try {
					long id = cursor
							.getLong(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Place.ROW_ID));
					String name = cursor
							.getString(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Place.NAME_COLUMN));
					double lat = cursor
							.getDouble(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Place.LAT_COLUMN));
					double lon = cursor
							.getDouble(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Place.LON_COLUMN));
					int distance = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(ItemProviderContract.Place.DISTANCE_COLUMN));
					String line = Long.toString(id) + ',' + name + ',' + lat + ',' + lon + ',' + distance;
					writer.write(line);
					writer.newLine();
					writer.flush();
				} catch(IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
			}
		}
		return true;
	}
	
	public static boolean canImport() {
		String itemsFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ '/' + APP_PATH + '/' + ITEMS_CSVFILENAME;
		File itemsFile = new File(itemsFilePath);
		
		String placesFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ '/' + APP_PATH + '/' + PLACES_CSVFILENAME;
		File placesFile = new File(placesFilePath);

		return (itemsFile.canRead() && placesFile.canRead()) ;
	}
	
	public static boolean importItems(ContentResolver resolver) {
		Log.i(TAG, "importItems");
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ '/' + APP_PATH + '/' + ITEMS_CSVFILENAME;
		File file = new File(filePath);
		if (file.canRead()) {
			BufferedReader reader = null;
			try {
				FileReader freader = new FileReader(file);
				reader = new BufferedReader(freader);
				String line;
				while((line = reader.readLine()) != null) {
					String[] strValues = line.split(",");
					if (strValues.length == 6) {
						ContentValues values = new ContentValues();
						values.put(ItemProviderContract.Item.ROW_ID, Long.decode(strValues[0]));
						values.put(ItemProviderContract.Item.NAME_COLUMN, strValues[1]);
						values.put(ItemProviderContract.Item.MEMO_COLUMN, strValues[2]);
						values.put(ItemProviderContract.Item.DATE_COLUMN, Long.decode(strValues[3]));
						values.put(ItemProviderContract.Item.PLACEID_COLUMN, Long.decode(strValues[4]));
						values.put(ItemProviderContract.Item.SHOULDNOTIFY_COLUMN, Boolean.parseBoolean(strValues[5]));
						
						Log.d(TAG,"values:" + values.toString());
						Uri uri = resolver.insert(ItemProviderContract.ITEM_CONTENTURI, values);
						if (uri == null) {
							Log.e(TAG, "update failed");
						}
					} else {
						Log.e(TAG, "invalid line");
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			}
		} else {
			Log.e(TAG, "unable to read file: " + filePath);
			return false;
		}
		return true;
	}
	
	public static boolean importPlaces(ContentResolver resolver) {
		Log.i(TAG, "importPlaces");
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ '/' + APP_PATH + '/' + PLACES_CSVFILENAME;
		File file = new File(filePath);
		if (file.canRead()) {
			BufferedReader reader = null;
			try {
				FileReader freader = new FileReader(file);
				reader = new BufferedReader(freader);
				String line;
				while((line = reader.readLine()) != null) {
					String[] strValues = line.split(",");
					if (strValues.length == 5) {
						ContentValues values = new ContentValues();
						values.put(ItemProviderContract.Place.ROW_ID, Long.decode(strValues[0]));
						values.put(ItemProviderContract.Place.NAME_COLUMN, strValues[1]);
						values.put(ItemProviderContract.Place.LAT_COLUMN, Double.parseDouble(strValues[2]));
						values.put(ItemProviderContract.Place.LON_COLUMN, Double.parseDouble(strValues[3]));
						values.put(ItemProviderContract.Place.DISTANCE_COLUMN, Integer.decode(strValues[4]));

						Log.d(TAG,"values:" + values.toString());
						Uri uri = resolver.insert(ItemProviderContract.PLACE_CONTENTURI, values);
						if (uri == null) {
							Log.e(TAG, "update failed");
						}
					} else {
						Log.e(TAG, "invalid line");
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			}
		} else {
			Log.e(TAG, "unable to read file: " + filePath);
			return false;
		}
		return true;
	}
}
