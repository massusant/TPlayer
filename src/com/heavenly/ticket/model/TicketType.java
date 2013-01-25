package com.heavenly.ticket.model;

public enum TicketType {

	ADULT, CHILD, STUDENT, DISABILITY;
	
	public static final String[] NAME = {"成人票", "儿童票", "学生票", "残军票"};
	
	public static final String[] CODE = {"1", "2", "3", "4"};
	
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
