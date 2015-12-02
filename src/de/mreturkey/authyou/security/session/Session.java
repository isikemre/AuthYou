package de.mreturkey.authyou.security.session;

import de.mreturkey.authyou.security.AuthPlayer;

public class Session {

	private final String id;
	
	private AuthPlayer authPlayer;
	private boolean expired;
	
	protected Session(String id) {
		this(id, null);
	}
	
	protected Session(String id, AuthPlayer authPlayer) {
		this.id = id;
		this.authPlayer = authPlayer;
	}

	public String getId() {
		return id;
	}

	public AuthPlayer getAuthPlayer() {
		return authPlayer;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setAuthPlayer(AuthPlayer authPlayer) {
		this.authPlayer = authPlayer;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}
	
}
