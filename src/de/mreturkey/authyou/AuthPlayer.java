package de.mreturkey.authyou;

import java.net.InetAddress;

import de.mreturkey.authyou.security.Password;
import de.mreturkey.authyou.security.session.Session;

public class AuthPlayer {
	
	private final String username;
	private final Password password;
	private final String passwordHash;
	private final long lastLogin;
	private final InetAddress ip;

	private Session session;
	private boolean loggedIn = false;
	
	protected AuthPlayer(String username, Password password, String passwordHash, long lastLogin, InetAddress ip) {
		this.username = username;
		this.password = password;
		this.passwordHash = passwordHash;
		this.lastLogin = lastLogin;
		this.ip = ip;
	}


	public String getUsername() {
		return username;
	}

	public Password getPassword() {
		return password;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public InetAddress getIP() {
		return ip;
	}

	public Session getSession() {
		return session;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public void setSession(Session session) {
		this.session = session;
		session.setAuthPlayer(this);
	}
	
	
}
