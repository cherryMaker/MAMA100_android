package com.mama100.android.member.domain.point;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.bean.ShopItem;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * @Description: 返回附近门店信息的应答类
 * @author created by liyang  
 * @date 2012-11-20 下午5:20:19 
 */
public class PointSideShopRes extends BaseRes {

	@Expose
	private int allRowsNo;

	@Expose
	List<ShopItem> list;

	public List<ShopItem> getList() {
		return list;
	}

	public void setList(List<ShopItem> list) {
		this.list = list;
	}

	public int getAllRowsNo() {
		return allRowsNo;
	}

	public void setAllRowsNo(int allRowsNo) {
		this.allRowsNo = allRowsNo;
	}

}
