package com.mama100.android.member.bean;


public class MobResponseCode {
	
	public static final String SUCCESS = "100";
	
	public static final String LOGIN_FALURE = "5101";
	
	// 手机用户不存圄1�7
	public static final String NON_EXIST = "5001";
	
	// 数据校验失败
	public static final String VALIDATE_ERROR = "5101";

	public static final String SYSTEM_EXCEPTION = "5901";

	public static final String SOCKET_TIMEOUT = "5903";

	public static final String CONNECT_TIMEOUT = "5904";
	
	// 客户端密钥不正确
	public static final String CLIENT_DES_KEY_ERROR = "5014";

	public static final String SERVER_404 = "5905";
	

	// 手机用户不存圄1�7
	public static final String CLENT_NON_EXIST = "227";
	
	
	// 开机检查软件版本却没有发现新的版本
	public static final String NEW_VER_NOT_FOUND = "6102";
	
	//response 为 null
	
	public static final String NULL_RESPONSE = "10000";
	//response.getCode 为 null
	public static final String NULL_CODE = "10001";
	//本地 tgt 为 “”
	public static final String NULL_TGT = "10002";
	
	// TGT无效，验证失败
	public static final String TGT_INVALID = "SC802";
}
