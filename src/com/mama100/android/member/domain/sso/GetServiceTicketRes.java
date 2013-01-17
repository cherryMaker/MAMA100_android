package com.mama100.android.member.domain.sso;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;


/**
 * 获取service ticket应答消息
 * @author Jimmy
 * 2012-7-28
 */
public class GetServiceTicketRes extends BaseRes {	
	
	/*
	 * sso server 生成的 service ticket
	 */
	@Expose
	private String ticket;
	
	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String toString() {
		
		String superSb = super.toString();
		
		StringBuffer sb = new StringBuffer(50);
		sb.append(superSb);
		sb.append("ticket: " + ticket);
		
		return sb.toString();
	}

}
