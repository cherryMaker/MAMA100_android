package com.mama100.android.member.activities;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.MonthDisplayHelper;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.message.MessageHomeActivity;
import com.mama100.android.member.bean.MobResponseCode;
import com.mama100.android.member.businesslayer.MessageProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.base.HtmlRes;
import com.mama100.android.member.domain.message.DetailMessageReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.StorageUtils;

//查看消息详情 用到的页面
public class WebViewActivity extends BaseActivity {
	public static final String ID = "id"; // 消息的id
	public static final int TYPE_HTML = 0;
	public static final int TYPE_URL = 1;

	public static final String TITLE_NAME = "title";
	private String TITLE_VALUE;

	public static final String TYPE_VALUE = "typeval";

	// 传过来参数的值
	private String CUR_TYPE_VALUE = null;
	// 当前加载类型，如TYPE_HTML
	public static int CURRENT_TYPE = TYPE_HTML;

	/*************************************************************************
	 * 消息详情界面用到的 变量
	 ****************************************************************************/
	private String here = "";// 消息详情,判断点击是否从通知栏点击进来

	private WebView webV_content;

	/***********************************************************************
	 * 通知栏 消息用到的变量
	 ***********************************************************************/
	private NotificationManager notificationManager;
	private int notificationId = 0;
	public boolean isSuccess = false;
	
	/***********************************************************************
	 * added by edwar 2012-09-13 为了支持js
	 ***********************************************************************/
	private ProgressDialog dialog;
	
	/*****************************************************
	 * android webview 调用 html里的javascript的方法 用到的变量
	 *****************************************************/
	Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(0);
		setBackgroundPicture(R.drawable.bg_wall);
		setupTopBar();
		// 获取消息的id
		String messageId = getIntent().getStringExtra(ID);
		// here作为最准确判断是从通知栏进来的变量。
		here = getIntent().getStringExtra("edwar");

		// 清除通知栏的消息
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(AppConstants.COMMON_ID);

		// 通知栏
		// 为了消息栏
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationId = Integer.valueOf(messageId);

		DetailMessageReq request = new DetailMessageReq();
		request.setId(messageId);
		request.setUname(BasicApplication.getInstance().getUsername());

		AsynQueReqHTMLTask task = new AsynQueReqHTMLTask(this);
		task.displayProgressDialog(R.string.doing_query);
		task.execute(request);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	private void setupTopBar() {
		setTopLabel(R.string.message_title2);
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonVisibility(View.GONE);
	}

