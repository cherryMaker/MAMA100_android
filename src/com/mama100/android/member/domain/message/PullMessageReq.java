package com.mama100.android.member.domain.message;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;

/**
 * Let's购 - 用于后台服务定时去服务器获取一条消息
 * 
 * @author jimmy
 */
public class PullMessageReq extends BaseReq {

	/**
	 * 用户名
	 */
	private String uname;

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {

		if (!super.validate(response)) {
			return false;
		}

		if (StringUtils.isBlank(uname)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须输入用户名");
			return false;
		}

		return true;
	}

}
