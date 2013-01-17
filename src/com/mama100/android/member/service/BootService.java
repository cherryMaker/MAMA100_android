/**
 * 
 */
package com.mama100.android.member.service;

import java.io.File;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.mama100.android.member.R;
import com.mama100.android.member.bean.MobResponseCode;
import com.mama100.android.member.businesslayer.SystemDataManager;
import com.mama100.android.member.domain.sys.CheckAppVerReq;
import com.mama100.android.member.domain.sys.CheckAppVerRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.SDCardUtil;

/**
 * <p>
 * Description: InitialService.java 在系统开始的时候就启动，职责是在后台处理一些通用的服务，比如： 网络连接检查，
 * 软件版本检查及下载， 数据库数据版本检查及下载， sdcard创建文件夹存放临时apk文件。。 在系统退出的时候关闭该service
 * </p>
 * 
 * @author aihua.yan 2011-10-27
 * 
 * 补充：added by aihua edwar , 2012-11-06.. 系统优化
 * 为了杜绝长引用，故不仅系统退出时要关闭该服务，当该服务执行完毕的时候，
 * 也应该要么自动关闭自身，要么通知前台去关闭它。。(发现之前已经优化了，不用修改)
 * 
 */
public class BootService extends Service {

	private String TAG = this.getClass().getSimpleName();

	private UpdateSoftwareHandler serviceHandler;

	private ApkAutoUpdater apkUpdater;

	private Activity mActivity;
	
	//判断是否需要更新软件版本，如果需要，则不进行数据版本检查
	private boolean isSoftwareNeedUpdate;//确保每次更新数据之前，版本已经是最新的

//	private CommonService commonService;
	
	
	private int serviceStartId = 0;
	
	private downloadApkThread apkThread;

	/** 1- 本方法用于启动LoginActivity时，后台自动检查更新软件版本。。
	 * onBind，当调用者退出时，因为是与调用者绑定的，所以该service也一并退出
	 * 或者也可以通过unBind来实时退出
	 * 目前这个没有用到。。而且，以后也不建议用bind,可能会造成长引用，代码控制要求高。
	*/
	@Override
	public IBinder onBind(Intent intent) {
		LogUtils.logi(TAG,"Bind start");
//		waitForHandlerSet();
		return null;
	}


