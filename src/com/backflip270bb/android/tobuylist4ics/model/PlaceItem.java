package com.backflip270bb.android.tobuylist4ics.model;

import android.provider.BaseColumns;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "PlaceItems")
public class PlaceItem implements BaseColumns {
	public static final int ID_UNKNOWN = -1;
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField
	private String name;
	@DatabaseField
	private double lat;
	@DatabaseField
	private double lon;
	@DatabaseField
	private Integer distance;
	
	public PlaceItem() {
	}

	public PlaceItem(String name, double lat, double lon, Integer distance) {
		super();
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.distance = distance;
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

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}
}
