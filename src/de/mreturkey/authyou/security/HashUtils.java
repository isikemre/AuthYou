package de.mreturkey.authyou.security;

import java.security.MessageDigest;
import java.util.Date;

public class HashUtils {

	public static String getSha256(String value) {
	    try{
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        md.update(value.getBytes());
	        return bytesToHex(md.digest());
	    } catch(Exception ex){
	        throw new RuntimeException(ex);
	    }
	 }
	
	 private static String bytesToHex(byte[] bytes) {
	    StringBuffer result = new StringBuffer();
	    for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
	    return result.toString();
	 }
	
	 //DEBUG
	 public static void main(String[] args){
		 long d1 = new Date().getTime();
		 System.out.println("HASH: "+getSha256("Hallo"));
		 System.out.println("Hat insgesamt "+(new Date().getTime() - d1) +" Millisekunden benätigt");
	 }
}
