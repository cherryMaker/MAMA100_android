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


/**
 * Encapsulation a Weibo error, when weibo request can not be implemented successful.
 *
 * 
 */
public class QQPlatformException extends Exception {
	/**
	100000  缺少参数response_type或response_type非法。
	 
	100001  缺少参数client_id。
	 
	100002  缺少参数client_secret。
	 
	100003  http head中缺少Authorization。
	 
	100004  缺少参数grant_type或grant_type非法。
	 
	100005  缺少参数code。
	 
	100006  缺少refresh token。
	 
	100007  缺少access token。
	 
	100008  该appid不存在。
	 
	100009  client_secret（即appkey）非法。
	 
	100010  回调地址不合法。
	 
	100011  APP不处于上线状态。
	 
	100012  HTTP请求非post方式。
	 
	100013  access token非法。
	 
	100014  access token过期。

	token过期时间为3个月。如果存储的access token过期，请重新走登录流程，根据使用Authentication_Code获取Access_Token或使用Implicit Grant方式获取Access Token获取新的access token值。 
	 
	100015  access token废除。

	token被回收，或者被用户删除。请重新走登录流程，根据使用Authentication_Code获取Access_Token或使用Implicit Grant方式获取Access Token获取新的access token值。 
	 
	100016  access token验证失败。
	 
	100017  获取appid失败。
	 
	100018  获取code值失败。
	 
	100019  用code换取access token值失败。
	 
	100020  code被重复使用。
	 
	100021  获取access token值失败。
	 
	100022  获取refresh token值失败。
	 
	100023  获取app具有的权限列表失败。
	 
	100024  获取某OpenID对某appid的权限列表失败。
	 
	100025  获取全量api信息、全量分组信息。
	 
	100026  设置用户对某app授权api列表失败。
	 
	100027  设置用户对某app授权时间失败。
	 
	100028  缺少参数which。
	 
	100029  错误的http请求。
	 
	100030  用户没有对该api进行授权，或用户在腾讯侧删除了该api的权限。请用户重新走登录、授权流程，对该api进行授权。
	 
	100031  第三方应用没有对该api操作的权限。请发送邮件进行申请接口权限。
	**/
	
	
	
	

	private static final long serialVersionUID = 475022994858770424L;
	
	
	private int statusCode = -1;
	
	
	
    public QQPlatformException(String msg) {
        super(msg);
    }

    public QQPlatformException(Exception cause) {
        super(cause);
    }

    public QQPlatformException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public QQPlatformException(String msg, Exception cause) {
        super(msg, cause);
    }

    public QQPlatformException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
    
    
	public QQPlatformException() {
		super(); 
	}

	public QQPlatformException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public QQPlatformException(Throwable throwable) {
		super(throwable);
	}

	public QQPlatformException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
}
