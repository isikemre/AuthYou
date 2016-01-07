package de.mreturkey.authyou.security.session;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.security.session.cache.Cache;
import de.mreturkey.authyou.util.MySQL;

public class Session {

	private final String id;
	private final UUID uuid;
	private final InetAddress ip;
	private long lastLogin;
	
	private final String username;
	
	private AuthPlayer authPlayer;
	private Player player;

	private boolean destroyed;
	private DestroyReason destroyReason;
	private SessionState state;
	
	private Cache cache;
	
	/**
	 * Use this, to create a existing Session.<br>
	 * This Session will be added to the Map (SESSIONS).
	 * @param id
	 * @param ip
	 * @param lastLogin
	 * @param destroyed
	 * @param state
	 */
	protected Session(UUID uuid, String id, InetAddress ip, long lastLogin, boolean destroyed, DestroyReason destroyReason, SessionState state, Player p) {
		this.id = id;
		this.uuid = uuid;
		this.ip = ip;
		this.lastLogin = lastLogin;
		
		this.player = p;
		this.authPlayer = AuthYou.getAuthManager().getQueryedAuthPlayer(this, player);
		this.username = p.getName();
		
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
		this(p.getUniqueId(), AuthYou.getSessionManager().generateId(), p.getAddress().getAddress(), System.currentTimeMillis(), false, DestroyReason.NOT_DESTROYED, SessionState.NOT_IN_USE, p);
		if(Config.getSessionsEnabled) this.insert(); //Local-Work
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
		return player;
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
	
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the State of this Session.
	 * @return the State of this Session.
	 */
	public SessionState getState() {
		return state;
	}
	
	public Cache getCache() {
		return cache;
	}
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}
	
	public void setAuthPlayer(AuthPlayer ap) {
		Validate.notNull(ap, "AuthPlayer cannot be null!");
		if(!ap.getUniqueId().equals(uuid)) throw new IllegalArgumentException("The UUID of authPlayer does not equals with the UUID in the session");
		this.authPlayer = ap;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Checks if this session is valid.<br>
	 * Destroys the session if 'ExpireOnIpChange' is true.<br><br>
	 * 
	 * Same effect like<br>
	 * <blockquote><pre>
	 * isVaild(player, false);
	 * </pre></blockquote>
	 * 
	 * @param p
	 * @return true if valid. Otherwise false.
	 */
	public boolean isValid(Player p) {
		return isValid(p, false);
	}
	
	/**
	 * Checks if this session is valid.<br>
	 * Destroys the session if 'ExpireOnIpChange' is true AND dontDestroy is false.<br><br>
	 * 
	 * @param p
	 * @return true if valid. Otherwise false.
	 */
	public boolean isValid(Player p, boolean dontDestroy) {
		if(state != SessionState.IN_USE) return false;
		if(destroyed) return false;
		
		if(!p.getAddress().getAddress().equals(ip)) {
			if(Config.getSessionExpireOnIpChange && !dontDestroy) this.destroy(DestroyReason.IP_CHANGE);
			return false;
		}
		final Date d = new Date();
		if(d.after(new Date(lastLogin + Config.getSessionTimeOut.getTime()))) {
			if(!dontDestroy) this.destroy(DestroyReason.EXPIRED);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks whether authPlayer is null.<br>
	 * If it's null, it returns false.<br>
	 * Otherwise true
	 * @return
	 */
	public boolean isPlayerRegisterd() {
		return authPlayer != null;
	}
	
	/**
	 * Checks whether player is logged in and the session is valid.<br>
	 * Returns true if is logged in.<br>
	 * Otherwise false.
	 * @return
	 */
	public boolean isPlayerLoggedIn() {
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
		this.authPlayer.update();
	}
	
	public void logout(Player p) throws InterruptedException, ExecutionException {
		if(authPlayer == null) throw new NullPointerException("authPlayer is null or not registered");
		this.destroy(DestroyReason.LOGOUT);
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
		if(Config.getSessionsEnabled)
			MySQL.insertOrUpdateSession(this);
	}
	
	/**
	 * Returns the ID which is inserted.
	 * @return
	 */
	public String insert() {
		if(Config.getSessionsEnabled) return this.id;
		return MySQL.insertSessionAndGetID(this);
	}
	
	/**
	 * Reload's this Session.<br><br>
	 * Returns true if the session reloaded successful.<br>
	 * But if the session is destroyed, the reload failed<br>
	 * or the session doesn't exist, it will return false.
	 * 
	 * @return true if reload was successfully. Otherwise false.<br>If false you need to generate a new session.
	 */
	public boolean reload(UUID uuid, Player player) {
		Validate.notNull(uuid, "UUID is null");
		if(!Config.getSessionsEnabled) {
			this.authPlayer = AuthYou.getAuthManager().getQueryedAuthPlayer(this, player);
			return true;
		}
		ResultSet rs = MySQL.query("SELECT * FROM session WHERE id = '"+id+"' AND uuid = '"+uuid.toString()+"'");
		try {
			if(rs.first()) {
				boolean destroyed = rs.getBoolean("destroyed");
				InetAddress ip = InetAddress.getByName(rs.getString("ip"));
				if(destroyed || !ip.equals(this.ip)) {
					this.close();
					return false;
				}
				this.lastLogin = rs.getTimestamp("last_login").getTime();
				this.state = SessionState.valueOf(rs.getString("state"));
				this.destroyed = destroyed;
				this.destroyReason = DestroyReason.valueOf(rs.getString("destroy_reason"));
				
				this.authPlayer = AuthYou.getAuthManager().getQueryedAuthPlayer(this, player);
				return true;
			} else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean reload() {
		return reload(this.uuid, this.player);
	}
	
	public void loadAuthPlayer(Player player) {
		this.authPlayer = AuthYou.getAuthManager().getQueryedAuthPlayer(this, player);
	}
	
	/**
	 * Closes this Session.<br><br>
	 * 
	 * This session will be removed from mysql database.<br>
	 * Also from the CachedSession list
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public void close() throws InterruptedException, ExecutionException {
		if(this.authPlayer != null) this.authPlayer.close();
		this.authPlayer = null;
		if(Config.getSessionsEnabled) MySQL.deleteSession(this);
		
		AuthYou.getSessionManager().removeCachedSession(this);
	}
	
}
