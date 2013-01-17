package com.mama100.android.member.activities.babyshop;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.activities.babyshop.MapBasicActivity.MGeneralListener;
import com.mama100.android.member.asynctask.AsyncLoadImg;
import com.mama100.android.member.bean.babyshop.ConcernedBabyShop;
import com.mama100.android.member.bean.babyshop.NearBabyShop;
import com.mama100.android.member.businesslayer.BabyShopProvider;
import com.mama100.android.member.domain.babyshop.ConcernedShopReq;
import com.mama100.android.member.domain.babyshop.ConcernedShopRes;
import com.mama100.android.member.domain.babyshop.NearShopReq;
import com.mama100.android.member.domain.babyshop.NearShopRes;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.widget.scrollview.HorizontalPager;

/**
 * 
 * @author eCo
 *
 */
public class BabyShopActivity extends BaseActivity {

	View tips_tmp;// 为获得popup window的高宽而设置了临时变量，无其它作用
	View call_menu_tmp; // 作用类似上述tips_tmp，另外也用于显示。
	Dialog call_menu;

	ListView list_shops;
	View list_footer;
	private boolean hasLoadNearShops = false;
	public static NearShopsListAdapter list_shops_adap = null;
	private NearShopReq nearReq=null;

	private boolean hasLoadConcernedShops = false;
	List<ConcernedBabyShop> concernedShops;

	// 推荐活动 支持左右滑动的控件
	private HorizontalPager actPager;
	// 显示当前是第几个活动的小圆点
	private LinearLayout actIdxContainer;
	
	private ScrollView scrV_main;
	// 关注的门店个数
	private int size;

	LocationListener mLocationListener;
	
