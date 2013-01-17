package com.mama100.android.member.widget.scrollview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


/**
 * 允许首先让子控件处理滚动事件
 * @author eco
 *
 */
public class ScrViewEventToChild extends ScrollView{

	public ScrViewEventToChild(Context context) {
		this(context , null);
	}
	
	public ScrViewEventToChild(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //System.out.println("MyScrollView-->onInterceptTouchEvent");
        return false;
    }


}
