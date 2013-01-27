package com.heavenly.ticket.model;

import java.util.List;

import com.heavenly.ticket.transaction.TrainTransaction;
import com.heavenly.ticket.transaction.TrainTransaction.QueryTrainParam;

public class TrainTrip {
	
	public TrainTrip(String no, String date, String from, String fromCode, String to, String toCode) throws Exception {
		trainNo = no;
		trainDate = date;
		fromName = from;
		toName = to;
//		fromCode = StationTransaction.stationCode(from);
//		toCode = StationTransaction.stationCode(to);
		List<Train> list = TrainTransaction.queryTrain(new QueryTrainParam(
				fromCode, toCode, date, "00:00--24:00"));
		if (list == null || list.isEmpty()) {
			throw new Exception("列车不存在！");
		}
		for (Train train : list) {
			if (train.getName().indexOf(trainNo) > -1) {
				trainNo4 = train.getId();
				break;
			}
		}
	}

	private String trainNo;
	private String trainNo4;
	private String trainDate;
	private String fromName;
	private String fromCode;
	private String toName;
	private String toCode;
	private List<Passenger> passengers;
	private Seat seat;
	
	public void setSeat(Seat seat) {
		this.seat = seat;
	}
	public String getTrainNo() {
		return trainNo;
	}
	public void setTrainNo4(String id) {
		trainNo4 = id;
	}
	public String getTrainNo4() {
		return trainNo4;
	}
	public String getTrainDate() {
		return trainDate;
	}
	public String getFromName() {
		return fromName;
	}
	public String getFromCode() {
		return fromCode;
	}
	public String getToName() {
		return toName;
	}
	public String getToCode() {
		return toCode;
	}
	public void setPassengers(List<Passenger> plist) {
		passengers = plist;
	}
	public List<Passenger> getPassengers() {
		return passengers;
	}
	public Seat getSeat() {
		return seat;
	}
}
