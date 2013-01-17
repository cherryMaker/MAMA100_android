package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;

/**
 * Let's购 - 第三方登录 或者 登录后， 第三方绑定调用统一的请求req
 * 
 * @author aihua 2012-08-03
 */
public class LoginByThirdPartyUserReq extends BaseReq {
	
	//必须参数:

	/**
	  * 第三方帐号用户id
	  */
	 private String uid;

	 /**
	  * 第三方帐号用户类型
	  */
	 private String userType;

	 /**
	  * 第三方帐号token
	  */
	 private String access_token;
	 
	 
	//可选参数:
	/**
	  * 第三方帐号token的有效期
	  */

	 private String token_expire_date;
	 
	 
	 
	 
	 

	public String getUid() {
		return uid;
	}






	public void setUid(String uid) {
		this.uid = uid;
	}






	public String getUserType() {
		return userType;
	}






	public void setUserType(String userType) {
		this.userType = userType;
	}






	public String getAccess_token() {
		return access_token;
	}






	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}






	public String getToken_expire_date() {
		return token_expire_date;
	}






	public void setToken_expire_date(String token_expire_date) {
		this.token_expire_date = token_expire_date;
	}






	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {

		if (!super.validate(response)) {
			return false;
		}

//		if (StringUtils.isBlank(access_token)) {
//			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
//			response.setDesc("必须有用户accesstoken");
//			return false;
//		}
//
//		if (StringUtils.isBlank(uid)) {
//			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
//			response.setDesc("必须有用户uid");
//			return false;
//		}
//		if (StringUtils.isBlank(userType)) {
//			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
//			response.setDesc("必须有微博类型");
//			return false;
//		}

		return true;
	}

}
