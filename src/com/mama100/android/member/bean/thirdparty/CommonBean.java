package com.mama100.android.member.bean.thirdparty;

import com.mama100.android.member.global.BasicApplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;



/**
 * @author edwar
 * @version 创建时间：2012-09-26 15:22
 * 公共Bean，子类：qqLogin,tencentWeibo, SinaWeibo  三种bean
 */
public class CommonBean {
	
////constant
	public static final int RES_SUCCESS=100;//授权成功后，WebViewActivity的返回码
	public static final int RES_REFUSE=101;//用户拒绝授权，WebViewActivity的返回码
	public static final int RES_FAILED=102;//其它原因授权失败后，WebViewActivity的返回码
	public static final String KEY_URL_DATA="data";//授权成功后，WebViewActivity返回数据的标志

	protected static final String XWEIBO_IS_FOLLOW_US="isFollowUs";
	
	protected static SharedPreferences shareprefencefile;
	
	public CommonBean() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param configFilename  本地文件名。。
	 */
	public CommonBean(String configFilename){
		shareprefencefile = BasicApplication.getInstance().getSharedPreferences(BasicApplication.getInstance().getMid()
				+'_'+configFilename,Context.MODE_PRIVATE);
	}
	
}
