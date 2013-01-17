package com.mama100.android.member.businesslayer;

import android.content.Context;
import android.util.Log;

import com.mama100.android.member.bean.MobResponseCode;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.base.HtmlRes;
import com.mama100.android.member.domain.message.DetailMessageReq;
import com.mama100.android.member.domain.message.MessageListReq;
import com.mama100.android.member.domain.message.MessageListRes;
import com.mama100.android.member.domain.message.PullMessageReq;
import com.mama100.android.member.domain.message.PullMessageRes;
import com.mama100.android.member.domain.sso.GetServiceTicketRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.MobValidateUtils;
import com.mama100.android.member.util.StorageUtils;
import com.mama100.android.member.util.StringUtils;

/**
 * 消息相关的
 * @author aihua  2012-08-03
 */
public class MessageProvider extends ClientDataSupport {
	private static MessageProvider instance;
	private Context context;
	private String TAG = this.getClass().getSimpleName();

	private MessageProvider(Context context) {
		super(context);
		this.context = context;
	}

	public static synchronized MessageProvider getInstance(Context context) {

		if (instance == null) {
			instance = new MessageProvider(context);
		}

		return instance;
	}
	
	/***********************************************************
	 *   不需要走sso流程 的 接口
	 *************************************************************/
	
	/**
	 * service在后台从服务器pull一条消息，然后显示在通知栏里面
	 */
	public BaseRes pullMessage(PullMessageReq request) {
		
		String httpAddr = getHttpIpAddress() + AppConstants.PULL_MESSAGE_ACTION;
		PullMessageRes response = (PullMessageRes) postData(request,
				PullMessageRes.class, httpAddr);
		return response;
	}
	
	
	/***********************************************************
	 *   走sso流程 的 接口
	 *************************************************************/

	public BaseRes getMesssageDetail(DetailMessageReq request) {
		
		String httpAddr = getHttpIpAddress() + AppConstants.GET_MESSAGE_DETAIL_ACTION;
		HtmlRes response = (HtmlRes) postDataWithSsoCheck(request,
				HtmlRes.class, httpAddr);
		return response;
	}
	
	public BaseRes getMesssageDetailFromNotificationBar(DetailMessageReq request) {
		BaseRes response = UserProvider.getInstance(context).autoLoginForDetailMessage();
		if(!response.getCode().equalsIgnoreCase(DeviceResponseCode.SUCCESS)){
			return response;
		}
		
		//如果自动登录成功
		String httpAddr = getHttpIpAddress() + AppConstants.GET_MESSAGE_DETAIL_ACTION;
		response = (HtmlRes) postDataWithTicket(request,
				HtmlRes.class, httpAddr,((GetServiceTicketRes)response).getTicket());
		
		
		//这种情况，其实用户已经自动登录了，就会沿用之前的username和mid,即使如果
		//BasicApplication 出异常的情况，也自动关闭进程了。。
//		if (response.getCode().equals(MobResponseCode.SUCCESS)) {
//			if(StringUtils.isBlank(BasicApplication.getInstance().getUsername())){
//			}
//			if(StringUtils.isBlank(BasicApplication.getInstance().getMid())){
//			}
//		}
		
		return response;
	}
	

	/**
	 * 进入消息列表界面，获取消息
	 */
	public BaseRes getMessageList(MessageListReq request) {
		String httpAddr = getHttpIpAddress() + AppConstants.GET_MESSAGE_LIST_ACTION;
		MessageListRes response = (MessageListRes) postDataWithSsoCheck(request,
				MessageListRes.class, httpAddr);
		
		LogUtils.logd("D","response = " + response);
		return response;
	}
}
