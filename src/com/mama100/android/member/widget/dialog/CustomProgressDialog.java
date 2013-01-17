package com.mama100.android.member.widget.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.mama100.android.member.R;

public class CustomProgressDialog extends ProgressDialog{

	public CustomProgressDialog(Context context) {
		super(context);
	}
	
	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progressbar_1);
	}
	
	public static CustomProgressDialog show(Context ctx){
		CustomProgressDialog d = new CustomProgressDialog(ctx);
		d.show();
		return d;
	}
}