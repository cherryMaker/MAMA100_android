/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mama100.android.member.outwardWeibo;

import com.mama100.android.member.R;
import com.mama100.android.member.global.BasicApplication;


/**
 * Encapsulation a Weibo error, when weibo request can not be implemented successful.
 *
 * 
 */
public class XWeiboException extends Exception {

	private static final long serialVersionUID = 475022994858770424L;
	
	
	/*10001	：	系统错误
	10002	：	服务端资源不可用
	10003	：	远程服务出错
	10005	：	该资源需要appkey拥有更高级的授权
	10006	：	缺少 source参数(appkey)
	10007	：	不支持的 MediaType (%s)
	10008	：	错误:参数错误，请参考API文档
	10009	：	任务过多，系统繁忙
	10010	：	任务超时
	10011	：	RPC错误
	10012	：	非法请求
	10013	：	不合法的微博用户
	10014	：	第三方应用访问api接口权限受限制
	10016	：	错误:缺失必选参数:%s，请参考API文档
	10017	：	错误:参数值非法,希望得到 (%s),实际得到 (%s)，请参考API文档
	10018	：	请求长度超过限制
	10020	：	接口不存在
	10021	：	请求的HTTP METHOD不支持
	10022	：	IP请求超过上限
	10023	：	用户请求超过上限
	10024	：	用户请求接口%s超过上限
	10025	：	内部接口参数错误
	20001	：	IDS参数为空
	20002	：	uid参数为空
	20003	：	用户不存在
	20005	：	不支持的图片类型,仅仅支持JPG,GIF,PNG
	20006	：	图片太大
	20007	：	请确保使用multpart上传了图片
	20008	：	内容为空
	20009	：	id列表太长了
	20012	：	输入文字太长，请确认不超过140个字符
	20013	：	输入文字太长，请确认不超过300个字符
	20014	：	传入参数有误，请再调用一次
	20016	：	发微博太多啦，休息一会儿吧
	20017	：	你刚刚已经发送过相似内容了哦，先休息一会吧
	20019	：	不要太贪心哦，发一次就够啦
	20023	：	很抱歉，此功能暂时无法使用，如需帮助请联系@微博客服 或者致电客服电话400 690 0000
	20031	：	需要弹出验证码
	20032	：	微博发布成功。目前服务器数据同步可能会有延迟，请耐心等待1-2分钟。谢谢
	20033	：	登陆状态异常
	20101	：	不存在的微博
	20102	：	不是你发布的微博
	20103	：	不能转发自己的微博
	20109	：	微博 id为空
	20111	：	不能发布相同的微博
	20112	：	由于作者隐私设置，你没有权限查看此微博
	20114	：	标签名太长
	20115	：	标签不存在
	20116	：	标签已存在
	20117	：	最多200个标签
	20118	：	最多5个标签
	20119	：	标签搜索失败
	20120	：	由于作者设置了可见性，你没有权限转发此微博
	20121	：	visible参数非法
	20122	：	应用不存在
	20123	：	最多屏蔽200个应用
	20124	：	最多屏蔽500条微博
	20125	：	没有屏蔽过此应用
	20126	：	不能屏蔽新浪应用
	20127	：	已添加了此屏蔽
	20128	：	删除屏蔽失败
	20129	：	没有屏蔽任何应用
	20201	：	不存在的微博评论
	20203	：	不是你发布的评论
	20204	：	评论ID为空
	20206	：	作者只允许关注用户评论
	20207	：	作者只允许可信用户评论
	20401	：	域名不存在
	20402	：	verifier错误
	20403	：	屏蔽用户列表中存在此uid
	20404	：	屏蔽用户列表中不存在此uid
	20405	：	uid对应用户不是登录用户的好友
	20406	：	屏蔽用户个数超出上限
	20407	：	没有合适的uid
	20408	：	从feed屏蔽列表中，处理用户失败
	20501	：	错误:source_user 或者target_user用户不存在
	20502	：	必须输入目标用户id或者 screen_name
	20503	：	关系错误，user_id必须是你关注的用户
	20504	：	你不能关注自己
	20505	：	加关注请求超过上限
	20506	：	已经关注此用户
	20507	：	需要输入验证码
	20508	：	根据对方的设置，你不能进行此操作
	20509	：	悄悄关注个数到达上限
	20510	：	不是悄悄关注人
	20511	：	已经悄悄关注此用户
	20512	：	你已经把此用户加入黑名单，加关注前请先解除
	20513	：	你的关注人数已达上限
	20522	：	还未关注此用户
	20523	：	还不是粉丝
	20601	：	列表名太长，请确保输入的文本不超过10个字符
	20602	：	列表描叙太长，请确保输入的文本不超过70个字符
	20603	：	列表不存在
	20604	：	不是对象所属者
	20605	：	列表名或描叙不合法
	20606	：	记录已存在
	20607	：	错误:数据库错误，请联系系统管理员
	20608	：	列表名冲突
	20610	：	目前不支持私有分组
	20611	：	创建list失败
	20612	：	目前只支持私有分组
	20613	：	错误:不能创建更多的列表
	20614	：	已拥有列表上下，请参考API文档
	20615	：	成员上线，请参考API文档
	20616	：	不支持的分组类型
	20617	：	最大返回300条
	20618	：	uid 不在列表中
	20701	：	不能提交相同的标签
	20702	：	最多两个标签
	20704	：	您已经收藏了此微博
	20705	：	此微博不是您的收藏
	20706	：	操作失败
	20801	：	trend_name是空值
	20802	：	trend_id是空值
	21001	：	标签参数为空
	21002	：	标签名太长，请确保每个标签名不超过14个字符
	21103	：	该用户已经绑定手机
	21104	：	verifier错误
	21105	：	你的手机号近期频繁绑定过多个帐号，如果想要继续绑定此帐号，请拨打客服电话400 690 0000申请绑定
	21108	：	原始密码错误
	21109	：	新密码错误
	21110	：	此用户暂时没有绑定手机
	21113	：	教育信息不存在
	21115	：	职业信息不存在
	21117	：	此用户没有qq信息
	21120	：	此用户没有微号信息
	21121	：	此微号已经存在
	21301	：	认证失败
	21302	：	用户名或密码不正确
	21303	：	用户名密码认证超过请求限制
	21304	：	版本号错误
	21305	：	缺少必要的参数
	21306	：	Oauth参数被拒绝
	21307	：	时间戳不正确
	21308	：	nonce参数已经被使用
	21309	：	签名算法不支持
	21310	：	签名值不合法
	21311	：	consumer_key不存在
	21312	：	consumer_key不合法
	21313	：	consumer_key缺失
	21314	：	Token已经被使用
	21315	：	Token已经过期
	21316	：	Token不合法
	21317	：	Token不合法
	21318	：	Pin码认证失败
	21319	：	授权关系已经被解除
	21320	：	不支持的协议
	21321	：	未审核的应用使用人数超过限制
	21322	：	重定向地址不匹配
	21323	：	请求不合法
	21324	：	client_id或client_secret参数无效
	21325	：	提供的Access Grant是无效的、过期的或已撤销的
	21326	：	客户端没有权限
	21327	：	token过期
	21328	：	不支持的 GrantType
	21329	：	不支持的 ResponseType
	21330	：	用户或授权服务器拒绝授予数据访问权限
	21331	：	服务暂时无法访问
	21332	：	access_token 无效
	21333	：	禁止使用此认证方式*/
	private int statusCode = -1;
	private String statusDesc=null; //错误代码友好描述
	
	
	
