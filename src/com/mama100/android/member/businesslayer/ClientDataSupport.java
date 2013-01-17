package com.mama100.android.member.businesslayer;

import java.io.File;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mama100.android.member.bean.MobResponseCode;
import com.mama100.android.member.domain.base.BasePropertyReq;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.sso.GetServiceTicketReq;
import com.mama100.android.member.domain.sso.GetServiceTicketRes;
import com.mama100.android.member.domain.sso.GetTgtRes;
import com.mama100.android.member.domain.sso.LoginReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.http.CoreHttpClient;
import com.mama100.android.member.http.HttpException;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.Md5Utils;
import com.mama100.android.member.util.StorageUtils;
import com.mama100.android.member.util.StringUtils;

/**
 * 客户端数据访问支持类
 * 
 * @author jimmy
 */
public abstract class ClientDataSupport {

	protected final String LOG_TAG = this.getClass().getSimpleName();

//	private static final String JSSESSION_ID = "jsessionid";
	private static final String JSSESSION_ID = "JSESSIONID";

	protected Context context;

	/**
	 * 登录已过期,请重新去SSO验证 
	 * 0-不需要 1-需要
	 */
	private String NEED_RELOGIN = "1";//需要重新登录验证

	protected ClientDataSupport(Context context) {
		this.context = context.getApplicationContext();
	}

	/**
	 * 获取HTTP地址,如：http://m.hyt.mama100.com
	 * 
	 * @return
	 */
	protected String getHttpIpAddress() {

		String url = "http://";
		url += BasicApplication.getInstance().getIpAddr();

		return url;
	}

	/**
	 * 获取SSO HTTP地址,如：http://m.hyt.mama100.com
	 * 
	 * @return
	 */
	protected String getSsoHttpIpAddress() {

		String url = "http://";
		url += BasicApplication.getInstance().getSsoIpAddr();

		return url;
	}

	
	/**
	 * 设置请求消息的基础参数
	 * 
	 * @param request
	 * @param global
	 */
	protected void setBaseRequestFields(BaseReq request, BasicApplication global) {

		String deviceId = global.getDevid();
		request.setDevid(deviceId);
		//added by edwar 2012-11-1, 基类请求增加流水号，且MD5编码
		String time = String.valueOf(System.currentTimeMillis());
		String tsno = Md5Utils.encryptTo16hex(deviceId+time);
		LogUtils.logi("MD5", "md5 tsno - " + tsno);
		request.setTsno(tsno);
		//added end
		
		if (!(request instanceof LoginReq)) {
		}

	}

	/**
	 * 设置请求消息的基础属性参数
	 * (较少用到)
	 * @param params
	 * @param global
	 */
	protected void setBaseRequestPropertyParams(Map<String, Object> params, BasicApplication global) {

		params.put("os", global.getOs());
		params.put("osver", global.getOsver());
		params.put("brand", global.getBrand());
		params.put("model", global.getModel());
		params.put("ver", global.getLocalVersion() + "");
		params.put("vernm", global.getLocalVersionName());
		params.put("width", global.getDisplayWidth());
		params.put("height", global.getDisplayHeight());
		params.put("dpi", global.getDpi() + "");

		// added by aihua.yan 2012-07-18
		params.put("iccid", global.getIccid() + "");
		
		// added by aihua.yan 2012-09-19 添加渠道编码
				params.put("channel", global.getChannel() + "");
	}

	/**
	 * 设置请求消息的手机基础属性参数
	 * 
	 * @param params
	 * @param global
	 */
	protected void setBaseRequestPropertyFields(BasePropertyReq request, BasicApplication global) {

		request.setOs(global.getOs());
		request.setOsver(global.getOsver());
		request.setBrand(global.getBrand());
		request.setModel(global.getModel());
		request.setVer(global.getLocalVersion() + "");
		request.setVernm(global.getLocalVersionName());
		request.setWidth(global.getDisplayWidth() + "");
		request.setHeight(global.getDisplayHeight() + "");
		request.setDpi(global.getDpi() + "");

		// added by aihua.yan 2012-07-18
		request.setIccid(global.getIccid() + "");
		
		// added by aihua.yan 2012-09-19 添加渠道编码
				request.setChannel(global.getChannel() + "");
		
		
	}

	/**
	 * 把对象中每个字段名和值放到参数map中
	 * 
	 * @param object
	 * @param params
	 */
	protected void buildParams(BaseReq request, Map<String, Object> params) {
		buildParams(request.getClass(), request, params);
	}

