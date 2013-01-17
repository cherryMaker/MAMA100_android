package com.mama100.android.member.domain.sso;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;


/**
 * SSO登录获取TGT的应答消息
 * @author Jimmy
 * 2012-7-28
 */
public class GetTgtRes extends BaseRes {

	/*
	 * sso server 生成的 tgt
	 */
	@Expose
	private String tgt;

	public String getTgt() {
		return tgt;
	}

	public void setTgt(String tgt) {
		this.tgt = tgt;
	}
	
	public String toString() {
		
		String superSb = super.toString();
		
		StringBuffer sb = new StringBuffer(50);
		sb.append(superSb);
		sb.append("tgt: " + tgt);
		
		return sb.toString();
	}
	
	
}
