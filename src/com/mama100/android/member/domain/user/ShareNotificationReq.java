package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;

/**
 * 拍乐秀分享成功后，通知服务器的请求
 * @author eco
 *
 */
public class ShareNotificationReq extends BaseReq{
	
	
	private String type;
	private String content;
	
	/**
	 * 社区类型，如sina
	 * @return
	 */
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 分享的文字 
	 * @return
	 */
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
