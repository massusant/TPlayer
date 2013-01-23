package com.heavenly.ticket.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.util.Log;

import com.heavenly.ticket.R;
import com.heavenly.ticket.model.LeftTicketState;
import com.heavenly.ticket.util.RpcHelper;

public class LeftTicketTransaction extends BaseTransaction {
	static final String TAG = "LeftTicketTransaction";
	final String URL_LEFT_TICKET_QUERY = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do";
	
	public static class LeftTicketResponse extends BaseResponse {
		public ArrayList<LeftTicketState> data;
	}
	
	private Bundle bundle;
	
	public void setParams(Bundle data) {
		bundle = data;
	}
	
	@Override
	public BaseResponse doAction() {
		try {
			LeftTicketResponse response = new LeftTicketResponse();
			response.data = doQueryTicket();
			response.success = true;
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private ArrayList<LeftTicketState> doQueryTicket() throws Exception  {
		String ret = RpcHelper.doInvokeRpc(URL_LEFT_TICKET_QUERY,
				obtainRequestHeader(), getParamList());
		Log.d(TAG, "" + ret);
		ArrayList<LeftTicketState> list = LeftTicketState.createListFromHtml(ret);
		return list;
	}

//	intent.putExtra(getString(R.string.intent_key_from_station_code), startStationCode);
//	intent.putExtra(getString(R.string.intent_key_to_station_code), destStationCode);
//	intent.putExtra(getString(R.string.intent_key_departure_date), mDateStartOff);
//	intent.putExtra(getString(R.string.intent_key_departure_time), mTimeStartOff);
//	intent.putExtra(getString(R.string.intent_key_train_no), "");
//	intent.putExtra(getString(R.string.intent_key_include_student), "00");
//	intent.putExtra(getString(R.string.intent_key_train_class), "D#Z#T#K#QT#");
//	intent.putExtra(getString(R.string.intent_key_train_pass_type), "QB");
	
	@Override
	public List<NameValuePair> getParamList() {
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("method", "queryLeftTicket"));
		param.add(new BasicNameValuePair("seatTypeAndNum", ""));
//		param.add(new BasicNameValuePair("includeStudent", "00"));
//		param.add(new BasicNameValuePair("trainClass", "D#Z#T#K#QT#"));
//		param.add(new BasicNameValuePair("trainPassType", "QB"));
//		param.add(new BasicNameValuePair("orderRequest.train_no", ""));
//		param.add(new BasicNameValuePair("orderRequest.from_station_telecode", )));
//		param.add(new BasicNameValuePair("orderRequest.to_station_telecode", "XAY"));
//		param.add(new BasicNameValuePair("orderRequest.train_date", "2013-02-09"));
//		param.add(new BasicNameValuePair("orderRequest.start_time_str", "00:00--24:00"));
		Set<String> set = bundle.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String name = it.next();
			param.add(new BasicNameValuePair(name, bundle.getString(name)));
		}
		return param;
	}

	@Override
	protected HashMap<String, String> obtainRequestHeader() {
		HashMap<String, String> header = super.obtainRequestHeader();
		header.put("Accept", "text/plain, */*");
		header.put("Referer", "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init");
		return header;
	}
	

}
