package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;


/**
 * Let's购 - 找回密码
 * @author jimmy
 */
	public class FindPwdReq extends BaseReq {

		/**
		 * 手机号码
		 */
		private String mobile;	

	/**
	 * 验证码
	 */
	private String vcode;
	
	
	
	
	

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {
		
		if (!super.validate(response)) {
			return false;
		}

		if (StringUtils.isBlank(mobile)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须输入手机号码");
			return false;
		}
		
		if (StringUtils.isBlank(vcode)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须输入验证码");
			return false;
		}
		
		return true;
	}



}
