package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;


/**
 * Let's购 - 注册应答请求
 * @author edwar
 */
//public class RegisterReq extends MktBasePropertyReq {
	public class RegisterReq extends BaseReq {

		/**
		 * 邮箱
		 */
		private String email;	
		

	public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

	/**
	 * 登录密码
	 */
	private String pwd;
	
	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {
		
		if (!super.validate(response)) {
			return false;
		}

		if (StringUtils.isBlank(pwd)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须输入登录密码");
			return false;
		}
		
		return true;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}


}
