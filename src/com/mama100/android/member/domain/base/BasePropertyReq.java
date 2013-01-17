package com.mama100.android.member.domain.base;

import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;


/**
 * @title 手机详细属性基类请求消息
 * @description 
 * @author 尹泉
 * @date 2011-10-12
 */
public abstract class BasePropertyReq extends BaseReq {

	/**
	 * 手机操作系统 如: android,ios
	 */
	private String os;
	
	/**
	 * 操作系统版本号 如: 2.3
	 */
	private String osver;
	
	/**
	 * 手机品牌 如: HTC
	 */
	private String brand;
	
	/**
	 * 手机型号 如: G7
	 */
	private String model;
	
	/**
	 * 客户端内部版本号 如: 2
	 */
	private String ver;

	/**
	 * 客户端显示给用户的版本号 如: 1.0.22
	 */
	private String vernm;
	
	/**
	 * 屏幕宽（像素） 如: 480
	 */
	private String width;
	
	/**
	 * 屏幕高（像素） 如: 800
	 */
	private String height;
	
	/**
	 * 屏幕密度 dots-per-inch
	 */
	private String dpi;

	
	/**
	 * 手机SIM卡ICCID码<br>
	 */
	private String iccid;
	
	
	/**
	 * apk渠道
	 */
	private String channel;
	

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}
	

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * 验证基类中的未加密字段
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {

		if (!super.validate(response)) {
			return false;
		}
		
		if (StringUtils.isBlank(os)) {
			response.setCode(DeviceResponseCode.INVALID_OS);
			response.setDesc("操作系统格式不正确");
			return false;
		}
		
		if (StringUtils.isBlank(osver)) {
			response.setCode(DeviceResponseCode.INVALID_OS_VER);
			response.setDesc("操作系统版本号格式不正确");
			return false;
		}
		
		if (StringUtils.isBlank(brand)) {
			response.setCode(DeviceResponseCode.INVALID_BRAND);
			response.setDesc("品牌格式不正确");
			return false;
		}

		if (StringUtils.isBlank(model)) {
			response.setCode(DeviceResponseCode.INVALID_MODEL);
			response.setDesc("型号格式不正确");
			return false;
		}

		if (StringUtils.isBlank(ver) || !StringUtils.isNumeric(ver)) {
			response.setCode(DeviceResponseCode.INVALID_APP_VER);
			response.setDesc("客户端内部版本号格式不正确");
			return false;
		}

		if (StringUtils.isBlank(vernm)) {
			response.setCode(DeviceResponseCode.INVALID_APP_VER_NAME);
			response.setDesc("客户端版本号格式不正确");
			return false;
		}

		if (StringUtils.isBlank(width) || !StringUtils.isNumeric(width)) {
			response.setCode(DeviceResponseCode.INVALID_SCREEN_WIDTH);
			response.setDesc("屏幕像素宽格式不正确");
			return false;
		}

		if (StringUtils.isBlank(height) || !StringUtils.isNumeric(height)) {
			response.setCode(DeviceResponseCode.INVALID_SCREEN_HIGHT);
			response.setDesc("屏幕像素高格式不正确");
			return false;
		}

		if (StringUtils.isBlank(dpi) || !StringUtils.isNumeric(dpi)) {
			response.setCode(DeviceResponseCode.INVALID_SCREEN_DPI);
			response.setDesc("屏幕密度格式不正确");
			return false;
		}
		
		if (StringUtils.isBlank(channel)) {
			response.setCode(DeviceResponseCode.INVALID_CHANNEL);
			response.setDesc("渠道编号不正确");
			return false;
		}
		
		return true;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsver() {
		return osver;
	}

	public void setOsver(String osver) {
		this.osver = osver;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getVernm() {
		return vernm;
	}

	public void setVernm(String vernm) {
		this.vernm = vernm;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getDpi() {
		return dpi;
	}

	public void setDpi(String dpi) {
		this.dpi = dpi;
	}
	
	
}
