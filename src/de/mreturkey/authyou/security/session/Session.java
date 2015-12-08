package de.mreturkey.authyou.security.session;

import java.net.InetAddress;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthYou;

public class Session {

	private final String id;
	private final UUID uuid;
	private final InetAddress ip;
	private final long lastLogin;
	
	private boolean destroyed;
	private SessionState state;
	
	/**
	 * Use this, to create a existing Session.<br>
	 * This Session will be added to the Map (SESSIONS).
	 * @param id
	 * @param ip
	 * @param lastLogin
	 * @param destroyed
	 * @param state
	 */
	public Session(UUID uuid, String id, InetAddress ip, long lastLogin, boolean destroyed, SessionState state) {
		this.id = id;
		this.uuid = uuid;
		this.ip = ip;
		this.lastLogin = lastLogin;
		
		this.destroyed = destroyed;
		this.state = state;
		SessionManager.SESSIONS.put(uuid, this);
	}
	
	/**
	 * Use this, to create a new Session.<br>
	 * This Session will be added to the Map (SESSIONS).
	 * @param p
	 */
	public Session(Player p) {
		this(p.getUniqueId(), AuthYou.getSessionManager().generateId(), p.getAddress().getAddress(), System.currentTimeMillis(), false, SessionState.IN_USE);
	}

	/**
	 * Returns the ID of this Session.
	 * @return the ID of this Session.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the UUID of the player, which is linked to this Session.
	 * @return the UUID of the player
	 */
	public UUID getUniqueId() {
		return uuid;
	}

	/**
	 * Returns the IP, which is stored in this Session.<br>
	 * Will be used to check the authentication.
	 * @return the IP, which is stored in this Session.
	 */
	public InetAddress getIp() {
		return ip;
	}
	
	/**
	 * Returns the last login of the player, which is linked with this Session, in Milliseconds.
	 * @return the last login of the player
	 */
	public long getLastLogin() {
		return lastLogin;
	}

	/**
	 * Returns true if this Session is destroyed. Otherwise false.<br>
	 * If this Session is destroyed, you cannot change nothings anymore of this Session.
	 * @return true if this Session is destroyed. Otherwise false.
	 */
	public boolean isDestroyed() {
		return destroyed;
	}

	/**
	 * Returns the State of this Session.
	 * @return the State of this Session.
	 */
	public SessionState getState() {
		return state;
	}

	/**
	 * Destroys this Session with the given DestroyReason.
	 */
	public void destroy(DestroyReason reason) {
		//TODO destroy()
	}
}
