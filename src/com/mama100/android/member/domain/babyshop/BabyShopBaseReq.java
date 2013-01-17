package com.mama100.android.member.domain.babyshop;

import com.mama100.android.member.domain.base.BaseReq;

public class BabyShopBaseReq extends BaseReq{
	
	//经度
	private String longitude;
	//纬度
	private String latitude;

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
	
	
	
}
