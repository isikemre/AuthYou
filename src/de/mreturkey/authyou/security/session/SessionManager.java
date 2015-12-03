package de.mreturkey.authyou.security.session;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

public class SessionManager {

	private static final HashMap<String, Session> SESSIONS = new HashMap<>();
	
	private final SessionIdentifierGenerator sessionIdentifierGenerator;
	
	public SessionManager() {
		this.sessionIdentifierGenerator = new SessionIdentifierGenerator();
	}
	
	public Session generateNewSession() {
		String randomId = generateRandomSessionId();
		while(SESSIONS.containsKey(randomId)) {
			randomId = generateRandomSessionId();
		}
		Session session = new Session(randomId);
		this.putSession(randomId, session);
		return session;
	}
	
	protected String generateRandomSessionId() {
		return sessionIdentifierGenerator.nextSessionId();
	}

	public static HashMap<String, Session> getSessions() {
		return SESSIONS;
	}
	
	public boolean existSession(String sessionId) {
		return SESSIONS.containsKey(sessionId);
	}
	
	public boolean existSession(Session session) {
		return SESSIONS.containsValue(session);
	}
	
	public Session getSession(String sessionId) {
		return SESSIONS.get(sessionId);
	}
	
	public void putSession(String sessionId, Session session) {
		SESSIONS.put(sessionId, session);
	}

	public SessionIdentifierGenerator getSessionIdentifierGenerator() {
		return sessionIdentifierGenerator;
	}
	
	public Session querySession(String query) {
		return null;
	}
	
	public boolean uploadSessionToSQL(Session session) {
		return false;
	}
	
	protected void removeSession(String sessionId) {
		SESSIONS.remove(sessionId);
	}
}

final class SessionIdentifierGenerator {
	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}
}
