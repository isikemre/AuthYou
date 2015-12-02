package de.mreturkey.authyou.security;

import de.mreturkey.authyou.util.HashUtils;

public class Password {

	private final String password;
	private String hash;
	
	public Password(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getHash() {
		if(hash == null) hash = HashUtils.getSha256(password);
		return hash;
	}
	
}
