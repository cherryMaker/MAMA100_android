package com.mama100.android.member.businesslayer;

import android.content.Context;

import com.mama100.android.member.domain.sys.CheckAppVerReq;
import com.mama100.android.member.domain.sys.CheckAppVerRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.util.LogUtils;

/**
 * 系统业务数据管理
 * 
 * @author jimmy
 */
public class SystemDataManager extends ClientDataSupport {

	private static SystemDataManager instance;

	public SystemDataManager(Context context) {
		super(context);
	}

	public static synchronized SystemDataManager getInstance(Context context) {

		if (instance == null) {
			instance = new SystemDataManager(context);
		}

		return instance;
	}

	/**
	 * 检查服务端软件版本号
	 * 
	 * @param request
	 * @return
	 */
	public CheckAppVerRes checkAppVersion(CheckAppVerReq request) {
		String httpAddr = getHttpIpAddress() + AppConstants.CHECK_VERSION_ACTION;
		CheckAppVerRes response = (CheckAppVerRes) postData(request,
				CheckAppVerRes.class, httpAddr);

		return response;
	}

}
