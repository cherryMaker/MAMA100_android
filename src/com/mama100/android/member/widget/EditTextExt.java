package com.mama100.android.member.widget;

import com.mama100.android.member.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextExt extends EditText {
	
	private String label = "";
	private int label_text_size = 24;

	
	
	public EditTextExt(Context context, AttributeSet attrs) {
		super(context, attrs);
		final TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.EditTextExt);
		label=type.getString(R.styleable.EditTextExt_label);
		setPadding(label.length()*label_text_size, 0, 0, 0);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		//设置文本的尺寸
		paint.setTextSize(label_text_size);
		//设置文本的颜色
		paint.setColor(Color.GRAY);
		//绘制文本,参数 22 - x, y - getHeight()/2+5
		canvas.drawText(label, 22, getHeight()/2+5, paint);
	}
	
	

}