	/**
	 * 通过反射把对象中每个字段名和值放到参数map中
	 * 
	 * @param params
	 */
	private void buildParams(Class<? extends BaseReq> clz, Object object,
			Map<String, Object> params) {

		Field[] fields = clz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {

			Field fieldObj = fields[i];
			fieldObj.setAccessible(true);

			String field = fieldObj.getName();
			try {
				params.put(field, fields[i].get(object));
			} catch (Exception e) {
				LogUtils.loge(LOG_TAG, LogUtils.getStackTrace(e));
				return;
			}

		}

		Class superClz = clz.getSuperclass();
		if (superClz != null) {
			buildParams(superClz, object, params);
		}

	}

	/**
	 * 把对象中每个字段名和值放到List<BasicNameValuePair>中
	 * 
	 * @param object
	 * @param params
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	protected void buildParams(BaseReq request, List<BasicNameValuePair> params)
			throws Exception {
		buildParams(request.getClass(), request, params);
	}

	/**
	 * 通过反射把对象中每个字段名和值放到List<BasicNameValuePair>中
	 * 
	 * @param params
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void buildParams(Class<? extends BaseReq> clz, Object object,
			List<BasicNameValuePair> params) throws Exception {

		Field[] fields = clz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {

			Field fieldObj = fields[i];
			fieldObj.setAccessible(true);

			String field = fieldObj.getName();
			Object fieldVal = fields[i].get(object);
			
			if (fieldVal != null) {
				//不使用强制转换，而是使用valueOf，这样int字段也能转换成请求参数
				params.add(new BasicNameValuePair(field,String.valueOf(fieldVal)));
			}

		}

		Class superClz = clz.getSuperclass();
		if (superClz != null) {
			buildParams(superClz, object, params);
		}

	}

	/**
	 * 解析JSON字符串，并转换成response对象
	 * 
	 * @param httpRes
	 * @param clz
	 * @return
	 * @throws Exception
	 */
	protected BaseRes parseJsonResponse(HttpResponse httpRes, Class clz) throws Exception {

		// 处理JSON返回结果

		String jsonStr = EntityUtils.toString(httpRes.getEntity());
		StringBuilder sb = new StringBuilder();
		String[] jsonItems = jsonStr.split(","); 
		sb.append(" parseJsonResponse - start - " + "\n");
		for (String string : jsonItems) {
			sb.append("  " + string + "\n");
		}
		sb.append(" parseJsonResponse - end - " + "\n");
		
		LogUtils.logi(LOG_TAG, "json str:" + sb.toString());

		JSONObject jsonObject = new JSONObject(jsonStr);

		// 取json中response节点中的字串
		String responseStr = jsonObject.getString("response");

		// 转换器
		GsonBuilder builder = new GsonBuilder();

		// 不转换没有 @Expose 注解的字段
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();

		// 从JSON串转成JAVA对象
		BaseRes response = gson.fromJson(responseStr, clz);

		//added by edwar 2012-08-31 释放内存--start
		jsonObject = null;
		builder = null;
		//added by edwar 2012-08-31 释放内存 --end
		return response;
	}

	/**
	 * 根据对象转换成JSON字符串
	 * 
	 * @param obj
	 *            对象
	 * @return
	 */
	protected String formatBeanToJson(Object obj) {

		// 转换器
		GsonBuilder builder = new GsonBuilder();

		// 不转换没有 @Expose 注解的字段
		builder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = builder.create();
		
		//added by edwar 2012-08-31 释放内存--start
		builder = null;
		//added by edwar 2012-08-31 释放内存--end

		return gson.toJson(obj);
	}
	
	/**
	 * POST提交数据
	 * 
	 * @param request
	 *            请求对象
	 * @return 应答对象
	 */
	protected BaseRes postData(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr, File file) {
		return postDataWithCookies(request, clz, httpAddr, file, null);
	}

	/**
	 * POST提交数据
	 * 
	 * @param request
	 *            请求对象
	 * @return 应答对象
	 */
	protected BaseRes postData(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr) {
		return postDataWithCookies(request, clz, httpAddr, null);
	}
	
