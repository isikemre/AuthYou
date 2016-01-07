package de.mreturkey.authyou;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.mreturkey.authyou.commands.AuthyouCmd;
import de.mreturkey.authyou.commands.ChangepasswordCmd;
import de.mreturkey.authyou.commands.LoginCmd;
import de.mreturkey.authyou.commands.LogoutCmd;
import de.mreturkey.authyou.commands.RegisterCmd;
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
		this.getCommand("login").setExecutor(new LoginCmd());
		this.getCommand("register").setExecutor(new RegisterCmd());
		this.getCommand("changepassword").setExecutor(new ChangepasswordCmd());
		this.getCommand("logout").setExecutor(new LogoutCmd());
		this.getCommand("authyou").setExecutor(new AuthyouCmd());
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
	
	/**
	 * Returns the cached session of the given player.
	 * @param p
	 * @return
	 */
	public static Session getSession(Player p) {
		Validate.notNull(p, "player cannot be null");
		if(!p.isOnline()) throw new IllegalArgumentException("player need to be online!");
		return sessionManager.getCachedSession(p);
	}
	
	public static Session getSession(String username) {
		Validate.notNull(username, "username cannot be null");
		for(Session s : sessionManager.getSessions().values()) {
			if(s != null && s.getUsername().equalsIgnoreCase(username)) return s;
		}
		return null;
	}
}
