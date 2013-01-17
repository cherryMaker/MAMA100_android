/**
 * 
 */
package com.mama100.android.member.activities;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Debug;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.user.EditProfileActivity;
import com.mama100.android.member.activities.user.LoginActivity;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.sys.HomeReq;
import com.mama100.android.member.domain.sys.HomeRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.service.BackgroundService;
import com.mama100.android.member.util.DesUtils;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.MobValidateUtils;
import com.mama100.android.member.util.StorageUtils;
import com.mama100.android.member.util.StringUtils;

/**
 * <p>
 * Description: BaseActivity.java 基本类，其它一切界面继承此类,包括一些通用的UI:如顶部栏，底部栏，
 * </p>
 * 
 * @author aihua.yan 2012-7-16
 * 
 */
public class BaseActivity extends Activity implements View.OnClickListener {

	/**
	 * 整个顶部
	 */

	protected RelativeLayout topBar;

	/**
	 * 顶部：logo 图片ImageView
	 */
	protected ImageView mlogo;

	/**
	 * 顶部：左边Button
	 */
	protected ImageButton leftButton;

	/**
	 * 顶部：右边Button
	 */
	protected ImageButton rightButton;

	/**
	 * 顶部：标题
	 */
	protected TextView title;

	/**
	 * 底部：确定提交Button
	 */
	protected Button submitButton;

	/**
	 * 中间内容：content
	 */

	protected LinearLayout ui_content;

	/**
	 * 上传提示
	 */
	protected TextView uploadText;

	// 现场数据管理者
	// protected WorkSiteDataManager wManager;

	protected Resources resources;

	// 本模块名称
	protected String thisModuleName = "";

	// 隐藏输入法的内应
	private EditText hide_input_method;

	protected int widthPixels;

	protected int heightPixels;

	protected int densityDpi;
	
	protected float density;

	public static final String IS_FROM_THIRD_PARTY = "is_from_third_party";// 是否从第三方进来-key
	// 如果非新用户，进入个人信息界面，还是从服务器加载，不是从第三方加载信息的标签
	public static final String IS_NEW_UID = "is_new_uid";// 是否新的第三方用户_key

	public  boolean is_from_third_party = false;// 是否从第三方进来-value
	public  boolean is_new_uid = false;// // 是否新的第三方用户_value

	private HomepageAsyncTask task;
	
	protected long total = 0; //获得系统的内存总数
	protected long free = 0 ; //获得系统的剩余内存

