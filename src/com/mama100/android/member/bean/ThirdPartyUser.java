package com.mama100.android.member.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;


public class ThirdPartyUser implements Serializable  {

	public static final String type_sina = "sina";
	public static final String type_qqweibo = "qqweibo";
	public static final String type_qq = "qq";

	// 状态,绑定成功
	public static final int status_already_asso = 1;
	// 状态,取消绑定
	public static final int status_cancel_asso = 2;

	/**
	 * 数据库帐号
	 */
	@Expose
	private String id;

	/**
	 * 妈网bbsmembers id
	 * 
	 */
	@Expose
	private String mama100Id;

	/**
	 * 类型，qq还是sina
	 */
	@Expose
	private String userType;

	/**
	 * 第三方帐号用户id
	 */
	@Expose
	private String uid;

	@Expose
	private String accessToken;

	@Expose
	private String tokenExpireDate;

	/***
	 * 绑定状态
	 */
	@Expose
	private String useStatus;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMama100Id() {
		return mama100Id;
	}

	public void setMama100Id(String mama100Id) {
		this.mama100Id = mama100Id;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenExpireDate() {
		return tokenExpireDate;
	}

	public void setTokenExpireDate(String tokenExpireDate) {
		this.tokenExpireDate = tokenExpireDate;
	}
	
	

	
	public String getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}

	public String  toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Weibo Item --Start" + "\n");
		sb.append("id - " + id + "\n");
		sb.append("mama100Id - " + mama100Id + "\n");
		sb.append("accessToken - " + accessToken + "\n");
		sb.append("tokenExpireDate - " + tokenExpireDate + "\n");
		sb.append("userYype - " + userType + "\n");
		sb.append("usestatus - " + useStatus + "\n");
		sb.append("Weibo Item --End" + "\n");
		return sb.toString();
		
		
	}

	

}
