/**
 * 
 */
package com.mama100.android.member.activities.user;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.bean.Child;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.UpdateBabyInfoReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.DateHelper;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.widget.EditTextEx;

/**
 * <p>
 * Description: AddOrModifyBabyActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-10-10 添加或修改宝宝信息界面
 */
public class AddOrModifyBabyActivity extends BaseActivity {
	private String TAG = this.getClass().getSimpleName();

	private EditText et_baby_name = null;
	private TextView et_baby_birth = null;
	private ImageView img_male = null;
	private ImageView img_female = null;

	private boolean isMale = true;// 默认男孩
	private boolean isNoGender = true;// 新增宝宝下，默认先没有性别
	private boolean isAddNewBaby = true;// 默认是新增一个宝宝
	private Child child = null; // 从intent传来的对象

	private CustomAsyncTask task;// 上传信息

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/***********************************
		 * 1，渲染主界面
		 * ********************************/
		setContentView(R.layout.add_or_modify_baby_info);

		/****************************************
		 * 2, 从intent里获取上个界面传来的意图
		 ****************************************/
		Intent intent = getIntent();
		judgeIsAddOrModifyBaby(intent);

		/****************************************
		 * 3, 建立组件变量
		 ****************************************/
		setupViews();

		/****************************************
		 * 4， 建立顶部栏
		 ****************************************/
		setupTopbarLabels();
		
		/****************************************
         * 5, 根据是否是添加还是修改，初始化当前页面
         ****************************************/
		initializeValuesForCurrentPage(child);

	}
	
	
/**
 * 根据是否是添加还是修改，初始化当前页面
 * @param childObj  从上个页面传来的宝宝对象，如果是添加，则为null
 */
	private void initializeValuesForCurrentPage(Child childObj) {
		
		/****************************************
         * 1， 初始化姓名 和 生日
         ****************************************/
		
		//前提：意图的action是编辑，且传过来的对象不为空
		if(!isAddNewBaby()&&childObj!=null){
			et_baby_name.setText(childObj.getName());
			et_baby_birth.setText(childObj.getBirthdateFront());
		}
		
		/****************************************
         * 2, 初始化性别栏
         ****************************************/
		checkGenderTypeAndInitialButtonBar();
	}

	private void setupTopbarLabels() {
		setTopBarVisibility(View.VISIBLE);
		setTopLabel(R.string.baby_info);
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonVisibility(View.INVISIBLE);
		setBackgroundPicture(R.drawable.bg_wall);
	}

	// 从intent 判读用户的 意图是 添加还是 编辑
	private void judgeIsAddOrModifyBaby(Intent intent) {
		if (intent != null) {
			String action = intent.getAction();
			if (action.equals("addbaby")) {
				setAddNewBaby(true);
			} else if (action.equalsIgnoreCase("modifybaby")) {
				setAddNewBaby(false);
				child = (Child) intent.getBundleExtra("babyinfo")
						.getSerializable("child");
			}
		} else {

		}
	}

	private void setupViews() {
		et_baby_name = (EditText) findViewById(R.id.et_baby_name);
		et_baby_birth = (TextView) findViewById(R.id.et_baby_birth);
		
		setBirthDateListener();
		img_female = (ImageView) findViewById(R.id.female_icon);
		img_male = (ImageView) findViewById(R.id.male_icon);
		img_female.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//如果无性别
				if (isNoGender()) {
					switchButton(isMale = false);
					setNoGender(false);
					return;
				}
				// 点“女”的按钮，只有在当前是男性的前提下，才有用。如果当前已经是女的，则按钮不起作用。同理，对于男性。
				if (isMale) {
					switchButton(isMale = !isMale);
				}
			}
		});

		img_male.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//如果无性别
				if (isNoGender()) {
					switchButton(isMale = true);
					setNoGender(false);
					return;
				}

				if (!isMale) {
					switchButton(isMale = !isMale);
				}
			}
		});
	}

	/****************************************
	 * 检查宝宝性别类型 及 初始化 性别栏
	 ****************************************/
	private void checkGenderTypeAndInitialButtonBar() {

		if (isAddNewBaby()) {
			img_female.setImageLevel(2);
			img_male.setImageLevel(2);
			setNoGender(true);
		} else {
			if (child != null && !StringUtils.isBlank(child.getGenderCode())) {
				setNoGender(false);
				isMale = child.getGenderCode().equalsIgnoreCase(
						Child.gender_boy);
				switchButton(isMale);
			} else {
				setNoGender(true);
				img_female.setImageLevel(2);
				img_male.setImageLevel(2);
			}
		}

	}

	/**
	 * 切换宝宝的性别栏
	 * @param sexflag
	 *            当前要实现的宝宝性别，true 代表男孩，false代表女孩
	 */
	private void switchButton(boolean sexflag) {

		img_female.setImageLevel((sexflag) ? 2 : 7);// 2，暗；7,亮
		img_male.setImageLevel((sexflag) ? 7 : 2);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_CANCELED);
		finish();
	}



	/****************************************
	 * 提交 或者 保存 要 验证 及 上传的方法
	 ****************************************/
	public void doSubmit(View v){
		
		/****************************************
         * 验证用户输入的宝宝信息
         ****************************************/
		// 验证宝宝姓名
		if (!checkName(et_baby_name.getText().toString())) {
			return;
		}
		
		// 验证宝宝生日
		if (!checkBirth(et_baby_birth.getText().toString())) {
			return;
		}
		
		// 验证宝宝性别
		if(isNoGender()){
			Toast.makeText(AddOrModifyBabyActivity.this, R.string.baby_sex_illlegal, Toast.LENGTH_SHORT).show();
			return;
		}
		
		/****************************************
         * 组织请求
         ****************************************/
		UpdateBabyInfoReq request = new UpdateBabyInfoReq();
		/****************************************
         * 判断是否是添加还是编辑，来确定是否上传数据库id
         ****************************************/
		if(!isAddNewBaby()){
			request.setId(child.getId());
		}
		request.setName(et_baby_name.getText().toString());
		request.setBirthdate(et_baby_birth.getText().toString());
		request.setGenderCode(((isMale)?Child.gender_boy:Child.gender_girl));

		task = new CustomAsyncTask(this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);
		
		
		
	}
	
	
	private boolean checkBirth(String value) {
		if(StringUtils.isBlank(value)){
			makeText(R.string.baby_birthdate_illlegal);
			return false;
		}
		return true;
	}

	
	private boolean checkName(String value) {
		try {
			if(StringUtils.isBlank(value)){
				makeText(R.string.baby_name_hint);
				return false;
			}else if(value.length() < 2){
				makeText(R.string.baby_name_len_short);
				return false;
			}else if(value.getBytes("GBK").length > 18){
				makeText(R.string.baby_name_len_long);
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	

	/****************************************
         *保存宝宝信息
         ****************************************/
	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			if (request instanceof UpdateBabyInfoReq) {
				return UserProvider.getInstance(getApplicationContext())
						.updateBabyInfo((UpdateBabyInfoReq) request);
			} 	else {
				return null;
			}

		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			makeText(response.getDesc());
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				return;
			}
			setResult(RESULT_OK);
			finish();
		}
	}
	
	
	/*********************************************************************
	 * 弹出 Calendar 选择日期 用到的变量和方法 --START
	 ********************************************************************/

	private void setBirthDateListener() {
		et_baby_birth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_BEGIN_TIME);
