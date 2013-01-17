package com.mama100.android.member.global;



public class AppConstants {

	
	//======================================//
	
	/********************************************************
	 * sharepreferences 用到的 key 变量
	 *******************************************************/
	
	public static final String IS_FIRST_OPEN = "isfirstopen";
	
	//用户账户
	public static final String LOGIN_ACCOUNT_KEY = "account";
	public static final String LOGIN_PWD_KEY = "pwd";
	//用户sso
	public static final String LOGIN_TGT = "tgt";
	public static final String SESSIONID = "sessionid";
	//用户weibo
	

	//用户个人信息 , 5 项
	public static final String NICK_NAME = "nickname";
	public static final String BABY_NAME = "babyname";
	public static final String BABY_SEX = "babysex";
	public static final String BABY_BIRTH = "babybirth";
	public static final String REGPOINT_FLASH = "regpointflash";
	public static final String REG_POINT_BALANCE = "regpointbalance";
	
	
	/*************************************************************************
	 * 消息详情界面用到的 变量
	 ****************************************************************************/
	public static final String IS_FROM_NOTIFICATION = "is_from_notification";//消息详情判断点击是否从通知栏点击进来
	public static final int COMMON_ID = 11111;
	
	/*****************************************************************************
	 * 
	 ******************************************************************************/
	
	public static final String URL_MY_SHOP = "user.htm?action=myshop&cid={0}";
	public static final String URL_POINT_GUIDE = "point.htm?action=guide";
	
	
	//Action Name
	public static final String INITIAL_SERVICE_NAME="com.biostime.service.BootService";

	
	//URL Address
	public final static int START_DISPLAY_MAIN_UI = 10000;
	public final static int UPDATE_APK_FAIL = 20000;
	public final static int NO_SDCARD_WARNING = 20007;
	public final static int CHECK_APP_VERSION = 20004;
	public final static int CHECK_APP_VERSION_FINISH = 20005;
	public final static int CHECK_APP_VERSION_EXCEPTION = 20006;
	public static final int SHOP_LOGIN = 10001;
	public static final int USER_LOGIN = 10001;
	
	//LENGTHS--根据得到条码的长度，判断是下面哪种码
	// 范概念的条码，不管具体哪一种TYPE
	public static final int TYPE_GENERAL_CODE = 50;
	//序列号
	public static final int TYPE_SERIAL_CODE = 12;
	//防伪码
	public static final int TYPE_ANTI_FAKE_CODE = 16;
	//会员卡号
	public static final int TYPE_MEMBER_CARD = 16;
	//产品条码
	public static final int TYPE_PRODUCT_BARCODE = 13;
	
	 //用户输入 的是 手机号码 
	  public static final String INPUT_MOB = "input_mob";
	  //用户输入 的是 会员卡号 
	public static final String INPUT_CARD = "input_card";
	
	//Debug 测试 专用 
	public static final boolean Debug = false;
	
	//用于后台检查数据版本 及下载
	
	public static final int START_CHECK_NET  = 79;
	public static final int FINISH_CHECK_NET  = 78;
	public static final int START_CHECK_DATA  = 80;
	public static final int FINISH_CHECK_DATA  = 81;
	public static final int UPDATING_DATA  = 82;
	public static final int NO_DATA_NEED_UPDATE  = 83;
	public static final int FINISH_NO_DATA_UPDATE  = 85;
	public static final int UPDATE_DATA_SUCCESSFUL  = 84;
	
	
	
	

	/******************************* 
	/* BackgroundService 用的， 用于判断进入或者退出应用    * 
	/*******************************/
	
	//打开mama100应用
	public final static int COME_IN = 2000;
	//关闭mama100应用
	public final static int GO_OUT = 2002;
	//后台从服务器获取信息时间间隔
	public static long UPLOAD_SERVICE_MIN_INTERVAL = 5*60*1000;  //5分钟
	
	
	/******************************* 
	/* 系统存放路径    * 
	/*******************************/
	//因为不能一下子建一个/mama100_data/storepic/的文件夹，必须一步一步来，所以才有ROOT_FOLDER_PATH，PICTURE_STORE_PATH等
	public  static String ROOT_FOLDER_PATH = "/mama100_data/"; //mama100数据在sdcard里存放的路径
	public  static String PICTURE_STORE_PATH ="/storepic/"; //mama100首页活动的图片及压缩过的照片在sdcard里存放的路径，可覆盖
	public  static String PICTURE_TEMP_PATH = "/temppic/"; //mama100系统拍摄照片及临时照片在sdcard里存放的路径，多张，要删除
	public  static String APK_FOLDER_PATH = "/apk/";//系统启动时，自动在手机Sdcard创建目录 
	
