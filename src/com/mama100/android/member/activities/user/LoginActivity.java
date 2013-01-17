/**
 * 
 */
package com.mama100.android.member.activities.user;

import java.sql.Timestamp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTaskWithMessage;
import com.mama100.android.member.activities.ThirdPartyLoginActivity;
import com.mama100.android.member.bean.ThirdPartyUser;
import com.mama100.android.member.bean.thirdparty.CommonBean;
import com.mama100.android.member.bean.thirdparty.QQLoginBean;
import com.mama100.android.member.bean.thirdparty.SinaWeiboBean;
import com.mama100.android.member.bean.thirdparty.TencentWeiboBean;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.sso.LoginReq;
import com.mama100.android.member.domain.user.LoginByThirdPartyUserReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BackStackManager;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;

/**
 * <p>
 * Description: LoginActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 用户登录界面
 */
public class LoginActivity extends ThirdPartyLoginActivity {

	EditText password;
	EditText account;
	CheckBox isShow;

	/***********************************************************
	 * 下面的三种登录入口
	 ***********************************************************/

	LinearLayout qq_account_login;
	LinearLayout sina_weibo_account_login;
	LinearLayout member_phone_login;

	protected static final String XWEIBO_CONFIG_FILENAME = "xweiboConfig";

	private CustomAsyncTask task;
	
	private boolean  isThirdPartyLogin = false;//标签，用于判断是否第三方登录
	
	
	private boolean isLogining = false; //避免登录弹出等待窗口时，被点返回而关闭窗口
	private boolean isAccessing = false; //避免访问服务器前几秒间歇，用户点击界面，触发不该触发的事件

	/*
	 * <p> onCreate </p>
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.login);
		setupViews();
		setupTopbarLabels();
		setBackgroundPicture(R.drawable.bg_wall);
		BasicApplication.getInstance().setToExit(false);
		
		//commented by edwar 2012-12-05 start, 不用这种方法关闭Activity
		// 将该页面放进栈里
//				BackStackManager.getInstance().putActivity(
//						AppConstants.ACTIVITY_LOGIN_HOMEPAGE, this);
				//commented by edwar 2012-12-05 end
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);// 百度统计
		// MobileProbe.onResume(this);//CNZZ统计
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);// 百度统计
		// MobileProbe.onPause(this);//CNZZ统计
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (task != null && task.isCancelled() == false) {
			task.cancel(true);
		}
	}

	private void setupTopbarLabels() {
		setTopBarVisibility(View.VISIBLE);
		setTopLabel(R.string.please_login_label);
		setLeftButtonImage(R.drawable.selector_cancel);
		setRightButtonImage(R.drawable.selector_register);
	}

	/**
	 * <p>
	 * 
	 * @Title: setupViews
	 * @param:
	 * @Description:
	 * @return </p>
	 */
	private void setupViews() {

		qq_account_login = (LinearLayout) findViewById(R.id.qq_account_login);
		sina_weibo_account_login = (LinearLayout) findViewById(R.id.sina_weibo_account_login);
		member_phone_login = (LinearLayout) findViewById(R.id.member_phone_login);

		qq_account_login.setOnClickListener(this);
		sina_weibo_account_login.setOnClickListener(this);
		member_phone_login.setOnClickListener(this);

		qq_account_login.setOnTouchListener(new MTouchListener());
		sina_weibo_account_login.setOnTouchListener(new MTouchListener());
		member_phone_login.setOnTouchListener(new MTouchListener());

		TextView lookfor_pwd;

		lookfor_pwd = (TextView) findViewById(R.id.lookfor_pwd);
		lookfor_pwd.setBackgroundResource(R.drawable.tv_selector);
		lookfor_pwd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						GetPasswordActivity.class));
			}
		});

		account = (EditText) findViewById(R.id.login_account);
		password = (EditText) findViewById(R.id.login_password);
		isShow = (CheckBox) findViewById(R.id.isShow);
		password.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		isShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

				} else {
					password.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

				}

				Editable etable = password.getText();
				// 移动光标至最后一位
				Selection.setSelection(etable, etable.length());
			}
		});
	}

	/**********************************
	 * 继承父类
	 **********************************/
	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		if (isLogining||isAccessing) {
			return;
		}
		finish();
		
		//commented by edwar 2012-12-05 start, 不用这种方法关闭Activity
