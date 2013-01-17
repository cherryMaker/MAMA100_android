package com.mama100.android.member.domain.point;

import com.mama100.android.member.domain.base.BaseReq;


/**
 * 获取兑换码状态请求
 * @author eco
 *
 */
public class ExchangeCodeStatusReq extends BaseReq{
	
	private String exchangeCode;

	public String getExchangeCode() {
		return exchangeCode;
	}

	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}
}
