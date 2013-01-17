package com.mama100.android.member.domain.user;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

//注册响应
public class RegisterRes extends BaseRes {
	
	
	/**************** 新增 *****************/
	
	/**
	 *用户名
	 */
	@Expose
	private String username;
	
	
	/**
	 *用户ID
	 */
	@Expose
	private String mid;
	
	
	
	
	
	

	public String getUsername() {
		return username;
	}







	public void setUsername(String username) {
		this.username = username;
	}







	public String getMid() {
		return mid;
	}







	public void setMid(String mid) {
		this.mid = mid;
	}







	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(".............get RegisterRes Info START....... \n");
		sb.append("username - " + username + "\n");
		sb.append("mid - " + mid + "\n");
		sb.append("\n");
		sb.append(".............get RegisterRes Info END....... \n");
		return sb.toString();
	}
	
	

}
