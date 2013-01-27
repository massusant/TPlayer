package com.heavenly.ticket.transaction;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.heavenly.ticket.model.LeftTicketState;
import com.heavenly.ticket.model.Passenger;
import com.heavenly.ticket.model.TrainTrip;
import com.heavenly.ticket.transaction.LeftTicketTransaction.LeftTicketResponse;



public class AutoOrderTransaction extends AsyncTask<Void, BaseResponse, BaseResponse> {
	
	static final String TAG = "AutoOrder";
	
	
	private Context mContext;
	
	
	public AutoOrderTransaction(Context context) {
		mContext = context;
	}
		
	public void doAction(Bundle param, TrainTrip trip) {
		if (trip == null) {
			Log.d(TAG, "信息不全！退出订票！");
			return;
		}
		// 获取余票
		int times = 0;
		LeftTicketState state = null;
		int leftTicketCount = 0;
		do {
			if (times++ > 0) {
				Log.d(TAG, "余票不足！");
				toastMsg("余票不足！", false);
				SystemClock.sleep(2000);
			}
			Log.d(TAG, "刷新余票信息……");
			toastMsg("刷新余票信息……", false);
			state = getLeftTicket(param, trip);
			if (state != null) {
				leftTicketCount = state.getLeftNumBySeat(trip.getSeat());
			}
		} while (state == null || !state.isBookable() || leftTicketCount > 0);
		// 获取TOKEN
		OrderTicketTransaction mOrderTrans = null; 
		while ((mOrderTrans = initOrder(state, trip.getTrainDate())) != null) {
			String code = null;
//			while (TextUtils.isEmpty(code = Log.input("输入订单验证码"))
//					|| code.length() < 4);
			// 提交订单
			submitOrder(code, mOrderTrans, trip.getPassengers());
			SystemClock.sleep(2000);
		}
	}
	
	static LeftTicketState getLeftTicket (Bundle param, TrainTrip trip) {
		LeftTicketTransaction trans = new LeftTicketTransaction();
		trans.setParams(param);
		BaseResponse resp = trans.doAction();
		if (resp != null && resp.success && resp instanceof LeftTicketResponse) {
			List<LeftTicketState> list = ((LeftTicketResponse) resp).data;
			if (list == null || list.isEmpty()) return null;
			for (LeftTicketState state : list) {
				if (state.getTrainNoShow().equals(trip.getToName())) {
					trip.setTrainNo4(state.getTrainNo4());
					return state;
				}
			}
		}
		return null;
	}
	
	static OrderTicketTransaction initOrder(LeftTicketState ticketState, String date) {
		OrderTicketTransaction trans = new OrderTicketTransaction();
		trans.setTicketInfo(ticketState, date);
		BaseResponse resp = trans.obtainToken();
		if (resp != null && resp.success) {
			return trans;
		}
		return null;
	}
	
	static boolean submitOrder(String verifyCode, OrderTicketTransaction trans, List<Passenger> passengers) {
		trans.setVerifyCode(verifyCode);
		BaseResponse resp = trans.makeOrder(passengers);
		if (resp == null) {
			Log.d(TAG, "下单异常！");
			return false;
		}
		Log.d(TAG, resp.msg);
		return resp.success;
	}
	

	@Override
	protected BaseResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onPostExecute(BaseResponse result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(BaseResponse... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
	
	public void toastMsg(String msg, boolean longTime) {
		Toast.makeText(mContext, msg,
				longTime ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}
}