package com.backflip270bb.android.tobuylist4ics.model;

import java.util.Calendar;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ToBuyItems")
public class ToBuyItem implements BaseColumns {
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField
	private String name;
	@DatabaseField
	private long date;
	@DatabaseField
	private String memo;
	@DatabaseField
	private Integer placeid;
	@DatabaseField
	private boolean shouldNotify;
	@DatabaseField
	private boolean isDone;
	
	public ToBuyItem() {
	}
	
	public ToBuyItem(String name) {
		super();
		this.name = name;
		this.date = Calendar.getInstance().getTimeInMillis();
	} 
	
	public ToBuyItem(String name, long date, String memo, Integer placeid,
			boolean shouldNotify) {
		super();
		this.name = name;
		this.date = date;
		this.memo = memo;
		this.placeid = placeid;
		this.shouldNotify = shouldNotify;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String text) {
		this.memo = text;
	}
	public Integer getPlaceId() {
		return placeid;
	}
	public void setPlaceid(Integer placeid) {
		this.placeid = placeid;
	}
	public boolean isShouldNotify() {
		return shouldNotify;
	}
	public void setShouldNotify(boolean shouldNotify) {
		this.shouldNotify = shouldNotify;
	}
	public boolean isDone() {
		return isDone;
	}
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}


}