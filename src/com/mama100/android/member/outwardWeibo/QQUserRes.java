package com.mama100.android.member.outwardWeibo;

import com.google.gson.annotations.Expose;

/**
 * ret: 返回码
 * 
 * msg: 如果ret<0，会有相应的错误信息提示，返回数据全部用UTF-8编码
 * 
 * nickname: 昵称
 * 
 * figureurl: 大小为30×30像素的头像URL
 * 
 * figureurl_1: 大小为50×50像素的头像URL
 * 
 * figureurl_2: 大小为100×100像素的头像URL
 * 
 * gender: 性别。如果获取不到则默认返回“男”
 * 
 * vip: 标识用户是否为黄钻用户（0：不是；1：是）
 * 
 * level: 黄钻等级（如果是黄钻用户才返回此参数）
 * 
 * 
 * @author eCo
 * 
 */
public class QQUserRes	 extends QQBaseRes {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8444573415582760917L;


	@Expose
	private String nickname;

	@Expose
	private String figureurl;//figureurl: 大小为30×30像素的头像URL
	@Expose
	private String figureurl_1; //figureurl_1: 大小为50×50像素的头像URL
	@Expose
	private String figureurl_2; //figureurl_2: 大小为100×100像素的头像URL
	@Expose
	private String gender;//gender: 性别。如果获取不到则默认返回“男”
	@Expose
	private String vip;//vip: 标识用户是否为黄钻用户（0：不是；1：是）
	@Expose
	private String level; //level: 黄钻等级（如果是黄钻用户才返回此参数）

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * figureurl: 大小为30×30像素的头像URL
	 * @return
	 */
	public String getFigureurl() {
		return figureurl;
	}

	/**
	 * 50×50
	 * @param figureurl
	 */
	public void setFigureurl(String figureurl) {
		this.figureurl = figureurl;
	}

	public String getFigureurl_1() {
		return figureurl_1;
	}

	public void setFigureurl_1(String figureurl_1) {
		this.figureurl_1 = figureurl_1;
	}

	/**
	 * 100×100
	 * @return
	 */
	public String getFigureurl_2() {
		return figureurl_2;
	}

	public void setFigureurl_2(String figureurl_2) {
		this.figureurl_2 = figureurl_2;
	}

	/**
	 * gender: 性别。如果获取不到则默认返回“男”
	 * @return
	 */
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * vip: 标识用户是否为黄钻用户（0：不是；1：是）
	 * @return
	 */
	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	/**
	 * level: 黄钻等级（如果是黄钻用户才返回此参数）
	 * @return
	 */
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
