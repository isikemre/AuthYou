package de.mreturkey.authyou.security.session.cache;

import java.util.HashMap;

import de.mreturkey.authyou.security.session.Session;

public class Cache {

	private static final int cacheKeysLength = CacheKey.values().length;
	
	private final Session session;
	private HashMap<CacheKey, Object> data = new HashMap<>(cacheKeysLength);
	
	public Cache(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}
	
	public void set(CacheKey key, Object value) {
		data.put(key, value);
	}
	
	public Object get(CacheKey key) {
		return data.get(key);
	}
	
	public boolean isMaxRegReached() {
		return (boolean) data.get(CacheKey.MAX_REG_REACHED);
	}
	
	public void close() {
		data.clear();
	}
	
}