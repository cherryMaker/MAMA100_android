package com.mama100.android.member.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
	public static boolean isBlank(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static boolean isAllBlank(String phone, String mob) {
		
		return (isBlank(phone)&&isBlank(mob))?true:false;
		
	}
	
	//added by 爱华 2011-11-16
	//判断是否为数字
	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*");// “”
		   return pattern.matcher(str).matches(); 
		}


	
	/**
	 * 
	 * @param str  类似"2012-02-13"
	 * @return  "20120213"
	 * 或者用于 -100 转成  100
	 */
	public static String removeDash(String str){
		return str.replace("-", "");
		}  
	
	

	/**
	 * @param time ,传入的19位时间字符串：2012-06-11 13:45:25
	 * @return 将标准19位时间 裁剪为 16位 ，去掉末尾的秒 如2012-06-11 13:45
	 * 如果本来就不足19位， 比如 2012-06-11 13:43， 就不用裁剪了
	 */
	public static String cat(String time) {
		if(time!=null){
		//标准时间格式 time = "2012-06-11 13:45:25"， 现在去掉秒
		int length = time.length();
		int lastComma = time.lastIndexOf(":");
		int STANDARD_LENGTH = 19;
		if(length >=STANDARD_LENGTH && length - lastComma == 3){
			return time.substring(0, lastComma);
		}}
		return time;
	}
	
	/**
	 * 获取绝对值
	 */

	public static String getAbsValue(String value){
		//判断value是否是数字
		if(isNumeric(removeDash(value))){
			int num = Integer.valueOf(value);
			int abs = Math.abs(num);
			return String.valueOf(abs);
		}
		return "不是数字";
		
	}
	
	
	/**
	 * 判断是否是负数
	 */
	public static boolean isNegativeValue(String value){
		//判断value是否是数字
		if(isNumeric(removeDash(value))){
			int num = Integer.valueOf(value);
			return (num<=0);
		}
		return false;
	}
	
	//检查是否为数字或字母
	private static boolean checkPwdChars(final String str){
		//先检查最后一位(提高效率)		
		char tmp;
		int i=str.length()-1;
		for(;i>=0;i--){
			tmp=str.charAt(i);
			if(!(('0'<=tmp&&tmp<='9')
					||('a'<=tmp&&tmp<='z')
					||('A'<=tmp&&tmp<='Z'))){
				return false;
			}
		};
		return true;
	}
	
    /**
     * 统计中英混合的字符串个数，其中单个字符为ASCII编码则统计加1，否则加2
     * @param chars
     * @return
     */
    public static int countEx(final CharSequence chars){
    	int count=0;
    	//ASCII字符统计为1，否则为2
		for(int i=0;i<chars.length();i++){
			if(chars.charAt(i)>=0&&chars.charAt(i)<=127)
				count++;
			else count +=2;
		}
		return count;	
    }
    
    
	public static String splitStringWithWildCard(int number,StringBuffer sb , Object wildcard){
		  int count = sb.length() / number ;
	        for (int i = 0; i < count; i++)
	            sb.insert((i+1)*number+i, wildcard );
	        return sb.toString();
	}
    

}
