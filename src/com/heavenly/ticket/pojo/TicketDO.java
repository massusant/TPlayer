package com.heavenly.ticket.pojo;

import java.util.List;


public class TicketDO {
	private String num;//车次
	private String start;//发站
	private String end;//到站
	private String startTime;//发站时间
	private String endTime;//到站时间
	private String useTime;//历时
	private int business;//商务座
	private int special;//特等座位
	private int first;//一等座
	private int second;//二等座
	private int superSoft;//高级软卧
	private int softSleeper;//软卧
	private int hardSleeprt;//硬卧
	private int sofeSite;//软座
	private int hardSite;//硬座
	private int noneSite;//无座
	private int other;//其他
	private String btn;
	private String[] meg ;
	private List<SkuDO> skusList;
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getUseTime() {
		return useTime;
	}
	public void setUseTime(String useTime) {
		this.useTime = useTime;
	}
	public int getBusiness() {
		return business;
	}
	public void setBusiness(int business) {
		this.business = business;
	}
	public int getSpecial() {
		return special;
	}
	public void setSpecial(int special) {
		this.special = special;
	}
	public int getFirst() {
		return first;
	}
	public void setFirst(int first) {
		this.first = first;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	public int getSuperSoft() {
		return superSoft;
	}
	public void setSuperSoft(int superSoft) {
		this.superSoft = superSoft;
	}
	public int getSoftSleeper() {
		return softSleeper;
	}
	public void setSoftSleeper(int softSleeper) {
		this.softSleeper = softSleeper;
	}
	public int getHardSleeprt() {
		return hardSleeprt;
	}
	public void setHardSleeprt(int hardSleeprt) {
		this.hardSleeprt = hardSleeprt;
	}
	public int getSofeSite() {
		return sofeSite;
	}
	public void setSofeSite(int sofeSite) {
		this.sofeSite = sofeSite;
	}
	public int getHardSite() {
		return hardSite;
	}
	public void setHardSite(int hardSite) {
		this.hardSite = hardSite;
	}
	public int getNoneSite() {
		return noneSite;
	}
	public void setNoneSite(int noneSite) {
		this.noneSite = noneSite;
	}
	public int getOther() {
		return other;
	}
	public void setOther(int other) {
		this.other = other;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getBtn() {
		return btn;
	}
	public void setBtn(String btn) {
		this.btn = btn;
	}
	public String[] getMeg(){
 		if(this.meg != null) {
			return meg;
		}
		if(this.btn == null){
			return null ;
		}
		String[] temp = btn.split("'");
		if(temp.length<8){
			return null;
		}
		temp = temp[7].split("#");
		if(temp.length != 14) {
			return null;
		}else {
			this.meg = temp;
			return this.meg;
		}
	}
	public List<SkuDO> getSkusList() {
		return skusList;
	}
	public void setSkusList(List<SkuDO> skusList) {
		this.skusList = skusList;
	}
}
