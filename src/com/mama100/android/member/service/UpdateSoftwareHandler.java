package com.mama100.android.member.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.HomePageActivity;
import com.mama100.android.member.activities.SplashActivity;
import com.mama100.android.member.global.AppConstants;

/**
 * 
 * 一个公共handler对象。 比如，在主界面点击更新数据版本，与系统启动时更新数据版本，对应的handler对象隶属于两个不同的activity,
 * 一个是loginActivity，一个是MainActivity,且这两个不继承同一个父类。 可能发生这种情况：
 * 在MainActivity里点击"更新数据版本"时， UI提示滚动条却发生在loginActivity里面，因为，handler还是旧的。
 * 所以这里将handler提取出来，脱离具体某个Activity,或许整个系统只有一个handler,都有可能。
 * 
 * @author edwar
 */

public class UpdateSoftwareHandler extends Handler {

	// 当前活动的activity
	private Activity currentActivity; // 不能用Context,只有活动才能添加窗体。
	// 2012-05-07,用于软件数据更新
	ProgressDialog mDialog;

	// 判断是否需要弹出提示框。如果在SplashActivity,就不要
	private boolean isNeedToast = true;

	public UpdateSoftwareHandler(Activity mActivity) {
		if (mActivity != null) {
			this.currentActivity = mActivity;
			if (mActivity instanceof HomePageActivity) {
				isNeedToast = false;
			}
			mDialog = new ProgressDialog(mActivity);
		} else {
			// TODO 如果传入的活动为空，具体操作
		}
	}

	@Override
	public void handleMessage(Message msg) {

		switch (msg.what) {

		case AppConstants.START_CHECK_NET:// 后台开始检查网络
			buildupDialog(R.string.checking_network);
			break;

		/**************************************************************
		 * 检查版本更新
		 **************************************************************/
		case AppConstants.CHECK_APP_VERSION:// 后台检查软件版本
			buildupDialog(R.string.checking_app_version);
			break;
		case AppConstants.UPDATE_APK_FAIL:
			if (isNeedToast)
				Toast.makeText(currentActivity, (CharSequence) msg.obj,
						Toast.LENGTH_LONG).show();
			// 本地软件已经是最新版本
			buildupDialog(R.string.latest_app_version);
			this.obtainMessage(AppConstants.FINISH_NO_DATA_UPDATE)
					.sendToTarget();// 关闭dialog
			break;

		case AppConstants.NO_SDCARD_WARNING:
			Toast.makeText(currentActivity, R.string.no_sdcard_warning,
					Toast.LENGTH_LONG).show();
			// 本地SD卡不存在
			buildupDialog(R.string.no_sdcard_warning);
			this.obtainMessage(AppConstants.FINISH_NO_DATA_UPDATE)
					.sendToTarget();// 关闭dialog
			break;

		case AppConstants.CHECK_APP_VERSION_FINISH:// 软件版本检查完毕
			cleanDialog();
			break;
		case AppConstants.CHECK_APP_VERSION_EXCEPTION:// 软件版本检查报错
			cleanDialog();
			break;

		/**************************************************************
		 * 检查数据更新，现在用不到
		 **************************************************************/
		case AppConstants.START_CHECK_DATA:// 后台开始检查模块数据
			buildupDialog(R.string.checking_data_version);
			break;
		case AppConstants.NO_DATA_NEED_UPDATE:// 数据版本已经最新
			buildupDialog(R.string.latest_data_version);
			this.obtainMessage(AppConstants.FINISH_NO_DATA_UPDATE)
					.sendToTarget();// 关闭dialog
			Toast.makeText(currentActivity, R.string.latest_data_version,
					Toast.LENGTH_SHORT).show();
			break;

		case AppConstants.UPDATING_DATA:// 后台正在更新模块数据
			buildupDialog(R.string.updating_module_data);
			break;
		case AppConstants.UPDATE_DATA_SUCCESSFUL:// 后台更新模块数据成功
			buildupDialog(R.string.update_module_data_success);
			break;
		case AppConstants.FINISH_CHECK_DATA:// 后台更新模块数据完毕
			if (mDialog != null)
				mDialog.cancel();
			mDialog = null;
			// Toast.makeText(currentActivity,
			// R.string.update_module_data_success, Toast.LENGTH_SHORT).show();
			break;
		case AppConstants.FINISH_NO_DATA_UPDATE:// 后台完毕
			cleanDialog();
			break;
		default:
			super.handleMessage(msg);
		}

	}

	public void cleanDialog() {
		if (mDialog != null)
			mDialog.cancel();
		mDialog = null;
	}
	
	//
	public void clearMemory(){
		cleanDialog();
		currentActivity = null;
	}

	public void buildupDialog(int id) {
		//如果不是主界面，才需要弹出对话框
		if (isNeedToast) {
			if (mDialog == null) {
				mDialog = new ProgressDialog(currentActivity);
			}
			String str = currentActivity.getResources().getString(id);
			mDialog.setMessage(str);
			if (!mDialog.isShowing())
				mDialog.show();
		}
	}
}
