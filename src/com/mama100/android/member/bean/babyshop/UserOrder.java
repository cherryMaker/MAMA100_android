package com.mama100.android.member.bean.babyshop;

import com.google.gson.annotations.Expose;

/**
 * 订单
 * @author eco
 *
 */
public class UserOrder {
	@Expose
	private String customerId;
	@Expose
	private String imgUrl;
	@Expose
	private String productId;
	@Expose
	private String productName;
	@Expose
	private String points;
	@Expose
	private String terminalCode;
	@Expose
	private String createdTimestamp;
	@Expose
	private String controlType;
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getPoints() {
		return points;
	}
	public void setPoints(String points) {
		this.points = points;
	}
	public String getTerminalCode() {
		return terminalCode;
	}
	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}
	public String getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(String createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	public String getControlType() {
		return controlType;
	}
	public void setControlType(String controlType) {
		this.controlType = controlType;
	}
	
	public void setNullToAll(){
		customerId=null;
		productId=null;
		productName=null;
		points=null;
		terminalCode=null;
		createdTimestamp=null;
		controlType=null;
	}
	public String getImgUrl() {
		if(imgUrl!=null){
			imgUrl=imgUrl.replace("\\", "");
		}
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
}
