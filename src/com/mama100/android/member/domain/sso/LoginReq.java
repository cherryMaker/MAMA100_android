package com.mama100.android.member.domain.sso;

import com.mama100.android.member.domain.base.BasePropertyReq;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;

/**
 * Let's购 - 登录应答请求
 * @author jimmy
 */
public class LoginReq extends BaseReq {
	
	/**
	 * 用户账号<br>
	 */
	private String username;	

	/**
	 * 登录密码
	 */
	private String password;	
	
	/***
	 * 是否“记住我“  
	 * true-记住  false-不记住
	 */	
	private String rememberMe;

	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {
		
		if (!super.validate(response)) {
			return false;
		}

		if (this instanceof LoginReq) {
			if (StringUtils.isBlank(username)) {
				response.setCode(DeviceResponseCode.VALIDATE_ERROR);
				response.setDesc("必须输入账号");
				return false;
			}
		}		
		
		if (StringUtils.isBlank(password)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须输入登录密码");
			return false;
		}
		
		return true;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(String rememberMe) {
		this.rememberMe = rememberMe;
	}
	
}
