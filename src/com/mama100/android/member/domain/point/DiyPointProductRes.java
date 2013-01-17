package com.mama100.android.member.domain.point;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

/**
* @Description: 返回产品相关信息的应答类
* @author created by liyang    
* @date 2012-11-20 下午2:40:38 
*/
public class DiyPointProductRes extends BaseRes {
	
	
	/**
	 * 商品id
	 */
	@Expose
	private String productId;

	
	/**
	 * 商品名称
	 */
	@Expose
	private String productName;

	/**
	 * 商品图片
	 */
	@Expose
	private String productImgUrl;

	/**
	 * 商品积分
	 * 
	 */
	@Expose
	private String serial;
	
	
	/**
	 * 商品序列号
	 * 
	 */
	@Expose
	private String point;

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

	public String getProductImgUrl() {
		return productImgUrl;
	}

	public void setProductImgUrl(String productImgUrl) {
		this.productImgUrl = productImgUrl;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	
	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
}
