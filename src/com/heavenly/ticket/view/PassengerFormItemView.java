package com.heavenly.ticket.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.heavenly.ticket.R;
import com.heavenly.ticket.model.IdCardType;
import com.heavenly.ticket.model.Passenger;
import com.heavenly.ticket.model.Seat;
import com.heavenly.ticket.model.TicketType;

public class PassengerFormItemView extends RelativeLayout implements OnClickListener {

	private String[] leftTicketNames;
	private Seat[] leftTicketTypes;
	
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
		getPassenger();
	}
	
	public void setPosition(int pos) {
		mPosition = pos;
		removeLine.setTag(pos);
	}
	
	public void setLeftTicket(String[] seatNames, Seat[] seats) {
		leftTicketNames = seatNames;
		leftTicketTypes = seats;
		if (seatNames != null && seatNames.length > 0) {
			seatTypeChoice.setText(seatNames[0]);
			getPassenger().setSeatType(leftTicketTypes[0]);
		}
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
		saveCheck = (CheckBox) findViewById(R.id.save_info);
		nameInput = (EditText) findViewById(R.id.passenger_name);
		idcardInput = (EditText) findViewById(R.id.id_code);
		mobileInput = (EditText) findViewById(R.id.moblephone);
		ticketTypeChoice = (Button) findViewById(R.id.ticket_for_people);
		seatTypeChoice = (Button) findViewById(R.id.seat_type);
		idcardTypeChoice = (Button) findViewById(R.id.id_type);
		
		ticketTypeChoice.setOnClickListener(this);
		seatTypeChoice.setOnClickListener(this);
		idcardTypeChoice.setOnClickListener(this);
		
		ticketTypeChoice.setText(TicketType.ADULT.toString());
		idcardTypeChoice.setText(IdCardType.GEN_IDCARD_2.toString());
	}
	
	private Passenger getPassenger() {
		if (mPassenger == null) {
			mPassenger = new Passenger();
		}
		// update values
		return mPassenger;
	}
	
	public Passenger pullPassengerData() {
		mPassenger.setName(nameInput.getText().toString());
		mPassenger.setIdcardCode(idcardInput.getText().toString());
		mPassenger.setMobile(mobileInput.getText().toString());
		return mPassenger;
	}
	
	private void onTicketChoiceClick() {
		if (ticketChoiceDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			ticketChoiceDialog = builder
					.setTitle("选择车票类型")
					.setItems(TicketType.NAME,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							TicketType ticket = TicketType.values()[which];
							getPassenger().setTicketType(ticket);
							ticketTypeChoice.setText(ticket.toString());
						}
					}).create();
		}
		ticketChoiceDialog.show();
	}
	
	private void onSeatChoiceClick() {
		if (seatChoiceDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			seatChoiceDialog = builder
					.setTitle("选择席别")
					.setItems(leftTicketNames,
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Seat seat = leftTicketTypes[which];
							getPassenger().setSeatType(seat);
							seatTypeChoice.setText(seat.toString());
						}
					}).create();
		}
		seatChoiceDialog.show();
	}
	
	private void onIdCardChoiceClick() {
		if (idcardChoiceDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			idcardChoiceDialog = builder
					.setTitle("选择证件类型")
					.setItems(IdCardType.NAME,
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							IdCardType idcard = IdCardType.values()[which];
							getPassenger().setIdcardType(idcard);
							idcardTypeChoice.setText(idcard.toString());
						}
					}).create();
		}
		idcardChoiceDialog.show();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.ticket_for_people:
			onTicketChoiceClick();
			break;
		case R.id.seat_type:
			onSeatChoiceClick();
			break;
		case R.id.id_type:
			onIdCardChoiceClick();
			break;
		}
	}
	
	private AlertDialog ticketChoiceDialog;
	private AlertDialog seatChoiceDialog;
	private AlertDialog idcardChoiceDialog;
	
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