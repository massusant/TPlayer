package com.heavenly.ticket.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.text.Html;
import android.text.TextUtils;

import com.heavenly.ticket.util.SeatNumArray;

public class LeftTicketState implements Serializable {
	private static final long serialVersionUID = -4645520009114915491L;
	
	private String mStationGetOn;
	private String mStationGetOff;
	private boolean mTrainBeginStation;
	private boolean mTrainEndStation;
	private String mTimeGetOn;
	private String mTimeGetOff;
	private String mTripTimeLast;
	private String mTrainNoShow;
	
	private boolean mBookable = false;
	
	private String pTrainNo4;
	private String pFromStationCode;
	private String pToStationCode;
	private String pFromStationNo;
	private String pToStationNo;
	private String pYpInfoDetail;
	private String pMmStr;
	private String pLocationCode;
	
	private SeatNumArray mSeatNumMap;
	private String mSeatNumString;
	
	private static final String[] SEAT_NAME_ARRAY = { "商务座", "特等座", "一等座",
		"二等座", "高级软卧", "软卧", "硬卧", "软座", "硬座", "无座", "其他", "length" };
	
	private static final String[] SEAT_CODE_ARRAY = { "9", "P", "M", "O", "6",
			"4", "3", "2", "1", "-", "-" };
	
	public static enum Seat {
		GSEAT_VIP, GSEAT_SPECIAL, GSEAT_FIRST, GSEAT_SECOND, 
		SLEEPER_VIP, SLEEPER_SOFT, SLEEPER_HARD, 
		SEAT_SOFT, SEAT_HARD, NONE_SEAT, OTHERS, length;
		
		public String toString() {
			return SEAT_NAME_ARRAY[this.ordinal()];
		}
		
		public String getCode() {
			return SEAT_CODE_ARRAY[this.ordinal()];
		}
		
		public static Seat createFromCode(String code) {
			for (int i = 0; i < Seat.length.ordinal(); i++) {
				if (SEAT_CODE_ARRAY[i].equalsIgnoreCase(code)) {
					return Seat.values()[i];
				}
			}
			return Seat.OTHERS;
		}
	}
	
	
	public int getLeftNumBySeat(Seat seat) {
		if (mSeatNumMap == null) {
			return SEAT_NOT_EXIST;
		}
		return mSeatNumMap.get(seat.ordinal(), SEAT_NOT_EXIST);
	}
	
	public static ArrayList<LeftTicketState> createListFromHtml(String html) throws Exception {
		if (TextUtils.isEmpty(html)) {
			return null;
		}
		String[] slist = html.split("</a>");
		if (slist == null) {
			throw new Exception("parse ticket info exception");
		}
		ArrayList<LeftTicketState> list = new ArrayList<LeftTicketState>(slist.length);
		for (int i = 0; i < slist.length; i++) {
			list.add(LeftTicketState.createFromHtml(slist[i]));
		}
		return list;
	}
	
	public static LeftTicketState createFromHtml(String html) throws Exception {
		html = html.replaceAll("&nbsp;", "");
		String trainSummary = Html.fromHtml(html).toString();
		
		String[] detail = html.split(",");
		String[] info = trainSummary.split(",");
		if (info == null || info.length < 16) {
			return null;
		}
		LeftTicketState state = new LeftTicketState();
		state.mTrainNoShow = info[1];
		state.mTripTimeLast = info[4];
		state.parseLeftSeatNum(info);
		
		String[] stationInfo = detail[2].split("<br>");
		state.mTrainBeginStation = stationInfo[0].startsWith("<img ");
		state.mTimeGetOn = stationInfo[1];
		if (state.mTrainBeginStation) {
			int offset = stationInfo[0].indexOf(">");
			state.mStationGetOn = stationInfo[0].substring(offset + 1); 
		} else {
			state.mStationGetOn = stationInfo[0];
		}
		
		stationInfo = detail[3].split("<br>");
		state.mTrainEndStation = stationInfo[0].startsWith("<img ");
		state.mTimeGetOff = stationInfo[1];
		if (state.mTrainEndStation) {
			int offset = stationInfo[0].indexOf(">");
			state.mStationGetOff = stationInfo[0].substring(offset + 1); 
		} else {
			state.mStationGetOff = stationInfo[0];
		}
		
		if (state.mBookable) {
			state.parseOrderParams(detail[16]);
		}
		return state;
	}
	
