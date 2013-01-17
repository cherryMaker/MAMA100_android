/**
 * 
 */
package com.mama100.android.member.activities.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.FeedbackReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;

/**
 * <p>
 * Description: FeedBackActivity.java
 * </p>
 * @author aihua.yan
 * 2012-7-16
 * 意见反馈界面
 */
public class FeedBackActivity extends BaseActivity {
	
	private EditText edt_feedback;
	private TextView tv_title;
	private TextView tv_tips2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()+"   onCreate");
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.feed_back);
		
		edt_feedback=(EditText) findViewById(R.id.edt_feedback);
		tv_title=(TextView) findViewById(R.id.tv_title);
		tv_tips2=(TextView) findViewById(R.id.tv_tips2);
		
		setTopLabel(getString(R.string.feed_back));
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonImage(R.drawable.selector_submit);
		setBackgroundPicture(R.drawable.bg_wall2);
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
	public void doClickRightBtn() {
		super.doClickRightBtn();
		
		if(isUnlogin()){
			goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_FEEDBACK_PAGE);
			return;
		}
		
		if(edt_feedback.getText().toString()==null
				||edt_feedback.getText().toString().equals("")){
			edt_feedback.setError(getString(R.string.feed_back_null));
			return;
		}
		
		final FeedbackReq request=new FeedbackReq();
		request.setContent(edt_feedback.getText().toString());
		
		final InnerTask task=new InnerTask(this);
		task.execute(request);
		task.displayProgressDialog(R.string.uploading_feed_back);
	}
	

	/**
	 * 具体的第三方的保存设置操作是在 父类ThirdPartyLoginActivity.OnActivityResult()里面执行，
	 * 该子类只负责简单的UI显示
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_FEEDBACK_PAGE:
				setUnlogin(false);
				BasicApplication.getInstance().setAutoLogin(true);
				
				//再次调用之前的点击事件。。
				//涉及到输入的情况下，就不主动点击;以免造成错误。
//				doClickRightBtn();
				break;

			default:
				break;
			}
		}
	}
	
	
	public void doClickLeftBtn(){
		onBackPressed();
	}
	
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		finish();
	}
	
	class InnerTask
	extends AsyncReqTask{
		public InnerTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return UserProvider.getInstance(mContext).uploadFeedback((FeedbackReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				Toast.makeText(mContext, response.getDesc(), Toast.LENGTH_SHORT)
				.show();
				return;
			}
//			Toast.makeText(mContext, response.getDesc(), Toast.LENGTH_SHORT)
//			.show();
			
			edt_feedback.setVisibility(View.GONE);
			tv_title.setVisibility(View.VISIBLE);
			tv_tips2.setVisibility(View.VISIBLE);
			setRightButtonVisibility(View.INVISIBLE);
			
		}
		
	}
	
	

}
