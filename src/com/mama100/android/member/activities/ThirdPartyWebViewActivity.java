package com.mama100.android.member.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mama100.android.member.R;
import com.mama100.android.member.bean.thirdparty.CommonBean;
import com.mama100.android.member.bean.thirdparty.QQLoginBean;
import com.mama100.android.member.bean.thirdparty.SinaWeiboBean;
import com.mama100.android.member.bean.thirdparty.TencentWeiboBean;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.outwardWeibo.QQPlatform;
import com.mama100.android.member.outwardWeibo.TCWeibo;
import com.mama100.android.member.outwardWeibo.XWeibo;

/**
 * 内嵌浏览器
 * 
 * @author ecoo
 * 
 */
public class ThirdPartyWebViewActivity extends BaseActivity {
	// /constant
	public static final String KEY_URL = "url"; // 请求的url

	// /views
	private WebView webView;
	private ProgressBar proBar_loading;

	// /fields
	private String value_url;
	private int auth_code = 00;

	// 根据action来判断顶部栏显示什么文本。。
	private int valueAction;
	public static final String KEY_ACTION = "action"; // 请求名称
	public static final int REQ_XUSER_WEIBO = 11; // 请求查看新浪用户微博
	public static final int REQ_MAMA100_WEIBO = 12; // 请求查看妈妈100微博

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		value_url = getIntent().getStringExtra(KEY_URL);
		valueAction = getIntent().getIntExtra(KEY_ACTION, REQ_XUSER_WEIBO);
		// 根据url里的内容判断来源
		
		if (value_url.contains(XWeibo.URL_OAUTH2_ACCESS_AUTHORIZE)) {
			auth_code = SinaWeiboBean.REQ_AUTHOR;
			super.setContentView(R.layout.webview_layout_sinaweibo);
		} else {
			super.setContentView(R.layout.webview_layout);
			auth_code = TencentWeiboBean.REQ_AUTHOR;
		}

		proBar_loading = (ProgressBar) findViewById(R.id.proBar_loading);
		webView = (WebView) findViewById(R.id.webV_view);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSaveFormData(false);
		webView.getSettings().setSavePassword(false);
		// modified by edwar 2012-11-05 隐藏最右边的滚动栏，不留空白
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//		webView.setScrollContainer(true);


		webView.loadUrl(value_url);

		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				//
				// LogUtils.logv("test", "url:"+url);
				return true;
			}

			// 接受https所有证书
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {

			}

			/**
			 * 地址栏发生变化，并有access_token或error_code字段， 即代表新浪服务器已响应请求并返回了数据
			 */
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.v("test", "onPageStarted url:" + url);

				if (auth_code == SinaWeiboBean.REQ_AUTHOR
						&& (url.contains("access_token") || url
								.contains("error_code"))) {
					Intent tmpIntent = new Intent().putExtra(
							CommonBean.KEY_URL_DATA, url);
					// 授权成功
					if (url.contains("access_token"))
						setResult(CommonBean.RES_SUCCESS, tmpIntent);
					// 用户拒绝授权
					else if (url.contains("error_code=21330")) {
						setResult(CommonBean.RES_REFUSE, tmpIntent);
					}
					// 其它原因失败
					else
						setResult(CommonBean.RES_FAILED, tmpIntent);
					finish();
				}

				else if (auth_code == QQLoginBean.REQ_AUTHOR
						&& (url.contains("access_token") || url
								.contains("usercancel"))) {
					Intent tmpIntent = new Intent().putExtra(
							CommonBean.KEY_URL_DATA, url);
					// 授权成功
					if (url.contains("access_token"))
						setResult(CommonBean.RES_SUCCESS, tmpIntent);
					// 用户拒绝授权
					else if (url.contains("usercancel")) {
						setResult(CommonBean.RES_REFUSE, tmpIntent);
					}
					// 其它原因失败
					else
						setResult(CommonBean.RES_FAILED, tmpIntent);
					finish();
				}

				else if (auth_code == TencentWeiboBean.REQ_AUTHOR
						&& (url.contains("access_token") || url
								.contains("usercancel"))) {
					Intent tmpIntent = new Intent().putExtra(
							CommonBean.KEY_URL_DATA, url);
					// 授权成功
					if (url.contains("access_token"))
						setResult(CommonBean.RES_SUCCESS, tmpIntent);
					// 用户拒绝授权
					else if (url.contains("usercancel")) {
						setResult(CommonBean.RES_REFUSE, tmpIntent);
					}
					// 其它原因失败
					else
						setResult(CommonBean.RES_FAILED, tmpIntent);
					finish();
				}

			}
		});

		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (progress >= 60) {
					proBar_loading.setVisibility(View.GONE);
				}
			}
		});
		setupTopbarLabels();
	}

	public void doClickLeftBtn() {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	/***********************************************************
	 * added by edwar 2012-10-30 START
	 ************************************************************/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 清空缓存
		if (webView != null) {
			
			//让webview不记住当前第三方登陆用户
			CookieSyncManager.createInstance(getApplicationContext());   
			final CookieManager cookieManager = CookieManager.getInstance();  
			cookieManager.removeAllCookie();  
			
			webView.clearCache(true);
			webView.clearHistory();	
			webView = null;
		}
	}
	
	/***********************************************************
	 * added by edwar 2012-10-30 END
	 ************************************************************/
	
	private void setupTopbarLabels() {
		if (valueAction == SinaWeiboBean.REQ_AUTHOR){
			if(isUnlogin()){
				setTopLabel(R.string.login_with_sina_weibo_account);
			}else{
				setTopLabel(R.string.bind_with_sina_weibo_account);
			}
			
		}else if
				( valueAction == QQLoginBean.REQ_AUTHOR){
			setTopLabel(R.string.login_with_qq_account);	
		}else if
				( valueAction == TencentWeiboBean.REQ_AUTHOR){
			setTopLabel(R.string.login_with_member_phone);
		}
		
		if (valueAction == REQ_XUSER_WEIBO)
			setTopLabel(R.string.my_weibo);
		if (valueAction == REQ_MAMA100_WEIBO)
			setTopLabel(R.string.mama100_X);
		setRightButtonVisibility(View.GONE);
		setLeftButtonImage(R.drawable.selector_back);
		setBackgroundPicture(R.drawable.bg_wall);
	}

}
