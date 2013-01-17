package com.mama100.android.member.domain.babyshop;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.bean.babyshop.NearBabyShop;
import com.mama100.android.member.domain.base.BaseRes;

@SuppressWarnings("serial")
public class NearShopRes extends BaseRes{
	@Expose
	private int allRowsNo;
	@Expose
	List<NearBabyShop> list;

	public List<NearBabyShop> getList() {
		return list;
	}

	public void setList(List<NearBabyShop> list) {
		this.list = list;
	}

	public int getAllRowsNo() {
		return allRowsNo;
	}

	public void setAllRowsNo(int allRowsNo) {
		this.allRowsNo = allRowsNo;
	}
	
}
