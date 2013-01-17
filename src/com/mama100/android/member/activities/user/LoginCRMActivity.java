/**
 * 
 */
package com.mama100.android.member.activities.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ViewAnimator;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.sso.LoginReq;
import com.mama100.android.member.domain.user.CrmMemberLoginReq;
import com.mama100.android.member.domain.user.CrmMemberLoginRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BackStackManager;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.widget.EditTextEx;

/**
 * <p>
 * Description: LoginCRMActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-09-28
 */
public class LoginCRMActivity extends BaseActivity {
	private CustomAsyncTask task;
	// 激活会员卡 步骤
	private int step = 0;

	/****************************
	 * 第一页面变量
	 ****************************/
	EditTextEx input_cell_phone; // 手机号
	Button submit_cell_phone; // 提交手机号

	/****************************
	 * 第二页面变量
	 ****************************/
	EditTextEx input_verify_code;// 验证码
	Button submit_verify_code;// 提交验证码
	private boolean isLogining = false; // 避免登录弹出等待窗口时，被点返回而关闭窗口

	/****************************
	 * 第三页面变量
	 ****************************/
	EditTextEx input_new_pwd1; // 输入新密码1
	EditTextEx input_new_pwd2; // 输入新密码2
	Button submit_pwd;// 提交新密码
	CheckBox cBox_showpwd; // 是否显示 密码

	/*****************************
	 * 通用
	 *****************************/
	// 切换标签时的动画
	private ViewAnimator view_animator;
	public Animation slideInLeft;
	public Animation slideInRight;
	public Animation slideOutLeft;
	public Animation slideOutRight;

	private final int PAGE_ONE = 0;// 第一页
	private final int PAGE_TWO = 1;// 第二页
	private final int PAGE_THREE = 2;// 第三页
	private int current = PAGE_ONE; // 当前页，默认第一页

	String phone = "";
	String code = "";

	public boolean isIntoFirstLogin = false; // 判断响应是哪个请求返回的

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onCreate");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.login_crm2);
		setupViews();
		setupCommonTopBar();
		setupTopBar();
		setBackgroundPicture(R.drawable.bg_wall);
		initializeAnimation(this);// 初始化switchButton栏的切换动画效果
		// 将该页面放进栈里
		BackStackManager.getInstance().putActivity(
				AppConstants.ACTIVITY_CRM_LOGIN_HOMEPAGE, this);
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

	private void setupViews() {
		/************************************************
		 * 第一页
		 ************************************************/
		input_cell_phone = (EditTextEx) findViewById(R.id.input_cell_phone);
		input_cell_phone.getEditText().setPadding(10, 0, 0, 0);
		input_cell_phone.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);

		((Button) findViewById(R.id.send_cell_phone))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hideSoftWindowInput();
						step = 1;
						// 验证用户名
						if (!verifyInput(input_cell_phone.getEditText(),
								AppConstants.CHECK_MOBILE)) {
							return;
						}

						phone = input_cell_phone.getEditTextStr();
						CrmMemberLoginReq request = new CrmMemberLoginReq();
						request.setMobile(phone);
						request.setStep(CrmMemberLoginReq.STEP_ONE);

