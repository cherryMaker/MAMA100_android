package com.mama100.android.member.util;



import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.CoordinateConvert;
import com.baidu.mapapi.GeoPoint;

/**
 * 地理位置工具类
 * @author eCo
 *
 */
public class GeoUtil {
	private static double DEF_PI180 = 0.01745329252; // PI/180.0
	private static double DEF_R = 6370693.5; // radius of earth 单位为米
	
	/**
	 * 计算两个经纬度间的距离
	 * @param lon1 经度
	 * @param lat1 纬度
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double getLongDistance(double lon1, double lat1, double lon2,
			double lat2) {
		double ew1, ns1, ew2, ns2;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 求大圆劣弧与球心所夹的角(弧度)
		distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1)
				* Math.cos(ns2) * Math.cos(ew1 - ew2);
		// 调整到[-1..1]范围内，避免溢出
		if (distance > 1.0)
			distance = 1.0;
		else if (distance < -1.0)
			distance = -1.0;
		// 求大圆劣弧长度
		distance = DEF_R * Math.acos(distance);
		return distance;
	}
	
	/**
	 * 根据一组地理坐标，返回他们的中心点。
	 * 思路是使用一个矩形恰好能包括所有散布的点，而这个矩形的中心点则为所有坐标的中心。
	 * @param points
	 * @return 返回中心点，null if error。
	 */
	public static GeoPoint getMapCenter(List<GeoPoint> points){
		if(points==null||points.size()==0)
			return null;
		//1.计算矩形边缘
		int maxLon=-720,maxLat=-720,minLon=720,minLat=720;
		for(GeoPoint tmp:points){
			maxLon=maxLon>tmp.getLongitudeE6()?maxLon:tmp.getLongitudeE6();
			maxLat=maxLat>tmp.getLatitudeE6()?maxLat:tmp.getLatitudeE6();
			
			minLon=minLon<tmp.getLongitudeE6()?minLon:tmp.getLongitudeE6();
			minLat=minLat<tmp.getLatitudeE6()?minLat:tmp.getLatitudeE6();
		}
		//2.计算中心点
		int lonCenter=(int) ((maxLon+minLon)/2.0d);
		int latCenter=(int) ((maxLat+minLat)/2.0d);
		return new GeoPoint(latCenter, lonCenter);
	}
	
	/**
	 * 根据给定的一组地理坐标，返回能够让地图适配屏幕的参数，参数总是3个，
	 * 依次为屏幕地图中心点纬度，中心点经度，百度缩放比例：int[3]{latCenter,lonCenter,zoom}。
	 * 通过设置这三个参数，可以让这一组地理坐标恰到好处地标记在一个屏幕上。
	 * @param widthpixel 手机屏幕宽度
	 * @param heightpixel 手机屏幕高度
	 * @param points GeoPoint列表
	 * @return {@code  int[3]:latCenter,lonCenter,zoom }。依次为屏幕地图中心点纬度（单位微度），
	 * 中心点经度（单位微度），百度缩放比例。
	 */
	public static int[] getAdaptiveParams(int widthpixel,int heightpixel,List<GeoPoint> points){
		if(points==null||points.size()==0)
			return null;
		//1.计算矩形边缘，初始化，确保最大的比任何取值都小，最小的比任何取值都大
		int maxLon=-720*1000000,maxLat=-720*1000000,minLon=720*1000000,minLat=720*1000000;
		for(GeoPoint tmp:points){
			maxLon=maxLon>tmp.getLongitudeE6()?maxLon:tmp.getLongitudeE6();
			maxLat=maxLat>tmp.getLatitudeE6()?maxLat:tmp.getLatitudeE6();
			
			minLon=minLon<tmp.getLongitudeE6()?minLon:tmp.getLongitudeE6();
			minLat=minLat<tmp.getLatitudeE6()?minLat:tmp.getLatitudeE6();
		}
		//2.计算中心点
		int lonCenter=(int) ((maxLon+minLon)/2.0d);
		int latCenter=(int) ((maxLat+minLat)/2.0d);
		//3.计算最东与最西、最南与最北距离
		double westEDis=getLongDistance(((double)maxLon)/1e6, ((double)latCenter)/1e6, ((double)minLon)/1e6, ((double)latCenter)/1e6);
		double southNDis=getLongDistance(((double)lonCenter)/1e6, ((double)maxLat)/1e6, ((double)lonCenter)/1e6, ((double)minLat)/1e6);
		
		//4.计算缩放比例,分东西方向最大的百度地图比例[3-18]，南北方向最大的百度地图比例[3-18]（越大越详细）

		/*******************算法解析*******************************
		 * 首先需要一个地图中心点，我们通过模拟一个矩形
		 * 将所有坐标恰好能包含，而矩形中心则近似为所有坐标的中心，
		 * 即地图中心点。
		 * 
		 * 再通过上面的矩形可以得到最东与最西、最南与最北距离。
		 * 以最东到最西的距离为例，我们得到west-East Distance之后，
		 * 要想所有坐标显示而且不拥挤即是让手机屏幕宽度恰好能够显示
		 * 这段距离，我们取屏幕宽度的6/7（高度7/8），计算出每个像素需要代表S米:
		 * Distance/(6/7 *width)。
		 * 根据百度的比例尺，pow(2,18-zoom)，是每个像素代表的距离(米)——此公式通过反复测试得出。
		 * 如zoom=17，则每个像素代表2米。因此通过求对数，则可以求得zoom。
		 *************************************************/
		int westEValue=(int) (18.0d-Math.log((westEDis/2.0d)/(3.0d/7.0d*widthpixel))
				/Math.log(2.0d));
		
		int southNValue=(int) (18.0d-Math.log((southNDis/2.0d)/(7.0d/16.0d*heightpixel))
				/Math.log(2.0d));
		//6.返回两者最小的值，以能够显示所有坐标
		int minValue=westEValue<=southNValue?westEValue:southNValue;
		//百度要求缩放比例范围为 3~18
		if(minValue<3)
			minValue=3;
		if(minValue>18)
			minValue=18;
		return new int[]{latCenter,lonCenter,minValue};
	}
	
	
	/**
	 * 以珠江新城 IFC(百度坐标)为参考，随机生成11组坐标数据(其中一组为IFC坐标)
	 * @param offset 偏移量，如0.1指113.123526中调整小数点后的第一位。建议取类似10, 1, 0.1, 0.01...的值
	 * @return
	 */
	public static List<GeoPoint> getTestingData(float offset){
		//珠江新城 IFC
		double lat=23.123258590698242;

		double lon=113.33031463623047;

		GeoPoint ifcPoint = new GeoPoint((int) (lat* 1E6),
				(int) (lon * 1E6));
		
		List<GeoPoint> points=new ArrayList<GeoPoint>();
		points.add(ifcPoint);
		for(int i=0;i<10;i++){
			//-1~1
			double random1=Math.random()+(-Math.random());
			double random2=Math.random()+(-Math.random());
			
			points.add(new GeoPoint((int)((lat+random1*offset*10)*1e6)
					,(int)((lon+random2*offset*10)*1e6)));
		}
		return points;
	}
	
