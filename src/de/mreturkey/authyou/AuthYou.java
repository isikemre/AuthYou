package de.mreturkey.authyou;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.event.PlayerEventListener;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.security.session.SessionManager;
import de.mreturkey.authyou.util.MySQL;

public class AuthYou extends JavaPlugin {
	
	private static AuthYou instance;
	private static final SessionManager sessionManager = new SessionManager();
	private static final AuthManager authManager = new AuthManager();
	private static Config config;
	
	public static AuthYou getInstance() {
		return instance;
	}

	public void onLoad() {
		instance = this;
		config = new Config();
		MySQL.openConnection();
	}
	
	public void onEnable() {
		registerEvents();
		registerCommands();
	}
	
	public void onDisable() {
		MySQL.close();
	}
	
	public void registerEvents() {
		if(Config.kickViaBungeeCord) Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);
	}
	
	public void registerCommands() {
		
	}
	
	public static SessionManager getSessionManager() {
		return sessionManager;
	}
	
	public static AuthManager getAuthManager() {
		return authManager;
	}
	
	public static Config getConf() {
		return config;
	}
	
	public static Session getSession(Player p) {
		Validate.notNull(p, "player cannot be null");
		if(!p.isOnline()) throw new IllegalArgumentException("player need to be online!");
		Session session = sessionManager.getCachedSession(p);
		if(session == null) {
			session = sessionManager.getQueryedSession(p);
		}
		return session;
	}
}
