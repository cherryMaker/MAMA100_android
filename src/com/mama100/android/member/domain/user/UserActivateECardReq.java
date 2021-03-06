package com.mama100.android.member.domain.user;
import com.mama100.android.member.domain.base.BaseReq;

/**
 * 
 * @author deric 2012-9-20
 */
public class UserActivateECardReq extends BaseReq {
	
	//必须参数:
	/**
	  * 手机号码
	  */
	 private String mobile;

	 /**
	  * 验证码
	  */
	 private String validateCode;

	 /**
	  * 步骤
	  */
	 private String step;
	 
	 public final static String STEP_ONE="send_validateCode";
	 public final static String STEP_TWO="sumit_validateCode";
	 
	 
	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}


}
