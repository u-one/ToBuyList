package com.backflip270bb.android.tobuylist4ics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			switch(position) {
			case 0:
				fragment = new ItemListFragment();
				break;
			case 1:
				fragment = new PlaceListFragment();
				break;
			case 2:
				fragment = new DebugFragment();
				break;
			default:
				fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_settings:
			break;
		case R.id.action_export:
			exportData();
			break;
		case R.id.action_import:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void exportData() {
		new AsyncTask<String, Integer, Boolean>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
			}

			@Override
			protected Boolean doInBackground(String... params) {
				exportItems();
				exportPlaces();
				return true;
			}
		}.execute("");
	}
	
	private boolean exportItems() {
		String filePath = Environment.getExternalStorageDirectory()
				+ "/ToBuyList/items.csv";
		File file = new File(filePath);
		file.getParentFile().mkdir();

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file, false);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);

			Cursor cursor = getContentResolver().query(
					ItemProviderContract.ITEM_CONTENTURI, null, null, null,
					null);
			while (cursor.moveToNext()) {
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
				String line = Long.toString(id) + ',' + name + ',' + memo + ',' + time + ',' + placeId + ',' + notify + '\n';
				bw.write(line);
				bw.flush();
			}
			bw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	private boolean exportPlaces() {
		String filePath = Environment.getExternalStorageDirectory()
				+ "/ToBuyList/places.csv";
		File file = new File(filePath);
		file.getParentFile().mkdir();

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file, false);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);

			Cursor cursor = getContentResolver().query(
					ItemProviderContract.PLACE_CONTENTURI, null, null, null,
					null);
			while (cursor.moveToNext()) {
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
				String line = id + ',' + name + ',' + lat + ',' + lon + ',' + distance + '\n';
				bw.write(line);
				bw.flush();
			}
			
			bw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void importData() {
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
			TextView dummyTextView = (TextView)rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}
