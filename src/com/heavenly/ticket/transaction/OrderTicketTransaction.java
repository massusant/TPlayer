package com.heavenly.ticket.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.heavenly.ticket.model.LeftTicketState;
import com.heavenly.ticket.model.Passenger;
import com.heavenly.ticket.model.Seat;
import com.heavenly.ticket.util.BitmapUtils;
import com.heavenly.ticket.util.RpcHelper;

public class OrderTicketTransaction extends BaseTransaction {
	private static final String TAG = "OrderTicketTransaction";

	private static final String URL_QUERY_TOKEN = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=submutOrderRequest";
	private static final String URL_VERIFY_CODE = "https://dynamic.12306.cn/otsweb/passCodeAction.do?rand=randp";
	private static final String URL_ORDER_CHECK = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=checkOrderInfo&rand=";
	private static final String URL_ORDER_QUENE = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do";
	private static final String URL_ORDER_SUBMIT = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=confirmSingleForQueue";
	
	private LeftTicketState mTicketState;
	private String mTravelDate;
	private String mVerifyCode;
	private String mToken;
	private String mTicket;
	private String mathRandom;
	
	private BaseResponse mResponse;
	
	public void setTicketInfo(LeftTicketState ticketState, String date) {
		mTicketState = ticketState;
		mTravelDate = date;
	}
	
	public void setVerifyCode(String code) {
		mVerifyCode = code;
	}
	
	
	@Override
	public BaseResponse doAction() {
		return null;
	}
	
	public String getTicket() {
		return mTicket;
	}
	
