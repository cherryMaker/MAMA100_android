/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mama100.android.member.outwardWeibo;

import android.text.TextUtils;

import com.mama100.android.member.outwardWeibo.AsyncSocialRunner.XResponseListener;

/**
 * @version 1.0 
 * @author ecoo
 */
public class XWeibo {
    public static String SERVER = "https://api.weibo.com/2/";
    public static String URL_OAUTH_TOKEN = "http://api.t.sina.com.cn/oauth/request_token";
    public static String URL_AUTHORIZE = "http://api.t.sina.com.cn/oauth/authorize";
    public static String URL_ACCESS_TOKEN = "http://api.t.sina.com.cn/oauth/access_token";
    public static String URL_AUTHENTICATION = "http://api.t.sina.com.cn/oauth/authenticate";

    public static String URL_OAUTH2_ACCESS_TOKEN = "https://api.weibo.com/oauth2/access_token";

    //我的微博  手机版页面    "url+/uid"
    public static String URL_VIEW_M_USER_WEIBO = "http://m.weibo.cn/u";
	//获取新浪微博用户信息的接口
	public static final String URL_USER_INF="https://api.weibo.com/2/users/show.json";
	//oauth2授权接口
    public static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";
    //关注一个用户接口
    public static String URL_FIREND_CREATE = "https://api.weibo.com/2/friendships/create.json";

    //分享接口
    public static String URL_POST_STATUS = "https://upload.api.weibo.com/2/statuses/upload.json";
    
    
    private static String APP_KEY = "";
    private static String APP_SECRET = "";

    private static XWeibo mWeiboInstance = null;
    private String mAccessToken = null;

    public static final String TOKEN = "access_token";
    public static final String EXPIRES = "expires_in";
    public static final String UID = "uid";
    public static final String SCREEN_NAME = "screen_name";
    public static final String AVATAR_URL = "avatar_url";
    public static final String DEFAULT_REDIRECT_URI = "wbconnect://success";// 暂不支持
    public static final String DEFAULT_CANCEL_URI = "wbconnect://cancel";// 暂不支持

    private String mRedirectUrl;

    private XWeibo() {
        mRedirectUrl = DEFAULT_REDIRECT_URI;
    }

    public synchronized static XWeibo getInstance() {
        if (mWeiboInstance == null) {
            mWeiboInstance = new XWeibo();
        }
        return mWeiboInstance;
    }

    // 设置accessToken
    public void setAccessToken(String token) {
        mAccessToken = token;
    }

    public String getAccessToken() {
        return this.mAccessToken;
    }

    public void setupConsumerConfig(String consumer_key, String consumer_secret) {
        XWeibo.APP_KEY = consumer_key;
        XWeibo.APP_SECRET = consumer_secret;
    }

    public static String getAppKey() {
        return XWeibo.APP_KEY;
    }

    public static String getAppSecret() {
        return XWeibo.APP_SECRET;
    }

    public static String getSERVER() {
        return SERVER;
    }

    public static void setSERVER(String sERVER) {
        SERVER = sERVER;
    }

    public String getRedirectUrl() {
        return mRedirectUrl;
    }

    public void setRedirectUrl(String mRedirectUrl) {
        this.mRedirectUrl = mRedirectUrl;
    }

    public boolean isSessionValid() {
            return (!TextUtils.isEmpty(mAccessToken));
    }
    
    
//    //网页方式授权方法
//    private void authorMethod2(Activity curActivity,Intent ){
//    	
//		String url=XWeibo.URL_OAUTH2_ACCESS_AUTHORIZE + "?display=mobile&response_type=token&client_id=" + 
//				CONSUMER_KEY +"&redirect_uri="+URL_ACTIVITY_CALLBACK;
//		Intent intent=new Intent(SinaWeiBoActivity.this, WebViewActivity.class)
//		.putExtra(WebViewActivity.KEY_URL, url);
//		curActivity.startActivityForResult(intent, REQ_AUTHOR);
//    }
    
    
    /**
     * 发布有图片微博
     * @param content  微博文字
     * @param pic file 微博图片,File或Bitmap对象,无则传null
     * @param listener 分享结果监听器
     * @throws XWeiboException
     */
    public void share2weibo(String content, Object pic,final XResponseListener listener){
    	try {
			checkAccessToken();
		} catch (XWeiboException e) {
			listener.onError(e);
			return;
		}
    	share2weibo(pic, content, "", "",listener);
    }
    
    /**
     * 发布有图片微博
     * @param file 微博图片,File或Bitmap对象,无则传null
     * @param status  微博文字
     * @param lon 经度
     * @param lat 纬度
     * @param listener 分享结果监听器
     * @throws XWeiboException
     */
    private void share2weibo(Object file, String status, String lon,
            String lat,final XResponseListener listener) 
            		{
        SocialParameters bundle = new SocialParameters();
        bundle.add("access_token", mAccessToken);
        bundle.add("status", status);
        if (!TextUtils.isEmpty(lon)) {
            bundle.add("lon", lon);
        }
        if (!TextUtils.isEmpty(lat)) {
            bundle.add("lat", lat);
        }
        StringBuffer url=new StringBuffer(URL_POST_STATUS);
        final AsyncSocialRunner weiboRunner = new AsyncSocialRunner();
        weiboRunner.postXStatus(url, bundle, file, listener);
    }
    
    public void getXUserInf(final String uid,
			final XResponseListener listener){
    	try {
			checkAccessToken();
		} catch (XWeiboException e) {
			listener.onError(e);
			return;
		}
        final AsyncSocialRunner weiboRunner = new AsyncSocialRunner();
        weiboRunner.getXUserInf(uid, mAccessToken, listener);
    }
    
    /**
     * 关注一个用户
     * @param uidBeFollow 需要关注用户的uid，如妈妈100的UID:1965962870
     * @param listener
     */
    public void followAUser(final String uidBeFollow,
			final XResponseListener listener){
    	try {
			checkAccessToken();
		} catch (XWeiboException e) {
			listener.onError(e);
			return;
		}
        SocialParameters bundle = new SocialParameters();
        bundle.add("access_token", mAccessToken);
        bundle.add("uid", uidBeFollow);
        
        StringBuffer  sbu=new StringBuffer(XWeibo.URL_FIREND_CREATE);
        final AsyncSocialRunner weiboRunner = new AsyncSocialRunner();
        weiboRunner.xFollow(sbu, bundle, listener);
    }
    
    
    /**
     * token invalid
     * @throws XWeiboException 
     */
    private void checkAccessToken() throws XWeiboException{
    	if(mAccessToken==null||mAccessToken.equals("")){
    		throw new XWeiboException("token参数为空",40042);
    	}

    }
    
}
