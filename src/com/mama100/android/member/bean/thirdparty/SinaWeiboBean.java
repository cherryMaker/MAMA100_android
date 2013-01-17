package com.mama100.android.member.bean.thirdparty;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences.Editor;
import android.net.Uri;

import com.mama100.android.member.bean.ThirdPartyUser;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.outwardWeibo.XAccessToken;
import com.mama100.android.member.outwardWeibo.XWeibo;
import com.mama100.android.member.outwardWeibo.XWeiboUserRes;
import com.mama100.android.member.util.LogUtils;

public class SinaWeiboBean extends CommonBean {

	// //constant
	public static final String URL_ACTIVITY_CALLBACK = "letsgoweibo://AuthBackActivity.com"; // 网页授权后，返回的URL，此处自定义为letsgoweibo://AuthBackActivity.com
	public static final String XWEIBO_KEY = "553250813"; // 宝宝通移动应用标记
	public static final String XWEIBO_UID = "1965962870"; // 妈妈100微博UID

	public static final int REQ_AUTHOR = 10; // 请求授权，调用WebViewActivity的请求码
	public static final String CONFIG_FILENAME = "xweiboConfig";

	/******************************************************************
	 * sina微博专用字段 --START
	 ******************************************************************/

	/**
	 * 用户性别
	 */
	private String gender;

	/**
	 * 用户头像地址
	 */
	private String profile_image_url;

	/**
	 * 用户所在地
	 */
	private String location;
	/**
	 * 用户描述
	 */
	private String description;

	// 第三方的key
	private String secretKey;

	/******************************************************************
	 * sina微博专用字段 -- END
	 ******************************************************************/

	/****************************************************************
	 * Start 通用字段
	 ****************************************************************/
	// //field
	protected static String accessToken; // 授权密钥
	protected static String expiresIn; // 有效时间，单位秒
	protected String uid; // 授权的用户UID
	protected String nickname; // 用户昵称
	/****************** 关注妈妈100网 用到的变量 ******************************/
	protected static boolean isFollowMama100; // 是否关注妈妈100官方微博

	private String TAG = this.getClass().getSimpleName();

	public SinaWeiboBean() {
		super(CONFIG_FILENAME);
		secretKey = BasicApplication.getInstance().getSina_key();
		// 新浪微博操作之前，必须初始化
		XWeibo.getInstance().setupConsumerConfig(SinaWeiboBean.XWEIBO_KEY,
				secretKey);

	}

	/********************************************************************
	 * 本地 SharedPreferences文件 涉及到的 操作 方法 -- START
	 ********************************************************************/

	// 从本地拿数据
	public void getValuesFromSharePreference() {
		ThirdPartyUser item = BasicApplication.getInstance()
				.getSinaWeiboItems();
		if (item != null) {
			accessToken = item.getAccessToken();
			expiresIn = item.getTokenExpireDate();
			uid = item.getUid();
			XWeibo.getInstance().setAccessToken(accessToken);
			
		}

		if (shareprefencefile != null) {

			// 改变，不从本地获取，而从服务器获取
			// accessToken = shareprefencefile.getString(XWeibo.TOKEN, "");
			// expiresIn = shareprefencefile.getString(XWeibo.EXPIRES, "");
			// uid = shareprefencefile.getString(XWeibo.UID, "");
			// nickname = shareprefencefile.getString(XWeibo.SCREEN_NAME, "");

			isFollowMama100 = shareprefencefile.getBoolean(XWEIBO_IS_FOLLOW_US,
					false);
		}
	}

	/**
	 * 获取授权成功后的基本授权信息
	 * 
	 * @param newS
	 *            第三方授权成功后返回的字符串
	 */
	public void setValuesIntoSharedPreference(String newS) {
		Uri newUri = Uri.parse(newS);

		accessToken = newUri.getQueryParameter("access_token");
		expiresIn = newUri.getQueryParameter("expires_in");
		uid = newUri.getQueryParameter("uid");

		// set token

		XWeibo.getInstance().setAccessToken(accessToken);

		// 改变为不保存本地了，
		// final Editor edit = shareprefencefile.edit();
		// edit.putString(XWeibo.TOKEN, accessToken);
		// edit.putString(XWeibo.EXPIRES, expiresIn);
		// edit.putString(XWeibo.UID, uid);
		// edit.commit();

		ThirdPartyUser item = BasicApplication.getInstance()
				.getSinaWeiboItems();
		item.setAccessToken(accessToken);
		item.setTokenExpireDate(expiresIn);
		item.setUid(uid);
		BasicApplication.getInstance().setSinaWeiboItems(item);
		

		// added by edwar 2012-10-25, 解决同一个uid，仅第一次才进入个人信息界面
		List<String> uidList = BasicApplication.getInstance().getSinaUidList();
		if (uidList == null) {
			uidList = new ArrayList<String>();
		}
		if (uidList.isEmpty()) {
			uidList.add(uid);
			BasicApplication.getInstance().setNewSinaUid(true);
		} else {
			if (!uidList.contains(uid)) {
				uidList.add(uid);
				BasicApplication.getInstance().setNewSinaUid(true);
			}else{
				BasicApplication.getInstance().setNewSinaUid(false);
			}
		}
		BasicApplication.getInstance().setSinaUidList(uidList);//更新列表
		//added end 2012-10-25

	}

