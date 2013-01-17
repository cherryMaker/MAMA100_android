/**
 * 
 */
package com.mama100.android.member.activities;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.babyshop.BabyShopActivity;
import com.mama100.android.member.activities.message.MessageHomeActivity;
import com.mama100.android.member.activities.photo.TakePhotoActivity;
import com.mama100.android.member.activities.regpoint.ActivateECardActivity;
import com.mama100.android.member.activities.regpoint.RegPointHomeActivity;
import com.mama100.android.member.activities.setting.FeedBackActivity;
import com.mama100.android.member.activities.setting.SettingHomeActivity;
import com.mama100.android.member.activities.user.EditProfileActivity;
import com.mama100.android.member.asynctask.AsyncBitmapTask;
import com.mama100.android.member.asynctask.AsyncDrawableTask;
import com.mama100.android.member.bean.HomeImageItem;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.sys.GetUnloginHomePicReq;
import com.mama100.android.member.domain.sys.HomeReq;
import com.mama100.android.member.domain.sys.HomeRes;
import com.mama100.android.member.domain.sys.RefreshHomeRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BackStackManager;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.service.BootService;
import com.mama100.android.member.util.DesUtils;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.PictureUtil;
import com.mama100.android.member.util.SDCardUtil;
import com.mama100.android.member.util.StorageUtils;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.widget.dialog.SureDialog;
import com.mama100.android.member.widget.scrollview.HorizontalPager;

/**
 * <p>
 * Description: HomePageActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 应用程序主界面
 * 
 * modified by edwar 2012-11-06,系统代码重构+性能优化
 * 
 * modified by liyang 2012-11-27,登陆流程修改（优化）
 * 主要是在未登录的情况下点击头像先进入登陆流程。
 */
@SuppressLint("ParserError")
public class HomePageActivity extends PhotoActivity implements OnClickListener {

	/*******************************
	 * 界面上面图片滚动栏用到的变量
	 *******************************/
	List<HomeImageItem> itemList = new ArrayList<HomeImageItem>();

	// 推荐活动 支持左右滑动的控件
	private HorizontalPager actPager;
	// 自动滑动推荐活动相关对象
	private boolean actThreadCanceled;
	private Thread actAutoFlyThread;// 每个六秒，自动移到
	private Handler actAutoFlyHandler;

	/*******************************
	 * 界面上面圆点滚动栏用到的变量
	 *******************************/
	// 显示当前是第几个活动的小圆点
	private LinearLayout actIdxContainer;

	/*******************************
	 * 个人信息栏用到的变量
	 ********************************/
	public String TAG = this.getClass().getSimpleName();
	private int size; // 活动个数

	/**********************************
	 * 启动 BootService
	 **********************************/
	private Intent update_software_intent = null;// 更新软件版本的意图
	private int DEMO_SIZE = 1; // 主页网络不好的情况下，默认的体验活动图片个数

	/*********************************
	 * 用于及时释放内存的变量
	 *********************************/
	private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
	private AsyncBitmapTask imageAvtarTask = null;
	private AsyncDrawableTask imageActPicTask = null;
	private RefreshHomeValueTask task = null;
	private TextView regpoint_balance;
	private TextView regpoint_tv; // 用于未登录的情况
	
	//用于判断onResume是在onActivityResult被执行后执行，过滤那种back-onReume的情况。
	//如果是， 则不去调用refreshHomeTask,防止首次登录时，refreshHomeTask在HomeAction未执行完的时候，执行导致无sessionId
	private boolean isAfterOnActivityResult = false;
	
