package com.mama100.android.member.outwardWeibo;

import com.mama100.android.member.outwardWeibo.AsyncSocialRunner.QQResponseListener;

/**
 * 与新浪微博不同的是，QQ调用用户接口时，除了附上acess_token，还需要“openid”
 * @author eco
 *
 */
public class QQPlatform {
	
    public static final String TOKEN = "access_token";
    public static final String EXPIRES = "expires_in";
    public static final String OPEN_ID = "open_id";
    public static final String NICKNAME = "nickname";

	
	//授权接口，可获得access_token
    public static final String URL_OAUTH2_ACCESS_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";
	//获得openid
    public static final String URL_OAUTH2_GET_OPENID = "https://graph.qq.com/oauth2.0/me";
    
    /**
     * 获得用户信息
     */
    public static final String URL_GET_USER_INF = "https://graph.qq.com/user/get_simple_userinfo";   
    
    private static final String APP_ID = "100307774";
    private static String APP_KEY = "";
    private String mAccessToken="";
    private String mExpiresIn="";
	private static QQPlatform mInstance;
    
    public synchronized static QQPlatform getInstance() {
		if (mInstance == null) {
            mInstance = new QQPlatform();
        }
        return mInstance;
    }

	public String getAccessToken() {
		return mAccessToken;
	}

	public  void setAccessToken(String accessToken) {
		this.mAccessToken = accessToken;
	}
    
	
    public void setConsumerConfig(String consumer_key) {
        QQPlatform.APP_KEY=consumer_key;
    }

	public static String getAppKey() {
		return APP_KEY;
	}
    
	public static String getAppId() {
		return APP_ID;
	}

	public String getExpiresIn() {
		return mExpiresIn;
	}

	public void setExpiresIn(String mExpiresIn) {
		this.mExpiresIn = mExpiresIn;
	}
	
    /**
     * token invalid
     * @throws XWeiboException 
     */
    private void checkAccessToken() throws QQPlatformException{
    	if(mAccessToken==null){
    		throw new QQPlatformException("token参数为空",100007);
    	}

    }
    
	
    /**
     * 获取一个腾讯用户的open id
     * @param 
     * @param listener
     */
    public void getOpenId(final QQResponseListener listener){
    	try {
			checkAccessToken();
		} catch (QQPlatformException e) {
			listener.onError(e);
			return;
		}
        SocialParameters bundle = new SocialParameters();
        bundle.add("access_token",mAccessToken);
        
        StringBuffer  sbu=new StringBuffer(QQPlatform.URL_OAUTH2_GET_OPENID);
        final AsyncSocialRunner runner = new AsyncSocialRunner();
        runner.qqGetOpenId(sbu, bundle, listener);
    }
    
    /**
     * 获取一个QQ空间用户的信息
     * @param uid
     * @param listener
     */
    public void getQzoneUserInf(final String openid,
			final QQResponseListener listener){
    	try {
			checkAccessToken();
		} catch (QQPlatformException e) {
			listener.onError(e);
			return;
		}
        final AsyncSocialRunner runner = new AsyncSocialRunner();
        runner.qqGetUserInf(openid, mAccessToken, listener);
    }
    
	
}