	/**
	 * 
	 * @param request
	 * @param clz
	 * @param httpAddr
	 * @param file
	 * @param cookies
	 * @return
	 */
	protected BaseRes postDataWithCookies(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr, File file, Map<String, String> cookies) {
		return postDataWithCookies(request, clz, httpAddr, file, cookies, false);
	}
	
	
	/**
	 * 
	 * @param request
	 * @param clz
	 * @param httpAddr
	 * @param file
	 * @param cookies
	 * @param isNeedSaveSessionId  是否需要更新保存sessionId
	 * @return
	 */
	protected BaseRes postDataWithCookies(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr, File file, Map<String, String> cookies, boolean isNeedSaveSessionId) {
//		/****************************************
//         *added by edwar, 解决总是 sso过期的问题。。 2012-11-1
//         ****************************************/
//         //是否是访问主页信息的请求
//		
//		boolean isFromHomeAction = (httpAddr.contains(AppConstants.HOME_ACTION))?true:false;
//		boolean isFromSsoCheck = true;//是否走sso流程的请求，默认是真
//		if(cookies==null || cookies.isEmpty()){
//			LogUtils.loge("sessionId", "fromSsoCheck - "+ "false , cookie is null or empty");
//			LogUtils.loge("sessionId", "httpAddr - "+ httpAddr);
//			isFromSsoCheck = false;
//		}
//		
//		//added end
		
		BaseRes response = null;
		CoreHttpClient coreHttpClient = null;

		try {
			response = clz.newInstance();

			// 设置request对象中的基础参数
			setBaseRequestFields(request,BasicApplication.getInstance());

			if (request instanceof BasePropertyReq) {
				BasePropertyReq propertyReq = (BasePropertyReq) request;
				// 设置请求消息的手机基础属性参数
				setBaseRequestPropertyFields(propertyReq, BasicApplication.getInstance());
			}

			// 验证request对象, 如果有错误, 将错误信息设置到response中
			if (!request.validate(response)) {
				return response;
			}

			// 把对象中每个字段名和值放到List<BasicNameValuePair>中
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			try {
				buildParams(request, params);
			} catch (Exception e) {
				response.setCode(DeviceResponseCode.SYSTEM_EXCEPTION);
				response.setDesc("请求数据转换失败,请稍后再试");
				return response;
			}

			
//			for (int i = 0; i < params.size(); i++) {
//				
//				BasicNameValuePair pair = params.get(i);
//				LogUtils.logd("D", "params Name= " + pair.getName());
//				LogUtils.logd("D", "params Value= " + pair.getValue());
//			}
			
			
			// http client 核心类
			coreHttpClient = new CoreHttpClient();
			HttpResponse httpResponse = null;
			
//			LogUtils.logi(AppConstants.PROGRESS_TAG, 
//					BasicApplication.getInstance().toApplicationInfoString());
//			LogUtils.logi(AppConstants.PROGRESS_TAG, 
//					BasicApplication.getInstance().toUserInfoString());

			// POST提交数据至服务器
			/*
			File file = null;
			if (request instanceof UpdateProfileReq&&BasicApplication.getInstance().isAvatarChanged()) {
				Uri uri = BasicApplication.getInstance().getUploadAvatarUri();
				file = new File(uri.getPath());
				httpResponse = coreHttpClient.post(httpAddr,file,params, cookies);
			} else {
				httpResponse = coreHttpClient.post(httpAddr, params, cookies);
			}
			*/
			
			if (file == null) {
				httpResponse = coreHttpClient.post(httpAddr, params, cookies);
			} else {
				httpResponse = coreHttpClient.post(httpAddr, file, params, cookies);
			}
			
			//added by edwar 2012-08-31 释放内存 --Start
//			coreHttpClient = null;
			if(file!=null){
				file = null;
			}
			if(params!=null&&!params.isEmpty()){
				params.clear();
				params = null;
			}
			//added by edwar 2012-08-31 释放内存 --end
			if (httpResponse == null) {
				LogUtils.loge(ClientDataSupport.class, "http response is null.");
				response.setCode(DeviceResponseCode.SYSTEM_EXCEPTION);
				response.setDesc("网络通信异常,请稍后再试");
				return response;
			}

			// 从服务端返回的response的cookie中获取jsessionid
			String sessionId = getSessionIdFromCookies(httpResponse);
			if (isNeedSaveSessionId
					&&StringUtils.isNotBlank(sessionId)
//					&&StringUtils.isBlank(BasicApplication.getInstance().getMySessionId())
					) {
				// 把jsessionid保存到客户端本地
				BasicApplication.getInstance().setSessionId(sessionId);
				StorageUtils.storeSessionId(context, sessionId);
			}

//			if (clz.isAssignableFrom(HtmlRes.class)) {//这个方法，clz是HtmlRes的同类或父类，将返回true.
//				if (HtmlRes.class.isAssignableFrom(clz)) {//这个方法，clz是HtmlRes的同类或子类，将返回true.
//				// 处理html返回数据
//				String htmlCtn = EntityUtils.toString(httpResponse.getEntity());
//				response = new HtmlRes();
//				((HtmlRes) response).setContent(htmlCtn);
//			} else {
				
				
				// 解析JSON字符串，并转换成response对象
				response = parseJsonResponse(httpResponse, response.getClass());
				
				/**************************************************
				 * 将公用判断加在这里
				 **************************************************/

				//1, 判断response是否为空
				if (response == null) {
					BaseRes res = new BaseRes();
					res.setCode(MobResponseCode.NULL_RESPONSE);
					res.setDesc("服务器获取的响应为空");
					return res;
				}
				
				//2,判断response的code 是否为空
				if (response.getCode() == null) {
					BaseRes res = new BaseRes();
					res.setCode(MobResponseCode.NULL_CODE);
					res.setDesc("服务器获取的响应代码为空");
					return res;
				}

//			}

		} catch (Exception e) {
			setExceptionResponse(e, response);
		} finally {
			if (coreHttpClient != null) {
				coreHttpClient.shutdownHttpClient();
			}
		}
		
		
		return response;		
	}

