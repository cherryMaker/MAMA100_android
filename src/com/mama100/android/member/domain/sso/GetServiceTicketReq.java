package com.mama100.android.member.domain.sso;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;

/**
 * Let's购 - 获取service ticket请求
 * @author jimmy
 */
public class GetServiceTicketReq extends BaseReq {
	
	/**
	 * service url 要访问的资源
	 */
	private String service;

	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {
		
		if (!super.validate(response)) {
			return false;
		}
		
		if (StringUtils.isBlank(service)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("service url必须提供");
			return false;
		}

		return true;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

}