//				final DatePickerDialog mDatePickerDialog = new DatePickerDialog(
//						EditProfileActivity.this, new MyDateSetListener(),
//						cal_birth_time.get(Calendar.YEAR), cal_birth_time
//								.get(Calendar.MONTH), cal_birth_time
//								.get(Calendar.DAY_OF_MONTH));
//				mDatePickerDialog.show();
			}
		});
	}
	
	
	// DatePickerDialog弹开窗口配套的callback
	private MyDateSetListener dateSetListener = new MyDateSetListener();
	// begin_time_btn对应的时间选择窗口
	DatePickerDialog mDatePickerDialog = null;
	// 开始时间
	private static final int DIALOG_BEGIN_TIME = 0;
	private Calendar cal_birth_time = Calendar.getInstance();
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");// 时间编码格式 年-月-日，如 2012-06-07
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_BEGIN_TIME:
				mDatePickerDialog = new DatePickerDialog(this, dateSetListener,
						cal_birth_time.get(Calendar.YEAR),
						cal_birth_time.get(Calendar.MONTH),
						cal_birth_time.get(Calendar.DAY_OF_MONTH));
				return mDatePickerDialog;
		}
		return null;
	}

	

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_BEGIN_TIME:
			if (!StringUtils.isBlank(et_baby_birth.getText().toString())) {
				String time = et_baby_birth.getText().toString();
				Date date = DateHelper.strToDate(time);
				Calendar car = Calendar.getInstance();
				if(date!=null){
					car.setTime(date);
				}
				//et_baby_birth的String如果不能转成日期
				else{
					et_baby_birth.setText(SIMPLE_DATE_FORMAT
					.format(car.getTime()));
				}
				((DatePickerDialog) dialog)
						.updateDate(car.get(Calendar.YEAR),
								car.get(Calendar.MONTH),
								car.get(Calendar.DAY_OF_MONTH));
			}else{
				((DatePickerDialog) dialog).updateDate(
						cal_birth_time.get(Calendar.YEAR),
						cal_birth_time.get(Calendar.MONTH),
						cal_birth_time.get(Calendar.DAY_OF_MONTH));
			}
			break;
		}
	}
	
	// 选择具体日期，确定触发的DateSet设置
	public class MyDateSetListener implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			cal_birth_time.set(year, monthOfYear, dayOfMonth);
			et_baby_birth.setText(SIMPLE_DATE_FORMAT
					.format(cal_birth_time.getTime()));
		}

	}


	/*********************************************************************
	 * 弹出 Calendar 选择日期 用到的变量和方法 --END
	 ********************************************************************/
	
	
	/****************************************
	 * 一般的 方法
	 ****************************************/
	public boolean isMale() {
		return isMale;
	}

	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	public boolean isAddNewBaby() {
		return isAddNewBaby;
	}

	public void setAddNewBaby(boolean isAddNewBaby) {
		this.isAddNewBaby = isAddNewBaby;
	}

	public boolean isNoGender() {
		return isNoGender;
	}

	public void setNoGender(boolean isNoGender) {
		this.isNoGender = isNoGender;
	}
	
	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		onBackPressed();
	}
}