	public void setContentView(int layoutResID) {
		webV_content = new WebView(this);
		
		/****************************************************
		 * 设置cookie  --- START
		 ****************************************************/
		//added by edwar  2012-09-20
		String httpAddr1 = getHttpIpAddress()
				+ AppConstants.POINT_OBTAIN_ACTION;
		CookieSyncManager cookieSyncManager = CookieSyncManager
				.createInstance(WebViewActivity.this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.setCookie(httpAddr1, "JSESSIONID="
				+ BasicApplication.getInstance().getMySessionId());
		cookieSyncManager.sync();
	    /****************************************************
		 * 设置cookie  --- END
		 ****************************************************/
		
		
		webV_content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webV_content.setBackgroundColor(Color.TRANSPARENT);
		webV_content.setEnabled(false);
		/********** html javascript 调用 webview 里的 对象common的方法-START ******/
		webV_content.clearCache(true);
		webV_content.getSettings().setJavaScriptEnabled(true);
		webV_content.addJavascriptInterface(new CommonTouchJs(), "common");
		/********** html javascript 调用 webview 里的 对象common的方法-END ******/
		
		ui_content.addView(webV_content);
	}

	@Override
	public void doClickLeftBtn() {
		// 1，如果MessageListActivity没有打开过，证明肯定是从通知栏进来的
	    //情况:@see BasicApplication里的isMessageListAlreadyStart 及 isHomepageAlreadyStart
		if (!BasicApplication.getInstance().isMessageListAlreadyStart()) {
			if (!BasicApplication.getInstance().isHomepageAlreadyStart()) {
				// 无论是否成功打开详情，都一样
				Intent intent = new Intent(getApplicationContext(),
						MessageHomeActivity.class);
				startActivity(intent);
			}else{
				finish();
			}
		}
		// 2，如果MessageListActivity已经打开，则无论是否是通知栏进来的，都一样
		else {
			// 如果成功打开详情界面
			if (isSuccess) {
				setResult(RESULT_OK);
			} else {
				setResult(RESULT_CANCELED);
			}
		}
		finish();
	}

	/***********************************************************
	 * added by edwar 2012-07-03 START
	 ************************************************************/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 清空缓存
		if (webV_content != null) {
			
			//让webview不记住当前第三方登陆用户
			CookieSyncManager.createInstance(getApplicationContext());   
			final CookieManager cookieManager = CookieManager.getInstance();  
			cookieManager.removeAllCookie();  
			
			webV_content.clearCache(true);
			webV_content.clearHistory();	
			webV_content = null;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		doClickLeftBtn();
	}

	/***********************************************************
	 * added by edwar 2012-07-03 END
	 ************************************************************/

	// 点击查看 具体消息详情时 用到
	class AsynQueReqHTMLTask extends AsyncReqTask {
		public AsynQueReqHTMLTask(Context context) {
			super(context);

		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			// 从通知栏进来,走自动登录流程
			if (here != null && here.equalsIgnoreCase("right")) {
				return MessageProvider.getInstance(getApplicationContext())
						.getMesssageDetailFromNotificationBar(
								(DetailMessageReq) request);
			} else {
				return MessageProvider.getInstance(getApplicationContext())
						.getMesssageDetail((DetailMessageReq) request);
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			if ((response.getCode().equals(MobResponseCode.TGT_INVALID))
					| response.getCode().equals(MobResponseCode.NULL_TGT)) {
				StorageUtils.storeLoginTGTInClient(getApplicationContext(), "");
				BasicApplication.getInstance().setTgt("");
			}
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				makeText(response.getDesc());
				return;
			}

			isSuccess = true;
			/*****************
			 * 通知栏进来的消息Start
			 *****************/
			if (here != null && here.equalsIgnoreCase("right")) {
				// 取消对应通知栏里的消息
				// 这里自动点击就cancel了，所以不用手动cancel
				// notificationManager.cancel(notificationId);
				// 更新未读消息
				int count = BasicApplication.getInstance().getUnreadMsgSum();
				BasicApplication.getInstance().setUnreadMsgSum(count - 1);
				//added by edwar 2012-11-07 解决MsgHome->back->Home的流程问题
				BasicApplication.getInstance().setFromNotificationBar(true);
			}
			/*****************
			 * 通知栏进来的消息END
			 *****************/

			final String htmlcontent = ((HtmlRes) response).getContent();
			// 之前css都直接写在内容里，没有用相对路径
			// webV_content.loadDataWithBaseURL(" ",CUR_TYPE_VALUE, "text/html",
			// "UTF-8","");
			// 现在，用相对的 ../../css 路径
			if(webV_content!=null){
			webV_content.loadDataWithBaseURL("http://"
					+ BasicApplication.getInstance().getIpAddr() + "/msg/ ",
					htmlcontent, "text/html", "UTF-8", "");
			}
		}

	}
	
	
	/****************************************************************************
	 * added by edwar 2012-09-13 网页调用webview里的方法  -START
	 ****************************************************************************/

	protected class CommonTouchJs {

		public void beforeSendData(String msg) {
			dialog = new ProgressDialog(WebViewActivity.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage(msg);
			dialog.setCancelable(true);
			dialog.show();
		}

		public void completeSendData(String msg) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			Toast.makeText(WebViewActivity.this, msg, Toast.LENGTH_LONG).show();
		}

		public void successSendData(String msg) {
			dialog.setMessage(msg);
			dialog.setCancelable(true);
			dialog.show();
		}
	}

	/****************************************************************************
	 * added by edwar 2012-09-13 网页调用webview里的方法  -END
	 ****************************************************************************/

	/**
	 * 获取HTTP地址,如：http://m.hyt.mama100.com
	 * 
	 * @return
	 */
	protected String getHttpIpAddress() {

		String url = "http://";
		url += BasicApplication.getInstance().getIpAddr();
		return url;
	}
	
}
