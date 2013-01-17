/**
 * 
 */
package com.mama100.android.member.activities.regpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.businesslayer.PointProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.point.PointSubmitReq;
import com.mama100.android.member.domain.point.PointSubmitRes;
import com.mama100.android.member.domain.point.PointVerifyReq;
import com.mama100.android.member.domain.point.PointVerifyRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.widget.EditTextEx;
import com.mama100.android.member.widget.dialog.SureDialog;
import com.mama100.android.member.zxing.CaptureActivity;

/**
 * <p>
 * Description: RegPointYourselfActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 自助积分界面
 */
public class RegPointYourselfActivity extends BaseActivity {

	/****************************
	 * 第一页面变量
	 ****************************/
	EditTextEx series; // 序列号
	EditTextEx verify_code;// 验证码

	/****************************
	 * 第二页面变量
	 ****************************/
	EditTextEx shop_code;// 门店编号
	TextView tv_label;// 购买产品和积分值提示

	/****************************
	 * 第三页面变量
	 ****************************/
	TextView tv_result;// 购买产品和积分值提示

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

	// 弹出帮助框
	private PopupWindow pw;
	private int POPUP_WINDOW_SHORT = 400; // 弹出窗口的短边
	private int POPUP_WINDOW_LONG = 480;// 弹出窗口的长边
	ViewGroup viewGroup;
	private TextView refer;
	public final int SUBMIT_RESPONSE = 0;
	public final int VERIFY_RESPONSE = 1;
	public String sum = ""; // 积分成功最后的用户总积分值

	/*************************************************
	 * 扫描用到的变量 ， added by edwar 2012-09-04
	 *************************************************/
	protected static final int SCAN_BARCODE_FOR_SERIES = 1000;
	protected static final int SCAN_BARCODE_FOR_VCODE = 1001;
	private Drawable scanner;

	/***************************************************
	 * 分隔字符串用到的常量
	 ****************************************************/
	private static int NUMBERS = 4;
	public static Object WILDCARD = '-';

	private EditText et_series;
	private EditText et_vcode;

	private CustomAsyncTask task = null;
	
