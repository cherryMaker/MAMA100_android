package com.mama100.android.member.bean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * @Description: 首页返回的微博内容
 * 
 * @author Mico
 * 
 * @date 2012-9-6 下午3:00:16
 */
public class WeiboShareContent implements Serializable {
	/**
	 * 主题
	 */
	@Expose
	private String theme;

	/**
	 * 微博@某人
	 */
	@Expose
	private List<String> atPerson = new ArrayList<String>();

	/**
	 * 固定内容
	 */
	@Expose
	private String content;


	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public List<String> getAtPerson() {
		return atPerson;
	}

	public void setAtPerson(List<String> atPerson) {
		this.atPerson = atPerson;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append("-----WeiBo Content start-------");
		sb.append("\n");
		sb.append("theme"+ theme + "\n");
		sb.append("@Person List as below: "+  "\n");
		for (String item : atPerson) {
			sb.append("person : "+  item + "\n");
		}
		sb.append("content - "+ content + "\n");
		sb.append("-----WeiBo Content end-------");
		return sb.toString();
		
	}
	

}