	public BaseResponse obtainToken() {
		if (mResponse == null) {
			mResponse = new BaseResponse();
		}
		String ret = RpcHelper.doInvokeRpcByPost(URL_QUERY_TOKEN, obtainRequestHeader(), getParamForToken());
		if (TextUtils.isEmpty(ret) || ret.length() < 11264) {
			mResponse.success = false;
			mResponse.msg = "TOKEN获取失败";
			return mResponse;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(ret.trim());
		String msg = sb.subSequence(3076, 11264).toString();
		Log.d(TAG, "subs==========");
		Log.d(TAG, msg);
		mToken = getTOKEN(msg);
		if (TextUtils.isEmpty(mToken)) {
			mResponse.success = false;
			mResponse.msg = "TOKEN获取失败";
			return mResponse;
		}
		
		String tmpTicket = getTicket(msg);
		if (!TextUtils.isEmpty(tmpTicket)) {
			mTicket = tmpTicket;
		}
		Log.d(TAG, "org.apache.struts.taglib.html.TOKEN=" + mToken);
		Log.d(TAG, "ticket=" + mTicket);
		return mResponse;
	}
	
	public Bitmap refreshVerifyBitmap() {
		mathRandom = String.valueOf(Math.random());
		Map<String, String> header = super.obtainRequestHeader();
		header.put("Accept", "*/*");
		header.put("Referer",
				"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
		return BitmapUtils.getFromURL(URL_VERIFY_CODE + "&" + mathRandom, header, null);
	}

	boolean checked = false;
	public BaseResponse makeOrder(List<Passenger> passengers) {
		List<NameValuePair> param = null;
		try {
			param = getParamForOrder(passengers);
			param.add(new BasicNameValuePair("tFlag", "dc"));
			String url = URL_ORDER_CHECK + mVerifyCode;
			String ret = RpcHelper.doInvokeRpcByPost(url,
					obtainOrderCheckHeader(), param);
			if (TextUtils.isEmpty(ret)) {
				mResponse.success = false;
				mResponse.msg = "订单校验失败！";
				return mResponse;
			}
			JSONObject json = new JSONObject(ret);
			// {"checkHuimd":"Y","check608":"Y","msg":"","errMsg":"Y"}
			if (!"Y".equals(json.getString("errMsg"))
					|| !"Y".equals(json.get("checkHuimd"))
					|| !"Y".equals(json.get("check608"))) {
				mResponse.success = false;
				mResponse.msg = json.getString("errMsg");
				return mResponse;
			}
			ret = RpcHelper.doInvokeRpc(URL_ORDER_QUENE, obtainOrderQueueHeader(), getQueneCountParams());
			if (TextUtils.isEmpty(ret)) return null;
			json = new JSONObject(ret);
			mTicket = json.optString("ticket", mTicket);
			param = getParamForOrder(passengers);
			String result = RpcHelper.doInvokeRpcByPost(URL_ORDER_SUBMIT, obtainOrderCheckHeader(), param);
			if (TextUtils.isEmpty(result)) {
				return null;
			}
			json = new JSONObject(result);
			ret = json.getString("errMsg");
			if (!"Y".equals(ret)) {
				mResponse.success = false;
				mResponse.msg = ret;
				return mResponse;
			}
			return mResponse;
		} catch (JSONException e) {
			e.printStackTrace();
			mResponse.success = false;
			mResponse.msg = "数据解析错误！";
		}
		mResponse.success = false;
		return mResponse;
	}
	
	@Override
	protected HashMap<String, String> obtainRequestHeader() {
		HashMap<String, String> header = super.obtainRequestHeader();
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		header.put("Referer",
				"https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init");
		header.remove("X-Requested-With");
		return header;
	}
	
	protected HashMap<String, String> obtainOrderQueueHeader() {
		HashMap<String, String> header = super.obtainRequestHeader();
		header.put("Accept", "application/json,text/javascript,*/*");
		header.remove("Origin");
		header.put("Referer",
				"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
		return header;
	}
	
	protected HashMap<String, String> obtainOrderCheckHeader() {
		HashMap<String, String> header = super.obtainRequestHeader();
		header.put("Accept", "application/json,text/javascript,*/*");
		header.put("Referer",
				"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
		return header;
	}

	private String getTOKEN(String body){
		String[] text = matcher(body,
				"org.apache.struts.taglib.html.TOKEN[\\w\\W]*</div>");
		if (text != null && text.length > 0) {
			return text[0].split("value=\"")[1].split("\"><")[0];
		}
		return null;
	}
	
	private String getTicket(String body) {
		int offset = body.indexOf("left_ticket");
		if (offset < 0) {
			return null;
		}
		body = body.substring(offset);
		String[] values = body.split("value=\"");
		return values[1].split("\"")[0];
	}
	
	private String[] matcher(String body, String pattern) {
		Matcher m = Pattern.compile(pattern).matcher(body);
		while (m.find()) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
			}
			return ss;
		}
		return null;
	}

	@Override
	public List<NameValuePair> getParamList() {
		
		return null;
	}
	
	private List<NameValuePair> getQueneCountParams() {
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("method", "getQueueCount"));
		param.add(new BasicNameValuePair("train_date", mTravelDate));
		param.add(new BasicNameValuePair("train_no", mTicketState.getTrainNo4()));
		param.add(new BasicNameValuePair("station", mTicketState.getTrainNoShow()));
		param.add(new BasicNameValuePair("seat", Seat.SEAT_HARD.getCode()));
		param.add(new BasicNameValuePair("from", mTicketState.getFromStationCode()));
		param.add(new BasicNameValuePair("to", mTicketState.getToStationCode()));
		param.add(new BasicNameValuePair("ticket", mTicket));
		return param;
	}
	
	private List<NameValuePair> getParamForToken() {
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("station_train_code", mTicketState.getTrainNoShow()));
		list.add(new BasicNameValuePair("round_start_time_str", "00:00--24:00"));
		list.add(new BasicNameValuePair("train_date", mTravelDate)); // XXX
		list.add(new BasicNameValuePair("round_train_date", mTravelDate));
		list.add(new BasicNameValuePair("seattype_num", ""));
		list.add(new BasicNameValuePair("train_class_arr", "QB#D#Z#T#K#QT#"));
		list.add(new BasicNameValuePair("start_time_str", "00:00--24:00"));
		list.add(new BasicNameValuePair("include_student", "00"));
		list.add(new BasicNameValuePair("from_station_telecode_name", mTicketState.getStationGetOn()));
		list.add(new BasicNameValuePair("to_station_telecode_name", mTicketState.getStationGetOff()));
		list.add(new BasicNameValuePair("single_round_type", "1"));
		list.add(new BasicNameValuePair("train_pass_type", "QB"));
		list.add(new BasicNameValuePair("from_station_telecode", mTicketState.getFromStationCode()));
		list.add(new BasicNameValuePair("to_station_telecode", mTicketState.getToStationCode()));
		list.add(new BasicNameValuePair("lishi", mTicketState.getTripTimeLast()));
		list.add(new BasicNameValuePair("train_start_time", mTicketState.getTimeGetOn()));
		list.add(new BasicNameValuePair("trainno4", mTicketState.getTrainNo4()));
		list.add(new BasicNameValuePair("arrive_time", mTicketState.getTimeGetOff()));
		list.add(new BasicNameValuePair("from_station_name", mTicketState.getStationGetOn()));
		list.add(new BasicNameValuePair("to_station_name", mTicketState.getStationGetOff()));
		list.add(new BasicNameValuePair("from_station_no", mTicketState.getFromStationNo()));
		list.add(new BasicNameValuePair("to_station_no", mTicketState.getToStationNo()));
		list.add(new BasicNameValuePair("ypInfoDetail", mTicketState.getYpInfoDetail()));
		list.add(new BasicNameValuePair("mmStr", mTicketState.getMmStr()));
		list.add(new BasicNameValuePair("locationCode", mTicketState.getLocationCode()));
		return list;
	}
	
