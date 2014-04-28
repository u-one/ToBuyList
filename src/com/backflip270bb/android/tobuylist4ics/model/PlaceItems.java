package com.backflip270bb.android.tobuylist4ics.model;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

public class PlaceItems {
	private final String TAG = getClass().getSimpleName();
	private Context context;
	public PlaceItems(Context context) {
		this.context = context;
	}
	public List<PlaceItem> findAll() {
        ToBuyListItemsDBHelper helper = getHelper();
        try {
            Dao<PlaceItem, Integer> dao = helper.getDao(PlaceItem.class);
            return dao.queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, "Database error", e);
            return null;
        } finally {
            helper.close();
        }
    }
	
	public PlaceItem find(Integer id) {
		if (id == null || id == PlaceItem.ID_UNKNOWN) {
			return null;
		}
		ToBuyListItemsDBHelper helper = getHelper();
		PlaceItem item = null;
		try {
            Dao<PlaceItem, Integer> dao = helper.getDao(PlaceItem.class);
            item = dao.queryForId(id);
        } catch (SQLException e) {
            Log.e(TAG, "Database error", e);
        } finally {
            helper.close();
        }
		return item;
	}
	
	public void save(PlaceItem item) {
	    ToBuyListItemsDBHelper helper = getHelper();
	    try {
	        Dao<PlaceItem, Integer> dao = helper.getDao(PlaceItem.class);
	        dao.createOrUpdate(item);
	    } catch (SQLException e) {
	        Log.e(TAG, "Database error", e);
	    } finally {
	        helper.close();
	    }
	}
	
	public void clear() {
		ToBuyListItemsDBHelper helper = getHelper();
		Dao<PlaceItem, Integer> dao;
		try {
			dao = helper.getDao(PlaceItem.class);
			dao.delete(dao.queryForAll());
		} catch (SQLException e) {
			Log.e(TAG, "Database error", e);
			e.printStackTrace();
		} finally {
			helper.close();
		}
	}
	
	private ToBuyListItemsDBHelper getHelper() {
		return new ToBuyListItemsDBHelper(context);
	}
}

