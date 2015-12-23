package de.mreturkey.authyou.security.session.cache;

import de.mreturkey.authyou.security.session.Session;

public class Cache {

	private final Session session;
	
	public Cache(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}

	public void close() {
		
	}
	
}
