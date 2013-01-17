package com.mama100.android.member.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;

/**
 *  异步处理MktBaseReq请求，返回MktBaseRes，并于handleRespone方法中更新UI等。
 * 同时，封装了等待提示框。
 * @author eCoo
 */
public abstract class AsyncReqTaskWithMessage 
extends AsyncTask<BaseReq, Void, BaseRes>  {
	//进度框
	protected ProgressDialog mProgressDialog;
	protected Context mContext;
	public AsyncReqTaskWithMessage(Context context) {
		this.mContext = context;
	}
	@Override
	protected BaseRes doInBackground(BaseReq... arg0) {
		
		return doRequest(arg0[0]);
	}
	
	public void onPostExecute(final BaseRes response) {
//		if(mContext instanceof Activity){
//			((Activity)mContext).runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					handleResponse(response);
//					return;
//				}
//			});
//		}
		handleResponse(response);

	}
	/**子类处理请求，此方法实现耗时的操作
	 * @param request 请求
	 * @return MktBaseRes 返回响应，由handleRespone处理
	 */
	protected abstract BaseRes doRequest(BaseReq request);
	/**子类处理响应，此方法内可以更新UI等
	 * @param response 来自doRequest的响应
	 */
	protected abstract void handleResponse(BaseRes response);

	
	/**
	 * 显示进度对话框 
	 * @param Message 对话框信息
	 */
	public void displayProgressDialog(int Message) {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
//		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		mProgressDialog.setTitle(mContext.getResources()
//				.getString(R.string.main_act_tv_friendly_tips));
		mProgressDialog.setMessage(mContext.getResources().getString(Message));
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();

	}

	/**
	 * <p>
	 * @Title: closeProgressDialog
	 * @Description:关闭进度对话框
	 */
	public void closeProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = null;
	}
}
