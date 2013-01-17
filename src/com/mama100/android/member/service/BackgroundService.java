package com.mama100.android.member.service;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.WebViewActivity;
import com.mama100.android.member.bean.MobResponseCode;
import com.mama100.android.member.businesslayer.MessageProvider;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.message.PullMessageReq;
import com.mama100.android.member.domain.message.PullMessageRes;
import com.mama100.android.member.domain.user.UploadLogFileReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.DateHelper;
import com.mama100.android.member.util.DesUtils;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.NetworkUtils;
import com.mama100.android.member.util.SDCardUtil;
import com.mama100.android.member.util.StorageUtils;
import com.mama100.android.member.util.StringUtils;

/**
 * 后台服务用于从服务器获取最新的消息，消息的种类分为：系统消息(包括用户意见反馈的回复消息)，用户个人积分消息。 将来可能有更多功能在该后台服务里生成。
 * 
 * @author aihua.yan 2012-08-14
 * 
 */
public class BackgroundService extends Service {
	private static final int FIRST_TIME_INTERVAL = 3000; //第一次发送前的间隔时间 3秒
	/**
	 * 
	 */
	private final static String TAG = "BackgroundService";
	/***********************************
	 * 与周期性从服务器获取消息的任务 有关
	 *********************************/
	private Timer timer = new Timer();
	private TimerUploadTask uploadTask = new TimerUploadTask();
	private NotificationManager notificationManager;
	private int serviceStartId = 0;
	
//	private Thread uploadLogThread = new UploadThread();
	public String LOG_FILE_PATH = "/sdcard/mama100_data/log.log";
	private int DAYS_OFFSET = 2;;

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.logi(TAG, "BackgroundService onCreate.......");
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// 1,先判断SD卡是否存在
//		if (SDCardUtil.isSDCardExist()) {
//			// 2,再判断该文件是否存在
//			File file = new File(LOG_FILE_PATH);
//			if (file.exists()) {
//				// 3,再判断现在日期距离文件的传建时间是否跨度超过2天
//				String createTime = StorageUtils.getShareValue(
//						getApplicationContext(), "logcreatetime");
//				LogUtils.loge(TAG, "createTime string - " + createTime);
//				if (!StringUtils.isBlank(createTime)) {
//					long previous = Long.parseLong(createTime);
//					LogUtils.loge(TAG, "createTime long- " + previous);
//					long current = System.currentTimeMillis();
//					LogUtils.loge(TAG, "currentTime long- " + current);
//					int day_offset = DateHelper.getHowManyDaysBetween(previous,
//							current);
//					LogUtils.loge(TAG, "day_offset - " + day_offset);
//					if (day_offset >= DAYS_OFFSET) {
//						// 4,如果这些都满足，就启动文件上传workThread
//						uploadLogThread.start();
//					}
//				} else {
//					// 如果创建时间都为空，就直接上传吧。。
//					uploadLogThread.start();
//				}
//			}
//		}
	}

	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		startUploadTimerTask();

		LogUtils.logi(TAG, "BackgroundService onStart.......");

	}

	/**
	 * 2- 本方法用于在菜单里或者系统设置里面的更新软件版本和数据库版本。。
	 * onStart，当调用者Activity退出或关闭时，该service不会一起退出，是独立的
	 * 也可以通过stopSelf,stopIntent来停止
	 */
	// 用ontartCommand方法， onStart已经@Deprecated掉了。
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.logi("BackgroundService", "Started by received start id " + startId
				+ ",Received flags - " + flags + ", \n intent -" + ": " + intent);
		serviceStartId = startId;
		startUploadTimerTask();
		return START_STICKY; //进程被杀，立马涅槃
	}

	/***********************************
	 * 下面的都与上传数据库里的任务有关的方法
	 *********************************/
	private void startUploadTimerTask() {
		try {
			timer.scheduleAtFixedRate(uploadTask, FIRST_TIME_INTERVAL,
					AppConstants.UPLOAD_SERVICE_MIN_INTERVAL);
		} catch (IllegalArgumentException e) {
			LogUtils.logi(TAG, LogUtils.getStackTrace(e));
		} catch (Exception e) {
			LogUtils.logi(TAG, LogUtils.getStackTrace(e));
		}
	}

	public class TimerUploadTask extends TimerTask {

		@Override
		public void run() {
			
			LogUtils.logd(TAG, "Timertask 再启动 " + TAG
					+ "...! \n"+ "time is: "
					+ new Timestamp(System.currentTimeMillis()).toString());
			// 检测网络状态, 并给出错误提示
			long current = System.currentTimeMillis();
			if (!NetworkUtils.checkNetworkStatusAndHintForService(
					getBaseContext(), current)) {
				return;
			} else {
				LogUtils.logd(TAG, "访问消息服务器 " + TAG + ".....................!");
				
				PullMessageReq request = new PullMessageReq();
				String username = "";
				
				if(!StringUtils.isBlank(BasicApplication.getInstance().getUsername())){
					username = BasicApplication.getInstance().getUsername();
				}else{
					
					String undecodedUsername = StorageUtils
							.getLastLoginAccount(getApplicationContext());
					try {
						username = DesUtils.decode(DesUtils.DES_COMMON_KEY, undecodedUsername);
					} catch (Exception e) {
						LogUtils.loge(TAG, "用户名解密出现问题");
						LogUtils.loge(TAG, e.getMessage());
					}
				}
				
				request.setUname(username);
				BaseRes response = MessageProvider.getInstance(getApplicationContext())
						.pullMessage((PullMessageReq) request);
				
				LogUtils.logd(TAG, "Timertask 收到服务应答 " + TAG
						+ "...! \n"+ "time is: "
						+ new Timestamp(System.currentTimeMillis()).toString());
				
				if (response == null || response.getCode() == null)
					return;
				if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
					LogUtils.loge(TAG, response.getDesc()); //服务器返回"没发现最新消息"
					return;
				}
				if (response != null) {
					if (response.getCode().equals(MobResponseCode.SUCCESS)) {
						LogUtils.logd(TAG, "收到最新消息成功");
						sendNotificationSuccess((PullMessageRes) response);
					} else {
						LogUtils.logd(TAG, "收到最新消息失败");
					}
				} else {
					String error = "服务器没有应答";
					LogUtils.loge(TAG, "" + error);
				}
				
			}
		}

	}


	
	//上传文件
	public class UploadThread extends Thread{
		@Override
		public void run() {
			super.run();
			LogUtils.logd(TAG, "upload log start");
			String path = LOG_FILE_PATH ;
			File file = new File(path);
			
			
			String deviceId = BasicApplication.getInstance().getDevid();
			if(StringUtils.isBlank(deviceId)){
				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				deviceId = tm.getDeviceId();
			}
			if(SDCardUtil.isSDCardExist())
			if (file.exists()) {
				UploadLogFileReq request = new UploadLogFileReq();
				request.setDeviceId(deviceId);
				BaseRes response= UserProvider.getInstance(getApplicationContext()).uploadLogFile(
						(UploadLogFileReq) request ,file);
				
				if(response.getCode().equalsIgnoreCase(MobResponseCode.SUCCESS)){
					LogUtils.loge(TAG, "文件log.log上传成功");
					//删除掉本地的log文件
					SDCardUtil.deleteFolder(path);
					StorageUtils.removeShareValue(getApplicationContext(), "logcreatetime");
					LogUtils.loge(TAG, "本地文件log.log删除成功");
				}
				
			}
		}
	}
	
	// End

	@Override
	public boolean onUnbind(Intent intent) {
		super.onUnbind(intent);
		LogUtils.logi(TAG, "BackgroundService onUnbind.......");
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtils.logi(TAG, "BackgroundService onDestroy.......");
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}

		// if(timer_delete!=null)
		// {timer_delete.cancel();
		// timer_delete.purge();
		// timer_delete = null;}

		if (uploadTask != null) {
			uploadTask.cancel();
			uploadTask = null;
		}
		
		
