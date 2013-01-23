package com.heavenly.ticket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.heavenly.ticket.R;

public class QueryLeftTicketActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_left_ticket);
		Intent intent = getIntent();
		initViews(intent);
		initData(intent);
	}
	
	private void initViews(Intent intent) {
		mTrainListView = (ListView) findViewById(R.id.train_ticket_list);
	}
	
	private void initData(Intent intent) {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_query_left_ticket, menu);
		return true;
	}
	
	private ListView mTrainListView;

}
