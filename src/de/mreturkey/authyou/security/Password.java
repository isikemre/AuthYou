package de.mreturkey.authyou.security;

import java.security.NoSuchAlgorithmException;

import de.mreturkey.authyou.util.HashUtils;

public class Password {

	private final String passwordHash;
	
	/**
	 * Stores the password hash and offers nice utils
	 * @param passwordHash
	 */
	public Password(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public String getHash() {
		return passwordHash;
	}
	
	public boolean compare(String password) {
		try {
			return HashUtils.comparePassword(passwordHash, password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static Password getNewPassword(String password) {
		try {
			return new Password(HashUtils.getHash(password, HashUtils.createSalt(16)));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
