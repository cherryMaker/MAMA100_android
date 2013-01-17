/**   
 * @Title: RegPointHelpActivity.java 
 * @Package com.mama100.android.member.activities.regpoint 

 * @version V1.0   
 */

package com.mama100.android.member.activities.regpoint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.businesslayer.PointProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.point.DiyPointProductReq;
import com.mama100.android.member.domain.point.DiyPointProductRes;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.MobValidateUtils;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.zxing.CaptureActivity;

/**
 *
 * @Description: 手动输入验证码 
 * @author created by liyang
 * @date 2012-11-16 下午5:02:46 
 */
public class RegPointInputActivity extends BaseActivity {
	
	public static boolean isRegpointSuccess ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.regpoint_input);
		
		//设置顶部标题、按钮图片、以及页面背景
		setLeftButtonImage(R.drawable.selector_back);
		setTopLabel("手动输码");
		setBackgroundPicture(R.drawable.bg_wall);
		
		((TextView)findViewById(R.id.help)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		((TextView)findViewById(R.id.help)).getPaint().setAntiAlias(true);
		((TextView)findViewById(R.id.help)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RegPointInputActivity.this,RegPointHelpActivity.class);
				intent.putExtra("isOperationByScan", false);
				intent.putExtra("isOperationByShop", false);
				startActivity(intent);
			}
		});
		
		
		//点击验证按钮时执行
		findViewById(R.id.verify).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 16位防伪码
				String code = ((EditText) findViewById(R.id.code_txt)).getText().toString();
				
				//判断输入的验证码是否是16位
				if(StringUtils.isBlank(code)){
					((EditText) findViewById(R.id.code_txt)).setError(getString(R.string.regpoint_input_error_1));
					((EditText) findViewById(R.id.code_txt)).requestFocus();
				}else if (!MobValidateUtils.checkInputAntiFake(code)) {
					((EditText) findViewById(R.id.code_txt)).setError(getString(R.string.regpoint_input_error_2));
					((EditText) findViewById(R.id.code_txt)).requestFocus();
				} else {
					// 构建request对象,将code设入req中
					DiyPointProductReq req = new DiyPointProductReq();
					req.setSecurity(code);
					
					// 执行异步线程验证防伪码是否存在
					CustomAsyncTask task = new CustomAsyncTask(RegPointInputActivity.this);
					task.displayProgressDialog(R.string.regpoint_input_verify);
					task.execute(req);
				}
			}
		});
	}
	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(isRegpointSuccess){
			((EditText) findViewById(R.id.code_txt)).setText("");
			isRegpointSuccess = false;
		}
	}


	/**
	 * 点击返回按钮时执行
	 */
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		startActivity(new Intent(this, CaptureActivity.class));
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		//清除页面内存,暂无
	}


	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			
			if(request instanceof DiyPointProductReq){
				return PointProvider.getInstance(RegPointInputActivity.this).diyPointVerifyByScan((DiyPointProductReq)request);

			}
			return null;
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			LogUtils.logd(getClass(), "SPECIAL_CODE = " +response.getCode() );
			//验证成功都跳转到选择门店页面
			//if(SPECIAL_CODE1.equals(response.getCode())){
			if("100".equals(response.getCode())){
				DiyPointProductRes res = (DiyPointProductRes)response;
				
				LogUtils.logd(getClass(), "productId = " +res.getProductId() );
				LogUtils.logd(getClass(), "point = " +res.getPoint() );
				LogUtils.logd(getClass(), "ImageUrl = " +res.getProductImgUrl() );
				LogUtils.logd(getClass(), "productName = " +res.getProductName() );
				
				Intent intent = new Intent(RegPointInputActivity.this,RegPointProductActivity.class);
				intent.putExtra("productId", res.getProductId());
				intent.putExtra("productSerial", res.getSerial());
				intent.putExtra("productPoint", res.getPoint());
				intent.putExtra("productImgUrl", res.getProductImgUrl());
				intent.putExtra("productName", res.getProductName());
				intent.putExtra("productCode", ((EditText) findViewById(R.id.code_txt)).getText().toString());
				intent.putExtra("isOperationByScan",false);
				startActivity(intent);
			}else{
				makeText(response.getDesc());
			}
		}
	}
}