	/**
	 * POST提交数据, 并带上cookies
	 * 
	 * @param request
	 *            请求对象
	 * @param cookies
	 *            带上cookies
	 * @return 应答对象
	 */
	protected BaseRes postDataWithCookies(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr, Map<String, String> cookies) {
		return postDataWithCookies(request, clz, httpAddr, null, cookies);
	}

	/**
	 * SSO登录获取TGT
	 * 
	 * @param request
	 * @return
	 */
	protected GetTgtRes getTgtFromSso(BaseReq request) {
		String httpAddr = getSsoHttpIpAddress() +  AppConstants.TICKET_URL;
		GetTgtRes response = (GetTgtRes) postData(request, GetTgtRes.class, httpAddr);

		return response;
	}

	/**
	 * 根据TGT和service url获取service ticket
	 * 
	 * @param tgt
	 * @param request
	 * @return
	 */
	protected GetServiceTicketRes getServiceTicketFromSso(String tgt, GetServiceTicketReq request) {

		String httpAddr = getSsoHttpIpAddress() + AppConstants.TICKET_URL + tgt;
		GetServiceTicketRes response = (GetServiceTicketRes) postData(request,
				GetServiceTicketRes.class, httpAddr);

		return response;
	}

	/**
	 * POST提交数据，带上service ticket
	 * 登录成功后，第一次访问首页，调用此方法
	 * @param request
	 * @param clz
	 * @param httpAddr
	 * @param ticket
	 *            service ticket
	 * @return
	 */
	protected BaseRes postDataWithTicket(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr, String ticket) {
		return postDataWithTicket(request, clz, httpAddr, null, ticket);
	}
	
	/**
	 * 
	 * @param request
	 * @param clz
	 * @param httpAddr
	 * @param file
	 * @param ticket
	 * @return
	 */
	protected BaseRes postDataWithTicket(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr, File file, String ticket) {
		
		if (httpAddr.indexOf("?") != -1) {
			httpAddr += "&"+AppConstants.TICKET+"=" + ticket;
		} else {
			httpAddr += "?"+AppConstants.TICKET+"=" + ticket;
		}
		return postDataWithCookies(request, clz, httpAddr, file, null, true);		
	}
	
