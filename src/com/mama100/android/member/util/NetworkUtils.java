package com.mama100.android.member.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.mama100.android.member.R;

/**
 * 网络监测工具类
 * @author jimmy
 */
public class NetworkUtils {
	
	private static String LOG_TAG = NetworkUtils.class.getSimpleName();
	
	public static final int RESULT_CON_OK = 0; 
	public static final int RESULT_CON_FAIL = -1; 
	public static final int RESULT_CON_CMWAP = -2;
	public static final int RESULT_CON_EXCEPTION = -9;
	
	public static final String HINT_OPEN_CMNET = "您需要在移动网络设置中打开CMNET接入点才可正常使用.";
	public static final String HINT_OPEN_CON = "您的网络似乎不正常,请检查您的手机网络.";
	public static final String HINT_NET_OK = "您的网络很正常";
	
	//private static final String MOBILE_CON_CMNET = "cmnet"; 
	private static final String MOBILE_CON_CMWAP = "cmwap"; 
	
	
	private static long lastmilliseconds; //上次网络很差的检查时间以微秒为单位

	private static long MINUTE_INTERVAL = 5; //间隔五分钟, 这里必须是整数

	private static boolean isWarning = false;//是否需要弹出提示
	
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	
	private static boolean  isExit = false; //是否完全退出程序：在登录界面点击退出
	
	public static boolean isExit() {
		return isExit;
	}
	public static void setExit(boolean b) {
		isExit = b;
	}
	
	public static boolean isNetworkAvailable(Context ctx) {
		
		try {
			ConnectivityManager cm = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			LogUtils.loge(LOG_TAG, LogUtils.getStackTrace(e));
			return false;
		}
		
	}
	
	/**
	 * 检查网络状态
	 * @param ctx
	 * @return 0-网络正常  -1-网络未连接 -2-CMWAP连接 -9-网络异常
	 */
	public static int checkNetwork(Context ctx) {
		
		int result = RESULT_CON_OK;
		
		try {
			
			ConnectivityManager cm = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			
			if (info != null && info.isConnected()) {		
				
				//String typeName = info.getTypeName(); mobile
				
				String extraInfo = info.getExtraInfo();
				if (extraInfo != null && extraInfo.toLowerCase().equals(MOBILE_CON_CMWAP)) {
					return RESULT_CON_CMWAP;
				} 
				
			} else {
				result = RESULT_CON_FAIL;
			}
			
		} catch (Exception e) {
			LogUtils.loge(LOG_TAG, LogUtils.getStackTrace(e));
			result = RESULT_CON_EXCEPTION;
		}
		
		return result;
	}
	
	
    /**
     * 检测网络状态, 并给出错误提示
     * @return
     */
    public static boolean checkNetworkStatusAndHint(Context ctx) {
    	
		// 检查网络状态
		int checkResult = checkNetwork(ctx);
		
		final StringBuffer msg = new StringBuffer(50);
		if (checkResult != NetworkUtils.RESULT_CON_OK) {
			
			
			if (checkResult == NetworkUtils.RESULT_CON_CMWAP) {
				msg.append(NetworkUtils.HINT_OPEN_CMNET);
			} else {	
				msg.append(NetworkUtils.HINT_OPEN_CON);
			}
			Toast.makeText(ctx, msg.toString(), Toast.LENGTH_LONG).show();
			return false;
		}else{
			msg.append(NetworkUtils.HINT_NET_OK);
//			Toast.makeText(ctx, msg.toString(), Toast.LENGTH_LONG).show();
		}
		
    	
    	return true;
    }
    /**
     * 检测网络状态, 并给出错误提示
     * @return
     */
    public static boolean checkNetworkStatusAndHint(Context ctx , boolean show) {
    	
    	// 检查网络状态
    	int checkResult = checkNetwork(ctx);
    	
    	final StringBuffer msg = new StringBuffer(50);
    	if (checkResult != NetworkUtils.RESULT_CON_OK) {
    		
    		
    		if (checkResult == NetworkUtils.RESULT_CON_CMWAP) {
    			msg.append(NetworkUtils.HINT_OPEN_CMNET);
    		} else {	
    			msg.append(NetworkUtils.HINT_OPEN_CON);
    		}
    		if(show)
    		Toast.makeText(ctx, msg.toString(), Toast.LENGTH_LONG).show();
    		return false;
    	}else{
    		msg.append(NetworkUtils.HINT_NET_OK);
//			Toast.makeText(ctx, msg.toString(), Toast.LENGTH_LONG).show();
    	}
    	
    	
    	return true;
    }
    
    
    
