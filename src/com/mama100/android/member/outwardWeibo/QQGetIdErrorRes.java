package com.mama100.android.member.outwardWeibo;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.outwardHttp.OutwardBaseRes;

/**
 * 
 * <b>Description:</b> 获取腾讯用户id请求，返回JSON，提示错误的应答类
 * @author ecoo
 */
public class QQGetIdErrorRes
extends OutwardBaseRes{
	private static final long serialVersionUID = 1L;

	/**
	 * 获取腾讯用户id错误码
	 */
	@Expose
	private String error;
	
	/**
	 * 获取腾讯用户id错误描述
	 */
	@Expose
	private String error_description;


	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getError_description() {
		return error_description;
	}

	public void setError_description(String error_description) {
		this.error_description = error_description;
	}

}
