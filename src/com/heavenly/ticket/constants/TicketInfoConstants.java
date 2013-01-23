package com.heavenly.ticket.constants;

import java.util.ArrayList;
import java.util.List;

import com.heavenly.ticket.pojo.OrderDO;
import com.heavenly.ticket.pojo.PassengerDO;

public class TicketInfoConstants {
	public static final List<PassengerDO> passengerList = new ArrayList<PassengerDO>();
	public static final List<OrderDO> orderList = new ArrayList<OrderDO>();
	
	static{
		passengerList.add(new PassengerDO("程通达","610103198812302413","","1"));
		passengerList.add(new PassengerDO("畅博","610103198207132030","","1"));
		passengerList.add(new PassengerDO("范元博","610103198808022857","","1"));
		
		orderList.add(new OrderDO("杭州","西安","2013-01-12","Z86","硬卧","下铺"));
		orderList.add(new OrderDO("杭州","西安","2013-01-12","Z86","软卧","下铺"));
		for(OrderDO order: orderList){
			for(PassengerDO passenger: passengerList){
				order.addPassengerDO(passenger);
			}
		}
	}
}
