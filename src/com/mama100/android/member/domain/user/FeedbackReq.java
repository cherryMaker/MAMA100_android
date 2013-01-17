package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;

/**
 * Let's购 - 意见反馈
 * 
 * @author aihua 2012-08-03
 */
public class FeedbackReq extends BaseReq {

	/**
	 * 反馈的意见
	 */
	private String content;


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {

		if (!super.validate(response)) {
			return false;
		}

		if (StringUtils.isBlank(content)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须输入反馈意见");
			return false;
		}

		return true;
	}

}