    /**
     * 检测网络状态, 并给出错误提示
     * @param milliseconds  用于在一定时间后显示网络提示框，与网络状态检测逻辑无关。
     * @return
     */
    public static boolean checkNetworkStatusAndHintForService(Context ctx, long milliseconds) {
    	
    	//先判断网络，如果没有可用网络，直接返回false;
    	if(checkNet(ctx)){
    	
    	if(lastmilliseconds==0){
    		lastmilliseconds = milliseconds;
    	}
    	//检查时间差。。如果超出了5分钟。就进行替换操作
			long l= milliseconds - lastmilliseconds;
			long day=l/(24*60*60*1000);
			long hour=(l/(60*60*1000)-day*24);
			long min=((l/(60*1000))-day*24*60-hour*60);
			long s=(l/1000-day*24*60*60-hour*60*60-min*60);
			
//			System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
//			LmUtils.logi(LOG_TAG, ""+day+"天"+hour+"小时"+min+"分"+s+"秒");
			//光看以分钟 为单位， 间隔多少分钟
			long min2=((l/(60*1000)));
//			System.out.println(""+min2+"分");
//			LmUtils.logi(LOG_TAG, ""+min2+"分");
//			LmUtils.logi("NetworkUtil", "间隔 "+ min2+ " 分钟" );
			if(min2>=MINUTE_INTERVAL){
				lastmilliseconds = milliseconds;
				if(!isExit)
				{isWarning = true;}else{
					isWarning = false;
				}
			}else{
				isWarning  = false;
			}
			
    	
    	
    	
    	// 检查网络状态
			
    	int checkResult = checkNetwork(ctx);
    	
    	final StringBuffer msg = new StringBuffer(50);
    	if (checkResult != NetworkUtils.RESULT_CON_OK) {
    		
    		
    		if (checkResult == NetworkUtils.RESULT_CON_CMWAP) {
    			msg.append(NetworkUtils.HINT_OPEN_CMNET);
    		} else {	
    			msg.append(NetworkUtils.HINT_OPEN_CON);
    		}
    		if(isWarning){
			Toast.makeText(ctx, msg.toString(), Toast.LENGTH_LONG).show();}
    		return false;
    	}else{
    		msg.append(NetworkUtils.HINT_NET_OK);
//			Toast.makeText(ctx, msg.toString(), Toast.LENGTH_LONG).show();
    	}
    	return true;
    	
    	}else{
    		return false;
    	}
    }
    
	/**
	 * @Title: doCheckNetWork
	 * @Description:检查网络环境，提示用户。
	 * @return true,网络正常，false,网络问题
	 */

	public static boolean doCheckNetWork(Context mContext) {
		boolean flag = false;
		LogUtils.logi(LOG_TAG, "check network thread Id: "
				+ Thread.currentThread().getId() + "");
		int code = -100;
		// ServerHandler被赋值之后，检查网络状态
		code = NetworkUtils.checkNetwork(mContext);
		LogUtils.logi(
				LOG_TAG,
				"CheckThread time is: "
						+ new Timestamp(System.currentTimeMillis()));
		switch (code) {
		case NetworkUtils.RESULT_CON_OK:
			LogUtils.logi(LOG_TAG, ">>> network is ok");
//			Global.showLongToast("网络正常");
			flag = true;
			break;
		case NetworkUtils.RESULT_CON_CMWAP:
			LogUtils.logi(LOG_TAG, ">>> network cmwap problem");
			break;

		default:
			LogUtils.logi(LOG_TAG, ">>> network didn't open");
			break;
		}

		return flag;
	}


	//added by edwar 2012-06-18 检查网络连接
	public static boolean checkNet(Context context) {

		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
}
