package com.mama100.android.member.bean.thirdparty;

import android.content.SharedPreferences.Editor;
import android.net.Uri;

import com.mama100.android.member.bean.ThirdPartyUser;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.outwardWeibo.QQGetOpenIdRes;
import com.mama100.android.member.outwardWeibo.QQPlatform;
import com.mama100.android.member.outwardWeibo.QQUserRes;
import com.mama100.android.member.outwardWeibo.TCWeibo;
import com.mama100.android.member.util.LogUtils;



/**
 * @author edwar
 * @version 创建时间：2012-09-27 14:32
 * 腾讯微博的bean
 */
public class TencentWeiboBean extends CommonBean{
	////constant
	public static final String APP_KEY = "801244182"; 

	public static final String URL_ACTIVITY_CALLBACK = "http://www.mama100.com"; //网页授权后，返回的URL，此处自定义为letsgoweibo://AuthBackActivity.com
	public static final int REQ_AUTHOR=30; //请求授权，调用WebViewActivity的请求码
	private static final String CONFIG_FILENAME="tcWeiboConfig";
	
	/******************************************************************
	 * 腾讯微博专用字段 --START
	 ******************************************************************/
	
	//TODO  以后添加
	
	
	/******************************************************************
	 * 腾讯微博专用字段 --END
	 ******************************************************************/
	
	private String TAG = this.getClass().getSimpleName();
	
	//第三方的key
	private String secretKey;
	
	
	
	
	
	
	
	
	/****************************************************************
	 * Start  通用字段
	 ****************************************************************/
	////field
	protected static String accessToken; //授权密钥
	protected static String expiresIn;   //有效时间，单位秒
	protected String uid; //授权的用户UID
	protected  String nickname; //用户昵称
	/******************  关注妈妈100网 用到的变量******************************/
	protected static boolean isFollowMama100; //是否关注妈妈100官方微博


	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("==== 腾讯微博用户信息 START ===" + "\n");
    	sb.append("accessToken = "+ accessToken+ "\n");
    	sb.append("expiredTime = "+ expiresIn+ "\n");
    	sb.append("userId = "+ uid+ "\n");
    	sb.append("nickname = "+ nickname+ "\n");
    	sb.append("==== 腾讯微博用户信息 END ==="+ "\n");
    	return sb.toString();
    }
	
	
	
	
	public TencentWeiboBean() {
		super();
		secretKey = BasicApplication.getInstance().getQqweibo_key();
		 //腾讯微博登录操作之前，必须初始化
		TCWeibo.getInstance().setConsumerConfig(secretKey);
	}
	
	


	//从本地拿数据
	public void getValuesFromSharePreference(){
		
		ThirdPartyUser item = BasicApplication.getInstance().getQqWeiboItems();
		if(item!=null){
		accessToken = item.getAccessToken();
		expiresIn = item.getTokenExpireDate();
		uid = item.getUid();
		}
		
		if(shareprefencefile !=null){
//			accessToken=shareprefencefile.getString(QQPlatform.TOKEN, "");
//			expiresIn=shareprefencefile.getString(QQPlatform.EXPIRES, "");
//			uid=shareprefencefile.getString(QQPlatform.OPEN_ID, ""); //相当于tencentWeibo的openId
//			nickname=shareprefencefile.getString(QQPlatform.NICKNAME, "");
			
			isFollowMama100 = shareprefencefile.getBoolean(XWEIBO_IS_FOLLOW_US,
					false);
		}
	}
	
	
	public void setValues(){
		if (isAccessTokenValid()) {
			LogUtils.loge("TAG", ">Access_token : " + accessToken
					+ " \n\n >Expires_in: " + expiresIn);
			// set token
			QQPlatform.getInstance().setAccessToken(accessToken);
			QQPlatform.getInstance().setExpiresIn(expiresIn);

		}
		if(isNicknameValid()){	
			LogUtils.loge("TAG","\n\n>你好，"+nickname);
    	}
	}
	
	
	/**
	 * 获取授权成功后的基本授权信息
	 * @param newS  第三方授权成功后返回的字符串
	 */
	public void setValuesIntoSharedPreference(String newS){
		Uri newUri = Uri.parse(newS);
		
		accessToken = newUri.getQueryParameter("access_token");
		expiresIn = newUri.getQueryParameter("expires_in");


		//set token
		TCWeibo.getInstance().setAccessToken(accessToken);
		TCWeibo.getInstance().setExpiresIn(expiresIn);

		
		ThirdPartyUser item = BasicApplication.getInstance().getQqWeiboItems();
		item.setAccessToken(accessToken);
		item.setTokenExpireDate(expiresIn);
		
		
//		final Editor edit=shareprefencefile.edit();
//		edit.putString(TCWeibo.TOKEN, accessToken);
//		edit.putString(TCWeibo.EXPIRES, expiresIn);
//		edit.putString(TCWeibo.OPEN_ID, uid);
//		edit.putString(TCWeibo.NICKNAME, nickname);
//		edit.commit();
		
	}
	


	public static String authorUrl(){
		String url=TCWeibo.URL_OAUTH2_ACCESS_AUTHORIZE + "?response_type=token&wap=2"+"&client_id="+ 
				APP_KEY+"&redirect_uri="+URL_ACTIVITY_CALLBACK;
		return url;
	}


	
	
	/**
	 * 获取第三方用户的 具体用户信息
	 * @param xRes  获取第三方信息获得的响应
	 */
	public void setValuesIntoSharedPreference(QQUserRes qRes) {
		nickname=qRes.getNickname();
//		final Editor edit=shareprefencefile.edit();
//		edit.putString(QQPlatform.NICKNAME, nickname);
//		edit.commit();
		
//		BasicApplication.getInstance().setNickname(nickname);
		BasicApplication.getInstance().setWeiboNickname(nickname);
	}
	
	
	/**
	 * 获取授权成功后，第三方用户的 Uid
	 * @param xRes  获取第三方信息获得的响应
	 */
	public void setValuesIntoSharedPreference(QQGetOpenIdRes response) {
		uid=((QQGetOpenIdRes)response).getOpenid();
//		final Editor edit=shareprefencefile.edit();
//		edit.putString(QQPlatform.OPEN_ID, uid);
//		edit.commit();
		
		
		ThirdPartyUser item = BasicApplication.getInstance().getQqWeiboItems();
		item.setUid(uid);
		
	}
	
	
	/***********************************************************************
	 * 基本的getter 和 setter 方法
	 **********************************************************************/
	
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	
	
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}


	public static boolean isFollowMama100() {
		return isFollowMama100;
	}

	public static void setFollowMama100(boolean isFollowMama100) {
		isFollowMama100 = isFollowMama100;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public boolean isAccessTokenValid() {
		return accessToken!=null&&!accessToken.equals("");
	}
	
	public boolean isUidValid() {
		return uid!=null&&!uid.equals("");
	}
	
	public boolean isNicknameValid() {
		return nickname!=null&&!nickname.equals("");
	}
	
}
