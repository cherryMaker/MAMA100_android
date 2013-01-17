package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.StringUtils;


/**
 * Let's购 - 更新用户资料
 * http://192.168.72.91:8080/member/user/updateInfo.action?
 * devid=xxx&nickname=mico&babySex=boy&babyName=bb&babyBirth=2012-7-18
 * @author aihua.yan  2012-08-03
 */
	public class UpdateProfileReq extends BaseReq {

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
		
//		if (StringUtils.isBlank(babySex)) {
//			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
//			response.setDesc("必须输入宝宝性别");
//			return false;
//		}
//		if (StringUtils.isBlank(babyName)) {
//			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
//			response.setDesc("必须输入宝宝姓名");
//			return false;
//		}
//		if (StringUtils.isBlank(babyBirth)) {
//			response.setCode(DeviceResponseCode.VALIDATE_ERROR);
//			response.setDesc("必须输入宝宝生日");
//			return false;
//		}
		
		return true;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
