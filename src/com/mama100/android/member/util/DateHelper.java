package com.mama100.android.member.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期工具类
 */
public class DateHelper {
	/**
	 * 时间范围：年
	 */
	public static final int YEAR = 1;

	/**
	 * 时间范围：季度
	 */
	public static final int QUARTER = 2;

	/**
	 * 时间范围：月
	 */
	public static final int MONTH = 3;

	/**
	 * 时间范围：旬
	 */
	public static final int TENDAYS = 4;

	/**
	 * 时间范围：周
	 */
	public static final int WEEK = 5;

	/**
	 * 时间范围：日
	 */
	public static final int DAY = 6;

	/* 基准时间 */
	private Date fiducialDate = null;
	private Calendar cal = null;

	private DateHelper(Date fiducialDate) {
		if (fiducialDate != null) {
			this.fiducialDate = fiducialDate;
		} else {
			this.fiducialDate = new Date(System.currentTimeMillis());
		}

		this.cal = Calendar.getInstance();
		this.cal.setTime(this.fiducialDate);
		this.cal.set(Calendar.HOUR_OF_DAY, 0);
		this.cal.set(Calendar.MINUTE, 0);
		this.cal.set(Calendar.SECOND, 0);
		this.cal.set(Calendar.MILLISECOND, 0);

		this.fiducialDate = this.cal.getTime();
	}

	/**
	 * 获取DateHelper实例
	 * 
	 * @param fiducialDate
	 *            基准时间
	 * @return
	 */
	public static DateHelper getInstance(Date fiducialDate) {
		return new DateHelper(fiducialDate);
	}

	/**
	 * 获取DateHelper实例, 使用当前时间作为基准时间
	 * 
	 * @return
	 */
	public static DateHelper getInstance() {
		return new DateHelper(null);
	}

	/**
	 * 获取年的第一天
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getFirstDayOfYear(int offset) {
		cal.setTime(this.fiducialDate);
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + offset);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * 获取年的最后一天
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getLastDayOfYear(int offset) {
		cal.setTime(this.fiducialDate);
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + offset);
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 31);
		return cal.getTime();
	}

	/**
	 * 获取季度的第一天
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getFirstDayOfQuarter(int offset) {
		cal.setTime(this.fiducialDate);
		cal.add(Calendar.MONTH, offset * 3);
		int mon = cal.get(Calendar.MONTH);
		if (mon >= Calendar.JANUARY && mon <= Calendar.MARCH) {
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		if (mon >= Calendar.APRIL && mon <= Calendar.JUNE) {
			cal.set(Calendar.MONTH, Calendar.APRIL);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		if (mon >= Calendar.JULY && mon <= Calendar.SEPTEMBER) {
			cal.set(Calendar.MONTH, Calendar.JULY);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		if (mon >= Calendar.OCTOBER && mon <= Calendar.DECEMBER) {
			cal.set(Calendar.MONTH, Calendar.OCTOBER);
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		return cal.getTime();
	}

	/**
	 * 获取季度的最后一天
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getLastDayOfQuarter(int offset) {
		cal.setTime(this.fiducialDate);
		cal.add(Calendar.MONTH, offset * 3);

		int mon = cal.get(Calendar.MONTH);
		if (mon >= Calendar.JANUARY && mon <= Calendar.MARCH) {
			cal.set(Calendar.MONTH, Calendar.MARCH);
			cal.set(Calendar.DAY_OF_MONTH, 31);
		}
		if (mon >= Calendar.APRIL && mon <= Calendar.JUNE) {
			cal.set(Calendar.MONTH, Calendar.JUNE);
			cal.set(Calendar.DAY_OF_MONTH, 30);
		}
		if (mon >= Calendar.JULY && mon <= Calendar.SEPTEMBER) {
			cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
			cal.set(Calendar.DAY_OF_MONTH, 30);
		}
		if (mon >= Calendar.OCTOBER && mon <= Calendar.DECEMBER) {
			cal.set(Calendar.MONTH, Calendar.DECEMBER);
			cal.set(Calendar.DAY_OF_MONTH, 31);
		}
		return cal.getTime();
	}

	/**
	 * 获取月的最后一天
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getFirstDayOfMonth(int offset) {
		cal.setTime(this.fiducialDate);
		cal.add(Calendar.MONTH, offset);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * 获取月的最后一天
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getLastDayOfMonth(int offset) {
		cal.setTime(this.fiducialDate);
		cal.add(Calendar.MONTH, offset + 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 获取旬的第一天
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getFirstDayOfTendays(int offset) {
		cal.setTime(this.fiducialDate);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (day >= 21) {
			day = 21;
		} else if (day >= 11) {
			day = 11;
		} else {
			day = 1;
		}

		if (offset > 0) {
			day = day + 10 * offset;
			int monOffset = day / 30;
			day = day % 30;
			cal.add(Calendar.MONTH, monOffset);
			cal.set(Calendar.DAY_OF_MONTH, day);
		} else {
			int monOffset = 10 * offset / 30;
			int dayOffset = 10 * offset % 30;
			if ((day + dayOffset) > 0) {
				day = day + dayOffset;
			} else {
				monOffset = monOffset - 1;
				day = day - dayOffset - 10;
			}
			cal.add(Calendar.MONTH, monOffset);
			cal.set(Calendar.DAY_OF_MONTH, day);
		}
		return cal.getTime();
	}

	/**
	 * 获取旬的最后一天
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getLastDayOfTendays(int offset) {
		Date date = getFirstDayOfTendays(offset + 1);
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 获取周的第一天(MONDAY)
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getFirstDayOfWeek(int offset) {
		cal.setTime(this.fiducialDate);
		cal.add(Calendar.DAY_OF_MONTH, offset * 7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

	/**
	 * 获取周的最后一天(SUNDAY)
	 * 
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date getLastDayOfWeek(int offset) {
		cal.setTime(this.fiducialDate);
		cal.add(Calendar.DAY_OF_MONTH, offset * 7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.add(Calendar.DAY_OF_MONTH, 6);
		return cal.getTime();
	}

	/**
	 * 获取指定时间范围的第一天
	 * 
	 * @param dateRangeType
	 *            时间范围类型
	 * @param offset
	 *            偏移量
	 * @return
	 */
	private Date getFirstDate(int dateRangeType, int offset) {
		return null;
	}

