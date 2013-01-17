package com.mama100.android.member.activities.babyshop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKMapViewListener;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.asynctask.AsyncLoadImg;
import com.mama100.android.member.bean.babyshop.UserOrder;
import com.mama100.android.member.businesslayer.BabyShopProvider;
import com.mama100.android.member.domain.babyshop.ShopDetailedReq;
import com.mama100.android.member.domain.babyshop.ShopDetailedRes;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.GeoUtil;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.widget.MapViewEx;


/**
 * 使用ShopDetailActivity前，必须初始化以下参数：
 * 	public static OrderList orders;
	public static String shopName;
	public static String address;
	public static String phone;
	public static String shopCode;
	
	退出Activity之前，onDestroy会将以上参数置null
 */
public class ShopDetailActivity
extends MapBasicActivity {

	/**
	 * 有否有订单
	 */
	public static boolean hasOrders=false;
	public static String shopName;
	public static String address;
	/**
	 * 电话可为空
	 */
	public static String phone;
	public static String shopCode;
	/**
	 * 门店纬度,单位微度
	 */
	public static int latitude=720;
	/**
	 * 门店经度，单位微度
	 */
	public static int longitude=720;
	
	protected View mPopView = null;	// 点击mark时弹出的气泡View
	protected MapViewEx mMapView = null;
	protected OverItemT overitem = null;
	View call_menu_tmp; //作用类似上述tips_tmp，另外也用于显示。
	private TextView tv_shop_inf;
	private TextView tv_shop_inf_tip;
	private String TAG = this.getClass().getSimpleName();
	
	public static int iZoom=18;
	
	protected void onCreate(Bundle savedInstanceState) {
		//1.内存测试 start
		long total = Runtime.getRuntime().totalMemory();
		LogUtils.loge(TAG , "获得的内存 - " + (total) + "字节");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.bbshop_detailed);
        
		call_menu_tmp=View.inflate(getApplicationContext(), R.layout.call_menu, null);
		addContentView(call_menu_tmp,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		call_menu_tmp.setVisibility(View.INVISIBLE);
		
		buildViewsInTopBar();
		setViews();

		mBMapMan.start();
        //初始化地图Activity
        super.initMapActivity(mBMapMan);
        mMapView = (MapViewEx)findViewById(R.id.mapV_map);
        //设置在缩放动画过程中也显示overlay,默认为不绘制
        mMapView.setDrawOverlayWhenZooming(true);
        mMapView.setTraffic(false);

        iZoom=15;
		mMapView.getController().setZoom(15);
		
		//有订单情况下的母婴店详情
		if(hasOrders){
			LayoutParams params=mMapView.getLayoutParams();
			params.height=heightPixels*6/16+4;
//			mMapView.setLayoutParams(new MapViewEx.LayoutParams(LayoutParams.FILL_PARENT
//					, heightPixels*2/3,null,0));
			mMapView.setLayoutParams(params);
		}
		//
		else{
			LayoutParams params=mMapView.getLayoutParams();
			params.height=heightPixels*9/16+4;
//			mMapView.setLayoutParams(new MapViewEx.LayoutParams(LayoutParams.FILL_PARENT
//					, heightPixels*2/3,null,0));
			mMapView.setLayoutParams(params);
		}
		findViewById(R.id.tv_orders).setVisibility(View.GONE);
		findViewById(R.id.layout_orders).setVisibility(View.GONE);
		
        if(latitude!=720&&longitude!=720){
        	mMapView.getController().animateTo(new GeoPoint(latitude, longitude));
        	
			// 创建点击mark时的弹出泡泡
			mPopView = super.getLayoutInflater().inflate(R.layout.map_popview2,
					null);
			mMapView.addView(mPopView, new MapView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.TOP_LEFT));
			mPopView.setVisibility(View.GONE);

			// 添加ItemizedOverlay
			Drawable marker = getResources()
					.getDrawable(R.drawable.loc_mapmark); // 得到需要标在地图上的资源
			marker.setBounds(0, 0, marker.getIntrinsicWidth(),
					marker.getIntrinsicHeight()); // 为maker定义位置和边界

			List<GeoPoint> points= new ArrayList<GeoPoint>();
			points.add(new GeoPoint(latitude, longitude));
			overitem = new OverItemT(marker,points,
					new OverItemT.OnTapListener() {
						@Override
						public void onTapMarkView(GeoPoint point, int index) {
							if (shopName != null) {
								// 更新气泡位置,并使之显示
								mMapView.updateViewLayout(
										mPopView,
										new MapView.LayoutParams(
												LayoutParams.WRAP_CONTENT,
												LayoutParams.WRAP_CONTENT,
												point,
												MapView.LayoutParams.BOTTOM_CENTER));
								((TextView) mPopView
										.findViewById(R.id.tv_poptip))
										.setText(shopName);
								// 此控件的主要作用是让mPopView居左
								((TextView) mPopView.findViewById(R.id.tv_hide))
										.setText(shopName);
								mPopView.setVisibility(View.VISIBLE);
							}
						}

						@Override
						public void onTapLayer(GeoPoint point) {
							// 消去弹出的气泡
							mPopView.setVisibility(View.GONE);
						}
					});
			mMapView.getOverlays().add(overitem); // 添加ItemizedOverlay实例到mMapView
        }
        else{
			mMapView.getController().animateTo(new GeoPoint(curLat,curLon));
        }
        
		mMapView.regMapViewListener(mBMapMan, new MKMapViewListener(){
			public void onMapMoveFinish() {
				iZoom=mMapView.getZoomLevel();
			}
		});
		
		// 添加定位图层
        mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
		
		//加载订单及简介
		ShopDetailedReq req=new ShopDetailedReq();
		req.setId(shopCode);
		new DetailTask(getApplicationContext()).execute(req);
		
		//1.内存测试 start
		long free = Runtime.getRuntime().totalMemory();
		LogUtils.loge(TAG, "用掉的内存 - " + (total-free) + "字节");
	}
	
	
	/**
	 * 设置顶部
	 */
	private void buildViewsInTopBar() {

		findViewById(R.id.mkt_top_left_btn).setVisibility(View.INVISIBLE);
		final TextView tv_title = (TextView) this.findViewById(R.id.menutitle);
		final ImageButton leftBtn = (ImageButton) this.findViewById(R.id.mkt_top_left_btn);
		leftBtn.setBackgroundResource(R.drawable.selector_back);
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if(shopName!=null)
			tv_title.setText(shopName);
	}
	
	
	
	private void setViews(){
		tv_shop_inf=(TextView)findViewById(R.id.tv_shop_inf);
		tv_shop_inf_tip=(TextView)findViewById(R.id.tv_shop_inf_tip);
		if(shopName!=null)
			((TextView)findViewById(R.id.tv_title)).setText(shopName);
		else
			findViewById(R.id.tv_title).setVisibility(View.GONE);
		
		
		((TextView)findViewById(R.id.tv_address)).setText(
				address!=null?address:"");
		if(address!=null&&address.length()>32){
			((TextView)findViewById(R.id.tv_address)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		}
		
		((TextView)findViewById(R.id.tv_phonenum)).setText(
				phone!=null?phone:"");
		if(phone!=null&&phone.length()>0){
			((TextView)findViewById(R.id.tv_phonenum)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					findViewById(R.id.imgV_call).performClick();
				}
			});
		}
		
		
		if(phone==null||phone.equals("")){
			findViewById(R.id.imgV_call).setVisibility(View.INVISIBLE);
		}
		else{
			findViewById(R.id.imgV_call).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onClickCall(((TextView)findViewById(R.id.tv_phonenum)).getText());
				}
			});
		}

	}
	
	@Override
	public void onBackPressed() {
		((ImageButton)findViewById(R.id.mkt_top_left_btn))
			.performClick();
	}
	@Override
	protected void onLocationChanged(Location location) {
		if((latitude==720||longitude==720)
				&&location!=null){
			mMapView.getController().animateTo(new GeoPoint((int)(location.getLatitude()*1e6),
					(int)(location.getLongitude()*1e6)));
			
		}
	}
	
	@Override
	public void onDestroy(){
		shopName=null;
		address=null;
		phone=null;
		shopCode=null;
		latitude=720;
		longitude=720;
		mMapView = null;
		super.onDestroy();
	}
	
	
	//弹出拨号menu
	public void onClickCall(final CharSequence charSequence){
		if(charSequence==null||charSequence.length()<=0){
			return;
		}
		final View view=View.inflate(getApplicationContext(), R.layout.call_menu, null);
		final Dialog call_menu = new AlertDialog.Builder(this).create();
		
		call_menu.show();
		call_menu.getWindow().setWindowAnimations(R.style.PopupAnimation);
		final Window window = call_menu.getWindow();
		final WindowManager.LayoutParams wl = window.getAttributes();
		// 根据x，y坐标设置窗口需要显示的位置
		// wl.x += x; //x小于0左移，大于0右移
		// wl.y +=heightPixels/2-280; //y小于0上移，大于0下移
		wl.y=0;
		wl.y += heightPixels/2-call_menu_tmp.getHeight()/2;
		// 对话框宽度
		wl.width =widthPixels;
		window.setAttributes(wl);
		window.setContentView(view);
		
		((TextView)view.findViewById(R.id.tv_number)).setText(charSequence);
		view.findViewById(R.id.imgV_cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				call_menu.dismiss();
			}
		});
		view.findViewById(R.id.imgV_call).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//call directly
				final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+charSequence.toString().replace("-","")));
				startActivity(intent);
				call_menu.dismiss();
			}
		});
	}
	

	class DetailTask extends AsyncReqTask{

		public DetailTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			if(BasicApplication.getInstance().isUnLogin()){
				return BabyShopProvider.getInstance(getApplicationContext()).getShopDetailedForUnlogin(
						(ShopDetailedReq)request);
			}else{
				return BabyShopProvider.getInstance(getApplicationContext()).getShopDetailed(
						(ShopDetailedReq)request);

			}
		}

		@Override
		protected void handleResponse(BaseRes response) {
			//activity已退出，防止空指针错误
			if(isFinishing()){
				return;
			}
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				//TODO 改成友好提示
				Toast.makeText(getApplicationContext(),response.getDesc(),Toast.LENGTH_SHORT)
					.show();
				return;
			}
			ShopDetailedRes res=(ShopDetailedRes)response;
			if(res.getMultipleShop()==null){
				return;
			}
			
			if(res.getMultipleShop().getShopText()!=null){
				tv_shop_inf.setText(res.getMultipleShop().getShopText());
				
				tv_shop_inf.setVisibility(View.VISIBLE);
				tv_shop_inf_tip.setVisibility(View.VISIBLE);
			}
			else tv_shop_inf.setText("");
			
			if(res.getSideFormDetailResultList()==null||res.getSideFormDetailResultList().size()<=0)
				return;
			
			findViewById(R.id.tv_orders).setVisibility(View.VISIBLE);
			findViewById(R.id.layout_orders).setVisibility(View.VISIBLE);
			
			for(UserOrder aOrder:res.getSideFormDetailResultList()){
				View layout=View.inflate(getApplicationContext(), R.layout.order_list_item, null);
				((TextView)layout.findViewById(R.id.tv_productitle))
				.setText(aOrder.getProductName());

				if(aOrder.getControlType().equals("exchange")){
					((TextView)layout.findViewById(R.id.tv_point))
						.setText("-"+aOrder.getPoints());
					((TextView) layout.findViewById(R.id.tv_point)).setTextColor(Color.parseColor("#8cc63f"));
					
					((ImageView)layout.findViewById(R.id.imgV_buyOrExch))
						.setBackgroundResource(R.drawable.do_exch);
				}
				else{
					((ImageView)layout.findViewById(R.id.imgV_buyOrExch))
						.setBackgroundResource(R.drawable.do_buy);
					
					((TextView)layout.findViewById(R.id.tv_point))
						.setText("+"+aOrder.getPoints());
				}
				((TextView)layout.findViewById(R.id.tv_date))
					.setText(aOrder.getCreatedTimestamp());
			
				final AsyncLoadImg loader=new AsyncLoadImg(layout.findViewById(R.id.imgV_product), aOrder.getImgUrl(),aOrder.getProductId(), R.drawable.product_defaul_img);
				loader.execute();
				((LinearLayout)findViewById(R.id.layout_orders))
					.addView(layout, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
			}
		}
		
	}
	
	
}
