package com.mama100.android.member.domain.user;

import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;


/**
 * Let's购 - 更新或添加用户的 宝宝信息
 * @author aihua.yan  2012-10-09
 */
	public class UpdateBabyInfoReq extends BaseReq {

		
		/****************************************
         * 1, 添加新宝宝需要上传的字段
         ****************************************/
		
		/**
		 * 孩子名字
		 */
		private String name;
		
		/**
		 * 孩子生日
		 */
		private String birthdate;
		
		/**
		 * 孩子性别代码
		 */
		private String genderCode;
		
		
		/****************************************
         * 2, 修改宝宝需要上传的字段 + 所有添加宝宝需要上传的字段
         ****************************************/
		
		 /**
		  * 数据库主键，宝宝id
		  */
		
		 private String id; //增加默认为不传,修改用服务器传来的值
		
		
		/****************************************
         *  3, 删除宝宝需要 上传的字段 +  宝宝id
         ****************************************/
		/***
		 * 专门用于删除宝宝信息的 字段 ， 只要删除时 需要填写， 其余 增加和修改就不传值
		 */
		private String step;
		/**
		 * 用户id
		 */
		private String customerId;
		
		
		public final static String DELETE_BABY = "delUserChild";
		
	
	public String getId() {
			return id;
		}





		public void setId(String id) {
			this.id = id;
		}


	
		public String getCustomerId() {
			return customerId;
		}





		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}





		public String getName() {
			return name;
		}


		public String getStep() {
			return step;
		}





		public void setStep(String step) {
			this.step = step;
		}





		public void setName(String name) {
			this.name = name;
		}


		public String getBirthdate() {
			return birthdate;
		}





		public void setBirthdate(String birthdate) {
			this.birthdate = birthdate;
		}





		public String getGenderCode() {
			return genderCode;
		}





		public void setGenderCode(String genderCode) {
			this.genderCode = genderCode;
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
