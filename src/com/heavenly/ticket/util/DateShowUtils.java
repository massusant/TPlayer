package com.heavenly.ticket.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期处理函数
 * @author bincode
 * @email	5235852@qq.com
 */
public class DateShowUtils {
	
	public static String getFormatedDateString() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		return getFormatedDateString(cal.getTime());
	}
	
	public static String getFormatedDateString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		return dateFormat.format(date);
	}
	
	public static Calendar getParseFromString(String dateStr) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date date = dateFormat.parse(dateStr);
		cal.setTime(date);
		return cal;
	}
}