	public static final int REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_REGPOINT_YOURSELF = 11111224;//点击提交自助积分，激活个人会员卡请求code

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.regpoint_diy);
		setBackgroundPicture(R.drawable.bg_wall2);
		setupViews();
		setupTopBar();
		initializeAnimation(this);// 初始化switchButton栏的切换动画效果
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

	// TextWatcher mTextWatcher = new TextWatcher() {
	// private CharSequence temp;
	// private int editStart ;
	// private int editEnd ;
	// private int length;//记录字符串被删除字符之前，字符串的长度
	// @Override
	// public void beforeTextChanged(CharSequence s, int arg1, int arg2,
	// int arg3) {
	// length = s.length();
	// }
	//
	// @Override
	// public void onTextChanged(CharSequence s, int arg1, int arg2,
	// int arg3) {
	// }
	//
	// @Override
	// public void afterTextChanged(Editable s) {
	// et_series.setSelection(length);
	// if(length%5==0){
	// StringBuffer sb = new StringBuffer(s.toString());
	// et_series.setText(StringUtils.splitStringWithWildCard(NUMBERS, sb,
	// WILDCARD));
	// Editable ea= et_series.getText(); //etEdit为EditText
	// }
	// }
	// };

	
	private void setupViews() {

		/************************************************
		 * 第一页
		 ************************************************/
		series = (EditTextEx) findViewById(R.id.reg_series);
		series.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

		ImageView scanner1 = (ImageView) findViewById(R.id.scanner1);
		scanner1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						CaptureActivity.class);
				startActivityForResult(intent, SCAN_BARCODE_FOR_SERIES);

			}
		});

		verify_code = (EditTextEx) findViewById(R.id.reg_code);
		verify_code.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

		ImageView scanner2 = (ImageView) findViewById(R.id.scanner2);
		scanner2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						CaptureActivity.class);
				startActivityForResult(intent, SCAN_BARCODE_FOR_VCODE);

			}
		});

		((ImageButton) findViewById(R.id.where_to_find))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new SureDialog(RegPointYourselfActivity.this)
								.showDialog(R.drawable.question_helper_what_is_serial_number);
					}
				});

		/************************************************
		 * 第二页
		 ************************************************/
		shop_code = (EditTextEx) findViewById(R.id.shop_code);
		shop_code.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		tv_label = (TextView) findViewById(R.id.pagetwo_tv_label);
		((ImageButton) findViewById(R.id.what_is_shopcode))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new SureDialog(RegPointYourselfActivity.this)
								.showDialog(R.drawable.question_helper_what_is_shopcode);
					}
				});

		/****************************************************
		 * 第三页
		 ********************************************************/
		tv_result = (TextView) findViewById(R.id.pagethree_tv_label);

		refer = (TextView) findViewById(R.id.refer);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		tv_label = null; // 释放Html.fromHtml引起的内存
		tv_result = null;// 释放Html.fromHtml引起的内存

		if (series != null) {
			series = null;
		}
		if (verify_code != null) {
			verify_code = null;
		}
		if (shop_code != null) {
			shop_code = null;
		}

		if (pw != null) {
			pw.dismiss();
			pw = null;
		}

		if (task != null) {
			task.cancel(true);
			task = null;
		}

	}

	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		onBackPressed();
	}

	@Override
	public void doClickRightBtn() {
		super.doClickRightBtn();
		switch (current) {
		case PAGE_ONE:
			doVerify();
			break;
		case PAGE_TWO:
			doConfirm();
			break;

		case PAGE_THREE:
			Intent intent = new Intent();
			intent.putExtra("sum", sum);
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			break;
		}

	}

	/**********************************
	 * 第三页用到的方法
	 ***********************************/
	// 第三页的顶部栏
	private void setupTopBar3() {
		setRightButtonImage(R.drawable.selector_done);
		setLeftButtonVisibility(View.GONE);
	}

	/**********************************
	 * 第二页用到的方法
	 ***********************************/
	private void setupTopBar2() {
		setRightButtonImage(R.drawable.selector_confirm);
		setLeftButtonImage(R.drawable.selector_back);

	}

	private void doConfirm() {
		PointSubmitReq request = new PointSubmitReq();
		request.setSerial(series.getEditTextStr());
		request.setSecurity(verify_code.getEditTextStr());
		request.setTerminal(shop_code.getEditTextStr());

		task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);
	}

	/**********************************
	 * 第一页用到的方法
	 ***********************************/
	private void setupTopBar() {
		setLeftButtonImage(R.drawable.selector_back);
		setTopLabel(R.string.regpoint_title);
		// 初始化第一页
		setRightButtonImage(R.drawable.selector_verify);
	}

	private void doVerify() {
		/****************************************
		 * 未登录的情况下， added by edwar 2012-10-16
		 ****************************************/
		if (isUnlogin()) {
			goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_YOURSELF_PAGE);
			return;
		}
		else
			//2,判断是否已经关联
			if(!isAsso())
			{
				showmemberDialog(R.string.activate_member_card_warning_tips4,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								
								Intent intent = new Intent(getApplicationContext(),
										ActivateECardActivity.class);
								intent.putExtra("requestcode", REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_REGPOINT_YOURSELF);
								startActivityForResult(intent,REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_REGPOINT_YOURSELF);
								closeDialog();
							}
						});
				return;
			}

		// 验证用户名
		if (!verifyInput(series.getEditText(), AppConstants.CHECK_SERIALNUMBER)) {
			return;
		}
		// 验证密码
		if (!verifyInput(verify_code.getEditText(),
				AppConstants.CHECK_ANTIFAKECODE)) {
			return;
		}


		PointVerifyReq request = new PointVerifyReq();
		request.setSerial(series.getEditTextStr());
		request.setSecurity(verify_code.getEditTextStr());

		task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);
	}

	/**********************************************
	 * 通用方法
	 **********************************************/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SCAN_BARCODE_FOR_SERIES:
				if (data != null) {
					String code = data.getType();
					
					LogUtils.loge(getClass(), "code ======================================== " + code);
					
					series.setEditTextStr(code);

					// StringBuffer sb = new StringBuffer(code);
					// series.setEditTextStr(StringUtils.splitStringWithWildCard(NUMBERS,
					// sb, WILDCARD));
				}

				break;
			case SCAN_BARCODE_FOR_VCODE:
				if (data != null) {
					String code = data.getType();
					
					LogUtils.loge(getClass(), "code ======================================== " + code);
					
					verify_code.setEditTextStr(code);

					
					// StringBuffer sb = new StringBuffer(code);
					// verify_code.setEditTextStr(StringUtils.splitStringWithWildCard(NUMBERS,
					// sb, WILDCARD));
				}

				break;

				//点击Verify了。。
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_YOURSELF_PAGE:
				setUnlogin(false);
				BasicApplication.getInstance().setAutoLogin(true);
				//涉及到输入的情况下，就不主动点击;以免造成错误。
