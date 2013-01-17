package com.mama100.android.member.domain.babyshop;

import com.google.gson.annotations.Expose;


public class NearShopReq extends BabyShopBaseReq{
	/**
	 * 
	 */
	@Expose
	private int pageNo;
	/**
	 * 
	 */
	@Expose
	private int pageSize;
	
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	/**
	 * pageNo++
	 */
	public void pageNoPlusPlus(){
		pageNo++;
	}
	
	/**
	 * pageNo--
	 */
	public void pageNoDecrease(){
		pageNo--;
	}
	
}
