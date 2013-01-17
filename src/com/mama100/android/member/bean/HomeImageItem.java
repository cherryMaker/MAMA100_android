package com.mama100.android.member.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import android.view.ViewDebug.ExportedProperty;

/**
 * 
 */

/**
 * @Description:首页图片对象
 * 
 * @author Mico
 * 
 * @date 2012-8-27 下午6:06:11
 */
public class HomeImageItem implements Serializable {

	/**
	 * 图片路径
	 */
	@Expose
	private String imgUrl;

	/**
	 * 图片详细ID
	 */
	@Expose
	private String infoId;

	/**
	 * 图片链接地址：跳转到其它地方
	 */
	@Expose
	private String linkUrl;

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

}
