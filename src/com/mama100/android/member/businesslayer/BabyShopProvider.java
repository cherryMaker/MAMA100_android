package com.mama100.android.member.businesslayer;

import org.apache.http.HttpResponse;

import com.mama100.android.member.bean.MobResponseCode;
import com.mama100.android.member.domain.babyshop.ConcernedShopReq;
import com.mama100.android.member.domain.babyshop.ConcernedShopRes;
import com.mama100.android.member.domain.babyshop.NearShopReq;
import com.mama100.android.member.domain.babyshop.NearShopRes;
import com.mama100.android.member.domain.babyshop.ShopDetailedReq;
import com.mama100.android.member.domain.babyshop.ShopDetailedRes;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.FeedbackReq;
import com.mama100.android.member.domain.user.LogoutRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.http.CoreHttpClient;
import com.mama100.android.member.util.StringUtils;

import android.content.Context;

/**
 * 母婴店相关
 * @author eco
 *
 */
public class BabyShopProvider extends ClientDataSupport {

	private static BabyShopProvider instance;

	
	private BabyShopProvider(Context context) {
		super(context);
	}
	
	public static synchronized BabyShopProvider getInstance(Context context) {

		if (instance == null) {
			instance = new BabyShopProvider(context);
		}
		return instance;
	}
	
	
	/**
	 * 关注的母婴店
	 */
	public ConcernedShopRes getConcernedShop(ConcernedShopReq request) {

		String httpAddr = getHttpIpAddress() + AppConstants.USER_CONCERNED_SHOP;
		ConcernedShopRes response = (ConcernedShopRes) postDataWithSsoCheck(request,
				ConcernedShopRes.class, httpAddr);
		return response;
	}
	
	/**
	 * 附近的母婴店
	 */
	public NearShopRes getNearShop(NearShopReq request) {

		String httpAddr = getHttpIpAddress() + AppConstants.USER_NEAR_SHOP;
		NearShopRes response = (NearShopRes) postData(request,
				NearShopRes.class, httpAddr);
		return response;
	}
	
	/**
	 * 获取母婴店详情(登陆状态)
	 */
	public ShopDetailedRes getShopDetailed(ShopDetailedReq request) {
		
		String httpAddr = getHttpIpAddress() + AppConstants.SHOP_DETAIL_LOGIN;
		ShopDetailedRes response = (ShopDetailedRes) postDataWithSsoCheck(request,
				ShopDetailedRes.class, httpAddr);
		return response;
	}
	
	/**
	 * 获取母婴店详情(未登陆状态)
	 */
	public ShopDetailedRes getShopDetailedForUnlogin(ShopDetailedReq request) {
		
		String httpAddr = getHttpIpAddress() + AppConstants.SHOP_DETAIL_NOT_LOGIN;
		ShopDetailedRes response = (ShopDetailedRes) postData(request,
				ShopDetailedRes.class, httpAddr);
		return response;
	}
	
	

}
