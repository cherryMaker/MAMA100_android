package com.mama100.android.member.domain.point;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * Let's购 - 用于在积分界面刚打开时或者下拉刷新时，去服务器获取一批积分信息
 * 
 * @author aihua 2012-08-03
 */
public class PointListReq extends BaseReq {

	
	/**
	 * 页面数
	 */
	private String  pageNo;
	/**
	 * 页面消息数
	 */
	private String  pageSize;
	
	
	/*
	 * 指定某个时间之前 , 时间格式 yyyy-MM-dd HH:mm:ss
	 */
	private String specTimeStr;

	/*
	 * 指定某个时间的开始时间 , 时间格式 yyyy-MM-dd HH:mm:ss
	 */
	private String startTimeStr;

	/*
	 * 指定某个时间的结束时间 , 时间格式 yyyy-MM-dd HH:mm:ss
	 */
	private String endTimeStr;
	
	

	public String getPageNo() {
		return pageNo;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getSpecTimeStr() {
		return specTimeStr;
	}

	public void setSpecTimeStr(String specTimeStr) {
		this.specTimeStr = specTimeStr;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {

		if (!super.validate(response)) {
			return false;
		}

		return true;
	}

}
