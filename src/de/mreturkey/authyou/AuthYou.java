package de.mreturkey.authyou;

import org.bukkit.plugin.java.JavaPlugin;

import de.mreturkey.authyou.security.session.SessionManager;

public class AuthYou extends JavaPlugin {
	
	private static AuthYou instance;
	private static final SessionManager sessionManager = new SessionManager();
	
	public static AuthYou getInstance() {
		return instance;
	}

	public void onLoad() {
		instance = this;
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public static SessionManager getSessionManager() {
		return sessionManager;
	}
	
}
