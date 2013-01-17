package com.mama100.android.member.activities.babyshop;



import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKMapViewListener;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.mama100.android.member.R;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.util.GeoUtil;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.widget.MapViewEx;



/**
 * 
 * @author eCo
 *
 */
public class ShopMapActivity extends MapBasicActivity {
	public static final String POINTS_LOC_KEY="points";
	public static final String NAME_KEY="name";
	public static final String TITLE_RES_ID_KEY="title";
	
	protected View mPopView = null;	// 点击mark时弹出的气泡View
	protected MapViewEx mMapView = null;
	protected boolean isIMap=true;
	protected int iZoom = 0;
	protected GeoPoint iCenter=null; //当前center
	protected GeoPoint iniCenter; //初始center
	protected OverItemT overitem = null;

	
	protected List<GeoPoint> points=null;
	protected int beginHideIndex=0; //points中，小于beginHideIndex点是在地图上隐藏的
	protected String[] pointName=null;
	protected int titleResId;
	
//	private String TAG = this.getClass().getSimpleName();
	
	protected void onCreate(Bundle savedInstanceState) {
//		//1.内存测试 start
//		long total = Runtime.getRuntime().totalMemory();
//		LogUtils.loge(TAG , "获得的内存 - " + (total) + "字节");
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.shops_mapview);

		buildViewsInTopBar(getIntent().getIntExtra(TITLE_RES_ID_KEY, R.string.near_bbshop));
        
		
		mBMapMan.start();
        //初始化地图Activity
        super.initMapActivity(mBMapMan);
        networkStateError=new NetworkStateInterface() {
			
			@Override
			public void onNetworkStateError() {
				Toast.makeText(BasicApplication.getInstance().getApplicationContext(), "网络异常了，稍候再加载地图",
				Toast.LENGTH_LONG).show();
			}
		};
        
        mMapView = (MapViewEx)findViewById(R.id.mapV_map);
        mMapView.setBuiltInZoomControls(true);
        //设置在缩放动画过程中也显示overlay,默认为不绘制
        mMapView.setDrawOverlayWhenZooming(true);
        mMapView.setTraffic(false);
        

        int[] pointsInt=getIntent().getIntArrayExtra(POINTS_LOC_KEY);
        points=new ArrayList<GeoPoint>();


        //
        for(int index=0;index<pointsInt.length;){
        	points.add(new GeoPoint(pointsInt[index], pointsInt[index+1]));
        	index=index+2;
        }
        pointName=getIntent().getStringArrayExtra(NAME_KEY);
        
        //进入界面时，最多显示5个
        int[] params=null;
        if(points.size()>6)
        	params=GeoUtil.getAdaptiveParams(widthPixels, heightPixels, points.subList(0, 6));
        else
        	params=GeoUtil.getAdaptiveParams(widthPixels, heightPixels, points);
        
		iZoom = params[2];
		iCenter=new GeoPoint(params[0], params[1]);
		iniCenter=iCenter;
		
		// 创建点击mark时的弹出泡泡
		mPopView=super.getLayoutInflater().inflate(R.layout.map_popview2, null);
		mMapView.addView( mPopView,
                new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                		null, MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);
		
		mPopView.findViewById(R.id.layout_popwindow).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//取出此门店对应的列表位置
				int index=(Integer) mPopView.getTag();
				BabyShopActivity.initNearShopDetailActivity(index);
				startActivity(new Intent(ShopMapActivity.this, ShopDetailActivity.class));
				isIMap=false;
			}
		});
		
        // 添加ItemizedOverlay
		Drawable marker = getResources().getDrawable(R.drawable.loc_mapmark);  //得到需要标在地图上的资源
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
				.getIntrinsicHeight());   //为maker定义位置和边界
		
		overitem = new OverItemT(marker, points,new OverItemT.OnTapListener() {
			@Override
			public void onTapMarkView(GeoPoint point,int index) {
				// 更新气泡位置,并使之显示
				mMapView.updateViewLayout(mPopView, new MapView.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						point, MapView.LayoutParams.BOTTOM_CENTER));
				((TextView)mPopView.findViewById(R.id.tv_poptip)).setText(pointName[index+beginHideIndex]);
				//此控件的主要作用是让mPopView居左
				((TextView)mPopView.findViewById(R.id.tv_hide)).setText(pointName[index+beginHideIndex]);
				mPopView.setVisibility(View.VISIBLE);
				//将列表位置传入
				mPopView.setTag(index+beginHideIndex);
			}
			
			@Override
			public void onTapLayer(GeoPoint point) {
				// 消去弹出的气泡
				mPopView.setVisibility(View.GONE);
			}
		});
		
		mMapView.getOverlays().add(overitem); //添加ItemizedOverlay实例到mMapView



		
		// 添加定位图层
        mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mLocationOverlay);
		
        mMapView.getController().setCenter(iCenter);
		mMapView.getController().setZoom(iZoom);
		
