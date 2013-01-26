package com.heavenly.ticket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.heavenly.ticket.R;

public class AutoBookActivity extends Activity {

	static final String TAG = "AutoBookActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_book);
		mTaskListView = (ListView) findViewById(R.id.list_view); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_auto_book, menu);
		return true;
	}
	
	public void onAddActionClick(MenuItem item) {
		Log.d(TAG, "add action");
		Intent intent = new Intent(this, TrainSelectorActivity.class);
		startActivity(intent);
	}
	
	private ListView mTaskListView;
}
