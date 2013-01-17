package com.mama100.android.member.bean.babyshop;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 母婴店
 * @author eco
 *
 */
public class BabyShop {
	
	@Expose
	private String code;
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
	private String bossPhone;
	@Expose
	private String type;
	@Expose
	private String isExchange;
	@Expose
	private String isVip;
	@Expose
	private String isCard;
	@Expose
	private Long snsecClickRate;
	
	@Expose
	private List<OrderList> sideFormDetailList;
	 
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
	public String getIsExchange() {
		return isExchange;
	}
	public void setIsExchange(String isExchange) {
		this.isExchange = isExchange;
	}
	public String getIsVip() {
		return isVip;
	}
	public void setIsVip(String isVip) {
		this.isVip = isVip;
	}
	public String getIsCard() {
		return isCard;
	}
	public void setIsCard(String isCard) {
		this.isCard = isCard;
	}
	public Long getSnsecClickRate() {
		return snsecClickRate;
	}
	public void setSnsecClickRate(Long snsecClickRate) {
		this.snsecClickRate = snsecClickRate;
	}
	public List<OrderList> getSideFormDetailList() {
		return sideFormDetailList;
	}
	public void setSideFormDetailList(List<OrderList> sideFormDetailList) {
		this.sideFormDetailList = sideFormDetailList;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

}
