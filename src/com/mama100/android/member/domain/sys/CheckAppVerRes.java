package com.mama100.android.member.domain.sys;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * 检查软件版本号应答消息
 * @author Jimmy
 */
public class CheckAppVerRes extends BaseRes {
	
	/**
	 * 服务端最新的版本号, 用于内部判断
	 * 如: 2
	 */
	@Expose
	private String nver;
	
	/**
	 * 服务端最新的版本号名称, 用于显示
	 * 如: 1.0.2
	 */
	@Expose
	private String nvernm;
	
	/**
	 * 需要下载的客户端程序的URL
	 */
	@Expose
	private String appurl;
	
	/**
	 * 升级内容的详细描述
	 */
	@Expose
	private String ctnt;

	public String getNver() {
		return nver;
	}

	public void setNver(String nver) {
		this.nver = nver;
	}

	public String getNvernm() {
		return nvernm;
	}

	public void setNvernm(String nvernm) {
		this.nvernm = nvernm;
	}

	public String getAppurl() {
		return appurl;
	}

	public void setAppurl(String appurl) {
		this.appurl = appurl;
	}

	public String getCtnt() {
		return ctnt;
	}

	public void setCtnt(String ctnt) {
		this.ctnt = ctnt;
	}
	
}
