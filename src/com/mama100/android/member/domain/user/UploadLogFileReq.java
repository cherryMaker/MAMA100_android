package com.mama100.android.member.domain.user;

import java.io.File;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;


/**
 * Let's购 - 上传Log日志
 * http://192.168.72.91:8080/member/user/updateInfo.action?
 * devid=xxx&nickname=mico&babySex=boy&babyName=bb&babyBirth=2012-7-18
 * @author aihua.yan  2012-08-03
 */
	public class UploadLogFileReq extends BaseReq {

		/**
		 * 设备id
		 */
		private String deviceId;	
		
		public String getDeviceId() {
			return deviceId;
		}
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
	
}
