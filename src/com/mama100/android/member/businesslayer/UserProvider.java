package com.mama100.android.member.businesslayer;

import java.io.File;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.mama100.android.member.bean.MobResponseCode;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.sso.GetServiceTicketReq;
import com.mama100.android.member.domain.sso.GetServiceTicketRes;
import com.mama100.android.member.domain.sso.GetTgtRes;
import com.mama100.android.member.domain.sso.LoginReq;
import com.mama100.android.member.domain.sys.GetUnloginHomePicReq;
import com.mama100.android.member.domain.sys.HomeReq;
import com.mama100.android.member.domain.sys.HomeRes;
import com.mama100.android.member.domain.sys.RefreshHomeRes;
import com.mama100.android.member.domain.user.ChangePwdReq;
import com.mama100.android.member.domain.user.CrmMemberLoginReq;
import com.mama100.android.member.domain.user.CrmMemberLoginRes;
import com.mama100.android.member.domain.user.FeedbackReq;
import com.mama100.android.member.domain.user.FindPwdReq;
import com.mama100.android.member.domain.user.GetProfileRes;
import com.mama100.android.member.domain.user.GetReceiverAddressRes;
import com.mama100.android.member.domain.user.GetVerifyCodeReq;
import com.mama100.android.member.domain.user.LoginByThirdPartyUserReq;
import com.mama100.android.member.domain.user.LoginByThirdPartyUserRes;
import com.mama100.android.member.domain.user.LogoutRes;
import com.mama100.android.member.domain.user.RegisterReq;
import com.mama100.android.member.domain.user.RegisterRes;
import com.mama100.android.member.domain.user.ShareNotificationReq;
import com.mama100.android.member.domain.user.ShareNotificationRes;
import com.mama100.android.member.domain.user.UpdateBabyInfoReq;
import com.mama100.android.member.domain.user.UpdateBabyInfoRes;
import com.mama100.android.member.domain.user.UpdateProfileReq;
import com.mama100.android.member.domain.user.UpdateReceiverAddressReq;
import com.mama100.android.member.domain.user.UploadLogFileReq;
import com.mama100.android.member.domain.user.UserActivateECardReq;
import com.mama100.android.member.domain.user.UserActivateECardRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.http.CoreHttpClient;
import com.mama100.android.member.util.DesUtils;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.StorageUtils;
import com.mama100.android.member.util.StringUtils;

public class UserProvider extends ClientDataSupport {
	private static UserProvider instance;
	private Context context;
	private String TAG = this.getClass().getSimpleName();

	private UserProvider(Context context) {
		super(context);
		this.context = context;
	}

	public static synchronized UserProvider getInstance(Context context) {

		if (instance == null) {
			instance = new UserProvider(context);
		}

		return instance;
	}

	/***********************************************************
	 * 不需要走sso流程 的 接口
	 *************************************************************/
	/**
	 * 注册
	 */
	@Deprecated
	public BaseRes register(RegisterReq request) {

		String httpAddr = getHttpIpAddress() + AppConstants.REGISTER_ACTION;
		RegisterRes response = (RegisterRes) postData(request,
				RegisterRes.class, httpAddr);
		return response;
	}

	/**
	 * 获取验证码，用于 “找回秘密” 或者 “注册”界面， 获取验证码
	 */
	public BaseRes getVerifyCode(GetVerifyCodeReq request) {

		String httpAddr = getHttpIpAddress() + AppConstants.VERIFY_CODE_ACTION;
		BaseRes response = (BaseRes) postData(request, BaseRes.class, httpAddr);
		return response;
	}

	/***
	 * 找回密码
	 */
	public BaseRes getPassword(FindPwdReq request) {

		String httpAddr = getHttpIpAddress() + AppConstants.GET_PWD_ACTION;
		BaseRes response = (BaseRes) postData(request, BaseRes.class, httpAddr);
		return response;
	}
	
	/***
	 * 未登录情况下，主页获取图片
	 */
	public BaseRes getActPictures(GetUnloginHomePicReq request) {
		String httpAddr = getHttpIpAddress() + AppConstants.GET_UNLOGIN_PICS;
		HomeRes response = (HomeRes) postData(request, HomeRes.class, httpAddr);
		return response;
	}

