package com.mama100.android.member.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.mama100.android.member.R;
import com.mama100.android.member.bean.MobResponseCode;
import com.mama100.android.member.bean.update.UpdateApkResult;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.NetworkUtils;
import com.mama100.android.member.util.SDCardUtil;
import com.mama100.android.member.util.StorageUtils;

/**
 * APK自动更新器
 * 
 * @author Jimmy , update by aihua.yan 2011-10-30
 * @version 1.1
 */
public class ApkAutoUpdater {

	private String LOG_TAG = this.getClass().getSimpleName();

	private Context context = null;

	private String apkUrl = "";

	private ProgressDialog dialog;

	// 文件存储
	private File updateDir = null;
	private File updateFile = null;

	private boolean isDownloading = false;

	private static ApkAutoUpdater instance;

	// Handlers
	private ProgressHandler progressHandler;
	private UpdateHandler updateHandler;

	// added by aihua.yan
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private static final int NOTIFICATION_EXPANED_VIEW_TITLE = R.id.notification_title;
	private static final int NOTIFICATION_EXPANED_VIEW_LABEL = R.id.notification_label;
	private static final int NOTIFICATION_EXPANED_VIEW_VALUE = R.id.notification_value;
	private static final int DOWNLOAD_APP = R.string.app_download_start;
	private static final int DOWNLOAD_ONGOING = R.string.app_download_ongoing;
	private static final int DOWNLOAD_SUCCESS = R.string.app_download_success;
	private static final int DOWNLOAD_FAILED = R.string.app_download_failed;
	private static final int DOWNLOAD_FAILED_REASON = R.string.app_downloading_toast_failed;//没用到
	private static final int DOWNLOAD_BOOST = R.string.app_downloading_toast_boost;
	private static final int NOTIFICATION_FIRST = 1001;

	public String tickerText = "";

	private int READ_TIME = 60*1000;//读文件时间

	private int CONNECT_TIME = 20*1000;//连接时间
	
	
	/************************************************
	 * 格式化str 用到的 应用名称 变量，在下载新的apk时及显示通知栏 用到
	 ************************************************/
	private static final String appname = BasicApplication.getInstance().getResources().getString(R.string.app_name);
	
	private ApkAutoUpdater() {
		
		LogUtils.logi(LOG_TAG, " >>>> ApkAuotUpdater runs in Thread -- : " + Thread.currentThread().getId());

//		updateDir = new File(Environment.getExternalStorageDirectory(),
//				BasicApplication.getInstance().getMama100Dir());
		updateDir = new File(SDCardUtil.getApkPath());
		updateFile = new File(updateDir.getPath(), BasicApplication.getInstance().getApkName() + ".apk");
		// 创建Handler
		HandlerThread apkUpdateThread = new HandlerThread(LOG_TAG,
				Process.THREAD_PRIORITY_BACKGROUND);
		apkUpdateThread.start();
		Looper mLooper = apkUpdateThread.getLooper();
		progressHandler = new ProgressHandler(mLooper);
		updateHandler = new UpdateHandler(mLooper);
	}

	public static ApkAutoUpdater getInstance() {
		if (instance == null) {
			instance = new ApkAutoUpdater();
		}
		return instance;
	}
	
	
	//不需要这个方法，不稀罕这点内存的释放。。而且如果在service的onDestroy()方法直接释放，会导致ApkUpdater没法下载了。
	//因为context = null 了。
//	public void clearMemory(){
//		context = null;
//		
//		updateDir = null;
//		
//		if(dialog!=null){
//			dialog.cancel();
//			dialog = null;
//		}
//
//		updateDir = null;
//		updateFile = null;
//		progressHandler=null;
//		updateHandler=null;
//
//		// added by aihua.yan
//		mNotificationManager=null;
//		mNotification=null;
//		
//	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void update(String apkUrl, String desc) {

		if (isDownloading) {
			Toast.makeText(context,
					BasicApplication.getInstance().getXmlString(R.string.app_download_wait_tip),
					Toast.LENGTH_LONG).show();
			return;
		}

		this.apkUrl = apkUrl;

		if (NetworkUtils.isNetworkAvailable(this.context) == false) {
			Toast.makeText(context,
					BasicApplication.getInstance().getXmlString(R.string.unavailable_network),
					Toast.LENGTH_LONG).show();
			return;
		}