	/**
	 * 登录之后,post数据都调用此方法,此方法会检查用户是否已经登录授权
	 * @param request
	 * @param clz
	 * @param httpAddr
	 * @param file 要上传的文件对象
	 * @return
	 */
	protected BaseRes postDataWithSsoCheck(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr, File file) {
		
		BaseRes response = null;

		try {
			response = clz.newInstance();
		} catch (Exception e) {
			setExceptionResponse(e, response);
			return response;
		}
		
		String sessionid = BasicApplication.getInstance().getMySessionId();
			

	
		
		if (StringUtils.isBlank(sessionid)) {

			// 保存在手机本地的session id已经丢失,需要重新去sso获取service ticket
			LogUtils.loge("sessionId", "client session lost, need retry sso check.");
			response = retrySsoCheck(request, clz, httpAddr, file);			

			return response;
		}

		Map<String, String> cookies = new HashMap<String, String>();
		cookies.put(JSSESSION_ID, sessionid);
		
		// POST提交数据, 并带上cookies
		response = postDataWithCookies(request, clz, httpAddr, file, cookies);
		
		// 检查response code 是否服务端有提示需要重新登录
		String needSsoLogin = response.getNeedSsoLogin();
		
		if (StringUtils.isNotBlank(needSsoLogin) && needSsoLogin.equals("1")) {			
			// 服务器session过期,重新去SSO验证		
			LogUtils.loge("sessionId", "server session lost, need retry sso check.");
			response = retrySsoCheck(request, clz, httpAddr, file);			
		}
		
		
		//added by edwar 2012-08-31 释放内存--start
		if(cookies!=null&&!cookies.isEmpty()){
			cookies.clear();
			cookies = null;
		}
		
		//added by edwar 2012-08-31 释放内存--end
		return response;		
	}
	

	/**
	 * 登录之后,post数据都调用此方法,此方法会检查用户是否已经登录授权
	 * @param request
	 * @param clz
	 * @param httpAddr
	 * @return
	 */
	protected BaseRes postDataWithSsoCheck(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr) {
		return postDataWithSsoCheck(request, clz, httpAddr, null);
	}
	
	private BaseRes retrySsoCheck(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr, File file) {
		

		/********************************************************
		 *  added by edwar 2012-09-20  清空本地sessionId - start
		 ******************************************************/
		BasicApplication.getInstance().setSessionId("");
		StorageUtils.storeSessionId(context, "");
		/********************************************************
		 *  added by edwar 2012-09-20  清空本地sessionId - end
		 ******************************************************/
		
		
		
		BaseRes response = null;

		try {
			response = clz.newInstance();
		} catch (Exception e) {
			setExceptionResponse(e, response);
			return response;
		}
		
		String tgt = BasicApplication.getInstance().getMyTgt();
		if(StringUtils.isBlank(tgt)){
			BaseRes res = new BaseRes();
			res.setCode(MobResponseCode.NULL_TGT);
			res.setDesc("您的登录信息已丢失,请退出后，再重新登录");
			return res;
		}
		
		GetServiceTicketReq getSTReq = new GetServiceTicketReq();
		getSTReq.setService(httpAddr);
		
		//  根据TGT和service url获取service ticket
		GetServiceTicketRes getSTRes = getServiceTicketFromSso(tgt, getSTReq);
		response.setCode(getSTRes.getCode());
		response.setDesc(getSTRes.getDesc());
		
		//added by edwar 2012-08-31 释放内存--start
		getSTReq = null;
		//added by edwar 2012-08-31 释放内存--end

		// 服务端为无效TGT增加特定的错误代码, 客户端清除本地TGT
		if (getSTRes.getCode().equals(MobResponseCode.TGT_INVALID)) {
//			StorageUtils.removeShareValue(context, AppConstants.LOGIN_TGT);
			StorageUtils.storeLoginTGTInClient(context, "");
			BasicApplication.getInstance().setTgt("");
			if(Looper.myLooper()==null){
				Looper.prepare();
			}
			//客户端提示
			Toast.makeText(context, getSTRes.getDesc(), Toast.LENGTH_LONG).show();
			Looper.loop();
			if (Looper.myLooper() != null) {
				Looper.myLooper().quit();
			}
			return response;
		}
		
		if (!getSTRes.getCode().equals(MobResponseCode.SUCCESS)) {			
			return response;
		}

		
		String st = getSTRes.getTicket();
		if (StringUtils.isBlank(st)) {
			response.setDesc("您的登录信息已过期,请先退出后再重新登录");
			return response;
		}
		
		// 访问之前未成功的URL，带上service ticket
		response = postDataWithTicket(request, clz, httpAddr, file, st);

		return response;
	}
	
