/**
 * 
 */
package com.mama100.android.member.activities.user;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.FindPwdReq;
import com.mama100.android.member.domain.user.GetVerifyCodeReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.NetworkUtils;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.widget.EditTextEx;

/**
 * <p>
 * Description: ForgetPasswordActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 用户找回密码界面
 */
public class GetPasswordActivity extends BaseActivity {

	/****************************
	 * 通用变量
	 ****************************/
	private TextView warning;
	private Handler mHandler;
	private ProgressDialog mDialog;

	/**********************************
	 * 输入手机帐号 有关的变量
	 **********************************/
	private int count = AppConstants.INTERVAL_TIME;
	private EditTextEx input;
	private Button send;
	private Button send_wait;

	/**********************************
	 * 输入验证码 有关的变量
	 **********************************/
	private LinearLayout vcode_bar;
	private Button send_vcode;
	private EditTextEx et_vcode;

	Timer timer;
	TimerTask task;
	public final int VERIFY_RESPONSE = 2000;
	public final int SUBMIT_RESPONSE = 1000;
	public int current; //当前是哪种类型的响应

	public void hideVcodebar() {
		if (vcode_bar != null) {
			vcode_bar.setVisibility(View.GONE);
		}
	}

	public void displayVcodebar() {
		if (vcode_bar != null) {
			vcode_bar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.lookfor_pwd);
		setupViews();
		setupTopbarLabels();
		initSubmitHandler();
		hideVcodebar();
		setBackgroundPicture(R.drawable.bg_wall);
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);//百度统计
//		MobileProbe.onResume(this);//CNZZ统计
	}
	
	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);//百度统计
