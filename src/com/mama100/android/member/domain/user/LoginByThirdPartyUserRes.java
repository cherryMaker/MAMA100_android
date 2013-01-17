package com.mama100.android.member.domain.user;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * 
 * @author deric
 * 2012-9-20
 */
public class LoginByThirdPartyUserRes extends BaseRes {
	
	/**
	 * 是否首次登录
	 * 0=否, 1=是
	 */
	@Expose
	private Integer isFirstLogin; 

	/**
	 * 妈妈100用户ID
	 */
	@Expose
	private String mid;
	
	/**
	 * 用户名
	 */
	@Expose
	private String username;
	
	/**
	 * 用户密码
	 */
	@Expose
	private String pwd;
	
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(".............get Login Res for Third Party Info START....... \n");
		sb.append("isFirstLogin - " + ""+((isFirstLogin==0)?false:true) + "\n");
		sb.append("mid - " + mid + "\n");
		sb.append("username - " + username + "\n");
		sb.append("pwd - " + pwd + "\n");
		sb.append("\n");
		sb.append(".............get Login Res for Third Party Info END....... \n");
		return sb.toString();
	}
	

	
	public boolean getIsFirstLogin() {
		return (isFirstLogin==0)?false:true;
	}

	public void setIsFirstLogin(Integer isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	
}
