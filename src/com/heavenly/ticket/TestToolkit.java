package com.heavenly.ticket;

import android.test.AndroidTestCase;

public class TestToolkit extends AndroidTestCase {
	
	public String getString(int id) {
		return getContext().getString(id);
	}
	
	public static void main(String[] args) {
		System.out.println(new TestToolkit().getString(R.string.title_activity_main));
	}
}
