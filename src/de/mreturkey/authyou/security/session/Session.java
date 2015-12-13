package de.mreturkey.authyou.security.session;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.util.MySQL;

public class Session {

	private final String id;
	private final UUID uuid;
	private final InetAddress ip;
	private final long lastLogin;
	
	private boolean destroyed;
	private DestroyReason destroyReason;
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
	protected Session(UUID uuid, String id, InetAddress ip, long lastLogin, boolean destroyed, DestroyReason destroyReason, SessionState state) {
		this.id = id;
		this.uuid = uuid;
		this.ip = ip;
		this.lastLogin = lastLogin;
		
		this.destroyed = destroyed;
		this.destroyReason = destroyReason;
		this.state = state;
		SessionManager.SESSIONS.put(uuid, this);
	}
	
	/**
	 * Use this, to create a new Session.<br>
	 * This Session will be added to the Map (SESSIONS).
	 * @param p
	 */
	protected Session(Player p) {
		this(p.getUniqueId(), AuthYou.getSessionManager().generateId(), p.getAddress().getAddress(), System.currentTimeMillis(), false, DestroyReason.NOT_DESTROYED, SessionState.IN_USE);
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
	 * Returns the reason why this session is destroyed or not.
	 * @return the reason why this session is destroyed or not.
	 */
	public DestroyReason getDestroyReason() {
		return destroyReason;
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
	
	/**
	 * Checks if this session is valid.<br>
	 * @param p
	 * @return
	 */
	public boolean isValid(Player p) {
		if(!p.getAddress().getAddress().equals(ip)) {
			if(Config.getSessionExpireOnIpChange) this.destroy(DestroyReason.IP_CHANGE);
			return false;
		}
		final Date d = new Date();
		if(d.after(new Date(lastLogin + Config.getSessionTimeOut.getTime()))) {
			this.destroy(DestroyReason.EXPIRED);
			return false;
		}
		return true;
		//TODO Sessions table ändern und LastDestroyedReason in authme table rein
	}
	
	public void update() {
		MySQL.insertOrUpdateSession(this);
	}
}
