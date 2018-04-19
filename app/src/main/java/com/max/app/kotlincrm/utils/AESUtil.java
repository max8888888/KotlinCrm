package com.max.app.kotlincrm.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

	/*
	 * 转为十六进制
	 */
	@SuppressWarnings("unused")
	private static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		System.out.println(strbuf.toString());
		return strbuf.toString();
	}

	/*
	 * 转为二进制
	 */
	@SuppressWarnings("unused")
	private static byte[] asBin(String src) {
		if (src.length() < 1)
			return null;
		byte[] encrypted = new byte[src.length() / 2];
		for (int i = 0; i < src.length() / 2; i++) {
			int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
			encrypted[i] = (byte) (high * 16 + low);
		}
		return encrypted;
	}

	/**
	 * 加密
	 */
	public static String encryptAES(String data, String secret_key) {
		byte[] key = asBin(secret_key);
		SecretKeySpec sKey = new SecretKeySpec(key, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, sKey);
			byte[] encrypted = cipher.doFinal(data.getBytes());
			return asHex(encrypted);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 解密
	 */
	public static String decryptAES(String encData, String secret_key) {
		byte[] tmp = asBin(encData);
		byte[] key = asBin(secret_key);
		SecretKeySpec sKey = new SecretKeySpec(key, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, sKey);
			byte[] decrypted = cipher.doFinal(tmp);
			return new String(decrypted);
		} catch (Exception e) {
			return null;
		}
	}
}
