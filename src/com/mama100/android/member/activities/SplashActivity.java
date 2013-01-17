/**
 * 
 */
package com.mama100.android.member.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mama100.android.member.R;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BackStackManager;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.NetworkUtils;
import com.mama100.android.member.util.StorageUtils;
import com.mama100.android.member.util.StringUtils;

/**
 * <p>
 * Description: SplashActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 欢迎界面
 * 
 */
public class SplashActivity extends Activity {

	private RelativeLayout rootLayout;
	private CustomAsyncTask task;

	// private Intent update_software_intent = null;// 更新软件版本的意图
	
	/****************************************************
	 * added by edwar 2012-09-20 进入引导页 
	 ****************************************************/
	private int GO_TO_GUIDE_PAGE = 10000;
	
	private boolean isLogining = false; //避免登录弹出等待窗口时，被点返回而关闭窗口

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		
		//弹出渠道号
		//BasicApplication.getInstance().showLongToast(getChannelCode(getApplicationContext()));
		
		rootLayout = (RelativeLayout) findViewById(R.id.layout_root);
		// setContentView(R.layout.splash2);
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onCreate");

		
		/*********************************************************************
		 * added by edwar 2012-09-20 start
		 *********************************************************************/
		
		//判读当前版本，如果与之前存储的版本不一样。就打开介绍界面。
		/************************* 1, 获取当前应用版本****************************************/
		int appversion = BasicApplication.getInstance().getLocalVersion();
		/************************* 2, 获取本地之前保留的应用版本*******************************/
		String value = StorageUtils.getShareValue(getApplicationContext(), AppConstants.LAST_APP_VERSION);
        int lastversion =  Integer.parseInt((StringUtils.isBlank(value))?"1":value);
		
    	//保留当前的应用版本进本地文件
		StorageUtils.setShareValue(getApplicationContext(), AppConstants.LAST_APP_VERSION, String.valueOf(appversion));
		BasicApplication.getInstance().setToExit(true);
		
		//如果本地保留的tgt 不为 "" , added by edwar 2012-09-03, 解决关机再启动不能自动登录
		//也解决tgt为空后，用户忘了注销而直接退出后，下次再进来的情况，就不会自动登录
		if(!StorageUtils.getLoginTGT(getApplicationContext()).equalsIgnoreCase("")){
			BasicApplication.getInstance().setAutoLogin(true);
		}else{
			BasicApplication.getInstance().setAutoLogin(false);
		}
        
        
		//同一个版本，首次进来，打开介绍界面;以后再次进来，就不用打开介绍界面。
		boolean isfirstopen = StorageUtils.getBooleanShareValue(this,
				AppConstants.IS_FIRST_OPEN);
		
		if (isfirstopen||(appversion!=lastversion)) {
			// 放入后台栈管理器
			BackStackManager.getInstance().putActivity(
					AppConstants.ACTIVITY_SPLASH_HOMEPAGE, this);
			startActivityForResult(new Intent(getApplicationContext(),
					SwitchViewDemoActivity.class), GO_TO_GUIDE_PAGE);
			return;
		}

		
		/*********************************************************************
		 * added by edwar 2012-09-20 end
		 *********************************************************************/
		judgeWhichChannelToGo();
	}
	
	
	/***
	 * 测试 channel
	获取渠道名 代码如下：
	 */
	public static String getChannelCode(Context context) {

	       String code = getMetaData(context, "BaiduMobAd_CHANNEL");

	       if (code != null) {

	           return code;

	       }

	       return "C_000";

	    }

	   

	    private static String getMetaData(Context context, String key) {

	       try {

	           ApplicationInfo  ai = context.getPackageManager().getApplicationInfo(

	                  context.getPackageName(), PackageManager.GET_META_DATA);

	           Object value = ai.metaData.get(key);

	       if (value != null) {

	              return value.toString();

	           }

	       } catch (Exception e) {

	           //

	       }

	       return null;

	    }


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogUtils.logi(AppConstants.PROGRESS_TAG, "SplashActivity onNewIntent");
		// onNewIntent --> onStart --> onResume

	}

	@Override
	protected void onDestroy() {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onDestroy");
		if (task != null && task.isCancelled() == false) {
			task.cancel(true);
		}
		super.onDestroy();
		rootLayout.setBackgroundDrawable(null);
	}
	
	
	
	//用于splash页面-> guide界面-> splash页面 这种页面转换
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == GO_TO_GUIDE_PAGE  &&resultCode == RESULT_OK){
			judgeWhichChannelToGo();
		}
	}

	
	//根据是否自动登录，选择正确的模式
	private void judgeWhichChannelToGo() {
		
		// 检测网络状态, 并给出错误提示
		if (!NetworkUtils
				.checkNetworkStatusAndHint(SplashActivity.this)) {
			goToUnLoginHomepage();
			return;
		}
		
		 if (BasicApplication.getInstance().isAutoLogin()) {
//				rootLayout.setBackgroundResource(R.drawable.welcome_picture);
				// TODO 显示后台滚动条 自动登录showProgress();
				new Handler().postDelayed(new Runnable() {
					public void run() {
						// TODO 关闭滚动条
						RedirectMainActivity();
					}
				}, 0);
			} else {
				goToUnLoginHomepage();
			}
	}


	/**
	 * <p>
	 * 
	 * @Title: goToUnLoginHomepage
	 * @param:
	 * @Description:
	 * @return </p>
	 */
	private void goToUnLoginHomepage() {
		Intent intent = new Intent(getApplicationContext(),
				HomePageActivity.class);
		intent.setAction(AppConstants.UNLOGIN_INTO_HOMEPAGE);
		startActivity(intent);
		finish();
		
	}

	/**
	 * 自动转换进主界面
	 */
	private void RedirectMainActivity() {

		BaseReq request = new BaseReq();
		task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);
		isLogining = true;
	}

	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return UserProvider.getInstance(getApplicationContext())
					.autoLogin();

		}

		@Override
		protected void handleResponse(BaseRes response) {
			isLogining = false;
			closeProgressDialog();
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				// 显示登录和注册按钮
				goToUnLoginHomepage();
				Toast.makeText(SplashActivity.this, response.getDesc(),
						Toast.LENGTH_LONG).show();
//				if(mProgressDialog!=null&&mProgressDialog.isShowing()){
//					task.closeProgressDialog();
//				}
				return;
			}

			Intent intent = new Intent(getApplicationContext(),
					HomePageActivity.class);
			startActivity(intent);
			finish();
		}

	}

	/*
	 * <p> onBackPressed </p>
	 */
	@Override
	public void onBackPressed() {
		if (isLogining) {
			return;
		}
		
		// 只有该页面刚打开或者用户注销了，isToExit才会为true;
		// 进入登录或者注册或者主界面，isToExit才会为false;
		if (BasicApplication.getInstance().isToExit()) {
			BasicApplication.getInstance().exit();
		}
		finish();
	}

}
