package com.mama100.android.member.businesslayer;

import android.content.Context;

import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.point.DiyPointProductReq;
import com.mama100.android.member.domain.point.DiyPointProductRes;
import com.mama100.android.member.domain.point.ExchangeCodeStatusReq;
import com.mama100.android.member.domain.point.ExchangeCodeStatusRes;
import com.mama100.android.member.domain.point.GetExchangeCodeReq;
import com.mama100.android.member.domain.point.GetExchangeCodeRes;
import com.mama100.android.member.domain.point.PointListReq;
import com.mama100.android.member.domain.point.PointListRes;
import com.mama100.android.member.domain.point.PointRelativeShopReq;
import com.mama100.android.member.domain.point.PointRelativeShopRes;
import com.mama100.android.member.domain.point.PointSideShopReq;
import com.mama100.android.member.domain.point.PointSideShopRes;
import com.mama100.android.member.domain.point.PointSubmitReq;
import com.mama100.android.member.domain.point.PointSubmitRes;
import com.mama100.android.member.domain.point.PointVerifyReq;
import com.mama100.android.member.domain.point.PointVerifyRes;
import com.mama100.android.member.global.AppConstants;

/**
 * 积分相关的 
 * @author aihua.yan  2012-08-03
 */
public class PointProvider extends ClientDataSupport {
	private static PointProvider instance;
	private Context context;

	private PointProvider(Context context) {
		super(context);
		this.context = context;
	}

	public static synchronized PointProvider getInstance(Context context) {

		if (instance == null) {
			instance = new PointProvider(context);
		}

		return instance;
	}
	
	/***********************************************************
	 *   不需要走sso流程 的 接口
	 *************************************************************/
	
	
	/***********************************************************
	 *   走sso流程 的 接口
	 *************************************************************/
	/**
	 * 进入积分列表界面
	 */
	public BaseRes getPointList(PointListReq request) {
		String httpAddr = getHttpIpAddress() + AppConstants.GET_POINT_LIST_ACTION;
		PointListRes response = (PointListRes) postDataWithSsoCheck(request,
				PointListRes.class, httpAddr);
		return response;
	}
	
	
	
	/******
	 *  序列号，防伪码验证
	 */
	public BaseRes diyPointVerify(PointVerifyReq request){
		
		String httpAddr = getHttpIpAddress() + AppConstants.DIY_POINT_VERIFY_ACTION;
		PointVerifyRes response = (PointVerifyRes) postDataWithSsoCheck(request,
				PointVerifyRes.class, httpAddr);
		return response;
	}
	
	
	
	/******
	 *  防伪码验证 1.36版实现
	 *  added by liyang  2012-12-19
	 */
	public BaseRes diyPointVerifyByScan(DiyPointProductReq request){
		
		String httpAddr = getHttpIpAddress() + AppConstants.DIY_POINT_VERIFY_BY_SCAN_ACTION;

		DiyPointProductRes response = (DiyPointProductRes) postData(request,DiyPointProductRes.class, httpAddr);
		
		return response;
	}
	
	
	
	/******
	 *  获取附近门店 1.36版实现
	 *  added by liyang  2012-12-19
	 */
	public BaseRes getSideShop(PointSideShopReq request){
		String httpAddr = getHttpIpAddress() + AppConstants.DIY_POINT_GET_SIDE_SHOP_ACTION;
		PointSideShopRes response = (PointSideShopRes) postData(request,PointSideShopRes.class, httpAddr);
		return response;
	}
	
	
	
	/******
	 *  获取关注门店 1.36版实现
	 *  added by liyang  2012-12-19
	 */
	public BaseRes getRelativeShop(PointRelativeShopReq request){
		String httpAddr = getHttpIpAddress() + AppConstants.DIY_POINT_GET_RELATIVE_SHOP_ACTION;
		PointRelativeShopRes response = (PointRelativeShopRes) postDataWithSsoCheck(request,PointRelativeShopRes.class, httpAddr);
		return response;
	}
	
	
	
	
	/******
	 *  提交积分
	 */
	public BaseRes diyPointSubmit(PointSubmitReq request){
		String httpAddr = getHttpIpAddress() + AppConstants.DIY_POINT_SUBMIT_ACTION;
		PointSubmitRes response = (PointSubmitRes) postDataWithSsoCheck(request,
				PointSubmitRes.class, httpAddr);
		return response;
	}
	
	
	/***********************************************************
	 *   以下为2.1版本添加的接口
	 *************************************************************/
	
	/**
	 * 取兑换码
	 */
	public BaseRes getExchangeCode(final GetExchangeCodeReq request) {
		String httpAddr = getHttpIpAddress() + AppConstants.GET_EXCHANGE_CODE_ACTION;
		final GetExchangeCodeRes response = (GetExchangeCodeRes) postDataWithSsoCheck(request,
				GetExchangeCodeRes.class, httpAddr);
		return response;
	}
	
	/**
	 * 取兑换码的状态
	 */
	public BaseRes getExchangeCodeStatus(final ExchangeCodeStatusReq request) {
		String httpAddr = getHttpIpAddress() + AppConstants.GET_EXCHANGE_CODE_STATUS_ACTION;
		final ExchangeCodeStatusRes response = (ExchangeCodeStatusRes) postDataWithSsoCheck(request,
				ExchangeCodeStatusRes.class, httpAddr);
		return response;
	}
	
}
