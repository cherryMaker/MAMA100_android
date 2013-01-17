package com.mama100.android.member.domain.sys;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.bean.HomeImageItem;
import com.mama100.android.member.bean.ThirdPartyUser;
import com.mama100.android.member.bean.WeiboShareContent;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * let's购 登录应答
 * 
 * @author aihua.yan 2012-07-18
 */
public class HomeRes extends BaseRes {

	/**
	 *(全称：imageUrls)主页形象图片路径
	 */
	@Expose
	private List<HomeImageItem> imgItems;
	
	/**
	 *(全称:avatarUrl ) 主页用户头像路径
	 */
	@Expose
	private String avatar;

	
	/**
	 *(全称:nickname ) 主页用户昵称
	 */
	@Expose
	private String nick;

	/**
	 *(全称:pointscore )主页用户积分
	 */
	@Expose
	private String point;

	/**
	 *(全称:messagesCount )主页消息个数
	 */
	@Expose
	private String msgCount;
	
	
	/**************** 新增 *****************/
	
	/**
	 *用户名
	 */
	@Expose
	private String username;
	
	
	/**
	 *用户ID
	 */
	@Expose
	private String mid;
	
	
	
	/**
	 * 微博分享的文字
	 */
	@Expose
	private WeiboShareContent weiboShareContent;

	/**
	 * 微博帐号集合
	 */
	@Expose
	private List<ThirdPartyUser> thirdPartyUsers;
	
	/**
	 * (0-未完善 1-完善)
	 */
	@Expose
	private String customerInfoCompleted;
	
	/**
	 * 是否关联 0=默认,1=是
	 */
	@Expose
	private String isAsso;
	
	
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
	
	/**
	 * mobile
	 */
	@Expose
	private String mobile;
	
	/**
	 *  bid 合生元CRM会员id
	 */
	@Expose
	private String bid;
	
	

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
	
	
	
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getIsAsso() {
		return isAsso;
	}

	public void setIsAsso(String isAsso) {
		this.isAsso = isAsso;
	}

	public List<HomeImageItem> getImgItems() {
		return imgItems;
	}

	public void setImgItems(List<HomeImageItem> imgItems) {
		this.imgItems = imgItems;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	
	public String getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(String msgCount) {
		this.msgCount = msgCount;
	}
	
	

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

	public String toString(){
		
		StringBuilder sb = new StringBuilder();
		sb.append(".............get HomePage Info START....... \n");
		sb.append(".............1 - ad pic urls....... \n");
		if(imgItems!=null&&imgItems.size()!=0){
		for (int i = 0; i < imgItems.size(); i++) {
		sb.append("imgUrl - " + imgItems.get(i).getImgUrl()+"\n");	
		sb.append("imgLinkUrl - " + imgItems.get(i).getLinkUrl()+"\n");	
		sb.append("imgId - " + imgItems.get(i).getInfoId()+"\n");	
		}}
		sb.append(".............2 - avatar pic urls....... \n");
		sb.append("avatar - " + avatar + "\n");
		
		sb.append("nickname - " + nick +"\n");
		sb.append("balance point - " + point +"\n");
		sb.append("username - " + username + "\n");
		sb.append("message count - " + msgCount +"\n");
		sb.append("mid - " + mid + "\n");
		sb.append(weiboShareContent.toString() + "\n");
		if(thirdPartyUsers!=null&&!thirdPartyUsers.isEmpty()){
		for (ThirdPartyUser item : thirdPartyUsers) {
			sb.append(item.toString());
		}
		}
		
		sb.append("sina key - " + sina_key + "\n");
		sb.append("qqweibo_key - " + qqweibo_key + "\n");
		sb.append("qq_key - " + qq_key + "\n");
		sb.append("\n");
		sb.append(".............get HomePage Info END....... \n");
		return sb.toString();
	}

	public WeiboShareContent getWeiboShareContent() {
		return weiboShareContent;
	}

	public void setWeiboShareContent(WeiboShareContent weiboShareContent) {
		this.weiboShareContent = weiboShareContent;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<ThirdPartyUser> getWeiboItems() {
		return thirdPartyUsers;
	}

	public void setWeiboItems(List<ThirdPartyUser> weiboItems) {
		this.thirdPartyUsers = weiboItems;
	}

	/**
	 * (0-未完善 1-完善)
	 * @return
	 */
	public String getCustomerInfoCompleted() {
		return customerInfoCompleted;
	}

	public void setCustomerInfoCompleted(String customerInfoCompleted) {
		this.customerInfoCompleted = customerInfoCompleted;
	}

	
	

}