	/***********************************************************
	 * 走sso流程 的 接口
	 *************************************************************/

	// >>>>>>>>>>>五种登录： 首次登录， 自动登录， 注册登录，第三方登录，会员手机登录   START<<<<<<<<<<<<<

	// >>>> 1 , 首次登录
	public BaseRes firstLogin(LoginReq login_req) {

//		String name = login_req.getUsername();
		BaseRes response = new BaseRes();

		/****************************************
		 * 获取并保存tgt至本地
		 *****************************************/
		response = generateTgt(login_req);
		if (!response.getCode().equals(MobResponseCode.SUCCESS)) {
			return response;
		}
		/****************************************
		 * 本地获取tgt,然后用该tgt+httpaddress, 获取st
		 *****************************************/
		String httpAddr = getHttpIpAddress() + AppConstants.HOME_ACTION;
		response = generateST(httpAddr);
		if (!response.getCode().equals(MobResponseCode.SUCCESS)) {
			return response;
		}

		return response;
//		/********************************************
//		 * 第一次登录
//		 ********************************************/
//		return getHomepageValue(response, httpAddr, name);
	}

	// >>> 2, ”记住我“前提下的自动登录
	public BaseRes autoLogin() {

		BaseRes response = new BaseRes();
		/****************************************
		 * 本地获取tgt,然后用该tgt+httpaddress, 获取st
		 *****************************************/
		String httpAddr = getHttpIpAddress() + AppConstants.HOME_ACTION;
		response = generateST(httpAddr);
		
		//如果响应不是成功的情况，而是其他多种情况中一种（比如tgt无效，或系统异常），立即返回
		if (!response.getCode().equals(MobResponseCode.SUCCESS)) {
			return response;
		}

		return response;
		/********************************************
		 * 自动登录
		 ********************************************/
//		return getHomepageValue(response, httpAddr);
	}
	
	
	// >>> 2, ”消息通知栏“的自动登录
	public BaseRes autoLoginForDetailMessage() {
		
		BaseRes response = new BaseRes();
		/****************************************
		 * 本地获取tgt,然后用该tgt+httpaddress, 获取st
		 *****************************************/
		String httpAddr = getHttpIpAddress() + AppConstants.GET_MESSAGE_DETAIL_ACTION;
		response = generateST(httpAddr);
		
		//如果响应不是成功的情况，而是其他多种情况中一种（比如tgt无效，或系统异常），立即返回
		if (!response.getCode().equals(MobResponseCode.SUCCESS)) {
		}
		return response;
		/********************************************
		 * 自动登录
		 ********************************************/
	}
	
