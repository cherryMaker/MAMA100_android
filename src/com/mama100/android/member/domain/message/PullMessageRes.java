/**
 * 
 */
package com.mama100.android.member.domain.message;
import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * 
 * @Description: 一条消息对象, 用于后台服务推送时，从服务器获取
 * 
 * @author Mico
 * 
 * @date 2012-7-20 下午3:33:56
 */
public class PullMessageRes extends BaseRes {

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

}
