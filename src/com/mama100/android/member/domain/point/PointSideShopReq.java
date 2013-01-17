package com.mama100.android.member.domain.point;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * @Description: 获取附近门店的请求
 * @author created by liyang  
 * @date 2012-11-20 下午5:24:10
 */
public class PointSideShopReq extends BaseReq {

	/**
	 * 经度
	 */
	private String longitude;

	/**
	 * 纬度
	 */
	private String latitude;
	
	/**
	 *页码 
	 */
	private int pageNo;
	
	/**
	 * 每页显示的个数
	 */
	private int pageSize;
	
	

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

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
	 * 下一页
	 */
	public void toNextPage(){
		pageNo ++;
	}
	
	/**
	 * 前一页
	 */
	public void toPreviousPage(){
		pageNo ++;
	}
	
	@Override
	public boolean validate(BaseRes response) throws Exception {
		// TODO Auto-generated method stub
		if (!super.validate(response)) {
			return false;
		}

		return true;
	}
}
