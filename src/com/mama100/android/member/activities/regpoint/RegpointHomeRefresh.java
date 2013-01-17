/*package com.mama100.android.member.activities.regpoint;

import com.mama100.android.member.global.BasicApplication;

*//**
 * @desc   积分成功后刷新积分首页金额
 * 
 * @author liyang 
 * 
 * @date   2012-12-20 下午2:16:58 
 * 
 *//*
public class RegpointHomeRefresh {
	
	private static String regpoint;
	
	private static boolean isClear;
	
	public static boolean hasRegpoint() {
		return regpoint != null;
	}

	public static void setRegpoint(String point){
		RegpointHomeRefresh.regpoint = point;
		
		if(regpoint != null){
			//更新积分余额
			String  sBalance = BasicApplication.getInstance().getLastRegpointBalance();
			Integer lBalance = Integer.valueOf(sBalance) + Integer.valueOf(regpoint);
			BasicApplication.getInstance().setLastRegpointBalance(String.valueOf(lBalance));	
		}
	}
	
	public static boolean isClear() {
		return isClear;
	}

	public static void setClear(boolean isClear) {
		RegpointHomeRefresh.isClear = isClear;
	}

	public static String getBalance(){
		return BasicApplication.getInstance().getLastRegpointBalance();
	}
}
*/