	private static String last_toast_tip=null; //上一次toast的文字
	private static Toast last_toast=null; //上一次toast
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//1.内存测试 start
				total = Runtime.getRuntime().totalMemory();
				LogUtils.loge("memory", "获得的内存 - " + (total) + "字节");
		
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.basic_ui_layout);
		buildDBManagerAndResource();
		buildViews();
		setBackButton();
		// startServiceHere();

		if (isUnlogin()) {
			setPreviousUnLoginStatus(true);
		} else {
			setPreviousUnLoginStatus(false);
		}

		// 这样可以让EditText获取焦点但不弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		final DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		widthPixels = dm.widthPixels;
		heightPixels = dm.heightPixels;
		densityDpi = dm.densityDpi;
		density=dm.density;
		
		LogUtils.logd("Here...width & height:", widthPixels + ","
				+ heightPixels);
		LogUtils.logd("Here...density & densityDpi:", dm.density + ","
				+ dm.densityDpi);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onNewIntent");
		super.onNewIntent(intent);
	}

	@Override
	public void onResume() {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onResume");
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStart() {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onStop");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onRestart");
		super.onRestart();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onConfigurationChanged");
	}

	@Override
	public void onPause() {
		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "   onPause");
		super.onPause();
	}

	private void setBackButton() {
		// setLeftButtonImage(R.drawable.top_back_btn_bg);
	}

	private void buildDBManagerAndResource() {
		resources = getResources();
		// wManager = new WorkSiteDataManager(getApplicationContext());
	}

	/**
	 * 初始化界面三层所有变量
	 */
	protected void buildViews() {
		buildViewsInTopBar();
		buildViewsInMiddleContent();
		buildViewsInBottomBar();
		buildViewsHideInputMethod();

	}

	private void buildViewsHideInputMethod() {
		hide_input_method = (EditText) findViewById(R.id.hide_input_method);
	}

	// 设置中部内容的容器变量
	private void buildViewsInMiddleContent() {
		ui_content = (LinearLayout) findViewById(R.id.ui_content);
	}

	/**
	 * 设置底部的提交 或者 bottomBar(现在没有)
	 */
	private void buildViewsInBottomBar() {

	}

	/**
	 * 设置顶部的4件套：leftButton, label, rightButton, logo变量
	 */
	private void buildViewsInTopBar() {

		topBar = (RelativeLayout) findViewById(R.id.menutop);

		mlogo = (ImageView) findViewById(R.id.top_logo); // 1
		leftButton = (ImageButton) this.findViewById(R.id.mkt_top_left_btn);// 2
		title = (TextView) this.findViewById(R.id.menutitle);// 3
		rightButton = (ImageButton) this.findViewById(R.id.mkt_top_right_btn);// 4

		rightButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);

	}

	/****** 所有设置方法setters: edwar yan 2012-05-10 START **/

	// 隐藏整个顶部
	public void setTopBarVisibility(int visible) {
		if (topBar == null)
			topBar = (RelativeLayout) findViewById(R.id.menutop);
		topBar.setVisibility(visible);

	}

	// 显示或者隐藏顶部logo
	public void setTopLogoVisibility(int visible) {
		if (mlogo == null)
			mlogo = (ImageView) findViewById(R.id.top_logo);
		mlogo.setVisibility(visible);
	}

	// 设置顶部logo的背景
	public void setTopLogo(int resId) {
		if (mlogo != null)
			mlogo.setImageResource(resId);
		mlogo.setVisibility(View.VISIBLE);
	}

	// 设置top 左边按钮背景图片
	public void setLeftButtonImage(int resid) {
		if (leftButton != null)
			leftButton.setBackgroundResource(resid);
		setLeftButtonVisibility(View.VISIBLE);
	}

	// 设置top 左边按钮背景图片的可见性
	public void setLeftButtonVisibility(int visibility) {
		if (leftButton != null)
			leftButton.setVisibility(visibility);
	}

	// 显示或者隐藏顶部label
	public void setTopLabelVisibility(int visible) {
		if (title != null)
			title.setVisibility(visible);
	}

	// 设置顶部label --- int
	public void setTopLabel(int resid) {
		if (title != null)
			title.setText(resid);
	}

	// 设置顶部label --- String
	public void setTopLabel(String str) {
		if (title != null)
			title.setText(str);
	}

	// 设置top 右边按钮背景图片
	public void setRightButtonImage(int resid) {
		if (rightButton != null)
			rightButton.setBackgroundResource(resid);
		setRightButtonVisibility(View.VISIBLE);
	}

	// 设置top 右边按钮背景图片的可见性
	public void setRightButtonVisibility(int visibility) {
		if (rightButton != null)
			rightButton.setVisibility(visibility);
	}

	// 设置背景图
	public void setBackgroundPicture(int resid) {
		findViewById(R.id.background).setBackgroundResource(resid);
	}

	/****** 所有设置方法 edwar yan 2012-05-10 END **/

	@Override
	public void setContentView(int layoutResID) {
		View v = View.inflate(this, layoutResID, null);
		ui_content.addView(v, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mkt_top_right_btn:
			doClickRightBtn();
			break;
		case R.id.mkt_top_left_btn:
			doClickLeftBtn();
			break;

		default:
			break;
		}
	}

	// 具体子类覆盖
	public void doClickLeftBtn() {
		hideSoftWindowInput();
	};

	// 具体子类覆盖
	public void doClickRightBtn() {
		hideSoftWindowInput();
	};

	protected void makeText(String msg) {
		if(last_toast==null){
			last_toast_tip=msg;
			last_toast=Toast.makeText(this, msg, Toast.LENGTH_LONG);
			last_toast.show();
		}
		else if(last_toast_tip!=null&&msg.equals(last_toast_tip)){
			last_toast.setText(msg);
			last_toast.setDuration(Toast.LENGTH_SHORT);
			last_toast.show();
		}
		else if(last_toast_tip!=null&&!msg.equals(last_toast_tip)){
			last_toast_tip=msg;
			last_toast=Toast.makeText(this, msg, Toast.LENGTH_LONG);
			last_toast.show();
		}
		
	}

	protected void makeText(int resId) {
		makeText(getString(resId));
	}

	@Override
	public void onBackPressed() {
		// 不用这个方法了，没必要每个界面都来比较 。。
		// // 只有主界面才显示确认退出对话框
		// if
		// (isCurrentTopActivityTheTargetActivity("com.mama100.android.member.activities.HomePageActivity"))
		// {
		// showExitDialog();
		// } else {
		// }
	}

	/**
	 * 检查当前 toppest Activity 是不是 主界面
	 * 
	 * @return
	 */
	private boolean isCurrentTopActivityTheTargetActivity(String target) {
		String activityName = getCurrentTopActivityName();
		LogUtils.logi("edwar", "the current toppest activity name is - "
				+ activityName);
		return (activityName != null && activityName != "" && activityName
				.equalsIgnoreCase(target.toString()));
	}

	/**
	 * 获取当前活动的Activity
	 * 
	 * @return
	 */
	private String getCurrentTopActivityName() {

		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> forGroundActivity = activityManager
				.getRunningTasks(1);
		RunningTaskInfo currentActivity;
		currentActivity = forGroundActivity.get(0);
		String activityName = currentActivity.topActivity.getClassName();
		return activityName;
	}

	//
	// /**
	// * 显示提示是否退出的对话框
	// */
	// public void showExitDialog() {
	//
	// showmemberDialog(R.string.exit_warning_msg, new View.OnClickListener() {
	// @Override
	// public void onClick(View arg0) {
	// //正常退出特有流程
	// checkAndStartBackgroundService();
	// finish();
	// closeDialog();
	//
	// //清空 sd卡里拍照留下的图片
	// // SDCardUtil.deleteFolder(SDCardUtil.getPictureTempPath());
	// BasicApplication.getInstance().clearNormalInfo();
	// BackStackManager.getInstance().closeAllActivity();
	// }
	// });
	// }

	// 通用dialog
	public Dialog dlg;

	// 隐藏输入法 edwar 2012-06-09
	/**
	 * @param et
	 *            , 界面的某个输入法
	 */
	protected void hideSoftWindowInput() {
		InputMethodManager imm = (InputMethodManager) BaseActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(hide_input_method.getWindowToken(), 0);
	}

	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/***********************************************************
	 * added by edwar 2012-07-03 START
	 ************************************************************/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ui_content != null) {
			ui_content.removeAllViews();
			ui_content = null;
		}

		if (task != null && task.isCancelled() == false) {
			task.cancel(true);
		}

		LogUtils.logi(AppConstants.PROGRESS_TAG, getClass().getSimpleName()
				+ "  onDestroy");

	}

	/***********************************************************
	 * added by edwar 2012-07-03 END
	 ************************************************************/

	/**
	 * 显示自定义风格的提示(确定)对话框。
	 * 
	 * @param msgid
	 *            msgid
	 * @param pos_listener
	 *            确认监听器，可以null，默认退出对话框
	 * @author added by ecoo
	 */
	public void showmemberDialog(int msgid, OnClickListener pos_listener) {
		showmemberDialog(msgid, 0, pos_listener, null);
	}

	/**
	 * 显示自定义风格的提示(确定与取消)对话框。
	 * 
	 * @param msgid
	 *            msgid
	 * @param yOffset
	 *            对话框在屏幕位置的Y轴偏移量(pixels)。y小于0上移，大于0下移，等于0居中。
	 * @param pos_listener
	 *            确认监听器，可以null，默认退出对话框
	 * @param cancel_listener
	 *            取消监听器 ，同上。
	 * @author added by ecoo
	 */
	public void showmemberDialog(int msgid, int yOffset,
			OnClickListener pos_listener, OnClickListener cancel_listener) {
		if (dlg == null)
			dlg = new AlertDialog.Builder(this).create();

		dlg.show();
		final View v = View.inflate(this, R.layout.dialog_layout, null);
		final Window window = dlg.getWindow();

		final WindowManager.LayoutParams wl = window.getAttributes();
		// 根据x，y坐标设置窗口需要显示的位置
		// wl.x += x; //x小于0左移，大于0右移
		// wl.y +=heightPixels/2-280; //y小于0上移，大于0下移
		wl.y = 0;
		wl.y += yOffset;
		//大屏幕
		if(widthPixels/density>390){
			// 对话框宽度
			wl.width = (int) (widthPixels * 0.76);
		}
		else{
			// 对话框宽度
			wl.width = (int) (widthPixels * 0.9);
		}
		window.setAttributes(wl);
		window.setContentView(v);
		// 设置信息
		((TextView) v.findViewById(R.id.tv_msg)).setText(this.getString(msgid));
		if (pos_listener == null)
			v.findViewById(R.id.btn_ok).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dlg.dismiss();
						}
					});

		else
			v.findViewById(R.id.btn_ok).setOnClickListener(pos_listener);

		if (cancel_listener == null)
			v.findViewById(R.id.btn_cancel).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dlg.dismiss();
						}
					});
		else
			v.findViewById(R.id.btn_cancel).setOnClickListener(cancel_listener);
	}

	
	
	
	
	/**
	 * 显示自定义风格的提示(确定与取消)对话框。
	 * 
	 * @param msgid
	 *            msgid
	 * @param yOffset
	 *            对话框在屏幕位置的Y轴偏移量(pixels)。y小于0上移，大于0下移，等于0居中。
	 * @param pos_listener
	 *            确认监听器，可以null，默认退出对话框
	 * @param cancel_listener
	 *            取消监听器 ，同上。
	 * @author added by ecoo
	 */
	public void showmemberDialog(String msg, int yOffset,
			OnClickListener pos_listener,OnClickListener cancel_listener) {
		if (dlg == null)
			dlg = new AlertDialog.Builder(this).create();

		dlg.show();
		final View v = View.inflate(this, R.layout.regpoint_dialog, null);
		final Window window = dlg.getWindow();

		final WindowManager.LayoutParams wl = window.getAttributes();
		// 根据x，y坐标设置窗口需要显示的位置
		// wl.x += x; //x小于0左移，大于0右移
		// wl.y +=heightPixels/2-280; //y小于0上移，大于0下移
		wl.y = 0;
		wl.y += yOffset;
		//大屏幕
		if(widthPixels/density>390){
			// 对话框宽度
			wl.width = (int) (widthPixels * 0.76);
		}
		else{
			// 对话框宽度
			wl.width = (int) (widthPixels * 0.9);
		}
		window.setAttributes(wl);
		window.setContentView(v);
		// 设置信息
		((TextView)v.findViewById(R.id.tv_msg)).setText(Html.fromHtml(msg));
		
		if (pos_listener == null)
			v.findViewById(R.id.btn_ok).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dlg.dismiss();
						}
					});

		else
			v.findViewById(R.id.btn_ok).setOnClickListener(pos_listener);

		if (cancel_listener == null)
			v.findViewById(R.id.btn_cancel).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dlg.dismiss();
						}
					});
		else
			v.findViewById(R.id.btn_cancel).setOnClickListener(cancel_listener);
	}

	/**
	 * 显示自定义风格的提示(确定)对话框。
	 * 
	 * @param msg
	 *            提示文本
	 * @param yOffset
	 *            对话框在屏幕位置的Y轴偏移量(pixels)。y小于0上移，大于0下移，等于0居中。
	 * @param pos_listener
	 *            确认监听器，可以null，默认退出对话框
	 * @author added by liyang
	 */
	public void showmemberDialog(String msg, int yOffset,
			OnClickListener pos_listener) {
		if (dlg == null)
			dlg = new AlertDialog.Builder(this).create();

		dlg.show();
		final View v = View.inflate(this, R.layout.dialog_layout1, null);
		final Window window = dlg.getWindow();

		final WindowManager.LayoutParams wl = window.getAttributes();
		// 根据x，y坐标设置窗口需要显示的位置
		// wl.x += x; //x小于0左移，大于0右移
		// wl.y +=heightPixels/2-280; //y小于0上移，大于0下移
		wl.y = 0;
		wl.y += yOffset;
		// 对话框宽度
		wl.width = (int) (widthPixels * 0.9);
		window.setAttributes(wl);
		window.setContentView(v);
		// 设置信息
		((TextView) v.findViewById(R.id.tv_msg)).setText(msg);
		if (pos_listener == null)
			v.findViewById(R.id.btn_ok).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dlg.dismiss();
						}
					});

		else
			v.findViewById(R.id.btn_ok).setOnClickListener(pos_listener);

	}

	/**
	 * 显示自定义风格的提示(确定)对话框。
	 * 
	 * @param msgid
	 *            msgid
	 * @param yOffset
	 *            对话框在屏幕位置的Y轴偏移量。y小于0上移，大于0下移，等于0居中。
	 * @param pos_listener
	 *            确认监听器，可以null，默认退出对话框
	 * @author added by ecoo
	 */
	public void showmemberDialog(int msgid, int yOffset,
			OnClickListener pos_listener) {
		showmemberDialog(msgid, yOffset, pos_listener, null);
	}

	/**
	 * 关闭当前dialog
	 * 
	 * @author added by ecoo
	 */
	public void closeDialog() {
		if (dlg != null)
			dlg.dismiss();
	}

	/**
	 * 设置当前对话框两个按键的文字,调用showmemberDialog方法之后使用才有效。
	 * 
	 * @author added by ecoo
	 * @param posBtnTipId
	 *            确定按键文字id
	 * @param cancelBtnTipId
	 *            取消按键文字id
	 */
	public void setDialogBtnTips(int posBtnTipId, int cancelBtnTipId) {
		if (dlg != null) {
			((Button) dlg.getWindow().findViewById(R.id.btn_ok))
					.setText(posBtnTipId);
			((Button) dlg.getWindow().findViewById(R.id.btn_cancel))
					.setText(cancelBtnTipId);
		}
	}

	/*********************************************************
	 * 通用的验证接口
	 *********************************************************/
	/**
	 * @param et
	 *            用于验证的输入
	 * @param type
	 *            验证的类型， @see AppConstants.CHECK_MOBILE
	 * @return false 不合法
	 */
	public boolean verifyInput(EditText et, int type) {
		boolean flag = true;

		if (et == null) {
			et.setError(getResources().getString(R.string.login_et_object_null));
			flag = false;
		}
		String value = et.getText().toString();

		// 验证类型
		switch (type) {
		case AppConstants.CHECK_MOBILE:
			if (StringUtils.isBlank(value)
					|| !MobValidateUtils.checkMobile(value)) {
				String result = getResources().getString(
						R.string.lookfor_pwd_warning5);
				et.setError(result);
				et.requestFocus();
				flag = false;
			}

			break;
		case AppConstants.CHECK_USERNAME:
			if (StringUtils.isBlank(value)) {
				et.setError(getResources().getString(R.string.login2));
				et.requestFocus();
				flag = false;
			}else if(value.length() < 2 || value.length() > 30){
				String target = getResources().getString(R.string.login_et_user_value_length_error);
				String result = String.format(target, "2", "30", "字符");
				et.setError(result);
				et.requestFocus();
				flag = false;
			}
			break;
		case AppConstants.CHECK_PASSWORD:
			if (StringUtils.isBlank(value)) {
				et.setError(getResources().getString(R.string.account_login_input_pwd_hint));
				et.requestFocus();
				flag = false;
			}else if(!MobValidateUtils.checkPassword2(value)){
				String target = getResources().getString(R.string.login_et_pwd_value_length_error);
				String result = String.format(target, "6", "18", "字符");
				et.setError(result);
				et.requestFocus();
				flag = false;
			}
				
			break;

		case AppConstants.CHECK_SERIALNUMBER:
			if (StringUtils.isBlank(value)
					|| !MobValidateUtils.checkInputSerial(value)) {
				String target = getResources().getString(
						R.string.regpoint_et_serial_length_error);
				String result = String.format(target,
						MobValidateUtils.SERIAL_NUMBER_LENGTH1,
						MobValidateUtils.SERIAL_NUMBER_LENGTH2, "数字");
				et.setError(result);
				et.requestFocus();
				flag = false;
			}
			break;
		case AppConstants.CHECK_ANTIFAKECODE:
			if (StringUtils.isBlank(value)
					|| !MobValidateUtils.checkInputAntiFake(value)) {
				String target = getResources().getString(
						R.string.regpoint_et_anticode_length_error);
				String result = String.format(target,
						MobValidateUtils.ANTI_FAKE_NUMBER_LENGTH, "数字");
				et.setError(result);
				et.requestFocus();
				flag = false;
			}
			break;
		case AppConstants.CHECK_NICKNAME:
			try {
				if (StringUtils.isBlank(value)) {
					et.setError(getString(R.string.nickname_null_error));
					et.requestFocus();
					flag = false;
				} else if(value.getBytes("GBK").length < 4){
					et.setError(getString(R.string.nickname_len_short_error));
					et.requestFocus();
					flag = false;
				} else if(value.getBytes("GBK").length > 18){
					et.setError(getString(R.string.nickname_len_long_error));
					et.requestFocus();
					flag = false;
				} 
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case AppConstants.CHECK_RECEIVER_ADDRESS:
			try {
				if (StringUtils.isBlank(value)
						|| value.getBytes("GBK").length < 10) {
					et.setError(getString(R.string.receiver_address_complete_illlegal));
					et.requestFocus();
					flag = false;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case AppConstants.CHECK_RECEIVER_ADDRESS2:
			try {
				if (StringUtils.isBlank(value)) {
					et.setError(getString(R.string.receiver_address_null_illlegal));
					et.requestFocus();
					flag = false;
				}else if(value.getBytes("GBK").length < 4){
					et.setError(getString(R.string.receiver_address_complete_illlegal));
					et.requestFocus();
					flag = false;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case AppConstants.CHECK_CUSTOMER_NAME:
			try {
				if (StringUtils.isBlank(value)) {
					et.setError(getString(R.string.receiver_null_illlegal));
					et.requestFocus();
					flag = false;
				}else if(value.length() < 2){
					et.setError(getString(R.string.receiver_name_illlegal));
					et.requestFocus();
					flag = false;
				}else if(value.getBytes("GBK").length> 18){
					et.setError(getString(R.string.receiver_long_illlegal));
					et.requestFocus();
					flag = false;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case AppConstants.CHECK_BABY_BIRTH:
			if (StringUtils.isBlank(value)) {
				et.setError(getString(R.string.baby_birthdate_illlegal));
				et.requestFocus();
				flag = false;
			}
			break;

		case AppConstants.CHECK_EMAIL:
			if (StringUtils.isBlank(value)
					|| !MobValidateUtils.checkEmail(value)) {
				et.setError(getString(R.string.check_email_illegal));
				et.requestFocus();
				flag = false;
			}
			break;

		default:
			break;
		}
		return flag;
	}

	/*************************************************************************
	 * 释放内存
	 ************************************************************************/
	/**
	 * 释放imageView的图片
	 * 
	 * @param view
	 *            欲释放的 imageView 对象
	 */
	public void clearImageViewMomery(ImageView view) {
		if (view != null) {
			view.setImageBitmap(null);
			view.setBackgroundDrawable(null);
			view.setImageURI(null);// 清掉之前的内存
		}
	}

	/*************************************************************
	 * 停止后台服务
	 *************************************************************/
	protected void checkAndStopBackgroundService() {
		// 检查服务是否开启， 如果开启，则关闭
		if (isServiceRunning(getApplicationContext(),
				BackgroundService.class.getName())) {
			LogUtils.logi(AppConstants.PROGRESS_TAG, "服务正在运行,将服务关闭");
			stopService(new Intent(this, BackgroundService.class));
		} else {
			LogUtils.logi(AppConstants.PROGRESS_TAG, "服务还没有运行");
		}
	}

	/*************************************************************
	 * 开启后台服务
	 *************************************************************/
	protected void checkAndStartBackgroundService() {
		// 检查服务是否开启， 如果开启，不做任何事
		if (isServiceRunning(getApplicationContext(),
				BackgroundService.class.getName())) {
			LogUtils.logi(AppConstants.PROGRESS_TAG, "服务正在运行");
		} else {
			// 如果服务没有开启，则开启服务。
			LogUtils.logi(AppConstants.PROGRESS_TAG, "服务还没有运行,开启服务");
			startService(new Intent(this, BackgroundService.class));
		}
	}

	/*************************************************************
	 * 启动-更新软件版本的服务
	 *************************************************************/
	protected void bootupSoftwareUpdateService(Intent intent) {
		startService(intent);
	}

	/*************************************************************
	 * 停止-更新软件版本的服务
	 *************************************************************/
	protected void shutdownSoftwareUpdateService(Intent intent) {
		stopService(intent);
	}

	/***************************************************************
	 * 以前用于：
	 * 特殊情况：首次注册登录-->EditProfile-->back-->HomePageActivity
	 * 特殊情况：消息通知栏-->DetailMessage-->MessageList-->back-->HomePageActivity
	 * 
	 * 现在除了上面两种，多了些用于： 多登录入口的情况下，无论在哪个入口进入，都需要初始化全局变量并保存。
	 * 该方法仅仅获取全局信息，比如用户信息，微博信息等，不涉及页面，在后台默默执行
	 ***************************************************************/
	public class HomepageAsyncTask extends AsyncReqTaskWithMessage {
		public HomepageAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return UserProvider.getInstance(getApplicationContext())
					.getHomepageValueWithTicket((HomeReq) request);

		}

		@Override
		protected void handleResponse(BaseRes response) {
			if(AppConstants.NEED_TRACING2)
				Debug.startMethodTracing("activity_trace2");
			
			closeProgressDialog();
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				makeText(response.getDesc());
				return; 
			}

			HomeRes res = (HomeRes) response;
			LogUtils.logi(AppConstants.PROGRESS_TAG, res.toString());

			// 重要的先执行
			// 8, 文件夹路径
			String mid = res.getMid();
			BasicApplication.getInstance().setMid(mid);
			
			setUnlogin(false);
			BasicApplication.getInstance().setAutoLogin(true);
			
			// 10,是否已经关联
			boolean isAsso = res.getIsAsso().equalsIgnoreCase("1") ? true
					: false;
			BasicApplication.getInstance().setAsso(isAsso);
			
			// 设置用户文件夹路径
			BasicApplication.getInstance().setAllFolderPath(mid);

			// 1,积分
			BasicApplication.getInstance().setLastRegpointBalance(
					res.getPoint());
			// 2,头像
			BasicApplication.getInstance().setLastAvatarUrl(res.getAvatar());
			// 清理掉未登录时，设置的默认头像，
			// 解决从设置-意见反馈-登录-设置-个人信息获取不到头像的问题 added by 2012-10-25 edwar
			BasicApplication.getInstance().setAvatarBitmap(null);

			// 3,消息
			BasicApplication.getInstance().setUnreadMsgSum(
					Integer.parseInt(res.getMsgCount()));
			// 4,微博分享内容
			BasicApplication.getInstance().setWeiboShareContent(
					res.getWeiboShareContent());
			// 5,用户名
			String username = res.getUsername();
			BasicApplication.getInstance().setUsername(username);

			// 加密保存用户名
			String encodedUsername = null;
			try {
				encodedUsername = DesUtils.encrypt(DesUtils.DES_COMMON_KEY,
						username);
				// 保存用户名
				StorageUtils.storeLoginAccountInClient(getApplicationContext(),
						encodedUsername);

			} catch (Exception e) {
				LogUtils.loge(AppConstants.PROGRESS_TAG, "用户名加密及保存本地出现问题");
				LogUtils.loge(AppConstants.PROGRESS_TAG, e.getMessage());
			}

			// 6,所有第三方的消息
			BasicApplication.getInstance().setWeiboItems(res.getWeiboItems());
			// 7,收货人地址是否完善
			BasicApplication.getInstance().setCustomerInfoCompleted(
					res.getCustomerInfoCompleted());

			// 9, 主页图片
			BasicApplication.getInstance().setImgList(res.getImgItems());

		

			// 11, 昵称
			String nick = res.getNick();
			BasicApplication.getInstance().setNickname(nick);

			// 12， Mobile
			String mobile = res.getMobile();
			BasicApplication.getInstance().setMobile(mobile);

		

			/****************************************
			 * 第三方要 先进入 个人信息界面 确保后台信息加载完才进入界面
			 * 
			 * @see BasicApplication requestCode字段定义
			 ****************************************/
			int requestCode = BasicApplication.getInstance().getRequestCode();
			// 1, 第三方帐号进来
			if (BasicApplication.getInstance().isLoginFromThirdParty()) {
				BasicApplication.getInstance().setLoginFromThirdParty(true);
				boolean isNewQQUid = BasicApplication.getInstance()
						.isNewQQUid();
				boolean isNewSinaUid = BasicApplication.getInstance()
						.isNewSinaUid();

				/**
				 * 且之前欲进入界面的非个人信息界面。 否则，会重新create个人界面了。。就错了
				 */
				if (requestCode != AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE) {
					// 如果是一个新的Uid
					if (isNewQQUid || isNewSinaUid) {
						Intent intent = new Intent(getApplicationContext(),
								EditProfileActivity.class);
						// 参考标签，EditProifleActivity里无论返回还是提交，都要回到用户再登录前，欲进入的界面
						intent.putExtra(
								EditProfileActivity.IS_FROM_THIRD_PARTY, true);

						// 如果非新用户，进入个人信息界面，还是从服务器加载，不是从第三方加载信息的标签
						intent.putExtra(EditProfileActivity.IS_NEW_UID, true);
						startActivityForResult(intent, requestCode);
					} else {
						BasicApplication.getInstance().setLoginFromThirdParty(
								true);
						Intent intent = new Intent();
						intent.putExtra(
								EditProfileActivity.IS_FROM_THIRD_PARTY, true);
						intent.putExtra(EditProfileActivity.IS_NEW_UID, false);
						setResult(RESULT_OK, intent);
						finish();
					}
				} else {
					// 如果是一个新的Uid
					Intent intent = new Intent();
					// 参考标签，EditProifleActivity里无论返回还是提交，都要回到用户再登录前，欲进入的界面
					intent.putExtra(EditProfileActivity.IS_FROM_THIRD_PARTY,
							true);

					// 如果非新用户，进入个人信息界面，还是从服务器加载，不是从第三方加载信息的标签
					if (isNewQQUid || isNewSinaUid) {
						intent.putExtra(EditProfileActivity.IS_NEW_UID, true);
					} else {
						intent.putExtra(EditProfileActivity.IS_NEW_UID, false);
					}
					setResult(RESULT_OK, intent);
					finish();
				}
				
			}
			//2,
			//特殊情况（注册邮箱进来）:首次注册登录-->EditProfile-->back-->HomePageActivity
			else if(BasicApplication.getInstance().isFromRegister()){
				
				/**
				 * 且之前欲进入界面的非个人信息界面。 否则，会重新create个人界面了。。就错了
				 */
				if (requestCode != AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE) {
					
					// 设置个人信息
					Intent intent = new Intent(getApplicationContext(),
							EditProfileActivity.class);
					intent.putExtra(EditProfileActivity.IS_FIRST_EDIT, true);// 提醒是初次注册，隐藏微博栏
					//modified by edwar 2012-12-04 Start, 解决会员卡界面(未登录)->登录或注册->进入该界面，回去会员卡界面不刷新登录状态的问题
//					startActivity(intent);
					startActivityForResult(intent,requestCode); //只有带上这个，才能成功传递请求码
//					finish();
					//modified by edwar end
					
					// 自动登录
					
					//注释by liyang  
					//注释原因 不注释该处会导致系统内部内存溢出，可能是某个dialog正常显示的时候进行了finish操作
					//注释以后测试发现之前该处出现的登陆跳转异常不再出现，流程正常运转。
//				finish();
					
					//added by edwar 2012-10-29
				}else{
					Intent intent = new Intent();
					intent.putExtra(EditProfileActivity.IS_FIRST_EDIT, true);// 提醒是初次注册，隐藏微博栏
					setResult(RESULT_OK, intent);
					finish();
				}
			}
			//3，特殊情况：消息通知栏-->DetailMessage-->MessageList-->back-->HomePageActivity
			else if(BasicApplication.getInstance().isFromNotificationBar())
			{
				Intent intent = new Intent(getApplicationContext(),
						HomePageActivity.class);
				startActivity(intent);
				finish();
				
			}
			//4, crm Login 会员手机登录 或者 正常登录进来
			else {
				BasicApplication.getInstance().setLoginFromThirdParty(false);
				Intent intent = new Intent();
				intent.putExtra(EditProfileActivity.IS_FROM_THIRD_PARTY, false);
				setResult(RESULT_OK, intent);
				finish();
			}
			if(AppConstants.NEED_TRACING2)
				Debug.stopMethodTracing();
			
			LogUtils.loge(AppConstants.TIME_CONSUME, "time - back from mama100" + new Timestamp(System.currentTimeMillis()));
		}
	}

	/**
	 * 后台获取全局信息。
	 */
	public void loadApplicationData() {
		task = new HomepageAsyncTask(BaseActivity.this);
		task.displayProgressDialog(R.string.doing_req_message3);
		task.execute(new HomeReq());
	}

	/****************************************
	 * added by edwar 2012-10-16 从未登录 --> 登录界面
	 ****************************************/

	/***************************************************************
	 ** added by edwar 2012-010-16 判断是否在未登录的情况下打开该页 的变量
	 ***************************************************************/
	private boolean isUnloginFlag = BasicApplication.getInstance().isUnLogin();

	/***************************************************************
	 ** added by edwar 2012-09-28 判断是否已经绑定积分通，true 代表是，false代表否
	 ***************************************************************/
	private boolean isAsso = BasicApplication.getInstance().isAsso();

	/****************************************
	 * 为了避免已登录情况下再进来该界面时，onResume方法的过度刷新()，用到的变量
	 ****************************************/
	private boolean isPreviousUnLoginStatus = true; // 前一个状态是未登录吗

	// 是否未登录， true,证明未登录;false,证明已经登录
	public boolean isUnlogin() {
		return BasicApplication.getInstance().isUnLogin();
	}

	public boolean isAsso() {
		return BasicApplication.getInstance().isAsso();
	}

	public void setAsso(boolean isAssoFlag) {
		this.isAsso = isAssoFlag;
		BasicApplication.getInstance().setAsso(isAssoFlag);
	}

	public boolean isPreviousUnLoginStatus() {
		return isPreviousUnLoginStatus;
	}

	public void setPreviousUnLoginStatus(boolean isPreviousUnLoginStatus) {
		LogUtils.loge("是未登录状态", (isPreviousUnLoginStatus) ? "true" : "false");
		this.isPreviousUnLoginStatus = isPreviousUnLoginStatus;
	}

	// 设置用户是否未登录，还是已经登录。
	public void setUnlogin(boolean isUnloginFlag) {
		this.isUnloginFlag = isUnloginFlag;
		BasicApplication.getInstance().setUnLogin(isUnloginFlag);
	}

	/******************************************************
	 * added by edwar,为1.2版的多种注册登录增加的方法--START 2012-10-16
	 ******************************************************/

	// 如果用户未登录，当用户点击必须登录后才能看的界面，则进入登录界面
	public void goToLoginPage(int requestCode) {
		BasicApplication.getInstance().setRequestCode(requestCode);
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivityForResult(intent, requestCode);
	}

	// 正常打开主界面
	@Deprecated
	public void goToHomePage() {
		Intent intent = new Intent(getApplicationContext(),
				HomePageActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 设置各种将来第三方的key
	 * 
	 * @param res
	 *            主页的响应 added by edwar 2012-10-17
	 */
	public void setThirdPartyKeys(HomeRes res) {
		if (res != null) {
			/******************* 设置各种第三方的key start 2012-10-17 **************/
			String qq_key = res.getQq_key();
			String sina_key = res.getSina_key();
			String qqweibo_key = res.getQqweibo_key();
			
			BasicApplication.getInstance().setQq_key(qq_key);
			BasicApplication.getInstance().setSina_key(sina_key);
			BasicApplication.getInstance().setQqweibo_key(qqweibo_key);
			/******************* 设置各种第三方的key end edwar 2012-10-17 **************/
		} else {
			LogUtils.loge(AppConstants.PROGRESS_TAG,
					"设置第三方key值时，发现服务的返回值是 null");
		}
	}
}