	// 3, 注册登录
	public BaseRes registerLoginByEmail(BaseReq registerReq) {
		BaseRes response = new BaseRes();
		
		String username = null;
		/********************************************
		 * 先注册
		 ********************************************/
		String httpAddr = getHttpIpAddress() + AppConstants.REGISTER_BY_EMAIL_ACTION;
		response = (RegisterRes) postData(registerReq, RegisterRes.class, httpAddr);
		
		
		if (!response.getCode().equals(MobResponseCode.SUCCESS)) {
			return response;
		}else if(response.getCode().equals(MobResponseCode.SUCCESS)){
			
			LogUtils.loge(TAG, response.toString());
			username = ((RegisterRes)response).getUsername();
			BasicApplication.getInstance().setUsername(username);
			
			// 加密保存用户名
			String encodedUsername = null;
			try {
				
				encodedUsername = DesUtils.encrypt(DesUtils.DES_COMMON_KEY, username);
				// 保存用户名
				StorageUtils.storeLoginAccountInClient(context, encodedUsername);
				
			} catch (Exception e) {
				LogUtils.loge(LOG_TAG, "用户名加密及保存本地出现问题");
				LogUtils.loge(LOG_TAG, e.getMessage());
			}
			
			//设置用户文件夹路径
			BasicApplication.getInstance().setAllFolderPath(((RegisterRes)response).getMid());
		}
		
		/****************************************
		 * 获取并保存tgt至本地
		 *****************************************/
		LoginReq request = new LoginReq();
		request.setPassword(((RegisterReq)registerReq).getPwd());
		request.setUsername(((RegisterReq)registerReq).getEmail());
		response = generateTgt(request);
		if (!response.getCode().equals(MobResponseCode.SUCCESS)) {
			return response;
		}
		/****************************************
		 * 本地获取tgt,然后用该tgt+httpaddress, 获取st
		 *****************************************/
		httpAddr = getHttpIpAddress() + AppConstants.HOME_ACTION;
		response = generateST(httpAddr);
		if (!response.getCode().equals(MobResponseCode.SUCCESS)) {
			return response;
		}
		
		/********************************************
		 * 注册登录， 先进编辑用户资料界面
		 ********************************************/
		// 弃用之前这种先加载再进入的模式
		
		// 不能用ssoCheck，因为这时还没有获取sessionId，只有用withTicket
//		response = (GetProfileRes) postDataWithTicket(request,
//				GetProfileRes.class, httpAddr,
//				((GetServiceTicketRes) response).getTicket());
		
		
		return response;
		
	}
	
	
	// >>>> 4 , 第三方登录
		public BaseRes loginByThirdParty(LoginByThirdPartyUserReq login_req) {
			
			//1，
			String addr = getHttpIpAddress() + AppConstants.THIRD_PARTY_USER_LOGIN_ACTION;
			LoginByThirdPartyUserRes login_response = (LoginByThirdPartyUserRes) postData(login_req, LoginByThirdPartyUserRes.class, addr);
			if (!login_response.getCode().equals(MobResponseCode.SUCCESS)) {
				return login_response;
			}

			//2，获取username
			LogUtils.loge("edwar",login_response.toString());
			String name = login_response.getUsername();
			String pwd = login_response.getPwd();
			BasicApplication.getInstance().setFirstLogin(login_response.getIsFirstLogin());
			LogUtils.loge("edwar", "name - " + name +" , pwd = " + pwd + "");

			/****************************************
			 * 获取并保存tgt至本地
			 *****************************************/
			LoginReq request = new LoginReq();
			request.setRememberMe("true");
			request.setUsername(name);
//			request.setPassword(name);
			request.setPassword(pwd);
			BaseRes response = new BaseRes();
			
			response = generateTgt(request);
			if (!response.getCode().equals(MobResponseCode.SUCCESS)) {
				return response;
			}
			/****************************************
			 * 本地获取tgt,然后用该tgt+httpaddress, 获取st
			 *****************************************/
			String httpAddr = getHttpIpAddress() + AppConstants.HOME_ACTION;
			response = generateST(httpAddr);
			if (!response.getCode().equals(MobResponseCode.SUCCESS)) {
				return response;
			}

			/********************************************
			 * 第一次登录
			 ********************************************/
			return response;
//			return getHomepageValue(response, httpAddr, name);
		}
		
		
		// >>>> 5 , CRM用户手机登录
		public BaseRes crmLogin(CrmMemberLoginReq request) {
			String httpAddr = getHttpIpAddress() + AppConstants.CRM_MEMBER_LOGIN_ACTION;
			BaseRes response = (BaseRes) postData(request, CrmMemberLoginRes.class, httpAddr);
			return response;
		}
		
		
		// >>>>>>>>>>>五种登录： 首次登录， 自动登录， 注册登录，第三方登录，会员手机登录  END<<<<<<<<<<<<<
		

	/**
	 * @param login_req
	 *            登录请求
	 * @return 生成tgt
	 */
	private BaseRes generateTgt(BaseReq request) {
		
		GetTgtRes response = getTgtFromSso(request);
		if (response.getCode().equals(MobResponseCode.SUCCESS)) {
			// 保存tgt
			BasicApplication.getInstance().setTgt(response.getTgt());
			StorageUtils.storeLoginTGTInClient(context, response.getTgt());
			LogUtils.logd(TAG, "tgt success - " + response.getTgt());
		}
		return response;
	}

