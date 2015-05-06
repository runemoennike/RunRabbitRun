package com.lionfish.runrunrun.thirdparty;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
	public static String makeMd5(String strtomd5) {
		String md5str = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(strtomd5.getBytes());
			BigInteger hash = new BigInteger(1, md5.digest());
			md5str = hash.toString(16);
		} catch (NoSuchAlgorithmException nsae) {
		}
		return md5str;
	}

	public static String processMd5(String strtomd5) {
		String tempmd5 = makeMd5(strtomd5);
		if (tempmd5.length() < 32) {
			tempmd5 = "0" + tempmd5;
		}
		return tempmd5;
	}

	// Read more:
	// http://bncapps.com/java-applet-as-a-facebook-app/#ixzz1HtTQB982
}
