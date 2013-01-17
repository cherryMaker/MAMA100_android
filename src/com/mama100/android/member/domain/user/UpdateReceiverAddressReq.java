package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;


/**
 * Let's购 - 更新收货人地址
 * @author aihua.yan  2012-09-28
 */
	public class UpdateReceiverAddressReq extends BaseReq {
		
		/**
		 * 地址
		 */
		private String address;

		/**
		 * 收货人
		 */
		private String receiver;
		
	
		/**
		 * 省
		 */
		private String addressProvince;
		
		
		/**
		 * 市
		 */
		private String addressCity;;
	
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



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public String getReceiver() {
		return receiver;
	}



	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}



	public String getAddressProvince() {
		return addressProvince;
	}



	public void setAddressProvince(String addressProvince) {
		this.addressProvince = addressProvince;
	}



	public String getAddressCity() {
		return addressCity;
	}



	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}
}
