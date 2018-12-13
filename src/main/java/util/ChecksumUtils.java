package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public final class ChecksumUtils {

	private static MessageDigest MD5;
	
	private static void lazyInitMD5Algorithm() {
		if(MD5 == null) 
			try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) { /* Not possible if JVM>=7 */ }
	}

	public static String hashMD5(String text) {
		lazyInitMD5Algorithm();
		
		MD5.update(text.getBytes());
		byte[] digest = MD5.digest();
		
		return DatatypeConverter.printHexBinary(digest).toUpperCase();
	}

}
