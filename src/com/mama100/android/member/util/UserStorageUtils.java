package com.mama100.android.member.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;

/**
 * @author edwar yan
 * @since 2012-8-27
 * @description 用于备份用户信息,一期不做
 */
public class UserStorageUtils {

	// 没有用到， 直接用BasicApplication.getInstance().getUsername() 来代替
	// private static String STORAGE_FILE_NAME;

	public static String getShareValue(Context context, String key) {
		return getUserSharePreference(context).getString(key, "");
	}

	public static void setShareValue(Context context, String key, String value) {
		SharedPreferences.Editor editor = getUserSharePreference(context)
				.edit();
		editor.putString(key, value).commit();
	}

	/*********************************************************
	 * 大批量操作 - 保存
	 **********************************************************/
	
	// 一次保存多个值，减少消耗
	public static void setShareValue(Context context, Map<String, String> map) {
		SharedPreferences.Editor editor = getUserSharePreference(context)
				.edit();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			LogUtils.logi("UserStorageUtils", " " + key + ":" + value + "\t");
			editor.putString(key, value);
		}
		editor.commit();
	}
	

	/**
	 * 一次保存所有用户信息
	 * 
	 * @param array
	 *            ,字符串数组， 有4个元素，依次代表：nickname, babyname,babysex,babybirth
	 */
	public static void storeUserAllInfo(Context mContext, String[] array) {
		UserStorageUtils.setShareValue(mContext, AppConstants.NICK_NAME,
				array[0]);
		UserStorageUtils.setShareValue(mContext, AppConstants.BABY_NAME,
				array[1]);
		UserStorageUtils.setShareValue(mContext, AppConstants.BABY_SEX,
				array[2]);
		UserStorageUtils.setShareValue(mContext, AppConstants.BABY_BIRTH,
				array[3]);
	}

	/**
	 * 一次保存所有用户信息2
	 * 
	 * @param 依次代表
	 *            ：nickname, babyname,babysex,babybirth
	 */
	public static void storeUserAllInfo2(Context mContext, String nickname,
			String babyname, String babysex, String babybirth) {
		UserStorageUtils.setShareValue(mContext, AppConstants.NICK_NAME,
				nickname);
		UserStorageUtils.setShareValue(mContext, AppConstants.BABY_NAME,
				babyname);
		UserStorageUtils
				.setShareValue(mContext, AppConstants.BABY_SEX, babysex);
		UserStorageUtils.setShareValue(mContext, AppConstants.BABY_BIRTH,
				babybirth);
	}

	
	/*********************************************************
	 * 大批量操作 - 读取
	 **********************************************************/
	public static Map getShareValue(Context context) {
		Map<String, String> map = new HashMap<String,String>();
		SharedPreferences sp = getUserSharePreference(context);
		buildMapItem(AppConstants.NICK_NAME, sp, map);
		buildMapItem(AppConstants.BABY_BIRTH, sp, map);
		buildMapItem(AppConstants.BABY_NAME, sp, map);
		buildMapItem(AppConstants.BABY_SEX, sp, map);
		buildMapItem(AppConstants.REG_POINT_BALANCE, sp, map);
		return map;
//		return getUserSharePreference(context).getString(key, "");
	}

	
	

	private static void buildMapItem(String key, SharedPreferences sp, Map<String, String> map) {
		map.put(key, sp.getString(key, ""));
	}

	// 返回 sharepreference
	private static SharedPreferences getUserSharePreference(Context context) {
		if (BasicApplication.getInstance()!=null&&
				BasicApplication.getInstance().getUsername()!=null
				&&!BasicApplication.getInstance().getUsername().endsWith("")) {
			return context.getSharedPreferences(BasicApplication.getInstance()
					.getUsername(), Context.MODE_PRIVATE);
		}else{
			return context.getSharedPreferences("tempuser", Context.MODE_PRIVATE);
		}
	}

	// 删除保留值。。
	public static void removeShareValue(Context context, String key) {
		SharedPreferences.Editor editor = getUserSharePreference(context)
				.edit();
		editor.remove(key);
		editor.commit();
	}

	public static void removeShareValues(Context context, String[] keys) {
		SharedPreferences.Editor editor = getUserSharePreference(context)
				.edit();

		for (String key : keys) {
			editor.remove(key);
		}

		editor.commit();
	}

	public static boolean getBooleanShareValue(Context context, String key) {
		return getUserSharePreference(context).getBoolean(key, true);
	}

	public static void setBooleanShareValue(Context context, String key,
			boolean flag) {
		SharedPreferences.Editor editor = getUserSharePreference(context)
				.edit();
		editor.putBoolean(key, flag).commit();
	}

	/*********************************
	 * 保存 或者 读取
	 ************************************/

	/**
	 * 保存用户昵称
	 * 
	 * @param nickname
	 */
	public static void storeUserNickName(Context mContext, String nickname) {
		UserStorageUtils.setShareValue(mContext, AppConstants.NICK_NAME,
				nickname);
	}

	/**
	 * 获取保存的用户昵称
	 */
	public static String getUserNickName(Context mContext) {
		return UserStorageUtils.getShareValue(mContext, AppConstants.NICK_NAME);
	}

	/**
	 * 保存用户积分余额
	 * 
	 * @param balance
	 */
	public static void storeUserRegBalance(Context mContext, String balance) {
		UserStorageUtils.setShareValue(mContext,
				AppConstants.REG_POINT_BALANCE, balance);
	}

	/**
	 * 获取保存的用户积分余额
	 */
	public static String getUserRegBalance(Context mContext) {
		return UserStorageUtils.getShareValue(mContext,
				AppConstants.REG_POINT_BALANCE);
	}

	/**
	 * 保存用户宝宝生日
	 * 
	 * @param babybirth
	 */
	public static void storeUserBabyBirth(Context mContext, String babybirth) {
		UserStorageUtils.setShareValue(mContext, AppConstants.BABY_BIRTH,
				babybirth);
	}

	/**
	 * 获取保存的用户宝宝生日
	 */
	public static String getUserBabyBirth(Context mContext) {
		return UserStorageUtils
				.getShareValue(mContext, AppConstants.BABY_BIRTH);
	}

	/**
	 * 保存用户宝宝性别
	 * 
	 * @param babysex
	 */
	public static void storeUserBabySex(Context mContext, String babysex) {
		UserStorageUtils
				.setShareValue(mContext, AppConstants.BABY_SEX, babysex);
	}

	/**
	 * 获取保存的用户宝宝性别
	 */
	public static String getUserBabySex(Context mContext) {
		return UserStorageUtils.getShareValue(mContext, AppConstants.BABY_SEX);
	}

	/**
	 * 保存用户宝宝姓名
	 * 
	 * @param babyname
	 */
	public static void storeUserBabyName(Context mContext, String babyname) {
		UserStorageUtils.setShareValue(mContext, AppConstants.BABY_NAME,
				babyname);
	}

	/**
	 * 获取保存的用户宝宝姓名
	 */
	public static String getUserNickname(Context mContext) {
		return UserStorageUtils.getShareValue(mContext, AppConstants.BABY_NAME);
	}

}
