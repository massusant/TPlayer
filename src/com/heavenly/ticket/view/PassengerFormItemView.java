package com.heavenly.ticket.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.heavenly.ticket.R;
import com.heavenly.ticket.model.Passenger;

public class PassengerFormItemView extends RelativeLayout {

	private OnClickListener clickListener;
	public PassengerFormItemView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PassengerFormItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PassengerFormItemView(Context context) {
		super(context);
		init();
	}
	
	public void setPosition(int pos) {
		mPosition = pos;
		removeLine.setTag(pos);
	}
	
	public void showAddButton(boolean show) {
		if (show) {
			addLine.setVisibility(View.VISIBLE);
			addLine.requestFocus();
		} else {
			addLine.setVisibility(View.GONE);
		}
	}
	
	public void setOnClickListener(OnClickListener l) {
		addLine.setOnClickListener(l);
		removeLine.setOnClickListener(l);
	}

	private void init() {
		View.inflate(getContext(), R.layout.item_passenger_info_form, this);
		addLine = (Button) findViewById(R.id.add_one_below);
		removeLine = (Button) findViewById(R.id.remove_line);
		ticketTypeChoice = (Button) findViewById(R.id.ticket_for_people);
		seatTypeChoice = (Button) findViewById(R.id.seat_type);
		idcardTypeChoice = (Button) findViewById(R.id.id_type);
		saveCheck = (CheckBox) findViewById(R.id.save_info);
		nameInput = (EditText) findViewById(R.id.passenger_name);
		idcardInput = (EditText) findViewById(R.id.id_code);
		mobileInput = (EditText) findViewById(R.id.moblephone);
	}
	
	public Passenger getPassenger() {
		if (mPassenger == null) {
			mPassenger = new Passenger();
		}
		// update values
		return mPassenger;
	}
	
	private Passenger mPassenger;
	private int mPosition;
	
	private Button addLine;
	private Button ticketTypeChoice;
	private Button seatTypeChoice;
	private Button idcardTypeChoice;
	private Button removeLine;
	private CheckBox saveCheck;
	private EditText nameInput;
	private EditText idcardInput;
	private EditText mobileInput;
	
}