	/**
	 * 获取指定时间范围的最后一天
	 * 
	 * @param dateRangeType
	 *            时间范围类型
	 * @param offset
	 *            偏移量
	 * @return
	 */
	private Date getLastDate(int dateRangeType, int offset) {
		return null;
	}

	/**
	 * 根据日历的规则，为基准时间添加指定日历字段的时间量
	 * 
	 * @param field
	 *            日历字段, 使用Calendar类定义的日历字段常量
	 * @param offset
	 *            偏移量
	 * @return
	 */
	public Date add(int field, int offset) {
		cal.setTime(this.fiducialDate);
		cal.add(field, offset);
		return cal.getTime();
	}

	/**
	 * 根据日历的规则，为基准时间添加指定日历字段的单个时间单元
	 * 
	 * @param field
	 *            日历字段, 使用Calendar类定义的日历字段常量
	 * @param up
	 *            指定日历字段的值的滚动方向。true:向上滚动 / false:向下滚动
	 * @return
	 */
	public Date roll(int field, boolean up) {
		cal.setTime(this.fiducialDate);
		cal.roll(field, up);
		return cal.getTime();
	}

	/**
	 * 把字符串转换为日期
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param format
	 *            日期格式
	 * @return
	 */
	public static Date strToDate(String dateStr, String format) {
		Date date = null;

		if (dateStr != null && (!dateStr.equals(""))) {
			DateFormat df = new SimpleDateFormat(format);
			try {
				date = df.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * 把字符串转换为日期，日期的格式为yyyy-MM-dd HH:ss
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @return
	 */
	public static Date strToDate(String dateStr) {
		Date date = null;

		if (dateStr != null && (!dateStr.equals(""))) {
			if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
				dateStr = dateStr + " 00:00";
			} else if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}")) {
				dateStr = dateStr + ":00";
			} else {
				System.out.println(dateStr + " 格式不正确");
				return null;
			}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:ss");
			try {
				date = df.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * 把日期转换为字符串
	 * 
	 * @param date
	 *            日期实例
	 * @param format
	 *            日期格式
	 * @return
	 */
	public static String dateToStr(Date date, String format) {
		return (date == null) ? "" : new SimpleDateFormat(format).format(date);
	}

	/**
	 * 取得当前日期 年-月-日
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		return f.format(Calendar.getInstance().getTime());
	}

	public static void main(String[] args) {
		
		testmain();
		

	}

	public static void testmain2() {
		DateHelper dateHelper = DateHelper.getInstance();
		System.out.println(dateHelper.getCurrentDate());
		/* Year */
		for (int i = -5; i <= 5; i++) {
			System.out.println("FirstDayOfYear(" + i + ") = "
					+ dateHelper.getFirstDayOfYear(i));
			System.out.println("LastDayOfYear(" + i + ") = "
					+ dateHelper.getLastDayOfYear(i));
		}

		/* Quarter */
		for (int i = -5; i <= 5; i++) {
			System.out.println("FirstDayOfQuarter(" + i + ") = "
					+ dateHelper.getFirstDayOfQuarter(i));
			System.out.println("LastDayOfQuarter(" + i + ") = "
					+ dateHelper.getLastDayOfQuarter(i));
		}

		/* Month */
		for (int i = -5; i <= 5; i++) {
			System.out.println("FirstDayOfMonth(" + i + ") = "
					+ dateHelper.getFirstDayOfMonth(i));
			System.out.println("LastDayOfMonth(" + i + ") = "
					+ dateHelper.getLastDayOfMonth(i));
		}

		/* Week */
		for (int i = -5; i <= 5; i++) {
			System.out.println("FirstDayOfWeek(" + i + ") = "
					+ dateHelper.getFirstDayOfWeek(i));
			System.out.println("LastDayOfWeek(" + i + ") = "
					+ dateHelper.getLastDayOfWeek(i));
		}

		/* Tendays */
		for (int i = -5; i <= 5; i++) {
			System.out.println("FirstDayOfTendays(" + i + ") = "
					+ dateHelper.getFirstDayOfTendays(i));
			System.out.println("LastDayOfTendays(" + i + ") = "
					+ dateHelper.getLastDayOfTendays(i));
		}
		
	}
	
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYYIMMIDD = "yyyy/MM/dd";
	public static final String Y_M_D_H_M_S = "yyyy-MM-dd HH:mm:ss";

	/**
	 * if string1 is yyyy-MM-dd return yyyy/MM/dd , and vice versa
	 */
	public static String formatChange(String string1) {
		String string2 = null;
		if (string1.contains("-")) {
			string2 = string1.replace('-', '/');
		} else if (string1.contains("/")) {
			string2 = string1.replace('/', '-');
		}
		return string2;
	}

	/**
	 * String --> java.util.Date
	 */
	public static Date string2date(String string, String format) {
		
		if (string == null || string.trim().length() == 0) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(string);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * String --> java.util.Date ,the format is default yyyy-MM-dd
	 */
	public static Date string2date(String string) {
		if (string == null || string.trim().length() == 0) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
		try {
			return sdf.parse(string);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * java.util.Date --> String
	 */
	public static String date2string(Date date, String format) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * java.util.Date -- > java.sql.Date
	 */
	public static java.sql.Date utilDate2sqlDate(Date uDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.sql.Date sDate = new java.sql.Date(uDate.getTime());
		return sDate;
	}

	/**
	 * java.sql.Date --> java.util.Date is auto.
	 */

	/**
	 * java.util.Date --> java.sql.Timestamp
	 */
	public static java.sql.Timestamp utilDate2sqlTimestamp(Date uDate) {
		Timestamp timestamp = new Timestamp(uDate.getTime());
		return timestamp;
	}

	/**
	 * java.sql.Timestamp --> java.util.Date
	 */
	public static Date sqlTimestamp2utilDate(Timestamp timestamp) {
		// Timestamp timestamp = new Timestamp(new Date().getTime());
		String time = timestamp.toLocaleString();
		// System.out.println(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(time);
			System.out.println(date);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * java.sql.Timestamp --> LocaleString not include nanos
	 */
	public static String sqlTimestamp2localeString(Timestamp timestamp) {
		String time = timestamp.toLocaleString();
		return time;
	}

	/**
	 * java.sql.Timestamp --> String include nanos
	 */
	public static String sqlTimestamp2string(Timestamp timestamp) {
		String string = timestamp.toString();
		return string;
	}

	/**
	 * String --> java.sql.Timestamp the format is
	 * "yyyy-MM-dd HH:mm:ss.nnnnnnn", nnn can omit.
	 */
	public static Timestamp string2sqlTimestamp(String time) {
		Timestamp timestamp = Timestamp.valueOf(time);
		System.out.println(timestamp.toString());
		return timestamp;
	}

	/**
	 * compare two util.Date ,if date1 is earlier return -1,equal return 0,
	 * later return 1
	 */
	public static int compareDate(Date date1, Date date2) {
		int result = date1.compareTo(date2);
		return result;
	}

	/**
	 * compare two sql.Timestamp ,if timestamp1 is earlier return -1,equal
	 * return 0, later return 1
	 */
	public static int compareTimestamp(Timestamp timestamp1,
			Timestamp timestamp2) {
		int result = timestamp1.compareTo(timestamp2);
		return result;
	}

	/**
	 *  向前或者向后推多少天
	 * @param date 当前日期的 str格式
	 * @param len  要推的天数
	 * @return 返回目标日期
	 */
	public static String dayMove(String date, int len) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(date));
			cal.add(Calendar.DATE, len);
			return sdf.format(cal.getTime());
		} catch (Exception e) {
			return date;
		}
	}
	
	/**
	 *  向前或者向后推多少月
	 * @param date 当前日期的str格式
	 * @param len  要推的月数
	 * @return 返回目标日期
	 */

	public static String monthMove(String date, int len) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(date));
			cal.add(Calendar.MONTH, len);
			return sdf.format(cal.getTime());
		} catch (Exception e) {
			return date;
		}
	}
	
	
	/** 
	   * 计算出离当前日期datas天的日期,若datas小于0表示当前日期之前datas天，若datas大于0表当前日期之后datas天 
	   * 
	   * @param 要计算的天数 
	   * @return 得到日期 
	   */ 
	  public static Date getDate(int datas) { 
	    GregorianCalendar calendar = new GregorianCalendar(); 
	    calendar.add(GregorianCalendar.DATE, datas); 
	    String begin = new java.sql.Date(calendar.getTime().getTime()).toString(); 
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	    Date beginDate = null; 
	    try { 
	      beginDate = sdf.parse(begin); 
	    } catch (ParseException e) { 
	      e.printStackTrace(); 
	    } 
	    return beginDate; 
	  } 

