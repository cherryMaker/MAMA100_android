package com.mama100.android.member.activities.regpoint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.BaseActivity;


/**
 * @Description:显示积分失败信息 
 * @author created by liyang
 * @date  2012-11-19 下午2:46:00 
 */
public class RegPointFailureActivity extends BaseActivity {

	View call_menu_view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.regpoint_failure);

		setTopLabel(R.string.regpoint_failure_title);
		setRightButtonImage(R.drawable.selector_done);
		setLeftButtonVisibility(View.INVISIBLE);
		setBackgroundPicture(R.drawable.bg_wall);

		//获取本次操作的积分数,构建显示的字符串
		String point = getIntent().getStringExtra("productPoint");
		String text = "您本次操作积分+"
				+ point
				+ "未能成功！\n原因可能是" 
				+ getIntent().getStringExtra("desc")
				+ "。目前该积分记录处于待审核状态，客服人员会在2-3个工作日内为您处理，您也可以拨打免费客服电话人工积分。";

		// 显示积分失败的信息
		((TextView)findViewById(R.id.failure_info)).setText(text);
		
		
		//拨打电话
		findViewById(R.id.failure_call).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickCall("4008301055");
			}
		});
		
	}
	
	
	/**
	 * 点击完成按钮时执行
	 */
	@Override
	public void doClickRightBtn() {
		// TODO Auto-generated method stub
		super.doClickRightBtn();
		startActivity(new Intent(this, RegPointHomeActivity.class));
		finish();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	
	//弹出拨号menu
	public void onClickCall(final CharSequence charSequence){
		if(charSequence==null||charSequence.length()<=0){
			return;
		}
		final View view=View.inflate(getApplicationContext(), R.layout.call_menu, null);
		final Dialog call_menu = new AlertDialog.Builder(this).create();
		
		call_menu.show();
		call_menu.getWindow().setWindowAnimations(R.style.PopupAnimation);
		final Window window = call_menu.getWindow();
		final WindowManager.LayoutParams wl = window.getAttributes();
		// 根据x，y坐标设置窗口需要显示的位置
		// wl.x += x; //x小于0左移，大于0右移
		// wl.y +=heightPixels/2-280; //y小于0上移，大于0下移
		wl.y=0;
		wl.y += heightPixels/2-view.getHeight()/2;
		// 对话框宽度
		wl.width =widthPixels;
		window.setAttributes(wl);
		window.setContentView(view);
		
		((TextView)view.findViewById(R.id.tv_number)).setText(charSequence);
		view.findViewById(R.id.imgV_cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				call_menu.dismiss();
			}
		});
		view.findViewById(R.id.imgV_call).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//call directly
				final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+charSequence.toString().replace("-","")));
				startActivity(intent);
				call_menu.dismiss();
			}
		});
	}	
}