	/***********************************
	 *  拍照临时照片 或者 保存本地的 文件名
	 ***********************************/
	
	public final static String TEMP_CROP_PICTURE_NAME = "/temp_crop_pic.jpg";//裁剪后的
	public final static String TEMP_CROP_PICTURE_NAME2 = "/temp_crop_pic2.jpg";//裁剪后的
	public final static String TEMP_BIG_PICTURE_NAME = "/temp_big_pic.jpg";//未经裁剪的大图, 无后缀，代码里加
	public final static String TEMP_STORE_PICTURE_NAME = "/temp_store_avatar.jpg";//保存本地的头像图,用于没有网络的情况
	public final static String TEMP_STORE_ACT_NAME = "/temp_store_act_pic";//保存本地的活动图(无后缀，代码里加),用于没有网络的情况
	/*********** 保存图片时，图片的质量压缩比  *************/
	public final static int BITMAP_COMPRESS =  100;
	public final static boolean TAKE_PHOTO_IS_USE_LOCAL_URI = true; //拍照时，是否自己定义Uri,如果false,则证明从媒体库获取系统生成图片的uri
	
	
	//找回密码 间隔时间 ，单位秒
	public final static int INTERVAL_TIME = 60;
	
    /***************************************************
	 * 新浪微博的常量
	 ***************************************************/
	public static final String XURL_ACTIVITY_CALLBACK = "letsgoweibo://AuthBackActivity.com"; //网页授权后，返回的URL，此处自定义为letsgoweibo://AuthBackActivity.com
	public static final String XWEIBO_APP_KEY = "553250813";  //宝宝通移动应用标记
	public static final String XWEIBO_APP_SECRET = "f596ff92288322c03361180875c987ac";  //宝宝通移动应用密钥
	public static final String XWEIBO_MAMA100_UID = "1965962870";  //妈妈100微博UID
	
	/***************************************************
	 * 服务器端各类ACTION
	 ***************************************************/
	//不需要sso
		public static final String REGISTER_ACTION = "/user/register.action";
		public static final String REGISTER_BY_EMAIL_ACTION = "/user/registerByEmail.action";
		public static final String VERIFY_CODE_ACTION = "/user/verifyCode.action";
		public static final String GET_PWD_ACTION ="/user/getLoginPwd.action";
		public static final String CHECK_VERSION_ACTION ="/sys/checkAppVer.action";
		public static final String PULL_MESSAGE_ACTION ="/msg/newestMessage.action";
		
		//<!-- CRM会员登录 version2.2 -->
		public static final String CRM_MEMBER_LOGIN_ACTION ="/user/crmMemberLogin.action";
		//<!-- 第三方帐号登录 version2.2 -->
		public static final String THIRD_PARTY_USER_LOGIN_ACTION ="/user/loginByThirdPartyUser.action";
		//<!-- 未登录，进入主页,获取服务器图片  version2.2 -->
		public static final String GET_UNLOGIN_PICS = "/user/actBigPic.action";

		//用户身边的母婴店  version2.3 -->
		public static final String USER_NEAR_SHOP = "/mall/side/sideShopList.action";
		//未登陆时的详情接口  version2.3 -->
		public static final String SHOP_DETAIL_NOT_LOGIN="/mall/side/relationShopDetail.action";

		
		
	//需要sso
		public static final String GET_MESSAGE_DETAIL_ACTION ="/sec/msg/msgDetailV2P2.action";
//		public static final String GET_MESSAGE_DETAIL_ACTION ="/sec/msg/msgDetailV2P2.action";
		public static final String HOME_ACTION ="/sec/homeV2P2.action";
		public static final String CHANGE_PWD_ACTION ="/sec/user/editLoginPwdV2P2.action";
		public static final String GET_PROFILE_ACTION ="/sec/user/userInfoV2P2.action";
		
		
		//<!-- 更新用户资料 version2.2 -->
		public static final String UPDATE_PROFILE_ACTION ="/sec/user/updateInfoV2P2.action";
		
