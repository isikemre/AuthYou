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
	
	public Session generateNewSession(){
		String randomId = generateRandomSessionId();
		while(SESSIONS.containsKey(randomId)) {
			randomId = generateRandomSessionId();
		}
		Session session = new Session(randomId);
		this.putSession(randomId, session);
		return session;
	}
	
	public String generateRandomSessionId(){
		return sessionIdentifierGenerator.nextSessionId();
	}

	public static HashMap<String, Session> getSessions() {
		return SESSIONS;
	}
	
	public Session getSession(String sessionId){
		return SESSIONS.get(sessionId);
	}
	
	public void putSession(String sessionId, Session session) {
		SESSIONS.put(sessionId, session);
	}

	public SessionIdentifierGenerator getSessionIdentifierGenerator() {
		return sessionIdentifierGenerator;
	}
	
}

final class SessionIdentifierGenerator {
	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		return new BigInteger(130, random).toString(32);
	}
}
