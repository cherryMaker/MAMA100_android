package com.mama100.android.member.outwardHttp;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mama100.android.member.outwardWeibo.QQGetIdErrorRes;
import com.mama100.android.member.outwardWeibo.QQGetOpenIdRes;
import com.mama100.android.member.outwardWeibo.SocialParameters;
import com.mama100.android.member.outwardWeibo.XWeibo;
import com.mama100.android.member.outwardWeibo.XWeiboBaseRes;
import com.mama100.android.member.outwardWeibo.XWeiboErrorRes;
import com.mama100.android.member.outwardWeibo.XWeiboUserRes;
import com.mama100.android.member.util.LogUtils;


/**
 * OutwardHttpClient辅助类
 * 里面包括该类的辅助静态常量及静态函数
 * @author ecoo
 *
 */
public class OutwardHttpClientHelper {
	public static final String LOG_TAG=OutwardHttpClientHelper.class.getName();
	

	
	// 成功
	public static final String SUCCESS = "100";
	// =================== 系统异常应答码 ===================
	// 系统异常
	public static final String SYSTEM_EXCEPTION = "900";
	// 数据库异常
	public static final String DB_EXCEPTION = "901";
	// 密钥异常
	public static final String KEY_EXCEPTION = "902";
	// 数据异常
	public static final String DATA_EXCEPTION = "903";
	// 系统维护
	public static final String SYSTEM_MAINTAIN = "904";
	// 积分服务器系统异常
	public static final String EJB_EXCEPTION = "905";
	// http通信异常
	public static final String HTTP_ERROR = "5906";
	// http网络连接异常
	public static final String HTTP_SOKET_EXCEPTION = "5907";
	// http网络通信超时
	public static final String HTTP_SOKET_TIMEOUT = "5908";
	
	
	/**
	 * 创建获取新浪微博用户信息的URL(GET方法)
	 * @param accessToken 签名
	 * @param userUid 查询用户的UID
	 * @return url GET方法的URL
	 */
	public static StringBuffer createSinaUserUrl(String accessToken,String userUid){
		StringBuffer sbuf=new StringBuffer(XWeibo.URL_USER_INF);
		sbuf.append("?access_token=");
		sbuf.append(accessToken);
		sbuf.append("&uid=");
		sbuf.append(userUid);
		return sbuf;
	}
	
	/**
	 * GET方法获得指定的OutwardRes
	 * @param outwardResClass 解释HttpResponse的具体应答类，
	 * 如{@link XWeiboUserRes}。
	 * @param urlForGet GET方法的URL
	 * @return OutwardBaseRes
	 */
	public static OutwardBaseRes outwardResByGet(Class<? extends OutwardBaseRes> outwardResClass, 
			StringBuffer urlForGet) {
		return outwardResByGet(outwardResClass,urlForGet,null);
	}

	
	
	/**
	 * GET方法获得指定的OutwardRes
	 * @param outwardResClass 解释HttpResponse的具体应答类，
	 * 如{@link XWeiboUserRes}。
	 * @param urlForGet GET方法的URL
	 * @return OutwardBaseRes
	 */
	public static OutwardBaseRes outwardResByGet(Class<? extends OutwardBaseRes> outwardResClass, 
			StringBuffer urlForGet,SocialParameters wbParams) {
		OutwardBaseRes response=null;
		final OutwardHttpClient httpClient=new OutwardHttpClient();
		try {
			response=outwardResClass.newInstance();
			HttpResponse httpResponse;
			if(wbParams!=null){
				List<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
	        	for(int i=0;i<wbParams.size();i++)
	        		params.add(new BasicNameValuePair(wbParams.getKey(i), wbParams.getValue(i)));
				// GET提交数据至服务器
	        	httpResponse = httpClient.get(urlForGet,params);
			}
			else 
				// GET提交数据至服务器
				httpResponse = httpClient.get(urlForGet);
			if (httpResponse == null) {
				LogUtils.loge(OutwardHttpClientHelper.class, "http response is null.");
				response.setCode(SYSTEM_EXCEPTION);
				response.setDesc("网络通信异常,请稍后再试");
				return response;
			}


			// 解析JSON字符串，并转换成response对象
			response = parseJsonResponse(httpResponse, response);
		} catch (Exception e) {
			setExceptionResponse(e, response);
		} finally {
			if (httpClient != null) {
				httpClient.shutdownHttpClient();
			}
		}
		if(response.getCode()==null){
			response.setCode(SUCCESS);
			response.setDesc("Success");
		}
		return response;
	}

	
	