	/**
	 * @param httpAddr
	 *            欲访问的路径
	 * @return 响应。空响应 或者 包含st的响应 生成st
	 */
	private BaseRes generateST(String httpAddr) {
		
		// 1，判断本地tgt是否为empty
		String tgt = BasicApplication.getInstance().getMyTgt();
		if (StringUtils.isBlank(tgt)) {
			BaseRes res = new BaseRes();
			res.setCode(MobResponseCode.NULL_TGT);
			res.setDesc("您的登录信息已丢失,请先退出后，再重新登录");
			return res;
		}

		GetServiceTicketReq getSTReq = new GetServiceTicketReq();
		getSTReq.setService(httpAddr);
		GetServiceTicketRes getSTRes = getServiceTicketFromSso(tgt, getSTReq);

		// 2,判断tgt是否过期或者无效
		// 服务端为无效TGT增加特定的错误代码, 客户端清除本地TGT
		if (getSTRes.getCode().equals(MobResponseCode.TGT_INVALID)) {
//			StorageUtils.removeShareValue(context, AppConstants.LOGIN_TGT);
			StorageUtils.storeLoginTGTInClient(context, "");
			BasicApplication.getInstance().setTgt("");
			if (Looper.myLooper() == null) {
				Looper.prepare();
			}
			// 客户端提示
			Toast.makeText(context, getSTRes.getDesc(), Toast.LENGTH_LONG)
					.show();
			Looper.loop();
			if (Looper.myLooper() != null) {
				Looper.myLooper().quit();
			}
			return getSTRes;
		}

		// 3,判断Code是否为Success
		if (getSTRes.getCode().equals(MobResponseCode.SUCCESS)) {
			LogUtils.logd(TAG, "st success - " + getSTRes.getTicket());
			BasicApplication.getInstance().setServiceTicket(
					getSTRes.getTicket());
		}
		return getSTRes;
	}
	
	/**
	 * @param request  刷新主页 
	 * @return 主页响应
	 * 用于回到主界面后的界面刷新
	 */
	public BaseRes refreshHomepageValue(HomeReq request) {
		
		String httpAddr = getHttpIpAddress() + AppConstants.REFRESH_HOMEPAGE_ACTION;
		BaseRes response = (RefreshHomeRes) postDataWithSsoCheck(request,
				RefreshHomeRes.class, httpAddr);
		return response;
	}
	
	
	/**
	 * @param request  打开主页请求 
	 * @return 主页响应
	 * 用于首次注册登录-->进个人界面-->回主页界面时用
	 * 或者
	 * 通知栏点击消息-->回消息列表-->回主页界面
	 */
//	public BaseRes getHomepageValue(HomeReq request) {
//		String httpAddr = getHttpIpAddress() + AppConstants.HOME_ACTION;
//		BaseRes response = (HomeRes) postDataWithSsoCheck(request,
//				HomeRes.class, httpAddr);
//		if (response.getCode().equals(MobResponseCode.SUCCESS)) {
//			String username = ((HomeRes)response).getUsername();
//			BasicApplication.getInstance().setUsername(username);
//			
//			
//			// 加密保存用户名
//			String encodedUsername = null;
//			try {
//				encodedUsername = DesUtils.encrypt(DesUtils.DES_COMMON_KEY, username);
//				// 保存用户名
//				StorageUtils.storeLoginAccountInClient(context, encodedUsername);
//
//			} catch (Exception e) {
//				LogUtils.loge(LOG_TAG, "用户名加密及保存本地出现问题");
//				LogUtils.loge(LOG_TAG, e.getMessage());
//			}
//			String mid = ((HomeRes)response).getMid();
//			//设置用户文件夹路径
//			BasicApplication.getInstance().setAllFolderPath(mid);
//		}
//		return response;
//	}


	/**
	 * @param response
	 *            GetServiceTicketRes,带有serviceTicket
	 * @param httpAddr
	 *            servicetTicket对应的已授权的页面路径
	 *            
	 *            自从HomeRes能返回username,这里@name, 这个变量已经没有什么用了，先留着
	 * @return
	 */
//	private BaseRes getHomepageValue(BaseRes res, String httpAddr, String name) {
//		httpAddr = getHttpIpAddress() + AppConstants.HOME_ACTION;
//		BaseRes response = (HomeRes) getHomepageValue(res, httpAddr);
//		return response;
//	}