						task = new CustomAsyncTask(LoginCRMActivity.this);
						task.displayProgressDialog(R.string.doing_req_message);
						task.execute(request);

					}
				});

		/************************************************
		 * 第二页
		 ************************************************/
		input_verify_code = (EditTextEx) findViewById(R.id.input_vcode);
		input_verify_code.getEditText().setInputType(
				InputType.TYPE_CLASS_NUMBER);
		// tv_label = (TextView) findViewById(R.id.pagetwo_tv_label);

		((Button) findViewById(R.id.send_vcode))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hideSoftWindowInput();

						step = 2;
						code = input_verify_code.getEditTextStr();
						CrmMemberLoginReq request = new CrmMemberLoginReq();
						request.setMobile(phone);
						request.setValidateCode(code);
						request.setStep(CrmMemberLoginReq.STEP_TWO);

						task = new CustomAsyncTask(LoginCRMActivity.this);
						task.displayProgressDialog(R.string.doing_req_message);
						task.execute(request);
						isLogining = true;

					}
				});

		/****************************************************
		 * 第三页
		 ********************************************************/

		input_new_pwd1 = (EditTextEx) findViewById(R.id.input_new_pwd1);
		input_new_pwd1.getEditText().setInputType(
				InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		input_new_pwd2 = (EditTextEx) findViewById(R.id.input_new_pwd2);
		input_new_pwd2.getEditText().setInputType(
				InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		((Button) findViewById(R.id.send_new_pwd))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						hideSoftWindowInput();

						// 验证密码
						if (input_new_pwd1.getEditTextStr() == null
								|| input_new_pwd1.getEditTextStr().length() < 6) {
							input_new_pwd1.getEditText().setError(
									getString(R.string.pwd_lenless_error));
							input_new_pwd1.requestFocus();
							return;
						}
						if (input_new_pwd2.getEditTextStr() == null
								|| input_new_pwd2.getEditTextStr().length() < 6) {
							input_new_pwd2.getEditText().setError(
									getString(R.string.pwd_lenless_error));
							input_new_pwd2.requestFocus();
							return;
						}

						if (input_new_pwd2.getEditTextStr() != null
								&& !input_new_pwd2.getEditTextStr().equals(
										input_new_pwd1.getEditTextStr())) {
							input_new_pwd2.getEditText().setError(
									getString(R.string.two_pwd_unlike));
							input_new_pwd2.requestFocus();
							return;
						}

						step = 3;
						String pwd = input_new_pwd2.getEditTextStr();

						CrmMemberLoginReq request = new CrmMemberLoginReq();
						request.setMobile(phone);
						request.setValidateCode(code);
						request.setPwd(pwd);
						request.setStep(CrmMemberLoginReq.STEP_THREE);

						task = new CustomAsyncTask(LoginCRMActivity.this);
						task.displayProgressDialog(R.string.doing_req_message);
						task.execute(request);
						isLogining = true;

					}
				});
		cBox_showpwd = (CheckBox) findViewById(R.id.check_box_show_pwd);
		cBox_showpwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					input_new_pwd1.getEditText().setInputType(
							InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					input_new_pwd2.getEditText().setInputType(
							InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				} else {
					input_new_pwd1.getEditText().setInputType(
							InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					input_new_pwd2.getEditText().setInputType(
							InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);

				}
				Editable etable = null;
				if (input_new_pwd1.getEditText().isFocusable())
					etable = input_new_pwd1.getEditText().getText();
				else if (input_new_pwd2.getEditText().isFocusable())
					etable = input_new_pwd2.getEditText().getText();
				// 移动光标至最后一位
				Selection.setSelection(etable, etable.length());
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (input_cell_phone != null) {
			input_cell_phone = null;
		}
		if (input_verify_code != null) {
			input_verify_code = null;
		}
		if (input_new_pwd1 != null) {
			input_new_pwd1 = null;
		}

		if (input_new_pwd2 != null) {
			input_new_pwd2 = null;
		}

		if (task != null) {
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
		if (isLogining) {
			return;
		}

		switch (current) {
		case PAGE_ONE:
			if (StringUtils.isBlank(input_cell_phone.getEditTextStr())) {
				finish();
				return;
			}
			showmemberDialog(R.string.login_crm_act_tv_exit_tips3,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							closeDialog();
							finish();
						}
					});
			break;
		case PAGE_TWO:
			setupTopBar();
			showSpecificPage(PAGE_ONE);
			break;

		case PAGE_THREE:
			setupTopBar2();
			showSpecificPage(PAGE_TWO);
			break;

		default:
			break;
		}
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		
//		//added by edwar 2012-12-05, 这个方法，目前基本用不到 START
//		// 多级传递，将结果传给主界面
//		if (
//				
//				//modified by edwar 2012-12-05  不必要一一写出requestCode
//				
////				(requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_COMPLETE_ADDRESS_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_BIND_SINA_WEIBO_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_FOLLOW_MAMA100_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_FEEDBACK_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_HISTORY_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_MEMBER_CARD_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_YOURSELF_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_MESSAGE_DETAIL_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_MESSAGE_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_TAKE_PHOTO_SHARE_PAGE
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_HOMEPAGE 
////				| requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_CONCERNED_BABYSHOP_PAGE)
//				
//	
//				//modified by edwar 2012-12-05  不必要一一写出requestCode
//				requestCode == BasicApplication.getInstance().getRequestCode()
//				//modified by edwar 2012-12-05 end
//				&& resultCode == RESULT_OK) {
//			setResult(RESULT_OK, data);
//			finish();
//		}
//		
//		//added by edwar 2012-12-05, 这个方法，目前基本用不到 END
//
//	}

	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {

			if (request instanceof CrmMemberLoginReq) {
				isIntoFirstLogin = false;
				return UserProvider.getInstance(getApplicationContext())
						.crmLogin((CrmMemberLoginReq) request);
			} else if (request instanceof LoginReq) {
				isIntoFirstLogin = true;
				return UserProvider.getInstance(getApplicationContext())
						.firstLogin((LoginReq) request);

			} else {
				return null;
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			isLogining = false;
			closeProgressDialog();

			// 不成功
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				makeText(response.getDesc());
				if (step == 1) {

				} else if (step == 2) {
					if (response.getCode().equals(
							CrmMemberLoginRes.wrong_validateCode)) {
					} else if (response.getCode().equals(
							CrmMemberLoginRes.mobile_is_not_crm_member)) {
						// TODO 跳去注册界面。
					}
					// 其余的已关联的情况
					else if (response.getCode().equals(
							CrmMemberLoginRes.CRM_MEMBER_HAS_ASSO)) {
						CrmMemberLoginRes res = (CrmMemberLoginRes) response;
						String name = res.getUsername();
						String pwd = res.getPwd();
						doAutoLogin(name, pwd);
					}
				}
				return;
			}
			// 成功的话
			else {
				if (step == 1) {
					setupTopBar2();
					showSpecificPage(PAGE_TWO);
				} else if (step == 2) {
					if (isIntoFirstLogin) {
						isIntoFirstLogin = false;//清空
						loadApplicationData();
					} else {
						setupTopBar3();
						showSpecificPage(PAGE_THREE);
					}
				} else if (step == 3) {
					if (isIntoFirstLogin) {
						isIntoFirstLogin = false;//清空
						loadApplicationData();
					} else {
						CrmMemberLoginRes res = (CrmMemberLoginRes) response;
						String name = res.getUsername();
						String pwd = res.getPwd();
						doAutoLogin(name, pwd);
					}
				}
				return;
			}
		}
	}

	/**********************************
	 * 第三页用到的方法
	 ***********************************/
	private void setupTopBar3() {
		setTopLabel(R.string.setting_pwd);
	}

	/**
	 * @param name
	 *            用户名
	 * @param pwd
	 *            密码
	 */

	public void doAutoLogin(String name, String pwd) {

		LoginReq request = new LoginReq();
		request.setRememberMe("true");
		request.setUsername(name);
		request.setPassword(pwd);

		task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);

	}

	/**********************************
	 * 第二页用到的方法
	 ***********************************/
	private void setupTopBar2() {
		setTopLabel(R.string.input_verifycode_need);
		input_verify_code.getEditText().setText(""); // 清空验证码

	}

	/**********************************
	 * 第一页用到的方法
	 ***********************************/
	private void setupTopBar() {
		setTopLabel(R.string.login_with_member_phone);
	}

	private void setupCommonTopBar() {
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonVisibility(View.GONE);
	}

	/**
	 * /**
	 * <p>
	 * 
	 * @Title: initializeAnimation
	 * @param:
	 * @Description: 定义各种动画的效果。。这个方法必须在子类里调用
	 * @return </p>
	 */
	protected void initializeAnimation(Activity mActivity) {
		// 创建动画,
		view_animator = (ViewAnimator) mActivity
				.findViewById(R.id.view_animator);
		// 四种动画效果
		slideInLeft = AnimationUtils
				.loadAnimation(this, R.anim.i_slide_in_left);
		slideInRight = AnimationUtils.loadAnimation(this,
				R.anim.i_slide_in_right);
		slideOutLeft = AnimationUtils.loadAnimation(this,
				R.anim.i_slide_out_left);
		slideOutRight = AnimationUtils.loadAnimation(this,
				R.anim.i_slide_out_right);
	}

	/**
	 * <p>
	 * 
	 * @Title: showSpecificPage
	 * @param:
	 * @Description: 显示指定页面
	 * @return </p>
	 */
	protected void showSpecificPage(int pageIndex) {
		if (pageIndex != current) {
			if (pageIndex > current) {
				view_animator.setInAnimation(slideInLeft);
				view_animator.setOutAnimation(slideOutLeft);
				view_animator.showNext();
			} else if (pageIndex < current) {
				view_animator.setInAnimation(slideInRight);
				view_animator.setOutAnimation(slideOutRight);
				view_animator.showPrevious();
			}
			// view_animator.setDisplayedChild(pageIndex);
			current = pageIndex;
		} else {

		}
	}
}
