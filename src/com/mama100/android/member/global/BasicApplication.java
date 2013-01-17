package com.mama100.android.member.global;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.SplashActivity;
import com.mama100.android.member.bean.HomeImageItem;
import com.mama100.android.member.bean.ThirdPartyUser;
import com.mama100.android.member.bean.WeiboShareContent;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.Md5Utils;
import com.mama100.android.member.util.PictureUtil;
import com.mama100.android.member.util.SDCardUtil;
import com.mama100.android.member.util.StorageUtils;
import com.mama100.android.member.util.StringUtils;

import dalvik.system.VMRuntime;

/**
 * 建立这个 类 主要目的是 全局变量
 */
public class BasicApplication extends Application implements
		Thread.UncaughtExceptionHandler {

	/**********************
	 * 渠道编码
	 *******************/
	private String channel;

	/********************************************************************
	 * 手机基本参数变量
	 ********************************************************************/
	/*
	 * 这个就是 IMEI码 GSM手机,返回 IMEI. 15位 CDMA手机，返回 MEID. 14位 Return null if device
	 * ID is not available.
	 */
	private String devid;

	/*
	 * 手机类型： 例如： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号 PHONE_TYPE_CDMA CDMA信号
	 */
	private int phoneType;

	/**
	 * 手机SIM卡IMSI码<br>
	 * IMEI imsi_国际移动用户识别码s
	 */
	private String imsi;

	/**
	 * ICCID:手机SIM卡背面码<br>
	 * ICCID-Integrate circuit card identity 集成电路卡识别码（固化在手机SIM卡中）
	 * ICCID为IC卡的唯一识别号码，共有20位数字组成，其编码格式为：XXXXXX 0MFSS YYGXX XXXXX。 分别介绍如下：
	 * 前六位运营商代码：中国移动的为：898600；中国联通的为：898601。
	 */

	private String iccid;

	// 操作系统
	private String os;

	// 操作系统版本号 如: 2.3
	private String osver;

	// 手机品牌 如: HTC
	private String brand;

	// 手机型号 如:G7
	private String model;

	// 本地版本号
	private int localVersion = 0;

	// 本地版本号名称
	private String localVersionName;

	// 屏幕宽(像素)
	private int displayWidth;

	// 屏幕高(像素)
	private int displayHeight;

	// 屏幕密度
	private int dpi;

	/********************************************************************
	 * 基本路径和端口及服务器版本等变量
	 ********************************************************************/

	// 应用名称
	private String appName;

	// 安装包名称
	private String apkName;

	// 服务器版本号
	private int serverVersion = 0;

	// 服务器版本号名称
	private String serverVersionName;

	// sdcard 路径
	private String mama100Dir;

	// IP地址+端口 或者 域名+端口
	private String ipAddr;

	/********************************************************************
	 * SSO 单点登录用到的变量
	 ********************************************************************/
	// SSO IP地址+端口 或者 域名+端口
	private String ssoIpAddr;
	// sso登录用的TGT
	private String tgt;
	// sso登录之后用到的sessoinId
	private String sessionId;

	/***********************************************************************
	 * 上传照片图片
	 **********************************************************************/
	private Uri avatarTempUri;// 头像临时路径，用于上传头像时用，用完即删，且删sd卡头像。
	
	// 标签，用于判断是否需要去删除sd卡。现在因为有了未登录拍照，所以默认删除。。
	//当用户发现  未登录临时文件夹(getTempCropingPhotoPathForUnlogin)有照片，但是用户自己的临时文件夹(getTempCropingPhotoPath)没照片时，
	//则证明用户是未登录时拍了一张照，然后登录进来之后没有拍过照片。。@see EditProfileActivity doClickRight()..
	private boolean isNeedToDeleteSDCardTempFolder = true;
	/***********************************************************************
	 * 本地内存保存图片用到的变量
	 ***********************************************************************/
	private Uri avatarStoreUri = null; // 本地内存保存的头像图片路径, 这个后来用不到了。
	private Uri cropImageStoreUri = null; // 本地内存保存的裁剪图片路径
	private Bitmap avatarBitmap = null; // 本地内存保存的头像图片bitmap
	private Bitmap tempAvatarBitmap = null; // 用于本地未上传的个人照及从第三方下载未上传的个人照
	private String userFolderPath = ""; // 本地用户的路径
	private String tempPicPath = ""; // 本地临时图片的路径
	private String tempStorePath = ""; // 本地保存图片的路径
	private String apkFolderPath = ""; // 本地apk的路径

	// 计算本地/storepic/里面 保存活动图片的增数器
	private int actPictureCount = 0;

	/*****************************************************************************
	 * 为不同用户备份用户资料用到的 变量, 本地保存用户的积分，昵称等
	 *****************************************************************************/
	private String mid = ""; // 用户ID
	private String username = "";
	private String mobile = ""; //用户手机号
	private String lastAvatarUrl = "";// 主界面判断头像是否有了新的图片
	private String lastRegpointBalance = "";// 主界面判断积分是否有了改变
	private String nickname = "";
	private String weiboAvatarUrl = ""; //第三方首次登录时，从第三方服务器获取的用户的头像路径
	private String weiboNickname = ""; //第三方首次登录时，从第三方服务器获取的用户的昵称
	private String customerInfoCompleted = "";// 是否完成收货人及地址资料
	private WeiboShareContent weiboShareContent = null; // 微博分享的文字
	/****************************************************************************
	 * 是否需要上传图片用到的变量
	 ****************************************************************************/
	private boolean isAvatarChanged = false;// 用户是否更改了个人信息图片

	/****************************************************************************
	 * 拍照裁剪 判断是从拍照进来的，还是从选择图片进来的，后者就不用旋转。。
	 ****************************************************************************/
	private boolean isFromGallery = false;

	/***********************************************************************
	 * 应用程序页面及程序声明周期(启动，离开)用到的变量
	 **********************************************************************/
	// 未读的消息数
	private int unreadMsgSum = 0;

	private String TAG = this.getClass().getSimpleName();

	// 标签，标记用户是否关闭应用程序。
	private static boolean isToExit = false;

	/*************************************************************************
	 * 通知栏 消息数
	 **************************************************************************/
	// 标签，判断是否homepageActivity已经打开，用于通知栏消息进来-->消息详情-->消息列表 -->home界面时，打开主界面
	// 同时也用于home界面-->点击活动-->消息详情-->home界面
	private static boolean isHomepageAlreadyStart = false;
	// 标签，判断是否MessageListActivity已经打开，用于通知栏消息进来-->消息详情-->消息列表
	// -->消息详情-->消息列表时，不用刷新列表
	private static boolean isMessageListAlreadyStart = false;
	//是否从通知栏 消息进入系统
	private boolean isFromNotificationBar =false; 

	// 标签，标记用户是否选择“记住我”
	private boolean isAutoLogin = false;
	
//	// 标签，标记用户的当前状态，或者是否未登录而进入主页
	private boolean isUnLogin = true;
	
	//标签， 标记用户是否是第三方账号初次登录(注册)
	private boolean isFirstLogin = false;
	
	//标签， 标记用户是否是从第三方登录(进来)
	private boolean isLoginFromThirdParty = false;
	
	//标签， 标记用户是否是邮箱 注册一个新号
	private boolean isFromRegister = false;

	// 全局资源对象
	public static Resources mResources;

	// 全局变量
	private static BasicApplication instance;

	// 2012-05-07，edwar,实时获取当前活动和handler --START。
	// 当前静态activity对象,用于随时获取当前activity对象。
	private static Activity currentActivity;

	/*************************************************************************
	 * 临时变量 Service Ticket
	 *************************************************************************/
	private String serviceTicket;
	
	/****************************************
         * added by edwar 2012-10-17 新增微博的全局变量
         ****************************************/
	private List<ThirdPartyUser> weiboItems = new ArrayList<ThirdPartyUser>();
	List<HomeImageItem> imgList = new ArrayList<HomeImageItem>();
	
	private ThirdPartyUser qqItems ;//用户的 QQ信息 
	private ThirdPartyUser sinaWeiboItems ; //用户的 sinaWeibo信息 
	private ThirdPartyUser qqWeiboItems ; //用户的 QQWeibo信息
	
	/****************************************
         * 新浪微博登录过的Uid集合 ， added by edwar 2012-10-25
         * 实现同一个微博账号除第一次，后几次不用再次进入个人信息界面
         ****************************************/
	private List<String> SinaUidList = new ArrayList<String>();
	private boolean isNewSinaUid = false;
	/****************************************
	 * QQ登录过的Uid集合 ， added by edwar 2012-10-25
	 * 实现同一个微博账号除第一次，后几次不用再次进入个人信息界面
	 ****************************************/
	private List<String> QQUidList = new ArrayList<String>();
	private boolean isNewQQUid = false;
	
	
	private boolean isAsso = false;//用户是否已经(激活会员卡 = 关联了积分通)
	
	/**
	 * 新浪key
	 */
	private String sina_key;
	
	/**
	 * qq微博key
	 */
	private String qqweibo_key;
	
	/**
	 * qqkey
	 */
	private String qq_key;
	
	//1.2版本，全局code -->用于 未登录下，主页->点击"消息列表"->登录->新浪/qq->个人信息->消息列表的 页面流程跳转
	private int requestCode = 0;
	

	/**
	 * 获取全局变量
	 * 
	 * @return
	 */
	public static BasicApplication getInstance() {
		if (instance == null) {
			LogUtils.loge("BasicApplication",
					" 程序出错,BasicApplication instance is null");
			System.exit(0);
		}
		return instance;
	}

	private final static int CWJ_HEAP_SIZE = 6 * 1024 * 1024;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		VMRuntime.getRuntime().setMinimumHeapSize(CWJ_HEAP_SIZE);

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());

		// CrashHandler crashHandler = CrashHandler.getInstance();
		// //注册crashHandler
		// crashHandler.init(getApplicationContext());
		// //发送以前没发送的报告(可选)
		// crashHandler.sendPreviousReportsToServer();

		try {
			LogUtils.logi(TAG, ">>>>>Initializing Global instance time is: "
					+ new Timestamp(System.currentTimeMillis()).toString());
			PackageManager pm = this.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			devid = tm.getDeviceId();

			// 如果有些手机不能获取 imei(deviceId)，imei为null的话，传mac address
			if (StringUtils.isBlank(devid)) {
				WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifi.getConnectionInfo();
				String mac = info.getMacAddress();
				
				
//				devid = mac.replace(":", "");
//				devid = mac;
				//modified by edwar 2012-11-1  mac地址也要md5编码，变成32位
				devid = Md5Utils.encryptTo16hex(mac);
				
			}

			/************** 老版宝宝通的 deviceId **********************/
			String old_version_deviceId = android.provider.Settings.System
					.getString(getContentResolver(),
							android.provider.Settings.System.ANDROID_ID);
			;

			LogUtils.loge(TAG, "old_version_deviceId - " + old_version_deviceId);

			// added by aihua.yan 2012-07-18
			String imei = tm.getDeviceId(); // 取出IMEI ， 我们这里叫deviceId
											// A0000022DB3966
			String tel = tm.getLine1Number(); // 取出MSISDN，很可能为空
			iccid = tm.getSimSerialNumber(); // 取出ICCID 89860311010201012626
			imsi = tm.getSubscriberId(); // 取出IMSI 460030251570696

			phoneType = tm.getPhoneType();

			os = "android";

			osver = android.os.Build.VERSION.RELEASE;
			brand = android.os.Build.BRAND;
			model = android.os.Build.MODEL;
			appName = getResources().getString(R.string.app_name);
			apkName = getResources().getString(R.string.apk_name);

			mama100Dir = getResources().getString(R.string.mama100_dir);

			localVersion = pi.versionCode;
			localVersionName = pi.versionName;

			serverVersion = localVersion;
			serverVersionName = localVersionName;

			/**
			 * 获取手机的 高，宽，密度dpi dots-per-inch: 160,240
			 */
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(metrics);
			displayWidth = metrics.widthPixels;
			displayHeight = metrics.heightPixels;
			dpi = metrics.densityDpi;

			ipAddr = getResources().getString(R.string.ip_addr);
			ssoIpAddr = getResources().getString(R.string.sso_ip);
			mResources = getResources();

			initValues();

			channel = getChannelCode(this);
			setChannel(channel);

		} catch (Exception e) {
			LogUtils.loge(TAG, LogUtils.getStackTrace(e));
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void initValues() {

		userFolderPath = ""; // 本地用户的路径
		tempPicPath = ""; // 本地临时图片的路径
		tempStorePath = ""; // 本地保存图片的路径
		apkFolderPath = ""; // 本地apk的路径

		/*****************************************************************************
		 * 为不同用户备份用户资料用到的 变量, 本地保存用户的积分，昵称等
		 *****************************************************************************/
		username = "";
		lastAvatarUrl = "";// 主界面判断头像是否有了新的图片
		lastRegpointBalance = "";// 主界面判断积分是否有了改变
		nickname = "";
		weiboAvatarUrl = "";

	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc(); // 内存释放，回收垃圾
	}

	/***********************************************************************
	 * 变量的 getter 和 setter
	 **********************************************************************/

	public int getUnreadMsgSum() {
		return unreadMsgSum;
	}

	public void setUnreadMsgSum(int unreadMsgSum) {
		this.unreadMsgSum = unreadMsgSum;
	}

	public String getTgt() {
		LogUtils.loge("sso", "tgt get - " + tgt);
		return tgt;
	}

	public void setTgt(String tgt) {
		LogUtils.loge("sso", "tgt set - " + tgt);
		this.tgt = tgt;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getDevid() {
		return devid;
	}

	public void setDevid(String devid) {
		this.devid = devid;
	}

	public int getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(int phoneType) {
		this.phoneType = phoneType;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsver() {
		return osver;
	}

	public void setOsver(String osver) {
		this.osver = osver;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public int getLocalVersion() {
		return localVersion;
	}

	public void setLocalVersion(int localVersion) {
		this.localVersion = localVersion;
	}

	public String getLocalVersionName() {
		return localVersionName;
	}

	public void setLocalVersionName(String localVersionName) {
		this.localVersionName = localVersionName;
	}

	public int getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(int serverVersion) {
		this.serverVersion = serverVersion;
	}

	public String getServerVersionName() {
		return serverVersionName;
	}

	public void setServerVersionName(String serverVersionName) {
		this.serverVersionName = serverVersionName;
	}

	public String getMama100Dir() {
		return mama100Dir;
	}

	public void setMama100Dir(String mama100Dir) {
		this.mama100Dir = mama100Dir;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getSsoIpAddr() {
		return ssoIpAddr;
	}

	public void setSsoIpAddr(String ssoIpAddr) {
		this.ssoIpAddr = ssoIpAddr;
	}

	public boolean isAvatarChanged() {
		return isAvatarChanged;
	}

	public void setAvatarChanged(boolean isAvatarChanged) {
		this.isAvatarChanged = isAvatarChanged;
	}

	public String getUserFolderPath() {
		return userFolderPath;
	}

	public void setUserFolderPath(String userFolderPath) {
		this.userFolderPath = userFolderPath;
	}

	public String getTempPicPath() {
		return tempPicPath;
	}

	public void setTempPicPath(String tempPicPath) {
		this.tempPicPath = tempPicPath;
	}

	public String getTempStorePath() {
		return tempStorePath;
	}

	public void setTempStorePath(String tempStorePath) {
		this.tempStorePath = tempStorePath;
	}

	public String getApkFolderPath() {
		return apkFolderPath;
	}

	public boolean isNeedToDeleteSDCardTempFolder() {
		return isNeedToDeleteSDCardTempFolder;
	}

	public void setNeedToDeleteSDCardTempFolder(
			boolean isNeedToDeleteSDCardTempFolder) {
		this.isNeedToDeleteSDCardTempFolder = isNeedToDeleteSDCardTempFolder;
	}

	public void setApkFolderPath(String apkFolderPath) {
		this.apkFolderPath = apkFolderPath;
	}

	public int getDisplayWidth() {
		return displayWidth;
	}

	public void setDisplayWidth(int displayWidth) {
		this.displayWidth = displayWidth;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public void setDisplayHeight(int displayHeight) {
		this.displayHeight = displayHeight;
	}

	public int getDpi() {
		return dpi;
	}

	public void setDpi(int dpi) {
		this.dpi = dpi;
	}

	public boolean isToExit() {
		return isToExit;
	}

	public void setToExit(boolean isToExit) {
		BasicApplication.isToExit = isToExit;
	}

	
	public boolean isFirstLogin() {
		return isFirstLogin;
	}

	public void setFirstLogin(boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	public boolean isLoginFromThirdParty() {
		return isLoginFromThirdParty;
	}

	public void setLoginFromThirdParty(boolean isLoginFromThirdParty) {
		this.isLoginFromThirdParty = isLoginFromThirdParty;
	}

	public boolean isFromRegister() {
		return isFromRegister;
	}

	public void setFromRegister(boolean isFromRegister) {
		this.isFromRegister = isFromRegister;
	}

	public boolean isUnLogin() {
		return isUnLogin;
	}

	public void setUnLogin(boolean isUnLogin) {
		this.isUnLogin = isUnLogin;
	}

	public boolean isAutoLogin() {
		return isAutoLogin;
	}

	public void setAutoLogin(boolean isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
	}

	public Uri getUploadAvatarUri() {
		return avatarTempUri;
	}

	public void setUploadAvatarUri(Uri avatarUri) {
		this.avatarTempUri = avatarUri;
	}

	public String getNickname() {
		if (nickname == null)
			nickname = "";
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getWeiboAvatarUrl() {
		return weiboAvatarUrl;
	}

	public void setWeiboAvatarUrl(String weiboAvatarUrl) {
		this.weiboAvatarUrl = weiboAvatarUrl;
	}

	public String getWeiboNickname() {
		return weiboNickname;
	}

	public void setWeiboNickname(String weiboNickname) {
		this.weiboNickname = weiboNickname;
	}

	public String getLastRegpointBalance() {
		if (lastRegpointBalance == null)
			lastRegpointBalance = "";
		return lastRegpointBalance;
	}

	public void setLastRegpointBalance(String lastRegpointBalance) {
		this.lastRegpointBalance = lastRegpointBalance;
	}

	public String getLastAvatarUrl() {
		if (lastAvatarUrl == null)
			lastAvatarUrl = "";
		return lastAvatarUrl;
	}

	public void setLastAvatarUrl(String lastAvatarUrl) {
		this.lastAvatarUrl = lastAvatarUrl;
	}

	@Deprecated
	public Uri getStoreAvatarUri() {
		return avatarStoreUri;
	}

	@Deprecated
	public void setStoreAvatarUri(Uri uri) {
		this.avatarStoreUri = uri;
	}

	public Uri getCropImageStoreUri() {
		return cropImageStoreUri;
	}

	public void setCropImageStoreUri(Uri cropImageStoreUri) {
		this.cropImageStoreUri = cropImageStoreUri;
	}

	public boolean isFromGallery() {
		return isFromGallery;
	}

	public void setFromGallery(boolean isFromGallery) {
		this.isFromGallery = isFromGallery;
	}

	public Bitmap getAvatarBitmap() {
//		if (avatarBitmap == null || avatarBitmap.isRecycled()) {
//			// 如果本地保留了上次用户的头像，就用上传头像
//			String filename = SDCardUtil.getPictureStorePath()
//					+ AppConstants.TEMP_STORE_PICTURE_NAME;
//			avatarBitmap = PictureUtil.getPictureFromSDcardByPath(filename);
//		}
		return avatarBitmap;
	}

	public void setAvatarBitmap(Bitmap bitmap) {
		// 回收之前的图片内存
		if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
//			avatarBitmap.recycle(); //个人信息拍照->回主页报原图片被回收错误，所以这里暂不回收
			avatarBitmap = null;
		}
		this.avatarBitmap = bitmap;
	}
	

	public Bitmap getTempAvatarBitmap() {
		return tempAvatarBitmap;
	}

	public void setTempAvatarBitmap(Bitmap tempAvatarBitmap) {
		this.tempAvatarBitmap = tempAvatarBitmap;
	}

	public String getServiceTicket() {
		return serviceTicket;
	}

	public void setServiceTicket(String serviceTicket) {
		this.serviceTicket = serviceTicket;
	}
	

	public String getSina_key() {
		return sina_key;
	}

	public void setSina_key(String sina_key) {
		this.sina_key = sina_key;
	}

	public String getQqweibo_key() {
		return qqweibo_key;
	}

	public void setQqweibo_key(String qqweibo_key) {
		this.qqweibo_key = qqweibo_key;
	}
	

	public List<String> getSinaUidList() {
		return SinaUidList;
	}

	public void setSinaUidList(List<String> sinaUidList) {
		SinaUidList = sinaUidList;
	}

	public List<String> getQQUidList() {
		return QQUidList;
	}

	public void setQQUidList(List<String> qQUidList) {
		QQUidList = qQUidList;
	}

	public boolean isNewSinaUid() {
		return isNewSinaUid;
	}

	public void setNewSinaUid(boolean isNewSinaUid) {
		this.isNewSinaUid = isNewSinaUid;
	}

	public boolean isNewQQUid() {
		return isNewQQUid;
	}

	public void setNewQQUid(boolean isNewQQUid) {
		this.isNewQQUid = isNewQQUid;
	}

	public String getQq_key() {
		return qq_key;
	}

	public void setQq_key(String qq_key) {
		this.qq_key = qq_key;
	}

	
	
	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public static String getXmlString(int id) {
		if (mResources == null) {
			LogUtils.logi("Global getXmlString", "Resouce  is  Null");
			return null;
		}

		return mResources.getString(id);
	}

	public Activity getCurrentActivity() {
		return currentActivity;
	}

	/**
	 * @param activity
	 *            当前活动，用于后台更新软件版本时，作为Dialog的载体
	 *            仅SplashActivity,及settingActivity两处用到该方法，因为就这两处要更新版本。
	 */
	public void setCurrentActivity(Activity activity) {
		BasicApplication.currentActivity = activity;
	}

	public boolean isHomepageAlreadyStart() {
		return isHomepageAlreadyStart;
	}

	public void setHomepageAlreadyStart(boolean isHomepageAlreadyStart) {
		BasicApplication.isHomepageAlreadyStart = isHomepageAlreadyStart;
	}

	public boolean isMessageListAlreadyStart() {
		return isMessageListAlreadyStart;
	}

	public void setMessageListAlreadyStart(boolean isMessageListAlreadyStart) {
		BasicApplication.isMessageListAlreadyStart = isMessageListAlreadyStart;
	}
	

	public boolean isFromNotificationBar() {
		return isFromNotificationBar;
	}

	public void setFromNotificationBar(boolean flag) {
		isFromNotificationBar = flag;
	}

	public int getActPictureCount() {
		return actPictureCount++;
	}

	public void setActPictureCount(int actPictureCount) {
		this.actPictureCount = actPictureCount;
	}

	
	public List<HomeImageItem> getImgList() {
		return imgList;
	}

	public void setImgList(List<HomeImageItem> imgList) {
		this.imgList = imgList;
	}
	
	public boolean isAsso() {
		return isAsso;
	}

	public void setAsso(boolean isAsso) {
		this.isAsso = isAsso;
	}

	public List<ThirdPartyUser> getWeiboItems() {
		return weiboItems;
	}
	
	

	public void setWeiboItems(List<ThirdPartyUser> weiboItems) {
		this.weiboItems = weiboItems;
		//初始化各个第三方对象
		for (ThirdPartyUser item : weiboItems) {
			if(item.getUserType().equalsIgnoreCase(ThirdPartyUser.type_qq)){
				qqItems = item;
			}else if(item.getUserType().equalsIgnoreCase(ThirdPartyUser.type_sina)){
				sinaWeiboItems = item;
			}else if(item.getUserType().equalsIgnoreCase(ThirdPartyUser.type_qqweibo)){
				qqWeiboItems = item;
			}
		}
		
	}
	
	
	public ThirdPartyUser getQqItems() {
		return (qqItems== null)?new ThirdPartyUser():qqItems;
	}
	
	public void setQqItems(ThirdPartyUser qqItems) {
		this.qqItems = qqItems;
	}
	
	public ThirdPartyUser getSinaWeiboItems() {
		return (sinaWeiboItems== null)?new ThirdPartyUser():sinaWeiboItems;
	}
	
	public void setSinaWeiboItems(ThirdPartyUser sinaWeiboItems) {
		this.sinaWeiboItems = sinaWeiboItems;
	}
	
	public ThirdPartyUser getQqWeiboItems() {
		return (qqWeiboItems == null) ? new ThirdPartyUser() : qqWeiboItems;
	}
	
	public void setQqWeiboItems(ThirdPartyUser qqWeiboItems) {
		this.qqWeiboItems = qqWeiboItems;
	}
	
	

	/*************************************************************
	 * 桌面应用快捷方式 --START
	 *************************************************************/


	/**
	 * 为当前应用添加桌面快捷方式
	 * 
	 * @param cx
	 * @param appName
	 *            快捷方式名称
	 */
	public static void addShortcut(Context cx) {
		if (hasShortcut(cx)) {
			return;
		}
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		Intent shortcutIntent = cx.getPackageManager()
				.getLaunchIntentForPackage(cx.getPackageName());
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// 获取当前应用名称
		String title = null;
		try {
			final PackageManager pm = cx.getPackageManager();
			title = pm.getApplicationLabel(
					pm.getApplicationInfo(cx.getPackageName(),
							PackageManager.GET_META_DATA)).toString();
		} catch (Exception e) {
		}
		// 快捷方式名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 不允许重复创建（不一定有效）
		shortcut.putExtra("duplicate", false);
		// 快捷方式的图标
		Parcelable iconResource = Intent.ShortcutIconResource.fromContext(cx,
				R.drawable.app_icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

		cx.sendBroadcast(shortcut);
	}

	/**
	 * 删除当前应用的桌面快捷方式
	 * 
	 * @param cx
	 */
	public static void delShortcut(Context cx) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");

		// 获取当前应用名称
		String title = null;
		try {
			final PackageManager pm = cx.getPackageManager();
			title = pm.getApplicationLabel(
					pm.getApplicationInfo(cx.getPackageName(),
							PackageManager.GET_META_DATA)).toString();
		} catch (Exception e) {
		}
		// 快捷方式名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		Intent shortcutIntent = cx.getPackageManager()
				.getLaunchIntentForPackage(cx.getPackageName());
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		cx.sendBroadcast(shortcut);
	}

	/**
	 * 判断桌面是否已添加快捷方式
	 * 
	 * @param cx
	 * @param titleName
	 *            快捷方式名称
	 * @return
	 */
	public static boolean hasShortcut(Context cx) {
		boolean result = false;
		// 获取当前应用名称
		String title = null;
		try {
			final PackageManager pm = cx.getPackageManager();
			title = pm.getApplicationLabel(
					pm.getApplicationInfo(cx.getPackageName(),
							PackageManager.GET_META_DATA)).toString();
		} catch (Exception e) {
		}

		final String uriStr;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			uriStr = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		final Uri CONTENT_URI = Uri.parse(uriStr);
		final Cursor c = cx.getContentResolver().query(CONTENT_URI, null,
				"title=?", new String[] { title }, null);
		if (c != null && c.getCount() > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * 删除桌面快捷方式 added by edwar 2012-07-12
	 */
	public void deleteShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));

		// 这里的intent要和创建时的intent设置一致
		// 指定快捷方式的启动对象
		// ComponentName comp = new ComponentName(this.getPackageName(), "." +
		// this.getLocalClassName());
		ComponentName comp = new ComponentName(getApplicationContext()
				.getPackageName(), ".activities.LoginActivity");
		Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				getApplicationContext(), R.drawable.app_icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		// 发出广播
		sendBroadcast(shortcut);
		// 将第一次启动的标识设置为false
		SharedPreferences setting = getSharedPreferences("silent.preferences",
				0);
		Editor editor = setting.edit();
		editor.putBoolean("FIRST_START", true);
		// 提交设置
		editor.commit();
	}

	/**
	 * 创建桌面快捷方式 added by edwar 2012-07-06
	 */
	public void createShortcut() {
		SharedPreferences setting = getSharedPreferences("silent.preferences",
				0);
		// 判断是否第一次启动应用程序（默认为true）
		boolean firstStart = setting.getBoolean("FIRST_START", true);
		// 第一次启动时创建桌面快捷方式
		if (firstStart) {
			Intent shortcut = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			// 快捷方式的名称
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					getString(R.string.app_name));
			// 不允许重复创建
			shortcut.putExtra("duplicate", false);

			// 指定快捷方式的启动对象
			// ComponentName comp = new ComponentName(this.getPackageName(), "."
			// + this.getLocalClassName());
			ComponentName comp = new ComponentName(getApplicationContext()
					.getPackageName(), ".activities.LoginActivity");
			Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

			// 快捷方式的图标
			ShortcutIconResource iconRes = Intent.ShortcutIconResource
					.fromContext(getApplicationContext(), R.drawable.app_icon);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
			// 发出广播
			sendBroadcast(shortcut);

			// 将第一次启动的标识设置为false
			Editor editor = setting.edit();
			editor.putBoolean("FIRST_START", false);
			// 提交设置
			editor.commit();
		}

	}

	// 退出时，完全杀掉进程，等同于KillProcess.. 我们应用目前用不到，因为我们有后台Service
	public void exit() {
		// TextView textView = new TextView(this);
		// textView.setText(10);// 制造一个空指针的异常,则系统Dalvik VM 会直接关闭你的进程
		// 退出程序
		android.os.Process.killProcess(android.os.Process.myPid());// 杀掉进程
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO
		LogUtils.loge("UncaughtException", ex.getLocalizedMessage() + "");
		Intent intent = new Intent(this, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	/*************************************************************
	 * 桌面应用快捷方式 --END
	 *************************************************************/

	public void showLongToast(int resId) {
		Toast.makeText(getApplicationContext(), mResources.getString(resId),
				Toast.LENGTH_SHORT).show();
	}

	public void showLongToast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
				.show();
	}

	/*************************************************************
	 * 清除缓存用到的方法
	 ***************************************************************/
	public void clearMemory() {
		if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
			avatarBitmap.recycle();
			avatarBitmap = null;
		}

	}

	/****************************************************************
	 * 
	 ****************************************************************/
	public String toApplicationInfoString() {
		StringBuilder sb = new StringBuilder();
		sb.append(".............get Application Info START....... \n");
		sb.append("apkName - " + apkName + "\n");
		sb.append("appName - " + apkName + "\n");
		sb.append("currentActivity - " + currentActivity + "\n");
		sb.append("isToExit - " + isToExit + "\n");
		sb.append("userFolderPath - " + userFolderPath + "\n");
		sb.append("avatarStoreUri - " + avatarStoreUri + "\n");
		sb.append("avatarTempUri - " + avatarTempUri + "\n");
		sb.append("isAutoLogin - " + isAutoLogin + "\n");
		sb.append("isAvatarChanged - " + isAvatarChanged + "\n");
		sb.append("localVersion - " + localVersion + "\n");
		sb.append("localVersionName - " + localVersionName + "\n");
		sb.append("serverVersion - " + serverVersion + "\n");
		sb.append("serverVersionName - " + serverVersionName + "\n");
		sb.append("serviceTicket - " + serviceTicket + "\n");
		sb.append("sessionId - " + sessionId + "\n");
		sb.append("tgt - " + tgt + "\n");
		sb.append("unreadMsgSum - " + unreadMsgSum + "\n");
		sb.append("ipAddr - " + ipAddr + "\n");
		sb.append("ssoIpAddr - " + ssoIpAddr + "\n");
		sb.append("mama100Dir - " + mama100Dir + "\n");
		sb.append("\n");
		sb.append(".............get device Info END....... \n");
		return sb.toString();
	}

	public String toDeviceInfoString() {
		StringBuilder sb = new StringBuilder();
		sb.append(".............get Device Info START....... \n");

		sb.append("channel - " + channel + "\n");
		sb.append("phoneType - " + phoneType + "\n");
		sb.append("brand - " + brand + "\n");
		sb.append("devid(iemi) - " + devid + "\n");
		sb.append("iccid - " + iccid + "\n");
		sb.append("imsi - " + imsi + "\n");
		sb.append("displayHeight - " + displayHeight + "\n");
		sb.append("displayWidth - " + displayWidth + "\n");
		sb.append("dpi - " + dpi + "\n");
		sb.append("model - " + model + "\n");
		sb.append("os - " + os + "\n");
		sb.append("osver - " + osver + "\n");
		sb.append("\n");
		sb.append(".............get device Info END....... \n");
		return sb.toString();
	}

	public String toUserInfoString() {
		StringBuilder sb = new StringBuilder();
		sb.append(".............get User Info START....... \n");
		// sb.append("username - " + StorageUtils.getLastLoginAccount(this) +
		// "\n");
		sb.append("username - " + username + "\n");
		sb.append("nickname - " + nickname + "\n");
		sb.append("pointbalance - " + lastRegpointBalance + "\n");
		sb.append("\n");
		sb.append(".............get User Info END....... \n");
		return sb.toString();

	}

	// 根据mid生成对应的文件夹名
	public void setAllFolderPath(String mid) {
		// setUsername(name); 这里不能写，因为现在直接用mid了，不能用Username
		String userfolderpath = SDCardUtil.ROOT_FOLDER_PATH + mid;
		setUserFolderPath(userfolderpath);
		setTempPicPath(userfolderpath + AppConstants.PICTURE_TEMP_PATH);
		setTempStorePath(userfolderpath + AppConstants.PICTURE_STORE_PATH);
		setApkFolderPath(userfolderpath + AppConstants.APK_FOLDER_PATH);
	}

	// 在用户设置界面，用户注销时,正常退出不用清零，因为有后台服务。。
	public void clearALLFolderPath() {
		String path = "";
		setUserFolderPath(path);
		setTempPicPath(path);
		setTempStorePath(path);
		setApkFolderPath(path);

	}

	// 用户注销
	public void clearOnLogout() {
		
		setUnLogin(true); //与clearNormalInfo()相关，放最后。
		setAvatarBitmap(null);
		clearMemory();
		clearALLFolderPath();
		setToExit(true);
		setLastAvatarUrl("");
		setLastRegpointBalance("");
		setMid("");
		setUsername("");
		setMobile("");
		setWeiboShareContent(null);
		clearNormalInfo();
		

		// 无论成功与否，都要清理tgt
		StorageUtils.storeLoginTGTInClient(getApplicationContext(), "");
		/********************************************************
		 *  added by edwar 2012-10-31  清空本地sessionId - start
		 ******************************************************/
		BasicApplication.getInstance().setSessionId("");
		StorageUtils.storeSessionId(getApplicationContext(), "");
		/********************************************************
		 *  added by edwar 2012-09-20  清空本地sessionId - end
		 ******************************************************/
		
		setTgt("");
		LogUtils.loge("SettingHomeActivity", "clear tgt success!");
	}

	private void clearThirdPartyRecord() {
		if(QQUidList!=null&&!QQUidList.isEmpty()){
			QQUidList.clear();
			QQUidList = null;
		}
		if(SinaUidList!=null&&!SinaUidList.isEmpty()){
			SinaUidList.clear();
			SinaUidList = null;
		}
		
		setNewQQUid(false);
		setNewSinaUid(false);
	}

	// 用户正常退出
	public void clearOnExit() {
		clearThirdPartyRecord();
		clearNormalInfo();
	}

	// 用户注销和正常退出公共流程
	public void clearNormalInfoForUnlogin() {
		
		setActPictureCount(0);
		
		// 设置主界面未打开
		setHomepageAlreadyStart(false);
		setMessageListAlreadyStart(false);
		clearTempPics();
		
		if (weiboItems != null && weiboItems.size() > 0) {
			weiboItems.clear();
			weiboItems = null;
		}
		
		if(imgList!=null&&imgList.size()>0){
			imgList.clear();
			imgList = null;
		}
		
		qqItems = null;
		qqWeiboItems = null;
		sinaWeiboItems = null;
		
		clearThirdPartyRecord();
		
	}
	// 用户注销和正常退出公共流程
	public void clearNormalInfo() {

		
		// 清除SessionId
		setSessionId("");
		StorageUtils.storeSessionId(getApplicationContext(), "");

		// 清除ST
		setServiceTicket("");
		// 清零保存活动照片计数器
		setActPictureCount(0);

		// 设置主界面未打开
		setHomepageAlreadyStart(false);
		// 设置消息列表界面未打开
		setMessageListAlreadyStart(false);
		
		clearTempPics();
		
		//清除是否初次登录，统一设置为false;
		setFirstLogin(false);
		setFromRegister(false);
		setNickname("");
		setWeiboAvatarUrl("");
		
		if (weiboItems != null && weiboItems.size() > 0) {
			weiboItems.clear();
			weiboItems = null;
		}
		
		if(imgList!=null&&imgList.size()>0){
			imgList.clear();
			imgList = null;
		}
		
		qqItems = null;
		qqWeiboItems = null;
		sinaWeiboItems = null;
		setFromNotificationBar(false);
	}

	public void clearTempPics() {
		
if (isNeedToDeleteSDCardTempFolder) {
			
	if(!isUnLogin()){
		LogUtils.loge("deletefolder", "BS_进入登录删除");		
			//1 , 清理SD卡用户临时文件 
			String userTempPath = SDCardUtil.getPictureTempPath();
			LogUtils.loge("deletefolderpath", userTempPath);	
			if(!StringUtils.isBlank(userTempPath)){
			LogUtils.logd(TAG, "清理用户临时文件 -  " + userTempPath + "...! \n" + "time is: "
					+ new Timestamp(System.currentTimeMillis()).toString());
			SDCardUtil.deleteFolder(userTempPath);
			LogUtils.logd(TAG, "清理用户临时文件完毕 -  "  + "...! \n" + "time is: "
					+ new Timestamp(System.currentTimeMillis()).toString());
			}
	}
			
	
	LogUtils.loge("deletefolder", "BS_进入公共删除");
			//2,清理SD卡公共临时文件 
			String commonTempPath = SDCardUtil.getPictureTempPathForUnlogin();
			LogUtils.loge("deletefolderpath", commonTempPath);
			if(!StringUtils.isBlank(commonTempPath)){
			LogUtils.logd(TAG, "清理公共临时文件 -  " + commonTempPath + "...! \n" + "time is: "
					+ new Timestamp(System.currentTimeMillis()).toString());
			SDCardUtil.deleteFolder(commonTempPath);
			LogUtils.logd(TAG, "清理公共临时文件完毕- " +  "...! \n" + "time is: "
					+ new Timestamp(System.currentTimeMillis()).toString());
			}
		}
		
	}

	/***********************************************************
	 * 公用方法： 获取 TGT , 1 ,从Application获取 ， 2 ,从sharePreference获取
	 ************************************************************/

	public String getMyTgt() {

		// 1,直接从Application获取就得了。。不用从SharePreference里取了。
		String tgt = getTgt();
		if (StringUtils.isBlank(tgt)) {
			// 2,从shareprefercen里获取
			tgt = StorageUtils.getLoginTGT(getApplicationContext());
		}
		// 如果tgt还是为"" 或者 null;
		if (StringUtils.isBlank(tgt)) {
			// 用户现在必须注销后，重新登录，才能建立新的TGT.
			// 如果用户不注销而选择正常退出时
			// SplashActivity所以应该根据本地tgt是否为null或者"" 来判断是否自动登录
			return "";
		}
		return tgt;
	}

	/***********************************************************
	 * 公用方法： 获取 sessionId , 1 ,从Application获取 ， 2 ,从sharePreference获取
	 ************************************************************/

	// 从内存或者sharepreference获取sessionId
	public String getMySessionId() {
		// 1, 从内存获取sessionId
		String sessionId = getSessionId();

		if (StringUtils.isBlank(sessionId)) {
			// 2,从shareprefercen里获取
			sessionId = StorageUtils.getSessionId(getApplicationContext());
		}
		// 如果sessionId还是为"" 或者 null;
		if (StringUtils.isBlank(sessionId)) {
			return "";
		}

		return sessionId;
	}

	public WeiboShareContent getWeiboShareContent() {
		return weiboShareContent;
	}

	public void setWeiboShareContent(WeiboShareContent weiboShareContent) {
		this.weiboShareContent = weiboShareContent;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getCustomerInfoCompleted() {
		return customerInfoCompleted;
	}

	public void setCustomerInfoCompleted(String customerInfoCompleted) {
		this.customerInfoCompleted = customerInfoCompleted;
	}

	/*******************************************************************
	 * 获取渠道编码 2012-09-19 added by edwar
	 *******************************************************************/

	public static String getChannelCode(Context context) {

		String code = getMetaData(context, "BaiduMobAd_CHANNEL");
		if (code != null) {
			return code;
		}
		return "";
	}

	private static String getMetaData(Context context, String key) {
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			Object value = ai.metaData.get(key);
			if (value != null) {
				return value.toString();
			}

		} catch (Exception e) {
			LogUtils.loge("BasicApplication", "获取渠道编码出错");
		}
		return null;
	}
}