		//<!-- 妈网用户,激活(发送验证码,激活)  version2.2 -->
		public static final String ACTIVATE_ECARD_ACTION ="/sec/user/userActivateECard.action";
		
		//<!-- CRM会员手机登录， 更新用户收货地址信息  version2.2 -->
		public static final String UPDATE_CRM_USER_RECEIVE_ADDRESS_INFO ="/sec/user/updateCrmUserInfo.action";
		
		//<!-- 用户账户设置，获取用户收货地址信息  version2.2 -->
		public static final String GET_CRM_USER_RECEIVE_ADDRESS_INFO ="/sec/user/crmuserInfo.action";
		
		//<!-- 用户账户设置，更新宝宝信息  version2.2 -->
		public static final String UPDATE_BABY_INFO ="/sec/user/updateUserChild.action";
		
		
		
		public static final String FEEDBACK_ACTION ="/sec/sys/feedbackV2P2.action";
		
		//新浪微博分享
		public static final String SHARE_NOTIFI_ACTION ="/sec/weibo/photoShareV2P2.action";
		
		//<!--绑定第三方的  version2.2 -->
		public static final String BINDING_ACTION ="/sec/weibo/bindingV2P2.action";
		//<!--解绑定第三方的  version2.2 -->
		public static final String UNBINDING_ACTION ="/sec/weibo/rmBindingV2P2.action";
		
		
		//消息
		public static final String GET_MESSAGE_LIST_ACTION ="/sec/msg/msgListV2P2.action";
	    //积分
		public static final String GET_POINT_LIST_ACTION ="/sec/point/pointListV2P2.action";
		
		public static final String GET_EXCHANGE_CODE_ACTION ="/sec/point/getExchangeCodeV2P2.action";//取兑换码
		public static final String GET_EXCHANGE_CODE_STATUS_ACTION ="/sec/point/getExchgCodeStatusV2P2.action";//取兑换码状态
		//自助积分
		public static final String DIY_POINT_VERIFY_ACTION ="/sec/point/diyPointVerifyV2P2.action";
		public static final String DIY_POINT_SUBMIT_ACTION ="/sec/point/diyPointSubmitV2P2.action";
		
		//1.36版本添加
		public static final String DIY_POINT_VERIFY_BY_SCAN_ACTION ="/point/diyPointVerifyByScan.action";
		public static final String DIY_POINT_GET_SIDE_SHOP_ACTION = "/mall/side/pointSideShop.action";
		public static final String DIY_POINT_GET_RELATIVE_SHOP_ACTION = "/sec/mall/side/pointRelationShopV2P2.action";
		
		//主界面刷新
		public static final String REFRESH_HOMEPAGE_ACTION = "/sec/refreshHomeV2P2.action";
		//上传log文件
		public static final String UPLOAD_LOG_ACTION = "/sec/sys/uploadLogFileV2P2.action";
		//用户领取积分
		public static final String POINT_OBTAIN_ACTION = "/sec/point/pointObtainV2P2.action";
		
		//登录时，查看用户关注的母婴店  version2.3 -->
		public static final String USER_CONCERNED_SHOP = "/sec/mall/side/relationShopListV2P2.action";
		//登录时,打开母婴店详情接口  version2.3 -->
		public static final String SHOP_DETAIL_LOGIN="/sec/mall/side/relationShopDetailV2P2.action";
		
	/***********************************************************
	 *  页面输入值的验证类型
	 ***********************************************************/
		public static final int CHECK_MOBILE = 1000;
		public static final int CHECK_USERNAME = 1001;
		public static final int CHECK_PASSWORD = 1002;
		public static final int CHECK_NICKNAME = 1003;
		public static final int CHECK_SERIALNUMBER = 1004;
		public static final int CHECK_ANTIFAKECODE = 1005;
		public static final int CHECK_RECEIVER_ADDRESS = 1006;
		public static final int CHECK_CUSTOMER_NAME = 1007;
		public static final int CHECK_EMAIL = 1008;
		public static final int CHECK_BABY_BIRTH = 1009;
		public static final int CHECK_RECEIVER_ADDRESS2 = 1010;
		
		
		
	/************************************************************
	 * 	单点登录用到的 常量
	 *************************************************************/
		public static final String TICKET = "ticket";
	public static final String TICKET_URL = "/v1/tickets/";