	public static int[] shopDistances=null;
	
	
	private double lat = 720.0d;// 纬度不能初始化为0,可能存在纬度为0
	private double lon = 720.0d;
	private String TAG = this.getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.babyshop);

		//
		tips_tmp = View.inflate(this, R.layout.shop_item_poptip, null);
		addContentView(tips_tmp, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		tips_tmp.setVisibility(View.INVISIBLE);

		call_menu_tmp = View.inflate(getApplicationContext(),
				R.layout.call_menu, null);
		addContentView(call_menu_tmp, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		call_menu_tmp.setVisibility(View.INVISIBLE);

		setBackgroundPicture(R.drawable.bg_wall);
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonImage(R.drawable.selector_map);
		setTopLabel(getString(R.string.baby_shop));

		list_shops = (ListView) findViewById(R.id.list_shops);
		list_footer=View.inflate(this, R.layout.shop_list_buttom, null);
		list_shops.addFooterView(list_footer);
		list_footer.setVisibility(View.GONE);
		list_shops.addHeaderView(View.inflate(getApplicationContext(), R.layout.shop_list_head, null));

		getView();

		setListeners();

		/******* 以下获得用户当前经纬度 ********/
		if (MapBasicActivity.mBMapMan == null) {
			MapBasicActivity.mBMapMan = new BMapManager(getApplication());
			MapBasicActivity.mBMapMan.init(MapBasicActivity.mStrKey,
					new MGeneralListener());
		}
		MapBasicActivity.networkStateError=new MapBasicActivity.NetworkStateInterface() {
			
			@Override
			public void onNetworkStateError() {
				makeText("网络异常，请确认网络正常后重试");
				findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);
			}
		};

		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					lat = location.getLatitude();
					lon = location.getLongitude();
					
					MapBasicActivity.curLat=(int)(lat*1e6);
					MapBasicActivity.curLon=(int)(lon*1e6);
					
//					
//					
//					//测试数据，使没有附近的母婴店
//					lon=104.776602;
//					lat=50.867402;
					
//					//澳门最西
//					lon=113.551626;
//					lat=22.165297;
					

					LogUtils.logv("当前位置 :", "lat:" + lat + "  lon:" + lon);

					// 停止定位服务
					if (MapBasicActivity.mBMapMan != null
							&& mLocationListener != null)
						MapBasicActivity.mBMapMan.getLocationManager()
								.removeUpdates(mLocationListener);
					if (MapBasicActivity.mBMapMan != null)
						MapBasicActivity.mBMapMan.stop();

					// 请求数据
					updateCurData(lat, lon);
					
				}
				else{
					new CheckLocateTask().execute();
				}
				
			}
		};
		// 注册定位事件
		if (MapBasicActivity.mBMapMan != null && mLocationListener != null)
			MapBasicActivity.mBMapMan.getLocationManager()
					.requestLocationUpdates(mLocationListener);
		if (MapBasicActivity.mBMapMan != null)
			MapBasicActivity.mBMapMan.start();
		// 等列表加载完了才允许查看地图
		rightButton.setClickable(false);

	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);// 百度统计
		// MobileProbe.onResume(this);//CNZZ统计

		// // 注册定位事件
		// if(MapBasicActivity.mBMapMan != null&&mLocationListener!=null)
		// MapBasicActivity.mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		// if(MapBasicActivity.mBMapMan != null)
		// MapBasicActivity.mBMapMan.start();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case AppConstants.REQUEST_CODE_UNLOGIN_INTO_CONCERNED_BABYSHOP_PAGE:
				setUnlogin(false);
				BasicApplication.getInstance().setAutoLogin(true);
				// 再次调用之前的点击事件。。
				findViewById(R.id.btn_concerned).performClick();
				break;

			default:
				break;
			}

		}

	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);// 百度统计
		// MobileProbe.onPause(this);//CNZZ统计

		if (MapBasicActivity.mBMapMan != null && mLocationListener != null)
			MapBasicActivity.mBMapMan.getLocationManager().removeUpdates(
					mLocationListener);
		if (MapBasicActivity.mBMapMan != null)
			MapBasicActivity.mBMapMan.stop();
		
		//1.内存测试 start
				free = Runtime.getRuntime().totalMemory();
				LogUtils.loge(TAG , "用掉的内存 - onPause " + (total-free) + "字节");
				LogUtils.loge(TAG , "剩余的内存 - onPause " + free + "字节");
	}

	@Override
	public void doClickLeftBtn() {
		onBackPressed();
	}

	@Override
	public void doClickRightBtn() {
		if (findViewById(R.id.layout_near).getVisibility() == View.VISIBLE
				&& list_shops.getCount() > 0) {

			final int size = list_shops_adap.itemList.size();

			// 用于装size*2个经度或纬度参数
			int[] geoPointParams = new int[size * 2];

			String[] shopName = new String[size];
			

			shopDistances=new int[size];
			
			// 分别将一个经纬度拆成两个参数装依次装入geoPointParams
			for (int index = 0; index < size * 2;) {
				geoPointParams[index] = (int) (list_shops_adap.itemList.get(
						index / 2).getLatitude() * 1e6);
				geoPointParams[index + 1] = (int) (list_shops_adap.itemList
						.get(index / 2).getLongitude() * 1e6);

				shopName[index / 2] = list_shops_adap.itemList.get(index / 2)
						.getName();
				final float distance=Float.valueOf(list_shops_adap.itemList.get(index / 2)
						.getInstance());
				shopDistances[index / 2]=(int)distance;
				
				index += 2;
			}

			final Intent intent = new Intent(this, ShopMapActivity.class);
			intent.putExtra(ShopMapActivity.POINTS_LOC_KEY, geoPointParams)
					.putExtra(ShopMapActivity.NAME_KEY, shopName);
			startActivity(intent);
			
			geoPointParams=null;
			shopName=null;
		}

		if (findViewById(R.id.layout_concerned).getVisibility() == View.VISIBLE
				&& concernedShops != null && concernedShops.size() > 0) {
			// TODO

			if (concernedShops.get(actPager.getCurrentPage())
					.isLocationWasNull()){
				makeText(R.string.no_loc_data_tip);
				return;
			}
			//
			final int size = 1;

			// 用于装size*2个经度或纬度参数
			int[] geoPointParams = new int[size * 2];

			String[] shopName = new String[size];

			geoPointParams[0] = (int) (Double.valueOf(concernedShops.get(
					actPager.getCurrentPage()).getLatitude()) * 1e6);
			geoPointParams[1] = (int) (Double.valueOf(concernedShops.get(
					actPager.getCurrentPage()).getLongitude()) * 1e6);
			shopName[0] = concernedShops.get(actPager.getCurrentPage())
					.getShopName();



			final Intent intent = new Intent(this, ShopMapActivity.class);
			intent.putExtra(ShopMapActivity.POINTS_LOC_KEY, geoPointParams)
					.putExtra(ShopMapActivity.NAME_KEY, shopName)
					.putExtra(ShopMapActivity.TITLE_RES_ID_KEY, R.string.concerned_bbshop);
			startActivity(intent);
			shopName=null;
			geoPointParams=null;
		}
		else if(findViewById(R.id.layout_concerned).getVisibility() == View.VISIBLE
				&& (concernedShops == null||concernedShops.size() ==0)){
			makeText("您还没有关注的母婴店");
			return;
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (MapBasicActivity.mBMapMan != null)
			MapBasicActivity.mBMapMan.stop();
		finish();
	}

	private void getView() {
		scrV_main=(ScrollView)findViewById(R.id.scrV_main);
		
		
		actIdxContainer = (LinearLayout) findViewById(R.id.home_act_idx_container);
		// 初始化 活动图片栏
		actPager = (HorizontalPager) findViewById(R.id.home_act_pager);
		actPager.addOnScrollListener(actItemOnScrollListener);
		
		actPager.setYScrollingListener(new HorizontalPager.YScrollingListener() {
			
			@Override
			public void OnYScrolling(int yDistance) {
				scrV_main.scrollBy(0, yDistance);
			}
		});
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
	 * 刷新推荐活动的小园点索引位置
	 * 
	 * @param selIdx
	 */
	private void refreshActIdx(final int selIdx) {

		final int itemSize = size;

		final Integer currentIdx = (Integer) actIdxContainer.getTag();
		if (currentIdx != null && currentIdx.intValue() == selIdx) {
			// Log.i(LOG_TAG, "same with current act idx.");
			return;
		}

		// Log.i(LOG_TAG, "set new idx: " + selIdx);
		actIdxContainer.setTag(selIdx);

		final Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// added by edwar 2012-09-10, 防止程序正在关闭中的出现空指针
				if (isFinishing()) {
					return;
				}
				for (int i = 0; i < itemSize; i++) {

					final ImageView imgView = (ImageView) actIdxContainer
							.getChildAt(i);
					final Boolean isSel = (Boolean) imgView.getTag();

					// 只更新有变化的小圆点
					if (i == selIdx) {
						if (isSel == null || !isSel.booleanValue()) {
							imgView.setImageResource(R.drawable.point_select2);
							imgView.setTag(Boolean.TRUE);
						}
					} else {
						if (isSel == null || isSel.booleanValue()) {
							imgView.setImageResource(R.drawable.point_unselect2);
							imgView.setTag(Boolean.FALSE);
						}
					}

				}

			}
		});

	}

	/**
	 * 初始化推荐活动的界面元素 及 活动底部的小圆点
	 */
	private void initActPagerItems(List<ConcernedBabyShop> itemList) {

		if (itemList != null && itemList.size() > 0) {
			size = itemList.size();
			for (int index = 0; index < size; index++) {
				final ConcernedBabyShop aShop = itemList.get(index);
				final View layout = View.inflate(BabyShopActivity.this,
						R.layout.concerned_shops, null);
				// 以下字段一定不能为null
				((TextView) layout.findViewById(R.id.tv_title)).setText(aShop
						.getShopName());
				((TextView) layout.findViewById(R.id.tv_pop)).setText(String
						.valueOf(aShop.getSnsecClickRate())+"人");

				
				//** 暂时不显示门店图片，edited in 2012.12.04 version 1.3.6
//				if (aShop.getShopLogo() != null) {
//					final AsyncLoadImg load = new AsyncLoadImg(
//							(layout.findViewById(R.id.imgV_img)),
//							aShop.getShopLogo(), aShop.getCode(),
//							R.color.transparent);
//					load.execute();
//				} else
//					layout.findViewById(R.id.imgV_img).setBackgroundResource(
//							R.color.transparent);

				
				if(aShop.getTerminalChannelCode()!=null&&aShop.getTerminalChannelCode().equals("03"))
					layout.findViewById(R.id.imgV_img_bg).setBackgroundResource(R.drawable.drugstore_icon);
				else if(aShop.getTerminalChannelCode()!=null&&aShop.getTerminalChannelCode().equals("02"))
					layout.findViewById(R.id.imgV_img_bg).setBackgroundResource(R.drawable.supermarket_icon);
				else
					layout.findViewById(R.id.imgV_img_bg).setBackgroundResource(R.drawable.babyshop_icon);
				
				
				if(aShop.getAddress()!=null&&!aShop.getAddress().equals("")){
					int shengIndex=aShop.getAddress().indexOf("省");
					int shiIndex=aShop.getAddress().indexOf("市");
					String addressTmp=null;
					
					if(shiIndex!=-1)
						addressTmp=aShop.getAddress().substring(shiIndex+1, aShop.getAddress().length());
					else if(shengIndex!=-1)
						addressTmp=aShop.getAddress().substring(shengIndex+1, aShop.getAddress().length());
					else
						addressTmp=aShop.getAddress();
					((TextView) layout.findViewById(R.id.tv_location))
							.setText(addressTmp);
					((TextView) layout.findViewById(R.id.tv_address)).setText(aShop.getAddress());
				}
				else{
					((TextView) layout.findViewById(R.id.tv_location)).setText("");
					((TextView) layout.findViewById(R.id.tv_address)).setText("");
				}
				
				if (aShop.getPhone() != null && !aShop.getPhone().equals(""))
					((TextView) layout.findViewById(R.id.tv_phonenum))
							.setText(aShop.getPhone());
				else if (aShop.getBossPhone() != null
						&& !aShop.getBossPhone().equals(""))
					((TextView) layout.findViewById(R.id.tv_phonenum))
							.setText(aShop.getBossPhone());
				else {
//					//记得修改  。测试，加上phonenum
//					((TextView) layout.findViewById(R.id.tv_phonenum))
//						.setText("020-84141681");	
					
					((TextView) layout.findViewById(R.id.tv_phonenum))
							.setText("");	
					layout.findViewById(R.id.imgV_call).setVisibility(View.INVISIBLE);
				}

				if (aShop.getInstance() != null
						&& !aShop.getInstance().equals("")) {
					float disFloat = Float.valueOf(aShop.getInstance());
					String distanceStr = null;
					if (disFloat >= 1000&&disFloat <= 15000) {
						distanceStr = new DecimalFormat("#.#")
								.format(disFloat / 1000) + "km";
					} else if (disFloat > 15000) {
//						distanceStr = ">15km";
						distanceStr = "";
					} else {
						distanceStr = new DecimalFormat("#").format(disFloat)
								+ "m";
					}
					((TextView) layout.findViewById(R.id.tv_distance))
							.setText(distanceStr);
				}

				if (aShop.getSideFormDetailList() != null
						&& aShop.getSideFormDetailList().size() > 0) {
					int num=1;
					//大屏幕手机，显示两条订单
					if(((float)heightPixels)/density>=640.0f
							&&aShop.getSideFormDetailList().size() > 1){
						num=2;
					}

					for(int index2=0;index2<num;index2++){
						//一条以上的订单使用order_list_item.xml
						final View aOrder=View.inflate(getApplicationContext(),
								num>1?R.layout.order_list_item:R.layout.concerned_order_item, null); 
						
						
						((TextView) aOrder.findViewById(R.id.tv_productitle))
								.setText(aShop.getSideFormDetailList().get(index2)
										.getProductName());

						if (aShop.getSideFormDetailList().get(index2).getControlType()
								.equals("exchange")) {
							((ImageView) aOrder.findViewById(R.id.imgV_buyOrExch))
									.setBackgroundResource(R.drawable.do_exch);
							((TextView) aOrder.findViewById(R.id.tv_point))
							.setText("-"+aShop.getSideFormDetailList().get(index2)
									.getPoints());
							((TextView) aOrder.findViewById(R.id.tv_point)).setTextColor(Color.parseColor("#8cc63f"));
						} else {
							((ImageView) aOrder.findViewById(R.id.imgV_buyOrExch))
									.setBackgroundResource(R.drawable.do_buy);
							((TextView) aOrder.findViewById(R.id.tv_point))
							.setText("+"+aShop.getSideFormDetailList().get(index2)
									.getPoints());
						}
						((TextView) aOrder.findViewById(R.id.tv_date))
								.setText(aShop.getSideFormDetailList().get(index2)
										.getCreatedTimestamp());

						final AsyncLoadImg loader = new AsyncLoadImg(
								aOrder.findViewById(R.id.imgV_product),
								aShop.getSideFormDetailList().get(index2).getImgUrl(),
								aShop.getSideFormDetailList().get(index2).getProductId(),
								R.drawable.product_defaul_img);
						loader.execute();
						
						((LinearLayout)layout.findViewById(R.id.layout_orders)).addView(aOrder);
						
					}
				}

				if (aShop.getIsVip() == false) {
					layout.findViewById(R.id.imgV_withvip)
							.setBackgroundResource(R.drawable.without_vip);
					layout.findViewById(R.id.imgV_withvip).setClickable(false);
				} else {
					layout.findViewById(R.id.imgV_withvip)
							.setBackgroundResource(R.drawable.with_vip);
					layout.findViewById(R.id.imgV_withvip).setClickable(true);
				}
				if (aShop.getIsCard() == false) {
					layout.findViewById(R.id.imgV_withcard)
							.setBackgroundResource(R.drawable.without_card);
					layout.findViewById(R.id.imgV_withcard)
							.setClickable(false);
				} else {
					layout.findViewById(R.id.imgV_withcard)
							.setBackgroundResource(R.drawable.with_card);
					layout.findViewById(R.id.imgV_withcard).setClickable(true);
				}
				if (aShop.getIsExchange() == false) {
					layout.findViewById(R.id.imgV_withexch)
							.setBackgroundResource(R.drawable.without_exch);
					layout.findViewById(R.id.imgV_withexch)
							.setClickable(false);
				} else {
					layout.findViewById(R.id.imgV_withexch)
							.setBackgroundResource(R.drawable.with_exch);
					layout.findViewById(R.id.imgV_withexch).setClickable(true);
				}
				
				layout.findViewById(R.id.imgV_call).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								onClickCall(((TextView) layout
										.findViewById(R.id.tv_phonenum))
										.getText());
							}
						});
				//点号码也弹拨号
				((TextView) layout.findViewById(R.id.tv_phonenum)).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								onClickCall(((TextView) layout
										.findViewById(R.id.tv_phonenum))
										.getText());
							}
						});
				
				//查看详情
				layout.findViewById(R.id.tv_detail).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO
								ShopDetailActivity.shopName = aShop
										.getShopName();
								ShopDetailActivity.address = aShop.getAddress();

								ShopDetailActivity.hasOrders = true;

								ShopDetailActivity.phone = aShop.getPhone();
								if (aShop.getPhone() == null
										|| aShop.getPhone().equals("")) {
									ShopDetailActivity.phone = aShop
											.getBossPhone();
								}
								ShopDetailActivity.shopCode = aShop.getCode();
								if (aShop.getLatitude() != null
										&& aShop.getLongitude() != null
										&& !aShop.getLatitude().equals("")) {
									ShopDetailActivity.latitude = (int) (Double
											.valueOf(aShop.getLatitude()) * 1e6);
									ShopDetailActivity.longitude = (int) (Double
											.valueOf(aShop.getLongitude()) * 1e6);
								}
								//若门店无经纬度，则初始化
								else{
									ShopDetailActivity.latitude = 720;
									ShopDetailActivity.longitude =720;
								}

								startActivity(new Intent(BabyShopActivity.this,
										ShopDetailActivity.class));

							}
						});
				//查看详情时，剪头的焦点效果
				layout.findViewById(R.id.tv_detail).setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction()==MotionEvent.ACTION_DOWN){
							((ImageView)layout.findViewById(R.id.imgV_arrow)).setPressed(true);
						}
						else if(event.getAction()==MotionEvent.ACTION_UP){
							((ImageView)layout.findViewById(R.id.imgV_arrow)).setPressed(false);
						}
						else if(event.getAction()==MotionEvent.ACTION_CANCEL){
							((ImageView)layout.findViewById(R.id.imgV_arrow)).setPressed(false);
						}
						return false;
					}
				});
				//点击订单也进入详情
				layout.findViewById(R.id.order_item).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								layout.findViewById(R.id.tv_detail).performClick();
							}});
				
				
				actPager.addView(layout);
			}

			// 初始化活动的底部小圆点
			initActIdxContainer(itemList.size());
		} else {
			size = 5; // demo
			for (int i = 0; i < size; i++) {
				View layout = View.inflate(BabyShopActivity.this,
						R.layout.concerned_shops, null);
				layout.findViewById(R.id.imgV_call).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								onClickCall("020-88888888");
							}
						});
				actPager.addView(layout);
			}

			// 初始化活动的底部小圆点
			initActIdxContainer(size);
		}
	}

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
				imgView.setImageResource(R.drawable.point_select2);
			} else {
				imgView.setImageResource(R.drawable.point_unselect2);
			}
			actIdxContainer.addView(imgView);
		}

	}

	public void onClickPrivilege(View viewCliked) {

		final TextView tips = new TextView(this);
		tips.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		tips.setTextColor(Color.parseColor("#4d4d4d"));
		tips.setFocusable(true);
		tips.setClickable(true);

		// View tips=View.inflate(this, R.layout.shop_item_poptip, null);
		switch (viewCliked.getId()) {
		case R.id.imgV_withvip:
			// ((TextView)tips.findViewById(R.id.tv_tip)).setText(R.string.biostime_vip_shop);
			tips.setText(R.string.biostime_vip_shop);
			break;
		case R.id.imgV_withcard:
			tips.setText(R.string.support_ele_card);
			// ((TextView)tips.findViewById(R.id.tv_tip)).setText(R.string.support_ele_card);
			break;
		case R.id.imgV_withexch:
			tips.setText(R.string.support_exchange);
			// ((TextView)tips.findViewById(R.id.tv_tip)).setText(R.string.support_exchange);
			break;
		}

		final PopupWindow mPopupWindow = new PopupWindow(tips,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.shop_poptip_bg));
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(false);

		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.popupAnimation);

		tips.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});

		int[] location = { 0, 0 };// mParent的位置
		viewCliked.getLocationOnScreen(location);
		mPopupWindow
				.showAtLocation(viewCliked, Gravity.NO_GRAVITY, location[0]
						- viewCliked.getWidth() - 1,
						location[1] - tips_tmp.getHeight());
	}

	private void setListeners() {
		findViewById(R.id.btn_near).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				v.setBackgroundResource(R.drawable.left_tab_on);
//				findViewById(R.id.btn_concerned).setBackgroundResource(
//						R.drawable.right_tab);
				((TextView)findViewById(R.id.tv_tab_left)).setTextColor(Color.parseColor("#4d4d4d"));
				((TextView)findViewById(R.id.tv_tab_left))
					.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(R.drawable.shop_tab_near_on), null,null, null);
				
				((TextView)findViewById(R.id.tv_tab_right)).setTextColor(Color.parseColor("#9f9f9f"));
				((TextView)findViewById(R.id.tv_tab_right))
					.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(R.drawable.shop_tab_concerned), null,null, null);
				findViewById(R.id.imgV_shoptab_line).setBackgroundResource(R.drawable.shop_tab_left);
				
				findViewById(R.id.layout_near).setVisibility(View.VISIBLE);
				findViewById(R.id.layout_near).startAnimation(AnimationUtils
						.loadAnimation(BabyShopActivity.this,R.anim.shop_in));
				