	/**
	 * 获取第三方用户的 具体用户信息
	 * 
	 * @param xRes
	 *            获取第三方信息获得的响应
	 */
	public void setValuesIntoSharedPreference(XWeiboUserRes xRes) {
		nickname = xRes.getScreen_name();
		profile_image_url = xRes.getProfile_image_url();
		LogUtils.loge(TAG, "nickname from SinaWeibo - " + nickname + ", \n"
				+ " avatar_url from SinaWeibo - " + profile_image_url);
		// BasicApplication.getInstance().setNickname(nickname);
		BasicApplication.getInstance().setWeiboNickname(nickname);
		BasicApplication.getInstance().setWeiboAvatarUrl(profile_image_url);

		// final Editor edit = shareprefencefile.edit();
		// edit.putString(XWeibo.SCREEN_NAME, nickname);
		// edit.putString(XWeibo.AVATAR_URL, profile_image_url);
		// edit.commit();
	}

	public void updateIsFollowUsValueInSharedPreference(boolean flag) {
		final Editor edit = shareprefencefile.edit();
		edit.putBoolean(XWEIBO_IS_FOLLOW_US, flag);
		edit.commit();
	}

	/**
	 * 解绑定的情况下，删除本地保存的信息。 删除新浪用户授权数据,没有删除uid、screen_name、has_follow_mama100数据，
	 * 因为没啥影响
	 */
	public void unbindUserToDeleteValueInSharedPreference() {
		// final Editor edit=shareprefencefile.edit();
		// edit.remove(XWeibo.TOKEN);
		// edit.remove(XWeibo.EXPIRES);
		// edit.commit();

		ThirdPartyUser item = BasicApplication.getInstance()
				.getSinaWeiboItems();
		item.setAccessToken(null);
		item.setTokenExpireDate(null);
		item.setUid(null);
		BasicApplication.getInstance().setSinaWeiboItems(item);

		accessToken = null;
		expiresIn = null;
		XWeibo.getInstance().setAccessToken(null);

	}

	/********************************************************************
	 * 本地 SharedPreferences文件 涉及到的 操作 方法 -- END
	 ********************************************************************/

	public void setValues() {
		if (isAccessTokenValid()) {
			LogUtils.loge("TAG", ">Access_token : " + accessToken
					+ " \n\n >Expires_in: " + expiresIn);
			// set token

			XWeibo.getInstance().setAccessToken(accessToken);

		}

		if (isNicknameValid()) {
			LogUtils.loge("TAG", "\n\n>你好，" + nickname);
		}

	}

	public static String authorUrl() {

		// &with_offical_account=1 ,用于在授权页面，多显示一个关注mama100官方微博的check栏
		//display=mobile 的页面会让程序异常
		String url = XWeibo.URL_OAUTH2_ACCESS_AUTHORIZE
				+ "?display=mobile&response_type=token&with_offical_account=1&client_id="
				+ XWEIBO_KEY + "&redirect_uri=" + URL_ACTIVITY_CALLBACK;
		return url;
	}

	/***********************************************************************
	 * 基本的getter 和 setter 方法
	 **********************************************************************/

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("==== 新浪微博用户信息 START ===" + "\n");
		sb.append("accessToken = " + accessToken + "\n");
		sb.append("expiredTime = " + expiresIn + "\n");
		sb.append("userId = " + uid + "\n");
		sb.append("nickname = " + nickname + "\n");

		sb.append("gender = " + gender + "\n");
		sb.append("profile_image_url = " + profile_image_url + "\n");
		sb.append("location = " + location + "\n");
		sb.append("description = " + description + "\n");
		sb.append("==== 新浪微博用户信息 END ===" + "\n");
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

	public static void setFollowMama100(boolean isfollowMama100) {
		isFollowMama100 = isfollowMama100;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public boolean isAccessTokenValid() {
		return accessToken != null && !accessToken.equals("");
	}

	public boolean isUidValid() {
		return uid != null && !uid.equals("");
	}

	public boolean isNicknameValid() {
		return nickname != null && !nickname.equals("");
	}

}
