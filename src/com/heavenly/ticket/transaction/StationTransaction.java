package com.heavenly.ticket.transaction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.heavenly.ticket.R;
import com.heavenly.ticket.model.Station;

public class StationTransaction {
	
	public static ArrayList<Station> FULL_STATION_LIST;

	public static ArrayList<Station> initFullList(Context context) {
		if (FULL_STATION_LIST != null && !FULL_STATION_LIST.isEmpty()) {
			return FULL_STATION_LIST;
		}
		ArrayList<Station> list = null;
		try {
			InputStream in = context.getResources().openRawResource(
					R.raw.station_name_java);
			InputStreamReader isReader = new InputStreamReader(in);
			BufferedReader bfReader = new BufferedReader(isReader);
			String data = bfReader.readLine();
			if (!TextUtils.isEmpty(data)) {
				String[] values = data.split("@");
				if (values != null && values.length > 0) {
					list = new ArrayList<Station>();
					for (int i = 0; i < values.length; i++) {
						if (!TextUtils.isEmpty(values[i])) {
							list.add(Station.create(values[i]));
						}
					}
					FULL_STATION_LIST = list;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
