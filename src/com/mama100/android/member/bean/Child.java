package com.mama100.android.member.bean;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.util.StringUtils;

public class Child implements Serializable  {

	public static final String gender_boy = "001";

	public static final String gender_girl = "002";

	//宝宝对应id
	@Expose
	private String id;

	/**
	 * 孩子名字
	 */
	@Expose
	private String name;

	/**
	 * 孩子生日
	 */
	@Expose
	private String birthdate;
	
	/**
	 * 孩子生日 , 前台显示
	 */
	@Expose
	private String birthdateFront;
	

	/**
	 * 用户id
	 */
	@Expose
	private String customerId;

	/**
	 * 孩子性别代码
	 */
	@Expose
	private String genderCode;

	/**
	 * 孩子性别 , 前台显示
	 */
	@Expose
	private String genderFrontName;

	public String getId() {
		return id;
	}




	public void setId(String id) {
		this.id = id;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public String getBirthdate() {
		return birthdate;
	}




	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}




	public String getCustomerId() {
		return customerId;
	}




	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}




	public String getGenderCode() {
		return genderCode;
	}




	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}
	
	
	

	public String getBirthdateFront() {
		return birthdateFront;
	}

	public void setBirthdateFront(String birthdateFront) {
		this.birthdateFront = birthdateFront;
	}

	public String getGenderFrontName() {
		String genderFrontName = "暂无";

		if (StringUtils.isNotBlank(genderCode)) {
			if (genderCode.equals(Child.gender_boy)) {
				genderFrontName = "男";
			} else if (genderCode.equals(gender_girl)) {
				genderFrontName = "女";
			}

		}

		return genderFrontName;
	}

	public void setGenderFrontName(String genderFrontName) {
		this.genderFrontName = genderFrontName;
	}

	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(".............get Baby Info START....... \n");
		
		sb.append("babyId - " + id + "\n");
		sb.append("babyParentId - " + customerId + "\n");
		sb.append("babyName - " + name + "\n");
		sb.append("babyGenderCode - " + genderCode +"\n");
		sb.append("babySex - " + genderFrontName +"\n");
		sb.append("babyBirth - " + birthdate + "\n");
		sb.append("babyBirth simple - " + birthdateFront + "\n");
		sb.append(".............get Baby Info END....... \n");
		return sb.toString();
		
		
	}

}