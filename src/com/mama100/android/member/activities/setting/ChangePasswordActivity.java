/**
 * 
 */
package com.mama100.android.member.activities.setting;

import android.content.Context;
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
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.ChangePwdReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;

/**
 * <p>
 * Description: ChangePasswordActivity.java
 * </p>
 * @author aihua.yan
 * 2012-7-16
 * 更改密码
 */
public class ChangePasswordActivity extends BaseActivity {

	private EditText edt_old_pwd;
	private EditText edt_new_pwd;
	private EditText edt_new_pwd2;
	
	private CheckBox cBox_showpwd;
	
	private Button  submit;
	
	private boolean acceptOldPwd=false;
	private boolean acceptNewPwd=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()+"   onCreate");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.change_pwd);
		
		edt_old_pwd=(EditText)findViewById(R.id.edt_old_pwd);
		edt_new_pwd=(EditText)findViewById(R.id.edt_new_pwd);
		edt_new_pwd2=(EditText)findViewById(R.id.edt_new_pwd2);
		cBox_showpwd=(CheckBox)findViewById(R.id.cBox_showpwd);
		submit = (Button)findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submit();
			}
		});
		
		
		setTopLabel(getString(R.string.change_pwd));
		setBackgroundPicture(R.drawable.bg_wall2);
		
		setLeftButtonImage(R.drawable.selector_back);
		
		//页面调整
		//setRightButtonImage(R.drawable.selector_modify);
		
		edt_old_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		edt_new_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		edt_new_pwd2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		setListener();
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
	
	public void doClickLeftBtn(){
		onBackPressed();
	}
	
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		finish();
	}
	
//	public void doClickRightBtn(){
//		 submit();
//	}
	
	
	private void submit(){
		acceptNewPwd=true;
		acceptOldPwd=true;
		
//		final Drawable errorDraw=getResources().getDrawable(R.drawable.edittext_error);
//		errorDraw.setBounds(0,0,26,26);
		//由于EditText已限制18位，因此无需检查是否超位
		if(edt_old_pwd.getText().toString()==null){
			edt_old_pwd.setError(getString(R.string.oldpwd_null_error));
			edt_old_pwd.requestFocus();
			acceptOldPwd=false;
		}else if(edt_old_pwd.getText().toString().length()<6){
			edt_old_pwd.setError(getString(R.string.oldpwd_lenless_error));
			edt_old_pwd.requestFocus();
			acceptOldPwd=false;
		}else if(edt_new_pwd.getText().toString()==null){
			edt_new_pwd.setError(getString(R.string.newpwd_null_error));
			edt_new_pwd.requestFocus();
			acceptNewPwd=false;
		}else if(edt_new_pwd.getText().toString().length()<6){
			edt_new_pwd.setError(getString(R.string.newpwd_lenless_error));
			edt_new_pwd.requestFocus();
			acceptNewPwd=false;
		}else if(edt_new_pwd2.getText().toString()==null){
			edt_new_pwd2.setError(getString(R.string.newpwd2_null_error));
			edt_new_pwd2.requestFocus();
			acceptNewPwd=false;
		}else if(edt_new_pwd2.getText().toString().length()<6){
			edt_new_pwd2.setError(getString(R.string.newpwd2_lenless_error));
			edt_new_pwd2.requestFocus();
			acceptNewPwd=false;
		}else if(!edt_new_pwd2.getText().toString().equals(edt_new_pwd.getText().toString())){
			edt_new_pwd2.setError(getString(R.string.two_pwd_unlike));
			edt_new_pwd2.requestFocus();
			acceptNewPwd=false;
		}else if(edt_new_pwd2.getText().toString().equals(edt_new_pwd.getText().toString())
				&&edt_new_pwd.getText().toString().equals(edt_old_pwd.getText().toString())){
			edt_new_pwd.setError(getString(R.string.newpwd_is_oldpwd));
			edt_new_pwd.requestFocus();
			acceptNewPwd=false;
		}
		
//		//检查非法字符
//		if(!checkPwdChars(edt_old_pwd.getEditText().getText())){
//			edt_old_pwd.getEditText().setError(
//					getString(R.string.pwd_ill_chars));
//			edt_old_pwd.requestFocus();
//			acceptOldPwd=false;
//		}
//		if(!checkPwdChars(edt_new_pwd.getEditText().getText())){
//			edt_new_pwd.getEditText().setError(
//					getString(R.string.pwd_ill_chars));
//			edt_new_pwd.requestFocus();
//			acceptNewPwd=false;
//		}
		
		if(acceptNewPwd&&acceptOldPwd){
			final ChangePwdReq request=new ChangePwdReq();
			request.setOldPwd(edt_old_pwd.getText().toString());
			request.setNewPwd(edt_new_pwd.getText().toString());
			
			final UpdatePwdTask task=new UpdatePwdTask(this);
			task.execute(request);
			task.displayProgressDialog(R.string.doing_req_message);
			
		}
	}
	
	
	
	private void setListener(){
		cBox_showpwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						edt_old_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
						edt_new_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
						edt_new_pwd2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); 
					}else{
						edt_old_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						edt_new_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						edt_new_pwd2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
						
					}
					 Editable etable=null;
					 if(edt_old_pwd.isFocusable())
						 etable = edt_old_pwd.getText();
					 else if(edt_new_pwd.isFocusable())
						 etable = edt_new_pwd.getText();
					 else if(edt_new_pwd2.isFocusable())
						 etable = edt_new_pwd2.getText();
					 //移动光标至最后一位
		             Selection.setSelection(etable, etable.length());
			}
		});
		
//		edt_old_pwd.getEditText().addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				if (!checkPwdChars(s)) {
//					final  Thread tmpThread=new Thread(new Runnable() {
//						@Override
//						public void run() {
//							try {
//								Thread.sleep(100);
//								runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										edt_old_pwd.getEditText().setError(
//												getString(R.string.pwd_ill_chars));
//										edt_old_pwd.requestFocus();
//									}
//								});
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//
//						}
//					});
//					tmpThread.start();
//					acceptOldPwd = false;
//				} else
//					acceptOldPwd = true;
//
//			}
//		});
//		
//		edt_new_pwd.getEditText().addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//			}
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//			@Override
//			public void afterTextChanged(Editable s) {
//				if(!checkPwdChars(s)){
//					final  Thread tmpThread=new Thread(new Runnable() {
//						@Override
//						public void run() {
//							try {
//								runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										edt_new_pwd.getEditText().setError(
//												getString(R.string.pwd_ill_chars));
//										edt_new_pwd.requestFocus();
//									}
//								});
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//
//						}
//					});
//					tmpThread.start();
//					acceptNewPwd=false;
//				}
//				else acceptNewPwd=true;
//			}
//		});
	}

	
	class UpdatePwdTask
	extends AsyncReqTask{
		public UpdatePwdTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return UserProvider.getInstance(mContext).changePwd((ChangePwdReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				Toast.makeText(mContext, response.getDesc(), Toast.LENGTH_SHORT)
				.show();
				return;
			}
			Toast.makeText(mContext, response.getDesc(), Toast.LENGTH_SHORT)
			.show();
			ChangePasswordActivity.this.finish();
			
		}
		
	}
	
	
}
