package com.heavenly.ticket.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.util.Log;

import com.heavenly.ticket.model.LeftTicketState;
import com.heavenly.ticket.util.RpcHelper;

public class LeftTicketTransaction extends BaseTransaction {
	static final String TAG = "LeftTicketTransaction";
	static final String URL_LEFT_TICKET_QUERY = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do";
	
	static final String[] keys = { "orderRequest.train_date",
			"orderRequest.from_station_telecode",
			"orderRequest.to_station_telecode", 
			"orderRequest.train_no",
			"trainPassType", 
			"trainClass", 
			"includeStudent", 
			"seatTypeAndNum",
			"orderRequest.start_time_str" };
	
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
//		String ret = RpcHelper
//				.doInvokeRpc(
//						"https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init",
//						null, null);
		String ret = RpcHelper.doInvokeRpc(URL_LEFT_TICKET_QUERY,
				obtainRequestHeader(), getParamList());
		Log.d(TAG, "" + ret);
		ArrayList<LeftTicketState> list = LeftTicketState.createListFromHtml(ret);
		return list;
	}
	
	@Override
	public List<NameValuePair> getParamList() {
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("method", "queryLeftTicket"));
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = bundle.getString(key);
			if (value != null) {
				param.add(new BasicNameValuePair(key, value));
			} else {
				param.add(new BasicNameValuePair(key, ""));
			}
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
