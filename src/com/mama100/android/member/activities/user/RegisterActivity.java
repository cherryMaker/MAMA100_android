/**
 * 
 */
package com.mama100.android.member.activities.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.bean.MobResponseCode;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.RegisterReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StorageUtils;

/**
 * <p>
 * Description: RegisterActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 用户注册界面
 * 
 * @modify by liyang 2012-12-17 注册账号增加自动补全功能
 */
public class RegisterActivity extends BaseActivity {
	

	
	//added by liyang  2012-12-17
	private EditText account;
	
	//annotation by liyang 2012-12-17
	//private EditText account;
	private EditText password2;
	private EditText password1;
	
	private Button submit;

	/***************************
	 * 判断当前是 获取验证码 还是 提交登录
	 ***************************/
	private boolean isCurrentSubmit = false;

	private CustomAsyncTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.register2);
		setupViews();
		setupTopbarLabels();
		setBackgroundPicture(R.drawable.bg_wall2);
		BasicApplication.getInstance().setToExit(false);
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
		account = (EditText) findViewById(R.id.register_email_account);
		account.setInputType(InputType.TYPE_CLASS_TEXT);
		
		
		password2 = (EditText) findViewById(R.id.register_pwd2);
		password1 = (EditText) findViewById(R.id.register_pwd1);
		password1.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		CheckBox isShow;
		isShow = (CheckBox) findViewById(R.id.isShow);
		isShow.setChecked(false);
		// 设置默认为不显示
		password2.setInputType(
				InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		isShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					password1.setInputType(
							InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					password2.setInputType(
							InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

				} else {
					password1.setInputType(
							InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					password2.setInputType(
							InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

				}
				Editable etable = password2.getText();
				// 移动光标至最后一位
				Selection.setSelection(etable, etable.length());
			}
		});
		
		//增加 by liyang 2012-10-29 界面调整
		submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				register();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (task != null && task.isCancelled() == false) {
			task.cancel(true);
			task = null;
		}

	}

	private void setupTopbarLabels() {
		setTopLabel(R.string.register);
		
		//注释by liyang  2012-10-29 界面调整
		//setRightButtonImage(R.drawable.selector_submit);
		
		setLeftButtonImage(R.drawable.selector_back);
	}

	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		doClickLeftBtn();
	}

	
//注释by liyang  2012-10-29 界面调整	
/**	@Override
	public void doClickRightBtn() {
		super.doClickRightBtn();
		// //TODO 提交注册
		// startActivity(new Intent(getApplicationContext(),
		// EditProfileActivity.class));
		register();
	}*/
	
	
	/**
	 * 检查邮箱是否包含中文
	 * 之前的邮箱验证正在表达式未进行中文验证
	 * 在此额外加上，最好是能把此验证合到之前的正则表达式中去
	 * added by liyang  
	 * 2012-10-24  
	 * @return
	 */
	public boolean isEmailExistCN(EditText et){
		//用来判断是否存在中文的正则表达式
		String regx = "[\u4e00-\u9fa5]";
		String text = et.getText().toString();
		
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(text);
		
		return matcher.find();
	}
	
	

	private void register() {
		// 验证邮箱
		if (!verifyInput(account, AppConstants.CHECK_EMAIL)) {
			return;
		}
		
		// 验证邮箱是否输入中文、不允许
		if(isEmailExistCN(account)){
			account.setError(getString(R.string.check_email_illegal));
			account.requestFocus();
			return;
		}

		
		// 验证密码
		if (!verifyInput(password1, AppConstants.CHECK_PASSWORD)) {
			return;
		}

		// 验证确认密码
		if (!verifyInput(password2, AppConstants.CHECK_PASSWORD)) {
			return;
		}

		if (password1.getText().toString() != null
				&& !password2.getText().toString().equals(
						password1.getText().toString())) {
			password2.setError(getString(R.string.two_pwd_unlike));
			password2.requestFocus();
			return;
		}

		// 验证两个密码的一致性

		RegisterReq request = new RegisterReq();
		request.setEmail(account.getText().toString());
		request.setPwd(password2.getText().toString());
		CustomAsyncTask task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);
	}

	
	//added by edwar 2012-12-05, 实现多级传递 START
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BasicApplication.getInstance().getRequestCode()
				&& resultCode == RESULT_OK) {
			setResult(RESULT_OK, data);
			finish();
		}

	}
	//added by edwar 2012-12-05, 实现多级传递 END
	
	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			if (request instanceof RegisterReq) {
				isCurrentSubmit = true;
				return UserProvider.getInstance(getApplicationContext())
						.registerLoginByEmail((RegisterReq) request);
				// return UserProvider.getInstance(getApplicationContext())
				// .register((RegisterReq) request);
			} else {
				return null;
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			if (isCurrentSubmit
					&& response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				makeText("注册成功");
				
				
				//commented by edwar 2012-12-05 start, 不用这种方法关闭Activity
				//added by edwar 2012-11-07
//				BackStackManager.getInstance().removeActivity(AppConstants.ACTIVITY_LOGIN_HOMEPAGE);
				//commented by edwar 2012-12-05 end
				BasicApplication.getInstance().setFromRegister(true);
				loadApplicationData();
				
			} else {
				makeText(response.getDesc());
			}

			// 判断是否出现tgt无效
			if (response.getCode().equals(MobResponseCode.TGT_INVALID)) {
				// 方法二,调用SplashActivity的onNewIntent()
				// Intent intent = new Intent(getApplicationContext(),
				// SplashActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
				// finish();

				StorageUtils.storeLoginTGTInClient(getApplicationContext(), "");
				BasicApplication.getInstance().setTgt("");

				// 方法一，直接finish()，不过会调用SplashActivity的onResume()
				finish();
			}
		}

	}

}
