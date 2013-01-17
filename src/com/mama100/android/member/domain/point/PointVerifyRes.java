/**
 * 
 */
package com.mama100.android.member.domain.point;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * @Description: 自助积分验证应答
 * 
 * @author Mico
 * 
 * @date 2012-7-20 下午2:32:26
 */
public class PointVerifyRes extends BaseRes {

	/**
	 * 产品名称
	 */
	@Expose
	private String pname;

	/**
	 * 产品积分
	 */
	@Expose
	private String point;

	/**
	 * 经过本次积分后的余额
	 * 
	 * point balance
	 */
	private String pbalance;

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getPbalance() {
		return pbalance;
	}

	public void setPbalance(String pbalance) {
		this.pbalance = pbalance;
	}

}
