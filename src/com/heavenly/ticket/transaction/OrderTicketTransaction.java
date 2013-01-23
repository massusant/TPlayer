package com.heavenly.ticket.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.util.Log;

import com.heavenly.ticket.model.LeftTicketState;
import com.heavenly.ticket.util.BitmapUtils;
import com.heavenly.ticket.util.RpcHelper;

public class OrderTicketTransaction extends BaseTransaction {
	private static final String TAG = "OrderTicketTransaction";

	private static final String URL_QUERY_TOKEN = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=submutOrderRequest";
	private static final String URL_VERIFY_CODE = "https://dynamic.12306.cn/otsweb/passCodeAction.do?rand=randp";
	private static final String URL_CONFIRM_PASSENGER = "https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init";
	
	
	private LeftTicketState mTicketState;
	private String mTravelDate;
	private String mVerifyCode;
	
	public void setTicketInfo(LeftTicketState ticketState, String date) {
		mTicketState = ticketState;
		mTravelDate = date;
	}
	
	@Override
	public BaseResponse doAction() {
		return null;
	}
	
	public String obtainToken() {
		String ret = RpcHelper.doInvokeRpcByPost(URL_QUERY_TOKEN, obtainRequestHeader(), getParamForToken());
		String token = getTOKEN(ret);
		Log.d(TAG, "org.apache.struts.taglib.html.TOKEN=" + token);
		return token;
	}
	
	public Bitmap refreshVerifyBitmap() {
		return BitmapUtils.getFromURL(URL_VERIFY_CODE, null);
	}

	public String makeOrder() {
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
	
//	protected HashMap<String, String> obtainVerifyCodeHeader() {
//		HashMap<String, String> header = super.obtainRequestHeader();
//		header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//		header.put("Referer",
//				"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
//		header.remove("X-Requested-With");
//		return header;
//	}

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
		Map<String, String> postOrderParam = new HashMap<String, String>();
		postOrderParam.putAll(getOrderParams(mTicketState));
		for(int x=0; x< 5  ;x++){ // 最大支持5人
			Map<String,String> submitOrderParams= new HashMap<String, String>();
			// PassengerDO passer = order.getPassengerDO().get(x);
			int y = x+1;
			String passenger = "passenger_"+y+"_";
			String checkbox = "checkbox"+x;
			submitOrderParams.put(checkbox,""+x);
			submitOrderParams.put("oldPassengers","姓名"+",1,"+"身份证号");
			submitOrderParams.put("passengerTickets", "席别代码"+",1,"+
					"上中下铺代码"+","+"姓名"+",1,"+"身份证号码"+","+"手机"+",Y");
			submitOrderParams.put(passenger+"seat", "席别代码"/*1--硬座,3--硬卧,4--软卧*/);//硬卧
			submitOrderParams.put(passenger+"seat_detail", "1"/*0-随机,3-上铺,2-中铺,1--下铺*/);//下铺
			submitOrderParams.put(passenger+"seat_detail_select", ""/*0-随机,3-上铺,2-中铺,1--下铺*/);//下铺
			submitOrderParams.put(passenger+"ticket", "1");//成人票  /* 1-成人 2-儿童 3-学生 4-残军*/
			submitOrderParams.put(passenger+"name", "");//姓名
			submitOrderParams.put(passenger+"cardtype", ""/*二代身份证*/);//证件类型
			submitOrderParams.put(passenger+"cardno", "");//证件号码
			submitOrderParams.put(passenger+"mobileno", "");//手机号
			// paras+=JavaUtils.toPostParam(submitOrderParams)+"&";
		}
		postOrderParam.put("org.apache.struts.taglib.html.TOKEN",getTOKEN("body")); // 余票
		postOrderParam.put("leftTicketStr", "getLeftTicketStr(body)" );  // 余票信息
		postOrderParam.put("textfield","中文或拼音首字母");
		postOrderParam.put("randCode", mVerifyCode);
		return null;
	}
	
	private Map<String,String> getOrderParams(LeftTicketState ticketState){
		Map<String,String> submitOrderParams= new HashMap<String, String>();
		submitOrderParams.put("orderRequest.train_date", mTravelDate);
		submitOrderParams.put("orderRequest.train_no",ticketState.getTrainNo4());
		submitOrderParams.put("orderRequest.station_train_code", ticketState.getTrainNoShow());
		submitOrderParams.put("orderRequest.from_station_telecode","");
		submitOrderParams.put("orderRequest.to_station_telecode", "");
		submitOrderParams.put("orderRequest.seat_type_code","");
		submitOrderParams.put("orderRequest.seat_detail_type_code","");
		submitOrderParams.put("orderRequest.ticket_type_order_num","");
		submitOrderParams.put("orderRequest.bed_level_order_num","000000000000000000000000000000");

		submitOrderParams.put("orderRequest.start_time", ticketState.getTimeGetOn());

		submitOrderParams.put("orderRequest.end_time", ticketState.getTimeGetOff());
		submitOrderParams.put("orderRequest.from_station_name", ticketState.getStationGetOn());
		submitOrderParams.put("orderRequest.to_station_name", ticketState.getStationGetOff());
		submitOrderParams.put("orderRequest.cancel_flag","1");
		submitOrderParams.put("orderRequest.id_mode","Y");
		submitOrderParams.put("checkbox9", "Y");
		submitOrderParams.put("orderRequest.reserve_flag", "A");//支付方式
		submitOrderParams.put("oldPassengers", "");
		return submitOrderParams;
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