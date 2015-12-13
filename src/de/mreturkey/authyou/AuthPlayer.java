package de.mreturkey.authyou;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.security.Password;
import de.mreturkey.authyou.security.session.Session;

public class AuthPlayer {

	private final int id;
	private final Session session;
	private final Player player;
	
	private final String username;
	private final Password password;
	
	private boolean loggedIn;
	private Location lastLocation;
	
	public AuthPlayer(int id, Session session, Player player, String username, Password password, Location lastLocation) {
		this.id = id;
		this.session = session;
		this.player = player;
		this.username = username;
		this.password = password;
		this.loggedIn = false;
		this.lastLocation = lastLocation;
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
		//TODO yap.
	}
}
