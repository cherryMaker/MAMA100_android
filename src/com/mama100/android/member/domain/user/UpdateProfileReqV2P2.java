package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;


/**
 * Let's购 - 更新用户资料
 * @author aihua.yan  2012-09-28
 */
	public class UpdateProfileReqV2P2 extends BaseReq {

		/**
		 * 昵称
		 */
		private String nickname;	
		
	
	/**
	 * 客户端验证
	 */
	@Override
	public boolean validate(BaseRes response) throws Exception {
		
		if (!super.validate(response)) {
			return false;
		}

		if (StringUtils.isBlank(nickname)) {
			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
			response.setDesc("必须输入昵称");
			return false;
		}
		
		return true;
	}



	public String getNickname() {
		return nickname;
	}



	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
