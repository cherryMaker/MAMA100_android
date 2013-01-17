/**
 * 
 */
package com.mama100.android.member.util;

import android.util.Log;

/**
 * @author Mico
 * @since 2012-3-31
 * 
 */
public class LogUtils {

	private static boolean D = true;
	
	
	
	
	/**
	 * 打印普通消息 （常用）
	 *  added by aihua.yan 2012-04-09
	 * @param clazz
	 * @param msg
	 */
	public static void logi(String tag, String msg) {
		if(D)
		Log.i(tag, msg);
	}
	
	/**
	 * 打印普通消息 （常用）
	 *  added by aihua.yan 2012-04-09
	 * @param clazz
	 * @param msg
	 */
	public static void logd(String tag, String msg) {
		if(D)
			Log.d(tag, msg);
	}
	
	/**
	 * 打印普通消息 （常用）
	 *  added by aihua.yan 2012-04-09
	 * @param clazz
	 * @param msg
	 */
	public static void logv(String tag, String msg) {
		if(D)
			Log.v(tag, msg);
	}
	
	/**
	 * 打印普通消息 （常用）
	 *  added by aihua.yan 2012-04-09
	 * @param clazz
	 * @param msg
	 */
	public static void logw(String tag, String msg) {
		if(D)
			Log.w(tag, msg);
	}
	
	
	
	/**
	 * 打印普通消息 （常用）
	 *  added by aihua.yan 2012-04-09
	 * @param clazz
	 * @param msg
	 */
	public static void loge(String tag, String msg) {
		Log.e(tag, msg);
	}
	
	
	/**
	 * 打印普通消息 （常用）
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void logi(Class<?> clazz, String msg) {
		Log.i(clazz.getSimpleName(), msg);
	}

	/**
	 * 打印普通消息 （常用）
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void logi(Class<?> clazz, String msg, Throwable tr) {
		Log.i(clazz.getSimpleName(), msg, tr);
	}

	/**
	 * 打印普通消息 （常用）
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void logi(Class<?> clazz, Exception e) {
		Log.i(clazz.getSimpleName(), e.getLocalizedMessage());
	}

	/**
	 * 打印调试信息
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void logd(Class<?> clazz, String msg) {
		Log.d(clazz.getSimpleName(), msg);
	}

	/**
	 * 打印调试信息
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void logd(Class<?> clazz, String msg, Throwable tr) {
		Log.d(clazz.getSimpleName(), msg, tr);
	}

	/**
	 * 打印调试信息
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void logd(Class<?> clazz, Exception e) {
		Log.d(clazz.getSimpleName(), e.getLocalizedMessage());
	}

	
	


	
	
	
	/**
	 * 打印系统错误信息
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void loge(Class<?> clazz, String msg) {
		Log.e(clazz.getSimpleName(), msg);
	}

	/**
	 * 打印系统错误信息
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void loge(Class<?> clazz, String msg, Throwable tr) {
		Log.e(clazz.getSimpleName(), msg, tr);
	}

	/**
	 * 打印系统错误信息
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void loge(Class<?> clazz, Exception e) {
		Log.e(clazz.getSimpleName(), e.getLocalizedMessage());
	}

	/**
	 * 打印警告信息
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void logw(Class<?> clazz, String msg) {
		Log.w(clazz.getSimpleName(), msg);
	}

	/**
	 * 打印警告信息
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void logw(Class<?> clazz, String msg, Throwable tr) {
		Log.w(clazz.getSimpleName(), msg, tr);
	}

	/**
	 * 打印警告信息
	 * 
	 * @param clazz
	 * @param msg
	 */
	public static void logw(Class<?> clazz, Exception e) {
		Log.w(clazz.getSimpleName(), e.getLocalizedMessage());
	}
	
	

	/**
	 * 输出完整的错误堆栈
	 * @param e
	 * @return
	 */
	public static String getStackTrace(Throwable e) {
		
		StringBuffer stack = new StringBuffer();
		stack.append(e);
		stack.append("\r\n");
		stack.append(e.getMessage());
		stack.append("\r\n");

		Throwable rootCause = e.getCause();

		while (rootCause != null) {
			stack.append("Root Cause:\r\n");
			stack.append(rootCause);
			stack.append("\r\n");
			stack.append(rootCause.getMessage());
			stack.append("\r\n");
			stack.append("StackTrace:\r\n");
			stack.append(rootCause);
			stack.append("\r\n");
			rootCause = rootCause.getCause();
		}

		for (int i = 0; i < e.getStackTrace().length; i++) {
			stack.append(e.getStackTrace()[i].toString());
			stack.append("\r\n");
		}
		
		return stack.toString();
	}
	
	
}