	private static final int OFFSET_LEFT_NUM = 5;
	private static final int SEAT_ENOUGHT = -1;
	private static final int SEAT_NOT_EXIST = -2;
	private static final int SEAT_NOT_ON_SEAL = -3;
	
	private void parseLeftSeatNum(String[] info) {
		if (info == null || info.length < OFFSET_LEFT_NUM
				+ Seat.length.ordinal()) {
			return;
		}
		mSeatNumMap = new SeatNumArray();
		StringBuilder sb = new StringBuilder();
		for (int i = OFFSET_LEFT_NUM; i < OFFSET_LEFT_NUM
				+ Seat.length.ordinal(); i++) {
			String dataStr = info[i];
			if ("--".equals(dataStr)) {
				continue;
			}
			if ("*".equals(dataStr)) {
				mSeatNumMap.put(i - OFFSET_LEFT_NUM, SEAT_NOT_ON_SEAL);
				continue;
			}
			if ("有".equals(dataStr)) {
				mSeatNumMap.put(i - OFFSET_LEFT_NUM, SEAT_ENOUGHT);
				mBookable = true;
				sb.append(Seat.values()[i - OFFSET_LEFT_NUM].toString())
						.append(":").append(dataStr).append("|");
				continue;
			}
			if ("无".equals(dataStr)) {
				mSeatNumMap.put(i - OFFSET_LEFT_NUM, 0);
				continue;
			}
			try {
				int num = Integer.parseInt(dataStr);
				mBookable = num > 0;
				mSeatNumMap.put(i - OFFSET_LEFT_NUM, num);
				sb.append(Seat.values()[i - OFFSET_LEFT_NUM].toString())
						.append(":").append(dataStr).append("|");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
			mSeatNumString = sb.toString();
		} else {
			mSeatNumString = "无票";
		}
	}
	
	private void parseOrderParams(String actionStr) throws Exception {
		String[] values = actionStr.split("'");
		if (values == null || values.length < 8) {
			throw new Exception("parse ticket info exception");
		}
		values = values[7].split("#");
		if (values == null || values.length < 14) {
			throw new Exception("parse ticket info exception");
		}
		
		pTrainNo4 = values[3];
		pFromStationCode = values[4];
		pToStationCode = values[5];
		pFromStationNo = values[9];
		pToStationNo = values[10];
		pYpInfoDetail = values[11];
		pMmStr = values[12];
		pLocationCode = values[13];
	}
	
	@Override
	public String toString() {
		return mTrainNoShow + " > " + (mBookable ? mSeatNumString : "no ticket");
	}
	
	public String getStationGetOn() {
		return mStationGetOn;
	}
	public String getStationGetOff() {
		return mStationGetOff;
	}
	public boolean ismTrainBeginStation() {
		return mTrainBeginStation;
	}
	public boolean ismTrainEndStation() {
		return mTrainEndStation;
	}
	public String getTimeGetOn() {
		return mTimeGetOn;
	}
	public String getTimeGetOff() {
		return mTimeGetOff;
	}
	public String getTripTimeLast() {
		return mTripTimeLast;
	}
	public String getTrainNoShow() {
		return mTrainNoShow;
	}
	public boolean isBookable() {
		return mBookable;
	}
	public String getTrainNo4() {
		return pTrainNo4;
	}
	public String getFromStationCode() {
		return pFromStationCode;
	}
	public String getToStationCode() {
		return pToStationCode;
	}
	public String getFromStationNo() {
		return pFromStationNo;
	}
	public String getToStationNo() {
		return pToStationNo;
	}
	public String getYpInfoDetail() {
		return pYpInfoDetail;
	}
	public String getMmStr() {
		return pMmStr;
	}
	public String getLocationCode() {
		return pLocationCode;
	}
	public String getsSeatNumString() {
		return mSeatNumString;
	}
	
}