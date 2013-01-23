package com.heavenly.ticket.pojo;

import java.util.ArrayList;
import java.util.List;

public class OrderDO {
	/**
	 * 一个准确的车票信息
	 */
	private String trainDate ;//哪天下单
	private String start ;
	private String end ;
	private String trainNum ; //车次
	private String seat ;//座位类型 硬卧 硬座
	private String seatDetai;//上下铺
	private List<PassengerDO> passengerDO = new ArrayList<PassengerDO>();//购票的人
	public String getTrainDate() {
		return trainDate;
	}
	public void setTrainDate(String trainDate) {
		this.trainDate = trainDate;
	}
	public OrderDO(String start , String end ,String date ,String num,String seat ,String seatDetai){
		this.start = start;
		this.end = end ;
		this.trainDate = date; 
		this.trainNum = num ;
		this.seat = seat ;
		this.seatDetai = seatDetai;
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
	public String getTrainNum() {
		return trainNum;
	}
	public void setTrainNum(String trainNum) {
		this.trainNum = trainNum;
	}
	public String getSeat() {
		return seat;
	}
	public void setSeat(String seat) {
		this.seat = seat;
	}
	public String getSeatDetai() {
		return seatDetai;
	}
	public void setSeatDetai(String seatDetai) {
		this.seatDetai = seatDetai;
	}
	public List<PassengerDO> getPassengerDO() {
		return passengerDO;
	}
	public void setPassengerDO(List<PassengerDO> passengerDO) {
		this.passengerDO = passengerDO;
	}
	public void addPassengerDO(PassengerDO passengerDO){
		this.passengerDO.add(passengerDO);
	}
	public String getTicketMes(){
		return this.getTrainDate()+":"+this.getTrainNum()+":"+this.getStart()+":"+this.getEnd();
	}
}
