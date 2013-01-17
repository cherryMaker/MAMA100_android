package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;


/**
 * Let's购 - 注册应答请求
 * @author jimmy
 */
//public class RegisterReq extends MktBasePropertyReq {
	public class GetVerifyCodeReq extends BaseReq {

		/**
		 * 电话
		 */
		private String mobile;	
		
		/*
		 * 验证码类型
		 */
		private String type;
	


	public String getType() {
			return type;
		}




		public void setType(String type) {
			this.type = type;
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
			response.setDesc("必须输入手机号");
			return false;
		}
		
		return true;
	}




	public String getMobile() {
		return mobile;
	}




	public void setMobile(String mobile) {
		this.mobile = mobile;
	}




	
}
