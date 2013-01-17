/**
 * 
 */
package com.mama100.android.member.activities;

import java.sql.Timestamp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.mama100.android.member.bean.thirdparty.CommonBean;
import com.mama100.android.member.bean.thirdparty.QQLoginBean;
import com.mama100.android.member.bean.thirdparty.SinaWeiboBean;
import com.mama100.android.member.bean.thirdparty.TencentWeiboBean;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.outwardHttp.OutwardBaseRes;
import com.mama100.android.member.outwardHttp.OutwardHttpClientHelper;
import com.mama100.android.member.outwardWeibo.AsyncSocialRunner;
import com.mama100.android.member.outwardWeibo.AsyncSocialRunner.QQResponseListener;
import com.mama100.android.member.outwardWeibo.QQGetOpenIdRes;
import com.mama100.android.member.outwardWeibo.QQPlatform;
import com.mama100.android.member.outwardWeibo.QQPlatformException;
import com.mama100.android.member.outwardWeibo.QQUserRes;
import com.mama100.android.member.outwardWeibo.XWeibo;
import com.mama100.android.member.outwardWeibo.XWeiboException;
import com.mama100.android.member.outwardWeibo.XWeiboUserRes;
import com.mama100.android.member.util.LogUtils;

/**
 * 
 * @author ecoo
 * 
 *         modified by edwar 2012-09-27 改类名:weiboActivity
 *         为ThirdPartyLoginActivity，因为之前单指新浪微博。 现在指多种第三方登录， 包括：sina微博，qq登录，腾讯微博
 */
public abstract class ThirdPartyLoginActivity extends BaseActivity {

	public String TAG = this.getClass().getSimpleName();

	/****************************
	 * 新浪微博登录变量
	 *****************************/
	private SinaWeiboBean bean_SinaWeibo;

	/****************************
	 * QQ聊天器-登录变量
	 *****************************/
	private QQLoginBean bean_qqlogin;

	/****************************
	 * 腾讯微博-登录变量
	 *****************************/
	private TencentWeiboBean bean_tencentWeibo;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bean_SinaWeibo =new SinaWeiboBean();
		bean_SinaWeibo.getValuesFromSharePreference();
		
