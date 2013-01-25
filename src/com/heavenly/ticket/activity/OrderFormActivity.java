package com.heavenly.ticket.activity;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
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
import com.heavenly.ticket.adapter.PassengerFormAdapter;
import com.heavenly.ticket.model.LeftTicketState;
import com.heavenly.ticket.transaction.OrderTicketTransaction;
import com.heavenly.ticket.view.PassengerFormItemView;

public class OrderFormActivity extends Activity implements OnClickListener {

	private List<PassengerFormItemView> mFormList;
	private LinkedList<PassengerFormItemView> removedForms;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_form);
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		initViews(intent);
		initData(intent);
	}

	private void initViews(Intent intent) {
		mMultiFormView = (LinearLayout) findViewById(R.id.passenger_info_list);
		mVerifyCodeText = (EditText) findViewById(R.id.verify_code_value);
		mVerifyCodeImage = (ImageView) findViewById(R.id.verify_code_image);
		
		removedForms = new LinkedList<PassengerFormItemView>();
		mFormList = new LinkedList<PassengerFormItemView>();
		
		PassengerFormItemView first = obtainFormView(0);
		mFormList.add(first);
		mMultiFormView.addView(first, 0);
	}

	private void initData(Intent intent) {
		ticketState = (LeftTicketState) intent
				.getSerializableExtra(getString(R.string.intent_key_left_ticket_state));
		travelDate = intent
				.getStringExtra(getString(R.string.intent_key_departure_date));

		initToken();
//		mAdapter = new PassengerFormAdapter(this);
//		mListView.setAdapter(mAdapter);
		
	}
	
	private PassengerFormItemView obtainFormView(int position) {
		PassengerFormItemView item;
		if (!removedForms.isEmpty()) {
			item = removedForms.removeFirst();
		} else {
			item = new PassengerFormItemView(this);
			item.setOnClickListener(this);
		}
//		item.setPosition(position);
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
		if (progress == null) {
			progress = ProgressDialog.show(this, "", "loading...", true);
		} else {
			progress.show();
		}
		new AsyncTask<Void, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Void... params) {
				if (transaction == null) {
					transaction = new OrderTicketTransaction();
				}
				transaction.setTicketInfo(ticketState, travelDate);
				if (transaction.obtainToken() != null) {
					return transaction.refreshVerifyBitmap();
				}
				return null;
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

	public void onVerifyCodeImageClick(View view) {
		if (progress == null) {
			progress = ProgressDialog.show(this, "", "loading...", true);
		} else {
			progress.show();
		}
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
		if (progress == null) {
			progress = ProgressDialog.show(this, "", "loading...", true);
		} else {
			progress.show();
		}
		new AsyncTask<Void, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Void... params) {
				if (transaction == null) {
					transaction = new OrderTicketTransaction();
				}
				transaction.setVerifyCode(code);
				transaction.makeOrder();
				return null;
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

	private LinearLayout mMultiFormView;
	private PassengerFormAdapter mAdapter;
	private OrderTicketTransaction transaction;
	private LeftTicketState ticketState;
	private String travelDate;
	private ProgressDialog progress;
	private EditText mVerifyCodeText;
	private ImageView mVerifyCodeImage;
}