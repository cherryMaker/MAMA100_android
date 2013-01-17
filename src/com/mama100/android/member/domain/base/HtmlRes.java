package com.mama100.android.member.domain.base;

import com.google.gson.annotations.Expose;

/**
 * 返回HTML的应答
 * @author jimmy
 */
public class HtmlRes extends BaseRes {

	/**
	 * html原始内容
	 */
	@Expose
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
