package com.mama100.android.member.domain.point;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * @Description: 获取关注门店的请求
 * @author created by liyang   
 * @date 2012-11-20 下午5:24:10 
 */
public class PointRelativeShopReq extends BaseReq {

	/**
	 * 经度
	 */
	private String longitude;

	/**
	 * 纬度
	 */
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

	@Override
	public boolean validate(BaseRes response) throws Exception {
		// TODO Auto-generated method stub
		if (!super.validate(response)) {
			return false;
		}

		return true;
	}
}
