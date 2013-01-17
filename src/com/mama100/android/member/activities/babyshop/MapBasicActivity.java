package com.mama100.android.member.activities.babyshop;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.util.LogUtils;

/**
 * 
 * @author eCo
 *
 */
public class MapBasicActivity extends MapActivity{
	public final static String mStrKey = "4518038717ACBB7A7B6EF84A3E1EAF3C9276C875";
	
	//百度MapAPI的管理类
	public static BMapManager mBMapMan = null;
	protected LocationListener mLocationListener = null;//onResume时注册此listener，onPause时需要Remove
	protected MyLocationOverlay mLocationOverlay = null;	//定位图层
	protected int widthPixels;
	protected int heightPixels;
	
	public static int  curLat = 720;// 纬度不能初始化为0,可能存在纬度为0
	public static int  curLon = 720;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(mBMapMan==null){
			mBMapMan = new BMapManager(this);
			mBMapMan.init(mStrKey,new MGeneralListener());
		}
        
		//最大30秒更新位置，最小8秒
		mBMapMan.getLocationManager().setNotifyInternal(30, 8);
		
		final DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		widthPixels = dm.widthPixels;
		heightPixels = dm.heightPixels;
		
		
		
        // 注册定位事件
        mLocationListener = new LocationListener(){
			@Override
			public void onLocationChanged(Location location) {
				if (location != null){
					
					curLat = (int)(location.getLatitude()*1e6);
					curLon = (int)(location.getLongitude()*1e6);
					
//					GeoPoint pt = new GeoPoint((int)(location.getLatitude()*1e6),
//							(int)(location.getLongitude()*1e6));
//					LogUtils.logv("MyBaiduDemo", "my location lat:"+location.getLatitude());
//					LogUtils.logv("MyBaiduDemo", "my location lon:"+location.getLongitude());
//					mMapView.getController().animateTo(pt);
					
					MapBasicActivity.this.onLocationChanged(location);
				}
			}
        };

	}
	
	
	protected void onLocationChanged(Location location) {
		//subclass override this method
		
	}


	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.e("MapBasicActivity", "onGetNetworkState error is "+ iError);
//			Toast.makeText(BasicApplication.getInstance().getApplicationContext(), "网络异常了，稍候再加载地图/列表",
//					Toast.LENGTH_LONG).show();
			networkStateError.onNetworkStateError();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.e("MapBasicActivity", "onGetPermissionState error is "+ iError);
			if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(BasicApplication.getInstance().getApplicationContext(), 
						"地图授权异常，请联系客服",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public static NetworkStateInterface networkStateError;
	
	public interface NetworkStateInterface{
		public void onNetworkStateError();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
//		if (mBMapMan != null) {
//			mBMapMan.destroy();
//			mBMapMan = null;
//		}
	}
	
	@Override
	protected void onPause() {
		if(mBMapMan != null&&mLocationListener!=null)
			mBMapMan.getLocationManager().removeUpdates(mLocationListener);
		if(mBMapMan != null)
			mBMapMan.stop();
		if(mLocationOverlay!=null){
			mLocationOverlay.disableMyLocation();
	        mLocationOverlay.disableCompass(); // 关闭指南针
		}
        
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// 注册定位事件，定位后将地图移动到定位点
		if(mBMapMan != null&&mLocationListener!=null)
			mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
		if(mBMapMan != null)
			mBMapMan.start();
		if(mLocationOverlay!=null){
			mLocationOverlay.enableMyLocation();
	        mLocationOverlay.enableCompass(); // 打开指南针
		}
		super.onResume();
	}
	
	/**
	 * 释放百度MapAPI的管理对象
	 */
	public static void delMapManager(){
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}


class OverItemT extends ItemizedOverlay<OverlayItem> {

	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	private Drawable marker;
//
//	private double mLat1 = 39.90923; // point1纬度
//	private double mLon1 = 116.357428; // point1经度
//
//	private double mLat2 = 39.90923;
//	private double mLon2 = 116.397428;

	private OnTapListener mTapLis;
	


	public OverItemT(Drawable marker,List<GeoPoint> points,OnTapListener tapLis) {
		super(boundCenterBottom(marker));
		this.marker = marker;
		mTapLis=tapLis;
		
//		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
//		GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));
//		GeoPoint p2 = new GeoPoint((int) (mLat2 * 1E6), (int) (mLon2 * 1E6));
//		
//		// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
//		mGeoList.add(new OverlayItem(p1, "P1", "point1"));
//		mGeoList.add(new OverlayItem(p2, "P2", "point2"));

		for(GeoPoint point:points){
			// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
			mGeoList.add(new OverlayItem(point,"",""));
		}
		populate();  //createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
	}

	public void updateOverlay()
	{
		populate();
	}

	
	public void updateOverlay(List<GeoPoint> points)
	{
		mGeoList.clear();
		
		for(GeoPoint point:points){
			// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
			mGeoList.add(new OverlayItem(point,"",""));
		}
		populate();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

		// Projection接口用于屏幕像素坐标和经纬度坐标之间的变换
		Projection projection = mapView.getProjection(); 
		for (int index = size() - 1; index >= 0; index--) { // 遍历mGeoList
			OverlayItem overLayItem = getItem(index); // 得到给定索引的item

			String title = overLayItem.getTitle();
			// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
			Point point = projection.toPixels(overLayItem.getPoint(), null); 

			// 可在此处添加您的绘制代码
			Paint paintText = new Paint();
			paintText.setColor(Color.BLUE);
			paintText.setTextSize(15);
			canvas.drawText(title, point.x-30, point.y, paintText); // 绘制文本
		}

		super.draw(canvas, mapView, shadow);
		//调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
		boundCenterBottom(marker);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mGeoList.get(i);
	}

	@Override
	public int size() {
		return mGeoList.size();
	}
	
	// 处理点击图钉事件
	@Override
	protected boolean onTap(int i) {
		setFocus(mGeoList.get(i));
		mTapLis.onTapMarkView(mGeoList.get(i).getPoint(),i);
		return true;
	}

	// 处理点击MapView事件
	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		mTapLis.onTapLayer(arg0);
		return super.onTap(arg0, arg1);
	}
	
	interface OnTapListener{
		/**
		 * 处理点击覆盖层的事件。如果点中了图钉事件会传递到onTapMarkView。
		 * @param point
		 */
		public void onTapLayer(GeoPoint point);
		/**
		 * 处理点击图钉事件(先触发了onTapLayer，再传事件至此)
		 * @param point
		 * @param pointList中的第几个点
		 */
		public void onTapMarkView(GeoPoint point,int index);
	}

}
