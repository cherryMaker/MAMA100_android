/**
 * 
 */
package com.mama100.android.member.activities.user;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.util.MobValidateUtils;
import com.mama100.android.member.util.NetworkUtils;
import com.mama100.android.member.util.StringUtils;

/**
 * <p>
 * Description: GetPasswordResultActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 用户找回密码成功界面
 */
public class GetPasswordResultActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.lookfor_pwd_result);
		setupTopbarLabels();
		setupViews();
		setBackgroundPicture(R.drawable.bg_wall);
	}

	private void setupViews() {
TextView login_now;
login_now = (TextView) findViewById(R.id.login_now);
login_now.setBackgroundResource(R.drawable.tv_selector);
login_now.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				redirectToLogin();
			}
		});
		
	}

	protected void redirectToLogin() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	private void setupTopbarLabels() {
		setTopLabel(R.string.login_tv_lookfor_pwd2);
		setLeftButtonVisibility(View.GONE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void doClickRightBtn() {
		super.doClickRightBtn();
		finish();
	}
	

@Override
public void onBackPressed() {
	super.onBackPressed();
	redirectToLogin();
}

}
