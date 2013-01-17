package com.mama100.android.member.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.util.StorageUtils;
import com.mama100.android.member.widget.scrollview.screenscroll.MyScrollLayout;
import com.mama100.android.member.widget.scrollview.screenscroll.OnViewChangeListener;

public class SwitchViewDemoActivity extends Activity implements
		OnViewChangeListener, OnClickListener {
	/** Called when the activity is first created. */

	private MyScrollLayout mScrollLayout;
	private ImageView[] mImageViews; //小圆点
	private int mViewCount;//视图个数，等于圆点数
	private int mCurSel;//当前被选择界面

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.switchscrollview);
		StorageUtils.setBooleanShareValue(this, AppConstants.IS_FIRST_OPEN, false);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);//百度统计
//		MobileProbe.onResume(this);//CNZZ统计
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);//百度统计
//		MobileProbe.onPause(this);//CNZZ统计
	}

	private void init() {
		
		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);
		//获取<MyScrollLayout>标签里的<FrameLayout>个数
		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new ImageView[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			// 用的巧妙，用getChildAt(i)这个方法，如果是我，就只会用findViewById
			mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);//已经选择的不能再点
		mScrollLayout.SetOnViewChangeListener(this);
	}

	private void setCurPoint(int index) {
		//数组越界或者index已经被选中，则直接退出
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;//更新数据
	}

	@Override
	public void OnViewChange(int view , boolean flag) {
		//如果是最后一张，点击关闭
		if(flag) {
			setResult(RESULT_OK);
			finish();}
//		setCurPoint(view);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mImageViews!=null){
			for (int i = 0; i < mImageViews.length; i++) {
				ImageView imageview = mImageViews[i];
				imageview.setBackgroundDrawable(null);
				
			}
			mImageViews=null;
			
		}
		
	}

	@Override
	public void onClick(View v) {
		int pos = (Integer) (v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}
}