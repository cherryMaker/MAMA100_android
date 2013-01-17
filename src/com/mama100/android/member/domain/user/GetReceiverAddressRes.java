package com.mama100.android.member.domain.user;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * 获取用户个人资料响应
 * 
 * @author aihua.yan 2012-08-03
 */
public class GetReceiverAddressRes extends BaseRes {

	/**
	 * 地址
	 */
	@Expose
	private String address;

	/**
	 * 收货人
	 */
	@Expose
	private String receiver;
	
	
	/**
	 * 省
	 */
	@Expose
	private String addressProvince;
	
	
	/**
	 * 市
	 */
	@Expose
	private String addressCity;
	
	

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(".............get Receiver Info START....... \n");
		sb.append("receiver - " + receiver + "\n");
		sb.append("address - " + address + "\n");
		sb.append("addressProvince - " + addressProvince + "\n");
		sb.append("addressCity - " + addressCity + "\n");
		sb.append("\n");
		sb.append(".............get Profile Info END....... \n");
		return sb.toString();
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
