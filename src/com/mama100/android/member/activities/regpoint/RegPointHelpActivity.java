package com.mama100.android.member.activities.regpoint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.zxing.CaptureActivity;

/**
 *
 * @Description: 帮助页面 
 * @author created by liyang   
 * @date 2012-11-16 下午5:02:46 
 */
public class RegPointHelpActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.regpoint_help);

		setLeftButtonImage(R.drawable.selector_back);
		setTopLabel("帮助");
		setBackgroundPicture(R.drawable.bg_wall);
		
		
		//从选择门店页面进入帮助页面时，滚动条滚到最底部
		final ScrollView mScrollView = (ScrollView)findViewById(R.id.scrollView);
		mScrollView.post(new Runnable() {
			@Override
	     	public void run() {
		   		//如果是从选择门店进入该页面
		   	   	//滚动条向下滚动到相应位置
				if(getIntent().getBooleanExtra("isOperationByShop", false)){
					int height = getWindowManager().getDefaultDisplay().getHeight();
					mScrollView.scrollTo(0, height);
				}	
			}	
		});
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
		
		//如果是由扫描页进来则点击返回的时候重新打开扫描页
		//否则直接关闭页面
		if(getIntent().getBooleanExtra("isOperationByScan", false)){
			startActivity(new Intent(this, CaptureActivity.class));
		}
	}
}