		showUpdateDialog(desc);

	}

	private void showUpdateDialog(String desc) {

		@SuppressWarnings("unused")
		AlertDialog alert = new AlertDialog.Builder(this.context)
				.setTitle(BasicApplication.getInstance().getXmlString(R.string.app_update_notification))
				// .setIcon(R.drawable.icon)
				.setMessage(desc)
				.setPositiveButton(
						BasicApplication.getInstance().getXmlString(R.string.confirm_update),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								downloadTheFile(apkUrl);
								showWaitDialog();
								//暗示用户习惯是积极更新，下次进主页还会继续给他更新软件
//								StorageUtils.setBooleanShareValue(context, AppConstants.IS_WANTTO_DOWNLOAD, true);
							}
						})
				.setNegativeButton(BasicApplication.getInstance().getXmlString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								//TODO 这里用户版本不是最新，但是选择不安装最新的时候。系统也不让他去更新最新的数据库。
								//因为容易出现新版本的数据库对应新版本的系统，所谓高版本数据匹配低版本app，会出问题。
								dialog.cancel();
								//暗示用户习惯不想更新，下次进主页不会给他更新软件
//								StorageUtils.setBooleanShareValue(context, AppConstants.IS_WANTTO_DOWNLOAD, false);
							}
						}).show();

	}

	private void showWaitDialog() {
		 dialog = new ProgressDialog(context);
		 dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		 dialog.setMessage("正在更新软件,请稍等...");
		 dialog.setIndeterminate(false);
		 dialog.setCancelable(true);
		 dialog.show();

		// added by aihua.yan
		updateOnPreExecute();

	}

	private void updateOnPreExecute() {

		// 1. get Notification Manager
		String ns = context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) context
				.getSystemService(ns);
		// 2. Instantiate the Notification
		int icon = R.anim.download_animation;
		tickerText = context.getResources().getString(
				R.string.app_download_start);
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);
		// 3.set the contentView
		mNotification.contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification);
		String str = BasicApplication.getInstance().getResources().getString(DOWNLOAD_BOOST);
		String str2 = String.format(str, appname);
		BasicApplication.getInstance().showLongToast(DOWNLOAD_BOOST);

	}

	private void updateOnProgressUpdate(int integer) {

		setExpandedView(NOTIFICATION_EXPANED_VIEW_TITLE, DOWNLOAD_APP);

		if (integer == 100) {
			mNotification.tickerText = context.getResources().getString(
					R.string.app_download_success_tip);
			setExpandedView(NOTIFICATION_EXPANED_VIEW_LABEL, DOWNLOAD_SUCCESS);
			setViewVisibility(NOTIFICATION_EXPANED_VIEW_VALUE, View.INVISIBLE);
			if(dialog!=null){
				if(dialog.isShowing()){
					dialog.cancel();
					dialog = null;
				}
			}
		} else {
			setExpandedView(NOTIFICATION_EXPANED_VIEW_LABEL, DOWNLOAD_ONGOING);
			setViewVisibility(NOTIFICATION_EXPANED_VIEW_VALUE, View.VISIBLE);
			mNotification.contentView.setTextViewText(R.id.notification_value,
					"" + integer + "%");
		}

		mNotification.contentView.setProgressBar(R.id.ProgressBar01, 100,
				integer, false);

		// 准备安装新的版本软件
		if (integer == 100) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			String type = getMIMEType(updateFile);
			intent.setDataAndType(Uri.fromFile(updateFile), type);
			// PendingIntent
			mNotification.contentIntent = PendingIntent.getActivity(context,
					R.string.apk_name, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			String tickerText = context.getResources().getString(
					DOWNLOAD_SUCCESS);

			// 高亮色 "<系统名>"
			// 改变字体颜色
			String bbt = context.getResources().getString(R.string.app_name);
			int start = tickerText.indexOf(bbt);
			int end = tickerText.indexOf(BasicApplication.getInstance().getXmlString(R.string.app));
			SpannableStringBuilder style = new SpannableStringBuilder(
					tickerText);
			style.setSpan(new TextAppearanceSpan(context,
					R.style.mama100textstyle), start, end,
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			mNotification.icon = R.drawable.app_icon;
			mNotification.setLatestEventInfo(context, style, "",
					mNotification.contentIntent);
		} else {
			mNotification.contentIntent = PendingIntent.getActivity(context, 0,
					new Intent(), 0);
//			mNotification.contentIntent = PendingIntent.getActivity(context, 0,
//					null, 0);
		}

		// 是否在点击一次以后，自动清除安装通知
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(NOTIFICATION_FIRST, mNotification);

		// 手动清除通知
		// mNotificationManager.cancel(NOTIFICATION_FIRST);

	}

	private void setViewVisibility(int viewId, int visibility) {
		mNotification.contentView.setViewVisibility(viewId, visibility);
	}

	private void setExpandedView(int widgetId, int StringId) {
		mNotification.contentView.setTextViewText(widgetId, context
				.getResources().getString(StringId));

	}

	private void downloadTheFile(final String apkUrl) {

		try {

			if (isDownloading) {
				return;
			}

			Runnable r = new Runnable() {

				private UpdateApkResult result = new UpdateApkResult();

				public void run() {
					
					LogUtils.logi(LOG_TAG, " >>>> downloadFile runs in Thread -- : "
							+ Thread.currentThread().getId());
					try {

						isDownloading = true;
						doDownloadTheFile(result, apkUrl);

					} catch (Exception e) {

						result.setCode(MobResponseCode.SYSTEM_EXCEPTION);
						result.setDesc(BasicApplication.getInstance()
								.getXmlString(R.string.system_exception));
						LogUtils.loge("ApkAutoUpdater-downloadTheFile", LogUtils.getStackTrace(e));
					} finally {
						isDownloading = false;
					}

					Message msg = new Message();

					Bundle bundle = new Bundle();
					bundle.putSerializable("result", result);

					msg.setData(bundle);

					updateHandler.sendMessage(msg);

				}
			};

			new Thread(r).start();

		} catch (Exception e) {
			LogUtils.loge("ApkAutoUpdater-downloadTheFile", LogUtils.getStackTrace(e));
		}

	}

	private void doDownloadTheFile(UpdateApkResult result, String apkUrl)
			throws Exception {

		// LogUtils.logi(LOG_TAG, "doDownloadTheFile...");

		if (!URLUtil.isNetworkUrl(apkUrl)) {
			result.setCode(MobResponseCode.VALIDATE_ERROR);
			result.setDesc(BasicApplication.getInstance().getXmlString(R.string.validate_error));
			return;
		}

		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream fos = null;

		int apkTotalSize = 0;

		try {

			if (!updateDir.exists()) {
				updateDir.mkdirs();// /sdcard/mama100
			}

			if (!updateFile.exists()) {
				updateFile.createNewFile(); // //sdcard/mama100_data/mama100_bbt.apk
				// 拔了数据线，否则sdcard不能打开
			}

			LogUtils.logi(LOG_TAG, "start connect");

			URL myURL = new URL(apkUrl);
			conn = (HttpURLConnection) myURL.openConnection();

			conn.setConnectTimeout(CONNECT_TIME );
			conn.setReadTimeout(READ_TIME );
			

			apkTotalSize = conn.getContentLength();
			LogUtils.logi(LOG_TAG, "get  apkSize is : " + apkTotalSize);
			// BasicApplication.getInstance().showShortToast( "apkTotalSize: " + apkTotalSize);

			if (conn.getResponseCode() == 404) {
				result.setCode(MobResponseCode.SERVER_404);
				result.setDesc(BasicApplication.getInstance().getXmlString(R.string.server_404_error)
						+ " - " + result.getCode());
				return;
			}

			is = conn.getInputStream();
			LogUtils.logi(LOG_TAG, "getInputStream : ");
			if (is == null) {
				throw new RuntimeException("stream is null");
			}

			// LogUtils.logi(LOG_TAG, updateFile.getAbsolutePath());
			fos = new FileOutputStream(updateFile);

			LogUtils.logi(LOG_TAG, "updateFile is : " + updateFile);

			int downloadedSize = 0;
			int downloadedCount = 0;

			byte buf[] = new byte[4096];

			do {

				int numread = is.read(buf);
				if (numread <= 0) {
					break;
				}

				fos.write(buf, 0, numread);

				downloadedSize += numread;

				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadedSize * 100 / apkTotalSize) - 10 >= downloadedCount) {

					downloadedCount += 10;
					LogUtils.logi(LOG_TAG, "downloadedCount is " + downloadedCount);

					if (downloadedCount <= 100) {
						LogUtils.logi(LOG_TAG, "progress is " + downloadedCount);
						progressHandler.obtainMessage(downloadedCount)
								.sendToTarget();
					}

					// LogUtils.logi(LOG_TAG, progressInfo);
				}

			} while (true);

			// LogUtils.logi(LOG_TAG, "Download  ok...");

			result.setApkTotalSize(apkTotalSize);
			result.setCode(MobResponseCode.SUCCESS);

		} catch (SocketTimeoutException e) {

			result.setCode(MobResponseCode.SOCKET_TIMEOUT);
			result.setDesc(BasicApplication.getInstance().getXmlString(R.string.socket_timeout_error));

		} catch (SocketException e) {

			result.setCode(MobResponseCode.CONNECT_TIMEOUT);
			result.setDesc(BasicApplication.getInstance().getXmlString(R.string.connect_timeout_error));

		} finally {

			if (dialog != null) {
				dialog.cancel();
			}

			if (conn != null) {
				conn.disconnect();
			}

			if (is != null) {
				is.close();
			}

			if (fos != null) {
				fos.close();
			}

		}

	}

	private class UpdateHandler extends Handler {

		/**
		 * 依赖注入mLooper
		 */
		public UpdateHandler(Looper mLooper) {
			super(mLooper);
			LogUtils.logi(LOG_TAG, " >>>> updateHandler() runs in Thread -- : " + Thread.currentThread().getId());
		}

		@Override
		public void handleMessage(Message msg) {
			LogUtils.logi(LOG_TAG, " >>>> updateHandler handlemessage runs in Thread -- : " + Thread.currentThread().getId());
			Bundle bundle = msg.getData();
			UpdateApkResult result = (UpdateApkResult) bundle
					.getSerializable("result");

			if (!result.getCode().equals(MobResponseCode.SUCCESS)) {
				Toast.makeText(context, result.getDesc(), Toast.LENGTH_LONG)
						.show();
				return;
			}

			// 弹出"安装最新宝宝通软件"对话框
			showInstallDialog(BasicApplication.getInstance().getXmlString(R.string.app_install_tip));
		}
	};

	private void showInstallDialog(String desc) {

		@SuppressWarnings("unused")
		AlertDialog alert = new AlertDialog.Builder(this.context)
				.setTitle(BasicApplication.getInstance().getXmlString(R.string.app_name))
				// .setIcon(R.drawable.icon)
				.setMessage(desc)
				.setPositiveButton(BasicApplication.getInstance().getXmlString(R.string.confirm),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								//暗示用户习惯是积极更新，下次进主页还会继续给他更新软件
//								StorageUtils.setBooleanShareValue(context, AppConstants.IS_WANTTO_DOWNLOAD, true);
								install(updateFile);
							}
						})
				.setNegativeButton(BasicApplication.getInstance().getXmlString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								//暗示用户习惯不想更新，下次进主页不会给他更新软件
//								StorageUtils.setBooleanShareValue(context, AppConstants.IS_WANTTO_DOWNLOAD, false);
//								delFile(); //将刚刚下载的新的apk删掉，还是保留，留给用户自己手动安装呢？
								/**
								 * ps.这里还是不删apk，因为通知栏里有通知安装，如果这里删了，那边再点通知就报“解析包错误”，
								 * 还不如不删。。要么，你删的时候，就要关闭那个通知。。这里，还是留给用户自己关闭好。。
								 */
								
								dialog.cancel();
								
							}
						}).show();

	}

	private class ProgressHandler extends Handler {

		/**
		 * 依赖注入
		 */
		public ProgressHandler(Looper mLooper) {

			super(mLooper);
			LogUtils.logi(LOG_TAG, " >>>> progressHandler() runs in Thread -- : " + Thread.currentThread().getId());
		}

		@Override
		public void handleMessage(Message msg) {
			LogUtils.logi(LOG_TAG, " >>>> progressHandler handleMessage runs in Thread -- : " + Thread.currentThread().getId());
			updateOnProgressUpdate(msg.what);
			if(dialog!=null)dialog.setProgress(msg.what);

		}
	};

	private void install(File f) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(f);

		intent.setDataAndType(Uri.fromFile(f), type);
		context.startActivity(intent);

	}

	public void delFile() {
		if (updateFile != null && updateFile.exists()) {
			updateFile.delete();
			// LogUtils.logi(LOG_TAG, "The TempFile(" + updateFile.getAbsolutePath() +
			// ") was deleted.");
		}
	}

	private String getMIMEType(File f) {

		String type = "";
		String fName = f.getName();
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}

		if (end.equals("apk")) {
		} else {
			type += "/*";
		}

		return type;
	}

}
