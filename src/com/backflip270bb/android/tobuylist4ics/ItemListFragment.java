package com.backflip270bb.android.tobuylist4ics;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.backflip270bb.android.tobuylist4ics.model.ItemProviderContract;

public class ItemListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	EditText editText;
	
	ItemCursorAdapter mItemAdapter;
	
	private static final String TAG = "ToBuyItemListFragment";
	
	private static final int TOBUYITEM_LOADER = 0;

	public ItemListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getLoaderManager().initLoader(TOBUYITEM_LOADER, null, this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		switch(loaderId) {
		case TOBUYITEM_LOADER:
			return new CursorLoader(this.getActivity(), ItemProviderContract.ITEM_CONTENTURI, null, null, null, null);
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

		mItemAdapter = new ItemCursorAdapter(getActivity(), null, true);
		setListAdapter(mItemAdapter);
		mItemAdapter.notifyDataSetChanged();

		ContentResolver resolver = getActivity().getContentResolver();
		Cursor cursor = resolver.query(ItemProviderContract.ITEM_CONTENTURI, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				
			}
		}
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		Log.d(TAG, "onListItemClick:id=" + id);
		ItemDetailActivity.start(getActivity(), id);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.item_list, menu);
		View view = menu.findItem(R.id.action_new).getActionView();
		editText = (EditText)view.findViewById(R.id.editText);
		ImageButton button = (ImageButton)view.findViewById(R.id.buttonOk);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = editText.getText().toString();
				if (true) {
					ContentValues values = new ContentValues();
					values.put(ItemProviderContract.Item.NAME_COLUMN, name);
					values.put(ItemProviderContract.Item.DATE_COLUMN, Calendar.getInstance().getTimeInMillis());
					getActivity().getContentResolver().insert(ItemProviderContract.ITEM_CONTENTURI, values);
					editText.setText(null);
					InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0); 
				} else {
					ItemDetailActivity.start(getActivity(), name);
				}
			}
		});
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_new:
			ItemDetailActivity.start(getActivity());
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
