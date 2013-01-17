package com.mama100.android.member.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mama100.android.member.R;

/**
 * 
 * 帮助界面弹出框
 * @author mico
 *
 */
public class SureDialog extends Dialog {
	private LayoutInflater inflater;
	private Activity activity;
	

	public SureDialog(Activity activity) {
		super(activity, R.style.act_detail_dialog);
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
	}

	public void showDialog(int resid) {
		ImageView imageview = (ImageView) inflater.inflate(
				R.layout.attend_act_dialog, null);
		imageview.setImageResource(resid);
		imageview.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		setContentView(imageview);
		
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		
		Window window = getWindow();
		
		WindowManager.LayoutParams winlp = window.getAttributes();
		winlp.width = (int) (display.getWidth() * 0.9);
		winlp.dimAmount = 0.7f; // 背景灰
		//winlp.alpha=0.8f;
		window.setAttributes(winlp);
		window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		show();
	}
}