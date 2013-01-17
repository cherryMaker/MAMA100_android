package com.mama100.android.member.widget;

import com.mama100.android.member.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 拓展的编辑框（带头部提示，TextView）。
 * 拓展的以下XML属性："tip_text"(头部TextView的值)，"hint_text"(EditText的hint)
 * "right_drawable"(最右边图片),"max_length"(最长字符数,整型)
 * Use it like this in xml:{@code tip_text="@string/value" }.
 * @author ecoo
 *
 */
public class EditTextEx extends LinearLayout {
	
	private boolean isEditable=true;
	private OnClickListenerEx mListener=null;
	private View layout;
	private EditText edt_field;
	private TextView tv_field;
	
	
	
	public static final String EX_XML_TIP_TEXT="tip_text";
	public static final String EX_XML_HINT_TEXT="hint_text";
	public static final String EX_XML_RIGHT_DRAWABLE="right_drawable";
	public static final String EX_XML_MAX_LENGTH="max_length";
	
	/*
	 * 由于setBackgroundResource方法(?)导致LinearLayout padding属性丢失
	 * 故在此提供一个属性，用于改变editTextEx的边框大小
	 */
	private int padding=0;
	
	public EditTextEx(Context context) {
		super(context);
		
		layout=View.inflate(context, R.layout.edittext_ex, null);
		edt_field=(EditText)layout.findViewById(R.id.edt_field);
		tv_field=(TextView)layout.findViewById(R.id.tv_field);
		
		this.setFocusable(true);
		this.setClickable(true);
//		this.setBackgroundResource(R.drawable.edt_field_normal);
		layout.setPadding(padding, padding, padding, padding);
		addView(layout,new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		setListener();
	}
	
	public EditTextEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		layout=((LayoutInflater)context.getSystemService
			      (Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.edittext_ex, null);

//		final View layout=View.inflate(context, R.layout.edittext_ex, null);
		edt_field=(EditText)layout.findViewById(R.id.edt_field);
		tv_field=(TextView)layout.findViewById(R.id.tv_field);
		
		final TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.EditTextEx); 
		
//		final int tip_txt_id = attrs.getAttributeResourceValue(null
//				, EX_XML_TIP_TEXT, 0);
//		final int hint_txt_id = attrs.getAttributeResourceValue(null
//				, EX_XML_HINT_TEXT, 0);
//
//        
//        
//		final int right_drawable_id = attrs.getAttributeResourceValue(null
//				, EX_XML_RIGHT_DRAWABLE, 0);
//		
//		final int max_length_id = attrs.getAttributeResourceValue(null
//				, EX_XML_MAX_LENGTH, 0);
		

		String tip_txt=type.getString(R.styleable.EditTextEx_tip_text);
        if (tip_txt!=null) {
        	tv_field.setText(tip_txt);
        } 
    	//xml没有"tip_text"属性，则为默认值
        else {
        }
        
		String hint_txt=type.getString(R.styleable.EditTextEx_hint_text);
        if (hint_txt!=null) {
        	edt_field.setHint(hint_txt);
        }
        //xml没有"hint_text"属性，则为默认值 
        else {
        }
		
		final Drawable right_drawable=type.getDrawable(R.styleable.EditTextEx_right_drawable);
        //xml没有"right_drawable"属性，不设置
        if(right_drawable!=null){
        	edt_field.setCompoundDrawablesWithIntrinsicBounds(null, null
        			,right_drawable , null);
        }
        
        
        //xml没有"max_length"属性，则不设置，默认为30
        final int max_length=type.getInteger(R.styleable.EditTextEx_max_length, -1);
        if(max_length>0){
        	edt_field.setFilters(new InputFilter[]{new InputFilter
        		.LengthFilter(max_length)});
        }


		this.setFocusable(true);
		this.setClickable(true);
//		this.setBackgroundResource(R.drawable.edt_field_normal);
//		layout.setPadding(padding,padding, padding, padding);
		addView(layout,new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		setListener();
	}
	

	private void setListener(){
		edt_field.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){

					if(!isEditable&&mListener!=null){
						mListener.onClick(EditTextEx.this);
					}
				}
			}
		});
		//可编辑时，模拟EditText的行为，焦点传递到EditText，并打开键盘
		super.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isEditable){
					edt_field.requestFocus();
					edt_field.setSelected(true);
					edt_field.performClick();
					final InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
					imm.showSoftInput(edt_field, InputMethodManager.RESULT_UNCHANGED_SHOWN);	
				}
				if(!isEditable&&mListener!=null){
					mListener.onClick(EditTextEx.this);
				}
			}
		});
		
		edt_field.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isEditable&&mListener!=null){
//					EditTextEx.this.performClick();
					mListener.onClick(EditTextEx.this);
				}
			}
		});
		
	
	}
	
	/**EditTextEx外部，不再建议使用此方法。已无效
	 * @see # setOnClickListener(OnClickListenerEx)
	 * @deprecated
	 */
	@Override
	public void setOnClickListener(OnClickListener listener){

	}
	
	/**
	 * 设置单击监听器。setEditable(false)之后才有效。
	 * @param listener
	 */
	public void setOnClickListener(OnClickListenerEx listener){
		mListener=listener;
	}
	
	public interface OnClickListenerEx
	extends View.OnClickListener{
	}
	
	/**
	 * 设置输入提示（出现在头部）
	 * @param resid
	 */
	public void setEditTip(int resid){
		tv_field.setText(resid);
	}
	/**
	 * 设置输入框背景文字
	 * @param resid
	 */
	public void setEditHint(int resid){
		edt_field.setHint(resid);
	}
	/**
	 * 设置输入提示（出现在头部）
	 * @param tip
	 */
	public void setEditTip(String tip){
		tv_field.setText(tip);
	}
	/**
	 * 设置输入框背景文字
	 * @param hint
	 */
	public void setEditHint(String hint){
		edt_field.setHint(hint);
	}
	
	/**
	 * 是否允许用户编辑
	 * @param editable
	 */
	public void setEditable(boolean editable){
		isEditable=editable;
		if(!editable){
			edt_field.setInputType(InputType.TYPE_NULL);
			edt_field.setFocusable(false);
			edt_field.setClickable(false);
			edt_field.setFocusableInTouchMode(true);
		}
		else{
			edt_field.setInputType(InputType.TYPE_CLASS_TEXT);
			edt_field.setFocusableInTouchMode(true);
		}
	}
	
	/**
	 * 
	 * @return EditText string value
	 */
	public String getEditTextStr(){
		return edt_field.getText().toString().trim();
	}
	/**set string value into EditText
	 * @param text set in EditText
	 */
	public void setEditTextStr(String text){
		edt_field.setText(text);
	}
	
	/**
	 * 
	 * @return 返回头部提示控件的实例（TextView）
	 */
	public TextView getTipTextview(){
		return tv_field;
	}
	
	/**
	 * EditTextEx类只提供了一些简单的操作，EditText其它众多操作还是需要通过此方法实现。
	 * @return 返回编辑框控件（EditText）,用于如设置输入类型等操作。
	 */
	public EditText getEditText(){
		return edt_field;
	}
	
	/**
	 * 由于setBackgroundResource方法(?)导致LinearLayout padding属性丢失
	 * 故在此提供一个属性，用于改变editTextEx的边框大小
	 * @param padding padding in pixels,default:0
	 */
	public void setPadding(int padding){
		this.padding=padding;
		layout.setPadding(padding,padding, padding, padding);
		
	}
	
	/**
	 * EditTextEx外部，不再建议使用此方法。
	 * @see #setPadding(int)
	 * @deprecated
	 */
	@Override
	public void setPadding(int left, int top, int right, int bottom){
		super.setPadding(left, top, right, bottom);	
	}

	
	

	
}
