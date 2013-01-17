/**
 * 
 */
package com.mama100.android.member.activities.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.PhotoActivity;
import com.mama100.android.member.asynctask.AsyncBitmapTask;
import com.mama100.android.member.bean.Child;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.GetProfileRes;
import com.mama100.android.member.domain.user.UpdateBabyInfoReq;
import com.mama100.android.member.domain.user.UpdateBabyInfoRes;
import com.mama100.android.member.domain.user.UpdateProfileReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BackStackManager;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.PictureUtil;
import com.mama100.android.member.util.SDCardUtil;
import com.mama100.android.member.util.StringUtils;

/**
 * <p>
 * Description: EditProfileActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 用户个人信息界面
 * 
 * 
 * modified by liyang 2012-11-27 流程修改（优化）
 * 增加管理收货地址功能
 */
public class EditProfileActivity extends PhotoActivity {
	public static final String IS_FIRST_EDIT = "is_first_edit";// 是否刚刚注册-key
	/****************************
	 * 顶部栏个人信息用到的变量
	 *****************************/
	private ImageView avatar;
	private ImageView hollow_avatar;
	private RelativeLayout baby_info_title;
	private String TAG = this.getClass().getSimpleName();

	private static boolean is_first_edit = false;// 是否刚刚注册-value
	public static final String IS_RECEIVER_EDIT = "is_receiver_edit";// 是否为收货信息编辑-key
	private EditText edt_nickname;

	/******************************************************
	 * 用于设置界面初始值，及判断界面值是否改变用到的变量。。
	 ******************************************************/

	/*********************************
	 * 用于及时释放内存的变量
	 *********************************/
	private Bitmap tempBitmap = null;// 用于临时保存新拍的头像
	private CustomAsyncTask updatetask = null;// 更新个人信息或者 刷新个人信息 或者 删除宝宝信息用到的异步
	/***************************************
	 * 从第三方服务器qq,sina获取用户头像
	 ***************************************/
	private AsyncBitmapTask imageViewTask = null;

	/*******************************************
	 * 动态获取宝宝信息用到的变量
	 *******************************************/
	private LinearLayout baby_info_content = null;
	protected final int REQUEST_ADD_BABY = 1000022;
	protected final int REQUEST_MODIFY_BABY = 1000023;
	private List<Child> childs = new ArrayList<Child>();
	private int DIMEN_BETWEEN_TEXT_AND_DRAWABLE = 30; // 设置宝宝信息栏，文本和图片间隔

	/****************************************
	 * 未登录情况下 增加的变量
	 ****************************************/
	private boolean isNeedToUpdateAvatarAndNickName = true; // 默认需要更新头像和昵称

	/********************************************
	 * 判断nickName是否改变的临时变量
	 ********************************************/
	private String preNickname = "";
	private boolean isDoSubmit = false;

	/****************************************
	 * 为了避免已登录情况下再进来该界面时，onResume方法的过度刷新()，用到的变量
	 * 
	 * 专门为该界面独立写一个， 其它界面共用父类的
	 ****************************************/
	private boolean isPreviousUnLoginStatus = true; // 前一个状态是未登录吗
	private boolean isRestarted = false; // 证明是页面流程是 个人信息界面 ->登录->个人信息界面

	/****************************************
	 * 专门用于 个人信息界面， 判断之前的状态 和 现在的 状态两个变量
	 ****************************************/
	private boolean isOnCreateLoginStat = false; // 默认onCreate时用户状态是未登录
	private boolean isOnResumeLoginStat = false; // 默认onResume用户状态也是未登录

	//added by liyang 2012-11-27
	LinearLayout layout_manage_address;// 管理收货地址框
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.edit_profile);
		setAvatarChanged(false);
		if (isUnlogin()) {
			setOnCreateLoginStat(false);
		} else {
			setOnCreateLoginStat(true);
		}

		Intent intent = getIntent();
		if(intent!=null){
		is_first_edit = intent.getBooleanExtra(IS_FIRST_EDIT, false);
		is_new_uid = intent.getBooleanExtra(IS_NEW_UID, false);
		is_from_third_party = intent
				.getBooleanExtra(IS_FROM_THIRD_PARTY, false);
		}
		edt_nickname = (EditText) findViewById(R.id.edt_nickname);
		setupViews();
		setupTopbarLabels();
		setBackgroundPicture(R.drawable.bg_wall);
		
