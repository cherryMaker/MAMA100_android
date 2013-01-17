package com.mama100.android.member.util;

import java.security.MessageDigest;

public class Md5Utils {

	/**
	 * 加密数据
	 * @param data 加密前的字节
	 * @return 加密后的字节
	 */
	public static byte[] encrypt(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			return digest.digest(data);
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * 加密数据
	 * 
	 * @param data
	 *            加密前的字符
	 * @return byte[] 加密后的字节
	 */
	public static byte[] encrypt(String data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			return digest.digest(data.getBytes("UTF-8"));
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * 加密数据
	 * @param data 加密前的字节
	 * @return 加密后的16进制数据
	 */
	public static String encryptTo16hex(byte[] data) {
		return ByteUtils.byteArrayToString(encrypt(data));
	}

	/**
	 * 加密数据
	 * @param data 加密前的字符
	 * @return 加密后的16进制数据
	 */
	public static String encryptTo16hex(String data) {
		return ByteUtils.byteArrayToString(encrypt(data));
	}


//	public static void main(String[] args) {
//		
//		String result = encryptTo16hex("830516"); 
//		System.out.println("result: " + result);		
//		
//	}

}