	private ImageView avatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.homepage3);
		Intent intent = getIntent();
		/**************************************************************************
		 ***** added by edwar 2012-09-20 判断是否 未登录的情况下打开主页 ***
		 **************************************************************************/
		if (!StringUtils.isBlank(intent.getAction())
				&& intent.getAction().equalsIgnoreCase(
						AppConstants.UNLOGIN_INTO_HOMEPAGE)) {
			setPreviousUnLoginStatus(true);
			setUnlogin(true);
			BasicApplication.getInstance().setAutoLogin(false);
		} else {
			setUnlogin(false);
			setPreviousUnLoginStatus(false);
			BasicApplication.getInstance().setAutoLogin(true);
		}
		/**************************************************************************
		 ***** added by edwar 2012-09-20 判断是否 未登录的情况下打开主页 ***
		 **************************************************************************/



		/**************************************************************************
		 * ****** 公共方法 适用于 未登录 和 已登录 两种情况 added by edwar 2012-09-20 *
		 * ************************************************************************/
		setupViews();
		setupTopbarLabels();
		setBackgroundPicture(R.drawable.bg_wall);

		loadHomePageData();
		update_software_intent = new Intent(getBaseContext(), BootService.class);
		// 如果用户想每次都更新，则才打开软件更新服务
		BasicApplication.getInstance().setCurrentActivity(this);
		bootupSoftwareUpdateService(update_software_intent);
		// 清除之前未关闭的splash
		BackStackManager.getInstance().removeActivity(
				AppConstants.ACTIVITY_SPLASH_HOMEPAGE);
		// 将主页放进栈里
		BackStackManager.getInstance().putActivity(
				AppConstants.ACTIVITY_MAIN_HOMEPAGE, this);
	
		BasicApplication.getInstance().setToExit(false);

		/**************************************************************************
		 * ****** 公共方法 适用于 未登录 和 已登录 两种情况 added by edwar 2012-09-20 *
		 * ************************************************************************/

		// 检查及停止后台服务
		checkAndStopBackgroundService();
		// 清除通知栏的消息
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(AppConstants.COMMON_ID);

	}
	

	/**
	 * 未登录的情况下，显示欢迎积分文字
	 */
	private void showWelcomeWord() {
		if (regpoint_tv == null)
			regpoint_tv = (TextView) findViewById(R.id.regpoint_tv);
		if (regpoint_balance == null)
			regpoint_balance = (TextView) findViewById(R.id.reg_balance);

		if (isUnlogin()) {
			regpoint_tv.setText(R.string.welcome_word);
			regpoint_tv.setTextColor(getResources().getColorStateList(R.color.mama100Orange));
			regpoint_balance.setVisibility(View.GONE);
			findViewById(R.id.question).setVisibility(View.GONE);
			regpoint_tv.setClickable(true);
			regpoint_tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_HOMEPAGE);
					return;
				}
			});

		} else {
			regpoint_tv.setText("积分余额：");
			regpoint_tv.setClickable(false);
			regpoint_balance.setVisibility(View.VISIBLE);
			findViewById(R.id.question).setVisibility(View.VISIBLE);
		}

	}

	/**************************************************************************
	 * Android 生命周期 方法
	 ***************************************************************************/

	// 在EditProfileActivity通过FLAG_ACTIVITY_CLEAR_TOP|FLAG_ACTIVITY_SINGLE_TOP进来
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// 有了RefreshTask,这些就用不着了
	}

	@Override
	public void onResume() {
		super.onResume();
		BasicApplication.getInstance().setHomepageAlreadyStart(true);
		
		if(AppConstants.NEED_TRACING)
		Debug.startMethodTracing("activity_trace");
		
		
		StatService.onResume(this);// 百度统计
		// MobileProbe.onResume(this);//CNZZ统计

		BasicApplication.getInstance().setCurrentActivity(this);
		showWelcomeWord();

		if (isUnlogin()) {
			//added by edwar 2012-11-07 为了主页登录->刷新头像
			setPreviousUnLoginStatus(true);
			setUnlogin(true);
			BasicApplication.getInstance().setAutoLogin(false);
			
			
			initialAvatarIntoViewsForUnlogin();
			//modified by edwar, 2012-11-06 直接在xml里赋值未登录的默认头像。。避免获取未null的头像
			modifyMessageNum(0);
			hideNickName();
			return;
		}

		if(isAfterOnActivityResult){
			isAfterOnActivityResult = false;//节省刷新
		}else{
		// 如果已经登录，但之前的状态是未登录状态，那么现在进来就尝试更新下主界面
			refreshHomeValues(); // 加载主页其它信息
		if (isPreviousUnLoginStatus()) {
			BasicApplication application = BasicApplication.getInstance();
			resetValueIntoView(application); // 加载主页广告act
		} else {
		}
		}
		actThreadCanceled = false;
		if (actAutoFlyThread == null || !actAutoFlyThread.isAlive()) {
			startActThread();
		}
		// }
		if(AppConstants.NEED_TRACING)
		Debug.stopMethodTracing();
		
	
	}

	private void refreshHomeValues() {
		// 连接服务器，刷新界面数据
		HomeReq request = new HomeReq();
		task = new RefreshHomeValueTask(this);
		task.execute(request);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		LogUtils.loge(TAG, "onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		LogUtils.loge(TAG, "onRestoreInstanceState");
	}

	private void modifyMessageNum(int num) {
		/** 顶部栏未读消息数用到的变量 **/
		Button email_num_icon = ((Button) findViewById(R.id.email_num_icon));
		email_num_icon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doClickLeftBtn();
			}
		});
		
		// TODO 打开新页面，从服务器获取消息数量；以后恢复页面，从本地获取消息数。。
		if (num > 0) {
			email_num_icon.setVisibility(View.VISIBLE);
			if (0 < num && num < 100) {
				email_num_icon.setText(num + "");
			} else if (num >= 100) {
				email_num_icon.setText("" + 99);// 99,最大值
			}
		} else {
			// 隐藏那个底部的消息数TextView
			email_num_icon.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);// 百度统计
		// MobileProbe.onPause(this);//CNZZ统计

		if (isUnlogin()) {
			return;
		}
		actThreadCanceled = true;
		if (actAutoFlyThread != null)
			actAutoFlyThread.interrupt();
		//1.内存测试 start
		free = Runtime.getRuntime().totalMemory();
		LogUtils.loge(TAG, "用掉的内存 - " + (total-free) + "字节");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!isUnlogin()) {
			actThreadCanceled = true;
			if (actAutoFlyThread != null)
				actAutoFlyThread.interrupt();

			// 关闭更新软件服务
			shutdownSoftwareUpdateService(update_software_intent);

			if (task != null && task.isCancelled() == false) {
				task.cancel(true);
				task = null;
			}
			if (imageAvtarTask != null && imageAvtarTask.isCancelled() == false) {
				imageAvtarTask.cancel(true);
				imageAvtarTask = null;
			}
			if (imageActPicTask != null && imageActPicTask.isCancelled() == false) {
				imageActPicTask.cancel(true);
				imageActPicTask= null;
			}
		}

		/**********************
		 ** 公共方法 ***
		 *********************/
		BasicApplication.getInstance().setHomepageAlreadyStart(false);
		regpoint_balance = null;// 释放Html.fromHtml()引起的内存

		if (bitmapList != null && bitmapList.size() > 0) {
			for (int i = 0; i < bitmapList.size(); i++) {
				Bitmap bitmap = bitmapList.get(i);
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
			}
		}
		bitmapList.clear();
		bitmapList = null;

		if (itemList != null) {
			itemList.clear();
			itemList = null;
		}

		// 释放主界面滑动图片栏
		clearActPictureBar();
		// 释放内存
		clearImageViewMomery((ImageView) findViewById(R.id.homepage_avatar));
		BasicApplication.getInstance().setCurrentActivity(null);

	}

	/**
	 * 声明基本的view
	 */
	private void setupViews() {
		setTopScrollImageBar();
		setCenterUserProfileBar();
		setBottomMenuBar();
	}

	/**
	 * 设置顶部栏的值
	 */
	private void setupTopbarLabels() {
		setTopLogo(R.drawable.logo);
		setTopLabelVisibility(View.INVISIBLE);
		setLeftButtonImage(R.drawable.selector_no_email);
		setRightButtonImage(R.drawable.selector_setting);
	}

	/*********************************************************************
	 * 打开界面，将Parcelable的值填充各变量值 用到的方法 --START
	 ********************************************************************/

	/**
	 * 从RES 获取主界面的 变量值,并设置到视图里
	 * 
	 * @param data
	 */
	private void setValueIntoView(HomeRes response) {
		// 检查response
		if (response != null) {
			
			//  将用户个人头像显示出来, 耗时的先做
			String imgUrl = response.getAvatar();
			// 更新 头像路径
			BasicApplication.getInstance().setLastAvatarUrl(imgUrl);
			setAvatarIntoViews(imgUrl);
			
			

			// 如果为空，就下载；不为空，证明未登录的情况下，已经下载过主界面了。所以，不用再次下载
			if (itemList.isEmpty()) {
				itemList = response.getImgItems();
				setActPicturesIntoViews(itemList);
				BasicApplication.getInstance().setImgList(itemList);
			}

			
			// 设置个人昵称
			String nickname = response.getNick();
			setNickName(nickname);

			// 设置积分余额
			String pointbalance = response.getPoint();

			// 更新 用户积分
			regpoint_balance = (TextView) findViewById(R.id.reg_balance);
			String balance_html = "<font color=#ff6600>" + pointbalance
					+ "</font>" + "<font color=#808080>分</font> ";

			// 显示且保存积分余额
			regpoint_balance.setText(Html.fromHtml(balance_html));
			BasicApplication.getInstance().setLastRegpointBalance(pointbalance);

			// TODO AsyncTask 获取服务器的图片
			// String[] imageUrls = response.getImages();

			// 5,设置未读消息数
			int unread = Integer.parseInt(response.getMsgCount());
			BasicApplication.getInstance().setUnreadMsgSum(unread);
			modifyMessageNum(unread);

			// 设置微博

			BasicApplication.getInstance().setWeiboShareContent(
					response.getWeiboShareContent());
			BasicApplication.getInstance().setUsername(response.getUsername());
			BasicApplication.getInstance().setMobile(response.getMobile());
			BasicApplication.getInstance().setWeiboItems(
					response.getWeiboItems());
			BasicApplication.getInstance().setMid(response.getMid());
			BasicApplication.getInstance().setCustomerInfoCompleted(
					response.getCustomerInfoCompleted());
			setAsso(response.getIsAsso().equalsIgnoreCase("1") ? true : false);
		} else {
			if (isUnlogin()) {
				getUnloginPicsFromServer();
			} else {
				// 如果response为空，从本地获取对应该用户名的用户信息，及未读消息数
				// 目前不考虑这种情况，因为如果与服务器不能交互的话，登录都进不了这里。

				// TODO 服务器端给新的接口，获取服务器图片。
				setActPicturesIntoViews(null);
			}
		}
	}

	/**
	 * 把已经 获取的全局 变量,设置到视图里
	 * 
	 * @param data
	 */
	private void resetValueIntoView(BasicApplication application) {

		if (application != null) {
			// // 1,设置个人昵称
			// String nickname = application.getNickname();
			// setNickName(nickname);
			//
			// // 设置积分余额
			// String pointbalance = application.getLastRegpointBalance();
			// pointbalance = (StringUtils.isBlank(pointbalance)) ? "0"
			// : pointbalance;
			//
			// // 更新 用户积分
			// regpoint_balance = (TextView) findViewById(R.id.reg_balance);
			// String balance_html = "<font color=#ff6600>" + pointbalance
			// + "</font>" + "<font color=#808080>分</font> ";
			//
			// //2,显示且保存积分余额
			// regpoint_balance.setText(Html.fromHtml(balance_html));
			//
			// // 将用户个人头像显示出来
			// String imgUrl = application.getLastAvatarUrl();
			// //3,更新 头像路径
			// setAvatarIntoViews(imgUrl);
			// // TODO AsyncTask 获取服务器的图片
			// // String[] imageUrls = response.getImages();

			// 如果为空，就下载；不为空，证明未登录的情况下，已经下载过主界面了。所以，不用再次下载
			// 为了解决无网络的情况下，直接未登录进来，然后再有网络登录成功，回来后这种情况下，要加载主页的act图片。
			if (itemList==null||itemList.isEmpty()) {
				itemList = application.getImgList();

				clearActPictureBar();

				setActPicturesIntoViews(itemList);
			}
			// //
			// //4,设置未读消息数
			// int unread = application.getUnreadMsgSum();
			// modifyMessageNum(unread);

			// 5, 是否已经关联

		}
	}

	private void clearActPictureBar() {
		actIdxContainer.removeAllViews();
		int count = actPager.getChildCount();
		for (int i = 0; i < count; i++) {
			RelativeLayout relativelayout = (RelativeLayout) actPager
					.getChildAt(i);
			ImageView imageView = (ImageView) relativelayout.getChildAt(0);
			imageView.setImageBitmap(null);
		}
		actPager.removeAllViewsInLayout();
		actPager.removeAllViews();
	}

	/**
	 * 设置积分余额
	 */
	private void setPointBalance(String pointbalance) {
		if (isPreviousUnLoginStatus()
				|| (regpoint_balance!=null&&!regpoint_balance.getText().toString()
						.equalsIgnoreCase(pointbalance))) {
			String balance_html = "<font color=#ff6600>" + pointbalance
					+ "</font>" + "<font color=#808080>分</font> ";

			// 显示且保存积分余额
			regpoint_balance.setText(Html.fromHtml(balance_html));
			BasicApplication.getInstance().setLastRegpointBalance(pointbalance);
		}
	}

	/**
	 * 设置个人昵称
	 */
	private void setNickName(String nickname) {
		// 显示且保存nickname
		((TextView) findViewById(R.id.nickname)).setText(nickname);
		BasicApplication.getInstance().setNickname(nickname);
	}
	
	//未登录的情况下，隐藏昵称
	private void hideNickName(){
		((TextView) findViewById(R.id.nickname)).setText("");
	}

	private void setAvatarIntoViews(String imgUrl) {

		if (StringUtils.isBlank(imgUrl)) {
			// TODO 用户初次登录， 服务器没有用户的个人头像信息的情况
			// 方法一，用drawable，问题：图片容易变形
			// Drawable drawable = getResources().getDrawable(
			// R.drawable.testavatar);
			// imageView.setImageDrawable(drawable);

			// 方法二，用uri,因为现在直接用bitmap而放弃
			// Uri path = Uri.parse(
			// "android.resource://com.mama100.android.member/drawable/testavatar");
			// avatar.setImageURI(path);
			// 保存默认头像
			// BasicApplication.getInstance().setStoreAvatarUri(path);

			// 方法三，直接用bitmap

			// LogUtils.loge(TAG, "get avatar - " + 1 );
			// Toast.makeText(HomePageActivity.this, "get avatar - " + 1 ,
			// Toast.LENGTH_SHORT).show();
			// 如果本地保留了上次用户的头像，就用上传头像
			String filename = SDCardUtil.getPictureStorePath()
					+ AppConstants.TEMP_STORE_PICTURE_NAME;
			Bitmap bitmap = PictureUtil.getPictureFromSDcardByPath(filename);
			// 如果从本地获取bitmap为空
			if (bitmap == null) {
				// LogUtils.loge(TAG, "get avatar - " + 2 +
				// " from local sdcard  TEMP_STORE_PICTURE_NAME" );
				// Toast.makeText(HomePageActivity.this, "get avatar - " + 2 +
				// " from local sdcard  TEMP_STORE_PICTURE_NAME" ,
				// Toast.LENGTH_SHORT).show();
				bitmap = BitmapFactory.decodeResource(BasicApplication
						.getInstance().getResources(),
						R.drawable.default_avatar);
			}
			bitmapList.add(bitmap);
			BasicApplication.getInstance().setAvatarBitmap(bitmap);
			// LogUtils.loge(TAG, "get avatar - " + 3 );

		} else {
			// LogUtils.logi(TAG, "set avatar 1 - : "
			// + new Timestamp(System.currentTimeMillis()).toString());

			// 异步加载图片
			avatar.setTag(imgUrl);
			imageAvtarTask = new AsyncBitmapTask(HomePageActivity.this);
			imageAvtarTask.execute(avatar);
		}

		// 2,修改头像
		// 设置头像为本地默认的头像,用于在个人信息界面修改头像之后回到首页的头像更新
		// 对于异步头像获取，在获取过程中，先从本地加载头像， 然后更新服务器最新的。。

		// LogUtils.logi(TAG, "set avatar 2 - : "
		// + new Timestamp(System.currentTimeMillis()).toString());
		avatar.setScaleType(ScaleType.FIT_CENTER);

		//modified by edwar 2012-11-06
		Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
		if(bitmap!=null)
		setAvatar(bitmap);
		//modified end
		
		// LogUtils.logi(TAG, "set avatar 3 - : "
		// + new Timestamp(System.currentTimeMillis()).toString());

	}

	private void initialAvatarIntoViewsForUnlogin() {
		ImageView avatar = (ImageView) findViewById(R.id.homepage_avatar);
		Bitmap bitmap = BitmapFactory.decodeResource(BasicApplication
				.getInstance().getResources(), R.drawable.default_avatar);
		bitmapList.add(bitmap);
		BasicApplication.getInstance().setAvatarBitmap(bitmap);
		avatar.setScaleType(ScaleType.FIT_CENTER);
		setAvatar(bitmap);

	}

	private void setActPicturesIntoViews(List<HomeImageItem> list) {
		// 初始化可滑动分页的活动内容
		initActPagerItems(list);
		initActAutoFlyHandler();
	}

	/*********************************************************************
	 * 打开界面，将Parcelable的值填充各变量值 用到的方法 --END
	 ********************************************************************/

	// 跳转至拍了秀
	@Override
	public void setPhotoIntoAvatar() {
		// 跳转至拍了秀
		final Intent toShareAct = new Intent(getApplicationContext(),
				TakePhotoActivity.class).putExtra(
				TakePhotoActivity.INIT_PHOTO_URI, BasicApplication
						.getInstance().getCropImageStoreUri());
		startActivity(toShareAct);
	}

	// 获取消息总数
	private int getMessageCount() {
		return BasicApplication.getInstance().getUnreadMsgSum();
	}

	/**
	 * 设置底部菜单栏
	 */
	private void setBottomMenuBar() {

		// 拍乐秀
		final View photo = findViewById(R.id.bt_photo);

		setImageCropParams(widthPixels - 20, widthPixels - 20);
		photo.setId(PhotoActivity.TAKE_PHOTO);
		photo.setOnClickListener(this);

		// 母婴店
		findViewById(R.id.bt_bbshop).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// 测试母婴店界面
						startActivity(new Intent(HomePageActivity.this,
								BabyShopActivity.class));
						// goToPersonalProfilePage();
					}
				});
		// // 个人信息
		// findViewById(R.id.bt_profile).setOnClickListener(
		// new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// goToPersonalProfilePage();
		// }
		// });
		// 个人头像
		((ImageView) findViewById(R.id.homepage_avatar))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// modified by liyang 2012-11-27 START
						// 修改前直接跳到个人信息页面 goToPersonalProfilePage();
						// 修改后判断是否已经登陆 已登陆跳个人信息页面  未登录进入登陆流程
						if(isUnlogin()){
							goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE);
						}else {
							goToPersonalProfilePage();
						}
						// modified by liyang 2012-11-27 END
					}
				});

		// 积分通
		findViewById(R.id.bt_reg).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						goToRegpointHomePage();
					}
				});

	}

	/**
	 * 设置中部用户个人信息栏
	 */
	private void setCenterUserProfileBar() {
		avatar = (ImageView) findViewById(R.id.homepage_avatar);
		findViewById(R.id.question).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new SureDialog(HomePageActivity.this)
								.showDialog(R.drawable.question_helper_what_is_use_of_reppoint);
					}
				});
	}

	/*
	 * 设置顶部滚动栏
	 */
	private void setTopScrollImageBar() {
		actIdxContainer = (LinearLayout) findViewById(R.id.home_act_idx_container);
		// 初始化 活动图片栏
		actPager = (HorizontalPager) this.findViewById(R.id.home_act_pager);
		actPager.addOnScrollListener(actItemOnScrollListener);
	}

	/*************************************************************************
	 * 滚动界面用到的方法
	 *************************************************************************/

	/**
	 * 初始化推荐活动的界面元素 及 活动底部的小圆点
	 */
	private void initActPagerItems(List<HomeImageItem> itemList) {

		if (itemList != null && itemList.size() > 0) {
			size = itemList.size();
			for (HomeImageItem item : itemList) {
				String imgUrl = item.getImgUrl();
				RelativeLayout relative_layout = (RelativeLayout) View.inflate(
						getApplicationContext(),
						R.layout.back_picture_front_progress, null);
				ProgressBar progressBar = (ProgressBar) relative_layout
						.findViewById(R.id.front_progressbar);
				ImageView imageView = (ImageView) relative_layout
						.findViewById(R.id.back_imageview);
				if (StringUtils.isBlank(imgUrl)) {
					imageView.setImageDrawable(getResources().getDrawable(
							R.drawable.default_act));
					progressBar.setVisibility(View.GONE);
				} else {
					// 异步加载图片
					relative_layout.setTag(imgUrl);

					imageActPicTask = new AsyncDrawableTask(HomePageActivity.this);
					imageActPicTask.execute(relative_layout);
				}
				actPager.addView(relative_layout);
				imageView.setOnClickListener(actItemClickListener);
			}

			// 初始化活动的底部小圆点
			initActIdxContainer(itemList.size());
		} else {
			size = DEMO_SIZE;
			for (int i = 0; i < DEMO_SIZE; i++) {

				RelativeLayout relative_layout = (RelativeLayout) View.inflate(
						getApplicationContext(),
						R.layout.back_picture_front_progress, null);
				ProgressBar progressBar = (ProgressBar) relative_layout
						.findViewById(R.id.front_progressbar);
				ImageView imageView = (ImageView) relative_layout
						.findViewById(R.id.back_imageview);

				// 本地默认存放临时图片
				String filename = SDCardUtil.getPictureStorePath()
						+ AppConstants.TEMP_STORE_ACT_NAME + (i++) + ".jpg";
				Bitmap bitmap = PictureUtil
						.getPictureFromSDcardByPath(filename);
				// 如果从本地获取bitmap成功
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
					bitmapList.add(bitmap);
				} else
					imageView.setBackgroundResource(R.drawable.default_act);
				progressBar.setVisibility(View.GONE);
				actPager.addView(relative_layout);
				imageView.setOnClickListener(actItemClickListener);
			}

			// 初始化活动的底部小圆点
			initActIdxContainer(DEMO_SIZE);
		}
	}

	/**
	 * 推荐活动滑动事件监听器
	 */
	private HorizontalPager.OnScrollListener actItemOnScrollListener = new HorizontalPager.OnScrollListener() {

		@Override
		public void onViewScrollFinished(int currentPage) {

			// 获取要即将显示的下一页
			int nextPage = actPager.getNextPage();
			// Log.i(LOG_TAG, "nextPage: " + nextPage);

			if (nextPage != -1) {
				// 刷新小圆点索引位置
				refreshActIdx(nextPage);
			}

		}

		@Override
		public void onScroll(int scrollX) {
			// Log.i(LOG_TAG, "scrollX: " + scrollX);

			/*
			 * // 获取要即将显示的下一页 int nextPage = actPager.getNextPage();
			 * //Log.i(LOG_TAG, "nextPage: " + nextPage);
			 * 
			 * if (nextPage != -1) { // 刷新小圆点索引位置 refreshActIdx(nextPage); }
			 */

		}

	};

	/**
	 * 初始化推荐活动自动切换的消息处理器
	 */
	private void initActAutoFlyHandler() {

		actAutoFlyHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				int currentPage = actPager.getCurrentPage();

				// 如果当前是最后一页,则向左滑动
				if (currentPage == size - 1) {
					actPager.scrollLeft();
					return;
				}

				// 如果是第1页,则向右滑动
				if (currentPage == 0) {
					actPager.scrollRight();
					return;
				}

				// 如果上一次用户手动向右滑动了,则继续向右滑动
				if (actPager.isScrollRight()) {
					// Log.i(LOG_TAG, "scroll to right.");
					actPager.scrollRight();
				} else {
					// Log.i(LOG_TAG, "scroll to left.");
					actPager.scrollLeft();
				}

			}

		};

	}

	/**
	 * 启动自动切换推荐活动的线程
	 */
	private void startActThread() {

		actAutoFlyThread = new Thread() {
			public void run() {
				// added by edwar 2012-09-10, 防止程序正在关闭中的出现空指针
				if (isFinishing()) {
					return;
				}

				while (!actThreadCanceled) {

					try {
						Thread.sleep(6000); // 定时器，每隔6秒，自动移动一次
					} catch (InterruptedException e) {
						actThreadCanceled = true;
						// Log.i(LOG_TAG, "act auto fly thread interrupted.");
					}

					if (actAutoFlyHandler != null)
						actAutoFlyHandler.sendEmptyMessage(0);
				}

			}
		};

		actAutoFlyThread.start();

	}

	/*****************************************************************************
	 * 滚动圆点用到的方法
	 *****************************************************************************/

	/**
	 * 初始化推荐活动小圆点指示的界面元素
	 */
	private void initActIdxContainer(int itemSize) {
		// added by edwar 2012-09-10, 防止程序正在关闭中的出现空指针
		if (isFinishing()) {
			return;
		}

		for (int i = 0; i < itemSize; i++) {

			ImageView imgView = new ImageView(this);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(2, 0, 2, 0);

			imgView.setLayoutParams(params);

			if (i == 0) {
				imgView.setImageResource(R.drawable.point_select);
			} else {
				imgView.setImageResource(R.drawable.point_unselect);
			}
			actIdxContainer.addView(imgView);
		}

	}

	/**
	 * 刷新推荐活动的小园点索引位置
	 * 
	 * @param selIdx
	 */
	private void refreshActIdx(final int selIdx) {

		final int itemSize = size;

		Integer currentIdx = (Integer) actIdxContainer.getTag();
		if (currentIdx != null && currentIdx.intValue() == selIdx) {
			// Log.i(LOG_TAG, "same with current act idx.");
			return;
		}

		// Log.i(LOG_TAG, "set new idx: " + selIdx);
		actIdxContainer.setTag(selIdx);

		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// added by edwar 2012-09-10, 防止程序正在关闭中的出现空指针
				if (isFinishing()) {
					return;
				}
				for (int i = 0; i < itemSize; i++) {

					ImageView imgView = (ImageView) actIdxContainer
							.getChildAt(i);
					Boolean isSel = (Boolean) imgView.getTag();

					// 只更新有变化的小圆点
					if (i == selIdx) {
						if (isSel == null || !isSel.booleanValue()) {
							imgView.setImageResource(R.drawable.point_select);
							imgView.setTag(Boolean.TRUE);
						}
					} else {
						if (isSel == null || isSel.booleanValue()) {
							imgView.setImageResource(R.drawable.point_unselect);
							imgView.setTag(Boolean.FALSE);
						}
					}

				}

			}
		});

	}

	/*******************************************************************
	 * 其它方法
	 *******************************************************************/

	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		goToMessagePage();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		showExitDialog();
	}

	@Override
	public void doClickRightBtn() {
		super.doClickRightBtn();
		// //设置主页
		startActivity(new Intent(getApplicationContext(),
				SettingHomeActivity.class));
	}

	/***************************************
	 * 事件用到的方法
	 ***************************************/

	/**
	 * 推荐活动点击事件监听器
	 */
	private View.OnClickListener actItemClickListener = new View.OnClickListener() {

		public void onClick(View view) {
			goToDetailedMessagePage();
		}
	};

	/**********************************************************************************
	 * 底部菜单
	 **********************************************************************************/

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "意见反馈");// 设置图标
		menu.add(0, 1, 0, "设置");// 设置图标
		menu.add(0, 2, 0, "退出");// 设置图标
		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 0:
			startActivity(new Intent(getApplicationContext(),
					FeedBackActivity.class));
			break;
		case 1:
			startActivity(new Intent(getApplicationContext(),
					SettingHomeActivity.class));
			break;
		case 2:
			onBackPressed();
			break;

		}
		return true;

	}

	/***************************************************************************************
	 * 点击 个人信息， 从服务器获取用户个人信息
	 ***************************************************************************************/

	// 获取个人信息
	protected void goToPersonalProfilePage() {
		// 设置个人信息
		Intent intent = new Intent(getApplicationContext(),
				EditProfileActivity.class);
		startActivity(intent);
	}

	// 未登录状态下，获取主页图片
	protected void getUnloginPicsFromServer() {
		GetUnloginHomePicReq request = new GetUnloginHomePicReq();
		CustomAsyncTask task = new CustomAsyncTask(this);
		task.execute(request);
	}

	// 已登录状态下，获取主页图片
	protected void getloginDataFromServer() {
		HomeReq request = new HomeReq();
		CustomAsyncTask task = new CustomAsyncTask(this);
		task.execute(request);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	// 用于每次回到主界面时，后台从服务器获取最新数据后，刷新UI界面数据
	class RefreshHomeValueTask extends AsyncReqTask {
		public RefreshHomeValueTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			LogUtils.loge(TAG, "进入刷新");
			return UserProvider.getInstance(getApplicationContext())
					.refreshHomepageValue((HomeReq) request);

		}

		@Override
		protected void handleResponse(BaseRes response) {
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				// makeText(response.getDesc());
				return;
			}
			/*********************************************
			 * 将获取服务器的最新值给主界面
			 *********************************************/
			RefreshHomeRes res = (RefreshHomeRes) response;
			LogUtils.logi(AppConstants.PROGRESS_TAG2, res.toString());
			final String messageUnreadSum = res.getCount();
			final String nickname = res.getNick();
			final String point = res.getPoint();
			final String avatarUrl = res.getAvatar();

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// 4, 更改头像  ， 耗时的放前面
					updateAvatar(avatarUrl);
					// 1，更改消息
					modifyMessageNum(Integer.valueOf(messageUnreadSum));
					// 2，修改昵称
					setNickName(nickname);
					// 3, 修改积分余额
					setPointBalance(point);

				}
			});
		}

		// 更新用户头像。如果服务器获取的头像url与本地上次保留的头像url不一样，则证明有了新的头像
		protected void updateAvatar(String url) {
//			// 如果前一次是未登录状态，那么要刷新；
//			// 如果前一次是已登录状态，但是图片路径不一样了，也要刷新
//			if (isPreviousUnLoginStatus()
//					|| !BasicApplication.getInstance().getLastAvatarUrl()
//							.equalsIgnoreCase(url)) {
//				setUnlogin(false);
//				setPreviousUnLoginStatus(false);
//				BasicApplication.getInstance().setAutoLogin(true);
//				
//				// 更新 头像路径
//				BasicApplication.getInstance().setLastAvatarUrl(url);
//				// 设置头像为本地默认的头像,用于在个人信息界面修改头像之后回到首页的头像更新
//				ImageView avatar = (ImageView) findViewById(R.id.homepage_avatar);
//				avatar.setScaleType(ScaleType.FIT_CENTER);
//				clearImageViewMomery(avatar);
//
//				// avatar.setImageURI(BasicApplication.getInstance().getCropImageStoreUri());
//				// 如果之前是未登录状态，现在则需要重新加载图片
//				if (isPreviousUnLoginStatus()) {
//					setAvatarIntoViews(url);
//				} else {
//					// 如果之前已经是登录状态，则不需要重新加载，直接从本地获取图片
//					Bitmap bitmap = BasicApplication.getInstance()
//							.getAvatarBitmap();
//					if(bitmap!=null)
//					setAvatar(bitmap);
//				}
//			}
			//modified by edwar 2012-11-07 
			//修改之前复杂的逻辑为， 如果没有头像，就去mama100服务器系统拿，如果内存有，就直接取
			avatar.setScaleType(ScaleType.FIT_CENTER);
			Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
			if(bitmap==null||!BasicApplication.getInstance().getLastAvatarUrl()
					.equalsIgnoreCase(url)){
				BasicApplication application = BasicApplication.getInstance();
				setAvatarIntoViews(url);
			}else{
				setAvatar(bitmap);
			}
			//modified end
		}

	}
	private void setAvatar(Bitmap bitmap) {
		avatar.setImageBitmap(bitmap);
	}	
	
	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {

			// 未登录下获取图片
			if (request instanceof GetUnloginHomePicReq) {
				return UserProvider.getInstance(getApplicationContext())
						.getActPictures((GetUnloginHomePicReq) request);
			}
			// 已登录下获取界面信息
			else if (request instanceof HomeReq) {
				return UserProvider.getInstance(getApplicationContext())
						.getHomepageValueWithTicket((HomeReq) request);
			} else {
				return null;
			}

		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				setActPicturesIntoViews(null); // 为无网络情况，添加本地保留的图片
				makeText(response.getDesc());
				return;
			}
			if (response instanceof HomeRes) {
				if (isUnlogin()) {
					// 获取未登录情况下的图片。。并且
					HomeRes res = (HomeRes) response;
					itemList = res.getImgItems();
					setThirdPartyKeys(res);
					setActPicturesIntoViews(itemList);

				} else {
					String username = ((HomeRes) response).getUsername();
					BasicApplication.getInstance().setUsername(username);
					BasicApplication.getInstance().setMobile(((HomeRes) response).getMobile());
					// 加密保存用户名
					String encodedUsername = null;
					try {
						encodedUsername = DesUtils.encrypt(
								DesUtils.DES_COMMON_KEY, username);
						// 保存用户名
						StorageUtils.storeLoginAccountInClient(
								getApplicationContext(), encodedUsername);

					} catch (Exception e) {
						LogUtils.loge(TAG, "用户名加密及保存本地出现问题");
						LogUtils.loge(TAG, e.getMessage());
					}
					String mid = ((HomeRes) response).getMid();
					// 设置用户文件夹路径
					BasicApplication.getInstance().setAllFolderPath(mid);

					// 将值设置进页面
					setValueIntoView((HomeRes) response);

				}
			}
		}
	}

	/**
	 * 显示提示是否退出的对话框
	 */
	public void showExitDialog() {
		showmemberDialog(R.string.exit_warning_msg, new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 正常退出特有流程
				if (!isUnlogin()) {
					LogUtils.loge(TAG, "进入登录退出");
					checkAndStartBackgroundService();
					BasicApplication.getInstance().clearOnExit();
				} else {
					LogUtils.loge(TAG, "进入未登录退出");
					BasicApplication.getInstance().clearNormalInfoForUnlogin();
					System.exit(0);
				}
				closeDialog();
				finish();
				BackStackManager.getInstance().closeAllActivity();
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LogUtils.loge("HomepageActivity", "onConfigurationChanged");
	}

	// 防止恶意刷兑换码的类
	public static class ExCodeDetection {
		private static int requestTime = 0;
		private static long millTime = -1l;

		/**
		 * 每次请求兑换码前调用,防止恶意刷兑换码
		 * 
		 * @return true 允许通过
		 */
		public static boolean tryReqCode() {
			// 第一次
			if (millTime == -1l) {
				millTime = System.currentTimeMillis();
				requestTime = 1;
				return true;
			}
			long curTime = System.currentTimeMillis();
			// 最近两次获取的时间小于100s
			if (curTime - millTime < 100000) {
				requestTime++;
			} else {
				requestTime = 1;
			}

			// 连续10次，请求不允许通过
			if (requestTime > 10) {
				return false;
			}
			millTime = curTime;
			return true;
		}

	}

	/******************************************************
	 * added by edwar,为1.2版的多种注册登录增加的方法--START 2012-09-21
	 ******************************************************/

	// 用于HomePage页面,点击模块按钮-> Login界面-> HomePage页面 这种页面转换
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 判断请求CODE，是要去哪一个界面,按Z型从左从上->右下顺序。。
		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_MESSAGE_PAGE:
				isAfterOnActivityResult = true;
				setUnlogin(false);
				BasicApplication.getInstance().setAutoLogin(true);
				goToMessagePage();
				break;
			
			//added by liyang 2012-12-27 START
			//登陆流程修改  如果用户在未登录的情况下点击头像进入登陆流程
			//登陆成功后 返回首页执行以下代码
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE:
				isAfterOnActivityResult = true;
				setUnlogin(false);
				BasicApplication.getInstance().setAutoLogin(true);
				goToPersonalProfilePage();
				break;	
			//added by liyang 2012-12-27 END	
				

			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_MESSAGE_DETAIL_PAGE:
				isAfterOnActivityResult = true;
				setUnlogin(false);
				BasicApplication.getInstance().setAutoLogin(true);
				goToDetailedMessagePage();
				break;

			default:
				break;
			}
		}
	}

	/****************************************
	 * 抽出各种点击方法
	 ****************************************/

	// 1，点击左上消息按钮
	public void goToMessagePage() {
		if (isUnlogin()) {
			goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_MESSAGE_PAGE);
		} else {
			// 消息主页
			startActivity(new Intent(getApplicationContext(),
					MessageHomeActivity.class));
		}

	}

	// 2,点击“我的会员卡”
	public void goToRegpointHomePage() {
		startActivity(new Intent(getApplicationContext(),
				RegPointHomeActivity.class));
		/*if (isUnlogin()) {
			startActivity(new Intent(getApplicationContext(),
					RegPointHomeActivity.class));
		} else {
			if (isAsso()) {
				startActivity(new Intent(getApplicationContext(),
						RegPointHomeActivity.class));
			} else {
				// 走关联积分通流程
				startActivity(new Intent(getApplicationContext(),
						ActivateECardActivity.class));

			}
		}*/
	}

	// 2,点击主界面广告
	public void goToDetailedMessagePage() {
		if (isUnlogin()) {
			goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_MESSAGE_DETAIL_PAGE);
		} else {
			itemList = BasicApplication.getInstance().getImgList();
			if (itemList == null || itemList.isEmpty()) {
				return;
			}
			int position = actPager.getCurrentPage();
			String id = ((HomeImageItem) itemList.get(position)).getInfoId();
			// 活动id可能为null
			if (id != null) {
				// 传id给WebViewActivity
				Intent toWebAct = new Intent(getApplicationContext(),
						WebViewActivity.class).putExtra(WebViewActivity.ID, id);
				startActivityForResult(toWebAct, 100);
			}
		}
	}

	@Override
	protected void updateFollowMaMa100CheckBoxStatus(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void shareWeiboComplete(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doSSoLogin(boolean b, int requestCode) {
		// TODO Auto-generated method stub

	}

	/**
	 * 从服务器加载数据
	 */
	private void loadHomePageData() {
		if (isUnlogin()) {
			getUnloginPicsFromServer();
		} else {
			isAfterOnActivityResult = true;
			getloginDataFromServer();
		}
	}

	/******************************************************
	 * added by edwar,为1.2版的多种注册登录增加的方法--END 2012-09-21
	 ******************************************************/

}
