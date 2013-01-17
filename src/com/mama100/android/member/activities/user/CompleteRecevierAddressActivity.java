package com.mama100.android.member.activities.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.activities.regpoint.ActivateECardActivity;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.GetReceiverAddressRes;
import com.mama100.android.member.domain.user.UpdateReceiverAddressReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.widget.wheel.OnWheelChangedListener;
import com.mama100.android.member.widget.wheel.OnWheelScrollListener;
import com.mama100.android.member.widget.wheel.WheelView;
import com.mama100.android.member.widget.wheel.adapter.AbstractWheelTextAdapter;
import com.mama100.android.member.widget.wheel.adapter.ArrayWheelAdapter;

public class CompleteRecevierAddressActivity extends BaseActivity {

	/****************************************
	 * 完善收货人地址
	 ****************************************/

	EditText receiver_name; // 收货人姓名
	EditText receiver_province_and_city; // 收货人省市
	EditText receiver_detail_address;// 详细地址
	Button submit_address;// 提交地址

	/****************************************
	 * 省市
	 ****************************************/
	private String addressProvince;
	private String addressCity;

	private CustomAsyncTask task;
	private boolean isFromActivate = false;

	// 注:2012-10-30 目前这个用不到，这个激活入口，被去掉了，这里先保留，留着以后备用。
	public static final int REQUEST_CODE_ACTIVATE_ECARD_ON_COMPLETE_ADDRESS = 11111225;// 未开通积分通前提下，进入完善地址界面，触发的请求

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_receiver_address);
		setupTopBar();
		setupViews();
		setBackgroundPicture(R.drawable.bg_wall2);
		loadReceiverAddress();
		Intent intent = getIntent();
		checkIsFromActivateCard(intent);

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	private void checkIsFromActivateCard(Intent intent) {
		String from = intent.getStringExtra("from");
		if (!StringUtils.isBlank(from)
				&& from.equals(ActivateECardActivity.ACTIVATE)) {
			isFromActivate = true;
		}
	}

	/**
	 * 加载收货人地址信息
	 */
	private void loadReceiverAddress() {
		// 如果未登录，直接离场
		if (isUnlogin())
			return;

		BaseReq request = new BaseReq();
		task = new CustomAsyncTask(CompleteRecevierAddressActivity.this);
		task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);

	}

	private void setupTopBar() {
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonVisibility(View.INVISIBLE);
		setTopLabel(getString(R.string.manage_receiver_address));
	}

	private void setupViews() {

		receiver_name = (EditText) findViewById(R.id.receiver_name);
		receiver_name.setInputType(InputType.TYPE_CLASS_TEXT);

		receiver_province_and_city = (EditText) findViewById(R.id.receiver_province_and_city);
		receiver_province_and_city.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog();
			}
		});

		receiver_detail_address = (EditText) findViewById(R.id.receiver_detail_address);

		((Button) findViewById(R.id.submit_address))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						/****************************************
						 * 考虑未登录进来的情况， added by edwar 2012-10-17
						 * 更新：这两个，一个登录入口，一个激活会员卡入口，目前都被取消。 这里先留着，以后备用。added by
						 * edwar 2012-10-30 START
						 ****************************************/
						// 1,判断是否已经登录
						if (isUnlogin()) {
							goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_COMPLETE_ADDRESS_PAGE);
							return;
						} else
						// 2,判断是否已经关联
						if (!isAsso()) {
							showmemberDialog(
									R.string.activate_member_card_warning_tips,
									new OnClickListener() {

										@Override
										public void onClick(View v) {
											Intent intent = new Intent(
													getApplicationContext(),
													ActivateECardActivity.class);
											intent.putExtra("requestcode",
													REQUEST_CODE_ACTIVATE_ECARD_ON_COMPLETE_ADDRESS);
											startActivityForResult(intent,
													REQUEST_CODE_ACTIVATE_ECARD_ON_COMPLETE_ADDRESS);
											closeDialog();
										}
									});
							return;
						}
						/****************************************
						 * 考虑未登录进来的情况， added by edwar 2012-10-17
						 * 更新：这两个，一个登录入口，一个激活会员卡入口，目前都被取消。 这里先留着，以后备用。added by
						 * edwar 2012-10-30 END
						 ****************************************/

						// 验证姓名
						if (!verifyInput(receiver_name,
								AppConstants.CHECK_CUSTOMER_NAME)) {
							return;
						}

						// 验证省市不能为空
						if (StringUtils.isBlank(receiver_province_and_city
								.getText().toString())) {
							makeText(R.string.receiver_province_and_city_null_illlegal);
							return;
						}

						// added by edwar 2012-10-30
						if (!verifyInput(receiver_detail_address,
								AppConstants.CHECK_RECEIVER_ADDRESS2)) {
							return;
						}
						// end

						// // 验证收货详细地址不能为空
						// if
						// (StringUtils.isBlank(receiver_detail_address.getText().toString())){
						// receiver_detail_address.setError(getString(R.string.receiver_address_illlegal));
						// return;
						// }

						String name = receiver_name.getText().toString();
						String address = receiver_detail_address.getText()
								.toString();

						UpdateReceiverAddressReq request = new UpdateReceiverAddressReq();
						request.setReceiver(name);
						request.setAddress(address);
						request.setAddressProvince(addressProvince);
						request.setAddressCity(addressCity);

						task = new CustomAsyncTask(
								CompleteRecevierAddressActivity.this);
						task.displayProgressDialog(R.string.doing_req_message);
						task.execute(request);

					}
				});
	}

	@Override
	public void doClickLeftBtn() {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		String tag = BasicApplication.getInstance().getCustomerInfoCompleted();
		if (tag == null || (tag != null && tag.equals("0"))) {
			showmemberDialog(R.string.cancel_complete_address_warning,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 如果是从 激活会员卡界面过来的话， 则要回到之前的界面
							// 已经激活成功，点“返回”按钮，就不能再回到第二页。而是回主界面。
							if (isFromActivate) {
								// startActivity(new
								// Intent(getApplicationContext(),
								// RegPointHomeActivity.class));
								setResult(RESULT_CANCELED);
								finish();
							} else {
								finish();
							}
						}
					});
		} else {
			finish();
		}
	}

	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {

			// 1, 更新收货人地址
			if (request instanceof UpdateReceiverAddressReq) {
				return UserProvider.getInstance(getApplicationContext())
						.updateReceiverAddress(
								(UpdateReceiverAddressReq) request);
			} else
			// 2, 获取收货人地址
			{
				return UserProvider.getInstance(getApplicationContext())
						.getReceiverAddress((BaseReq) request);
			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			// 操作失败的时候才显示提示信息
			
			if (response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				// 返回的是 获取收货人地址的响应
				if (response instanceof GetReceiverAddressRes) {
					String name = ((GetReceiverAddressRes) response)
							.getReceiver();
					String address = ((GetReceiverAddressRes) response)
							.getAddress();
					String addProvince = ((GetReceiverAddressRes) response)
							.getAddressProvince();
					String addCity = ((GetReceiverAddressRes) response)
							.getAddressCity();
					// 初始化界面
					InitialViewWithLoadedValue(name, addProvince, addCity,
							address);
				} else {
					String tag = BasicApplication.getInstance()
							.getCustomerInfoCompleted();
					if (tag == null || (tag != null && tag.equals("0"))) {
						BasicApplication.getInstance()
								.setCustomerInfoCompleted("1");
						LogUtils.logi("address", "" + 1);
					}
					if (isFromActivate) {
						// startActivity(new Intent(getApplicationContext(),
						// RegPointHomeActivity.class));
						LogUtils.logi("address", "result ok");
						setResult(RESULT_OK);
					}
					finish();
				}
			}else{
				makeText(response.getDesc());
			}
		}
	}

	/**
	 * @param name
	 *            姓名
	 * @param address
	 *            地址
	 */

	public void InitialViewWithLoadedValue(String name, String addressProvince,
			String addressCity, String address) {
		if (!StringUtils.isBlank(name)) {
			receiver_name.setText(name);
		}

		if (!StringUtils.isBlank(address)) {
			receiver_detail_address.setText(address);
		}

		if (!StringUtils.isBlank(addressProvince)
				&& !StringUtils.isBlank(addressCity)) {
			receiver_province_and_city.setText(addressProvince + addressCity);
		}

		this.addressProvince = addressProvince;
		this.addressCity = addressCity;

		// added by edwar 2012-11-02 更新用户是否完善了地址
		if (isNameComplete() && isProvinceComplete(addressProvince)
				&& isCityComplete(addressCity) && isDetailedAddressComplete()) {
			BasicApplication.getInstance().setCustomerInfoCompleted("1");
		} else {
			BasicApplication.getInstance().setCustomerInfoCompleted("0");
		}
		// added end
	}

	private boolean isDetailedAddressComplete() {
		return verifyInput(receiver_detail_address,
				AppConstants.CHECK_RECEIVER_ADDRESS2);
	}

	private boolean isCityComplete(String city) {
		return !StringUtils.isBlank(city);
	}

	private boolean isProvinceComplete(String province) {
		return !StringUtils.isBlank(province);
	}

	private boolean isNameComplete() {
		// 验证姓名
		return verifyInput(receiver_name, AppConstants.CHECK_CUSTOMER_NAME);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_COMPLETE_ADDRESS_PAGE:
				// 再次调用之前的点击事件。。
				((Button) findViewById(R.id.submit_address)).performClick();
				break;

			case REQUEST_CODE_ACTIVATE_ECARD_ON_COMPLETE_ADDRESS:
				break;
			default:
				break;
			}
		}
	}

	/** 用来判断是否还可以继续滚动 */
	private boolean scrolling = false;

	/** 用来保存省或者直辖市的数组 */
	private String[] provinces;

	/** 用来保存城市的二维数组 */
	private String[][] cities;

	/** 用来显示省或者直辖市的控件 */
	private WheelView province;

	/** 用来显示城市的控件 */
	private WheelView city;

	// 点击省市地区输入框时弹出来
	private void showDialog() {
		initData();

		int provinceIndex = getProvinceIndex(addressProvince);
		int cityIndex = getCityIndex(addressCity, provinceIndex);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		View view = getLayoutInflater().inflate(R.layout.wheel_dialog, null);
		// 用来显示城市
		city = (WheelView) view.findViewById(R.id.city);
		city.setVisibleItems(5);

		// 用来显示省份直辖市
		province = (WheelView) view.findViewById(R.id.country);
		province.setViewAdapter(new CountryAdapter(this));
		// 可以设置成无限循环滚动
		province.setCyclic(true);
		province.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateCities(city, cities, newValue);
				}
			}
		});

		province.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updateCities(city, cities, province.getCurrentItem());
			}
		});

		province.setCurrentItem(provinceIndex);

		// 初始化城市控件
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				cities[provinceIndex]);
		city.setViewAdapter(adapter);
		city.setCurrentItem(cityIndex);

		builder.setView(view);
		builder.setTitle("请选择省市地区");
		builder.setNeutralButton("确定",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(province.getCurrentItem()<provinces.length){
							addressProvince = provinces[province.getCurrentItem()];
						
							if(city.getCurrentItem()<cities[province.getCurrentItem()].length){
								addressCity = cities[province.getCurrentItem()][city.getCurrentItem()];
							}
						}
						
						
						// 设置省市显示
						receiver_province_and_city.setText(addressProvince
								+ addressCity);
					}
				});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	/**
	 * 加载数据 省市所有的数据都以XML文件的形式保存在values下面
	 */
	private void initData() {

		provinces = getResources().getStringArray(R.array.province);

		cities = new String[][] {
				getResources().getStringArray(R.array.beijing),
				getResources().getStringArray(R.array.tianjing),
				getResources().getStringArray(R.array.shanghai),
				getResources().getStringArray(R.array.chongqing),
				getResources().getStringArray(R.array.liaoning),
				getResources().getStringArray(R.array.jilin),
				getResources().getStringArray(R.array.heilongjiang),
				getResources().getStringArray(R.array.hebei),
				getResources().getStringArray(R.array.shanxi),
				getResources().getStringArray(R.array.henan),
				getResources().getStringArray(R.array.shandong),
				getResources().getStringArray(R.array.jiangsu),
				getResources().getStringArray(R.array.anhui),
				getResources().getStringArray(R.array.jiangxi),
				getResources().getStringArray(R.array.zhejiang),
				getResources().getStringArray(R.array.fujian),
				getResources().getStringArray(R.array.guangdong),
				getResources().getStringArray(R.array.hainan),
				getResources().getStringArray(R.array.guizhou),
				getResources().getStringArray(R.array.yunnan),
				getResources().getStringArray(R.array.sichuan),
				getResources().getStringArray(R.array.hunan),
				getResources().getStringArray(R.array.hubei),
				getResources().getStringArray(R.array.shanxi2),
				getResources().getStringArray(R.array.gansu),
				getResources().getStringArray(R.array.qinghai),
				getResources().getStringArray(R.array.neimenggu),
				getResources().getStringArray(R.array.xizang),
				getResources().getStringArray(R.array.xinjiang),
				getResources().getStringArray(R.array.guangxi),
				getResources().getStringArray(R.array.ningxia) };
	}

	/**
	 * Updates the city wheel
	 */
	private void updateCities(WheelView city, String cities[][], int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				cities[index]);
		city.setViewAdapter(adapter);
		city.setCurrentItem(cities[index].length / 2);
	}

	/**
	 * Adapter for countries
	 */
	private class CountryAdapter extends AbstractWheelTextAdapter {
		/**
		 * Constructor
		 */
		protected CountryAdapter(Context context) {
			super(context, R.layout.province, NO_RESOURCE);

			// setItemTextResource(R.id.country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);

			TextView tv = (TextView) view.findViewById(R.id.flag);
			tv.setText(provinces[index]);
			return view;
		}

		@Override
		public int getItemsCount() {
			return provinces.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return provinces[index];
		}
	}

	/**
	 * 返回省索引
	 * 
	 * @return
	 */
	public int getProvinceIndex(String province) {
		for (int i = 0; i < provinces.length; i++) {
			if (provinces[i].equals(province)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 返回市索引
	 * 
	 * @return
	 */
	public int getCityIndex(String city, int provinceIndex) {
		// 获得某个省下面的城市数组
		String[] selectCitys = cities[provinceIndex];

		for (int i = 0; i < selectCitys.length; i++) {
			if (selectCitys[i].equals(city)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		provinces = null;
		cities = null;
	}
}
