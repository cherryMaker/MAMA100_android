package com.mama100.android.member.outwardWeibo;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.outwardHttp.OutwardBaseRes;

public class QQBaseRes
extends OutwardBaseRes{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	//腾讯接口返回码，等于0，正确返回。否则错误
	@Expose
	private String ret;
	
	//腾讯接口返回信息
	@Expose
	private String msg;


	/**
	 * 
	 * @return 腾讯接口返回码。等于0，正确返回；否则错误。
	 */
	public String getRet() {
		return ret;
	}


	/**
	 * 
	 * @param ret 
	 */
	public void setRet(String ret) {
		this.ret = ret;
	}


	/**
	 * 腾讯接口返回信息
	 * @return
	 */
	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}


	
}
