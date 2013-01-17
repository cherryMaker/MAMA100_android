package com.mama100.android.member.outwardHttp;

import java.io.Serializable;

/**
 * 
 * <b>Description:</b> 处理对外Http请求（新浪微博等），解释HttpResponse的应答基类
 * @version 1.0 
 * @author ecoo
 */
public class OutwardBaseRes implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public OutwardBaseRes(){

	}
	
	/**
	 * 处理结果代码<br>
	 * 100 - 成功,注意，这里只代表成功返回了JSON数据（JSON数据可能告诉我们，access_token已失效）
	 */
	private String code;
	
	/**
	 * 结果描述<br>
	 */
	private String desc;

	
	/**
	 * 
	 * @param code 100 - 成功。注意，这里只代表成功返回了JSON数据（JSON数据可能告诉我们，access_token已失效）
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 
	 * @param code 100 - 成功,注意，这里只代表成功返回了JSON数据（JSON数据可能告诉我们，access_token已失效）
	 */
	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
