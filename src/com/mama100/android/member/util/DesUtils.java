package com.mama100.android.member.util;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;


public class DesUtils {
	
	// DES编码格式
	private static String DES_FORMAT = "DES/ECB/NoPadding";
	
	// 内容编码
	private static String DES_ENCODING = "UTF-8";
	
	// 全局密钥 不更改
	//public static String DES_COMMON_KEY = "fy486eiw78/w&fpu";

	// 全局密钥 不更改
	public static String DES_COMMON_KEY = "fy486eiw";

	// 版本密钥 每次发布新版本 会更改此密钥
	//public static String DES_VERSION_KEY = "qgur&#3kedueb83h"; 

	// 版本密钥 每次发布新版本 会更改此密钥
	public static String DES_VERSION_KEY = "qgur&#3k"; 
	
	/**
	 * DES加密
	 * 
	 * @param key
	 *            密钥
	 * @param data
	 *            需加密的原始数据
	 * @return 加密并以base64编码的数据
	 * @throws Exception
	 */
	public static String encrypt(String key, String data) throws Exception {
		
		if (key == null || key.length() == 0) {
			System.out.println("key is null.");
			return null;
		}
		
		if (data == null || data.length() == 0) {
			System.out.println("data is null.");
			return null;
		}

		if (key.length() % 8 != 0 || key.length() > 64) {
			System.out.println("invalid key.");
			throw null;
		}

		byte[] bytes = handleDesData(data.getBytes(DES_ENCODING));

		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key.getBytes("ASCII"));

		// 创建一个密匙工厂，然后用它把DESKeySpec转换成 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES_FORMAT);

		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey);

		// 现在，获取数据并加密
		// 正式执行加密操作
		byte[] result = cipher.doFinal(bytes);

		// base64再次编码
		Base64 base64 = new Base64();
		byte[] encodedBytes = base64.encode(result);

		return new String(encodedBytes);
	}

	/**
	 * DES解密
	 * 
	 * @param key
	 *            密钥
	 * @param encryptedStr
	 *            已加密的数据
	 * @return 解密后的数据
	 * @throws Exception
	 */
	public static String decode(String key, String encryptedStr) throws Exception {

		if (key == null || key.length() == 0) {
			System.out.println("key is null.");
			return null;
		}
		
		if (encryptedStr == null || encryptedStr.length() == 0) {
			System.out.println("encryptedStr is null.");
			return null;
		}

		if (key.length() % 8 != 0 || key.length() > 64) {
			System.out.println("invalid key.");
			throw null;
		}

		// 先将Base64字串转码为byte[]
		Base64 base64 = new Base64();
		byte[] decodedBytes = base64.decode(encryptedStr.getBytes());

		// 建立解密所需的Key. 因为加密時的key是用ASCII转换, 所以這边也用ASCII做
		DESKeySpec objDesKeySpec = new DESKeySpec(key.getBytes("ASCII"));
		SecretKeyFactory objKeyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey objSecretKey = objKeyFactory.generateSecret(objDesKeySpec);

		// 设定一个DES/ECB/PKCS5Padding的Cipher
		// ECB对应到.Net的CipherMode.ECB
		// 用PKCS5Padding对应到.Net的PaddingMode.PKCS7
		Cipher objCipher = Cipher.getInstance(DES_FORMAT);

		// 设定為解密模式, 并设定解密的key
		objCipher.init(Cipher.DECRYPT_MODE, objSecretKey);

		// 输出解密后的字串. 因为加密时指定PaddingMode.PKCS7, 所以可以不用处理空字元
		// 不过若想保险点, 也是可以用trim()去处理一遍
		String decryptedStr = new String(objCipher.doFinal(decodedBytes), DES_ENCODING).trim();

		return decryptedStr;
	}
	
	/**
	 * 处理待加密的数据，不足8的倍数需在后面补零
	 * 
	 * @param data
	 *            待加密的数据
	 * @return 处理后的数据
	 */
	private static byte[] handleDesData(byte[] data) {

		int length = data.length;
		byte[] nData;

		int mod = length % 8;

		if (mod != 0) {
			nData = new byte[length + 8 - mod];
		} else {
			nData = new byte[length];
		}

		for (int i = 0; i < nData.length; i++) {
			if (i < length) {
				nData[i] = data[i];
			}
		}

		return nData;
	}
	


}
