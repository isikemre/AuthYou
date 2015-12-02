package de.mreturkey.authyou.security;

import java.net.InetAddress;

import de.mreturkey.authyou.security.session.Session;

public class AuthPlayer {
	
	private final String username;
	private final Password password;
	private final long lastLogin;
	private final InetAddress lastIP;

	private Session session;
	
	
	public AuthPlayer(String username, Password password, long lastLogin, InetAddress lastIP) {
		this.username = username;
		this.password = password;
		this.lastLogin = lastLogin;
		this.lastIP = lastIP;
	}


	public String getUsername() {
		return username;
	}


	public Password getPassword() {
		return password;
	}


	public long getLastLogin() {
		return lastLogin;
	}


	public InetAddress getLastIP() {
		return lastIP;
	}


	public Session getSession() {
		return session;
	}


	public void setSession(Session session) {
		this.session = session;
	}
	
}
