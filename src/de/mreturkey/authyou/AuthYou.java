package de.mreturkey.authyou;

import org.bukkit.plugin.java.JavaPlugin;

import de.mreturkey.authyou.security.session.SessionManager;
import de.mreturkey.authyou.util.MySQL;

public class AuthYou extends JavaPlugin {
	
	private static AuthYou instance;
	private static final SessionManager sessionManager = new SessionManager();
	private static final AuthManager authManager = new AuthManager();
	
	public static AuthYou getInstance() {
		return instance;
	}

	public void onLoad() {
		instance = this;
		MySQL.openConnection();
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public static SessionManager getSessionManager() {
		return sessionManager;
	}
	
	public static AuthManager getAuthManager() {
		return authManager;
	}
	
}
