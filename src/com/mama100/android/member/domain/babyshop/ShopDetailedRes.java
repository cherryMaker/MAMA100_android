package com.mama100.android.member.domain.babyshop;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.bean.babyshop.DetailedShopBean;
import com.mama100.android.member.bean.babyshop.UserOrder;
import com.mama100.android.member.domain.base.BaseRes;

@SuppressWarnings("serial")
public class ShopDetailedRes extends BaseRes{
	
	@Expose
	private DetailedShopBean multipleShop;
	
	@Expose
	private List<UserOrder> sideFormDetailResultList;

	public DetailedShopBean getMultipleShop() {
		return multipleShop;
	}

	public void setMultipleShop(DetailedShopBean multipleShop) {
		this.multipleShop = multipleShop;
	}

	public List<UserOrder> getSideFormDetailResultList() {
		return sideFormDetailResultList;
	}

	public void setSideFormDetailResultList(
			List<UserOrder> sideFormDetailResultList) {
		this.sideFormDetailResultList = sideFormDetailResultList;
	}
}
