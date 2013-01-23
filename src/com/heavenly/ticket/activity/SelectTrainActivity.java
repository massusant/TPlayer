package com.heavenly.ticket.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heavenly.ticket.R;
import com.heavenly.ticket.adapter.StationSuggestionListAdapter;
import com.heavenly.ticket.adapter.TrainSelectorAdapter;
import com.heavenly.ticket.model.Station;
import com.heavenly.ticket.model.Train;
import com.heavenly.ticket.transaction.TrainTransaction;
import com.heavenly.ticket.transaction.TrainTransaction.QueryTrainParam;
import com.heavenly.ticket.util.DateShowUtils;

public class SelectTrainActivity extends Activity {
	
	final String TAG = "TrainSelector";
	
	final CharSequence[] TIME_ITEMS = new CharSequence[] { "00:00--24:00",
			"00:00--06:00", "06:00--12:00", "12:00--18:00", "18:00--24:00" };
	
	final CharSequence[] TRAIN_TYPE_ITEMS = new CharSequence[] { "动车",
			"Z字头", "T字头", "K字头", "其他" };

	private String startStationCode;
	private String destStationCode;
	private String mDateStartOff;
	private String mTimeStartOff;
	private boolean[] mSelectTrainClass = {true, true, true, true, true};
	private TrainSelectorAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_selector);
		initViews();
	}
	
	private void initViews() {
		
		startStationInput = (AutoCompleteTextView) findViewById(R.id.saddr_value);
		destStationInput = (AutoCompleteTextView) findViewById(R.id.daddr_value);
		mTrainList = (ListView) findViewById(R.id.train_list);
		
		StationSuggestionListAdapter adapter = new StationSuggestionListAdapter(this);
		startStationInput.setAdapter(adapter);
		destStationInput.setAdapter(adapter);
		startStationInput.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				stationSelector.onItemClick(arg0, startStationInput, arg2, arg3);
			}
		});
		destStationInput.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				stationSelector.onItemClick(arg0, destStationInput, arg2, arg3);
			}
		});
		
		mDateStartOff = DateShowUtils.getFormatedDateString();
		setViewText(R.id.date_value, mDateStartOff);
		mTimeStartOff = TIME_ITEMS[0].toString();
		setViewText(R.id.time_value, mTimeStartOff);
		setViewText(R.id.train_type, getTrainClassShow().toString());
		mHandler = new Handler();
	}
	
	private void setViewText(int viewId, String date) {
		((TextView) findViewById(viewId)).setText(date);
	}
	
	private String getTrainClassShow() {
		StringBuilder trainClass =  new StringBuilder();
		for (int i = 0; i < TRAIN_TYPE_ITEMS.length; i++) {
			if (mSelectTrainClass[i]) {
				trainClass.append(TRAIN_TYPE_ITEMS[i]).append("|");
			}
		}
		if (trainClass.length() > 0) {
			trainClass.deleteCharAt(trainClass.length() - 1);
		}
		return trainClass.toString();
	}
	
	private OnItemClickListener stationSelector = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View inputView, int position,
				long id) {
			StationSuggestionListAdapter adapter = (StationSuggestionListAdapter) parent
					.getAdapter();
			if (adapter != null && adapter.getCount() > 0 && position < adapter.getCount()) {
				Station target = adapter.getItem(position);
				if (inputView != null && inputView.equals(startStationInput)) {
					startStationCode = target.getCode();
				}
				if (inputView != null && inputView.equals(destStationInput)) {
					destStationCode = target.getCode();
				}
			}
		}
	};
	
	public void onChangeDateClick(View view) {
		Log.d(TAG, "changeDate");
		String time = ((TextView) view).getText().toString();
		Calendar cur;
		try {
			cur = DateShowUtils.getParseFromString(time);
		} catch (ParseException e) {
			cur = Calendar.getInstance();
			cur.setTimeInMillis(System.currentTimeMillis());
			e.printStackTrace();
		}
		if (mDatePicker == null) {
			mDatePicker = new DatePickerDialog(this, new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					Calendar ret = Calendar.getInstance();
					ret.set(year, monthOfYear, dayOfMonth);
					mDateStartOff = DateShowUtils.getFormatedDateString(ret.getTime());
					setViewText(R.id.date_value, mDateStartOff);
				}
			}, cur.get(Calendar.YEAR), cur.get(Calendar.MONTH),
					cur.get(Calendar.DAY_OF_MONTH));
		} else {
			mDatePicker.updateDate(cur.get(Calendar.YEAR),
					cur.get(Calendar.MONTH), cur.get(Calendar.DAY_OF_MONTH));
		}
		mDatePicker.show();
	}
	
	public void onChangeTimeClick(View view) {
		Log.d(TAG, "changeTime");
		if (mTimeChoiceDialog == null) {
			mTimeChoiceDialog = new AlertDialog.Builder(this)
			.setItems(TIME_ITEMS, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mTimeStartOff = TIME_ITEMS[which].toString();
					setViewText(R.id.time_value, mTimeStartOff);
					dialog.dismiss();
				}
			}).setTitle("选择时段").setCancelable(true).create();
		}
		mTimeChoiceDialog.show();
	}
	
	public void onChangeTypeClick(View view) {
		Log.d(TAG, "changeType");
		if (mTrainClassChoiceDialog == null) {
			mTrainClassChoiceDialog = new AlertDialog.Builder(this)
			.setPositiveButton("选择", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String trainClass = getTrainClassShow();
					if (TextUtils.isEmpty(trainClass)) {
						Toast.makeText(
								SelectTrainActivity.this,
								"至少选择一种", Toast.LENGTH_SHORT)
								.show();
						mHandler.postDelayed(new Runnable() {
							public void run() {
								mTrainClassChoiceDialog.show();
							}
						}, 200);
					} else {
						setViewText(R.id.train_type, trainClass.toString());
					}
				}
			})
			.setMultiChoiceItems(TRAIN_TYPE_ITEMS, mSelectTrainClass,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which, boolean isChecked) {
						mSelectTrainClass[which] = isChecked;
					}
				}).setTitle("选择列车类型").create();
		}
		mTrainClassChoiceDialog.show();
	}
	
	public void onQueryTrainClick(View view) {
		if (TextUtils.isEmpty(startStationCode) || TextUtils.isEmpty(destStationCode)) {
			Toast.makeText(this, "请输入起点和终点", Toast.LENGTH_LONG).show();
			return;
		}
		if (progress == null) {
			progress = ProgressDialog.show(this, null, "加载中……", true, true);
		}
		if (!progress.isShowing()) {
			progress.show();
		}
		Log.d(TAG, "queryTrain");
		new AsyncTask<String, Void, ArrayList<Train>>() {
			@Override
			protected ArrayList<Train> doInBackground(String... params) {
				try {
					ArrayList<Train> list = TrainTransaction.queryTrain(
							new QueryTrainParam(params[0], params[1],
									params[2], params[3]), mSelectTrainClass);
					return list;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null; 
			}
			@Override
			protected void onPostExecute(ArrayList<Train> result) {
				if (mAdapter == null) {
					mAdapter = new TrainSelectorAdapter(SelectTrainActivity.this);
				}
				mAdapter.setData(result);
				mTrainList.setAdapter(mAdapter);
				if (progress != null) {
					progress.dismiss();
				}
			}
		}.execute(startStationCode, destStationCode, mDateStartOff,
				mTimeStartOff);
	}
	

	public void onQueryLeftTicketClick(View view) {
		Intent intent = new Intent(SelectTrainActivity.this,
				LeftTicketResultActivity.class);
		intent.putExtra(getString(R.string.intent_key_from_station_code), startStationCode);
		intent.putExtra(getString(R.string.intent_key_to_station_code), destStationCode);
		intent.putExtra(getString(R.string.intent_key_departure_date), mDateStartOff);
		intent.putExtra(getString(R.string.intent_key_departure_time), mTimeStartOff);
		intent.putExtra(getString(R.string.intent_key_train_no), "");
		intent.putExtra(getString(R.string.intent_key_include_student), "00");
		intent.putExtra(getString(R.string.intent_key_train_class), "D#Z#T#K#QT#");
		intent.putExtra(getString(R.string.intent_key_train_pass_type), "QB");
		startActivity(intent);
	}
	
//	param.add(new BasicNameValuePair("method", "queryLeftTicket"));
//	param.add(new BasicNameValuePair("orderRequest.from_station_telecode", "HZH"));
//	param.add(new BasicNameValuePair("orderRequest.to_station_telecode", "XAY"));
//	param.add(new BasicNameValuePair("orderRequest.train_date", "2013-02-09"));
//	param.add(new BasicNameValuePair("orderRequest.start_time_str", "00:00--24:00"));
//	param.add(new BasicNameValuePair("orderRequest.train_no", "5600000Z8601"));
//	param.add(new BasicNameValuePair("includeStudent", "00"));
//	param.add(new BasicNameValuePair("trainClass", "D#Z#T#K#QT#"));
//	param.add(new BasicNameValuePair("trainPassType", "QB"));
//	param.add(new BasicNameValuePair("seatTypeAndNum", ""));

	private AutoCompleteTextView startStationInput;
	private AutoCompleteTextView destStationInput;
	private ListView mTrainList;
	private ProgressDialog progress;
	private DatePickerDialog mDatePicker;
	private AlertDialog mTimeChoiceDialog;
	private AlertDialog mTrainClassChoiceDialog;
	private Handler mHandler;
	
	class StationItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
		}
		
	}
}