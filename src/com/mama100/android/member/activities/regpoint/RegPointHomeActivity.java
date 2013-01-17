package com.mama100.android.member.activities.regpoint;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.activities.HomePageActivity;
import com.mama100.android.member.activities.user.CompleteRecevierAddressActivity;
import com.mama100.android.member.asynctask.AsyncBitmapTask;
import com.mama100.android.member.businesslayer.PointProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.point.ExchangeCodeStatusReq;
import com.mama100.android.member.domain.point.ExchangeCodeStatusRes;
import com.mama100.android.member.domain.point.GetExchangeCodeReq;
import com.mama100.android.member.domain.point.GetExchangeCodeRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.PictureUtil;
import com.mama100.android.member.util.SDCardUtil;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.util.UserStorageUtils;
import com.mama100.android.member.widget.anim.Rotate3d;
import com.mama100.android.member.zxing.CaptureActivity;

/**
 * 
 * @modified by liyang  2012-11-29  1.36版登陆流程优化 
 * 在未登录时显示登录按钮、为开通积分通时显示立即开通按钮
 * 只有在都符合的情况下才显示自助积分和积分记录 
 * 
 * 
 * @modified by liyang  2012-11-29  代码重构
 * 尽量减少全局变量,进一步封装代码  未完
 */
