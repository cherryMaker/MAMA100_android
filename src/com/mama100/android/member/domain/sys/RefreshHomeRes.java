package com.mama100.android.member.domain.sys;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * let's购 主界面 刷新界面应答
 * 
 * @author aihua.yan 2012-07-18
 */
public class RefreshHomeRes extends BaseRes {

	
	
	/**
	 * 用户头像路径
	 */
	@Expose
	private String avatar;

	/**
	 * 用户昵称
	 */
	@Expose
	private String nick;

	/**
	 * 用户积分余额
	 */
	@Expose
	private String point;

	/**
	 * 未读消息个数
	 */
	@Expose
	private String count;
	
	
	
	/**
	 * 新浪key
	 */
	@Expose
	private String sina_key;
	
	/**
	 * qq微博key
	 */
	@Expose
	private String qqweibo_key;
	
	/**
	 * qqkey
	 */
	@Expose
	private String qq_key;
	
	
	
	
	public String getSina_key() {
		return sina_key;
	}





	public void setSina_key(String sina_key) {
		this.sina_key = sina_key;
	}





	public String getQqweibo_key() {
		return qqweibo_key;
	}





	public void setQqweibo_key(String qqweibo_key) {
		this.qqweibo_key = qqweibo_key;
	}





	public String getQq_key() {
		return qq_key;
	}





	public void setQq_key(String qq_key) {
		this.qq_key = qq_key;
	}





	public String getAvatar() {
		return avatar;
	}





	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}





	public String getNick() {
		return nick;
	}





	public void setNick(String nick) {
		this.nick = nick;
	}





	public String getPoint() {
		return point;
	}





	public void setPoint(String point) {
		this.point = point;
	}





	public String getCount() {
		return count;
	}





	public void setCount(String count) {
		this.count = count;
	}





	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(".............Refresh HomePage Info START....... \n");
//		sb.append(".............1 - ad pic urls....... \n");
//		if(imgItems!=null&&imgItems.size()!=0){
//		for (int i = 0; i < imgItems.size(); i++) {
//		sb.append("imgUrl - " + imgItems.get(i).getImgUrl()+"\n");	
//		sb.append("imgLinkUrl - " + imgItems.get(i).getLinkUrl()+"\n");	
//		sb.append("imgId - " + imgItems.get(i).getInfoId()+"\n");	
//		}}
//		sb.append(".............2 - avatar pic urls....... \n");
		sb.append("avatar - " + avatar + "\n");
		sb.append("nickname - " + nick +"\n");
		sb.append("balance point - " + point +"\n");
		sb.append("message count - " + count +"\n");
		sb.append("\n");
		sb.append(".............get HomePage Info END....... \n");
		return sb.toString();
	}



}
