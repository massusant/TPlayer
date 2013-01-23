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
	private static final String URL_ORDER_CHECK = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=checkOrderInfo&rand=";
	private static final String URL_ORDER_SUBMIT = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=confirmSingleForQueueOrder";
	
	private LeftTicketState mTicketState;
	private String mTravelDate;
	private String mVerifyCode;
	private String mToken;
	
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
		Log.d(TAG, "org.apache.struts.taglib.html.TOKEN=" + mToken);
		return mToken;
	}
	
	public Bitmap refreshVerifyBitmap() {
		return BitmapUtils.getFromURL(URL_VERIFY_CODE, null);
	}

	public String makeOrder() {
		String url = URL_ORDER_CHECK + mVerifyCode;
		String ret = RpcHelper.doInvokeRpcByPost(url, obtainOrderCheckHeader(), getParamForOrder());
		try {
			JSONObject json = new JSONObject(ret);
			// {"checkHuimd":"Y","check608":"Y","msg":"","errMsg":"Y"}
			if (!"Y".equals(json.getString("errMsg"))
					|| !"Y".equals(json.get("checkHuimd"))
							|| !"Y".equals(json.get("check608"))) {
				return null;
			}
			ret = RpcHelper.doInvokeRpcByPost(URL_ORDER_SUBMIT, obtainOrderCheckHeader(), getParamForOrder());
			Log.d(TAG, "\n\n=====================================================");
			for (int i = 0; i < ret.length(); i += 2000) {
				if (ret.length() >= i + 2000) {
					Log.d(TAG, ret.substring(i, i + 2000));
				} else {
					Log.d(TAG, ret.substring(i, ret.length()));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
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
		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		header.put("Referer",
				"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
		return header;
	}

	private static String getTOKEN(String body){
		Matcher m = Pattern.compile("org.apache.struts.taglib.html.TOKEN[\\w\\W]*</div>").matcher(body);
		while (m.find()) {
			int n = m.groupCount();
			String[] ss = new String[n + 1];
			for (int i = 0; i <= n; i++) {
				ss[i] = m.group(i);
			}
			return ss[0].split("value=\"")[1].split("\"><")[0];
		}
		return null;
	}
	
	

	@Override
	public List<NameValuePair> getParamList() {
		
		return null;
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
		param.addAll(getOrderParams(mTicketState));
		param.add(new BasicNameValuePair("checkbox0", "0"));
//		for(int x=0; x< 5  ;x++){ // 最大支持5人
			int index = 0;
			int no = index +1;
			String passenger = "passenger_"+no+"_";
			
			param.add(new BasicNameValuePair("passengerTickets", Seat.SEAT_HARD.getCode()+",0,"+
					"1"+","+"文雪龙"+",1,"+"610481198805240037"+","+"13646815023"+",Y"));
			param.add(new BasicNameValuePair("oldPassengers","文雪龙"+",1,"+"610481198805240037"));
			param.add(new BasicNameValuePair(passenger+"seat", Seat.SEAT_HARD.getCode()));/*1--硬座,3--硬卧,4--软卧*///硬卧
//			param.add(new BasicNameValuePair(passenger+"seat_detail", ""));/*0-随机,3-上铺,2-中铺,1--下铺*///下铺
//			param.add(new BasicNameValuePair(passenger+"seat_detail_select", "1"));/*0-随机,3-上铺,2-中铺,1--下铺*///下铺
			param.add(new BasicNameValuePair(passenger+"ticket", "1"));//成人票  /* 1-成人 2-儿童 3-学生 4-残军*/
			param.add(new BasicNameValuePair(passenger+"name", "文雪龙"));//姓名
			param.add(new BasicNameValuePair(passenger+"cardtype", "1"));/*二代身份证*///证件类型
			param.add(new BasicNameValuePair(passenger+"cardno", "610481198805240037"));//证件号码
			param.add(new BasicNameValuePair(passenger+"mobileno", "13646815023"));//手机号
			// paras+=JavaUtils.toPostParam(submitOrderParams)+"&";
//		}
		for (int i = 0; i < 4; i++) {
			param.add(new BasicNameValuePair("checkbox9", "Y"));
			param.add(new BasicNameValuePair("oldPassengers", ""));
		}
		param.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN",mToken)); // token
		param.add(new BasicNameValuePair("leftTicketStr", mTicketState.getYpInfoDetail() ));  // 余票信息
		param.add(new BasicNameValuePair("textfield","中文或拼音首字母"));
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
		param.add(new BasicNameValuePair("orderRequest.seat_type_code",Seat.SEAT_HARD.getCode()));
//		param.add(new BasicNameValuePair("orderRequest.seat_detail_type_code",""));
		param.add(new BasicNameValuePair("orderRequest.ticket_type_order_num",""));
		param.add(new BasicNameValuePair("orderRequest.bed_level_order_num","000000000000000000000000000000"));
		param.add(new BasicNameValuePair("orderRequest.start_time", ticketState.getTimeGetOn()));
		param.add(new BasicNameValuePair("orderRequest.end_time", ticketState.getTimeGetOff()));
		param.add(new BasicNameValuePair("orderRequest.from_station_name", ticketState.getStationGetOn()));
		param.add(new BasicNameValuePair("orderRequest.to_station_name", ticketState.getStationGetOff()));
		param.add(new BasicNameValuePair("orderRequest.cancel_flag","1"));
		param.add(new BasicNameValuePair("orderRequest.id_mode","Y"));
		param.add(new BasicNameValuePair("orderRequest.reserve_flag", "A"));//支付方式
//		param.add(new BasicNameValuePair("oldPassengers", ""));
//		param.add(new BasicNameValuePair("checkbox9", "Y"));
		return param;
	}
	
	private static String checkLocation(String httpBody) {
		String locationUrl = null;
		// 2.
		String bodyLocationStr = "";
		if (httpBody.length() > 5120) {
			bodyLocationStr = httpBody.substring(0, 5120);// 太长则截取部分内容
		} else {
			bodyLocationStr = httpBody;
		}
		bodyLocationStr = bodyLocationStr.replaceAll("<!--(?s).*?-->", "")
				.replaceAll("['\"]", "");// 去除注释和引号部分

		int metaLocation = -1;
		metaLocation = bodyLocationStr.toLowerCase().indexOf(
				"http-equiv=refresh");
		if (metaLocation != -1) {
			String locationPart = bodyLocationStr.substring(metaLocation,
					bodyLocationStr.indexOf(">", metaLocation));
			metaLocation = locationPart.toLowerCase().indexOf("url");
			if (metaLocation != -1) {
				// 假定url=...是在 > 之前最后的部分
				locationUrl = locationPart.substring(metaLocation + 4,
						locationPart.length()).replaceAll("\\s+[^>]*", "");
				return locationUrl;
			}
		}
		// 3.
		Matcher locationMath = Pattern
				.compile(
						"(?s)<script.{0,50}?>\\s*((document)|(window)|(this))\\.location(\\.href)?\\s*=")
				.matcher(httpBody.toLowerCase());
		if (locationMath.find()) {
			String[] cs = httpBody.substring(locationMath.end()).trim()
					.split("[> ;<]");
			locationUrl = cs[0];
			cs = null;
			return locationUrl;
		}
		// 没有转向
		return null;
	}	
}