package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;

/**
 * Let's购 - 修改密码
 * 
 * @author aihua 2012-08-03
 */
public class ChangePwdReq extends BaseReq {

	/**
	 * 旧密码
	 */
	private String oldPwd;

	/**
	 * 新密码
	 */
	private String newPwd;


	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {

		if (!super.validate(response)) {
			return false;
		}

		if (StringUtils.isBlank(oldPwd)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须输入原始密码");
			return false;
		}

		if (StringUtils.isBlank(newPwd)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须输入新密码");
			return false;
		}

		return true;
	}

}
