package com.mama100.android.member.domain.base;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;

/**
 * 营销360 - 请求基类
 * @author jimmy
 */
public class BaseReq {
	
	/**
	 * 手机设备ID<br>
	 * IMEI 移动设备唯一身份码
	 */
	private String devid;
	
	/**
	 * 交易流水号
	 */
	private String tsno;
	
	
	/**
	 * 验证base request中的字段,并将错误信息设置到response对象
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	public boolean validate(BaseRes response) throws Exception {
		
		if (StringUtils.isBlank(devid)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("设备ID格式不正确");
			return false;
		}
		
		return true;
	}

	/**
	 * 验证终端编号,并将错误信息设置到response对象
	 * @param serialCode 产品序列号(12或14位)
	 * @param response
	 * @return
	 */
	protected boolean validateTerminalCode(String terminalCode, BaseRes response) {
		
 		if (StringUtils.isBlank(terminalCode) || (terminalCode.length() != 5 && 
 				terminalCode.length() != 6) || !StringUtils.isNumeric(terminalCode)) {
			response.setCode(DeviceResponseCode.INVALID_TERMINAL_CODE);
			response.setDesc("终端编号输入错误");
			return false;
 		}
 		
 		return true;
	}
	
	/**
	 * 验证产品序列号,并将错误信息设置到response对象
	 * @param serialCode 产品序列号(12或14位)
	 * @param response
	 * @return
	 */
	public boolean validateSerialCode(String serialCode, BaseRes response) {
		
 		if (StringUtils.isBlank(serialCode) || (serialCode.length() != 12 && 
 				serialCode.length() != 14) || !StringUtils.isNumeric(serialCode)) {
			response.setCode(DeviceResponseCode.INVALID_PRODUCT_SERIAL_CODE);
			response.setDesc("序列号输入错误");
			return false;
 		}
 		
		return true;
	}
	
	/**
	 * 验证产品防伪码,并将错误信息设置到response对象
	 * @param securityCode 产品防伪码(16位)
	 * @param response
	 * @return
	 */
	public boolean validateSecurityCode(String securityCode, BaseRes response) {
		
		if (StringUtils.isBlank(securityCode) || securityCode.length() != 16 || 
				!StringUtils.isNumeric(securityCode)) {
			response.setCode(DeviceResponseCode.INVALID_PRODUCT_SECURITY_CODE);
			response.setDesc("防伪码输入错误");
			return false;
 		}
 		
		return true;
	}
	
	/**
	 * 验证促销员编号
	 * @param code 促销员编号(6位数字)
	 * @param response
	 * @return
	 */
	public boolean validateSalerCode(String code, BaseRes response) {
		
 		if (StringUtils.isBlank(code) || code.length() != 6 || !StringUtils.isNumeric(code)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("促销员工号输入错误");
			return false;
 		}
 		
		return true;
	}
	
	/**
	 * 验证员工编号
	 * @param code 员工编号(4-5位数字)
	 * @param response
	 * @return
	 */
	public boolean validateTcdMgCode(String code, BaseRes response) {
		
 		if (StringUtils.isBlank(code) || (code.length() != 4 && 
 				code.length() != 5&& 
 				code.length() != 6) || !StringUtils.isNumeric(code)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("员工工号输入错误");
			return false;
 		}
 		
		return true;
	}
	
	public String getTsno() {
		return tsno;
	}

	public void setTsno(String tsno) {
		this.tsno = tsno;
	}

	public String getDevid() {
		return devid;
	}

	public void setDevid(String devid) {
		this.devid = devid;
	}
	
}