		bean_qqlogin = new QQLoginBean();
		bean_qqlogin.getValuesFromSharePreference();
		
//		bean_tencentWeibo = new TencentWeiboBean();
//		bean_tencentWeibo.getValuesFromSharePreference();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearAllObjects();
	}
	
	private void clearAllObjects() {
		bean_qqlogin = null;
		bean_SinaWeibo = null;
//		bean_tencentWeibo = null;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	// 网页方式授权方法
	protected void author2Method(int type) {
		String url = "";
		switch (type) {
		case SinaWeiboBean.REQ_AUTHOR:
			url = SinaWeiboBean.authorUrl();
			break;
		case QQLoginBean.REQ_AUTHOR:
			url = QQLoginBean.authorUrl();
			break;

		case TencentWeiboBean.REQ_AUTHOR:
			url = TencentWeiboBean.authorUrl();
			break;
		default:
			break;
		}

		Intent intent = new Intent(getApplicationContext(),
				ThirdPartyWebViewActivity.class).putExtra(
				ThirdPartyWebViewActivity.KEY_URL, url).putExtra(
				ThirdPartyWebViewActivity.KEY_ACTION, type);
		startActivityForResult(intent, type);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == CommonBean.RES_SUCCESS) {
			// 1 . 新浪微博用户授权回调处理
			if (requestCode == SinaWeiboBean.REQ_AUTHOR) {

				final String newS = data
						.getStringExtra(CommonBean.KEY_URL_DATA).replace("#",
								"?");
				bean_SinaWeibo.setValuesIntoSharedPreference(newS);
//				Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
				LogUtils.loge(TAG, "\n\n >Access_token : "
						+ bean_SinaWeibo.getAccessToken()
						+ " \n\n >Expires_in: " + bean_SinaWeibo.getExpiresIn());

				// 获取用户信息
				// 获取openid
				LogUtils.loge(TAG, "3 getOpenId start- "+new Timestamp(System.currentTimeMillis()));
				XWeibo.getInstance().getXUserInf(bean_SinaWeibo.getUid(),
						new XUserWeiboLis());
				LogUtils.loge(TAG, "4 getOpenId end- "+new Timestamp(System.currentTimeMillis()));
			}

			else
			// 2 . QQ用户登录授权回调处理
			if (requestCode == QQLoginBean.REQ_AUTHOR) {
				final String newS = data.getStringExtra(QQLoginBean.KEY_URL_DATA)
						.replace("#", "");

				bean_qqlogin.setValuesIntoSharedPreference(newS);

				Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
				LogUtils.loge(TAG, ">Access_token : " + bean_qqlogin.getAccessToken()
						+ " \n\n >Expires_in: " + bean_qqlogin.getExpiresIn());

				// 获取openid
				LogUtils.loge(TAG, "3 getOpenId start- "+new Timestamp(System.currentTimeMillis()));
				QQPlatform.getInstance().getOpenId(new GetOpenIdLis());
				LogUtils.loge(TAG, "4 getOpenId end- "+new Timestamp(System.currentTimeMillis()));
			}
			else

			// 3 . 腾讯微博用户授权回调处理
			if (requestCode == TencentWeiboBean.REQ_AUTHOR) {
				final String newS = data.getStringExtra(TencentWeiboBean.KEY_URL_DATA).replace(
						"#", "?");
				bean_tencentWeibo.setValuesIntoSharedPreference(newS);
				Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
				LogUtils.loge(TAG, ">Access_token : " + bean_tencentWeibo.getAccessToken()
						+ " \n\n >Expires_in: " + bean_tencentWeibo.getExpiresIn()
						+"\n\n>你好，"+bean_tencentWeibo.getNickname());
			}

		}
		//点击了网页上的"取消"
		else if ((requestCode == SinaWeiboBean.REQ_AUTHOR||
				requestCode == QQLoginBean.REQ_AUTHOR||
				requestCode == TencentWeiboBean.REQ_AUTHOR)
				&& resultCode == CommonBean.RES_REFUSE) {
			
			Toast.makeText(ThirdPartyLoginActivity.this, "已取消操作",
					Toast.LENGTH_SHORT).show();
		}
		// 其它原因导致授权失败
		else if (
				(requestCode == SinaWeiboBean.REQ_AUTHOR||
				requestCode == QQLoginBean.REQ_AUTHOR||
				requestCode == TencentWeiboBean.REQ_AUTHOR)
				&& resultCode == CommonBean.RES_FAILED) {
			//让webview不记住当前登陆用户
			CookieSyncManager.createInstance(getApplicationContext());   
			CookieManager cookieManager = CookieManager.getInstance();  
			cookieManager.removeAllCookie();  
			
			Toast.makeText(ThirdPartyLoginActivity.this, "授权失败",
					Toast.LENGTH_SHORT).show();
		}
	}

	/*****************************************************************************************************************
	 * 新浪微博 获取用户信息的 方法 集合 -- added by edwar 2012-09-27 START
	 *****************************************************************************************************************/

	// 获取用户信息结果，监听器
	class XUserWeiboLis implements AsyncSocialRunner.XResponseListener {
		@Override
		public void onComplete(final OutwardBaseRes response) {
			final XWeiboUserRes xRes = (XWeiboUserRes) response;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!xRes.getCode().equals(OutwardHttpClientHelper.SUCCESS)) {
						Toast.makeText(ThirdPartyLoginActivity.this,
								xRes.getCode() + ":" + xRes.getDesc(),
								Toast.LENGTH_LONG).show();
						return;
					}

					bean_SinaWeibo.setValuesIntoSharedPreference(xRes);
					LogUtils.loge(TAG,
							"\n\n>你好，" + bean_SinaWeibo.getNickname());
					
//					clearAllObjects(); //等异步执行完毕后，再去处理内存。。
				}
			});
		}

		@Override
		public void onError(final XWeiboException e) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/*
					 * 21327：token过期 21314：Token已经被使用 21315：Token已经过期
					 * 21316：Token不合法 21317：Token不合法
					 */
					if (e.getStatusCode() == 21327
							|| e.getStatusCode() == 21314
							|| e.getStatusCode() == 21315
							|| e.getStatusCode() == 21316
							|| e.getStatusCode() == 21317) {
						Toast.makeText(ThirdPartyLoginActivity.this,
								"授权已失效，请重新授权", Toast.LENGTH_LONG).show();
						author2Method(SinaWeiboBean.REQ_AUTHOR);
						return;
					}
					Toast.makeText(
							ThirdPartyLoginActivity.this,
							String.format("获取用户信息失败" + ":%s", e.getMessage()
									+ "[code:" + e.getStatusCode())
									+ "]", Toast.LENGTH_LONG).show();
				}
			});
		}

	}
	
	
	//更新是否关注妈妈100checkbox状态
	protected abstract void updateFollowMaMa100CheckBoxStatus(boolean b);
	protected abstract void shareWeiboComplete(boolean b);
	protected abstract void doSSoLogin(boolean b, int requestCode);
	
	
	
    //关注结果，监听器
    public class WeiboFollowLis
    implements AsyncSocialRunner.XResponseListener{
 	@Override
 	public void onComplete(final OutwardBaseRes response) {
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
            	 if(ThirdPartyLoginActivity.this.isFinishing()){
             		return;
             	}
            	 
     			if(!response.getCode().equals(OutwardHttpClientHelper.SUCCESS)){
     				Toast.makeText(ThirdPartyLoginActivity.this, response.getCode()+":"+response.getDesc(), Toast.LENGTH_LONG)
     				.show();
     				return;
     			}
     			//此时为取消关注操作
     			if(SinaWeiboBean.isFollowMama100()){
                    Toast.makeText(ThirdPartyLoginActivity.this, "已取消关注", Toast.LENGTH_SHORT).show();
                    SinaWeiboBean.setFollowMama100(false);
                    updateFollowMaMa100CheckBoxStatus(false);
                    bean_SinaWeibo.updateIsFollowUsValueInSharedPreference(false);
     			}
     			else{
                    Toast.makeText(ThirdPartyLoginActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                    SinaWeiboBean.setFollowMama100(true);
                    updateFollowMaMa100CheckBoxStatus(true);
                    bean_SinaWeibo.updateIsFollowUsValueInSharedPreference(true);
     			}
             }
         });
 	}

 	@Override
 	public void onError(final XWeiboException e) {
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
            	 if(ThirdPartyLoginActivity.this.isFinishing()){
              		return;
              	}
            	 
         if(e.getStatusCode()==21327
         		||e.getStatusCode()==21314
         		||e.getStatusCode()==21315
         		||e.getStatusCode()==21316
         		||e.getStatusCode()==21317
        		||e.getStatusCode()==21332){
         	Toast.makeText(ThirdPartyLoginActivity.this,"授权已失效，请重新授权",Toast.LENGTH_LONG)
         	.show();
         	author2Method(SinaWeiboBean.REQ_AUTHOR);
         	return;
         }
         //20506，用户已关注mama100。此处视为成功。
         if(e.getStatusCode()==20506){
             Toast.makeText(ThirdPartyLoginActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
             SinaWeiboBean.setFollowMama100(true);
             updateFollowMaMa100CheckBoxStatus(true);
             bean_SinaWeibo.updateIsFollowUsValueInSharedPreference(true);
				return;
         }
         Toast.makeText(
        		 ThirdPartyLoginActivity.this,e.getStatusDesc(), Toast.LENGTH_SHORT).show();
 	}
         });
 	   
 	}

	
    }
   
    
    

	
    //分享结果，监听器
    public class MyShareWeiboLis
    implements AsyncSocialRunner.XResponseListener{
        @Override
        public void onComplete(final OutwardBaseRes response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	if(ThirdPartyLoginActivity.this.isFinishing()){
                		return;
                	}
                	if(!response.getCode().equals(OutwardHttpClientHelper.SUCCESS)){
                		//新浪服务器认为我们的请求无效： 400，连续分享很容易造成这个问题。
                		//前面我们已把这个错误代码组合在desc中了 "-code"
        				if(response.getDesc().contains("-400")){
        					Toast.makeText(ThirdPartyLoginActivity.this, "400：新浪微博拒绝了我们的分享...", Toast.LENGTH_LONG)
    							.show();
        				}
        				else
        					Toast.makeText(ThirdPartyLoginActivity.this, response.getCode()+":"+response.getDesc(), Toast.LENGTH_LONG)
        						.show();
        				

        				shareWeiboComplete(false);
        				return;
        			}
                	shareWeiboComplete(true);
                	}
            });
        }

        @Override
        public void onError(final XWeiboException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	if(ThirdPartyLoginActivity.this.isFinishing()){
                		return;
                	}
                	shareWeiboComplete(false);
                    setRightButtonVisibility(View.VISIBLE);
                    /*
                   	21327：token过期
                    21314：Token已经被使用
                	21315：Token已经过期
                	21316：Token不合法
                	21317：Token不合法 
                	21332: access_token无效
                	*/
                    if(e.getStatusCode()==21327
                    		||e.getStatusCode()==21314
                    		||e.getStatusCode()==21315
                    		||e.getStatusCode()==21316
                    		||e.getStatusCode()==21317
                    		||e.getStatusCode()==21332){
                    	Toast.makeText(ThirdPartyLoginActivity.this,e.getStatusDesc(),Toast.LENGTH_LONG)
                    	.show();
                    	author2Method(SinaWeiboBean.REQ_AUTHOR);
                    	return;
                    }
                    Toast.makeText(
                    		ThirdPartyLoginActivity.this,e.getStatusDesc(), Toast.LENGTH_LONG).show();
                }
            });
        }
    	
    }
    

    


	/*****************************************************************************************************************
	 * 新浪微博 获取用户信息的 方法 集合 -- added by edwar 2012-09-27 END
	 *****************************************************************************************************************/

	/*****************************************************************************************************************
	 * QQ登录用户信息的 方法 集合 -- added by edwar 2012-09-27 START
	 *****************************************************************************************************************/
    
    
	
	// 1,先获取用户uid(就是QQ的openId)监听器
	class GetOpenIdLis implements QQResponseListener {

		@Override
		public void onComplete(final OutwardBaseRes response) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!response.getCode().equals(
							OutwardHttpClientHelper.SUCCESS)) {
						Toast.makeText(getApplicationContext(),
								response.getCode() + ":" + response.getDesc(),
								Toast.LENGTH_LONG).show();
						return;
					}
					bean_qqlogin
							.setValuesIntoSharedPreference((QQGetOpenIdRes) response);
					QQPlatform.getInstance().getQzoneUserInf(
							bean_qqlogin.getUid(), new QQUserLis());
					doSSoLogin(true, QQLoginBean.REQ_AUTHOR);
				}
			});

		}

		@Override
		public void onError(final QQPlatformException e) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/*
					 * 100013 access token非法。 100014 access token过期。 100015
					 * access token废除。 100016 access token验证失败。
					 */
					if (e.getStatusCode() == 100013
							|| e.getStatusCode() == 100014
							|| e.getStatusCode() == 100015
							|| e.getStatusCode() == 100016) {
						Toast.makeText(ThirdPartyLoginActivity.this, "授权已失效",
								Toast.LENGTH_LONG).show();
						return;
					}
					Toast.makeText(ThirdPartyLoginActivity.this,
							"发生异常:" + e.getStatusCode(), Toast.LENGTH_LONG)
							.show();

				}
			});

		}

	}

	
	// 2,在先获取用户uid(就是QQ的openId)之后，再获取用户信息结果，监听器
	class QQUserLis implements AsyncSocialRunner.QQResponseListener {
		@Override
		public void onComplete(final OutwardBaseRes response) {
			final QQUserRes qRes = (QQUserRes) response;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!qRes.getCode().equals(OutwardHttpClientHelper.SUCCESS)) {
						Toast.makeText(ThirdPartyLoginActivity.this,
								qRes.getCode() + ":" + qRes.getDesc(),
								Toast.LENGTH_LONG).show();
						return;
					}
					LogUtils.loge(TAG, "3.5 getNickname - start "+new Timestamp(System.currentTimeMillis()));
					bean_qqlogin.setValuesIntoSharedPreference(qRes);
					LogUtils.loge(TAG,  "\n\n>你好，"
							+ bean_qqlogin.getNickname());
					LogUtils.loge(TAG, "3.8 getNickname - start "+new Timestamp(System.currentTimeMillis()));
				}
			});
		}

		@Override
		public void onError(final QQPlatformException e) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/*
					 * 100013 access token非法。 100014 access token过期。 100015
					 * access token废除。 100016 access token验证失败。
					 */
					if (e.getStatusCode() == 100013
							|| e.getStatusCode() == 100014
							|| e.getStatusCode() == 100015
							|| e.getStatusCode() == 100016) {
						Toast.makeText(ThirdPartyLoginActivity.this, "授权已失效，请重新授权",
								Toast.LENGTH_LONG).show();
						author2Method(QQLoginBean.REQ_AUTHOR);
						return;
					}
					Toast.makeText(
							ThirdPartyLoginActivity.this,
							String.format("获取用户信息失败" + ":%s", e.getMessage()
									+ "[code:" + e.getStatusCode())
									+ "]", Toast.LENGTH_LONG).show();
				}
			});
		}

	}


	/*****************************************************************************************************************
	 * QQ登录用户信息的 方法 集合 -- added by edwar 2012-09-27 END
	 *****************************************************************************************************************/

	/*****************************************************************************************************************
	 * 腾讯微博用户信息的 方法 集合 -- added by edwar 2012-09-27 START
	 *****************************************************************************************************************/

	/*****************************************************************************************************************
	 * 腾讯微博用户信息的 方法 集合 -- added by edwar 2012-09-27 END
	 *****************************************************************************************************************/

	
	
	/*******************************************************
	 * getter & setters
	 */
	
	public SinaWeiboBean getBean_SinaWeibo() {
		return bean_SinaWeibo;
	}

	public void setBean_SinaWeibo(SinaWeiboBean bean_SinaWeibo) {
		this.bean_SinaWeibo = bean_SinaWeibo;
	}

	public QQLoginBean getBean_qqlogin() {
		return bean_qqlogin;
	}

	public void setBean_qqlogin(QQLoginBean bean_qqlogin) {
		this.bean_qqlogin = bean_qqlogin;
	}

	public TencentWeiboBean getBean_tencentWeibo() {
		return bean_tencentWeibo;
	}

	public void setBean_tencentWeibo(TencentWeiboBean bean_tencentWeibo) {
		this.bean_tencentWeibo = bean_tencentWeibo;
	}

	
	
	
}
