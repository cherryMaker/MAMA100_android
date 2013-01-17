/**
 * 
 */
package com.mama100.android.member.bean;

import com.google.gson.annotations.Expose;

/**
 * 
 * @Description: 消息对象
 * 
 * @author Mico
 * 
 * @date 2012-7-20 下午3:33:56
 */
public class MessageItem {

	/**
	 * 消息ID
	 */
	@Expose
	private String id;

	/**
	 * 标题
	 */
	@Expose
	private String title;

	/**
	 * 内容
	 */
	@Expose
	private String content;

	/**
	 * 日期
	 */
	@Expose
	private String date;

	/**
	 * 是否已读
	 * 
	 * 1-已读
	 * 
	 * -1-未读
	 */
	@Expose
	private String isRead;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getIsRead() {
		return isRead;
	}

	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

}
