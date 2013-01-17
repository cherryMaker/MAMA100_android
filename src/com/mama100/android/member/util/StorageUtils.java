package com.mama100.android.member.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mama100.android.member.global.AppConstants;


/**
 * @author edwar yan
 * @since 2012-6-10
 * @description 用于
 */
public class StorageUtils {

	public static final String STORAGE_FILE_NAME = "biostime";
	
	public static String getShareValue(Context context, String key) {
		return context.getSharedPreferences(STORAGE_FILE_NAME,
				Context.MODE_PRIVATE).getString(key, "");
	}

	public static void setShareValue(Context context, String key, String value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(
				STORAGE_FILE_NAME, Context.MODE_PRIVATE).edit();
		editor.putString(key, value).commit();
	}

	
	//删除保留值。。
	public static void removeShareValue(Context context, String key) {
		SharedPreferences.Editor editor = context.getSharedPreferences(
				STORAGE_FILE_NAME, Context.MODE_PRIVATE).edit();
		editor.remove(key);
		editor.commit();
	}

	public static void removeShareValues(Context context, String[] keys) {

		SharedPreferences.Editor editor = context.getSharedPreferences(
				STORAGE_FILE_NAME, Context.MODE_PRIVATE).edit();

		for (String key : keys) {
			editor.remove(key);
		}

		editor.commit();
	}

	
	
	public static boolean getBooleanShareValue(Context context, String key) {
		return context.getSharedPreferences(STORAGE_FILE_NAME,
				Context.MODE_PRIVATE).getBoolean(key, true);
	}
	
	public static void setBooleanShareValue(Context context, String key, boolean flag) {
		SharedPreferences.Editor editor = context.getSharedPreferences(
				STORAGE_FILE_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean(key, flag).commit();
	}
	
	/*********************************
	 *   保存  或者  读取
	 ************************************/
	/**
	 * 在手机客户端保存用户名和密码
	 * @param account
	 * @param encodedPwd
	 */
	public static void storeLoginInfoInClient(Context mContext, String account, String encodedPwd) {
		StorageUtils.setShareValue(mContext, AppConstants.LOGIN_ACCOUNT_KEY, account);
		StorageUtils.setShareValue(mContext, AppConstants.LOGIN_PWD_KEY, encodedPwd);
		
	}
	
	/**
	 * 在手机客户端保存用户名
	 * @param account
	 * @param encodedPwd
	 */
	public static void storeLoginAccountInClient(Context mContext , String account) {
		StorageUtils.setShareValue(mContext, AppConstants.LOGIN_ACCOUNT_KEY, account);
		
	}
	
	/**
	 * 获取上一次登录成功的用户名
	 */
	public static String getLastLoginAccount(Context mContext) {
		return StorageUtils.getShareValue(mContext, AppConstants.LOGIN_ACCOUNT_KEY);
	}
	
	
	/**
	 * 在手机客户端保存TGT
	 * @param account
	 * @param encodedPwd
	 */
	public static void storeLoginTGTInClient(Context mContext , String tgt) {
		StorageUtils.setShareValue(mContext, AppConstants.LOGIN_TGT, tgt);
		
	}
	
	/**
	 * 获取上一次登录成功的TGT
	 */
	public static String getLoginTGT(Context mContext) {
		return StorageUtils.getShareValue(mContext, AppConstants.LOGIN_TGT);
	}
	
	/**
	 * 保存sessionId
	 * @param mContext
	 * @param str
	 */
	public static void storeSessionId(Context mContext, String str){
		StorageUtils.setShareValue(mContext, AppConstants.SESSIONID, str);
	}
	
	/**
	 * 获取sessionId
	 * @param mContext
	 * @param str
	 */
	public static String getSessionId(Context mContext){
		return StorageUtils.getShareValue(mContext, AppConstants.SESSIONID);
	}
	
	
	/**
	 * 删除sessionId
	 * @param mContext
	 * @param str
	 */
	public static void removeSessionId(Context mContext){
		StorageUtils.removeShareValue(mContext, AppConstants.SESSIONID);
	}
	
	
	
}