//		initialAvatarForUnlogin();// added by edwar 2012-10-25 解决用户进来没有头像
		//modified by edwar, 2012-11-06 直接在xml里赋值未登录的默认头像。。避免获取未null的头像
	}

	private void initialAvatarForUnlogin() {

		Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
		if (bitmap != null) {
			setAvatar(bitmap);
			bitmap = null;
		} else {
			bitmap = BitmapFactory.decodeResource(BasicApplication
					.getInstance().getResources(), R.drawable.default_avatar);
			BasicApplication.getInstance().setAvatarBitmap(bitmap);
			setAvatar(bitmap);
			bitmap = null;
		}

	}

	/**
	 * 
	 * @param isNeedToUpdateAvatar
	 *            需要更新头像么？ 用于避免频繁获取头像
	 */
	private void loadPersonalProfile(boolean isNeed) {

		// 如果已经登录，就加载
		isNeedToUpdateAvatarAndNickName = isNeed;
		BaseReq request = new BaseReq();
		CustomAsyncTask task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);
	}

	/***************************************************************************
	 * 设置宝宝信息栏
	 ***************************************************************************/
	private void setBabyInfoIntoViews() {
		if (baby_info_content == null) {
			baby_info_content = (LinearLayout) findViewById(R.id.baby_info_content);
		}

		// 清空之前的view
		baby_info_content.removeAllViews();

		if (childs == null || childs.isEmpty()) {

		} else {
			for (final Child child : childs) {
				LinearLayout baby_row_view = (LinearLayout) View.inflate(
						getApplicationContext(),
						R.layout.single_baby_info_layout, null);
				/****************************************
				 * 设置短击事件
				 ****************************************/

				baby_row_view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// 将 这个Child传递到新的Activity里面

						Intent intent = new Intent(getApplicationContext(),
								AddOrModifyBabyActivity.class);
						intent.setAction("modifybaby");
						Bundle bundle = new Bundle();
						bundle.putSerializable("child", child);
						intent.putExtra("babyinfo", bundle);
						startActivityForResult(intent, REQUEST_MODIFY_BABY);
					}
				});
				/****************************************
				 * 设置长按事件
				 ****************************************/

				baby_row_view
						.setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								// 弹出确认对话框
								showmemberDialog(
										R.string.delete_baby_info_warning, 0,
										new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												closeDialog();
												// 确定

												/****************************************
												 * 组织请求
												 ****************************************/
												UpdateBabyInfoReq request = new UpdateBabyInfoReq();
												request.setStep(UpdateBabyInfoReq.DELETE_BABY);
												request.setId(child.getId());
												request.setCustomerId(child
														.getCustomerId());

												updatetask = new CustomAsyncTask(
														EditProfileActivity.this);
												updatetask
														.displayProgressDialog(R.string.doing_req_message);
												updatetask.execute(request);

											}
										}, new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												closeDialog();
												// 取消
											}
										});
								setDialogBtnTips(R.string.btn_confirm,
										R.string.btn_back);

								return true;
							}
						});

				// 设置文本
				TextView baby_name = (TextView) baby_row_view
						.findViewById(R.id.baby);
				baby_name.setText(child.getName());
				int icon_id = (child.getGenderCode() == null || child
						.getGenderCode().equals(Child.gender_boy)) ? R.drawable.male_icon
						: R.drawable.femail_icon;

				Resources res = getResources();
				// 设置图片
				Drawable d;
				d = res.getDrawable(icon_id);
				Drawable d1;
				d1 = res.getDrawable(R.drawable.selector_rightarrow);
				// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
				d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
				d1.setBounds(0, 0, d1.getMinimumWidth(), d1.getMinimumHeight());
				baby_name.setCompoundDrawables(d, null, d1, null); // 设置左图标

				baby_name
						.setCompoundDrawablePadding(DIMEN_BETWEEN_TEXT_AND_DRAWABLE);// 设置文本和图片间隔
				//
				baby_info_content.addView(baby_row_view);
			}
		}

		// 设置最底部的 “添加宝宝” 栏

		LinearLayout baby_row_view = (LinearLayout) View
				.inflate(getApplicationContext(),
						R.layout.single_baby_info_layout, null);
		// 添加宝宝事件
		baby_row_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (childs != null &&childs.size()>=3) {
					Toast.makeText(getApplicationContext(), R.string.add_baby_exceed_max_warning,Toast.LENGTH_SHORT).show();
				}else{
				Intent intent = new Intent(getApplicationContext(),
						AddOrModifyBabyActivity.class);
				intent.setAction("addbaby");
				startActivityForResult(intent, REQUEST_ADD_BABY);
				}
			}
		});

		// 设置文本
		TextView baby_name = (TextView) baby_row_view.findViewById(R.id.baby);
		baby_name.setText("宝宝");
		// 设置图片
		Resources res = getResources();
		Drawable d;
		d = res.getDrawable(R.drawable.add_icon);

		Drawable d1;
		d1 = res.getDrawable(R.drawable.selector_rightarrow);
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
		d1.setBounds(0, 0, d1.getMinimumWidth(), d1.getMinimumHeight());

		baby_name.setCompoundDrawables(d, null, d1, null); // 设置左图标
		baby_name.setCompoundDrawablePadding(DIMEN_BETWEEN_TEXT_AND_DRAWABLE);// 设置文本和图片间隔
		//
		baby_info_content.addView(baby_row_view);

	}

	private void setAvatarIntoViews() {
		/****
		 * 简单逻辑： 1,如果是第三方qq或者腾讯入口首次注册登录进来的，就直接从第三方的服务器获取nickname和头像
		 * 2,否则，从mama100服务器获取nickname和头像。
		 */
		if (!isAvatarChanged()) { // 解决先拍照，再登录，之前照片遗失的问题。added by 2012-10-25
									// edwar
			if (BasicApplication.getInstance().isFirstLogin()) {
				String url = BasicApplication.getInstance().getWeiboAvatarUrl();
				setAvatarFromThirdPatryIntoViews(url, true);
			} else {
				setAvatarWithPreviousBitmapOrTestBitmap();
			}
		}
	}

	private void setAvatarWithPreviousBitmapOrTestBitmap() {
		// avatar.setImageURI(BasicApplication.getInstance().getStoreAvatarUri());
		Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
		if (bitmap != null) {
			// LogUtils.loge("HomepageActivity", "get avatar - " + 8 +
			// " - from local getAvatarBitmap" );
			// Toast.makeText(EditProfileActivity.this, "get avatar - " + 8
			// + " - from local getAvatarBitmap" ,
			// Toast.LENGTH_SHORT).show();
			setAvatar(bitmap);
			bitmap = null;
		} else {
			loadValuesFromBasicApplication(); // 为了从意见反馈登录，再回设置，然后再进个人信息界面，会没有头像的问题
			// LogUtils.loge("HomepageActivity", "get avatar - " + 9 +
			// " - from local testavtar");
			// Toast.makeText(EditProfileActivity.this, "get avatar - " + 9
			// + " - from local testavtar" , Toast.LENGTH_SHORT).show();
			// 为了注册进来的情况
			// bitmap = BitmapFactory.decodeResource(BasicApplication
			// .getInstance().getResources(), R.drawable.default_avatar);
			// BasicApplication.getInstance().setAvatarBitmap(bitmap);
			// avatar.setImageBitmap(bitmap);
			// bitmap = null;
		}
	}

	private void setAvatar(Bitmap bitmap) {
		avatar.setImageBitmap(bitmap);
	}

	/*********************************************************************
	 * 打开界面，将Parcelable的值填充各变量值 用到的方法 --END
	 ********************************************************************/

	@Override
	protected void onRestart() {
		super.onRestart();
		isRestarted = true; // 证明是页面流程是 个人信息界面 ->登录->个人信息界面

	}

	@Override
	public void onResume() {
		if(AppConstants.NEED_TRACING)
		Debug.startMethodTracing("activity_trace");
		
		
		super.onResume();
		StatService.onResume(this);// 百度统计
		// 保存之前的nickname
		preNickname = edt_nickname.getText().toString();

		// 是否显示宝宝栏。。 只有既登录又关联才显示。
		if (!isUnlogin() && isAsso()) {
			displayBabyInfoBar();
		} else {
			hideBabyInfoBar();

		}
		// 如果未登录，直接离开
		if (isUnlogin()) {

			return;
		}
		
		if(!isAsso()){
			layout_manage_address.setVisibility(View.GONE);
		}else{
			layout_manage_address.setVisibility(View.VISIBLE);
		}
		
		//commented by edwar 2012-12-05 start, 不用这种方法关闭Activity
		//modified by edwar  及时清除无效的Activity 2012-11-07
//		BackStackManager.getInstance().removeActivity(AppConstants.ACTIVITY_LOGIN_HOMEPAGE);
		//commented by edwar 2012-12-05 end
		
		BackStackManager.getInstance().removeActivity(AppConstants.ACTIVITY_CRM_LOGIN_HOMEPAGE);
		
		setOnResumeLoginStat(true);
		resetPageValues();
		// MobileProbe.onResume(this);//CNZZ统计
		if(AppConstants.NEED_TRACING)
		Debug.stopMethodTracing();
		
	}

	// 为onResume时，从未登录到登录
	private void resetPageValues() {
		/****************************************
		 * 这边的逻辑是，从该界面的未登录进来，到已登录进来
		 * 
		 * 1，如果用户在未登录时，已经输入了用户名，或者编辑了头像，则显示用户输入的。
		 * 
		 * 2，如果用户没有输入任何值，则 1.1 判断，如果从第三方进来，则优先显示第三方的值和头像。。 1.2
		 * 
		 * 判断，如果不是从第三方进来，那么从服务器获取值，然后显示
		 * 
		 ****************************************/

		/**************** 第三方登录 *************************/
		if (is_from_third_party) {
			if (
			// 如果之前的状态是登录，而今也是登录，则是一般的登录 加载
			(isOnCreateLoginStat() && isOnResumeLoginStat() && (isRestarted == false))
					// 如果之前的状态是未登录，而今成为已登录，则要刷新页面
					|| (!isOnCreateLoginStat() && isOnResumeLoginStat() && (isRestarted == true))) {
				// 用完一次，就清空
				is_from_third_party = false;
				if (is_new_uid) {
					is_new_uid = false;
					// is_new_uid,用于只有第一次的新用户才从第三方服务器获取
					loadValuesFromThirdPartyServer();

					// 加上下面这两个，那么在 刚加载完第三方信息的个人界面->点Home->点该应用->个人界面，
					// 就不会再去loadValuesFromBasicApplication
					setOnCreateLoginStat(true);
					setOnResumeLoginStat(true);

				} else {
					loadValuesFromBasicApplication(); // 这个会获取不到宝宝信息，但可以获取用户头像
					loadPersonalProfile(true); // 这个可以获取宝宝信息，但获取不到用户头像

					// 加上下面这两个，那么在 刚加载完第三方信息的个人界面->点Home->点该应用->个人界面，
					// 就不会再去loadValuesFromBasicApplication
					setOnCreateLoginStat(true);
					setOnResumeLoginStat(true);
				}
			} else {

			}

		} else
		/**************** 非第三方登录 *************************/
		{
			if
			// 如果之前的状态是登录，而今也是登录，则是一般的登录 加载
			(isOnCreateLoginStat() && isOnResumeLoginStat()
					&& (isRestarted == false)) {
				loadPersonalProfile(true);
			} else if // 如果之前的状态是未登录，而今成为已登录，则要刷新页面
			(!isOnCreateLoginStat() && isOnResumeLoginStat()
					&& (isRestarted == true)) {
				loadValuesFromBasicApplication(); // 这个会获取不到宝宝信息，但可以获取用户头像
				loadPersonalProfile(true); // 这个可以获取宝宝信息，但获取不到用户头像

				// 加上下面这两个，那么在 刚加载完第三方信息的个人界面->点Home->点该应用->个人界面，
				// 就不会再去loadValuesFromBasicApplication
				setOnCreateLoginStat(true);
				setOnResumeLoginStat(true);
			} else {

			}
		}

		// /****************************************
		// * 显示宝宝信息
		// ****************************************/
		// if (isAsso()) {
		// isNeedToUpdateAvatarAndNickName = false;
		// BaseReq request = new BaseReq();
		// CustomAsyncTask task = new CustomAsyncTask(this);
		// task.execute(request);
		// }

	}

	private void loadValuesFromBasicApplication() {

		// 未登录进来，如果用户无输入， 昵称应该是空的。
		if (!isNickNameChanged()) {
			// 用户昵称
			String nickname = BasicApplication.getInstance().getNickname();
			edt_nickname.setText(nickname);
			setNickNameChanged(true);
		}

		if (!isAvatarChanged()) {
			// 用户头像
			String url = BasicApplication.getInstance().getLastAvatarUrl();
			setAvatarFromThirdPatryIntoViews(url, false);
			
		}

	}

	private void loadValuesFromThirdPartyServer() {

		// 未登录进来，如果用户无输入， 昵称应该是空的。
		if (!isNickNameChanged()) {
			// 用户昵称
			String nickname = BasicApplication.getInstance().getWeiboNickname();
			edt_nickname.setText(nickname);
			setNickNameChanged(true);
		}

		if (!isAvatarChanged()) {
			// 用户头像
			String url = BasicApplication.getInstance().getWeiboAvatarUrl();
			setAvatarFromThirdPatryIntoViews(url,true);
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);// 百度统计
		// MobileProbe.onPause(this);//CNZZ统计
		//1.内存测试 start
				free = Runtime.getRuntime().totalMemory();
				LogUtils.loge(TAG, "用掉的内存 - " + (total-free) + "字节");
	}

	private void setupTopbarLabels() {
		if (is_first_edit) {
			setTopLabel(R.string.edit_my_profile);

		} else
			setTopLabel(R.string.my_profile);
		setLeftButtonImage(R.drawable.selector_back);
		// 界面调整 by liyang
		// setRightButtonImage(R.drawable.selector_submit);
	}

	private void setupViews() {
		setTopAvatarbar();
		setImageCropParams(200, 200); // 一个小小的头像，200已经够了
		baby_info_content = (LinearLayout) findViewById(R.id.baby_info_content);
		baby_info_title = (RelativeLayout) findViewById(R.id.baby_info_title);
		//added by edwar 2012-10-30
		findViewById(R.id.layout_img_bg).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftWindowInput();
			}
		});
		
		
		//added by liyang 2012-11-27
		layout_manage_address = (LinearLayout) findViewById(R.id.layout_manage_address);
		layout_manage_address.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 跳转到管理收货地址页面
				Intent intent = new Intent(getApplicationContext(),CompleteRecevierAddressActivity.class);
				startActivity(intent);
			}
		});
	}

	// 隐藏宝宝信息栏
	private void hideBabyInfoBar() {
		baby_info_content.setVisibility(View.GONE);
		baby_info_title.setVisibility(View.GONE);
	}

	// 显示宝宝信息栏
	private void displayBabyInfoBar() {
		baby_info_content.setVisibility(View.VISIBLE);
		baby_info_title.setVisibility(View.VISIBLE);
	}

	/**
	 * 顶部照片栏
	 */
	private void setTopAvatarbar() {
		// 填充用户名
		// String username = StorageUtils
		// .getLastLoginAccount(getApplicationContext());
//		String username = BasicApplication.getInstance().getUsername();

		// ((TextView) findViewById(R.id.tv_acc_name)).setText(username);

		avatar = (ImageView) findViewById(R.id.imgV_face);
		avatar.setId(TAKE_PHOTO);
		avatar.setOnClickListener(this);

		hollow_avatar = (ImageView) findViewById(R.id.hollow_avatar);

		// 编辑按钮
		ImageButton edit = ((ImageButton) findViewById(R.id.edit_avatar));
		edit.setId(TAKE_PHOTO);
		edit.setOnClickListener(this);

	}

	@Override
	public void setPhotoIntoAvatar() {
		try {
			Uri curPhotoUri = BasicApplication.getInstance()
					.getCropImageStoreUri();
			BasicApplication.getInstance().setUploadAvatarUri(curPhotoUri);
			setAvatarChanged(true);
			// isPhoto TODO 是否要保留登录之前所拍的照片
			tempBitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), curPhotoUri);
			avatar.setBackgroundDrawable(null);
			// photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			setAvatar(tempBitmap);

			// 这个临时图片不用放进bitmapList里面，因为tempBitmap自己会根据是否上传判断是否要释放，释放早了，主页就没最新头像了。。

			// 头像率先抢掉焦点，以免让输入框获得焦点，从而避免进入界面就打开键盘
			avatar.requestFocusFromTouch();
			avatar.requestFocus();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/****************************************
	 * 从宝宝信息界面返回时，根据requestCode 是add 还是 modify 做相应的 操作。
	 ****************************************/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE:

			if (resultCode == RESULT_OK) {
				is_from_third_party = data.getBooleanExtra(IS_FROM_THIRD_PARTY,
						false);
				is_new_uid = data.getBooleanExtra(IS_NEW_UID, false);
				BasicApplication.getInstance().setAutoLogin(true);
				// 涉及到加载信息到输入框的情况下，就不主动点击;以免造成错误。
				// doClickRightBtn();
			
			} else {

			}
			break;

		case REQUEST_ADD_BABY:
			if (resultCode == RESULT_OK) {
				loadPersonalProfile(false);
			} else {

			}

			break;

		case REQUEST_MODIFY_BABY:
			if (resultCode == RESULT_OK) {
				loadPersonalProfile(false);
			} else {

			}

			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (layout_manage_address != null) {
			layout_manage_address.setBackgroundDrawable(null);
			layout_manage_address.removeAllViews();
		}
		
		if (childs != null && !childs.isEmpty()) {
			childs.clear();
			childs = null;
		}

		if (baby_info_content != null) {
			baby_info_content.removeAllViews();
			baby_info_content = null;
		}

		if (!isAvatarChanged) {
			if (tempBitmap != null && !tempBitmap.isRecycled()) {
				tempBitmap.recycle();
				tempBitmap = null;
			}
		}

		if (avatar != null)
			avatar.setBackgroundDrawable(null);
		if (hollow_avatar != null)
			hollow_avatar.setBackgroundDrawable(null);
		edt_nickname = null;

		if (updatetask != null && updatetask.isCancelled() == false) {
			updatetask.cancel(true);
			updatetask = null;
		}

		if (imageViewTask != null && imageViewTask.isCancelled() == false) {
			imageViewTask.cancel(true);
			imageViewTask = null;
		}
		clearImageViewMomery(avatar);
		clearImageViewMomery(hollow_avatar);
	}

	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		
		//added by edwar 2012-11-06 避免主页onResume时，显示这里放弃的第三方图片
		BasicApplication.getInstance().setAvatarBitmap(null);
		if (isAvatarChanged()
		// || isNickNameChanged()
		) {
			showmemberDialog(R.string.edit_profile_warning1,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							closeDialog();

//							// 从第三方登录进来，点返回，还要回到在未登录的时候，点击欲进入的界面,且之前欲进入的界面不是自身
//							if (is_from_third_party
//									&& BasicApplication.getInstance()
//											.getRequestCode() != AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE
//													//added by edwar 2012-12-04 Start, 解决会员卡界面(未登录)->登录或注册->进入该界面，回去会员卡界面不刷新登录状态的问题
//											||BasicApplication.getInstance().getRequestCode() == AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_SELECT_SHOP_PAGE
//													//added by edwar 2012-12-04 end		
//									) {
//								setResult(RESULT_OK);
//								finish();
//							} else {
//								finish();
//							}
							
							setResult(RESULT_OK);
							finish();
							
						}
					});
		} else {
//			if ((is_from_third_party
//					&& BasicApplication.getInstance().getRequestCode() != AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE)
//					//added by edwar 2012-12-04 Start, 解决会员卡界面(未登录)->登录或注册->进入该界面，回去会员卡界面不刷新登录状态的问题
//					||BasicApplication.getInstance().getRequestCode() == AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_SELECT_SHOP_PAGE
//							//added by edwar 2012-12-04 Start end.
//					) {
//				setResult(RESULT_OK);
//				finish();
//			} else {
//				finish();
//			}
			
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		doClickLeftBtn();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}
	
	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {

			// 更新个人信息
			if (request instanceof UpdateProfileReq) {

				File file = null;
				if (BasicApplication.getInstance().isAvatarChanged()) {

					/*
					 * Uri uri = BasicApplication.getInstance()
					 * .getUploadAvatarUri(); file = new File(uri.getPath());
					 */
					String path = SDCardUtil.getTempCropingPhotoPath();
					// @see BasicApplication.isNeedToDeleteSDCardTempFolder 字段
					String pathForUnlogin = SDCardUtil
							.getTempCropingPhotoPathForUnlogin(); // 用户未登录时，就拍了照
					// String path2 =
					// "/mnt/sdcard/mama100_data/temppic/temp_crop_pic.jpg";
					file = new File(path);
					if (file.exists()) {
						LogUtils.loge(TAG, "登录后指定拍照的图片路径存在");
					} else {
						LogUtils.loge(TAG, "登录后指定拍照的图片路径不存在");
						file = new File(pathForUnlogin);
						if (file.exists()) {
							LogUtils.loge(TAG, "未登录下，共用临时拍照的图片路径存在");
						} else {
							LogUtils.loge(TAG, "未登录下，共用临时拍照的图片路径不存在");
							file = null;
							LogUtils.loge(TAG, "无图片被上传");
						}
					}
				}

				isDoSubmit = true;
				return UserProvider.getInstance(mContext).updateProfile(
						(UpdateProfileReq) request, file);
			}
			// 删除宝宝信息
			else if (request instanceof UpdateBabyInfoReq) {
				isDoSubmit = false;
				return UserProvider.getInstance(mContext).updateBabyInfo(
						(UpdateBabyInfoReq) request);
			}
			// 获取或刷新个人信息
			else {
				isDoSubmit = false;
				return UserProvider.getInstance(getApplicationContext())
						.getProfile(request);
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				Toast.makeText(mContext, response.getDesc(), Toast.LENGTH_SHORT)
				.show();
				return;
			}

			// 从第三方登录进来，点提交，成功后，还要回到在未登录的时候，点击欲进入的界面,且不是该界面
			if (isDoSubmit) {
//				if ((isDoSubmit
//						&& is_from_third_party
//						&& (BasicApplication.getInstance().getRequestCode() != AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE))
//						//added by edwar 2012-12-04 Start, 解决会员卡界面(未登录)->登录或注册->进入该界面，回去会员卡界面不刷新登录状态的问题
//						||(isDoSubmit&&BasicApplication.getInstance().getRequestCode() == AppConstants.REQUEST_CODE_UNLOGIN_INTO_REGPOINT_SELECT_SHOP_PAGE)
//						//added by edwar 2012-12-04 end
//						) {
				setResult(RESULT_OK);
				finish();
			}

			// 1， 删除宝宝信息
			if (response instanceof UpdateBabyInfoRes) {
				loadPersonalProfile(false);
			} else
			// 2,获取个人信息
			if (response instanceof GetProfileRes) {
				/****************************************
				 * 刷新页面 及 当前宝宝信息
				 ****************************************/
				GetProfileRes res = (GetProfileRes) response;

				if (isNeedToUpdateAvatarAndNickName) {
					// 将文本信息显示出来
					String nickname = res.getNickname();
					edt_nickname.setText(nickname);
					// 将内存里的用户个人头像显示出来
					setAvatarIntoViews();
				}

				// 只有已经关联积分通的用户，才能看到宝宝栏
				if (isAsso()) {
					// 清空列表先
					if (childs != null && !childs.isEmpty()) {
						childs.clear();
					}
					childs = res.getChilds();
					setBabyInfoIntoViews();
				}
			} else {

				// 3，更新个人资料
				// 更新本地头像
				if (isAvatarChanged) {
					BasicApplication.getInstance().setAvatarBitmap(tempBitmap);
				}

				// 保存新修改的昵称，也为了保存第三方的昵称
				if (isNickNameChanged()) {
					BasicApplication.getInstance().setNickname(
							edt_nickname.getText().toString());
				}

				/***
				 * 为了注册登录进来的流程，完善个人资料 提交之后，要进入主界面
				 */
				finish();

			}
		}

	}

	/*******************************************************************
	 * 其它方法
	 *******************************************************************/

	private boolean isNickNameChanged = false;

	// 个人昵称是否改变
	private boolean isNickNameChanged() {
		return isNickNameChanged;
	}

	// 设置个人昵称是否改变
	private void setNickNameChanged(boolean flag) {
		this.isNickNameChanged = flag;
	}

	// 个人头像是否改变
	private boolean isAvatarChanged() {
		return isAvatarChanged;
	}

	private boolean isAvatarChanged = false;

	public void setAvatarChanged(boolean isAvatarChanged) {
		this.isAvatarChanged = isAvatarChanged;
		BasicApplication.getInstance().setAvatarChanged(isAvatarChanged);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void clicksubmit(View view) {
		if (isUnlogin()) {
			goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_PROFILE_PAGE);
			return;
		}

		if (!verifyInput(edt_nickname,
				AppConstants.CHECK_NICKNAME)) {
			return;
		}

		final UpdateProfileReq request = new UpdateProfileReq();
		request.setNickname(edt_nickname.getText().toString());
		updatetask = new CustomAsyncTask(this);
		updatetask.execute(request);
		updatetask.displayProgressDialog(R.string.updating_profile_tip);
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

	/*******************************************************
	 * 从第三方服务器获取用户头像
	 * isAvatarChanged
	 *******************************************************/
	private void setAvatarFromThirdPatryIntoViews(String imgUrl , boolean isAvatarChanged) {

		if (StringUtils.isBlank(imgUrl)) {
			
			String filename = SDCardUtil.getPictureStorePath()
					+ AppConstants.TEMP_STORE_PICTURE_NAME;
			Bitmap bitmap = PictureUtil.getPictureFromSDcardByPath(filename);
			//modified by edwar, 2012-11-06 直接在xml里赋值未登录的默认头像，所以这里没有必要再赋值，精简赋值
		} else {

			// 异步加载图片
			avatar.setTag(imgUrl);
			imageViewTask = new AsyncBitmapTask(EditProfileActivity.this, true,
					true);
			imageViewTask.execute(avatar);
			// 因为从第三方获取的，所以相当于更新了头像
						setAvatarChanged(isAvatarChanged);
						//清空第一次登录的状态
						BasicApplication.getInstance().setFirstLogin(false);
		}

		// 2,修改头像
		// 设置头像为本地默认的头像,用于在个人信息界面修改头像之后回到首页的头像更新
		// 对于异步头像获取，在获取过程中，先从本地加载头像， 然后更新服务器最新的。。

		// LogUtils.logi(TAG, "set avatar 2 - : "
		// + new Timestamp(System.currentTimeMillis()).toString());
		avatar.setScaleType(ScaleType.FIT_CENTER);

		tempBitmap = BasicApplication.getInstance().getAvatarBitmap();
		if(tempBitmap!=null)
			setAvatar(tempBitmap);
		BasicApplication.getInstance().setAvatarBitmap(null);
		// LogUtils.logi(TAG, "set avatar 3 - : "
		// + new Timestamp(System.currentTimeMillis()).toString());

	}

	public boolean isOnCreateLoginStat() {
		return isOnCreateLoginStat;
	}

	public void setOnCreateLoginStat(boolean isOnCreateLoginStat) {
		this.isOnCreateLoginStat = isOnCreateLoginStat;
	}

	public boolean isOnResumeLoginStat() {
		return isOnResumeLoginStat;
	}

	public void setOnResumeLoginStat(boolean isOnResumeLoginStat) {
		this.isOnResumeLoginStat = isOnResumeLoginStat;
	}
}