	/**
	 * Post方法获得指定的OutwardRes
	 * @param outwardResClass 解释HttpResponse的具体应答类，
	 * 如{@link XWeiboUserRes}。如果不需要捕捉返回的json数据，就传递{@link OutwardBaseRes}.class
	 * @param urlForPost POST方法的URL
	 * @return OutwardBaseRes
	 * @param wbParams 相当于Map，用于post请求
	 * @param file File或Bitmap对象文件,无文件传null
	 */
	public static OutwardBaseRes outwardResByPost(Class<? extends OutwardBaseRes> outwardResClass, 
			StringBuffer urlForPost,SocialParameters wbParams,Object file) {
		OutwardBaseRes response=null;
		final OutwardHttpClient httpClient=new OutwardHttpClient();
		try {
			response=outwardResClass.newInstance();
			
			List<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
        	for(int i=0;i<wbParams.size();i++)
        		params.add(new BasicNameValuePair(wbParams.getKey(i), wbParams.getValue(i)));
        	
        	final HttpResponse httpResponse=httpClient.post(urlForPost, file, params);
			
			if (httpResponse == null) {
				LogUtils.loge(OutwardHttpClientHelper.class, "http response is null.");
				response.setCode(SYSTEM_EXCEPTION);
				response.setDesc("网络通信异常,请稍后再试");
				return response;
			}
			// 解析JSON字符串，并转换成response对象
			response = parseJsonResponse(httpResponse, response);
		} catch (Exception e) {
			setExceptionResponse(e, response);
		} finally {
			if (httpClient != null) {
				httpClient.shutdownHttpClient();
			}
		}
//		if(response instanceof XWeiboErrorRes){
//			
//		}
		if(response.getCode()==null){
			response.setCode(SUCCESS);
			response.setDesc("Success");
		}
		return response;
	}
	
	
	/**
	 * Post方法获得指定的OutwardRes
	 * @param outwardResClass 解释HttpResponse的具体应答类，
	 * 如{@link XWeiboUserRes}。如果不需要捕捉返回的json数据，就传递{@link OutwardBaseRes}.class
	 * @param urlForPost POST方法的URL
	 * @return OutwardBaseRes
	 * @param wbParams 相当于Map，用于post请求
	 * @param bitmap 图片
	 */
	public static OutwardBaseRes outwardResByPost(Class<? extends OutwardBaseRes> outwardResClass, 
			StringBuffer urlForPost,SocialParameters wbParams,Bitmap bitmap) {
		OutwardBaseRes response=null;
		final OutwardHttpClient httpClient=new OutwardHttpClient();
		try {
			response=outwardResClass.newInstance();
			
			List<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
        	for(int i=0;i<wbParams.size();i++)
        		params.add(new BasicNameValuePair(wbParams.getKey(i), wbParams.getValue(i)));
        	
        	final HttpResponse httpResponse=httpClient.post(urlForPost, bitmap, params);
			
			if (httpResponse == null) {
				LogUtils.loge(OutwardHttpClientHelper.class, "http response is null.");
				response.setCode(SYSTEM_EXCEPTION);
				response.setDesc("网络通信异常,请稍后再试");
				return response;
			}
			// 解析JSON字符串，并转换成response对象
			response = parseJsonResponse(httpResponse, response);
		} catch (Exception e) {
			setExceptionResponse(e, response);
		} finally {
			if (httpClient != null) {
				httpClient.shutdownHttpClient();
			}
		}
//		if(response instanceof XWeiboErrorRes){
//			
//		}
		if(response.getCode()==null){
			response.setCode(SUCCESS);
			response.setDesc("Success");
		}
		return response;
	}
	
	
	/**
	 * 解析httpRes中的JSON字符串，并转换成OutwardBaseRes对象
	 * @param httpRes HttpResponse实例
	 * @param res   具体的OutwardBaseRes实例
	 * @return OutwardBaseRes 成功，则返回OutwardBaseRes具体实例；但当HttpResponse中JSON为错误信息时，则返回错误应答实例，如{@link XWeiboErrorRes}
	 * @throws Exception
	 */
	public static OutwardBaseRes parseJsonResponse(HttpResponse httpRes,OutwardBaseRes res) 
			throws Exception {
		// 处理JSON返回结果
		return parseJsonResponse(EntityUtils.toString(httpRes.getEntity(),"UTF-8"), res);  
	}
	
	
	/**
	 * 解析JSON字符串，并转换成OutwardBaseRes对象
	 * @param jsonStr jsonStr
	 * @param res   具体的OutwardBaseRes实例
	 * @return OutwardBaseRes 成功，则返回OutwardBaseRes具体实例；但当HttpResponse中JSON为错误信息时，则返回错误应答实例，如{@link XWeiboErrorRes}
	 * @throws Exception
	 */
	public static OutwardBaseRes parseJsonResponse(String jsonStr,OutwardBaseRes res) 
			throws Exception {
		LogUtils.logd(LOG_TAG, "json str:" + jsonStr);
		//转换器   
        final GsonBuilder builder = new GsonBuilder(); 
        
        // 不转换没有 @Expose 注解的字段    
        builder.excludeFieldsWithoutExposeAnnotation();   
        final Gson gson = builder.create(); 

		//检查Json数据，返回的是否为错误信息。
		//新浪微博返回的错误信息存在"error_code"字段
    	if(res instanceof XWeiboBaseRes&&jsonStr.contains("error_code")){
    		return gson.fromJson(jsonStr, XWeiboErrorRes.class);
    	}
    	//获取腾讯用户ID错误。此时会存在"error"字段
    	else if(res instanceof QQGetOpenIdRes&&jsonStr.contains("error")){
    		final int headindex=jsonStr.indexOf('(');
    		final int tailindex=jsonStr.indexOf(')');
			return gson.fromJson(jsonStr.substring(headindex+1, tailindex), QQGetIdErrorRes.class);
		}
    	else if(res instanceof QQGetOpenIdRes){
    		final int headindex=jsonStr.indexOf('(');
    		final int tailindex=jsonStr.indexOf(')');

			return gson.fromJson(jsonStr.substring(headindex+1, tailindex), QQGetOpenIdRes.class);
		}
    	
        // 从JSON串转成JAVA对象
        return gson.fromJson(jsonStr, res.getClass());
	}
	
	
	/**
	 * 设置异常返回信息
	 * @param response
	 */
	private static void setExceptionResponse(Exception e, OutwardBaseRes response) {
		String result = null;
		String desc = null;
		
		if (e instanceof SocketException) { 
			/*
			 This SocketException may be thrown during socket creation or setting options, 
			 and is the superclass of all other socket related exceptions. 
			 */
			result = HTTP_SOKET_EXCEPTION;
			desc = "网络连接超时,请确定网络正常后重试";
		} else if (e instanceof SocketTimeoutException) {
			/*
			 This exception is thrown when a timeout expired on 
			 a socket read or accept operation. 
			 */
			result = HTTP_SOKET_TIMEOUT;
			desc = "网络通信超时,请确定网络正常后重试";
		} else if (e instanceof HttpResponseException) {
			result = HTTP_ERROR;
			HttpResponseException httpEx = (HttpResponseException) e;
			desc = "网络通信失败,请稍后再试";
			if(!(httpEx.getStatusCode()==-1)){
				desc = desc +"-" + httpEx.getStatusCode();
			}
			LogUtils.loge(LOG_TAG, e.getMessage());
		} else {
			result = SYSTEM_EXCEPTION; // 系统异常
			desc = "系统处理失败,请稍后再试";
			LogUtils.loge(LOG_TAG, e.getMessage());
		}

		response.setCode(result);
		response.setDesc(desc);
	}
	
	
}
