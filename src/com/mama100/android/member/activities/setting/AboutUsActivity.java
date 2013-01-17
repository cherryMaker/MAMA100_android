/**
 * 
 */
package com.mama100.android.member.activities.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.ThirdPartyLoginActivity;
import com.mama100.android.member.activities.ThirdPartyWebViewActivity;
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
import com.mama100.android.member.outwardWeibo.XWeibo;
import com.mama100.android.member.util.LogUtils;

/**
 * <p>
 * Description: AboutUsActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 关于我们
 */
public class AboutUsActivity extends ThirdPartyLoginActivity {

	private View layout_followus;
	private CheckBox cbox_isfollow;

	/*******************************************************
	 * 与微博相关的变量
	 ******************************************************/
	private SinaWeiboBean bean_sinaWeibo;
	
	private CustomAsyncTask task; //绑定微博时， 先发送请求到mama100服务器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onCreate");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.about_us);
		bean_sinaWeibo = getBean_SinaWeibo();

		// 3, add the info Text edwar 2012-08-26
		TextView versionName = (TextView) this.findViewById(R.id.tv_version);
		String info = "Android "
				+ BasicApplication.getInstance().getLocalVersionName();
		versionName.setText(info);

		// setImagePicture();
		layout_followus = findViewById(R.id.layout_followus);
		cbox_isfollow = (CheckBox) findViewById(R.id.cbox_isfollow);

		
		if(!isUnlogin()){
			
		if (SinaWeiboBean.isFollowMama100())
			cbox_isfollow.setChecked(true);
		else
			cbox_isfollow.setVisibility(View.GONE); //未关注mama100,不显示

		// 未绑定微博,不显示cbox_isfollow
		if (!bean_sinaWeibo.isAccessTokenValid()) {
			cbox_isfollow.setVisibility(View.GONE);
			layout_followus.setPadding(0, 12, 0, 12);
		}
		}

		layout_followus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isUnlogin()){
					goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_FOLLOW_MAMA100_PAGE);
					return;
				}
				
				
				if (cbox_isfollow.isChecked()) {
					// 未绑定微博,但已关注mama100
					if (!bean_sinaWeibo.isAccessTokenValid()) {
						Toast.makeText(AboutUsActivity.this, "请绑定微博后，再操作",
								Toast.LENGTH_SHORT).show();
						author2Method(SinaWeiboBean.REQ_AUTHOR);
						// final Intent intent=new Intent(AboutUsActivity.this,
						// WebViewActivity.class)
						// .putExtra(WebViewActivity.KEY_URL,
						// XWeibo.URL_VIEW_M_USER_WEIBO +
						// "/"+AppConstants.XWEIBO_UID);
						// startActivity(intent);
						return;
					}

					final Intent intent = new Intent(getApplicationContext(),
							ThirdPartyWebViewActivity.class)
							.putExtra(
									ThirdPartyWebViewActivity.KEY_URL,
									XWeibo.URL_VIEW_M_USER_WEIBO + "/"
											+ AppConstants.XWEIBO_MAMA100_UID)
							.putExtra(ThirdPartyWebViewActivity.KEY_ACTION,
									ThirdPartyWebViewActivity.REQ_MAMA100_WEIBO);
					startActivity(intent);
				} else {
					// 未绑定微博，也未关注mama100
					if (!bean_sinaWeibo.isAccessTokenValid()) {
						Toast.makeText(AboutUsActivity.this, "请绑定微博后，再操作",
								Toast.LENGTH_LONG).show();
						author2Method(SinaWeiboBean.REQ_AUTHOR);
						return;
					}
					XWeibo.getInstance().followAUser(
							AppConstants.XWEIBO_MAMA100_UID,
							new WeiboFollowLis());
				}

			}
		});

		setLeftButtonImage(R.drawable.selector_back);
		setTopLabel(getString(R.string.about_us));
		setBackgroundPicture(R.drawable.bg_wall);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (task != null && task.isCancelled() == false) {
			task.cancel(true);
			task = null;
		}
	}

	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	
		
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_FOLLOW_MAMA100_PAGE:
				setUnlogin(false);
				BasicApplication.getInstance().setAutoLogin(true);
				//再次调用之前的点击事件。。
				layout_followus.performClick();
				break;

			default:
				break;
			}
			
		}
		
		
		// 这里沿用第三方登录的请求对象，其实这里不是第三方登录，而是向服务器提交绑定或解绑定请求
				LoginByThirdPartyUserReq request = new LoginByThirdPartyUserReq();
				// 新浪微博用户授权回调处理
				if (resultCode == CommonBean.RES_SUCCESS) {
					if (requestCode == SinaWeiboBean.REQ_AUTHOR) {

							// 更新bean
							updateBeanContent();
//							setSinaWeiboBindStatus(true);

							SinaWeiboBean bean = getBean_SinaWeibo();
							request.setUserType(ThirdPartyUser.type_sina);
							request.setUid(bean.getUid());
							request.setAccess_token(bean.getAccessToken());
							request.setToken_expire_date(bean.getExpiresIn());

							task = new CustomAsyncTask(this);
							task.displayProgressDialog(R.string.doing_req_message);
							task.execute(request);
			
			} else if (requestCode == QQLoginBean.REQ_AUTHOR) {

			}

			else if (requestCode == TencentWeiboBean.REQ_AUTHOR) {

			}

		}

	}

	
	//实现父类的抽象方法，目的，更新checkbox状态
	@Override
	protected void updateFollowMaMa100CheckBoxStatus(boolean b) {
		if (cbox_isfollow != null) {
			if (b) {
				cbox_isfollow.setChecked(true);
				cbox_isfollow.setVisibility(View.VISIBLE);
			} else {
				cbox_isfollow.setVisibility(View.INVISIBLE); //未关注，不显示
			}
		}
	}

	@Override
	protected void shareWeiboComplete(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doSSoLogin(boolean b, int requestCode) {
		// TODO Auto-generated method stub
		
	}
	
	

	protected void updateBeanContent() {
		bean_sinaWeibo = getBean_SinaWeibo();
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
				return UserProvider.getInstance(getApplicationContext())
						.bindAction((LoginByThirdPartyUserReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {
			
			//TODO  response.getCode = 101 , 已经被别人绑定，就不能绑定
			closeProgressDialog();
			makeText(response.getDesc());
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				if(response.getCode().equals(DeviceResponseCode.WEIBO_IS_BOUNDED_BY_OTHER))
					//让webview不记住当前登陆用户
					CookieSyncManager.createInstance(getApplicationContext());   
					CookieManager cookieManager = CookieManager.getInstance();  
					cookieManager.removeAllCookie();  
					
					//如果发现微博已经被别人绑过，这样用户就不能绑定该微博
					bean_sinaWeibo.unbindUserToDeleteValueInSharedPreference();
					updateBeanContent();
			}else{
				//绑定成功
				XWeibo.getInstance().followAUser(
						AppConstants.XWEIBO_MAMA100_UID, new WeiboFollowLis());
			}
		}
	}
	
}