//		BackStackManager.getInstance().removeActivity(AppConstants.ACTIVITY_LOGIN_HOMEPAGE);
		//commented by edwar 2012-12-05 end
		
	}

	@Override
	public void doClickRightBtn() {
		super.doClickRightBtn();
		if (isLogining||isAccessing) {
			return;
		}
		goToRegisterPage();
	}

	private void goToRegisterPage() {
		
		//modified by edwar , 2012-12-05, 实现多级activity传递
//		startActivity(new Intent(getApplicationContext(),
//				RegisterActivity.class));
		startActivityForResult(new Intent(getApplicationContext(),
				RegisterActivity.class), BasicApplication.getInstance().getRequestCode());
		//modified by edwar , 2012-12-05,end
	}

	/*
	 * <p> onBackPressed </p>
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	
		doClickLeftBtn();
	}

	private void login() {
		
		if (isLogining||isAccessing) {
			return;
		}
		
		// 验证用户名
		if (!verifyInput(account, AppConstants.CHECK_USERNAME)) {
			return;
		}
		// 验证密码
		 if (!verifyInput(password,AppConstants.CHECK_PASSWORD)) {
			 return;
		 }

		// TODO 2直接连接SSO服务器，单点登录
		
		hideSoftWindowInput();
		LoginReq request = new LoginReq();
		request.setRememberMe("true");
		request.setUsername(account.getText().toString());
		request.setPassword(password.getText().toString());

		task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message2);
		task.execute(request);
		isLogining = true;
	}

	class CustomAsyncTask extends AsyncReqTaskWithMessage {
		

		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			
			//1，登录界面最上方的登录
			if (request instanceof LoginReq) {
				isThirdPartyLogin = false;
				return UserProvider.getInstance(getApplicationContext())
						.firstLogin((LoginReq) request);
			} 
			//2，登录界面第三方的登录
			else if (request instanceof LoginByThirdPartyUserReq) {
				LogUtils.loge("edwar", "go to login third parter");
				isThirdPartyLogin = true;
				
				return UserProvider.getInstance(getApplicationContext())
						.loginByThirdParty((LoginByThirdPartyUserReq) request);
			}
			else {
				return null;
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			isLogining = false;
			isAccessing =false;
			closeProgressDialog();
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				makeText(response.getDesc());
				return;
			}
			if(isThirdPartyLogin){
				BasicApplication.getInstance().setLoginFromThirdParty(true);
			}else{
				BasicApplication.getInstance().setLoginFromThirdParty(false);
			}
			
			LogUtils.loge(AppConstants.TIME_CONSUME, "time - access mama100" + new Timestamp(System.currentTimeMillis()));
			
			loadApplicationData();
			
		}

	}

	// 点击登录按钮响应事件
	public void clicklogin(View v) {
		login();
	}

	/******************************************************
	 * added by edwar,为1.2版的多种注册登录增加的方法--START 2012-09-25
	 ******************************************************/
	public void loginSinaWeibo(View v) {
		author2Method(SinaWeiboBean.REQ_AUTHOR);
	}

	public void loginQQ(View v) {
		author2Method(QQLoginBean.REQ_AUTHOR);
	}

	public void loginCRMphone(View v) {
		Intent intent = new Intent(getApplicationContext(),
				LoginCRMActivity.class);
		//modified by edwar 2012-12-05, 实现多级传递 START
//		startActivityForResult(intent,
//				AppConstants.REQUEST_CODE_UNLOGIN_INTO_MESSAGE_PAGE);
		startActivityForResult(intent,BasicApplication.getInstance().getRequestCode());
		//modified by edwar 2012-12-05, 实现多级传递 END
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 新浪微博用户授权回调处理
		if (resultCode == CommonBean.RES_SUCCESS) {
			LoginByThirdPartyUserReq request = new LoginByThirdPartyUserReq();
			if (requestCode == SinaWeiboBean.REQ_AUTHOR) {
				SinaWeiboBean	bean = getBean_SinaWeibo();
				request.setUserType(ThirdPartyUser.type_sina);

				LogUtils.loge(TAG,
						"1 - " + new Timestamp(System.currentTimeMillis()));
				request.setUid(bean.getUid());
				LogUtils.loge(TAG,
						"1.5 - bean getUid value =  " + bean.getUid());
				LogUtils.loge(TAG,
						"2 - " + new Timestamp(System.currentTimeMillis()));
				request.setAccess_token(bean.getAccessToken());
				request.setToken_expire_date(bean.getExpiresIn());

				task = new CustomAsyncTask(this);
				task.displayProgressDialog(R.string.doing_req_message2);
				task.execute(request);
				isAccessing = true;
			}
		} else 
			//多级传递，
			//目前用于 1，从LoginCRM界面返回  2，从个人信息界面返回
			if (
					
					//modified by edwar 2012-12-05  不必要一一写出requestCode
//					(
//					 requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_COMPLETE_ADDRESS_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_BIND_SINA_WEIBO_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_FOLLOW_MAMA100_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_FEEDBACK_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_HISTORY_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_MEMBER_CARD_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_YOURSELF_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_MESSAGE_DETAIL_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_MESSAGE_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_TAKE_PHOTO_SHARE_PAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_HOMEPAGE
//					|requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_CONCERNED_BABYSHOP_PAGE
//					)
					
					//modified by edwar 2012-12-05  不必要一一写出requestCode
					requestCode == BasicApplication.getInstance().getRequestCode()
					//modified by edwar 2012-12-05 end
				&& resultCode == RESULT_OK) {
			setResult(RESULT_OK, data);
			finish();
			//commented by edwar 2012-12-05 start, 不用这种方法关闭Activity
//			BackStackManager.getInstance().removeActivity(AppConstants.ACTIVITY_LOGIN_HOMEPAGE);
			//commented by edwar 2012-12-05 end
		}
	}

	@Override
	protected void updateFollowMaMa100CheckBoxStatus(boolean b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void shareWeiboComplete(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doSSoLogin(boolean b, int requestCode) {
		LogUtils.loge(AppConstants.TIME_CONSUME, "time - qq back" + new Timestamp(System.currentTimeMillis()));
		LoginByThirdPartyUserReq request = new LoginByThirdPartyUserReq();
		if (requestCode == QQLoginBean.REQ_AUTHOR) {
			QQLoginBean	bean = getBean_qqlogin();
			request.setUserType(ThirdPartyUser.type_qq);
			request.setUid(bean.getUid());
			request.setAccess_token(bean.getAccessToken());
			request.setToken_expire_date(bean.getExpiresIn());

		} else if (requestCode == TencentWeiboBean.REQ_AUTHOR) {
			TencentWeiboBean bean = getBean_tencentWeibo();
			request.setUserType(ThirdPartyUser.type_qqweibo);
			request.setUid(bean.getUid());
			request.setAccess_token(bean.getAccessToken());
			request.setToken_expire_date(bean.getExpiresIn());
		}

		task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message2);
		task.execute(request);
		isAccessing = true;
	}

	/******************************************************
	 * added by edwar,为1.2版的多种注册登录增加的方法--END 2012-09-25
	 ******************************************************/

	/********************************************************
	 * 列表状态改变的效果
	 ********************************************************/
	class MTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				arg0.bringToFront();
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
			}
			if (event.getAction() == MotionEvent.ACTION_CANCEL) {

			}
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(isLogining||isAccessing){
			return;
		}
		switch (v.getId()) {
		case R.id.qq_account_login:
			
			LogUtils.loge(AppConstants.TIME_CONSUME, "time - click qq" + new Timestamp(System.currentTimeMillis()));
			
			loginQQ(v);
			break;
		case R.id.sina_weibo_account_login:
			loginSinaWeibo(v);
			break;
		case R.id.member_phone_login:
			loginCRMphone(v);
			break;

		default:
			break;
		}

	}
}
