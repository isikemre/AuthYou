package de.mreturkey.authyou;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.security.Password;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.util.MySQL;

public class AuthPlayer {

	private final int id;
	private final Session session;
	private final Player player;

	private final String username;
	private final UUID uuid;
	private final Password password;
	
	private boolean loggedIn;
	private Location lastLocation;
	
	public AuthPlayer(int id, Session session, Player player, Password password, Location lastLocation, boolean loggedIn) {
		this.id = id;
		this.session = session;
		this.player = player;
		this.uuid = player.getUniqueId();
		this.username = player.getName();
		this.password = password;
		
		this.loggedIn = loggedIn;
		this.lastLocation = lastLocation;
		
		AuthYou.getAuthManager().addAuthPlayer(this);
	}

	public int getId() {
		return id;
	}

	public Session getSession() {
		return session;
	}

	public Player getPlayer() {
		return player;
	}

	public UUID getUniqueId() {
		return uuid;
	}
	
	public String getUsername() {
		return username;
	}

	public Password getPassword() {
		return password;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public Location getLastLocation() {
		return lastLocation;
	}
	
	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public void update() {
		MySQL.updateAuthPlayer(this);
	}
	
	public void close() {
		this.loggedIn = false;
		this.lastLocation = player.getLocation();
		this.update();
		AuthYou.getAuthManager().removeAuthPlayer(this);
	}
}
