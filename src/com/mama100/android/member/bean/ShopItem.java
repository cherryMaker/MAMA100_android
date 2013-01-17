/**   
 * @Title: ShopItem.java 
 * @Package com.mama100.android.member.bean 
 * @Description: 积分模块 门店展示用到的门店选项
 * @author mama100   
 * @date 2012-11-21 上午10:44:16 
 * @version V1.0   
 */

package com.mama100.android.member.bean;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.util.LogUtils;

/**
 * @author liyang
 * 
 */
public class ShopItem {
	
	/**
	 * 是否已经选中
	 */
	private boolean isChecked;
	
	/**
	 * 门店名称
	 */
	@Expose
	private String shopName;
	
	
	/**
	 * 门店地址
	 */
	@Expose
	private String address;
	
	
	/**
	 * 门店距离
	 */
	@Expose
	private Double distance;
	
	/**
	 * 
	 */
	@Expose
	private String terminalCode;

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getDistance() {
		if(distance == null){
			distance = Double.valueOf(0);
		}
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getTerminalCode() {
		return terminalCode;
	}

	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if( !(o instanceof ShopItem)){
			return  false;
		}else{
			ShopItem shopItem = (ShopItem)o;
			LogUtils.logd(getClass(), "(this.shopName == shopItem.shopName)" +(this.shopName == shopItem.shopName));
			return this.shopName.equals(shopItem.shopName) ;
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return shopName;
	}
}