//				doClickRightBtn();
				
			case REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_REGPOINT_YOURSELF:
				doClickRightBtn();
				break;	
				
			default:
				break;
			}
		}

	}

	public class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SUBMIT_RESPONSE:
				PointSubmitRes submitRes = (PointSubmitRes) msg.obj;

				// TODO 获取积分总值
				sum = submitRes.getPbalance();
				String value = submitRes.getPoint();
				String html_title = "<font color=#000000>恭喜您,</font> "
						+ "<font color=#ff6600>" + "成功积分" + value + "分！\n"
						+ "</font>" + "<font color=#000000>现在您的总积分余额</font>"
						+ "<font color=#ff6600>" + sum + "分。" + "</font>";
				tv_result.setText(Html.fromHtml(html_title));

				BasicApplication.getInstance().setLastRegpointBalance(sum);
				setupTopBar3();
				showSpecificPage(PAGE_THREE);

				break;
			case VERIFY_RESPONSE:
				PointVerifyRes verifyRes = (PointVerifyRes) msg.obj;

				// TODO 获取产品名 和 积分值
				String product = verifyRes.getPname();
				String value2 = verifyRes.getPoint();

				String html_title2 = "<font color=#000000>您购买的产品是</font> "
						+ "<font color=#ff6600>" + product + "</font>"
						+ "<font color=#000000>,将增加</font>"
						+ "<font color=#ff6600>" + value2 + "</font>"
						+ "<font color=#000000>积分，请确认您的购买产品门店:</font>";
				tv_label.setText(Html.fromHtml(html_title2));
				setupTopBar2();
				showSpecificPage(PAGE_TWO);
				shop_code.getEditText().setText("");

				break;

			default:
				break;
			}

		}
	}

	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			if (request instanceof PointVerifyReq) {
				return PointProvider.getInstance(getApplicationContext())
						.diyPointVerify((PointVerifyReq) request);
			} else if (request instanceof PointSubmitReq) {
				return PointProvider.getInstance(getApplicationContext())
						.diyPointSubmit((PointSubmitReq) request);
			} else {
				return null;
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				makeText(response.getDesc());
				return;
			}
			// 自助积分提交
			if (response instanceof PointSubmitRes) {
				ResultHandler handler = new ResultHandler();
				handler.obtainMessage(SUBMIT_RESPONSE, response).sendToTarget();
			}
			// 自助积分验证
			else if (response instanceof PointVerifyRes) {
				ResultHandler handler = new ResultHandler();
				handler.obtainMessage(VERIFY_RESPONSE, response).sendToTarget();
			}
		}

	}

	@Override
	public void onBackPressed() {
		if (pw != null && pw.isShowing()) {
			viewGroup.setBackgroundResource(0);// 移除背景
			pw.dismiss();
			pw = null;
			return;
		}

		switch (current) {
		case PAGE_ONE:
			if (StringUtils.isBlank(series.getEditTextStr())
					&& StringUtils.isBlank(verify_code.getEditTextStr())) {
				finish();
				return;
			}
			showmemberDialog(R.string.main_act_tv_exit_tips2,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							closeDialog();
							finish();
						}
					});
			break;
		case PAGE_TWO:
			setupTopBar2();
			showSpecificPage(PAGE_ONE);
			break;

		case PAGE_THREE:
			finish();
			break;

		default:
			break;
		}
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
