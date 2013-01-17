/**
 * 
 */
package com.mama100.android.member.activities.regpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ViewAnimator;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.activities.user.CompleteRecevierAddressActivity;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.UpdateReceiverAddressReq;
import com.mama100.android.member.domain.user.UserActivateECardReq;
import com.mama100.android.member.domain.user.UserActivateECardRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.widget.EditTextEx;

/**
 * <p>
 * 激活会员卡
 * </p>
 * 
 * @author aihua.yan 2012-10-15
 */
public class ActivateECardActivity extends BaseActivity {
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
	private int current = PAGE_ONE; // 当前页，默认第一页

	String phone = "";
	String code = "";
	public final static String ACTIVATE = "activate";// 信息，传递给收货人地址界面，用于判断来源

	private int reqCode = 0;// 从inteng 获取请求码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onCreate");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activate_e_card);
		setupViews();
		setupCommonTopBar();
		setupTopBar();
		setBackgroundPicture(R.drawable.bg_wall);
		initializeAnimation(this);// 初始化switchButton栏的切换动画效果
		reqCode = getIntent().getIntExtra("requestcode", 0);
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
						UserActivateECardReq request = new UserActivateECardReq();
						request.setMobile(phone);
						request.setStep(UserActivateECardReq.STEP_ONE);

						task = new CustomAsyncTask(ActivateECardActivity.this);
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
						
						// 验证验证码是否为空
						if (StringUtils.isBlank(input_verify_code.getEditTextStr())) {
							input_verify_code.getEditText().setError(getResources().getString(R.string.lookfor_pwd_warning11));
							input_verify_code.getEditText().requestFocus();
							return;
						}
						
						String code = input_verify_code.getEditTextStr();
						
						UserActivateECardReq request = new UserActivateECardReq();
						request.setMobile(phone);
						request.setValidateCode(code);
						request.setStep(UserActivateECardReq.STEP_TWO);

						task = new CustomAsyncTask(ActivateECardActivity.this);
						task.displayProgressDialog(R.string.doing_req_message);
						task.execute(request);

					}
				});
	}
	
	
	
	private void checkVerifyCode(String verifyCode){
		if(StringUtils.isBlank(verifyCode)){
			input_verify_code.getEditText().setError(getResources().getString(R.string.lookfor_pwd_warning11));
			input_verify_code.getEditText().requestFocus();
			return;
		}
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
		switch (current) {
		case PAGE_ONE:
			if (StringUtils.isBlank(input_cell_phone.getEditTextStr())) {
				finish();
				return;
			}
			showmemberDialog(R.string.login_crm_act_tv_exit_tips2,
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

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RegPointHomeActivity.REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_REGPOINT_HISTORY
				|| requestCode == RegPointHomeActivity.REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_EXCHANGE_CARD
				|| requestCode == RegPointYourselfActivity.REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_REGPOINT_YOURSELF
				|| requestCode == CompleteRecevierAddressActivity.REQUEST_CODE_ACTIVATE_ECARD_ON_COMPLETE_ADDRESS
			    ||requestCode == AppConstants.REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_SHOP_SELECT //added by edwar 2012-11-30,解决用户取消完成收货人地址信息，页面不能自动隐藏
				) {
			setResult(resultCode);
			finish();
		}

	}

	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			if (request instanceof UserActivateECardReq) {
				return UserProvider.getInstance(getApplicationContext())
						.activateECard((UserActivateECardReq) request);
			} else if (request instanceof UpdateReceiverAddressReq) {
				return UserProvider.getInstance(getApplicationContext())
						.updateReceiverAddress(
								(UpdateReceiverAddressReq) request);
			} else {
				return null;
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			

			// 不成功
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				if (step == 1) {
					makeText(response.getDesc());
				} else if (step == 2) {
					// 失败情况一：验证码错误
					if (response.getCode().equals(
							UserActivateECardRes.wrong_validateCode)) {
						//验证码填写错误用此种提示
						makeText(response.getDesc());
					} else
					// 失败情况二：妈网用户已关联
					if (response.getCode().equals(
							UserActivateECardRes.MAMA100_USER_HAS_ASSO)) {
						//失败的时候弹出对话框提示
						showErrorDialog(response.getDesc());
					}else{
						//处理其余的情况
						makeText(response.getDesc());	
					}
				}
				return;
			}
			// 成功的话
			else {
				//成功的时候用此种提示
				makeText(response.getDesc());
				
				if (step == 1) {
					setupTopBar2();
					showSpecificPage(PAGE_TWO);
					clearPage2PreviousValue();

				} else if (step == 2) {
					BasicApplication.getInstance().setAsso(true);
					//及时更新用户的 账户
					BasicApplication.getInstance().setMobile(phone);
					goToCompleteReceiverAddress();
				}
				return;
			}
		}

	}

	/**********************************
	 * 第二页用到的方法
	 ***********************************/
	private void setupTopBar2() {
		setTopLabel(R.string.input_verifycode_need);
		setLeftButtonImage(R.drawable.selector_back);
	}

	// 跳到设置收货人地址界面
	private void goToCompleteReceiverAddress() {
		Intent intent = new Intent(getApplicationContext(),
				CompleteRecevierAddressActivity.class);
		intent.putExtra("from", ACTIVATE);
		startActivityForResult(intent, reqCode);
	}

	public void clearPage2PreviousValue() {
		if (input_verify_code == null)
			input_verify_code = (EditTextEx) findViewById(R.id.input_vcode);
		input_verify_code.getEditText().setText("");
	}

	private void showErrorDialog(String error) {
		showmemberDialog(error, 0, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				onBackPressed();
			}
		});
	}

	/**********************************
	 * 第一页用到的方法
	 ***********************************/
	private void setupTopBar() {
		setTopLabel(R.string.homepage_4);
		setLeftButtonImage(R.drawable.selector_cancel);
	}

	private void setupCommonTopBar() {
		setLeftButtonImage(R.drawable.selector_cancel);
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
