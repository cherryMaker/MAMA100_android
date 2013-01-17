package com.mama100.android.member.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateUtils {
	
	private static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd");
	private static SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static SimpleDateFormat FileName_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	public static String formatSimpleDateStr(int year, int monthOfYear, int dayOfMonth) {
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.DATE, dayOfMonth);
		
		return SIMPLE_DATE_FORMAT.format(cal.getTime());
	}
	
	public static Date parseSimpleDate(String dateStr) throws ParseException {
		return SIMPLE_DATE_FORMAT.parse(dateStr);
	}

	public static String formatSimpleDateStr(Date date) {
		return SIMPLE_DATE_FORMAT.format(date);
	}

	public static String formatDisplayDateStr(Date date) {
		return DISPLAY_DATE_FORMAT.format(date);
	}
	
	public static Date parseDbDate(String dateStr) throws ParseException {
		return DB_DATE_FORMAT.parse(dateStr);
	}
	
	public static String formatDbDateStr(Date date) {
		return DB_DATE_FORMAT.format(date);
	}
	
	public static String formatFileNameDateStr(Date date) {
		return FileName_DATE_FORMAT.format(date);
	}
	
	/**
	 * 获取当前考核月时间段对象
	 * @return
	 */
	public static DateRange findAuditMonthRange() {
		
		DateRange range = new DateRange();
		
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		
		Calendar nowCal = Calendar.getInstance();
		int currentMonth = nowCal.get(Calendar.MONTH) + 1;
		int currentDay = nowCal.get(Calendar.DATE);
		
		System.out.println("currentMonth: " + currentMonth + " currentDay: " + currentDay);
		
		fromCal.set(Calendar.DATE, 26);
		fromCal.set(Calendar.HOUR_OF_DAY, 0);
		fromCal.set(Calendar.MINUTE, 0);
		fromCal.set(Calendar.SECOND, 0);		

		fromCal.add(Calendar.MONTH, -1);

		toCal.set(Calendar.DATE, 25);
		toCal.set(Calendar.HOUR_OF_DAY, 23);
		toCal.set(Calendar.MINUTE, 59);
		toCal.set(Calendar.SECOND, 59);
//		toCal.set(Calendar.HOUR_OF_DAY, 0);
//		toCal.set(Calendar.MINUTE, 0);
//		toCal.set(Calendar.SECOND, 0);
	                  
		if (currentDay >= 26) { 
			
			// 下个月的统计范围	
			fromCal.add(Calendar.MONTH, 1);
			toCal.add(Calendar.MONTH, 1);	
		}
		
		Date from = fromCal.getTime();
		Date to = toCal.getTime();		
		
		range.setFrom(from);
		range.setTo(to);
		
		return range;
	}

}
