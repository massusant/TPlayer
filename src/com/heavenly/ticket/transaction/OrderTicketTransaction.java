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
import android.util.Log;

import com.heavenly.ticket.model.LeftTicketState;
import com.heavenly.ticket.model.Seat;
import com.heavenly.ticket.util.BitmapUtils;
import com.heavenly.ticket.util.RpcHelper;

public class OrderTicketTransaction extends BaseTransaction {
	private static final String TAG = "OrderTicketTransaction";

	private static final String URL_QUERY_TOKEN = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=submutOrderRequest";
	private static final String URL_VERIFY_CODE = "https://dynamic.12306.cn/otsweb/passCodeAction.do?rand=randp";
	//private static final String URL_CONFIRM_PASSENGER = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init";
	private static final String URL_ORDER_QUENE = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do";
	private static final String URL_ORDER_CHECK = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=checkOrderInfo&rand=";
	private static final String URL_ORDER_SUBMIT = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=confirmSingleForQueue";
	
	private LeftTicketState mTicketState;
	private String mTravelDate;
	private String mVerifyCode;
	private String mToken;
	private String mTicket;
	private String mathRandom;
	
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
	
	public String obtainToken() {
		String ret = RpcHelper.doInvokeRpcByPost(URL_QUERY_TOKEN, obtainRequestHeader(), getParamForToken());
		mToken = getTOKEN(ret);
		mTicket = getTicket(ret);
		Log.d(TAG, "org.apache.struts.taglib.html.TOKEN=" + mToken);
		return mToken;
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
	public String makeOrder() {
		List<NameValuePair> param = null;
		try {
			param = getParamForOrder();
			param.add(new BasicNameValuePair("tFlag", "dc"));
			String url = URL_ORDER_CHECK + mVerifyCode;
			RpcHelper.setFollowRedirect(false);
			String ret = RpcHelper.doInvokeRpcByPost(url,
					obtainOrderCheckHeader(), param);
			JSONObject json = new JSONObject(ret);
			// {"checkHuimd":"Y","check608":"Y","msg":"","errMsg":"Y"}
			if (!"Y".equals(json.getString("errMsg"))
					|| !"Y".equals(json.get("checkHuimd"))
					|| !"Y".equals(json.get("check608"))) {
				return null;
			}
//			ret = RpcHelper.doInvokeRpc(URL_ORDER_QUENE, obtainOrderCheckHeader(), getQueneCountParams());
			param = getParamForOrder();
			String result = RpcHelper.doInvokeRpcByPost(URL_ORDER_SUBMIT + "&rand=" + mVerifyCode, obtainOrderCheckHeader(), param);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
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
		String[] text = matcher(body,
				"left_ticket[\\w\\W]*/>");
		if (text != null && text.length > 0) {
			return text[0].split("value=\"")[1].split("\"")[0];
		}
		return null;
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
	
//		station_train_code:D378
//		train_date:2013-01-23
//		seattype_num:
//		from_station_telecode:XHH
//		to_station_telecode:AOH
//		include_student:00
//		from_station_telecode_name:杭州
//		to_station_telecode_name:上海
//		round_train_date:2013-01-23
//		round_start_time_str:00:00--24:00
//		single_round_type:1
//		train_pass_type:QB
//		train_class_arr:D#
//		start_time_str:18:00--24:00
//		lishi:01:25
//		train_start_time:20:26
//		trainno4:580000D37807
//		arrive_time:21:51
//		from_station_name:杭州南
//		to_station_name:上海虹桥
//		from_station_no:11
//		to_station_no:15
//		ypInfoDetail:O*****0081M*****0072O*****3039
//		mmStr:C288747556E5EDCD7CF418C465AD7B05890A8DF675CE8399E1FE0F59
//		locationCode:G1
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
	
	private List<NameValuePair> getParamForOrder() {
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN", mToken)); // token
		param.add(new BasicNameValuePair("leftTicketStr", mTicket ));  // 余票信息
		param.add(new BasicNameValuePair("textfield", "中文或拼音首字母"));
		param.add(new BasicNameValuePair("checkbox0", "0"));
		param.addAll(getOrderParams(mTicketState));
//		for(int x=0; x< 5  ;x++){ // 最大支持5人
			int index = 0;
			int no = index +1;
			String passenger = "passenger_"+no+"_";
			
			param.add(new BasicNameValuePair("passengerTickets", Seat.SEAT_HARD.getCode()+",0,"+
					"1"+","+"文雪龙"+",1,"+"610481198805240037"+","+"13646815023"+",Y"));
			param.add(new BasicNameValuePair("oldPassengers","文雪龙"+",1,"+"610481198805240037"));
			param.add(new BasicNameValuePair(passenger+"seat", Seat.SEAT_HARD.getCode()));/*1--硬座,3--硬卧,4--软卧*///硬卧
			param.add(new BasicNameValuePair(passenger+"ticket", "1"));//成人票  /* 1-成人 2-儿童 3-学生 4-残军*/
			param.add(new BasicNameValuePair(passenger+"name", "文雪龙"));//姓名
			param.add(new BasicNameValuePair(passenger+"cardtype", "1"));/*二代身份证*///证件类型
			param.add(new BasicNameValuePair(passenger+"cardno", "610481198805240037"));//证件号码
			param.add(new BasicNameValuePair(passenger+"mobileno", "13646815023"));//手机号
//			param.add(new BasicNameValuePair(passenger+"seat_detail", ""));/*0-随机,3-上铺,2-中铺,1--下铺*///下铺
//			param.add(new BasicNameValuePair(passenger+"seat_detail_select", "1"));/*0-随机,3-上铺,2-中铺,1--下铺*///下铺
			// paras+=JavaUtils.toPostParam(submitOrderParams)+"&";
			param.add(new BasicNameValuePair("checkbox9", "Y"));
//		}
		for (int i = 0; i < 4; i++) {
			param.add(new BasicNameValuePair("oldPassengers", ""));
			param.add(new BasicNameValuePair("checkbox9", "Y"));
		}
		param.add(new BasicNameValuePair("orderRequest.reserve_flag", "A"));//支付方式
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
//		param.add(new BasicNameValuePair("oldPassengers", ""));
//		param.add(new BasicNameValuePair("checkbox9", "Y"));
		return param;
	}

}