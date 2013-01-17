package com.mama100.android.member.bean.thirdparty;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

import com.mama100.android.member.bean.ThirdPartyUser;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.outwardWeibo.QQGetOpenIdRes;
import com.mama100.android.member.outwardWeibo.QQPlatform;
import com.mama100.android.member.outwardWeibo.QQUserRes;
import com.mama100.android.member.util.LogUtils;



/**
 * @author edwar
 * @version 创建时间：2012-09-26 17:55
 * QQ聊天器帐号登录Bean
 */
public class QQLoginBean extends CommonBean {
	
////constant

	public static final String URL_ACTIVITY_CALLBACK = "http://www.mama100.com"; //网页授权后，返回的URL，此处自定义为letsgoweibo://AuthBackActivity.com
	
	public static final int REQ_AUTHOR=20; //请求授权，调用WebViewActivity的请求码
	private static final String CONFIG_FILENAME="qqPlatformConfig";
	
	
	/******************************************************************
	 * QQ专用字段 --START
	 ******************************************************************/
	private String figureurl;//figureurl: 大小为30×30像素的头像URL
	
	private String figureurl_1; //figureurl_1: 大小为50×50像素的头像URL
	
	private String figureurl_2; //figureurl_2: 大小为100×100像素的头像URL
	
	private String gender;//gender: 性别。如果获取不到则默认返回“男”
	
	private String vip;//vip: 标识用户是否为黄钻用户（0：不是；1：是）
	
	private String level; //level: 黄钻等级（如果是黄钻用户才返回此参数）
	/******************************************************************
	 * QQ专用字段 --END
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
	
	public QQLoginBean(){
		super();
		
		secretKey = BasicApplication.getInstance().getQq_key();
		// QQ登录操作之前，必须初始化
		QQPlatform.getInstance().setConsumerConfig(secretKey);
		
	}

	//从本地拿数据
	public void getValuesFromSharePreference(){
		ThirdPartyUser item = BasicApplication.getInstance().getQqItems();
		if(item!=null){
		accessToken = item.getAccessToken();
		expiresIn = item.getTokenExpireDate();
		uid = item.getUid();
		QQPlatform.getInstance().setAccessToken(accessToken);
		QQPlatform.getInstance().setExpiresIn(expiresIn);
		}
		
//		if (shareprefencefile != null) {
//			accessToken=shareprefencefile.getString(QQPlatform.TOKEN, "");
//			expiresIn=shareprefencefile.getString(QQPlatform.EXPIRES, "");
//			uid=shareprefencefile.getString(QQPlatform.OPEN_ID, ""); //相当于QQ的openId
//			nickname=shareprefencefile.getString(QQPlatform.NICKNAME, "");
//		}
		
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
		QQPlatform.getInstance().setAccessToken(accessToken);
		QQPlatform.getInstance().setExpiresIn(expiresIn);
		
//		final Editor edit=shareprefencefile.edit();
//		edit.putString(QQPlatform.TOKEN, accessToken);
//		edit.putString(QQPlatform.EXPIRES, expiresIn);
//		edit.commit();
		
		ThirdPartyUser item = BasicApplication.getInstance().getQqItems();
		item.setAccessToken(accessToken);
		item.setTokenExpireDate(expiresIn);
		BasicApplication.getInstance().setQqItems(item);
	}
	


	public static String authorUrl(){
		String url=QQPlatform.URL_OAUTH2_ACCESS_AUTHORIZE + "?response_type=token&display=mobile&scope=get_user_info,get_simple_userinfo"+"&client_id="+ 
				QQPlatform.getAppId()+"&redirect_uri="+URL_ACTIVITY_CALLBACK;
		return url;
	}

	
	
	/**
	 * 获取第三方用户的 具体用户信息
	 * @param xRes  获取第三方信息获得的响应
	 */
	public void setValuesIntoSharedPreference(QQUserRes qRes) {
		nickname=qRes.getNickname();
//		figureurl = qRes.getFigureurl(); //获取用户的头像路径
//		figureurl = qRes.getFigureurl_1(); //获取用户的头像路径
		figureurl = qRes.getFigureurl_2(); //获取用户的头像路径
		LogUtils.loge(TAG, "nickname from QQ - " + nickname + ",\n"+  "avatar_url from QQ - " + figureurl);
//		BasicApplication.getInstance().setNickname(nickname);
		BasicApplication.getInstance().setWeiboNickname(nickname);
		BasicApplication.getInstance().setWeiboAvatarUrl(figureurl);
//		final Editor edit=shareprefencefile.edit();
//		edit.putString(QQPlatform.NICKNAME, nickname);
//		edit.putString(XWeibo.AVATAR_URL, figureurl);
//		edit.commit();
	}
	
	
	/**
	 * 获取授权成功后，第三方用户的 Uid
	 * @param xRes  获取第三方信息获得的响应
	 */
	public void setValuesIntoSharedPreference(QQGetOpenIdRes response) {
		uid=((QQGetOpenIdRes)response).getOpenid();  //QQ的uid用 openId代替
//		final Editor edit=shareprefencefile.edit();
//		edit.putString(QQPlatform.OPEN_ID, uid);
//		edit.commit();
		
		
		ThirdPartyUser item = BasicApplication.getInstance().getQqItems();
		item.setUid(uid);
		BasicApplication.getInstance().setQqItems(item);
		
		// added by edwar 2012-10-25, 解决同一个uid，仅第一次才进入个人信息界面
		List<String> uidList = BasicApplication.getInstance().getQQUidList();
		if (uidList == null) {
			uidList = new ArrayList<String>();
		}
		if (uidList.isEmpty()) {
			uidList.add(uid);
			BasicApplication.getInstance().setNewQQUid(true);
		} else {
			if (!uidList.contains(uid)) {
				uidList.add(uid);
				BasicApplication.getInstance().setNewQQUid(true);
			}else{
				BasicApplication.getInstance().setNewQQUid(false);
			}
		}
		BasicApplication.getInstance().setQQUidList(uidList);//更新列表
		//added end 2012-10-25
		
		
		LogUtils.loge(TAG, " - QQLoginBean uid=  "+uid);
	}

	

	/***********************************************************************
	 * 基本的getter 和 setter 方法
	 **********************************************************************/
	
	public String getFigureurl() {
		return figureurl;
	}

	public void setFigureurl(String figureurl) {
		this.figureurl = figureurl;
	}

	public String getFigureurl_1() {
		return figureurl_1;
	}

	public void setFigureurl_1(String figureurl_1) {
		this.figureurl_1 = figureurl_1;
	}

	public String getFigureurl_2() {
		return figureurl_2;
	}

	public void setFigureurl_2(String figureurl_2) {
		this.figureurl_2 = figureurl_2;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	
	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("==== QQ登录用户信息 START ===" + "\n");
    	sb.append("accessToken = "+ accessToken+ "\n");
    	sb.append("expiredTime = "+ expiresIn+ "\n");
    	sb.append("userId = "+ uid+ "\n");
    	sb.append("nickname = "+ nickname+ "\n");
    	sb.append("figureurl = "+ figureurl+ "\n");
    	sb.append("figureurl_1 = "+ figureurl_1+ "\n");
    	sb.append("figureurl_2 = "+ figureurl_2+ "\n");
    	sb.append("gender = "+ gender+ "\n");
    	sb.append("vip = "+ vip+ "\n");
    	sb.append("level = "+ level+ "\n");
    	sb.append("==== QQ登录用户信息 END ==="+ "\n");
    	return sb.toString();
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
