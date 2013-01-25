package com.heavenly.ticket.model;

public class Passenger {
	
	/* {"first_letter":"SNOWDX",
	 * "isUserSelf":"",
	 * "mobile_no":"13646815023",
	 * "old_passenger_id_no":"",
	 * "old_passenger_id_type_code":"",
	 * "old_passenger_name":"",
	 * "passenger_flag":"0",
	 * "passenger_id_no":"610481198805240037",
	 * "passenger_id_type_code":"1",
	 * "passenger_id_type_name":"",
	 * "passenger_name":"文雪龙",
	 * "passenger_type":"1",
	 * "passenger_type_name":"",
	 * "recordCount":"3"} 
	 */

	private TicketType ticketType = TicketType.ADULT;
	private Seat seatType;
	private IdCardType idcardType = IdCardType.GEN_IDCARD_2;
	private String name;
	private String idcardCode;
	private String mobile;
	private boolean save;
	
	public TicketType getTicketType() {
		return ticketType;
	}
	public void setTicketType(TicketType ticketType) {
		this.ticketType = ticketType;
	}
	public Seat getSeatType() {
		return seatType;
	}
	public void setSeatType(Seat seatType) {
		this.seatType = seatType;
	}
	public IdCardType getIdcardType() {
		return idcardType;
	}
	public void setIdcardType(IdCardType idcardType) {
		this.idcardType = idcardType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdcardCode() {
		return idcardCode;
	}
	public void setIdcardCode(String idcardCode) {
		this.idcardCode = idcardCode;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public boolean isSave() {
		return save;
	}
	public void setSave(boolean save) {
		this.save = save;
	}
	
}