	private List<NameValuePair> getParamForOrder(List<Passenger> passengers) {
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN",
				mToken)); // token
		param.add(new BasicNameValuePair("leftTicketStr", mTicket)); // 余票信息
		param.add(new BasicNameValuePair("textfield", "中文或拼音首字母"));
		param.add(new BasicNameValuePair("checkbox0", "0"));
		param.addAll(getOrderParams(mTicketState));
		for (int i = 0; i < passengers.size(); i++) { // 最大支持5人
			int no = i + 1;
			Passenger item = passengers.get(i);
			String passenger = "passenger_" + no + "_";
			param.add(new BasicNameValuePair("passengerTickets", item
					.getSeatType().getCode() + ",0,"
					+ item.getTicketType().getCode() + ","
					+ item.getName() + ","
					+ item.getIdcardType().getCode() + ","
					+ item.getIdcardCode() + "," + item.getMobile() + ",Y"));
			param.add(new BasicNameValuePair("oldPassengers", item.getName()
					+ "," + item.getIdcardType().getCode() + ","
					+ item.getIdcardCode()));
			param.add(new BasicNameValuePair(passenger + "seat", item
					.getSeatType().getCode()));/* 1--硬座,3--硬卧,4--软卧 */// 硬卧
			param.add(new BasicNameValuePair(passenger + "ticket", item
					.getTicketType().getCode()));// 成人票 /* 1-成人 2-儿童 3-学生 4-残军*/
			param.add(new BasicNameValuePair(passenger + "name", item.getName()));// 姓名
			param.add(new BasicNameValuePair(passenger + "cardtype", item
					.getIdcardType().getCode()));/* 二代身份证 */// 证件类型
			param.add(new BasicNameValuePair(passenger + "cardno", item
					.getIdcardCode()));// 证件号码
			param.add(new BasicNameValuePair(passenger + "mobileno", item
					.getMobile()));// 手机号
			// param.add(new BasicNameValuePair(passenger+"seat_detail",
			// ""));/*0-随机,3-上铺,2-中铺,1--下铺*///下铺
			// param.add(new BasicNameValuePair(passenger+"seat_detail_select",
			// "1"));/*0-随机,3-上铺,2-中铺,1--下铺*///下铺
			// paras+=JavaUtils.toPostParam(submitOrderParams)+"&";
			param.add(new BasicNameValuePair("checkbox9", "Y"));
		}
		for (int i = 0; i < 5 - passengers.size(); i++) {
			param.add(new BasicNameValuePair("oldPassengers", ""));
			param.add(new BasicNameValuePair("checkbox9", "Y"));
		}
		param.add(new BasicNameValuePair("orderRequest.reserve_flag", "A"));// 支付方式
		param.add(new BasicNameValuePair("randCode", mVerifyCode));
		return param;
	}
	
	private List<NameValuePair> getOrderParams(LeftTicketState ticketState){
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("orderRequest.train_date", mTravelDate));
		param.add(new BasicNameValuePair("orderRequest.train_no",ticketState.getTrainNo4()));
		param.add(new BasicNameValuePair("orderRequest.station_train_code", ticketState.getTrainNoShow()));
		param.add(new BasicNameValuePair("orderRequest.from_station_telecode",ticketState.getFromStationCode()));
		param.add(new BasicNameValuePair("orderRequest.to_station_telecode", ticketState.getToStationCode()));
		param.add(new BasicNameValuePair("orderRequest.seat_type_code", "")); // XXX 更改为空字符串
//		param.add(new BasicNameValuePair("orderRequest.seat_detail_type_code",""));
		param.add(new BasicNameValuePair("orderRequest.ticket_type_order_num",""));
		param.add(new BasicNameValuePair("orderRequest.bed_level_order_num","000000000000000000000000000000"));
		param.add(new BasicNameValuePair("orderRequest.start_time", ticketState.getTimeGetOn()));
		param.add(new BasicNameValuePair("orderRequest.end_time", ticketState.getTimeGetOff()));
		param.add(new BasicNameValuePair("orderRequest.from_station_name", ticketState.getStationGetOn()));
		param.add(new BasicNameValuePair("orderRequest.to_station_name", ticketState.getStationGetOff()));
		param.add(new BasicNameValuePair("orderRequest.cancel_flag","1"));
		param.add(new BasicNameValuePair("orderRequest.id_mode","Y"));
		return param;
	}

}