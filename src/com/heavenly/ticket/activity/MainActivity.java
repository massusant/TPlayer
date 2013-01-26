package com.heavenly.ticket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.heavenly.ticket.R;

public class MainActivity extends Activity {

	private String mShowName;
	private String mShowGender;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		initViews(intent);
		initData(intent);
	}
	
	private void initViews(Intent intent) {
		mWelcomePrompt = (TextView) findViewById(R.id.welcome_prompt);
	}
	
	private void initData(Intent intent) {
		mShowName = intent.getStringExtra("show.value.user.showname");
		mShowGender = intent.getStringExtra("show.value.user.showgender");
		
		mWelcomePrompt.setText(buildWelcomeString(mShowName, mShowGender));
	}
	
	private String buildWelcomeString(String showName, String showGender) {
		if (TextUtils.isEmpty(showName)) {
			return "您好！";
		}
		if (TextUtils.isEmpty(showGender)) {
			return showName + " , 您好！";
		}
		return showName + showGender + " , 您好！";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void onOrderNormalClick(View view) {
		Intent intent = new Intent(this, SelectTrainActivity.class);
		startActivity(intent);
	}
	
	public void onOrderAutoClick(View view) {
		Intent intent = new Intent(this, AutoBookActivity.class);
		startActivity(intent);
	}

	private TextView mWelcomePrompt;
	
}
