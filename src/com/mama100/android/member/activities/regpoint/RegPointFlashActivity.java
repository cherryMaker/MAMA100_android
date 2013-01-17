package com.mama100.android.member.activities.regpoint;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.mama100.android.member.R;
import com.mama100.android.member.zxing.CaptureActivity;


/**
 * @Description: 闪屏页面 
 * @author created by liyang
 * @date 2012-11-16 下午5:02:46 
 */
public class RegPointFlashActivity extends Activity implements
		OnTouchListener, OnGestureListener {

	private GestureDetector gestureDetector;
	
	// 计时器 当闪屏页面显示时间到10秒后自动跳转
	private Timer flashTimer = new Timer();
	
	// 定义显示时间常量
	private static final int TIME = 10000; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regpoint_flash);

		gestureDetector = new GestureDetector(this);

		// 设置整个页面监听事件
		findViewById(R.id.flash_view).setOnTouchListener(this);
		findViewById(R.id.flash_view).setLongClickable(true);
		
		findViewById(R.id.kownBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoNextPage();
			}
		});
		
		//启动计时器
		flashTimer.schedule(flashTask,TIME);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//退出计时器
		flashTimer.cancel();
		flashTimer = null;
		
		
		if(flashTask.cancel()){
			flashTask = null;
		}
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		// 只有向左滑动的时候才关闭当前页面
		if (e1.getX() - e2.getX() > 100) {
			gotoNextPage();
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

	private TimerTask flashTask = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(0);
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			//只有在已登录和已开通积分通的情况下才会跳转到扫描页面
			gotoNextPage();
		}
	};
	
	
	/**
	 * 打开扫描页
	 */
	private void gotoNextPage(){
		//只有在已登录和已开通积分通的情况下才会跳转到扫描页面
		if(!getIntent().getBooleanExtra("isUnLogin",true )&&getIntent().getBooleanExtra("isAsso", false)){
			startActivity(new Intent(this,CaptureActivity.class));
		}
		
		finish();
	}
}
