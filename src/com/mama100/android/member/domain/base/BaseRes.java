package com.mama100.android.member.domain.base;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * @title 会员营销系统 - 基础应答消息
 * @description 如果没有特殊返回的字段,此类也可直接使用
 * @author 尹泉
 * @date 2012-3-29
 */
public class BaseRes implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 交易流水号
	 */
	@Expose
	private String tsno;
	
	/**
	 * 处理结果代码<br>
	 * 100 - 成功
	 */
	@Expose
	private String code;
	
	/**
	 * 结果描述<br>
	 */
	@Expose
	private String desc;
	
	/**
	 * 登录已过期,请重新去SSO验证 
	 * 0-不需要 1-需要
	 */
	@Expose
	private String needSsoLogin;
	
	
	public String getTsno() {
		return tsno;
	}

	public void setTsno(String tsno) {
		this.tsno = tsno;
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

	public String getNeedSsoLogin() {
		return needSsoLogin;
	}

	public void setNeedSsoLogin(String needSsoLogin) {
		this.needSsoLogin = needSsoLogin;
	}

}
