/**
 * 
 */
package com.mama100.android.member.domain.point;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;

/**
 * @Description: 自助积分时，验证序列号和防伪码的请求
 * 
 * @author aihua
 * 
 * @date 2012-08-03
 */
public class PointVerifyReq extends BaseReq{

	/**
	 * 序列号
	 */
	private String serial;

	/**
	 * 防伪码
	 */
	private String security;
	
	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {

		if (!super.validate(response)) {
			return false;
		}

		if (StringUtils.isBlank(serial)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("请输入序列号");
			return false;
		}
		
		if (StringUtils.isBlank(security)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("请输入防伪码");
			return false;
		}
	

		return true;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

}
