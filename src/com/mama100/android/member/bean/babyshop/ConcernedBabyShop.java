package com.mama100.android.member.bean.babyshop;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 母婴店
 * @author eco
 *
 */
public class ConcernedBabyShop {
	
	@Expose
	private String code;
	
	@Expose
	private String terminalChannelCode;
	@Expose
	private String shopName;
	@Expose
	private String address;
	@Expose
	private String typeCode;
	@Expose
	private Date startDate;
	@Expose
	private Date endDate;
	@Expose
	private String phone;
	@Expose
	private String shopLogo;
	@Expose
	private String bossPhone;
	@Expose
	private String type;
	@Expose
	private boolean isExchange;
	@Expose
	private boolean isVip;
	@Expose
	private boolean isCard;
	@Expose
	private Long snsecClickRate;
	@Expose
	private String instance;
	@Expose
	private String longitude;
	@Expose
	private String latitude;
	
	@Expose
	private List<UserOrder> sideFormDetailList;
	 
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getBossPhone() {
		return bossPhone;
	}
	public void setBossPhone(String bossPhone) {
		this.bossPhone = bossPhone;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean getIsExchange() {
		return isExchange;
	}
	public void setIsExchange(boolean isExchange) {
		this.isExchange = isExchange;
	}
	public boolean getIsVip() {
		return isVip;
	}
	public void setIsVip(boolean isVip) {
		this.isVip = isVip;
	}
	public boolean getIsCard() {
		return isCard;
	}
	public void setIsCard(boolean isCard) {
		this.isCard = isCard;
	}
	public Long getSnsecClickRate() {
		return snsecClickRate;
	}
	public void setSnsecClickRate(Long snsecClickRate) {
		this.snsecClickRate = snsecClickRate;
	}
	public List<UserOrder> getSideFormDetailList() {
		return sideFormDetailList;
	}
	public void setSideFormDetailList(List<UserOrder> sideFormDetailList) {
		this.sideFormDetailList = sideFormDetailList;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
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
	/**
	 * 与当前位置的距离(原单词 distance)
	 * @return
	 */
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	
	/**
	 * 经纬度信息是否为空
	 * @return true 为空
	 */
	public boolean isLocationWasNull(){
		if(latitude==null||latitude.equals("")
				||longitude==null||longitude.equals(""))
				return true;
		return false;
	}
	public String getShopLogo() {
		return shopLogo;
	}
	public void setShopLogo(String shopLogo) {
		this.shopLogo = shopLogo;
	}
	
	public void clearMemory(){
		if (sideFormDetailList!=null) {
			sideFormDetailList.clear();
			sideFormDetailList = null;
		}
	}
	public String getTerminalChannelCode() {
		return terminalChannelCode;
	}
	public void setTerminalChannelCode(String terminalChannelCode) {
		this.terminalChannelCode = terminalChannelCode;
	}

}
