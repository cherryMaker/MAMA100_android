package com.mama100.android.member.domain.user;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.bean.Child;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * 获取用户个人资料响应
 * 
 * @author aihua.yan 2012-08-03
 */
public class GetProfileRes extends BaseRes {

	/**
	 * 昵称
	 */
	@Expose
	private String nickname;
	/**
	 *宝宝信息
	 */
	@Expose
	private List<Child> childs;
	/**
	 * 个人图片
	 */
	@Expose
	private String avatar;
	

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	

	

	public List<Child> getChilds() {
		return childs;
	}

	public void setChilds(List<Child> childs) {
		this.childs = childs;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(".............get Profile Info START....... \n");
		sb.append("avatar pic - " + avatar + "\n");
		sb.append("nickname - " + nickname + "\n");
		if(childs!=null){
		for (Child item : childs) {
			sb.append(item.toString());
		}}
		sb.append("\n");
		sb.append(".............get Profile Info END....... \n");
		return sb.toString();
	}
}