	/**
	 * @param response
	 *            GetServiceTicketRes,带有serviceTicket
	 * @param httpAddr
	 *            servicetTicket对应的已授权的页面路径
	 * @return
	 */
//	private BaseRes getHomepageValue(BaseRes res, String httpAddr) {
//		HomeReq home_req = new HomeReq();
//		BaseRes response = (HomeRes) postDataWithTicket(home_req,
//				HomeRes.class, httpAddr,
//				((GetServiceTicketRes) res).getTicket());
//		
//		if (response.getCode().equals(MobResponseCode.SUCCESS)) {
//			
//			String username = ((HomeRes)response).getUsername();
//			BasicApplication.getInstance().setUsername(username);
//			
//			
//			// 加密保存用户名
//			String encodedUsername = null;
//			try {
//				encodedUsername = DesUtils.encrypt(DesUtils.DES_COMMON_KEY, username);
//				// 保存用户名
//				StorageUtils.storeLoginAccountInClient(context, encodedUsername);
//
//			} catch (Exception e) {
//				LogUtils.loge(LOG_TAG, "用户名加密及保存本地出现问题");
//				LogUtils.loge(LOG_TAG, e.getMessage());
//			}
//			String mid = ((HomeRes)response).getMid();
//			//设置用户文件夹路径
//			BasicApplication.getInstance().setAllFolderPath(mid);
//		}
//		return response;
//	}

	/**
	 * @param response
	 *            GetServiceTicketRes,带有serviceTicket
	 * @param httpAddr
	 *            servicetTicket对应的已授权的页面路径
	 * @return
	 */
	public BaseRes getHomepageValueWithTicket(HomeReq request) {
		
		String httpAddr = getHttpIpAddress() + AppConstants.HOME_ACTION;
		BaseRes response = new BaseRes();
		
		//如果SessionId为空，则证明首次登录，则调用ST;否则，为自动登录
		if(StringUtils.isBlank(BasicApplication.getInstance().getSessionId())){
			String serviceTicket = 	BasicApplication.getInstance().getServiceTicket();
			 response = (HomeRes) postDataWithTicket(request, HomeRes.class,
					httpAddr, serviceTicket);
		}else{
			//这个基本不会走吧。。因为首页是最基本的请求
			 response = (HomeRes) postDataWithSsoCheck(request, HomeRes.class,
					httpAddr);
		}
		return response;
	}
	


