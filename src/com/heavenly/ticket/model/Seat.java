package com.heavenly.ticket.model;

import java.util.HashMap;

public enum Seat {
	GSEAT_VIP, GSEAT_SPECIAL, GSEAT_FIRST, GSEAT_SECOND, 
	SLEEPER_VIP, SLEEPER_SOFT, SLEEPER_HARD, 
	SEAT_SOFT, SEAT_HARD, SEAT_NONE, OTHERS, length;
	
	private static final String[] SEAT_NAME_ARRAY = { "商务座", "特等座", "一等座",
		"二等座", "高级软卧", "软卧", "硬卧", "软座", "硬座", "无座", "其他", "length" };
	private static final String[] SEAT_CODE_ARRAY = { "9", "P", "M", "O", "6",
		"4", "3", "2", "1", "-", "-" };
	
	private final static HashMap<String, Seat> CODE_SEAT_MAP = new HashMap<String, Seat>();
	
	static {
		for (int i = 0; i < Seat.OTHERS.ordinal(); i++) {
			CODE_SEAT_MAP.put(SEAT_CODE_ARRAY[i], Seat.values()[i]);
		}
	}
	
	public String toString() {
		return SEAT_NAME_ARRAY[this.ordinal()];
	}
	
	public String getCode() {
		return SEAT_CODE_ARRAY[this.ordinal()];
	}
	
	public static Seat createFromCode(String code) {
//		for (int i = 0; i < Seat.length.ordinal(); i++) {
//			if (SEAT_CODE_ARRAY[i].equalsIgnoreCase(code)) {
//				return Seat.values()[i];
//			}
//		}
//		return Seat.OTHERS;
		return CODE_SEAT_MAP.get(code);
	}
}