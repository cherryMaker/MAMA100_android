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

import com.mama100.android.member.outwardHttp.OutwardBaseRes;
import com.mama100.android.member.outwardHttp.OutwardHttpClientHelper;


/**
 * 
 * Implements a weibo api as a asynchronized way. Every object used this runner should implement interface RequestListener.
 * @author ecoo
 * 
 * 
 * modified by edwar 2012-09-27
 * 公共接口类，包含 新浪微博，QQ聊天器，QQ微博 三种
 */
public class AsyncSocialRunner {
	
	
	public AsyncSocialRunner(){

	}
	
	/*************************************************************************
	 * 新浪微博 的 方法  - START  added by edwar 2012-09-27
	 *************************************************************************/
	
	/**
	 * 
	 * @param uid
	 * @param accessToken
	 * @param listener
	 */
	public void getXUserInf(
			final String uid,
			final String accessToken,
			final XResponseListener listener){
		new Thread(new Runnable() {
			@Override
			public void run() {
				final OutwardBaseRes bRes=OutwardHttpClientHelper.outwardResByGet(XWeiboUserRes.class, 
						OutwardHttpClientHelper.createSinaUserUrl(accessToken, uid));
                try {
    				if(bRes instanceof XWeiboErrorRes){
    					throw new XWeiboException(((XWeiboErrorRes)bRes).getError(),
    							Integer.valueOf(((XWeiboErrorRes) bRes).getError_code()));
    				}
    				listener.onComplete(bRes);		
                } 
                catch (XWeiboException e) {
                    listener.onError(e);
                }
			}
		}).start();
	}
	
	/**
	 * 发布一条新浪图片微博
	 * @param url  post url
	 * @param wbParams 至少包括access_token、status字段的对象。
	 * @param file File或Bitmap对象文件,无文件传null
	 * @param listener 分享结果监听器
	 */
	public void postXStatus( 
			final StringBuffer url, 
			final SocialParameters wbParams, 
			final Object file,
			final XResponseListener listener){
		new Thread(new Runnable() {
			@Override
			public void run() {
				final OutwardBaseRes bRes=OutwardHttpClientHelper.outwardResByPost(
						XWeiboBaseRes.class, url, wbParams, file);
				
                try {
                	//是错误信息的json，抛出XWeiboException异常
    				if(bRes instanceof XWeiboErrorRes){
    					throw new XWeiboException(((XWeiboErrorRes)bRes).getError(),
    							Integer.valueOf(((XWeiboErrorRes) bRes).getError_code()));
    				}
    				listener.onComplete(bRes);		
                } 
                //是错误信息的json，用onError处理
                catch (XWeiboException e) {
                    listener.onError(e);
                }
			}
		}).start();
	}
	
	
	/**
	 * 关注一个用户(新浪微博 )
	 * @param url  post url
	 * @param wbParams 至少包括access_token、需要关注的用户ID。
	 * @param listener 分享结果监听器
	 */
	public void xFollow( 
			final StringBuffer url, 
			final SocialParameters wbParams, 
			final XResponseListener listener){
		new Thread(new Runnable() {
			@Override
			public void run() {
				final OutwardBaseRes bRes=OutwardHttpClientHelper.outwardResByPost(
						XWeiboBaseRes.class, url, wbParams, null);
				
                try {
                	//是错误信息的json，抛出XWeiboException异常
    				if(bRes instanceof XWeiboErrorRes){
    					throw new XWeiboException(((XWeiboErrorRes)bRes).getError(),
    							Integer.valueOf(((XWeiboErrorRes) bRes).getError_code()));
    				}
    				listener.onComplete(bRes);		
                } 
                //是错误信息的json，用onError处理
                catch (XWeiboException e) {
                    listener.onError(e);
                }
			}
		}).start();
	}
	
	
    
	/**
	 * 新浪微博   操作结果监听器
	 */
    public static interface XResponseListener {
        public void onComplete(OutwardBaseRes response);
        
        public void onError(XWeiboException e);
    }
	
	
	/*************************************************************************
	 * 新浪微博 的 方法  - END  added by edwar 2012-09-27
	 *************************************************************************/
	

	/*************************************************************************
	 * QQ聊天器 的 方法  - START added by edwar 2012-09-27
	 *************************************************************************/
	
	
	/**
	 * 
	 * @param url  获取openId的路径
	 * @param wbParams 带有accessToken的 参数集合
	 * @param listener 回调函数
	 */
	public void qqGetOpenId( 
			final StringBuffer url, 
			final SocialParameters wbParams, 
			final QQResponseListener listener){
		new Thread(new Runnable() {
			@Override
			public void run() {
				final OutwardBaseRes bRes=OutwardHttpClientHelper.outwardResByGet(
						QQGetOpenIdRes.class, url, wbParams);
				
                try {
                	//是错误信息的json，抛出XWeiboException异常
    				if(bRes instanceof QQGetIdErrorRes){
    					throw new QQPlatformException(((QQGetIdErrorRes)bRes).getError_description(),
    							Integer.valueOf(((QQGetIdErrorRes) bRes).getError()));
    				}
    				listener.onComplete(bRes);		
                } 
                //是错误信息的json，用onError处理
                catch (QQPlatformException e) {
                    listener.onError(e);
                }
			}
		}).start();
	}
	
	
	/**
	 * 
	 * @param uid
	 * @param accessToken
	 * @param listener
	 */
	public void qqGetUserInf(
			final String openid,
			final String accessToken,
			final QQResponseListener listener){
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sbuf=new StringBuffer(QQPlatform.URL_GET_USER_INF);
				sbuf.append("?format=json&access_token=");
				sbuf.append(accessToken);
				sbuf.append("&oauth_consumer_key=");
				sbuf.append(QQPlatform.getAppId());
				sbuf.append("&openid=");
				sbuf.append(openid);
				final OutwardBaseRes bRes=OutwardHttpClientHelper.outwardResByGet(QQUserRes.class, 
						sbuf);
                try {
    				if(!((QQBaseRes)bRes).getRet().equals("0")){
    					throw new QQPlatformException(((QQBaseRes)bRes).getMsg(),
    							Integer.valueOf(((QQBaseRes) bRes).getRet()));
    				}
    				listener.onComplete(bRes);		
                } 
                catch (QQPlatformException e) {
                    listener.onError(e);
                }
			}
		}).start();
	}
	
	
	
	/**
	 * QQ账号   操作结果监听器
	 */
    public static interface QQResponseListener {
        public void onComplete(OutwardBaseRes response);
        
        public void onError(QQPlatformException e);
    }
    
    

	/*************************************************************************
	 * QQ聊天器 的 方法  - END  added by edwar 2012-09-27
	 *************************************************************************/

	
}