//		Log.v("onCreate iZoom:", iZoom+"");
//		Log.v("onCreate iCenter:", iCenter.getLatitudeE6()+"");

		
		mMapView.regMapViewListener(mBMapMan, new MKMapViewListener(){
			
			public void onMapMoveFinish() {
				if(isIMap){
					iZoom = mMapView.getZoomLevel();
					iCenter=mMapView.getMapCenter();
//						
//					Log.v("onMapMoveFinish iZoom:", iZoom+"");
//					Log.v("onMapMoveFinish iCenter:", iCenter.getLatitudeE6()+"");
					
					Log.v("ShopMapActivity", iZoom + "Lvl: "
							+ 1 / mMapView.metersToPixels(1) + " m/pix");
					if(iZoom >14){
						overitem.updateOverlay(points);
						beginHideIndex=0;
					}
					//zoom=14时隐藏1500m内的点
					else if (iZoom <= 14) {
						if (BabyShopActivity.shopDistances != null) {
							final int len = BabyShopActivity.shopDistances.length;

							//每缩小一个比例，隐藏范围加1000米
							final int scope=(1501+(1000*(14-iZoom)));
							for (int index = 0; index < len; index++) {

								if (BabyShopActivity.shopDistances[index] <= scope
										&& (index != len-1 && BabyShopActivity.shopDistances[index + 1]> scope)){
									overitem.updateOverlay(points.subList(
											index, points.size()));
									beginHideIndex=index;
									break;
								}
								//最少显示默认的6家(如果总数大于6的话)
								else if((index == len-1 && BabyShopActivity.shopDistances[index]<= scope)){
									overitem.updateOverlay(points.subList(
											index-5>0?index-5:0, points.size()));
									beginHideIndex=index-5>0?index-5:0;
									break;
								}
							}
						}
					}
					
					//当比例被用户缩小到小于11时，放大地图比例到11，移至初始化中心点
					if(iZoom<11){
						mMapView.getController().setZoom(11);
						mMapView.getController().animateTo(iniCenter);
					}
				}
			}
		});
		
		
//		//1.内存测试 start
//		long free = Runtime.getRuntime().totalMemory();
//		LogUtils.loge(TAG, "用掉的内存 - " + (total-free) + "字节");
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (points!=null) {
			points.clear();
			points = null;
		}
		
			pointName = null;
	}


	
	/**
	 * 设置顶部
	 */
	private void buildViewsInTopBar(int titleResId) {
		final TextView tv_title = (TextView) this.findViewById(R.id.menutitle);
		final ImageButton rightButton = (ImageButton) this.findViewById(R.id.mkt_top_right_btn);
		rightButton.setVisibility(View.INVISIBLE);
		
		final ImageButton leftButton = (ImageButton) this.findViewById(R.id.mkt_top_left_btn);
		leftButton.setVisibility(View.VISIBLE);
		leftButton.setBackgroundResource(R.drawable.selector_back);
		leftButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_title.setText(titleResId);
	}

	
	@Override
	public void onRestart(){
		super.onRestart();
		
		if(mBMapMan != null)
			mBMapMan.start();

//        //从详情地图回来，要回到原始zoom
//		//目前ShopMapActivity只有到ShopDetailActivity的跳转才可以这样做
//        for(int i=0;i<ShopDetailActivity.iZoom-iZoom;i++){
//        	mMapView.getController().zoomOut();
//        	if(i==ShopDetailActivity.iZoom-iZoom-1){
//                Log.v("iCenter:", iCenter.getLatitudeE6()+"");
//                mMapView.getController().animateTo(new GeoPoint(iCenter.getLatitudeE6()+2, iCenter.getLongitudeE6()+2));
//        	}
//        }
//        for(int i=0;i>ShopDetailActivity.iZoom-iZoom;i--){
//        	mMapView.getController().zoomIn();
//        	if(i==ShopDetailActivity.iZoom-iZoom+1){
//                Log.v("iCenter:", iCenter.getLatitudeE6()+"");
//                mMapView.getController().animateTo(new GeoPoint(iCenter.getLatitudeE6()+2, iCenter.getLongitudeE6()+2));
//        	}
//        }
        
        mMapView.getController().setZoom(iZoom+1); //这是百度地图的一个bug，设置相同的zoom无效(mMapView在详情的zoom已变，在当前的zoom没有变)
        mMapView.getController().setZoom(iZoom);
        mMapView.getController().setCenter(new GeoPoint(iCenter.getLatitudeE6()+1, iCenter.getLongitudeE6()+1)); //百度地图bug，道理同上

//        mMapView.getController().animateTo(iCenter);
        
        

        isIMap=true;
		mMapView.regMapViewListener(mBMapMan, new MKMapViewListener(){
			
			public void onMapMoveFinish() {
				if(isIMap){
					iZoom = mMapView.getZoomLevel();
					iCenter=mMapView.getMapCenter();
//						
//					Log.v("onMapMoveFinish iZoom:", iZoom+"");
//					Log.v("onMapMoveFinish iCenter:", iCenter.getLatitudeE6()+"");
					
//					Log.v("ShopMapActivity", iZoom + "Lvl: "
//					+ 1 / mMapView.metersToPixels(1) + " m/pix");
					if(iZoom >14){
						overitem.updateOverlay(points);
						beginHideIndex=0;
					}
					//zoom=14时隐藏1500m内的点
					else if (iZoom <= 14) {
						if (BabyShopActivity.shopDistances != null) {
							final int len = BabyShopActivity.shopDistances.length;
							//每缩小一个比例，隐藏范围加1000米
							final int scope=(int) (1501+(1000 * ( Math.pow(2,(14-iZoom))-1  ) ));
							for (int index = 0; index < len; index++) {


								if (BabyShopActivity.shopDistances[index] <= scope
										&& (index != len-1 && BabyShopActivity.shopDistances[index + 1]> scope)){
									overitem.updateOverlay(points.subList(
											index, points.size()));
									beginHideIndex=index;
									break;
								}
								//最少显示默认的6家(如果总数大于6的话)
								else if((index == len-1 && BabyShopActivity.shopDistances[index]<= scope)){
									overitem.updateOverlay(points.subList(
											index-5>0?index-5:0, points.size()));
									beginHideIndex=index-5>0?index-5:0;
									break;
								}
							}
						}
					}
			
					
					//当比例被用户缩小到小于11时，放大地图比例到11，移至初始化中心点
					if(iZoom<11){
						mMapView.getController().setZoom(11);
						mMapView.getController().animateTo(iniCenter);
					}
					
				}
			}
		});

	}
	
}


