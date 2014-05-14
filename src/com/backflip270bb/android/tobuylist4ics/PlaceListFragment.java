package com.backflip270bb.android.tobuylist4ics;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class PlaceListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	PlaceCursorAdapter mItemAdapter;
	
	private static final int PLACE_LOADER = 0;

	public PlaceListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getLoaderManager().initLoader(PLACE_LOADER, null, this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		switch(loaderId) {
		case PLACE_LOADER:
			return new CursorLoader(this.getActivity(), ItemProviderContract.PLACE_CONTENTURI, null, null, null, null);
		default:
			break;
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mItemAdapter.swapCursor(cursor);
		mItemAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mItemAdapter.swapCursor(null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mItemAdapter = new PlaceCursorAdapter(getActivity(), null, true);
		setListAdapter(mItemAdapter);
		mItemAdapter.notifyDataSetChanged();

		ContentResolver resolver = getActivity().getContentResolver();
		Cursor cursor = resolver.query(ItemProviderContract.PLACE_CONTENTURI, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				
			}
		}
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		if (mItemAdapter != null) {
			long item_id = mItemAdapter.getItemId(position);
			PlaceDetailActivity.start(this.getActivity(), item_id);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.place_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			PlaceDetailActivity.start(getActivity());
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}


}
