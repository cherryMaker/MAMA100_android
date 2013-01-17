package com.mama100.android.member.bean.update;

import java.io.Serializable;

/**
 * 通用返回结果
 * @author jimmy
 */
public class BaseResult implements Serializable {
	
	private String code;
	
	private String desc;
	
	private String reserve1;
	
	private String reserve2;

	private String reserve3;

	
	public String getFullDesc() {
		return desc  + " - " + code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}
	
}
