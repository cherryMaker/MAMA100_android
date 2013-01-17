package com.mama100.android.member.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类
 * 
 * @author jimmy
 * 
 * modified by liyang 2012-11-28 修改密码验证规则
 */
public class MobValidateUtils {
	
	public static int MEMBER_CARD_NUMBER_LENGTH = 16;
	public static int ANTI_FAKE_NUMBER_LENGTH = 16;
	public static int MOBILE_NUMBER_LENGTH = 11;
	
	//新序列号是12位，老序列号是14位
	public static int SERIAL_NUMBER_LENGTH1 = 12;
	public static int SERIAL_NUMBER_LENGTH2 = 14;
	public static int TRADE_PASSWORD_LENGTH = 6;

	/**
	 * 正则表达式检查
	 * 
	 * @param reg
	 * @param string
	 * @return
	 */
	public static boolean check(String reg, String string) {

		boolean tem = false;

		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(string);

		tem = matcher.matches();

		return tem;
	}

	/**
	 * 检验用户名 取值范围为a-z,A-Z,0-9,"_",汉字<br>
	 * 不能以"_"结尾 用户名有最小长度和最大长度限制，比如用户名必须是4-20位
	 * 只能中文，英文，数字，下划线，不能符号，小数点。
	 */
	public static boolean checkRegUsername(String username, int min, int max) {
		String regex = "[\\w\u4e00-\u9fa5]{" + min + "," + max + "}(?<!_)";
		return check(regex, username);

	}
	
	
	/**
	 * 检验用户名 取值范围为a-z,A-Z,0-9,汉字<br>
	 * 有最小长度和最大长度限制，比如用户名必须是2-20位
	 * 只能中文，英文，数字,不能下划线，符号，小数点。
	 */
	public static boolean checkFirstLoginUsername(String username, int min,
			int max) {

		//只能中文，英文，数字,不能下划线，符号，小数点。2-20位。这里1个汉字算1位
		String regex = "([0-9]|[A-Za-z]|[\u4e00-\u9fa5]{" + min + "," + max
				+ "})+";

		// 原始的 只能数字、字母、中间下划线， 不能汉字
		// String regex = "[\\w\u4e00-\u9fa5]{" + min + "," + max + "}(?<!_)";

		// "_"位置没有限制
		// String regex = "[\u4e00-\u9fa5_a-zA-Z0-9_]{" + min + "," + max + "}";

		// 允许中间出现"_",无位数判断
		// String regex = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$";
		// 允许中间出现"_",有位数判断。
		// String regex = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]{" + min +
		// "," + max + "}+$";

		return check(regex, username);

	}
	
	

	/**
	 * 手机号码验证,11位，不知道详细的手机号码段，只是验证开头必须是1和位数
	 * 
	 * 国家号码段分配如下：
　　移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
　　联通：130、131、132、152、155、156、185、186
　　电信：133、153、180、189、（1349卫通）
	 */
	public static boolean checkMobile(String mobile) {
        //String reg = "^[1][\\d]{10}";
		//String reg = "^[1]([3][0-9]{1}|[5][0-9]{1}|[8][5-9]{1}|80)[0-9]{8}$";
		String reg = "\\d{11}";
		
		return check(reg, mobile);
	}

	/**
	 * 检查EMAIL地址 用户名和网站名称必须>=1位字符
	 * 地址结尾必须是以com|cn|com|cn|net|org|gov|gov.cn|edu|edu.cn结尾
	 */
	public static boolean checkEmail(String email) {
		String regex = "\\w+\\@\\w+\\.(com|cn|com.cn|net|org|gov|gov.cn|edu|edu.cn)";
		return check(regex, email);
	}

	/**
	 * 检查邮政编码(中国),6位，第一位必须是非0开头，其他5位数字为0-9
	 */
	public static boolean checkPostcode(String postCode) {
		String regex = "^[1-9]\\d{5}";
		return check(regex, postCode);
	}

	/**
	 * 验证国内电话号码 格式：010-67676767，区号长度3-4位，必须以"0"开头，号码是7-8位
	 */
	public static boolean checkPhoneNr(String phoneNr) {
		String regex = "^[0]\\d{2,3}\\-\\d{7,8}";
		return check(regex, phoneNr);
	}

	/**
	 * 验证国内身份证号码：15或18位，由数字组成，不能以0开头
	 */
	public boolean checkIdCard(String idNr) {
		String reg = "^[1-9](\\d{14}|\\d{17})";
		return check(reg, idNr);
	}

	/**
	 * 网址验证<br>
	 * 符合类型：<br>
	 * http://www.test.com<br>
	 * http://163.com
	 */
	public static boolean checkWebSite(String url) {
		String reg = "^(http)\\://(\\w+\\.\\w+\\.\\w+|\\w+\\.\\w+)";
		return check(reg, url);
	}
	
	/**
	 * 由数字和字母组成,且长度要在8-16位之间。
	 * 需要要同时含有数字和字母
	 * 解析：
	 * (?![0-9]+$) 预测该位置后面不全是数字
	 * (?![a-zA-Z]+$) 预测该位置后面不全是字母
	 * [0-9A-Za-z] {8,16} 由8-16位数字或这字母组成
	 * $ 匹配行结尾位置
	 * @param pwd
	 * @return
	 */
	public static boolean checkPassword(String pwd){
		String reg = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
		return check(reg, pwd);
	}
	
	/**
	 * 由数字和字母组成,且长度要在8-16位之间。
	 * 不需要要同时含有数字和字母
	 * @param pwd
	 * @return
	 */
	public static boolean checkPassword2(String pwd){
		//modified by liyang  START 
		//String reg = "[A-Za-z0-9]{6,18}";
		String reg = ".{6,18}";
		return check(reg, pwd.trim());
		//modified by liyang  END
	}
	
	
	
	/**
	 * 由数字和字母组成,且长度要在6-20位之间。
	 * 不需要要同时含有数字和字母
	 * @param pwd
	 * @return
	 */
	public static boolean checkPassword3(String pwd){
		return !(pwd.length() < 6
				|| pwd.length() > 20);
	}
	
	
	
	
	/**
	 * 验证序列号<br>
	 * 符合类型：<br>
	 * 12位序列号<br>
	 */
	public static boolean checkInputSerial(String number) {
		int length = number.length();
		boolean flag = false;
		if(length==SERIAL_NUMBER_LENGTH1||length==SERIAL_NUMBER_LENGTH2)
		{
			flag = true;
		}else{
			flag = false;
		}
		return flag;
	}

	
	/**
	 * 验证防伪码<br>
	 * 符合类型：<br>
	 * 16位防伪号
	 */
	public static boolean checkInputAntiFake(String number) {
		return number.length()==ANTI_FAKE_NUMBER_LENGTH;
	}

	

}
