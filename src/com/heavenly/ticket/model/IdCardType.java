package com.heavenly.ticket.model;

public enum IdCardType {
	
	GEN_IDCARD_2, GEN_IDCARD_1, HK_CARD, TW_CARD, PASSPORT;
	
	public static final String[] NAME = { "二代身份证", "一代身份证", 
			"港澳通行证", "台湾通行证", "护照" };
	
	public static final String[] CODE = {"1", "2", "C", "G", "B"};
	
	@Override
	public String toString() {
		return NAME[ordinal()];
	}
	
	public String getCode() {
		return CODE[ordinal()];
	}
	
	public static TicketType createFromCode(String code) {
		for (int i = 0; i < CODE.length; i++) {
			if (CODE[i].equalsIgnoreCase(code)) {
				return TicketType.values()[i];
			}
		}
		return TicketType.values()[0];
	}
}
