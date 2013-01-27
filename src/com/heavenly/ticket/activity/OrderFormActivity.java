package com.heavenly.ticket.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.heavenly.ticket.R;
import com.heavenly.ticket.model.LeftTicketState;
import com.heavenly.ticket.model.Passenger;
import com.heavenly.ticket.model.Seat;
import com.heavenly.ticket.transaction.BaseResponse;
import com.heavenly.ticket.transaction.OrderTicketTransaction;
import com.heavenly.ticket.view.PassengerFormItemView;

public class OrderFormActivity extends Activity implements OnClickListener {

	public static final String INTENT_KEY_FOR_TASK = "task_mode";
	
	private List<PassengerFormItemView> mFormList;
	private LinkedList<PassengerFormItemView> removedForms;
	
	private String[] mLeftSeatNames;
	private Seat[] mLeftSeatType;
	
	private boolean mTaskMode = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_form);
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mTaskMode = intent.getBooleanExtra(INTENT_KEY_FOR_TASK, false);
		initViews(intent);
		initData(intent);
	}

	private void initViews(Intent intent) {
		mMultiFormView = (LinearLayout) findViewById(R.id.passenger_info_list);
		mVerifyCodeText = (EditText) findViewById(R.id.verify_code_value);
		mVerifyCodeImage = (ImageView) findViewById(R.id.verify_code_image);
		
		removedForms = new LinkedList<PassengerFormItemView>();
		mFormList = new LinkedList<PassengerFormItemView>();
		if (mTaskMode) {
			findViewById(R.id.control_panel).setVisibility(View.INVISIBLE);
			findViewById(R.id.start_task).setVisibility(View.VISIBLE);
		}
	}

	private void initData(Intent intent) {
		
		if (mTaskMode) {
			travelDate = intent
					.getStringExtra(getString(R.string.intent_key_departure_date));
			mLeftSeatNames = Seat.SEAT_NAME_ARRAY;
			mLeftSeatType = Seat.values();
			PassengerFormItemView first = obtainFormView(0);
			mFormList.add(first);
			mMultiFormView.addView(first, 0);
			return;
		}
		
		ticketState = (LeftTicketState) intent
				.getSerializableExtra(getString(R.string.intent_key_left_ticket_state));
		travelDate = intent
				.getStringExtra(getString(R.string.intent_key_departure_date));
		mLeftSeatNames = ticketState.getSeatLeftNames();
		mLeftSeatType = ticketState.getSeatLeftTypes();
		initToken();
		
	}
	
	private PassengerFormItemView obtainFormView(int position) {
		PassengerFormItemView item;
		if (!removedForms.isEmpty()) {
			item = removedForms.removeFirst();
		} else {
			item = new PassengerFormItemView(this);
			item.setOnClickListener(this);
		}
		item.setLeftTicket(mLeftSeatNames, mLeftSeatType);
		return item;
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.add_one_below) {
			if (mFormList.size() < 5) {
				int pos = mMultiFormView.getChildCount();
				PassengerFormItemView itemView = obtainFormView(pos);
				mFormList.add(itemView);
				mMultiFormView.addView(itemView, pos);
			}
		} else if (view.getId() == R.id.remove_line) {
			if (mFormList.size() > 1) {
				int pos = (Integer) view.getTag();
				PassengerFormItemView itemView = mFormList.remove(pos);
				mMultiFormView.removeView(itemView);
				removedForms.addLast(itemView);
			}
		}
		int count = mMultiFormView.getChildCount();
		for (int i = 0; i < count; i++) {
			PassengerFormItemView itemView = (PassengerFormItemView) mMultiFormView
					.getChildAt(i);
			boolean hideAddButton = i < count - 1 || i == 4;
			itemView.showAddButton(!hideAddButton);
			itemView.setPosition(i);
		}
	}
	
	private void initToken() {
		showProgress();
		new AsyncTask<Void, Void, Bitmap>() {
			BaseResponse response;
			@Override
			protected Bitmap doInBackground(Void... params) {
				if (transaction == null) {
					transaction = new OrderTicketTransaction();
				}
				transaction.setTicketInfo(ticketState, travelDate);
				response = transaction.obtainToken();
				if (response != null && response.success) {
					ticketState.parseLeftSeatNum(transaction.getTicket());
					mLeftSeatNames = ticketState.getSeatLeftNames();
					mLeftSeatType = ticketState.getSeatLeftTypes();
					return transaction.refreshVerifyBitmap();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				progress.dismiss();
				if (response == null) {
					Toast.makeText(OrderFormActivity.this, "获取Token失败！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (response.success && result != null) {
					mVerifyCodeImage.setImageBitmap(result);
					PassengerFormItemView first = obtainFormView(0);
					mFormList.add(first);
					mMultiFormView.addView(first, 0);
				} else {
					Toast.makeText(OrderFormActivity.this, response.msg,
							Toast.LENGTH_SHORT).show();
				}
			}
		}.execute();
	}

	public void onVerifyCodeImageClick(View view) {
		showProgress();
		mVerifyCodeText.setText("");
		new AsyncTask<Void, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Void... params) {
				if (transaction == null) {
					transaction = new OrderTicketTransaction();
				}
				return transaction.refreshVerifyBitmap();
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				progress.dismiss();
				if (result != null) {
					mVerifyCodeImage.setImageBitmap(result);
				}
			}
		}.execute();
	}

	public void onSubmitClick(View view) {
		final String code = mVerifyCodeText.getText().toString();
		if (TextUtils.isEmpty(code)) {
			Toast.makeText(this, "输入验证码", Toast.LENGTH_SHORT).show();
			return;
		}
		showProgress();
		new AsyncTask<Void, Void, BaseResponse>() {
			@Override
			protected BaseResponse doInBackground(Void... params) {
				if (transaction == null) {
					transaction = new OrderTicketTransaction();
				}
				transaction.setVerifyCode(code);
				return transaction.makeOrder(getPassengersData());
			}

			@Override
			protected void onPostExecute(BaseResponse result) {
				progress.dismiss();
				if (result != null) {
					if (result.success) {
						Toast.makeText(OrderFormActivity.this, "订单提交成功！",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(OrderFormActivity.this, result.msg,
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(OrderFormActivity.this, "订单提交失败！",
							Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}
	
	public void onStartTaskClick(View view) {
		final List<Passenger> passengers = getPassengersData();
		showProgress();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				
				return null;
			}
			
		}.execute();
	}
	
	public void requestVerifyCodeDialog(Bitmap verifyimg, final String verifyCode) {
	}
	
	private ArrayList<Passenger> getPassengersData() {
		ArrayList<Passenger> list = new ArrayList<Passenger>();
		for (int i = 0; i < mFormList.size(); i++) {
			list.add(mFormList.get(i).pullPassengerData());
		}
		return list;
	}
	
	private void showProgress() {
		if (progress == null) {
			progress = ProgressDialog.show(this, "", "loading...", true);
		} else {
			progress.show();
		}
	}

	private LinearLayout mMultiFormView;
	private OrderTicketTransaction transaction;
	private LeftTicketState ticketState;
	private String travelDate;
	private ProgressDialog progress;
	private EditText mVerifyCodeText;
	private ImageView mVerifyCodeImage;
}