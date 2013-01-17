package com.mama100.android.member.widget;

import com.mama100.android.member.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;


/**
 * 
 * @author aihua 2012-03-07
 * 该类主要目的是，模仿iphone允许用户在打开输入法软键盘的时候，
 * 随意点击屏幕非控件空白，即可关闭输入法软键盘
 *
 */
public class HideLinearLayout extends LinearLayout {
	
	//一个一直为gone的内应组件，是调用关闭输入软键盘必备的参数。
	private EditText mEditText;

	public HideLinearLayout(Context context) {
		super(context);
	}
	
	public HideLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
			}
		});
	}
	
	//在这里，隐藏软键盘。
	protected void hide() {
		InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}


	/**
	 * 根据view类的调用方法顺序，onMeasure()->onLayout()->onDraw()
	 * 因为本控件主要目的就是在LinearLayout最下面，增加一个一直隐藏的EditText,
	 * 所以调用onFinishInflate(),在这里写我们的逻辑最好，既可以不影响界面布局，也不会
	 * 造成新一轮的view方法调用，节省内耗。
	 */

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//设置垂直布局
		this.setOrientation(LinearLayout.VERTICAL);
		//渲染画布，注意inflate()第三个参数viewGroup root 这里为null,
		//因为在本view里面定义，默认本view就是父类，就是root. 可能在非view类里面定义时，要设置
		View v = View.inflate(getContext(), R.layout.basic_hide_software_input,null);
		//初始化内应EditText
		mEditText  = (EditText) v.findViewById(R.id.hide_software_input);
		//添加组件
		this.addView(v);
		//要求布局。因为EditText显示属性是gone，不会造成布局的改变。所以不需要执行。
//		requestLayout();
		//倒一盆脏水
		invalidate();
	}
	
	
	
	

	
	
	

}
