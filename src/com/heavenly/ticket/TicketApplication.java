package com.heavenly.ticket;

import com.heavenly.ticket.util.RpcHelper;

import android.app.Application;

public class TicketApplication extends Application {

	@Override
	public void onCreate() {
		RpcHelper.init(this);
		super.onCreate();
	}

}