	  /** 
	   * 计算出离特定的日期datas天的日期,若datas小于0表示往前推，当前日期之前datas天，若datas大于0表当前日期之后datas天 
	   * 
	   * @param 要计算的天数 
	   * @return 得到日期 
	   */ 
	  public static Date getDate(Date beginDate, int datas) { 
	    Calendar beginCal=Calendar.getInstance();
	    beginCal.setTime(beginDate); 
	    GregorianCalendar calendar = new GregorianCalendar(beginCal.get(Calendar.YEAR),beginCal.get(Calendar.MONTH),beginCal.get(Calendar.DATE)); 
	    calendar.add(GregorianCalendar.DATE, datas); 
	    String begin = new java.sql.Date(calendar.getTime().getTime()).toString(); 
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	    Date endDate = null; 
	    try { 
	      endDate = sdf.parse(begin); 
	    } catch (ParseException e) { 
	      e.printStackTrace(); 
	    } 
	    return endDate; 
	  } 

	public static void testmain() {
		// Date date2 = TimeUtils.string2date("2010-05-05", YYYY_MM_DD);
		// Date date1 = new Date();
		// System.out.println(TimeUtils.compareDate(date1, date2));
		System.out.println(formatChange(YYYY_MM_DD));
		Date target = getDate(-60);
		System.out.println(target);
		String nowStr = MyDateUtils.formatDbDateStr(target);
		System.out.println(nowStr);
		
		
		
		Date beginDate = null;
		try {
			beginDate = MyDateUtils.parseSimpleDate("2012-01-01");
			System.out.println(beginDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Date target2 = getDate(beginDate, -90);
		System.out.println(target2);
		
		String nowStr2 = MyDateUtils.formatDbDateStr(target2);
		System.out.println(nowStr2);
		
		
	}
	
	 // date and time
    private static int mYear;
    private static int mMonth;
    private static int mDay;
    private static int mHour;
    private static int mMinute;
	
    /**
     * 获取当前时间， 以24为制，比如晚上7点，返回就是19
     */
	public static int getCurrentHour(){
		
		 final Calendar c = Calendar.getInstance();
	        mYear = c.get(Calendar.YEAR);
	        mMonth = c.get(Calendar.MONTH);
	        mDay = c.get(Calendar.DAY_OF_MONTH);
	        mHour = c.get(Calendar.HOUR_OF_DAY);
	        mMinute = c.get(Calendar.MINUTE);
	        return mHour;
	}
	
	
	public static  int getHowManyHoursBetween(long lastmilliseconds, long milliseconds){
		//检查时间差。。如果超出了5分钟。就进行替换操作
		long l= milliseconds - lastmilliseconds;
		int day=(int) (l/(24*60*60*1000));
		int hour=(int) (l/(60*60*1000)-day*24);
		int min=(int) ((l/(60*1000))-day*24*60-hour*60);
		int s=(int) (l/1000-day*24*60*60-hour*60*60-min*60);
		
//		System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
		
		//光看以分钟 为单位， 间隔多少分钟
		int min2=(int) ((l/(60*1000)));
		int hour2 = (int) (l/(60*60*1000)); //以小时为单位，不要以XX天XX时
		return hour2;
	}
	
	public static  int getHowManyDaysBetween(long lastmilliseconds, long milliseconds){
		//检查时间差。。如果超出了5分钟。就进行替换操作
		long l= milliseconds - lastmilliseconds;
		int day=(int) (l/(24*60*60*1000));
		int hour=(int) (l/(60*60*1000)-day*24);
		int min=(int) ((l/(60*1000))-day*24*60-hour*60);
		int s=(int) (l/1000-day*24*60*60-hour*60*60-min*60);
		
		//光看以分钟 为单位， 间隔多少分钟
		int min2=(int) ((l/(60*1000)));
		int hour2 = (int) (l/(60*60*1000)); //以小时为单位，不要以XX天XX时
		return day;
	}
}
