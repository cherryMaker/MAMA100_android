package com.mama100.android.member.outwardWeibo;

import com.google.gson.annotations.Expose;

public class QQGetOpenIdRes
extends QQBaseRes{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Expose
	private String openid;

	/**
	 * 返回的腾讯用户的ID
	 * @return
	 */
	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
}
