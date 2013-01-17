package com.mama100.android.member.outwardWeibo;

import com.mama100.android.member.outwardWeibo.AsyncSocialRunner.QQResponseListener;

/**
 * 与新浪微博不同的是，QQ调用用户接口时，除了附上acess_token，还需要“openid”
 * @author eco
 *
 */
public class TCWeibo {
	
    public static final String TOKEN = "access_token";
    public static final String EXPIRES = "expires_in";
    public static final String OPEN_ID = "open_id";
    public static final String OPEN_KEY = "open_key";
    public static final String NICKNAME = "nickname";

	
	//授权接口，可获得access_token
    public static final String URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.t.qq.com/cgi-bin/oauth2/authorize";
    
    /**
     * 获得用户信息
     */
    public static final String URL_GET_USER_INF = "https://open.t.qq.com/api/user/info";   
    
    private static final String APP_KEY = "801244182";
    private static String APP_SECRET = "";
    private String mAccessToken="";
    private String mExpiresIn="";
	private static TCWeibo mInstance;
    
    public synchronized static TCWeibo getInstance() {
		if (mInstance == null) {
            mInstance = new TCWeibo();
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
        TCWeibo.APP_SECRET=consumer_key;
    }

	public static String getAppKey() {
		return APP_SECRET;
	}
    
	public static String getAppId() {
		return APP_KEY;
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
     * 获取一个QQ空间用户的信息
     * @param uid
     * @param listener
     */
    public void getUserInf(final String openid,
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
