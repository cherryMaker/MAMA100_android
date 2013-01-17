package com.mama100.android.member.domain.point;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * 兑换码状态应答类
 * @author eco
 *
 */
public class ExchangeCodeStatusRes extends BaseRes{
	
	/**
	 * 兑换码状态有：0-未使用、1-已使用、2-已过期
	 */
	@Expose
	private String codeStatus;
	
	@Expose
	private String statusDesc;

	
	/**
	 * 兑换码状态有：0-未使用、1-已使用、2-已过期
	 */
	public String getCodeStatus() {
		return codeStatus;
	}

	/**
	 * 兑换码状态有：0-未使用、1-已使用、2-已过期
	 */
	public void setCodeStatus(String codeStatus) {
		this.codeStatus = codeStatus;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	

}
