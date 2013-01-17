package com.mama100.android.member.domain.message;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;

/**
 * Let's购 - 用于在消息界面刚打开时或者下拉刷新时，去服务器获取一批消息
 * 
 * @author aihua 2012-08-03
 */
public class DetailMessageReq extends BaseReq {

	/**
	 * messageId
	 */
	private String id;
	
	/**
	 * 
	 * uname
	 */
	
	private String uname;
	
	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {

		if (!super.validate(response)) {
			return false;
		}

		if (StringUtils.isBlank(id)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须提供消息id");
			return false;
		}
	

		return true;
	}

}
