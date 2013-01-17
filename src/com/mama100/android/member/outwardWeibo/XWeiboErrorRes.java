package com.mama100.android.member.outwardWeibo;

import com.google.gson.annotations.Expose;


/**
 * 
 * <b>Description:</b> 新浪Http请求，返回JSON，提示错误的应答类
 * 如JSON：<br>{
	<br>"request" : "/statuses/home_timeline.json",
	<br>"error_code" : "20502",
	<br>"error" : "Need you follow uid."
   <br> }
   <br>error_code的意义具体见{@link XWeiboException}
 * @version 1.0 
 * @author ecoo
 */
public class XWeiboErrorRes
extends XWeiboBaseRes{
	private static final long serialVersionUID = 1L;

	/**
	 * 新浪Http请求错误码
	 */
	@Expose
	private String error_code;
	
	/**
	 * 新浪Http请求错误描述
	 */
	@Expose
	private String error;

	/**
	 * 请求地址
	 */
	private String request;
	
	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}
	
}
