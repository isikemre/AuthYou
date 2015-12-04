package de.mreturkey.authyou.security.session;

import java.net.InetAddress;
import java.util.Date;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.util.ExpireThread;

public class Session {

	private final String id;
	private final Date created;
	
	private final ExpireThread expireThread;

	private SessionState state;
	private SessionDestroyReason destroyReason;
	
	private AuthPlayer authPlayer;
	private boolean destroyed;
	
	private InetAddress ip;
	
	protected Session(final String id) {
		this(id, null);
	}
	
	protected Session(final String id, final AuthPlayer authPlayer) {
		this.expireThread = new ExpireThread(this);
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
	
	public ExpireThread geExpireThread() {
		return expireThread;
	}

	public void setState(SessionState state) {
		this.state = state;
	}

	public void setIP(InetAddress ip) {
		this.ip = ip;
	}
	
	public void setAuthPlayer(AuthPlayer authPlayer) {
		this.authPlayer = authPlayer;
	}

	public void destroy(SessionDestroyReason destroyReason) {
		this.destroyed = true;
		this.destroyReason = destroyReason;
		this.state = SessionState.DESTROYED;
		if(this.authPlayer != null) this.authPlayer.setSession(null);
		AuthYou.getSessionManager().removeSession(id);
	}
	
}
