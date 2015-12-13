package de.mreturkey.authyou.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashUtils {
	
	private static final SecureRandom rnd = new SecureRandom();

	/*
	public static String getSha256(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(value.getBytes());
			return bytesToHex(md.digest());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte byt : bytes)
			result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}
	*/
	
	/**
     * Method getSHA256.
     *
     * @param message String
     *
     * @return String * @throws NoSuchAlgorithmException
	 * @throws NoSuchAlgorithmException 
     */
    private static String getSHA256(String message) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        sha256.reset();
        sha256.update(message.getBytes());
        byte[] digest = sha256.digest();
        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
    }

    /**
     * Method getHash.
     *
     * @param password String
     * @param salt     String
     * @param name     String
     *
     * @return String * @throws NoSuchAlgorithmException * @see fr.xephi.authme.security.crypts.EncryptionMethod#getHash(String, String, String)
     * @throws NoSuchAlgorithmException 
     */
    public static String getHash(String password, String salt) throws NoSuchAlgorithmException {
        return "$SHA$" + salt + "$" + getSHA256(getSHA256(password) + salt);
    }

    /**
     * Method comparePassword.
     *
     * @param hash       String
     * @param password   String
     * @param playerName String
     *
     * @return boolean * @throws NoSuchAlgorithmException * @see fr.xephi.authme.security.crypts.EncryptionMethod#comparePassword(String, String, String)
     * @throws NoSuchAlgorithmException 
     */
    public static boolean comparePassword(String hash, String password) throws NoSuchAlgorithmException {
        String[] line = hash.split("\\$");
        return hash.equals(getHash(password, line[2]));
    }
    
    public static String createSalt(int length) throws NoSuchAlgorithmException {
            byte[] msg = new byte[40];
            rnd.nextBytes(msg);
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            sha1.reset();
            byte[] digest = sha1.digest(msg);
            return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest)).substring(0, length);
        }
	
}
