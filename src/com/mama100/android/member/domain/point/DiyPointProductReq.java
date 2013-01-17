package com.mama100.android.member.domain.point;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * @Description:进行防伪码验证的请求
 * @author created by liyang  
 * @date 2012-11-20 下午2:40:38 
 */
public class DiyPointProductReq extends BaseReq{

	/**
	 * 防伪码
	 */
	private String security;
	

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {

		if (!super.validate(response)) {
			return false;
		}

		return true;
	}
}
