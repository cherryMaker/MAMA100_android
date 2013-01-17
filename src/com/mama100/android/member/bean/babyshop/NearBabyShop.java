package com.mama100.android.member.bean.babyshop;

import com.google.gson.annotations.Expose;

public class NearBabyShop {
	@Expose
	private String terminalCode;
	@Expose
	private String terminalChannelCode;
	
	@Expose
	private double longitude;
	@Expose
	private double latitude;
	@Expose
	private String accuracy;
	@Expose
	private String createdBy;
	@Expose
	private String updatedBy;
	@Expose
	private String instance;
	@Expose
	private String address;
	@Expose
	private String name;
	@Expose
	private String shortName;
	@Expose
	private String phone;
	@Expose
	private String shopLogo;
	@Expose
	private String mobile;
	@Expose
	private boolean isCard;
	@Expose
	private boolean isVip;
	@Expose
	private boolean isExchange;
	@Expose
	private String snsecClickRate;
	
	
	public String getTerminalCode() {
		return terminalCode;
	}
	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public boolean getIsCard() {
		return isCard;
	}
	public void setIsCard(boolean isCard) {
		this.isCard = isCard;
	}
	public boolean getIsVip() {
		return isVip;
	}
	public void setIsVip(boolean isVip) {
		this.isVip = isVip;
	}
	public boolean getIsExchange() {
		return isExchange;
	}
	public void setIsExchange(boolean isExchange) {
		this.isExchange = isExchange;
	}
	public String getSnsecClickRate() {
		return snsecClickRate;
	}
	public void setSnsecClickRate(String snsecClickRate) {
		this.snsecClickRate = snsecClickRate;
	}
	public String getShopLogo() {
		// 将 ‘\’去掉
		if(shopLogo!=null){
			shopLogo=shopLogo.replace("\\","");
		}
		return shopLogo;
	}
	public void setShopLogo(String shopLogo) {
		this.shopLogo = shopLogo;
	}
	
	public String getTerminalChannelCode() {
		return terminalChannelCode;
	}
	public void setTerminalChannelCode(String terminalChannelCode) {
		this.terminalChannelCode = terminalChannelCode;
	}
	
}
