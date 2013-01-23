package com.heavenly.ticket.transaction;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.text.TextUtils;

import com.heavenly.ticket.model.Train;
import com.heavenly.ticket.util.RpcHelper;

public class TrainTransaction {
	
	// https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=queryststrainall&date=2013-02-01&fromstation=SHH&tostation=EAY&starttime=00:00--24:00
//	private static String URL_QUERY_TRAIN = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do";
	private static String URL_QUERY_TRAIN = "http://dynamic.12306.cn/otsquery/query/queryRemanentTicketAction.do";

	public static ArrayList<Train> queryTrain(QueryTrainParam param) throws JSONException {
		ArrayList<NameValuePair> paramList = param.getParamList();
		paramList.add(new BasicNameValuePair("method", "queryststrainall"));
		String ret = RpcHelper.invokeRpc(URL_QUERY_TRAIN, null, paramList);
		if (TextUtils.isEmpty(ret)) {
			return null;
		}
		JSONArray array = new JSONArray(ret);
		if (array != null && array.length() > 0) {
			ArrayList<Train> trains = new ArrayList<Train>();
			for (int i = 0; i < array.length(); i++) {
				trains.add(Train.createFromJSONObject(array.getJSONObject(i)));
			}
			return trains;
		}
		return null;
	}
	
	public static ArrayList<Train> queryTrain(QueryTrainParam param,
			boolean[] selectedClasses) throws JSONException {
		ArrayList<Train> list = queryTrain(param);
		if (selectedClasses == null || selectedClasses.length == 5) {
			return list;
		}
		ArrayList<Train> typeList = new ArrayList<Train>();
		mainloop : for (Train train : list) {
			for (int i = 0; i < selectedClasses.length; i++) {
				if (!selectedClasses[i]) {
					continue;
				}
				switch (i) {
				case 0:
					if (train.getName().startsWith("G")
							|| train.getName().startsWith("D")) {
						typeList.add(train);
						continue mainloop; 
					} break;
				case 1:
					if (train.getName().startsWith("Z")) {
						typeList.add(train);
						continue mainloop;
					} break;
				case 2:
					if (train.getName().startsWith("T")) {
						typeList.add(train);
						continue mainloop;
					} break;
				case 3:
					if (train.getName().startsWith("K")) {
						typeList.add(train);
						continue mainloop;
					} break;
				default:
					if (!train.getName().startsWith("G") 
							&& !train.getName().startsWith("D")
							&& !train.getName().startsWith("Z")
							&& !train.getName().startsWith("T")
							&& !train.getName().startsWith("K")) {
						typeList.add(train);
					}
					continue mainloop;
				}
			}
		}
		
		return list;
	}
	
	public static class QueryTrainParam {
		String fromStation;
		String toStation;
		String date;
		String startTime;
		public QueryTrainParam(String from, String to, String date, String time) {
			fromStation = from;
			toStation = to;
			this.date = date;
			startTime = time;
		}
		public ArrayList<NameValuePair> getParamList() {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("fromstation", fromStation));
			params.add(new BasicNameValuePair("tostation", toStation));
			params.add(new BasicNameValuePair("date", date));
			params.add(new BasicNameValuePair("starttime", startTime));
			return params;
		}
	}
	
	
}