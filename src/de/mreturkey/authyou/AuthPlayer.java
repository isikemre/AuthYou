package de.mreturkey.authyou;

import java.net.InetAddress;

import org.bukkit.entity.Player;

import de.mreturkey.authyou.security.Password;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.security.session.SessionDestroyReason;
import de.mreturkey.authyou.util.LogUtil;
import de.mreturkey.authyou.util.QueryThreadAuthPlayer;
import de.mreturkey.authyou.util.SQLQueryType;

public class AuthPlayer {
	
	private Player player;
	
	private final String username;
	private final Password password;
	private final String passwordHash;
	private long lastLogin;
	private InetAddress ip;

	private Session session;
	private boolean loggedIn = false;
	
	protected AuthPlayer(Player player, String username, Password password, String passwordHash, long lastLogin, InetAddress ip, boolean loggedIn) {
		this.player = player;
		this.username = username;
		this.password = password;
		this.passwordHash = passwordHash;
		this.lastLogin = lastLogin;
		this.ip = ip;
		this.loggedIn = loggedIn;
		
		this.session = AuthYou.getSessionManager().generateNewSession(this);
		this.session.setIP(ip);
	}

	public Player getPlayer() {
		return player;
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
		if(loggedIn) {
			if(session != null) {
				session.destroy(SessionDestroyReason.DESTROYED);
			}
			session = AuthYou.getSessionManager().generateNewSession(this);
		}
		this.refresh();
	}
	
	public void setSession(Session session) {
		this.session = session;
	}
	
	public void logout() {
		this.setLoggedIn(false);
		this.session.destroy(SessionDestroyReason.LOGOUT);
		AuthYou.getAuthManager().removeAuthPlayer(this);
	}
	
	public void logout(Player p) {
		this.player = p;
		this.logout();
	}
	
	public void refresh(Player p) {
		this.player = p;
		this.refresh();
	}
	
	private void refresh() {
		this.ip = this.player.getAddress().getAddress();
		this.lastLogin = System.currentTimeMillis();
		this.session.setIP(ip);
		new QueryThreadAuthPlayer(this, SQLQueryType.REFRESH);
	}
	
	public void renewSession() {
		if(!session.isDestroyed()) session.destroy(SessionDestroyReason.DESTROYED);
		session = AuthYou.getSessionManager().generateNewSession(this);
		LogUtil.consoleSenderLog("--- DEBUG --- ["+player.getName()+"] Session renewed with IP ["+player.getAddress().getAddress()+"] at ("
				+System.currentTimeMillis()+") AND is logged in = "+loggedIn+".");
	}
	
}
