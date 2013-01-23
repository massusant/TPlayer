package com.heavenly.ticket.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Train {

	private String id;
	private String name;
	private String startStationName;
	private String startTime;
	private String endStationName;
	private String endTime;
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getStartStationName() {
		return startStationName;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndStationName() {
		return endStationName;
	}

	public String getEndTime() {
		return endTime;
	}

	public static Train createFromJSONObject(JSONObject json) throws JSONException {
		Train train = new Train();
		train.id = json.getString("id");
		train.name = json.getString("value");
		train.startStationName = json.getString("start_station_name");
		train.endStationName = json.getString("end_station_name");
		train.startTime = json.getString("start_time");
		train.endTime = json.getString("end_time");
		return train;
	}
	
}
