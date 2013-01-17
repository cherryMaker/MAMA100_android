package com.mama100.android.member.domain.babyshop;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.bean.babyshop.ConcernedBabyShop;
import com.mama100.android.member.domain.base.BaseRes;

@SuppressWarnings("serial")
public class ConcernedShopRes extends BaseRes{
	
	@Expose
	List<ConcernedBabyShop> list;

	public List<ConcernedBabyShop> getList() {
		return list;
	}

	public void setList(List<ConcernedBabyShop> list) {
		this.list = list;
	}
	
}