		/***************************************************
		 *
		 **********************************************/
		//因为设计的px是基于台式电脑屏幕的，而手机px大小肯定不一样的。需要转换。
		public static float ratio = 0.56206f;//480/854的比值,480是android手机标准宽度分480份。854是勇哥的设计电脑的px值
		//专门用来熟悉，测试，及验证系统流程的流程TAG
		public static String PROGRESS_TAG = "progress";
		public static String PROGRESS_TAG2 = "progress2";
//		public static final String IS_WANTTO_DOWNLOAD = "is_wantto_download";//研究用户习惯，是否想要下载，如果不想，下次进来主页不会自动更新软件版本。

		/***************************************************
		 * 部分必须用栈管理器来维护的 Activity, 不是所有的activity都需要放进该管理器内，目前，只有homepage 和 splashActivity
		 * homepage: 只有这个界面，没有finish(),且有退出全程序的 出口。
		 * splash: 因为登录后，自动打开主页，无法关闭splash. 且 如果在登录和注册点返回，也要回到splash, splash 不能 finish()
		 ******************************************************/
		public static String ACTIVITY_MAIN_HOMEPAGE = "HomePageActivity";
		public static String ACTIVITY_SPLASH_HOMEPAGE = "SplashActivity";
		public static String ACTIVITY_SETTING_HOMEPAGE = "SettingHomeActivity";//以后用
		public static String ACTIVITY_LOGIN_HOMEPAGE = "LoginActivity";//以后用
		public static String ACTIVITY_CRM_LOGIN_HOMEPAGE = "LoginCRMActivity";//以后用

		
		

		/******************************************************
		 *  added by edwar,为1.2版的多种注册登录增加的常量--START
		 *  2012-09-21
		 ******************************************************/
		
		public static String LAST_APP_VERSION = "lastAppVersion";
		/****************** Action 文本  ********************/
		//未登录，进入主页
		public static String UNLOGIN_INTO_HOMEPAGE = "unlogin_into_homepage";
		
		//未登录，进入主页点击积分栏的文本
		public static String UNLOGIN_INTO_REGPOINT_HISTORY_PAGE = "unlogin_into_regpoint_history_page";
		
		//未登录，进入自助积分，点击“验证”按钮的文本
		public static String UNLOGIN_INTO_REGPOINT_YOURSELF_PAGE = "unlogin_into_regpoint_yourself_page";
		
		//未登录，进入主页点击"请登录"按钮，进入登录
		public static String UNLOGIN_INTO_LOGIN_PAGE = "unlogin_into_login_page";
		
		//未登录，在主页点击左上角消息按钮，进入消息主页
		public static String UNLOGIN_INTO_MESSAGE_PAGE = "unlogin_into_message_page";
		
		//未登录，在主页点击主页广告，进入消息详情主页
		public static String UNLOGIN_INTO_MESSAGE_DETAIL_PAGE = "unlogin_into_message_detail_page";
		
		//未登录，在主页点击个人信息按钮，进入个人信息主页
		public static String UNLOGIN_INTO_PROFILE_PAGE = "unlogin_into_profile_page";
		
		//未登录，在主页点击“我的会员卡”，进入我的会员卡主页
		public static String UNLOGIN_INTO_MEMBER_CARD_PAGE = "unlogin_into_membercard_page";
		
		//未登录，在主页点击“拍乐秀”，进入拍乐秀分享主页，拍乐秀可以直接进入，但是点击分享，就必须要登录
		public static String UNLOGIN_INTO_TAKE_PHOTO_SHARE_PAGE = "unlogin_into_take_photo_share_page";
		
		//未登录，在主页点击设置，进入设置界面，点击账户设置，进入账户设置主页
		public static String UNLOGIN_INTO_ACCOUNT_SETTING_PAGE = "unlogin_into_account_setting_page";
		
		//未登录，在主页点击设置，进入设置界面，点击修改密码，进入修改密码主页
		public static String UNLOGIN_INTO_MODIFY_PWD_PAGE = "unlogin_into_modify_pwd_page";
		
		//未登录，在主页点击设置，进入设置界面，点击意见反馈，进入意见反馈主页
		public static String UNLOGIN_INTO_FEEDBACK_PAGE = "unlogin_into_feedback_page";
		
