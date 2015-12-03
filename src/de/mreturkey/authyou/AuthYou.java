package de.mreturkey.authyou;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.mreturkey.authyou.commands.LoginCommand;
import de.mreturkey.authyou.event.PlayerEventListener;
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
		registerEvents();
		registerCommands();
	}
	
	public void onDisable() {
		
	}
	
	public void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);
	}
	
	public void registerCommands() {
		this.getCommand("login").setExecutor(new LoginCommand());
	}
	
	public static SessionManager getSessionManager() {
		return sessionManager;
	}
	
	public static AuthManager getAuthManager() {
		return authManager;
	}
	
	public static AuthPlayer getAuthPlayer(Player player) {
		return authManager.getAuthPlayer(player);
	}
	
}
