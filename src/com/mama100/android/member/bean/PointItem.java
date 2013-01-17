/**
 * 
 */
package com.mama100.android.member.bean;

import com.google.gson.annotations.Expose;

/**
 * @Description: 积分对象
 * 
 * @author Mico
 * 
 * @date 2012-7-20 上午10:05:07
 */
public class PointItem {

	/**
	 * 积分详细ID
	 */
	@Expose
	private String id;

	/**
	 * 积分产品名称
	 */
	@Expose
	private String pname;

	/**
	 * 积分
	 */
	 @Expose
	private String point;

	/**
	 * 积分时间
	 */
	 @Expose
	private String date;

	/**
	 * 积分类别
	 * 
	 * 积分-1, 活动-2, 兑换-3
	 */
	 @Expose
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