    public XWeiboException(String msg) {
        super(msg);
        statusDesc="";
    }

    public XWeiboException(Exception cause) {
        super(cause);
        statusDesc="";
    }

    public XWeiboException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
        formDesc();
    }

    public XWeiboException(String msg, Exception cause) {
        super(msg, cause);
        statusDesc="";
    }

    public XWeiboException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
        formDesc();
    }

    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getStatusDesc(){
    	return statusDesc;
    }
    
    
	public XWeiboException() {
		super(); 
		statusDesc="";
	}

	public XWeiboException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		statusDesc="";
	}

	public XWeiboException(Throwable throwable) {
		super(throwable);
		statusDesc="";
	}

	public XWeiboException(int statusCode) {
		super();
		this.statusCode = statusCode;
		formDesc();
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * 生成错误代码友好描述,
	 * 由于错误代码数量众多，现只支持一部分友好描述，以后视具体情况可再添加。
	 */
	private void formDesc(){
		//***********************
		//授权相关
		//***********************
		if(statusCode==21327
				||statusCode==21314
        		||statusCode==21315
        		||statusCode==21316
        		||statusCode==21317
        		||statusCode==21332){
			statusDesc=BasicApplication.getXmlString(R.string.auth_error01);
			return;
		}
		
		//***********************
		//分享相关
		//***********************
		else if(statusCode==20016){
			statusDesc=BasicApplication.getXmlString(R.string.share_error01); //分享操作过于频繁
		}
		else if(statusCode==20017||statusCode==20017||statusCode==20019||statusCode==20111){
			statusDesc=BasicApplication.getXmlString(R.string.share_error02); //分享内容相同或相似
		}
		else if(statusCode==20020){
			statusDesc=BasicApplication.getXmlString(R.string.share_error03); //包含广告信息
		}
		else if(statusCode==20032){
			statusDesc=BasicApplication.getXmlString(R.string.share_error04); //分享成功，但新浪服务器
		}
		else if(statusCode==20012||statusCode==20013){
			statusDesc=BasicApplication.getXmlString(R.string.share_error05); //内容太长
		}
		else if(statusCode==20005){
			statusDesc=BasicApplication.getXmlString(R.string.share_error06); //不支持的图片类型
		}
		else if(statusCode==20006){
			statusDesc=BasicApplication.getXmlString(R.string.share_error07); //图片太大
		}
		else if(statusCode==20018||statusCode==20021){
			statusDesc=BasicApplication.getXmlString(R.string.share_error08); //包含非法内容
		}
		//***********************
		//添加关注相关
		//***********************
		else if(statusCode==20504){
			statusDesc=BasicApplication.getXmlString(R.string.follow_error01); //不能关注自己
		}
		else if(statusCode==20505){
			statusDesc=BasicApplication.getXmlString(R.string.follow_error02); //加关注请求超过上限
		}
		else if(statusCode==20506){
			statusDesc=BasicApplication.getXmlString(R.string.follow_error03); //已关注此用户
		}
		else if(statusCode==20521){
			statusDesc=BasicApplication.getXmlString(R.string.follow_error04); //添加关注操作太频繁
		}
		
		
		else if(statusCode==10002){
			statusDesc=BasicApplication.getXmlString(R.string.xweibo_service_unavailable); //暂停微博服务
		}
		//***********************
		//其它错误信息的统一描述
		//***********************
		else{
			statusDesc=BasicApplication.getXmlString(R.string.xweibo_other_error)+"["+statusCode+"]"; //其它错误
		}
	}
	
}