	/**
	 * session丢失或过期时,需重新去SSO验证
	 * @param request
	 * @param clz
	 * @param httpAddr
	 * @return
	 */
	/*
	private BaseRes retrySsoCheck(BaseReq request, Class<? extends BaseRes> clz,
			String httpAddr) {
		
		BaseRes response = null;

		try {
			response = clz.newInstance();
		} catch (Exception e) {
			setExceptionResponse(e, response);
			return response;
		}
		
		String tgt = StorageUtils.getLoginTGT(context);
		if(StringUtils.isBlank(tgt)){
			BaseRes res = new BaseRes();
			res.setCode(MobResponseCode.NULL_TGT);
			res.setDesc("您的登录信息已丢失,请退出软件后重新登录");
			return res;
		}
		
		GetServiceTicketReq getSTReq = new GetServiceTicketReq();
		getSTReq.setService(httpAddr);
		
		//  根据TGT和service url获取service ticket
		GetServiceTicketRes getSTRes = getServiceTicketFromSso(tgt, getSTReq);
		response.setCode(getSTRes.getCode());
		response.setDesc(getSTRes.getDesc());
		
		//added by edwar 2012-08-31 释放内存--start
		getSTReq = null;
		//added by edwar 2012-08-31 释放内存--end

		// 服务端为无效TGT增加特定的错误代码, 客户端清除本地TGT
		if (getSTRes.getCode().equals(MobResponseCode.TGT_INVALID)) {
			StorageUtils.removeShareValue(context, AppConstants.LOGIN_TGT);
			BasicApplication.getInstance().setAutoLogin(false);
			if(Looper.myLooper()==null){
				Looper.prepare();
			}
			//客户端提示
			Toast.makeText(context, getSTRes.getDesc(), Toast.LENGTH_LONG).show();
			Looper.loop();
			if (Looper.myLooper() != null) {
				Looper.myLooper().quit();
			}
			return response;
		}
		
		if (!getSTRes.getCode().equals(MobResponseCode.SUCCESS)) {			
			return response;
		}

		
		String st = getSTRes.getTicket();
		if (StringUtils.isBlank(st)) {
			response.setDesc("您的登录信息已过期,请退出重新登录");
			return response;
		}
		
		// 访问之前未成功的URL，带上service ticket
		response = postDataWithTicket(request, clz, httpAddr, st);

		return response;
	}
	*/


	/**
	 * 从服务端返回的response的cookie中获取jsessionid
	 * 
	 * @param httpResponse
	 * @return
	 */
	private String getSessionIdFromCookies(HttpResponse httpResponse) {

		Header[] headers = httpResponse.getHeaders("Set-Cookie");
		if (headers == null) {
			return null;
		}

		for (int i = 0; i < headers.length; i++) {

			String cookie = headers[i].getValue();
			String[] cookievalues = cookie.split(";");
			for (int j = 0; j < cookievalues.length; j++) {

				String[] keyPair = cookievalues[j].split("=");
				String key = keyPair[0].trim();

				if (key.toUpperCase().equals(JSSESSION_ID)) {
					LogUtils.logd("UserProvider", " - Key: "+key+"");
					String value = keyPair.length > 1 ? keyPair[1].trim() : null;
					return value;
				}

			}

		}

		return null;
	}

	/**
	 * 设置异常返回信息
	 * 
	 * @param response
	 */
	protected void setExceptionResponse(Exception e, BaseRes response) {

		String result = null;
		String desc = null;

		if (e instanceof SocketException) {
			/*
			 * This SocketException may be thrown during socket creation or
			 * setting options, and is the superclass of all other socket
			 * related exceptions.
			 */
			result = DeviceResponseCode.HTTP_SOKET_EXCEPTION;
			desc = "网络连接超时,请确定网络正常后重试";
		} else if (e instanceof SocketTimeoutException) {
			/*
			 * This exception is thrown when a timeout expired on a socket read
			 * or accept operation.
			 */
			result = DeviceResponseCode.HTTP_SOKET_TIMEOUT;
			desc = "网络通信超时,请确定网络正常后重试";
		} else if (e instanceof HttpException) {
			result = DeviceResponseCode.HTTP_ERROR;
			HttpException httpEx = (HttpException) e;
			desc = "网络通信异常,请稍后再试";
			if (!(httpEx.getStatusCode() == -1)) {
				desc = desc + "-" + httpEx.getStatusCode();
			}
			LogUtils.loge(LOG_TAG, LogUtils.getStackTrace(e));
		} else {
			result = DeviceResponseCode.SYSTEM_EXCEPTION; // 系统异常
			desc = "系统处理异常,请稍后再试";
			LogUtils.loge(LOG_TAG, LogUtils.getStackTrace(e));
		}

		response.setCode(result);
		response.setDesc(desc);

	}

}