//		MobileProbe.onPause(this);//CNZZ统计
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDialog != null) {
			mDialog.cancel();
		}
		if (mHandler != null) {
			mHandler = null;
		}

		destroyTimer();
	}

	private void destroyTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}

		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	private void setupViews() {
		input = (EditTextEx) findViewById(R.id.input);
		input.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
		warning = (TextView) findViewById(R.id.error_warning);

		send = (Button) findViewById(R.id.send);
		send_wait = (Button) findViewById(R.id.send_wait);
		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hideWarning(warning);
				hideVcodebar();
				hideSoftWindowInput();
				String mobile = ((EditTextEx) input).getEditTextStr();
				// 验证手机号
				if (!verifyInput(input.getEditText(), AppConstants.CHECK_MOBILE)) {
					return;
				}
				// 检测网络状态, 并给出错误提示
				if (!NetworkUtils
						.checkNetworkStatusAndHint(GetPasswordActivity.this)) {
					return;
				}
				
				getVerify();

			}
		});

		/********************************************************
		 * 输入验证码
		 ********************************************************/
		vcode_bar = (LinearLayout) findViewById(R.id.vcode_bar);
		et_vcode = (EditTextEx) findViewById(R.id.vcode);
		et_vcode.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
		send_vcode = (Button) findViewById(R.id.send_vcode);
		send_vcode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideWarning(warning);

				// 构造登录表单数据
				String vcode = et_vcode.getEditTextStr();

				// 验证验证码
				if (StringUtils.isBlank(vcode)) {
					et_vcode.getEditText().setError(getResources().getString(R.string.alert_verifycode_need));
					et_vcode.getEditText().requestFocus();
					return;
				}
				
				// 检测网络状态, 并给出错误提示
				if (!NetworkUtils
						.checkNetworkStatusAndHint(GetPasswordActivity.this)) {
					return;
				}
			submit();
			}
		});

	}
	
	private void submit() {
		// 验证手机号
		if (!verifyInput(input.getEditText(), AppConstants.CHECK_MOBILE)) {
			return;
		}
	
		FindPwdReq request = new FindPwdReq();
		request.setMobile(input.getEditTextStr());
		request.setVcode(et_vcode.getEditTextStr());

		CustomAsyncTask task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);
	}
	
	
	protected void getVerify() {

		GetVerifyCodeReq request = new GetVerifyCodeReq();
		request.setMobile(input.getEditTextStr());
		request.setType("password");

		CustomAsyncTask task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);
	}
	

	protected void switchWaitingButton(Button btn) {
		showWaitButton();
		if (timer == null)
			timer = new Timer();
		if (task == null)
			task = new CountDownTask();
		timer.schedule(task, 200, 1000);
	}

	// 显示等待按钮
	private void showWaitButton() {
		send.setVisibility(View.GONE);
		send_wait.setVisibility(View.VISIBLE);
	}

	// 显示发送按钮
	private void showSendButton() {
		send_wait.setVisibility(View.GONE);
		send.setVisibility(View.VISIBLE);
	}

	public class CountDownTask extends TimerTask {

		@Override
		public void run() {
			if (count >= 0) {
				mHandler.obtainMessage(0).sendToTarget();
			} else {
				mHandler.obtainMessage(1).sendToTarget();
				destroyTimer();
				count = AppConstants.INTERVAL_TIME;
			}
		}
	}

	// 隐藏错误信息
	private void hideWarning(TextView tv) {
		tv.setVisibility(View.INVISIBLE);

	}

	// 显示错误信息
	private void showWarning(TextView tv, String str) {
		tv.setText(str);
		tv.setVisibility(View.VISIBLE);
	}

	// 显示错误信息
	private void showWarning(TextView tv, int res) {
		tv.setText(res);
		tv.setVisibility(View.VISIBLE);
	}

	private void setupTopbarLabels() {
		setTopLabel(R.string.login_tv_lookfor_pwd2);
		setLeftButtonImage(R.drawable.selector_back);
	}

	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		finish();
	}

	private void initSubmitHandler() {

		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					String numberFormat = getResources().getString(
							R.string.lookfor_pwd_label3);
					String sum = String.format(numberFormat, count--);
					send_wait.setText(sum);
					break;
				case 1:
					showSendButton();
					break;
				case 100:
					// Bundle bundle = msg.getData();
					// BaseResult result = (BaseResult)
					// bundle.getSerializable("result");

					try {

						// if (result.getCode().equals(MobResponseCode.SUCCESS))
						// {
						// showWarning(warning,R.string.lookfor_pwd_warning6);
						// displayVcodebar();
						// } else {
						// // 显示失败结果
						// showWarning(warning,result.getDesc());
						// }
					} finally {
						if (mDialog.isShowing()) {
							mDialog.dismiss();
						}
					}

					break;

				case 200:
					// Bundle bundle = msg.getData();
					// BaseResult result = (BaseResult)
					// bundle.getSerializable("result");
					try {
						// if (result.getCode().equals(MobResponseCode.SUCCESS))
						// {
						// showWarning(warning,R.string.lookfor_pwd_warning9);
						// ForgetPasswordActivity.this.finish();
						// } else {
						// // 显示失败结果
						// showWarning(warning,result.getDesc());
						// }

					} finally {
						if (mDialog.isShowing()) {
							mDialog.dismiss();
						}
					}

					break;

				default:
					break;
				}

			}

		};

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		doClickLeftBtn();
	}
	
	/**********************************************
	 *  通用方法
	 **********************************************/
	
	public class ResultHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case SUBMIT_RESPONSE:
			
				startActivity(new Intent(getApplicationContext(),
						GetPasswordResultActivity.class));
				finish();	
				
				break;
			case VERIFY_RESPONSE:
				switchWaitingButton(send);
				displayVcodebar();// 测试用
				// TODO 检查本机网络设置
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
			if (request instanceof FindPwdReq) {
				current =  SUBMIT_RESPONSE;
				return UserProvider.getInstance(getApplicationContext())
						.getPassword((FindPwdReq) request);
			} else if (request instanceof GetVerifyCodeReq) {
				current =  VERIFY_RESPONSE;
				return UserProvider.getInstance(getApplicationContext())
						.getVerifyCode((GetVerifyCodeReq) request);
			}else{
				return null;
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			makeText(response.getDesc());
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				return;
			}
			
			if (current == VERIFY_RESPONSE) {
				ResultHandler handler = new ResultHandler();
				handler.obtainMessage(VERIFY_RESPONSE , response).sendToTarget();
			}
			if (current == SUBMIT_RESPONSE) {
				ResultHandler handler = new ResultHandler();
				handler.obtainMessage(SUBMIT_RESPONSE , response).sendToTarget();
			}
		}

	}
}