//				new AnimationUtils().loadAnimation(context, id)
				
				findViewById(R.id.layout_concerned).setVisibility(View.GONE);
//				findViewById(R.id.layout_concerned).startAnimation(AnimationUtils
//						.loadAnimation(BabyShopActivity.this,R.anim.popup_out));
				findViewById(R.id.home_act_idx_container).setVisibility(
						View.GONE);

				if (list_shops.getCount() <= 0 && lat != 720.0d
						&& lon != 720.0d && !hasLoadNearShops) {
					hasLoadNearShops = true;
					if(nearReq==null){
						nearReq = new NearShopReq();
						nearReq.setPageNo(1);
						nearReq.setPageSize(25);
						nearReq.setLatitude(String.valueOf(lat));
						nearReq.setLongitude(String.valueOf(lon));
					}
					new NearTask(getApplicationContext()).execute(nearReq);
				}

			}
		});
		findViewById(R.id.btn_concerned).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (isUnlogin()) {
							goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_CONCERNED_BABYSHOP_PAGE);
							return;
						}

//						v.setBackgroundResource(R.drawable.right_tab_on);
//						findViewById(R.id.btn_near).setBackgroundResource(
//								R.drawable.left_tab);
						
						((TextView)findViewById(R.id.tv_tab_left)).setTextColor(Color.parseColor("#9f9f9f"));
						((TextView)findViewById(R.id.tv_tab_left))
							.setCompoundDrawablesWithIntrinsicBounds(
									getResources().getDrawable(R.drawable.shop_tab_near), null,null, null);
						
						((TextView)findViewById(R.id.tv_tab_right)).setTextColor(Color.parseColor("#4d4d4d"));
						((TextView)findViewById(R.id.tv_tab_right))
							.setCompoundDrawablesWithIntrinsicBounds(
									getResources().getDrawable(R.drawable.shop_tab_concerned_on), null,null, null);
						findViewById(R.id.imgV_shoptab_line).setBackgroundResource(R.drawable.shop_tab_right);
						
						findViewById(R.id.layout_near).setVisibility(View.GONE);
						findViewById(R.id.layout_concerned).setVisibility(
								View.VISIBLE);
						findViewById(R.id.home_act_idx_container).setVisibility(
								View.VISIBLE);
						findViewById(R.id.layout_concerned).startAnimation(AnimationUtils
								.loadAnimation(BabyShopActivity.this,R.anim.shop_in));

						if (actPager.getChildCount() <= 0 && lat != 720.0d
								&& lon != 720.0d && !hasLoadConcernedShops) {
							hasLoadConcernedShops = true;
							final ConcernedShopReq req = new ConcernedShopReq();
							req.setLatitude(String.valueOf(lat));
							req.setLongitude(String.valueOf(lon));

							ConcernedTask task = new ConcernedTask(
									getApplicationContext());
							task.execute(req);

						}
					}
				});
		
		
		//加载更多附近母婴店
		list_footer.findViewById(R.id.tv_tips).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!((TextView)v).isClickable()){
					return;
				}
				list_footer.findViewById(R.id.layout_loading).setVisibility(View.VISIBLE);
				v.setVisibility(View.INVISIBLE);
				
				nearReq.pageNoPlusPlus();
				new NearTask(getApplicationContext()).execute(nearReq);

			}
		});
	}

	// 弹出拨号menu
	public void onClickCall(final CharSequence charSequence) {
		if (charSequence == null || charSequence.length() <= 0) {
			return;
		}
		final View view = View.inflate(getApplicationContext(),
				R.layout.call_menu, null);

		if (call_menu == null)
			call_menu = new AlertDialog.Builder(this).create();

		call_menu.show();
		call_menu.getWindow().setWindowAnimations(R.style.PopupAnimation);

		final Window window = call_menu.getWindow();
		final WindowManager.LayoutParams wl = window.getAttributes();
		// 根据x，y坐标设置窗口需要显示的位置
		// wl.x += x; //x小于0左移，大于0右移
		// wl.y +=heightPixels/2-280; //y小于0上移，大于0下移
		wl.y = 0;
		wl.y += heightPixels / 2 - call_menu_tmp.getHeight() / 2;
		// 对话框宽度
		wl.width = widthPixels;
		window.setAttributes(wl);
		window.setContentView(view);

		// final PopupWindow mPopupWindow = new PopupWindow(call_menu2,
		// LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT, false);
		//
		// mPopupWindow.setOutsideTouchable(true);
		// mPopupWindow.update();
		// mPopupWindow.setTouchable(true);
		// /*设置点击menu以外其他地方以及返回键退出*/
		// mPopupWindow.setFocusable(true);
		// mPopupWindow.setAnimationStyle(R.style.popupAnimation);

		((TextView) view.findViewById(R.id.tv_number)).setText(charSequence);
		view.findViewById(R.id.imgV_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						call_menu.dismiss();
					}
				});
		view.findViewById(R.id.imgV_call).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// call directly
						final Intent intent = new Intent(Intent.ACTION_CALL,
								Uri.parse("tel:"
										+ charSequence.toString().replace("-",
												"")));
						startActivity(intent);
						call_menu.dismiss();
					}
				});
	}

	private void updateCurData(double lat, double lon) {
		// 当前附近母婴店可见
		if (findViewById(R.id.layout_near).getVisibility() == View.VISIBLE
				&& list_shops.getCount() <= 0&&!hasLoadNearShops) {
			hasLoadNearShops = true;
			if(nearReq==null){
				nearReq = new NearShopReq();
				nearReq.setPageNo(1);
				nearReq.setPageSize(25);
				nearReq.setLatitude(String.valueOf(lat));
				nearReq.setLongitude(String.valueOf(lon));
			}
			new NearTask(getApplicationContext()).execute(nearReq);
		}
		else if (actPager.getChildCount() <= 0 && lat != 720.0d
				&& lon != 720.0d && !hasLoadConcernedShops) {
			hasLoadConcernedShops = true;
			final ConcernedShopReq req = new ConcernedShopReq();
			req.setLatitude(String.valueOf(lat));
			req.setLongitude(String.valueOf(lon));
			new ConcernedTask(getApplicationContext()).execute(req);
		}
	}

	class ConcernedTask extends AsyncReqTask {

		public ConcernedTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return BabyShopProvider.getInstance(getApplicationContext())
					.getConcernedShop((ConcernedShopReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {
			// activity已退出，防止空指针错误
			if (isFinishing()) {
				return;
			}
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				// TODO 改成友好提示
				makeText(response.getDesc());
				hasLoadConcernedShops = false;
				findViewById(R.id.proBar_2).setVisibility(View.INVISIBLE);
				return;
			}
			final ConcernedShopRes res = (ConcernedShopRes) response;
			if (res.getList() == null || res.getList().size() <= 0) {
				// TODO 改成友好提示
				makeText("您还没有关注的母婴店");
				findViewById(R.id.proBar_2).setVisibility(View.INVISIBLE);
				return;
			}
			findViewById(R.id.proBar_2).setVisibility(View.INVISIBLE);

			concernedShops = res.getList();
			
			//设置pager最大高度
			findViewById(R.id.home_act_pager).getLayoutParams().height=heightPixels*3/4;
			initActPagerItems(res.getList());

		}

	}

	class NearTask extends AsyncReqTask {

		public NearTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return BabyShopProvider.getInstance(getApplicationContext())
					.getNearShop((NearShopReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {
			// activity已退出，防止空指针错误
			if (isFinishing()) {
				return;
			}
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				if(list_shops_adap == null){
					hasLoadNearShops=false;
					findViewById(R.id.progressBar1).setVisibility(View.GONE);
				}
				else{
					list_footer.findViewById(R.id.layout_loading).setVisibility(View.INVISIBLE);
					list_footer.findViewById(R.id.tv_tips).setVisibility(View.VISIBLE);
					((TextView)list_footer.findViewById(R.id.tv_tips)).setText(R.string.load_near_failed);
				}
				nearReq.pageNoDecrease();
				makeText(response.getDesc());
				return;
			}
			NearShopRes res = (NearShopRes) response;

			//第一次加载列表
			if (list_shops_adap == null) {
				if (res.getList() == null || res.getList().size() <= 0) {
					makeText("附近没有发现合生元母婴店");
					findViewById(R.id.progressBar1).setVisibility(View.GONE);
					return;
				}
				
				list_shops_adap = new NearShopsListAdapter(res.getList());

				//动态设置listview高度
				int[] listPosition={-1,-1};
				list_shops.getLocationOnScreen(listPosition);
				final LayoutParams tmpPara=list_shops.getLayoutParams();
				tmpPara.height=heightPixels-listPosition[1];
				list_shops.setLayoutParams(tmpPara);
				
//				list_shops.addHeaderView(View.inflate(getApplicationContext(), R.layout.shop_list_head, null));
				list_shops.setAdapter(list_shops_adap);
				list_shops_adap.notifyDataSetChanged();
				findViewById(R.id.progressBar1).setVisibility(View.GONE);
				rightButton.setClickable(true);
				
				list_footer.setVisibility(View.VISIBLE);
				if(res.getAllRowsNo()<=list_shops_adap.getCount()){
					((TextView)list_footer.findViewById(R.id.tv_tips)).setText(R.string.load_near_done);
					list_footer.findViewById(R.id.tv_tips).setClickable(false);
				}
			}
			else{
				//TODO
				list_shops_adap.itemList.addAll(res.getList());
				list_shops_adap.notifyDataSetChanged();
				
				
				list_footer.findViewById(R.id.layout_loading).setVisibility(View.INVISIBLE);
				list_footer.findViewById(R.id.tv_tips).setVisibility(View.VISIBLE);
				
				if(res.getAllRowsNo()<=list_shops_adap.getCount()){
					((TextView)list_footer.findViewById(R.id.tv_tips)).setText(R.string.load_near_done);
					list_footer.findViewById(R.id.tv_tips).setClickable(false);
				}
				else
					((TextView)list_footer.findViewById(R.id.tv_tips)).setText(R.string.load_near_more);
			}

		}

	}

	public class NearShopsListAdapter extends BaseAdapter {

		// 从服务器获取的列表。。
		public List<NearBabyShop> itemList;

		// 传值从此口进入
		public NearShopsListAdapter(List<NearBabyShop> list) {
			if (list != null)
				itemList = list;
			else
				itemList = new ArrayList<NearBabyShop>();
		}

		@Override
		public int getCount() {
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(BabyShopActivity.this,
						R.layout.list_shop_item3, null);
			}

			/**
			 * 附近门店一定不为空的字段
			 * name,shortName,latitude,langitude,instance,snsecClickRate
			 */
			((TextView) convertView.findViewById(R.id.tv_title))
					.setText(itemList.get(position).getName());
			// ((TextView)convertView.findViewById(R.id.tv_location)).setText(itemList.get(position).getAddress());
			((TextView) convertView.findViewById(R.id.tv_pop)).setText(itemList
					.get(position).getSnsecClickRate()+"人");

			//** 暂时不显示门店图片，edited in 2012.12.04 version 1.3.6
//			convertView.findViewById(R.id.imgV_img).setTag(
//					itemList.get(position).getTerminalCode());
//			convertView.findViewById(R.id.imgV_img).setBackgroundResource(R.color.transparent);

			if(itemList.get(position).getTerminalChannelCode()!=null){
				if(itemList.get(position).getTerminalChannelCode().equals("03"))
					convertView.findViewById(R.id.imgV_img_bg).setBackgroundResource(R.drawable.drugstore_icon);
				else if(itemList.get(position).getTerminalChannelCode().equals("02"))
					convertView.findViewById(R.id.imgV_img_bg).setBackgroundResource(R.drawable.supermarket_icon);
				else
					convertView.findViewById(R.id.imgV_img_bg).setBackgroundResource(R.drawable.babyshop_icon);
			}
			
			float disFloat = Float
					.valueOf(itemList.get(position).getInstance());
			String distanceStr = null;
			if (disFloat >= 1000) {
				distanceStr = new DecimalFormat("#.#").format(disFloat / 1000)
						+ "km";
			} else {
				distanceStr = new DecimalFormat("#").format(disFloat) + "m";
			}
			//
			//
			// String disStr=itemList.get(position).getInstance();
			// //去掉小数点
			// int index=disStr.indexOf('.');
			// if(index!=-1){
			// disStr=disStr.substring(0, index);
			// }
			// int distance;
			// //如果大于1000(m)
			// if(disStr.length()>3){
			// distance=Integer.valueOf(disStr).intValue()/1000;
			// }

			((TextView) convertView.findViewById(R.id.tv_distance))
					.setText(distanceStr);
			

			if(itemList.get(position).getAddress()!=null&&!itemList.get(position).getAddress().equals("")){
				int shengIndex=itemList.get(position).getAddress().indexOf("省");
				int shiIndex=itemList.get(position).getAddress().indexOf("市");
				String addressTmp=null;
				
				if(shiIndex!=-1)
					addressTmp=itemList.get(position).getAddress().substring(shiIndex+1, itemList.get(position).getAddress().length());
				else if(shengIndex!=-1)
					addressTmp=itemList.get(position).getAddress().substring(shengIndex+1, itemList.get(position).getAddress().length());
				else
					addressTmp=itemList.get(position).getAddress();
				((TextView) convertView.findViewById(R.id.tv_location))
					.setText(addressTmp);
			}
			else
				((TextView) convertView.findViewById(R.id.tv_location))
				.setText("");
			
			if (itemList.get(position).getIsVip() == false) {
				convertView.findViewById(R.id.imgV_withvip)
						.setBackgroundResource(R.drawable.without_vip);
				convertView.findViewById(R.id.imgV_withvip).setClickable(false);
			} else {
				convertView.findViewById(R.id.imgV_withvip)
						.setBackgroundResource(R.drawable.with_vip);
				convertView.findViewById(R.id.imgV_withvip).setClickable(true);
			}
			if (itemList.get(position).getIsCard() == false) {
				convertView.findViewById(R.id.imgV_withcard)
						.setBackgroundResource(R.drawable.without_card);
				convertView.findViewById(R.id.imgV_withcard)
						.setClickable(false);
			} else {
				convertView.findViewById(R.id.imgV_withcard)
						.setBackgroundResource(R.drawable.with_card);
				convertView.findViewById(R.id.imgV_withcard).setClickable(true);
			}
			if (itemList.get(position).getIsExchange() == false) {
				convertView.findViewById(R.id.imgV_withexch)
						.setBackgroundResource(R.drawable.without_exch);
				convertView.findViewById(R.id.imgV_withexch)
						.setClickable(false);
			} else {
				convertView.findViewById(R.id.imgV_withexch)
						.setBackgroundResource(R.drawable.with_exch);
				convertView.findViewById(R.id.imgV_withexch).setClickable(true);
			}

			// double lat=itemList.get(position).getLatitude();
			// double lon=itemList.get(position).getLongitude();
			// LogUtils.logv("test:"+itemList.get(position).getAddress(),
			// "lat:"+lat+"  lon:"+lon);

			convertView.findViewById(R.id.tv_detail).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							
							initNearShopDetailActivity(position);
							startActivity(new Intent(BabyShopActivity.this,
									ShopDetailActivity.class));
						}
					});

			//** 暂时不显示门店图片，edited in 2012.12.04 version 1.3.6
