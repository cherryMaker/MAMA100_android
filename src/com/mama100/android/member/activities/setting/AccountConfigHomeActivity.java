/**
 * 
 */
package com.mama100.android.member.activities.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.ThirdPartyLoginActivity;
import com.mama100.android.member.activities.user.CompleteRecevierAddressActivity;
import com.mama100.android.member.bean.ThirdPartyUser;
import com.mama100.android.member.bean.thirdparty.CommonBean;
import com.mama100.android.member.bean.thirdparty.QQLoginBean;
import com.mama100.android.member.bean.thirdparty.SinaWeiboBean;
import com.mama100.android.member.bean.thirdparty.TencentWeiboBean;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.LoginByThirdPartyUserReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;

/**
 * <p>
 * Description: AccountConfigHomeActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-10-16 帐号设置主界面
 * 
 * modified by liyang 2012-10-27 流程修改（优化）
 * 将管理收货地址移到个人信息页面下
 */
public class AccountConfigHomeActivity extends ThirdPartyLoginActivity
		implements View.OnClickListener {

	LinearLayout layout_sina_weibo_bind_status; // 新浪微博绑定状态框
	
	//注释by liyang 2012-11-27
	//注释原因：该功能已经移到个人信息页面
	//LinearLayout layout_manage_address;// 管理收货地址框
	TextView tv_authstate_sina;// 显示绑定状态

	/*******************************************************
	 * 与微博相关的变量
	 ******************************************************/
	private SinaWeiboBean bean_sinaWeibo;
	private CustomAsyncTask task;
	private boolean isBinding = false; // 判断当前是绑定吗？
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onCreate");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.account_config_home);
		setupTopBar();
		setupViews();
		setBackgroundPicture(R.drawable.bg_wall2);
		// 更新当前活动
		BasicApplication.getInstance().setCurrentActivity(this);

		/****************************************
		 * 第三方绑定和解绑定涉及到的方法
		 ****************************************/
		initialThirdPartyWidgetStatus();

	}

	/**
	 * 初始化第三方的绑定状态
	 */
	private void initialThirdPartyWidgetStatus() {
		if (isUnlogin()) {
			return;
		}
		initalSinaWeiboWidgetStatus();
	}

	// 1,新浪微博
	private void initalSinaWeiboWidgetStatus() {
		bean_sinaWeibo = getBean_SinaWeibo();
		if (bean_sinaWeibo.isAccessTokenValid()) {
			setSinaWeiboBindStatus(true);
		} else {
			setSinaWeiboBindStatus(false);
		}
	}

	/**
	 * @param b
	 *            是否已经绑定，true,是 ， 从而显示“auth_alive - 点击解绑”等
	 */
	private void setSinaWeiboBindStatus(boolean b) {
		if (tv_authstate_sina == null) {
			tv_authstate_sina = (TextView) findViewById(R.id.tv_authstate_sina);
		}

		if (b) {
			tv_authstate_sina.setText(R.string.auth_alive);
		} else {
			tv_authstate_sina.setText(R.string.make_auth); //点击绑定
		}
	}

	private void setupViews() {
		layout_sina_weibo_bind_status = (LinearLayout) findViewById(R.id.layout_sina_weibo_bind_status);
		
		//注释by liyang 2012-11-27
		//注释原因：该功能已经移到个人信息页面
		//layout_manage_address = (LinearLayout) findViewById(R.id.layout_manage_address);

		layout_sina_weibo_bind_status.setOnClickListener(this);
		
		//注释by liyang 2012-11-27
		//注释原因：该功能已经移到个人信息页面
		//layout_manage_address.setOnClickListener(this);
	}

	private void setupTopBar() {
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonVisibility(View.INVISIBLE);
		setTopLabel(getString(R.string.account_config));
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);// 百度统计
		// MobileProbe.onResume(this);//CNZZ统计
		//如果之前的状态是未登录，而今成为已登录，则要刷新页面
		
		if (isUnlogin()) {
			return;
		}
		
		if(isPreviousUnLoginStatus()){
			resetPageValues();
		}
		
		//注释by liyang 2012-11-27
		//注释原因：该功能已经移到个人信息页面
		/****************************************
         * added by edwar 2012-10-30
         * 如果未关联，则不显示"完善收货地址" 这个框。
         * START
         ****************************************/
		//if(!isAsso()){
		//	layout_manage_address.setVisibility(View.GONE);
		//}else{
		//	layout_manage_address.setVisibility(View.VISIBLE);
		//}
		/****************END************************/
	}

	
	
	private void resetPageValues() {
		initialThirdPartyWidgetStatus();
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);// 百度统计
		// MobileProbe.onPause(this);//CNZZ统计
	}

	@Override
	public void doClickLeftBtn() {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		// Call 400
		case R.id.layout_sina_weibo_bind_status:

			if (isUnlogin()) {
				goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_BIND_SINA_WEIBO_PAGE);
				return;
			}

			// 绑定
			//TODO根据我们服务器，确定是否能绑定
			if (bean_sinaWeibo!=null&&!bean_sinaWeibo.isAccessTokenValid()) {
				isBinding = true;
				author2Method(SinaWeiboBean.REQ_AUTHOR);
			} else {
				// 解绑
				showmemberDialog(R.string.cancel_auth_tip, 0, new View.OnClickListener() {
//					showmemberDialog(R.string.cancel_auth_tip, heightPixels / 2
//							- heightPixels * 2 / 7, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						bean_sinaWeibo
								.unbindUserToDeleteValueInSharedPreference();
						updateBeanContent();
						closeDialog();
						makeText(R.string.auth_canceled);

						isBinding = false;
						
						LoginByThirdPartyUserReq request = new LoginByThirdPartyUserReq();
						SinaWeiboBean bean = getBean_SinaWeibo();
						request.setUserType(ThirdPartyUser.type_sina);
						task = new CustomAsyncTask(
								AccountConfigHomeActivity.this);
						task.displayProgressDialog(R.string.doing_req_message);
						task.execute(request);
						
						//让webview不记住当前第三方登陆用户
						CookieSyncManager.createInstance(getApplicationContext());   
						final CookieManager cookieManager = CookieManager.getInstance();  
						cookieManager.removeAllCookie();  
					}
				});
			}

			break;
			// 注释by liyang 2012-11-27
			// 注释原因：该功能已经移到个人信息页面
			// case R.id.layout_manage_address:
			//goToManageReceiverAddressPage();
			// break;
		}
	}

	
	// 注释by liyang 2012-11-27
	// 注释原因：该功能已经移到个人信息页面
	/**private void goToManageReceiverAddressPage() {
		// 设置收货地址
		Intent intent = new Intent(getApplicationContext(),
				CompleteRecevierAddressActivity.class);
		startActivity(intent);
	}*/

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// added by edwar 2012-08-31 释放Activity引用--start
		BasicApplication.getInstance().setCurrentActivity(null);
		// added by edwar 2012-08-31 释放内存引用--end
		//注释by liyang 2012-11-27
		//注释原因：该功能已经移到个人信息页面
		//if (layout_manage_address != null) {
		//	layout_manage_address.setBackgroundDrawable(null);
		//	layout_manage_address.removeAllViews();
		//}
		
		if (layout_sina_weibo_bind_status != null) {
			layout_sina_weibo_bind_status.setBackgroundDrawable(null);
			layout_sina_weibo_bind_status.removeAllViews();
		}
		
		
		if (task != null && task.isCancelled() == false) {
			task.cancel(true);
			task = null;
		}

	}

	/**
	 * 具体的第三方的保存设置操作是在 父类ThirdPartyLoginActivity.OnActivityResult()里面执行，
	 * 该子类只负责简单的UI显示
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == CommonBean.RES_SUCCESS) {
			// 这里沿用第三方登录的请求对象，其实这里不是第三方登录，而是向服务器提交绑定或解绑定请求
			LoginByThirdPartyUserReq request = new LoginByThirdPartyUserReq();
			if (requestCode == SinaWeiboBean.REQ_AUTHOR) {

				// 更新bean
				updateBeanContent();
//				setSinaWeiboBindStatus(true);

				SinaWeiboBean bean = getBean_SinaWeibo();
				request.setUserType(ThirdPartyUser.type_sina);
				request.setUid(bean.getUid());
				request.setAccess_token(bean.getAccessToken());
				request.setToken_expire_date(bean.getExpiresIn());

				task = new CustomAsyncTask(this);
				task.displayProgressDialog(R.string.doing_req_message);
				task.execute(request);
			}

			else
			// 2 . QQ用户登录授权回调处理
			if (requestCode == QQLoginBean.REQ_AUTHOR) {
			} else

			// 2 . 腾讯微博用户授权回调处理
			if (requestCode == TencentWeiboBean.REQ_AUTHOR) {
			}

		}
		// 点击了网页上的"取消"
		else if ((requestCode == SinaWeiboBean.REQ_AUTHOR
				|| requestCode == QQLoginBean.REQ_AUTHOR || requestCode == TencentWeiboBean.REQ_AUTHOR)
				&& resultCode == CommonBean.RES_REFUSE) {
		}
		// 其它原因导致授权失败
		else if ((requestCode == SinaWeiboBean.REQ_AUTHOR
				|| requestCode == QQLoginBean.REQ_AUTHOR || requestCode == TencentWeiboBean.REQ_AUTHOR)
				&& resultCode == CommonBean.RES_FAILED) {
		}

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_BIND_SINA_WEIBO_PAGE:
				setUnlogin(false);
				BasicApplication.getInstance().setAutoLogin(true);
				// 再次调用之前的点击事件。。
				layout_sina_weibo_bind_status.performClick();
				break;

			default:
				break;
			}

		}

	}

	protected void updateBeanContent() {
		bean_sinaWeibo = getBean_SinaWeibo();
	}

	/****************************************
	 * 继承父类实现的接口
	 ****************************************/

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
		// TODO Auto-generated method stub

	}

	/****************************************
	 * 绑定或者解绑 用到的 方法
	 ****************************************/

	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			if (isBinding) {
				return UserProvider.getInstance(getApplicationContext())
						.bindAction((LoginByThirdPartyUserReq) request);
			} else if (!isBinding) {
				return UserProvider.getInstance(getApplicationContext())
						.unbindAction((LoginByThirdPartyUserReq) request);
			} else {
				return null;
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			
			//TODO  response.getCode = 101 , 已经被别人绑定，就不能绑定
			closeProgressDialog();
			makeText(response.getDesc());
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				if(response.getCode().equals(DeviceResponseCode.WEIBO_IS_BOUNDED_BY_OTHER))
					//如果发现微博已经被别人绑过，这样用户就不能绑定该微博
				if (isBinding) {
					//让webview不记住当前登陆用户
					CookieSyncManager.createInstance(getApplicationContext());   
					CookieManager cookieManager = CookieManager.getInstance();  
					cookieManager.removeAllCookie();  
					
					bean_sinaWeibo.unbindUserToDeleteValueInSharedPreference();
					updateBeanContent();
					isBinding = false;
				}
				return;
			}else{
				if (isBinding) {
					setSinaWeiboBindStatus(true);
				} else if (!isBinding) {
					setSinaWeiboBindStatus(false);
				}
				
			}
		}
	}
}
