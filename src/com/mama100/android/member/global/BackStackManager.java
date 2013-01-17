/*
 * @(#)ActivityManager.java		       version: 0.1 
 * Date:2012-2-3
 *
 * Copyright (c) 2011 CFuture09, Institute of Software, 
 * Guangdong Ocean University, Zhanjiang, GuangDong, China.
 * All rights reserved.
 */
package com.mama100.android.member.global;

import java.util.HashMap;

import java.util.Set;

import com.mama100.android.member.util.LogUtils;

import android.app.Activity;

/**
 * 一个Activity管理器,管理活动的Activity。
 * 至于命名为BackStack,是学习google的叫法，后台栈。
 * 
 * @author Geek_Soledad (66704238@51uc.com)
 */
public class BackStackManager {

	private static BackStackManager activityTaskManager = null;
	private HashMap<String, Activity> activityMap = null;

	private BackStackManager() {
		activityMap = new HashMap<String, Activity>();
	}

	/**
	 * 返回activity管理器的唯一实例对象。
	 * 
	 * @return
	 */
	public static synchronized BackStackManager getInstance() {
		if (activityTaskManager == null) {
			activityTaskManager = new BackStackManager();
		}
		return activityTaskManager;
	}

	/**
	 * 将一个activity添加到管理器。
	 * 
	 * @param activity
	 */
	public Activity putActivity(String name, Activity activity) {
		return activityMap.put(name, activity);
	}

	/**
	 * 得到保存在管理器中的Activity对象。
	 * 
	 * @param name
	 * @return
	 */
	public Activity getActivity(String name) {
		return activityMap.get(name);
	}

	/**
	 * 返回管理器的Activity是否为空。
	 * 
	 * @return 当且当管理器中的Activity对象为空时返回true，否则返回false。
	 */
	public boolean isEmpty() {
		return activityMap.isEmpty();
	}

	/**
	 * 返回管理器中Activity对象的个数。
	 * 
	 * @return 管理器中Activity对象的个数。
	 */
	public int size() {
		return activityMap.size();
	}

	/**
	 * 返回管理器中是否包含指定的名字。
	 * 
	 * @param name
	 *            要查找的名字。
	 * @return 当且仅当包含指定的名字时返回true, 否则返回false。
	 */
	public boolean containsName(String name) {
		return activityMap.containsKey(name);
	}

	/**
	 * 返回管理器中是否包含指定的Activity。
	 * 
	 * @param activity
	 *            要查找的Activity。
	 * @return 当且仅当包含指定的Activity对象时返回true, 否则返回false。
	 */
	public boolean containsActivity(Activity activity) {
		return activityMap.containsValue(activity);
	}

	/**
	 * 关闭所有活动的Activity。
	 */
	public void closeAllActivity() {
		Set<String> activityNames = activityMap.keySet();
		for (String string : activityNames) {
			LogUtils.loge("BackStackManager", ">>> Is finising Activity -" + string);
			finisActivity(activityMap.get(string));
		}
		LogUtils.loge("BackStackManager", ">>> All Activities - finished" + "" );
		activityMap.clear();
	}

	/**
	 * 关闭所有活动的Activity除了指定的一个之外。
	 * 
	 * @param nameSpecified
	 *            指定的不关闭的Activity对象的名字。
	 */
	public void closeAllActivityExceptOne(String nameSpecified) {
		Set<String> activityNames = activityMap.keySet();
		Activity activitySpecified = activityMap.get(nameSpecified);
		for (String name : activityNames) {
			if (name.equals(nameSpecified)) {
				continue;
			}
			finisActivity(activityMap.get(name));
		}
		activityMap.clear();
		activityMap.put(nameSpecified, activitySpecified);
	}

	/**
	 * 移除Activity对象,如果它未结束则结束它。
	 * 
	 * @param name
	 *            Activity对象的名字。
	 */
	public void removeActivity(String name) {
		Activity activity = activityMap.remove(name);
		finisActivity(activity);
	}

	private final void finisActivity(Activity activity) {
		if (activity != null) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
	}
}
