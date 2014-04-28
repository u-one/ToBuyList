package com.backflip270bb.android.tobuylist4ics.model;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

public class ToBuyItems {
	private final String TAG = getClass().getSimpleName();
	private Context context;
	public ToBuyItems(Context context) {
		this.context = context;
	}
	public List<ToBuyItem> findAll() {
        ToBuyListItemsDBHelper helper = new ToBuyListItemsDBHelper(context);
        try {
            Dao<ToBuyItem, Integer> dao = helper.getDao(ToBuyItem.class);
            return dao.queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, "Database error", e);
            return null;
        } finally {
            helper.close();
        }
    }
	
	public void save(ToBuyItem item) {
	    ToBuyListItemsDBHelper helper = new ToBuyListItemsDBHelper(context);
	    try {
	        Dao<ToBuyItem, Integer> dao = helper.getDao(ToBuyItem.class);
	        dao.createOrUpdate(item);
	    } catch (SQLException e) {
	        Log.e(TAG, "Database error", e);
	    } finally {
	        helper.close();
	    }
	}
	
	public void clear() {
		ToBuyListItemsDBHelper helper = getHelper();
		Dao<ToBuyItem, Integer> dao;
		try {
			dao = helper.getDao(ToBuyItem.class);
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
