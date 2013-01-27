package com.heavenly.ticket;

import java.util.ArrayList;
import java.util.List;

import com.heavenly.ticket.model.Passenger;
import com.heavenly.ticket.model.Seat;
import com.heavenly.ticket.model.TrainTrip;

public class Constants {

	static Seat sSeat = Seat.SLEEPER_HARD;
	public static TrainTrip createTrip () {
//		try {
//			
//			TrainTrip trip = new TrainTrip("L664", "2012-02-15", "杭州", "上海",
//					sSeat);
////			TrainTrip trip = new TrainTrip("Z88", "2012-02-15", "西安", "杭州",
////					sSeat);
//			trip.setPassengers(createPassengers(sSeat));
//			return trip;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return null;
	}
	
	public static List<Passenger> createPassengers(Seat seat) {
		ArrayList<Passenger> list = new ArrayList<Passenger>();
//		list.add(new Passenger(seat, "文雪龙", "610481198805240037", "13646815023"));
		return list;
	}
}
