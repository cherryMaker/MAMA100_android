package com.mama100.android.member.bean.babyshop;

import com.google.gson.annotations.Expose;

/**
 * 母婴店详情接口的shop bean
 */
@SuppressWarnings("serial")
public class DetailedShopBean implements java.io.Serializable {

	// Fields
	@Expose
	private Long id;
	@Expose
	private String shopUserid;
	@Expose
	private String shopText;
	@Expose
	private String logoUrlS128S128;
	@Expose
	private String shopName;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShopUserid() {
		return this.shopUserid;
	}

	public void setShopUserid(String shopUserid) {
		this.shopUserid = shopUserid;
	}


	public String getShopText() {
		return this.shopText;
	}

	public void setShopText(String shopText) {
		this.shopText = shopText;
	}

	public String getLogoUrlS128S128() {
		return logoUrlS128S128;
	}

	public void setLogoUrlS128S128(String logoUrlS128S128) {
		this.logoUrlS128S128 = logoUrlS128S128;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}


}