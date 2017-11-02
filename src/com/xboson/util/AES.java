/* CatfoOD 2017年11月2日 上午11:11:48 yanming-sohu@sohu.com Q.412475540 */

package com.xboson.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;

public class AES {
	
	private final static byte[] salt;
	private static final String algorithmName = "AES/CBC/PKCS5Padding";
	private static final String keytype = "PBKDF2WithHmacSHA1";
	private static final String keyname = "AES";
	private static final int keylen = 16 * 8;
	private static final int itercount = 5999;
	private static final IvParameterSpec iv;
	
	static {
		salt = "xBoson XX 2017.11.2 --jym".getBytes();
		
		// 随机生成切不可变化, 用于加强密钥
		iv = new IvParameterSpec(new byte[]{
			0x33, 0x16, 0x71, 0x11,
			0x67, (byte)0x81, 0x01, 0x41,
			(byte)0x91, 0x38, 0x11, 0x33,
			0x63, 0x44, 0x21, 0x41,
		});
	}

	
	/**
	 * 生成 aes 密钥
	 * @throws Exception 
	 */
	public static byte[] aesKey(String pass) throws ServletException {
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance(keytype);
			PBEKeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, itercount, keylen);
			SecretKey secretKey = skf.generateSecret(spec);
			return secretKey.getEncoded();
		} catch(Exception e) {
			throw new ServletException(e);
		}
	}
	
	
	public static byte[] Encode(byte[] data, byte[] password) throws ServletException {
		try {
			Cipher c = Cipher.getInstance(algorithmName);
			SecretKeySpec key = new SecretKeySpec(password, keyname);
			c.init(Cipher.ENCRYPT_MODE, key, iv);
			return c.doFinal(data);
		} catch(Exception e) {
			throw new ServletException(e);
		}
	}
	
	
	public static byte[] Decode(byte[] data, byte[] password) throws ServletException {
		try {
			
			Cipher c = Cipher.getInstance(algorithmName);
			SecretKeySpec key = new SecretKeySpec(password, keyname);
			c.init(Cipher.DECRYPT_MODE, key, iv);
			return c.doFinal(data);
		} catch(Exception e) {
			throw new ServletException(e);
		}
	}
}