	/**
	 * 修改个人密码
	 */
	public BaseRes changePwd(ChangePwdReq request) {
		String httpAddr = getHttpIpAddress() + AppConstants.CHANGE_PWD_ACTION;
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				BaseRes.class, httpAddr);
		return response;
	}

	/**
	 * 获取个人资料
	 */
	public BaseRes getProfile(BaseReq request) {
		String httpAddr = getHttpIpAddress() + AppConstants.GET_PROFILE_ACTION;
		GetProfileRes response = (GetProfileRes) postDataWithSsoCheck(request,
				GetProfileRes.class, httpAddr);
		return response;
	}

	/**
	 * 更新个人资料
	 */
	public BaseRes updateProfile(UpdateProfileReq request, File file) {

		String httpAddr = getHttpIpAddress()
				+ AppConstants.UPDATE_PROFILE_ACTION;
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				BaseRes.class, httpAddr, file);
		
		LogUtils.logd("D", "update = " + response);
		
		return response;
	}
	
	/**
	 * 获取用户收货地址
	 */
	public BaseRes getReceiverAddress(BaseReq request) {
		String httpAddr = getHttpIpAddress() + AppConstants.GET_CRM_USER_RECEIVE_ADDRESS_INFO;
		GetReceiverAddressRes response = (GetReceiverAddressRes) postDataWithSsoCheck(request,
				GetReceiverAddressRes.class, httpAddr);
		
		LogUtils.logd("D", "get = " + response);
		
		return response;
	}
	
	/**
	 * 更新用户收货地址
	 */
	public BaseRes updateReceiverAddress(UpdateReceiverAddressReq request) {
		String httpAddr = getHttpIpAddress()
				+ AppConstants.UPDATE_CRM_USER_RECEIVE_ADDRESS_INFO;
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				BaseRes.class, httpAddr);
		return response;
	}
	
	/**
	 * 更新宝宝信息
	 */
	public UpdateBabyInfoRes updateBabyInfo(UpdateBabyInfoReq request) {
		String httpAddr = getHttpIpAddress()
				+ AppConstants.UPDATE_BABY_INFO;
		UpdateBabyInfoRes response = (UpdateBabyInfoRes) postDataWithSsoCheck(request,
				UpdateBabyInfoRes.class, httpAddr);
		return response;
	}
	
	
	
	
	
	/**
	 * 上传log文件
	 */
	public BaseRes uploadLogFile(UploadLogFileReq request, File file) {
		String httpAddr = getHttpIpAddress()
				+ AppConstants.UPLOAD_LOG_ACTION;
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				BaseRes.class, httpAddr, file);
		return response;
	}
	
	
	
	
	
	
	/**
	 * 用户注册-->个人资料 -->主界面
	 * 更新个人资料及打开主界面
	 */
	//TODO 更新个人资料及打开主界面
	public BaseRes updateProfileAndOpenHomepage(UpdateProfileReq request) {
		
		String httpAddr = getHttpIpAddress()
				+ AppConstants.UPDATE_PROFILE_ACTION;
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				BaseRes.class, httpAddr);
		
		return response;
	}

	/**
	 * 意见反馈
	 */
	public BaseRes uploadFeedback(FeedbackReq request) {

		String httpAddr = getHttpIpAddress() + AppConstants.FEEDBACK_ACTION;
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				BaseRes.class, httpAddr);
		return response;
	}
	
	
	//modified by edwar 2012-09-04
	public LogoutRes logout(String tgt) {
//		public LogoutRes logout() {
		LogoutRes response = new LogoutRes();
		if (StringUtils.isBlank(tgt)) {
			response.setCode(MobResponseCode.NULL_TGT);
			response.setDesc("您的登录信息已丢失,请退出软件后重新登录");
			return response;
		}

		// http client 核心类
		CoreHttpClient coreHttpClient = new CoreHttpClient();
		HttpResponse httpResponse = null;

		try {
			//测试sso的连接
			httpResponse = coreHttpClient.delete("http://"+BasicApplication.getInstance().getSsoIpAddr()
					+ AppConstants.TICKET_URL+ tgt);

			// 解析JSON字符串，并转换成response对象
			response = (LogoutRes) parseJsonResponse(httpResponse,
					response.getClass());

			/**************************************************
			 * 将公用判断加在这里
			 **************************************************/

			// 1, 判断response是否为空
			if (response == null) {
				response = new LogoutRes();
				response.setCode(MobResponseCode.NULL_RESPONSE);
				response.setDesc("服务器获取的响应为空");
				return response;
			}

			// 2,判断response的code 是否为空
			if (response.getCode() == null) {
				response.setCode(MobResponseCode.NULL_CODE);
				response.setDesc("服务器获取的响应代码为空");
			}
		} catch (Exception e) {
			setExceptionResponse(e, response);
		} finally {
			if (coreHttpClient != null) {
				coreHttpClient.shutdownHttpClient();
				coreHttpClient = null;
			}
		}
		return response;
	}
	
	
	/**
	 * 拍乐秀分享后，通知服务器
	 */
	public BaseRes shareNotification(ShareNotificationReq request) {

		String httpAddr = getHttpIpAddress() + AppConstants.SHARE_NOTIFI_ACTION;
		//测试地址
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				ShareNotificationRes.class, httpAddr);
		return response;
	}
	
	
	/**
	 * 妈网用户， 激活会员卡
	 */
	public BaseRes activateECard(UserActivateECardReq request) {
		
		String httpAddr = getHttpIpAddress() + AppConstants.ACTIVATE_ECARD_ACTION;
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				UserActivateECardRes.class, httpAddr);
		return response;
	}
	
	
	/**
	 * 绑定第三方
	 */
	public BaseRes bindAction(LoginByThirdPartyUserReq request) {
		
		String httpAddr = getHttpIpAddress() + AppConstants.BINDING_ACTION;
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				BaseRes.class, httpAddr);
		return response;
	}
	
	
	/**
	 * 解绑定第三方
	 */
	public BaseRes unbindAction(BaseReq request) {
		
		String httpAddr = getHttpIpAddress() + AppConstants.UNBINDING_ACTION;
		BaseRes response = (BaseRes) postDataWithSsoCheck(request,
				BaseRes.class, httpAddr);
		return response;
	}
	
	
	
}
