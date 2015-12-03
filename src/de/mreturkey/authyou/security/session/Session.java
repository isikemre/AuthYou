package de.mreturkey.authyou.security.session;

import java.net.InetAddress;
import java.util.Date;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;

public class Session {

	private final String id;
	private final Date created;

	private SessionState state;
	private SessionDestroyReason destroyReason;
	
	private AuthPlayer authPlayer;
	private boolean destroyed;
	
	private InetAddress ip;
	
	protected Session(final String id) {
		this(id, null);
	}
	
	protected Session(final String id, final AuthPlayer authPlayer) {
		this.id = id;
		this.authPlayer = authPlayer;
		this.created = new Date();
		this.state = authPlayer == null ? SessionState.NOT_IN_USE : SessionState.IN_USE;
		this.destroyReason = null;
	}

	public String getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	public SessionState getState() {
		return state;
	}

	public SessionDestroyReason getDestroyReason() {
		return destroyReason;
	}

	public AuthPlayer getAuthPlayer() {
		return authPlayer;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public InetAddress getIP() {
		return ip;
	}

	public void setState(SessionState state) {
		this.state = state;
	}

	public void setAuthPlayer(AuthPlayer authPlayer) {
		this.authPlayer = authPlayer;
	}

	public void setIP(InetAddress ip) {
		this.ip = ip;
	}

	public void destroy(SessionDestroyReason destroyReason) {
		this.destroyed = true;
		this.destroyReason = destroyReason;
		AuthYou.getSessionManager().removeSession(id);
	}
	
}