	/**2- 本方法用于在菜单里或者系统设置里面的更新软件版本和数据库版本。。
	 * onStart，当调用者Activity退出或关闭时，该service不会一起退出，是独立的
	 * 也可以通过stopSelf,stopIntent来停止
	 */
	//用ontartCommand方法， onStart已经@Deprecated掉了。
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.logi("BootService", "Started by received start id " + startId +",Received flags - " + flags + ", intent -" + ": " + intent);
		serviceStartId = startId;
		//更新上下文为当前活动的对象。
		if (BasicApplication.getInstance().getCurrentActivity() != null) {
			mActivity = BasicApplication.getInstance().getCurrentActivity();
		}
		//更新Handler为当前活动的对象。
			serviceHandler = new UpdateSoftwareHandler(mActivity);
		// 2,检查软件版本
		doCheckSoftwareVersion();	
		return START_NOT_STICKY;//进程被杀，不再涅槃
	}
	

	private void doCheckSoftwareVersion() {
		LogUtils.loge(TAG, "APK CHECK START");
		apkThread = new downloadApkThread();
		apkThread.start();
	}

	private void doCreateFolderInSdcard() {
		/**
		 * 向sdcard写文件夹
		 */
		try {
			LogUtils.logi(TAG, "Start Write in Sdcard");
			// 1.判断是否存在sdcard
			if (SDCardUtil.isSDCardExist()) {
				// 目录
				File path = new File(SDCardUtil.getApkPath());
				// 文件
				if (!path.exists()) {
					// 2.创建目录，可以在应用启动的时候创建
					path.mkdirs();
				}
			}
			else {
				String status = SDCardUtil.checkAndReturnSDCardStatus();
				Toast.makeText(getApplicationContext(), ""+status, Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			LogUtils.loge(TAG, LogUtils.getStackTrace(e));
		}
	}

	/**
	 * 启用线程下载最新软件版本
	 */
	private class downloadApkThread extends Thread {

		@Override
		public void run() {
			if(serviceHandler!=null)
			serviceHandler.obtainMessage(AppConstants.CHECK_APP_VERSION).sendToTarget();
			
			// 初始化软件更新器
			apkUpdater = ApkAutoUpdater.getInstance();
			apkUpdater.setContext(mActivity);

			// 为apkUpdate提供Looper
			if (Looper.myLooper() == null)
			Looper.prepare();

			LogUtils.logi(TAG, "downloadApkThread-Thread Id: "
					+ Thread.currentThread().getId() + "");
		
			SystemDataManager sysManager = SystemDataManager
					.getInstance(mActivity);
			CheckAppVerRes mAppVerRes = sysManager
					.checkAppVersion(new CheckAppVerReq());
			if (mAppVerRes == null) {
				serviceHandler.obtainMessage(AppConstants.CHECK_APP_VERSION_EXCEPTION).sendToTarget();
				BasicApplication.getInstance().showLongToast("获取服务器版本号出错，请检查网络情况。");
				stopThisService();
				return;
			}

			// 1,找到比当前更新的版本， 返回MobResponseCode.SUCCESS
			if (mAppVerRes.getCode().equals(MobResponseCode.SUCCESS)) {
				if (mAppVerRes.getNver().equals("")) {
					serviceHandler.obtainMessage(AppConstants.CHECK_APP_VERSION_EXCEPTION).sendToTarget();
					BasicApplication.getInstance().showLongToast("服务器版本号为空");
					stopThisService();
					return;
				}
				// 服务器的最新版本号码
				// int serverVersion = Integer.parseInt(mAppVerRes.getNver());
				float serverVersion = Float.parseFloat(mAppVerRes.getNver());
				// 本地的版本号
				float localVersion = BasicApplication.getInstance().getLocalVersion();
				LogUtils.logi(TAG, "local ver: " + localVersion
						+ " server ver: " + serverVersion);

				//edwar  2012-06-21, 服务器端已经比较过版本了，本地再进行一次比较，双保险
				isSoftwareNeedUpdate = false;
				if (localVersion < serverVersion) {
					isSoftwareNeedUpdate = true;
				}
				// 检查sd卡是否存在
				if (SDCardUtil.isSDCardExist()) {
					doCreateFolderInSdcard();
					if (isSoftwareNeedUpdate) {//如果确实需要更新版本
						serviceHandler.obtainMessage(AppConstants.CHECK_APP_VERSION_FINISH).sendToTarget();
						downloadApp(mAppVerRes);
					} else {
						deleteDownloadedApp();//这句其实有点多余，因为这个时候新app还没有下载。
						stopThisService();
					}
				} else {
					BasicApplication.getInstance().showLongToast(R.string.sdcard_is_unmounted);
					LogUtils.logi(TAG, "存储卡不可读");
					serviceHandler.obtainMessage(AppConstants.NO_SDCARD_WARNING).sendToTarget();
					stopThisService();
			}
			}
			else {
				// 2,找不到比当前更新的版本， 返回MobResponseCode.NEW_VER_NOT_FOUND
				//这里的fail不是传统意义上的，而是值本地版本已经最新
				if(serviceHandler!=null)
				serviceHandler.obtainMessage(AppConstants.UPDATE_APK_FAIL,
						mAppVerRes.getDesc()).sendToTarget();
				stopThisService();
			}

				Looper.loop();
				if (Looper.myLooper() != null) {
					Looper.myLooper().quit();
				}
		}

	}
	
	


	/**
	 * 显示软件升级提示对话框
	 * 
	 * @param checkResult
	 * @description  是否要升级软件
	 */
	private void downloadApp(CheckAppVerRes data) {
		String apkUrl = data.getAppurl();
		LogUtils.logi(TAG, "update apkUrl: " + apkUrl);
//		apkUpdater.update(apkUrl, data.getDesc());
		apkUpdater.update(apkUrl, data.getCtnt()); //服务器获取新的软件描述
		//edwar 2012-06-21 用户如果取消安装最新版本，则直接显示登录界面。。不会建议去更新最新数据库。
		enterIntoLoginPage();
		stopThisService();
	}

	public void stopThisService() {
		LogUtils.logi(TAG, "Stop This Service with StartId " + serviceStartId);
		stopSelf();
	}


	public void enterIntoLoginPage() {
		// 显示登陆界面
		serviceHandler.obtainMessage(AppConstants.START_DISPLAY_MAIN_UI)
				.sendToTarget();
	}



	/**
	 * 清理工作, 删除旧的下载的临时文件
	 */
	private void deleteDownloadedApp() {
		apkUpdater.delFile();
	}

	/*
	 * <p> onRebind </p>
	 */
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		LogUtils.logi(TAG,"Rebind starts");
	}

	/*
	 * <p> onUnbind </p>
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		if(apkThread!=null){
			apkThread.interrupt();
			apkThread = null;
		}
		
		LogUtils.logi(TAG,"Unbind bootService, 退出整个程序");
		stopSelf();
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		serviceHandler.clearMemory();
		//不能清空apkUpdater,因为Service率先关闭了。。而用户还没有点击下载
//		apkUpdater.clearMemory();
		if(apkThread!=null){
			apkThread.interrupt();
			apkThread = null;
		}
		serviceHandler = null;
		apkUpdater = null;
		mActivity = null;
		LogUtils.logi(TAG,"destory bootService");
		stopSelf();
	}
	

}