	/**
	 * 以珠江新城 IFC(百度坐标)为参考，随机生成指定个数的坐标数据(其中一组为IFC坐标)
	 * @param offset 偏移量，如0.1指113.123526中调整小数点后的第一位。建议取类似10, 1, 0.1, 0.01...的值
	 * @param numNeeded 需要的坐标的个数，大于1
	 * @return
	 */
	public static List<GeoPoint> getTestingData(float offset,int numNeeded){
		if(numNeeded<=1)
			return null;
		//珠江新城 IFC()
		double lat=23.123258590698242;

		double lon=113.33031463623047;

		GeoPoint ifcPoint = new GeoPoint((int) (lat* 1E6),
				(int) (lon * 1E6));
		
		List<GeoPoint> points=new ArrayList<GeoPoint>();
		points.add(ifcPoint);
		for(int i=0;i<numNeeded-1;i++){
			//-1~1
			double random1=Math.random()+(-Math.random());
			double random2=Math.random()+(-Math.random());
			
			points.add(new GeoPoint((int)((lat+random1*offset*10)*1e6)
					,(int)((lon+random2*offset*10)*1e6)));
		}
		return points;
	}
	

	/**
	 * 将国际标准（WGS84）坐标转换成百度坐标
	 * @param gpsLat 纬度
	 * @param gpsLon 经度
	 * @return
	 */
	public static GeoPoint wgs84ToBaidu(double gpsLat,double gpsLon){
		//测试数据
//    	double IFClon=113.3188348719025d;
//    	double IFClat=23.119042092823754d;
//        GeoPoint tmpP=GeoUtil.wgs84ToBaidu(IFClat, IFClon);
//        Log.v("MyBaiduDemo", "Baidu.getLatitudeE6():"+tmpP.getLatitudeE6());
//        Log.v("MyBaiduDemo", "Baidu.getLatitudeE6():"+tmpP.getLongitudeE6());

		return CoordinateConvert.bundleDecode(CoordinateConvert.fromWgs84ToBaidu( 
				new GeoPoint((int)(gpsLat*1E6), (int)(gpsLon*1E6))));
	}
	
	/**
	 * 将Google、高德地图(Navi)坐标转换成百度坐标
	 * @param googleLat 纬度
	 * @param googleLon 经度
	 * @return
	 */
	public static GeoPoint googleToBaidu(double googleLat,double googleLon){
//        测试数据
//        double googleIFCLat=23.117600d;
//        double googleIFCLon=113.324000d;
//        GeoPoint tmpP2=GeoUtil.googleToBaidu(googleIFCLat, googleIFCLon);
//        Log.v("MyBaiduDemo", "Baidu.getLatitudeE6()2:"+tmpP2.getLatitudeE6());
//        Log.v("MyBaiduDemo", "Baidu.getLatitudeE6()2:"+tmpP2.getLongitudeE6());
		return CoordinateConvert.bundleDecode(CoordinateConvert.fromGcjToBaidu( 
				new GeoPoint((int)(googleLat*1E6), (int)(googleLon*1E6))));
	}
	
}
