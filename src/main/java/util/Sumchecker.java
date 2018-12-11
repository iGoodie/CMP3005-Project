package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class Sumchecker {

	private static MessageDigest MD5;

	public static String hashMD5(String text) {
		if(MD5 == null) try { // Lazy init
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) { /* Not possible if JVM>=7 */ }
		
		MD5.update(text.getBytes());
		byte[] digest = MD5.digest();
		
		return DatatypeConverter.printHexBinary(digest).toUpperCase();
	}

}
