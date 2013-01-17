package com.mama100.android.member.domain.point;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * 兑换码应答类
 * @author eco
 *
 */
public class GetExchangeCodeRes extends BaseRes{
	
	/**
	 * 兑换码，6位
	 */
	@Expose
	private String exchangeCode;
	
	/**
	 * 兑换码有效期，单位秒
	 */
	@Expose
	private String activeTime;

	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}

	public String getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(String activeTime) {
		this.activeTime = activeTime;
	}
	

}
