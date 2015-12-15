package de.mreturkey.authyou.security.session;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.util.MySQL;

public class Session {

	private final String id;
	private final UUID uuid;
	private final InetAddress ip;
	private long lastLogin;
	
	private AuthPlayer authPlayer;

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
		
		this.authPlayer = AuthYou.getAuthManager().getQueryedAuthPlayer(this, Bukkit.getPlayer(uuid));
		
		this.destroyed = destroyed;
		this.destroyReason = destroyReason;
		this.state = state;
		AuthYou.getSessionManager().addCachedSession(this);
	}
	
	/**
	 * Use this, to create a new Session.<br>
	 * This Session will be added to the Map (SESSIONS).
	 * @param p
	 */
	protected Session(Player p) {
		this(p.getUniqueId(), AuthYou.getSessionManager().generateId(), p.getAddress().getAddress(), System.currentTimeMillis(), false, DestroyReason.NOT_DESTROYED, SessionState.NOT_IN_USE);
		this.update();
	}

	/**
	 * Returns the ID of this Session.
	 * @return the ID of this Session.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the UUID of the player, which uses this session
	 * @return the UUID of the player
	 */
	public UUID getUniqueId() {
		return uuid;
	}
	
	/**
	 * Returns the AuthPlayer, which uses this session
	 * @return the {@link AuthPlayer}, which uses this session
	 */
	public AuthPlayer getAuthPlayer() {
		return authPlayer;
	}
	
	/**
	 * Returns the Player, which uses this session
	 * @return the {@link Player}, which uses this session
	 */
	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	/**
	 * Returns the IP, which is stored in this Session.<br>
	 * Will be used to check the validation.
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
	 * Checks if this session is valid.<br>
	 * @param p
	 * @return
	 */
	public boolean isValid(Player p) {
		if(state != SessionState.IN_USE) return false;
		if(destroyed) return false;
		
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
	
	/**
	 * Checks wether authPlayer is null.<br>
	 * If it's null, it returns false.<br>
	 * Otherwise true
	 * @return
	 */
	public boolean isPlayerRegisterd() {
		return authPlayer != null;
	}
	
	/**
	 * Checks wether player is logged in and the session is valid.<br>
	 * Returns true if is logged in.<br>
	 * Otherwise false.
	 * @return
	 */
	public boolean isPlayerLoggedIn(Player p) {
		if(authPlayer != null && authPlayer.isLoggedIn() && state == SessionState.IN_USE) return true;
		return false;
	}
	
	public void login(Player p) {
		if(authPlayer == null) throw new NullPointerException("authPlayer is null or not registered");
		this.state = SessionState.IN_USE;
		this.authPlayer.setLoggedIn(true);
		this.lastLogin = System.currentTimeMillis();
		this.destroyed = false;
		this.destroyReason = DestroyReason.NOT_DESTROYED;
		this.update();
	}
	
	/**
	 * Destroys this Session with the given DestroyReason.
	 */
	public void destroy(DestroyReason reason) {
		this.destroyed = true;
		this.destroyReason = reason;
		this.authPlayer.close();
		this.authPlayer = null;
		this.state = SessionState.DESTROYED;
		this.update();
	}
	
	public void update() {
		MySQL.insertOrUpdateSession(this);
	}
}
