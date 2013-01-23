package com.heavenly.ticket.pojo;


public class PassengerDO {
	private String name ;
	private String cardid ; //身份证
	private String mobile ; //手机号
	/**
	* <option value="1">二代身份证</option>
	* <option value="2">一代身份证</option>
	* <option value="C">港澳通行证</option>
	* <option value="G">台湾通行证</option>
	*/
	private String cardtype ;//证件类型  
	
	public PassengerDO(String name ,String cardid,String mobile, String cardtype){
		this.name = name ;
		this.cardid =cardid;
		this.mobile = mobile;
		this.cardtype = cardtype;
	}
	public static  PassengerDO ceartPeoForWEI(){
		return new PassengerDO("weichao", "510725199003242416", "15116335086", "1");
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCardid() {
		return cardid;
	}
	public void setCardid(String cardid) {
		this.cardid = cardid;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
}
