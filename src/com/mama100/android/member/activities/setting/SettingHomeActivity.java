/**
 * 
 */
package com.mama100.android.member.activities.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.activities.user.EditProfileActivity;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BackStackManager;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.service.BootService;
import com.mama100.android.member.util.LogUtils;

/**
 * <p>
 * Description: SettingHomeActivity.java
 * </p>
 * @author aihua.yan
 * 2012-7-16
 * 设置模块主界面
 * 
 * 
 * modified by liyang 2012-11-27  流程修改（优化）
 * 未登录状态下，设置界面隐藏个人信息功能，变成一个登录按钮。
 * 
 * 
 * modified by liyang 2012-12-17  增加当前位置功能（定位所在城市）
 */
public class SettingHomeActivity 
extends BaseActivity implements View.OnClickListener {
	
	Button btn_call;
	LinearLayout layout_inf;
	LinearLayout layout_account;
	LinearLayout layout_changepwd;
	LinearLayout layout_aboutus;
	LinearLayout layout_feedback;
	LinearLayout layout_checkversion;
	
	Button btn_cancel_account;
	
	private Intent update_software_intent = null;//更新软件版本的意图
	
	private LogOutTask myLogoutTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()+"   onCreate");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.setting_home2);
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonVisibility(View.INVISIBLE);
		setTopLabel(getString(R.string.setting));
		
		btn_call=(Button)findViewById(R.id.btn_call);
		btn_cancel_account=(Button)findViewById(R.id.btn_cancel_account);
		
		layout_inf=(LinearLayout)findViewById(R.id.layout_inf);
		layout_account=(LinearLayout)findViewById(R.id.layout_account);
		layout_changepwd=(LinearLayout)findViewById(R.id.layout_changepwd);
		layout_aboutus=(LinearLayout)findViewById(R.id.layout_aboutus);
		layout_checkversion=(LinearLayout)findViewById(R.id.layout_checkversion);
		layout_feedback=(LinearLayout)findViewById(R.id.layout_feedback);
		
		
		btn_call.setOnClickListener(this);
		
		//给电话号码添加点击事件 added by edwar 2012-08-17
		TextView tv = (TextView) findViewById(R.id.tv_phonenum);
//		tv.setBackgroundResource(R.drawable.tv_selector);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_call.setPressed(true);
				btn_call.performClick();
			}
		});
		//end
		
		layout_inf.setOnClickListener(this);
		layout_account.setOnClickListener(this);
		layout_changepwd.setOnClickListener(this);

		layout_aboutus.setOnClickListener(this);
		layout_feedback.setOnClickListener(this);
		layout_checkversion.setOnClickListener(this);
		btn_cancel_account.setOnClickListener(this);
		
		setBackgroundPicture(R.drawable.bg_wall2);
		
		layout_inf.setOnTouchListener(new MTouchListener());
		layout_account.setOnTouchListener(new MTouchListener());
		layout_changepwd.setOnTouchListener(new MTouchListener());
		
		layout_aboutus.setOnTouchListener(new MTouchListener());
		layout_feedback.setOnTouchListener(new MTouchListener());
		layout_checkversion.setOnTouchListener(new MTouchListener());
		
		//更新当前活动
		update_software_intent = new Intent(getBaseContext(), BootService.class);
	}	
		
	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);//百度统计
		// 更新当前活动
		BasicApplication.getInstance().setCurrentActivity(this);
		//MobileProbe.onResume(this);//CNZZ统计
		//added by edwar 新增未登录的情况下，隐藏“注销”按钮
				
		//modified by liyang 2012-12-27 START
		if(!isUnlogin()){
			hideLoginBtn();
			displayInfLayout();
			displayCancelButton();
			displayChangePwdLayout();
			displayAccountConfigLayout();
		}else{
			displayLoginBtn();
			hideInfLayout();
			hideCancelButton();
			hideChangePwdLayout();
			hideAccountConfigLayout();
		}		
		//modified by liyang 2012-12-27 END
	}
	
	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);//百度统计