		//未登录，在主页点击母婴店，进入“母婴店”界面，点击“关注的母婴店”
		public static String UNLOGIN_INTO_CONCERNED_BABY_SHOP = "unlogin_into_interested_babyshop_page";

		public static boolean NEED_TRACING = false;
		public static boolean NEED_TRACING2 = false;

		public static final String TIME_CONSUME = "timeConsume";
		
		/****************** 请求编号 REQUEST_CODE ********************/
		
	
		
		//未登录，进入主页
		public static final int REQUEST_CODE_UNLOGIN_INTO_HOMEPAGE = 1000000;
		
		//未登录，在主页点击左上角消息按钮，进入消息主页
		public static final int REQUEST_CODE_UNLOGIN_INTO_MESSAGE_PAGE = 1000001;
		
		//未登录，在主页点击主页广告，进入消息详情主页
		public static final int REQUEST_CODE_UNLOGIN_INTO_MESSAGE_DETAIL_PAGE = 1000002;
		
		//未登录，在主页点击个人信息按钮，进入个人信息主页
		public static final int REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE = 1000003;
		
		//未登录，在主页点击“我的会员卡”，进入我的会员卡主页
		public static final int REQUEST_CODE_UNLOGIN_INTO_MEMBER_CARD_PAGE = 1000004;
		
		//未登录，在主页点击“拍乐秀”，进入拍乐秀分享主页，拍乐秀可以直接进入，但是点击分享，就必须要登录
		public static final int REQUEST_CODE_UNLOGIN_INTO_TAKE_PHOTO_SHARE_PAGE = 1000005;
		
		//未登录，在主页点击设置，进入设置界面，点击账户设置，进入账户设置主页
		public static final int REQUEST_CODE_UNLOGIN_INTO_ACCOUNT_SETTING_PAGE = 1000006;
		
		//未登录，在主页点击设置，进入设置界面，点击修改密码，进入修改密码主页
		public static final int REQUEST_CODE_UNLOGIN_INTO_MODIFY_PWD_PAGE = 1000007;
		
		//未登录，在主页点击设置，进入设置界面，点击意见反馈，进入意见反馈主页
		public static final int REQUEST_CODE_UNLOGIN_INTO_FEEDBACK_PAGE = 1000008;
		//未登录，进入主页点击积分栏的文本
		public static final int REQUEST_CODE_UNLOGIN_INTO_REGPOINT_HISTORY_PAGE = 1000009;
		//未登录，进入自助积分，点击验证
		public static final int REQUEST_CODE_UNLOGIN_INTO_REGPOINT_YOURSELF_PAGE = 1000010;
		//未登录，进入主页点击"请登录"按钮，进入登录
		public static final int REQUEST_CODE_UNLOGIN_INTO_LOGIN_PAGE = 1000011;
		//未登录，进入账户设置-->完善收货人地址，点击"提交"按钮，进入登录
		public static final int REQUEST_CODE_UNLOGIN_INTO_COMPLETE_ADDRESS_PAGE = 1000012;
		
		//未登录，进入账户设置-->点击"绑定新浪微博"按钮，进入登录
		public static final int REQUEST_CODE_UNLOGIN_INTO_BIND_SINA_WEIBO_PAGE = 1000013;
		
		//未登录，进入设置-->关于我们 --> 点击"关注妈妈100微博"按钮，进入登录
		public static final int REQUEST_CODE_UNLOGIN_INTO_FOLLOW_MAMA100_PAGE = 1000014;
		
		//未登录，在主页，进入母婴店--> 点击"关注的母婴店"按钮，进入登录
		public static final int REQUEST_CODE_UNLOGIN_INTO_CONCERNED_BABYSHOP_PAGE = 1000015;
		
		//未登录，在选择门店页面--> 点击"登陆"按钮，进入登录
		public static final int REQUEST_CODE_UNLOGIN_INTO_REGPOINT_SELECT_SHOP_PAGE = 1000016;
		
		//added by liyang 2012-12-27 
		//未登录  在设置页登陆进入登陆流程，登陆成功，返回设置页
		public static final int REQUEST_CODE_UNLOGIN_INTO_SETTING_PAGE = 1000017;
		
		/******************************************************
		 *  added by edwar,为1.2版的多种注册登录增加的常量--END
		 *  2012-09-21
		 ******************************************************/
		
		public static final int REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_SHOP_SELECT = 2000000;
		
}