//			if (itemList.get(position).getShopLogo() != null) {
//				final AsyncLoadImg load = new AsyncLoadImg(
//						convertView.findViewById(R.id.imgV_img), itemList.get(
//								position).getShopLogo(), itemList.get(position)
//								.getTerminalCode(), R.color.transparent);
//				load.execute();
//				
////				final AsyncLoadImg load = new AsyncLoadImg(
////						convertView.findViewById(R.id.imgV_img), 
////						"http://img2.mama100.com/site/mobile/shop/f98ea66734b3c3f20134c05d5fea0018_128_128.jpg",
////						String.valueOf(position), R.color.transparent);
////				load.execute();
//			}
//			else{
//				convertView.findViewById(R.id.imgV_img).setBackgroundResource(R.color.transparent);
//			}
//			LogUtils.logd("position", "position:"+position);
			
			return convertView;
		}
	}

	/**
	 * 根据BabyShopActivity的附近母婴店数据初始化ShopDetailActivity
	 * 
	 * @param position
	 *            此母婴店在列表中的位置
	 */
	public static void initNearShopDetailActivity(int position) {
		ShopDetailActivity.shopName = list_shops_adap.itemList.get(position)
				.getName();
		ShopDetailActivity.address = list_shops_adap.itemList.get(position)
				.getAddress();

		ShopDetailActivity.hasOrders = false;
		ShopDetailActivity.phone = list_shops_adap.itemList.get(position)
				.getPhone();
		if (ShopDetailActivity.phone == null
				|| ShopDetailActivity.phone.equals("")) {
			ShopDetailActivity.phone = list_shops_adap.itemList.get(position)
					.getMobile();
		}
		ShopDetailActivity.shopCode = list_shops_adap.itemList.get(position)
				.getTerminalCode();
		ShopDetailActivity.latitude = (int) (list_shops_adap.itemList.get(
				position).getLatitude() * 1e6);
		ShopDetailActivity.longitude = (int) (list_shops_adap.itemList.get(
				position).getLongitude() * 1e6);

	}

	@Override
	public void onDestroy() {
		
		LogUtils.loge("BabyShopActivity", "onDestroy");
		
		if(concernedShops !=null){
			if (!concernedShops.isEmpty()) {
				for (ConcernedBabyShop item : concernedShops) {
					item.clearMemory();
					item = null;
				}
			}
		 concernedShops.clear();
		 concernedShops = null;
		}
		
		
		if (list_shops_adap != null) {
			list_shops_adap.itemList.clear();
			list_shops_adap.itemList = null;
			list_shops_adap = null;
		}
		
		//释放view
		list_shops.clearAnimation();
//		list_shops.removeAllViews(); //AdatperView不支持
		list_shops.removeAllViewsInLayout();
		list_shops = null;
		
		scrV_main.removeAllViews();
		scrV_main.removeAllViewsInLayout();
		scrV_main = null;
		
		clearActPictureBar();
		
		super.onDestroy();
		
		//1.内存测试 start
		free = Runtime.getRuntime().totalMemory();
		LogUtils.loge(TAG , "剩余的内存 - onDestroy " + free + "字节");
		LogUtils.loge(TAG , "用掉的内存 - onDestroy " + (total-free) + "字节");
	}
	
	private void clearActPictureBar() {
		actIdxContainer.removeAllViews();
		int count = actPager.getChildCount();
		for (int i = 0; i < count; i++) {
			RelativeLayout relativelayout = (RelativeLayout) actPager
					.getChildAt(i);
			relativelayout.removeAllViews();
			relativelayout.removeAllViewsInLayout();
			relativelayout = null;
		}
		actPager.removeAllViewsInLayout();
		actPager.removeAllViews();
		actPager = null;
	}
	
	/**
	 * 检查8秒内是否能获得当前位置，如果不能则提示稍候重试
	 *
	 */
	class CheckLocateTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(lat>361&&lon>361){
				//TODO 此处改成友好提示
				makeText(R.string.get_loc_error_tip);
				findViewById(R.id.progressBar1).setVisibility(View.GONE);
				findViewById(R.id.proBar_2).setVisibility(View.GONE);
			}
		}
		
	}

} 