//		MobileProbe.onPause(this);//CNZZ统计
		
		//added by liyang  2012-12-18
	}
	
	
	class MTouchListener implements
	OnTouchListener{
		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				arg0.bringToFront();
			}
			if(event.getAction()==MotionEvent.ACTION_UP){
			}
			if(event.getAction()==MotionEvent.ACTION_CANCEL){

			}
			return false;
		}	
	}
	
	@Override
	public void doClickLeftBtn() {
		onBackPressed();
	}
	
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		finish();
	}
	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()){
		    //Call 400
			case R.id.btn_call:
				callConsultant();
				break;
			case R.id.layout_inf:
				goToPersonalProfilePage();
				break;
			case R.id.layout_account:
				goToAccountConfigPage();
				break;
			//修改密码
			case R.id.layout_changepwd:
				startActivity(new Intent(
						SettingHomeActivity.this,ChangePasswordActivity.class));
				break;
			case R.id.layout_aboutus:
				startActivity(new Intent(
						SettingHomeActivity.this,AboutUsActivity.class));
				break;
			case R.id.layout_feedback:
				startActivity(new Intent(
						SettingHomeActivity.this,FeedBackActivity.class));
				break;
			//检查版本更新
			case R.id.layout_checkversion:
				BasicApplication.getInstance().setCurrentActivity(this);
				bootupSoftwareUpdateService(update_software_intent);
				break;
			//注销
			case R.id.btn_cancel_account:
				
				showmemberDialog(R.string.cancel_warning_msg, new View.OnClickListener() {		
					@Override
					public void onClick(View arg0) {
						String tgt = BasicApplication.getInstance().getMyTgt();
						myLogoutTask = new LogOutTask();
						myLogoutTask.execute(tgt);
						
						BasicApplication.getInstance().clearOnLogout();
						closeDialog();
						
						//TODO 将微博数据的SharedPreferences 置空 
						
						
						//让webview不记住当前第三方登陆用户
						CookieSyncManager.createInstance(getApplicationContext());   
						final CookieManager cookieManager = CookieManager.getInstance();  
						cookieManager.removeAllCookie();  
						
						
						//modified by edwar 2012-11-06 符合1.0版本，但是不符合1.3
//						BackStackManager.getInstance().closeAllActivity();
//						Intent intent2 = new Intent(getApplicationContext(), SplashActivity.class);
//						startActivity(intent2);
						BackStackManager.getInstance().closeAllActivityExceptOne(AppConstants.ACTIVITY_MAIN_HOMEPAGE);
						
					    //modified end
						finish();
						
					}
				});
				
				
			
				break;

		}
	}
	

	private void goToAccountConfigPage() {
		Intent intent = new Intent(getApplicationContext(),
				AccountConfigHomeActivity.class);
		startActivity(intent);
	}


	private void goToPersonalProfilePage() {
		// 设置个人信息
		Intent intent = new Intent(getApplicationContext(),
				EditProfileActivity.class);
		startActivity(intent);
	}

	//呼叫育婴顾问
	private void callConsultant() {
		final Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" +getString(R.string.parenting_adviser_phone)));
		startActivity(intent);
	}


	/**********************************************************
	 *  底部弹出菜单按钮栏
	 **********************************************************/
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "退出");// 设置图标
		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 0:
			onBackPressed();
			break;
		}
		return true;

	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//added by edwar 2012-08-31 释放Activity引用--start
		BasicApplication.getInstance().setCurrentActivity(null);
		//added by edwar 2012-08-31 释放内存引用--end
		
		if(btn_call!=null)
		btn_call.setBackgroundDrawable(null);
		if(btn_cancel_account!=null)
		btn_cancel_account.setBackgroundDrawable(null);
		
		if(layout_inf!=null){
			layout_inf.setBackgroundDrawable(null);
			layout_inf.removeAllViews();
		}
		if(layout_account!=null){
			layout_account.setBackgroundDrawable(null);
			layout_account.removeAllViews();
		}
		if(layout_changepwd!=null){
			layout_changepwd.setBackgroundDrawable(null);
			layout_changepwd.removeAllViews();
		}
		 if(layout_aboutus!=null){
		 layout_aboutus.setBackgroundDrawable(null);
		 layout_aboutus.removeAllViews();
		 }
		 if(layout_feedback!=null){
		 layout_feedback.setBackgroundDrawable(null);
		 layout_feedback.removeAllViews();
		 }
		 if(layout_checkversion!=null){
		 layout_checkversion.setBackgroundDrawable(null);
		 layout_checkversion.removeAllViews();
		 }

		
		shutdownSoftwareUpdateService(update_software_intent);
		
		if(myLogoutTask!=null&&!myLogoutTask.isCancelled()){
			myLogoutTask.cancel(true);
			myLogoutTask = null;
		}
	}
	
	//通知服务端注销
	class LogOutTask extends AsyncTask<String, Void, BaseRes>{
		@Override
		protected BaseRes doInBackground(String... params) {
			return UserProvider.getInstance(getApplicationContext()).logout(params[0]);
		}
		
	}
	
	
	
	/****************************************
         * 未登录情况下，隐藏一下“注销”按钮和“修改密码”、“账户设置”选项
         ****************************************/
	
	
	private void hideCancelButton() {
		btn_cancel_account.setVisibility(View.GONE);
	}
	
	private void displayCancelButton() {
		btn_cancel_account.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 未登陆  直接隐藏
	 * 
	 * added by liyang  2012-11-27
	 */
	private void hideChangePwdLayout() {
		layout_changepwd.setVisibility(View.GONE);
	}
	
	/**
	 * 已经登陆  显示
	 * 
	 * added by liyang  2012-11-27
	 */
	private void displayChangePwdLayout() {
		layout_changepwd.setVisibility(View.VISIBLE);
	}
	
	
	/**
	 * 未登陆  直接隐藏
	 * 
	 * added by liyang  2012-11-27
	 */
	private void hideAccountConfigLayout() {
		layout_account.setVisibility(View.GONE);
	}
	
	
	/**
	 * 已经登陆  显示
	 * 
	 * added by liyang  2012-11-27
	 */
	private void displayAccountConfigLayout() {
		layout_account.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 未登陆  将layout_inf的背景重置为R.drawable.setting_tab_selector
	 * 
	 * added by liyang  2012-11-27
	 */
	private void hideInfLayout() {
		layout_inf.setBackgroundResource(R.drawable.setting_tab_selector);
		layout_inf.setVisibility(View.GONE);
	}
	
	/**
	 * 已经登陆  将layout_inf的背景重置为R.drawable.setting_toptab_selector
	 * added by liyang  2012-11-27
	 */
	private void displayInfLayout() {
		layout_inf.setBackgroundResource(R.drawable.setting_toptab_selector);
		layout_inf.setVisibility(View.VISIBLE);
	}
	
	
	/**
	 * 隐藏登陆按钮
	 * 
	 * added by liyang  2012-11-27
	 */
	private void hideLoginBtn(){
		findViewById(R.id.loginBtn).setVisibility(View.GONE);
	}
	
	/**
	 * 显示登陆按钮
	 * 
	 * added by liyang  2012-11-27
	 */
	private void displayLoginBtn(){
		findViewById(R.id.loginBtn).setVisibility(View.VISIBLE);
		findViewById(R.id.loginBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_SETTING_PAGE);
			}
		});
	}
}
