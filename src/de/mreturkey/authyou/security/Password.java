package de.mreturkey.authyou.security;

import java.security.NoSuchAlgorithmException;

import de.mreturkey.authyou.util.HashUtils;

public class Password {

	private final String username;
	private final String password;
	private String salt;
	private String hash;
	
	public Password(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getSalt() throws NoSuchAlgorithmException {
		return salt == null ? HashUtils.createSalt(16) : salt;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String generateHash() {
		if(hash == null)
			try {
				hash = HashUtils.getHash(password, getSalt(), username);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		return hash;
	}
	
}