//		if(uploadLogThread!=null&&!uploadLogThread.isInterrupted()){
//			uploadLogThread.interrupt();
//			uploadLogThread = null;
//		}
		
		// if(deleteTask!=null){
		// deleteTask.cancel();
		// deleteTask = null;
		// }

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	private void sendTestNotificationSuccess() {
		PullMessageRes res = new PullMessageRes();
		res.setId("46");
		res.setDesc("这是一个测试");
		res.setContent("何时才能度过黎明？");
		res.setTitle("宝宝通有消息啦");
		sendNotificationSuccess(res);

	}

	private void sendNotificationSuccess(PullMessageRes response) {
		Notification notification = new Notification(R.drawable.app_icon,
				"宝宝通有新消息啦!", System.currentTimeMillis());

		// 1, 设置intent
		Intent intent = new Intent(getApplicationContext(),
				WebViewActivity.class).putExtra(WebViewActivity.ID,
				response.getId()).putExtra("edwar", "right");
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);// 关键的一步，设置启动模式

		/**********************************************************
		 * 来自网络
		 *********************************************************/
		// Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);
		// intent.setComponent(new ComponentName(this.getPackageName(),
		// WebViewActivity.class.getName()));
		// //
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);//关键的一步，设置启动模式
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
		// Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);//关键的一步，设置启动模式
		// intent.putExtra(WebViewActivity.ID,
		// response.getId()).putExtra("edwar", "right");

		// 2, 设置 pendingIntent
		// getActivity方法里第二个参数requestCode的值为response.getId(),因为将来在
		// WebViewActivity里面，获取getIntent().getStringExtra(ID)时，对于pendingIntent,拿的就是requestCode的值。所以如此
		PendingIntent contentIntent = PendingIntent.getActivity(
				getBaseContext(), Integer.valueOf(response.getId()), intent, 0);
		notification.contentIntent = contentIntent;

		// 3, 取消这次pendingIntent, 如果设置，点击将不打开意图
		// contentIntent.cancel();

		// 4.set the contentView
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.upload_report_notification);
		contentView.setImageViewResource(R.id.notify_icon, R.drawable.app_icon);
		contentView.setTextViewText(R.id.notify_label, response.getTitle());
		contentView.setTextViewText(R.id.notify_content, response.getContent());
		notification.contentView = contentView;

		// 5,是否在点击一次以后，自动清除安装通知,这里设置为是
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// long id = System.currentTimeMillis();
		// notificationManager.notify((int) id, notification);

		// //6.设置消息数字
		int count = BasicApplication.getInstance().getUnreadMsgSum();
		BasicApplication.getInstance().setUnreadMsgSum((count + 1)); // 更新总消息数
		// notification.number=count+1;

		// 用消息自身的id作为notificationId,以便将来在消息列表里点击相同id的消息时，及时clear通知栏里对应id的消息
		// notificationManager.notify(Integer.valueOf(response.getId()),
		// notification);
		notificationManager.notify(AppConstants.COMMON_ID, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
