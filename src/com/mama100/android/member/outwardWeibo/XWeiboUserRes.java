package com.mama100.android.member.outwardWeibo;

import com.google.gson.annotations.Expose;


/**
 * 新浪微博用户基本信息应答类(解释自JSON)
 * @version 1.0 
 * @author ecoo
 */
public class XWeiboUserRes
extends XWeiboBaseRes{
	private static final long serialVersionUID = 1L;
	/**
	 * 用户UID
	 */
	@Expose
	private String id;
	/**
	 * 用户昵称
	 */
	@Expose
	private String screen_name;
	/**
	 * 用户性别
	 */
	@Expose
	private String gender;
	
	/**
	 * 用户头像地址
	 */
	@Expose
	private String profile_image_url;

	/**
	 * 用户所在地
	 */
	@Expose
	private String location;
	/**
	 * 用户描述
	 */
	private String description;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getScreen_name() {
		return screen_name;
	}
	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getProfile_image_url() {
		return profile_image_url;
	}
	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