public class RegPointHomeActivity extends BaseActivity implements
		OnClickListener, OnTouchListener {
	
	private ViewGroup lay_front;
	private ViewGroup lay_reverse;
	private ImageView imgV_front;
	private ImageView imgV_target;
	private ImageView imgV_hand;
	private ImageView imgV_perform_ex;
	private ImageView imgV_reverse;

	
	private String exChangeCode;
	private int activeTime;

	/**
	 * 正面已旋转的角度，1为正面
	 */
	private float card1Degrees = 0;
	
	/**
	 * 反面已旋转的角度，0为反面
	 */
	private float card0Degrees = 0;
	private float cardCenterX = -1.0f;
	private float cardCenterY = -1.0f;

	private int cardClickedViewId = 0; // 触发整个旋转事件的View
										// id,imgV_front或imgV_reverse

	private AsyncBitmapTask imageViewTask = null; // 用于从非第三方登录进来后下载头像
	
	private Timer mTimer = null;
	
	public static final int REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_REGPOINT_HISTORY = 11111222;// 点击积分历史，激活个人会员卡请求code
	public static final int REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_EXCHANGE_CARD = 11111223;// 点击兑换码卡片，激活个人会员卡请求code
	public static final int REQUEST_CODE_COMPLETE_ADDRESS_ON_CLICK_EXCHANGE_CARD = 11111333;// 点击兑换码卡片，激活完善个人信息请求code

	private ImageView avatar;
	private boolean isPreviousUnAsso = false; //默认已经关联

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.regpoint_home);

		avatar = (ImageView) findViewById(R.id.imgV_avatar);

		lay_front = (ViewGroup) findViewById(R.id.lay_front);
		lay_reverse = (ViewGroup) findViewById(R.id.lay_reverse);

		imgV_front = (ImageView) findViewById(R.id.imgV_front);
		imgV_reverse = (ImageView) findViewById(R.id.imgV_reverse);

		imgV_target = (ImageView) findViewById(R.id.imgV_target);
		imgV_hand = (ImageView) findViewById(R.id.imgV_hand);
		imgV_perform_ex = (ImageView) findViewById(R.id.imgV_perform_ex);

		setBackgroundPicture(R.drawable.bg_wall);
		setLeftButtonImage(R.drawable.selector_back);
		setTopLabel(R.string.homepage_1);
		resetPageValues();

		preRotationFor0();

		imgV_front.setOnClickListener(this);
		imgV_reverse.setOnClickListener(this);

		findViewById(R.id.btn_remake).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadLoadingView();
				showOutdateVis(false);
				new GetCodeTask(getApplicationContext()).execute(new GetExchangeCodeReq());
			}
		});

		findViewById(R.id.btn_hasused).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 旋转回正面
				imgV_reverse.performClick();
			}
		});
		
		findViewById(R.id.btn_reg_diy).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RegPointHomeActivity.this,CaptureActivity.class);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.btn_reg_his).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToRegpointHistoryPage();
			}
		});
		
		//在进入该页面的时候进行判断
		//如果未登录显示立即登录页面
		//如果未开通积分通显示立即开通页面
		//否则显示自助积分主页面
		if(isUnlogin()){
			loadUnLoginView();
		}else if(!isAsso()){
			//added by edwar 2011-11-30
			setPreviousUnAssoStatus(true);
			loadUnOpenRegpointView();
		}else {
			loadRegpointHomeView();
		}
	}
	
	
	
	@Override
	public void onRestart() {
		super.onRestart();
		setPointBalance(BasicApplication.getInstance().getLastRegpointBalance());
	}

	@Override
	public void onResume() {
		super.onResume();
		
		// added by liyang  2012-11-19 START
		// 增加代码部分主要是实现第一次打开扫描功能的用户会弹出闪屏页面
		if (isFirstOpen()) {
			Intent intent = new Intent(this, RegPointFlashActivity.class);
			intent.putExtra("isUnLogin", isUnlogin());
			intent.putExtra("isAsso", isAsso());
			startActivity(intent);
			setFlashOpened();
		} 
		// added by liyang  2012-11-19 END
		
		StatService.onResume(this);// 百度统计
		// MobileProbe.onResume(this);//CNZZ统计
		// 如果未登录，直接离开
		if (isUnlogin()) {
			return;
		}
		
		
		// 如果之前的状态是未登录，而今成为已登录，则要刷新页面
		if (isPreviousUnLoginStatus()) {
			resetPageValues();
		}
		
		//added by edwar 2012-11-30 start
		//如果现在还是未关联，就直接离开，不走下面的
		if(!isAsso()){
			return;
		}
		//现在新增加一种情况，从 “已登录但未关联” 到 “已登录已关联”的 刷新。。
		if(isPreviousUnAssoStatus()) {
			resetPageValues();
		}
		//added by edwar 2012-12-26 -start, 解决积分回来余额刷新
		else{
			setPointBalance(BasicApplication.getInstance().getLastRegpointBalance());
		}
		//added by edwar 2012-12-26 -end
//		added by edwar 2012-11-30 end
	}

	/**
	 * 将全局变量重新刷新界面
	 * 
	 * @param application
	 *            全局变量
	 */
	private void resetPageValues() {
		// // 如果不是从第三方登录，且onActivity也被调用，
		// // 则证明是从其他登录直接进来，而非back->OnResume进来。。则需要重新加载信息。
		// // if (!is_from_third_party && isAfterOnActivityResult) {
		// if (isAfterOnActivityResult) {
		// BasicApplication application = BasicApplication.getInstance();
		// setAvatarIntoViews(
		// application.getLastAvatarUrl(), RegPointHomeActivity.this);
		// } else {
		// setAvatar();
		// }

		// modified by edwar 2012-11-07
		// 修改之前复杂的逻辑为， 如果没有头像，就去mama100服务器系统拿，如果内存有，就直接取
		avatar.setScaleType(ScaleType.FIT_CENTER);
		Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
		if (bitmap != null) {
			setAvatar(bitmap);
		} else {
			BasicApplication application = BasicApplication.getInstance();
			setAvatarIntoViews(application.getLastAvatarUrl(),
					RegPointHomeActivity.this);
		}
		// modified end
		setNickname();
		setUsername();
		setPointBalance(BasicApplication.getInstance().getLastRegpointBalance());
		
	}

	// 用于从非第三方登录入口进来，要获取头像
	private void setAvatarIntoViews(String imgUrl, Activity mActivity) {
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

				// modified by edwar, 2012-11-06
				// 直接在xml里赋值未登录的默认头像，所以这里没有必要再赋值，精简赋值
				// bitmap = BitmapFactory.decodeResource(BasicApplication
				// .getInstance().getResources(),
				// R.drawable.default_avatar);
			}
			// BasicApplication.getInstance().setAvatarBitmap(bitmap);
			// modified end

		} else {
			// LogUtils.logi(TAG, "set avatar 1 - : "
			// + new Timestamp(System.currentTimeMillis()).toString());

			// 异步加载图片
			avatar.setTag(imgUrl);
			imageViewTask = new AsyncBitmapTask(mActivity);
			imageViewTask.execute(avatar);
		}

		// 2,修改头像
		// 设置头像为本地默认的头像,用于在个人信息界面修改头像之后回到首页的头像更新
		// 对于异步头像获取，在获取过程中，先从本地加载头像， 然后更新服务器最新的。。

		// LogUtils.logi(TAG, "set avatar 2 - : "
		// + new Timestamp(System.currentTimeMillis()).toString());
		avatar.setScaleType(ScaleType.FIT_CENTER);

		// modified by edwar 2012-11-06
		Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
		if (bitmap != null)
			setAvatar(bitmap);
		// modified end

		// LogUtils.logi(TAG, "set avatar 3 - : "
		// + new Timestamp(System.currentTimeMillis()).toString());

	}

	private void setAvatar(Bitmap bitmap) {
		avatar.setImageBitmap(bitmap);
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);// 百度统计
		// MobileProbe.onPause(this);//CNZZ统计
	}

	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		doClickLeftBtn();
	}
	
	

	// // 设置头像
	// private void setAvatar() {
	// avatar.setScaleType(ScaleType.FIT_CENTER);
	// Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
	// if(bitmap!=null)
	// setAvatar(bitmap);
	// }
	
	

	/***************
	 * Begin****************** 以下为实现卡片旋转效果的相关方法
	 ************************************/
	@Override
	public void onClick(View v) {
		super.onClick(v);

		// 3,判断有没有完善收货信息
		String tag = BasicApplication.getInstance().getCustomerInfoCompleted();
		LogUtils.logi("address", "on click -  " + tag);

		if (v.getId() == R.id.imgV_front || v.getId() == R.id.imgV_reverse) {

			// 如果还没有登录，显示登录框提示
			if (!isUnlogin()&&isAsso()&&tag == null || (tag != null && tag.equals("0"))) {
				showmemberDialog(R.string.complete_receiver_inf,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.setClass(getApplicationContext(),
										CompleteRecevierAddressActivity.class);
								intent.putExtra("from",
										ActivateECardActivity.ACTIVATE);
								startActivityForResult(intent,
										REQUEST_CODE_COMPLETE_ADDRESS_ON_CLICK_EXCHANGE_CARD);
								closeDialog();
							}
						});
				return;
			}

			// Find the center of the front container,
			// lay_front.wid==lay_reverse.wid
			if (cardCenterX < 0)
				cardCenterX = lay_front.getWidth() / 2.0f;
			if (cardCenterY < 0)
				cardCenterY = lay_front.getHeight() / 2.0f;

			cardClickedViewId = v.getId();
			if (v.getId() == R.id.imgV_front) {
				// 让正面覆盖物隐藏
				loadHandView(false);

				setCardLayVisibity(true);
			} else {
				setCardLayVisibity(false);
			}

			apply1Rotation(1, card1Degrees, card1Degrees + 90);
			apply0Rotation(1, card0Degrees, card0Degrees + 90);
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 先让反面旋转180度
	 */
	private void preRotationFor0() {
		final Rotate3d rotation = new Rotate3d(0, 180, 0, 0, 1.0f, true);
		rotation.setDuration(1);
		rotation.setFillAfter(true);
		// rotation.setInterpolator(new AccelerateInterpolator());
		card0Degrees = 180;
		lay_reverse.startAnimation(rotation);
	}
	
	
	/**
	 * 让正面旋转180度
	 */
	private void preRotationFor1() {
		final Rotate3d rotation = new Rotate3d(180, 0, 0, 0, 1.0f, true);
		rotation.setDuration(1);
		rotation.setFillAfter(true);
		// rotation.setInterpolator(new AccelerateInterpolator());
		card1Degrees = 0;
		lay_front.startAnimation(rotation);
	}
	
	private void apply1Rotation(int position, float start, float end) {
		final Rotate3d rotation = new Rotate3d(start, end, cardCenterX,
				cardCenterY, getMDepthZ(), true);
		rotation.setDuration(650);
		rotation.setFillAfter(true);
		// rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new HalfRotaEndLis(false));

		lay_front.startAnimation(rotation);
	}

	private void apply0Rotation(int position, float start, float end) {
		final Rotate3d rotation = new Rotate3d(start, end, cardCenterX,
				cardCenterY, getMDepthZ(), true);
		rotation.setDuration(650);
		rotation.setFillAfter(true);
		// rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new HalfRotaEndLis(true));

		lay_reverse.startAnimation(rotation);
	}

	private final class HalfRotaEndLis implements Animation.AnimationListener {
		private final boolean isFront;

		private HalfRotaEndLis(boolean isFront) {
			this.isFront = isFront;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			if (isFront) {
				imgV_front.setClickable(false);
				// 此处调用获取兑换码接口
				if (cardClickedViewId == R.id.imgV_front) {
					loadLoadingView();
				}
				card1Degrees += 90;
				lay_front.post(new RotaNextHalfAction(isFront));
			} else {
				imgV_reverse.setClickable(false);
				card0Degrees += 90;
				lay_reverse.post(new RotaNextHalfAction(isFront));
			}
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * start the second half of the animation.
	 * */
	private final class RotaNextHalfAction implements Runnable {
		private final boolean isFront;

		public RotaNextHalfAction(boolean isFront) {
			this.isFront = isFront;
		}

		public void run() {

			Rotate3d rotation;

			if (cardClickedViewId == R.id.imgV_front) {

				setCardLayVisibity(false);
			} else {
				setCardLayVisibity(true);
			}

			if (isFront)
				rotation = new Rotate3d(card1Degrees, card1Degrees + 90,
						cardCenterX, cardCenterY, getMDepthZ(), false);
			else
				rotation = new Rotate3d(card0Degrees, card0Degrees + 90,
						cardCenterX, cardCenterY, getMDepthZ(), false);
			rotation.setDuration(650);
			rotation.setFillAfter(true);
			// rotation.setInterpolator(new AccelerateInterpolator());
			rotation.setAnimationListener(new ResetContainer(isFront));

			if (isFront)
				lay_front.startAnimation(rotation);
			else
				lay_reverse.startAnimation(rotation);
		}
	}

	/**
	 * 第二次旋转事件的监听器，onAnimationEnd中的操作为旋转180（两次90）度后执行。即重置。
	 * 
	 */
	private final class ResetContainer implements Animation.AnimationListener {
		private boolean isFront;

		public ResetContainer(boolean isFront) {
			this.isFront = isFront;
		}

		// 旋转180度后
		@Override
		public void onAnimationEnd(Animation arg0) {
			// 正面旋转180（两次90）度后
			if (isFront) {
				imgV_front.setClickable(true);
				imgV_front.clearAnimation();
				card1Degrees += 90;
			} else {
				// imgV_reverse.setClickable(true);
				imgV_reverse.clearAnimation();
				card0Degrees += 90;
			}

			// 整个旋转由正面触发，即动画后呈现反面
			if (cardClickedViewId == R.id.imgV_front) {
				// 让反面覆盖物呈现
				if (!isFront) {
					
					if(isUnlogin()){
						showUnLoginVis();
					}else if(!isAsso()){
						showUnOpenVis();
					}
					// 检查
					else if (!HomePageActivity.ExCodeDetection.tryReqCode()) {
						makeText(R.string.oper_too_frequently02);
						loadRemakeView();
					} else {
						// 请求兑换码
						new GetCodeTask(getApplicationContext())
								.execute(new GetExchangeCodeReq());
					}
				}
			} else {
				// 让正面覆盖物呈现
				if (isFront) {
					loadHandView(true);
				}
			}

		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationStart(Animation arg0) {
		}
	}

	/**
	 * 让卡片其中一层可见
	 * 
	 * @param frontVis
	 *            正面可见
	 */
	private void setCardLayVisibity(boolean frontVis) {
		if (frontVis) {
			imgV_front.setVisibility(View.VISIBLE);
			lay_front.bringToFront();
			
			imgV_reverse.setVisibility(View.INVISIBLE);
		} else {
			imgV_front.setVisibility(View.INVISIBLE);

			imgV_reverse.setVisibility(View.VISIBLE);
			lay_reverse.bringToFront();
		}
	}

	/***************
	 * End****************** 实现卡片旋转效果的相关方法 End
	 *****************/

	class GetCodeTask extends AsyncReqTask {
		public GetCodeTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return PointProvider.getInstance(RegPointHomeActivity.this)
					.getExchangeCode((GetExchangeCodeReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				// TODO 此处会改成友好的对话框提示
				makeText(response.getDesc());
				showOutdateVis(true);
				return;
			}
			GetExchangeCodeRes codeRes = (GetExchangeCodeRes) response;

			loadRemakeView();
			exChangeCode = codeRes.getExchangeCode();
			((TextView)findViewById(R.id.tv_regcode)).setText(codeRes.getExchangeCode());
			((TextView)findViewById(R.id.tv_counting)).setText(codeRes.getActiveTime());

			try {
				activeTime = Integer.valueOf(codeRes.getActiveTime());
			} catch (NumberFormatException ex) {
				activeTime = 120;
			}

			mTimer = new Timer(true);
			mTimer.schedule(new InnerTimeTask(), 1000, 1000);
		}

	}

	class InnerTimeTask extends TimerTask {
		@Override
		public void run() {

			if (isFinishing()) {
				cancel();
			}

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					--activeTime;
					if (activeTime >= 0)
						((TextView)findViewById(R.id.tv_counting)).setText(activeTime + "");

					// 每5秒检查兑换码状态
					if (activeTime >= 0 && activeTime % 5 == 0) {
						final ExchangeCodeStatusReq request = new ExchangeCodeStatusReq();
						request.setExchangeCode(exChangeCode);

						new GetCodeStatusTask(RegPointHomeActivity.this)
								.execute(request);
					}
					// 延迟3秒，客户端自动显示过期
					if (activeTime <= -3) {
						mTimer.cancel();
						showOutdateVis(true);
					}

				}
			});
		}

	}

	class GetCodeStatusTask extends AsyncReqTask {
		public GetCodeStatusTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return PointProvider.getInstance(RegPointHomeActivity.this)
					.getExchangeCodeStatus((ExchangeCodeStatusReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				// TODO 此处会改成友好的对话框提示,而且只提示一次
				// makeText(response.getDesc());

				return;
			}
			ExchangeCodeStatusRes codeRes = (ExchangeCodeStatusRes) response;

			// Log.d("test",
			// "codeRes status:............."+codeRes.getCodeStatus());

			// 已使用
			if (codeRes.getCodeStatus().equals("1")) {
				mTimer.cancel();

				loadUsedView();
			}
			// 已过期
			else if (codeRes.getCodeStatus().equals("2")) {
				mTimer.cancel();
				showOutdateVis(true);
			}

		}

	}

	/******************************************************
	 * added by edwar,为1.2版的多种注册登录增加的方法--START 2012-10-16
	 ******************************************************/

	/****************************************
	 * 因为存在未登录的情况，所以抽出各种点击方法
	 ****************************************/
	//modified by liyang 2012-11-30
	//点击"积分列表"按钮
	public void goToRegpointHistoryPage() {
		startActivity(new Intent(getApplicationContext(),RegPointHistoryActivity.class));
	}

	// 用于HomePage页面,点击模块按钮-> Login界面-> HomePage页面 这种页面转换
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// 判断请求CODE，是要去哪一个界面,按Z型从左从上->右下顺序。。
		if (resultCode == RESULT_OK) {
			setUnlogin(false);
			BasicApplication.getInstance().setAutoLogin(true);

			switch (requestCode) {

			// 点击“积分记录”
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_HISTORY_PAGE:
				is_from_third_party = data.getBooleanExtra(IS_FROM_THIRD_PARTY,
						false);
				//btn_reg_his.performClick();
				break;

			// 点击 旋转会员卡
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_MEMBER_CARD_PAGE:
				is_from_third_party = data.getBooleanExtra(IS_FROM_THIRD_PARTY,
						false);

				break;
			case REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_REGPOINT_HISTORY:

				//btn_reg_his.performClick();
				break;
			case REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_EXCHANGE_CARD:
				onClick(imgV_front);
				break;
			case REQUEST_CODE_COMPLETE_ADDRESS_ON_CLICK_EXCHANGE_CARD:
				setUsername();
				onClick(imgV_front);
			
			//added by liyang  2012-11-29
			//点击立即登录按钮进入登陆流程
			//登陆成功后跳转回页面执行以下操作
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_SELECT_SHOP_PAGE:
				//setUnlogin(false);
				if(isAsso()){
					loadRegpointHomeView();
				}else{
					loadUnOpenRegpointView();
				}
				preRotationFor1();
				preRotationFor0();
				setCardLayVisibity(true);
				break;
				
			//added by liyang  2012-11-29	
			//点击立即开通按钮进入登陆流程
			//登陆成功后跳转回页面执行以下操作	
			case AppConstants.REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_SHOP_SELECT:
				loadRegpointHomeView();
				preRotationFor1();
				preRotationFor0();
				setCardLayVisibity(true);
				break;	
			default:
				break;
			}
		}
		
		//added by edwar  2012-11-30
		//为了用户在完成收货地址时，直接返回的情况
		else if (resultCode == RESULT_CANCELED) {
			//modified by liyang 2012-12-03 点击立即登录按钮-点击取消按钮-返回上一步 系统报错。
			//原因是点击取消并未登录，但是此处将登陆状态改为true，导致返回首页时进行了
			//refresh value 的操作，调用了postDataWithSsoCheck报错。
			//setUnlogin(false);
			//BasicApplication.getInstance().setAutoLogin(true);
			switch (requestCode) {
			case AppConstants.REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_SHOP_SELECT:
				//将上面代码移到此处				
				setUnlogin(false);
				BasicApplication.getInstance().setAutoLogin(true);
				
				if(isAsso()){
					loadRegpointHomeView();
				}else{
					loadUnOpenRegpointView();
				}
				
				
				preRotationFor1();
				preRotationFor0();
				setCardLayVisibity(true);
				break;	
			}
		}
	}
	

	/**
	 * 是否显示过期界面
	 * @param isOutOfDate (true 显示 false 不显示)
	 * modified by liyang 2012-11-29           
	 */
	private void showOutdateVis(boolean isOutOfDate) {
		if (isOutOfDate) {
			findViewById(R.id.tv_regcode).setVisibility(View.INVISIBLE);
			findViewById(R.id.tv_expire).setVisibility(View.INVISIBLE);
			findViewById(R.id.tv_counting).setVisibility(View.INVISIBLE);
			findViewById(R.id.tv_second).setVisibility(View.INVISIBLE);
			findViewById(R.id.btn_remake).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.tv_regcode).setVisibility(View.VISIBLE);
			findViewById(R.id.tv_expire).setVisibility(View.VISIBLE);
			findViewById(R.id.tv_counting).setVisibility(View.VISIBLE);
			findViewById(R.id.tv_second).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_remake).setVisibility(View.INVISIBLE);
		}
	}
	
	
	/**
	 * 设置用户昵称
	 * added by liyang 2012-11-29 
	 */
	private void setNickname() {
		((TextView) findViewById(R.id.tv_nick)).setText(BasicApplication.getInstance().getNickname());
	}	
		
	// modified by liyang 2012-11-29 
	// 设置用户名
	private void setUsername() {
		String mobile = BasicApplication.getInstance().getMobile();
		String html = "<font color=#4d4d4d>会员帐号：" + mobile+ "</font>";
		((TextView) findViewById(R.id.tv_account)).setText(Html.fromHtml(html));
	}						

	// modified by liyang 2012-11-29 
	// 设置积分余额
	private void setPointBalance(String point) {
		String balance_html = "<font color=#4d4d4d>积分余额：</font> "
				 			+ "<font color=#ff6650>" + point + "分</font>" ;
		((TextView) findViewById(R.id.tv_point)).setText(Html.fromHtml(balance_html));
	}			
	
	/**
	 * 旋转z轴深度
	 * added by liyang  2012-11-29
	 * @return
	 */
	float getMDepthZ(){
		// 屏幕密度为240时的z轴深度
		float mStandardZ = 122.0f ;
		float mDepthZ = -1.0f;
		if(mDepthZ == -1.0){
			 // z轴深度=标准深度x相对比的平方
			mDepthZ =mStandardZ / (240f * 240f / (densityDpi * densityDpi));
		}
		return mDepthZ;
	}
	
	/**
	 * 判断是否是第一次进入扫描页面
	 * added by liyang  2012-11-19 
	 */
	boolean isFirstOpen() {
		return UserStorageUtils.getBooleanShareValue(getApplicationContext(),AppConstants.REGPOINT_FLASH);
	}

	/**
	 * 设置闪屏页为已经打开过的状态
	 * added by liyang  2012-11-19 
	 */
	void setFlashOpened() {
		UserStorageUtils.setBooleanShareValue(getApplicationContext(),AppConstants.REGPOINT_FLASH, false);
	}
	
	
	
	/**
	 * 重构 by liyang  2012-11-29
	 * 加载正在获取兑换码的视图
	 */
	void loadLoadingView(){
		findViewById(R.id.layout_loading).setVisibility(View.VISIBLE);
		findViewById(R.id.layout_remake).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_used).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_unlogin).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_unOpen).setVisibility(View.INVISIBLE);
	}
	
	
	/**
	 * 重构 by liyang  2012-11-29
	 * 加载兑换码已经使用的视图
	 */
	void loadUsedView(){
		findViewById(R.id.layout_unlogin).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_unOpen).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_loading).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_remake).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_used).setVisibility(View.VISIBLE);
	}
	
	
	/**
	 * 重构 by liyang  2012-11-29
	 * 加载获取兑换码成功的视图
	 */
	void loadRemakeView(){
		findViewById(R.id.layout_loading).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_used).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_unlogin).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_unOpen).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_remake).setVisibility(View.VISIBLE);
	}
	
	
	
	/**
	 * 是否显示未登录界面
	 * @param isOutOfDate (true 显示 false 不显示)
	 * modified by liyang 2012-11-29           
	 */
	private void showUnLoginVis() {
		findViewById(R.id.layout_unlogin).setVisibility(View.VISIBLE);
		
		findViewById(R.id.layout_loading).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_used).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_remake).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_unOpen).setVisibility(View.INVISIBLE);
	}
	
	
	
	/**
	 * 是否显示未开通界面
	 * @param isOutOfDate (true 显示 false 不显示)
	 * modified by liyang 2012-11-29           
	 */
	private void showUnOpenVis() {
		findViewById(R.id.layout_unOpen).setVisibility(View.VISIBLE);
		
		findViewById(R.id.layout_loading).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_used).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_remake).setVisibility(View.INVISIBLE);
		findViewById(R.id.layout_unlogin).setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 未登录情况
	 * 显示页面在未登录的情况下的界面布局
	 * added by liyang  2012-11-29
	 */
	void loadUnLoginView(){
		//隐藏立即开通积分通视图
		findViewById(R.id.openLayout).setVisibility(View.GONE);
		
		findViewById(R.id.openRegpointBtn).setVisibility(View.GONE);
		//隐藏显示自助积分视图
		findViewById(R.id.regpointLayout).setVisibility(View.GONE);
		
		findViewById(R.id.regpointHomeBtnLayout).setVisibility(View.GONE);
		//显示立即登陆视图
		findViewById(R.id.loginLayout).setVisibility(View.VISIBLE);
		
		findViewById(R.id.loginBtn).setVisibility(View.VISIBLE);
		//执行登陆流程
		findViewById(R.id.loginBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toLogin();
			}
		});
		
		loadHandView(false);
	}
	
	
	/**
	 * 已登录  未开通积分通情况
	 * 显示页面在未开通积分通的情况下的界面布局
	 * added by liyang  2012-11-29
	 */
	void loadUnOpenRegpointView(){
		//显示立即开通视图
		findViewById(R.id.openLayout).setVisibility(View.VISIBLE);
		
		findViewById(R.id.openRegpointBtn).setVisibility(View.VISIBLE);
		//隐藏立即登陆视图
		findViewById(R.id.loginLayout).setVisibility(View.GONE);
		
		findViewById(R.id.loginBtn).setVisibility(View.GONE);
		//隐藏自助积分视图
		findViewById(R.id.regpointLayout).setVisibility(View.GONE);
		
		findViewById(R.id.regpointHomeBtnLayout).setVisibility(View.GONE);
		
		//执行开通积分通流程
		findViewById(R.id.openRegpointBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toOpenRegpoint();
			}
		});
		
		//设置昵称：例如您好，XXX
		String nickDefault= BasicApplication.getInstance().getNickname();
		String nickWithExist = StringUtils.isBlank(nickDefault)? "宝宝靓妈咪":nickDefault;
		String html = "您好，<font color=#ff6600>" +nickWithExist +"</font>,<br>"
					+ "您还没有开通妈妈100电子会员卡。" ;
		((TextView)findViewById(R.id.regpointTips)).setText(Html.fromHtml(html));
		
		loadHandView(false);
	}
	
	/**
	 * 已登录  已开通积分通情况
	 * 显示页面在已登录和已开通积分通的情况下的界面布局
	 * added by liyang  2012-11-29
	 */
	void loadRegpointHomeView(){
		
		//隐藏立即开通视图
		findViewById(R.id.openLayout).setVisibility(View.GONE);
		
		findViewById(R.id.openRegpointBtn).setVisibility(View.GONE);
		
		//隐藏登陆按钮视图
		findViewById(R.id.loginLayout).setVisibility(View.GONE);
		
		findViewById(R.id.loginBtn).setVisibility(View.GONE);
		
		//显示自助积分按钮视图
		findViewById(R.id.regpointLayout).setVisibility(View.VISIBLE);
		
		findViewById(R.id.regpointHomeBtnLayout).setVisibility(View.VISIBLE);
	
		//加载已开通积分通卡片视图
		loadHandView(true);
	}
	
	
	/**
	 * 显示和隐藏手势图标
	 */
	void loadHandView(boolean flag){
		if(flag){
			imgV_target.setVisibility(View.VISIBLE);
			imgV_hand.setVisibility(View.VISIBLE);
			imgV_perform_ex.setVisibility(View.VISIBLE);
		}else{
			imgV_target.setVisibility(View.INVISIBLE);
			imgV_hand.setVisibility(View.INVISIBLE);
			imgV_perform_ex.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 点击登陆按钮执行
	 * added by liyang  2012-11-29
	 */
	void toLogin(){
		goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_SELECT_SHOP_PAGE);
	}
	
	
	/**
	 * 点击开通积分通按钮执行
	 * added by liyang  2012-11-29
	 */
	void toOpenRegpoint(){
		Intent intent = new Intent(getApplicationContext(),ActivateECardActivity.class);
		intent.putExtra("requestcode", AppConstants.REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_SHOP_SELECT);
		startActivityForResult(intent,AppConstants.REQUEST_CODE_ACTIVATE_ECARD_ON_CLICK_SHOP_SELECT);
	}
	
	/**
	 * 页面销毁时，清理页面缓存
	 */
	@Override
	protected void onDestroy() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}
		if (lay_reverse != null)
			lay_reverse.clearAnimation();
		if (lay_front != null)
			lay_front.clearAnimation();

		if (imageViewTask != null && imageViewTask.isCancelled() == false) {
			imageViewTask.cancel(true);
			imageViewTask = null;
		}
		super.onDestroy();
	}

	//added by edwar 2012-11-30 设置刚进页面的未关联状态
	private void setPreviousUnAssoStatus(boolean flag) {
		isPreviousUnAsso  = flag;
	}

	private boolean isPreviousUnAssoStatus() {
		return isPreviousUnAsso;
	}
    //added by edwar 2012-11-30 end 
}
