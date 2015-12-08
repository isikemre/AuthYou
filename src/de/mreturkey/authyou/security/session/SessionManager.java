package de.mreturkey.authyou.security.session;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class SessionManager {

	protected static final SessionIdentifierGenerator IDENTIFIER_GENERATOR = new SessionIdentifierGenerator();
	public static final HashMap<UUID, Session> SESSIONS = new HashMap<>();
	
	public SessionManager() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Generates a new Session for the given Player.
	 * @param p
	 * @return
	 */
	public Session getNewSession(Player p) {
		Session session = new Session(p);
		return session;
	}
	
	/**
	 * Generates a new ID for a Session, which doesn't exist.
	 * @return
	 */
	public String generateId() {
		String id = IDENTIFIER_GENERATOR.nextSessionId();
		while(SESSIONS.containsKey(id)) id = IDENTIFIER_GENERATOR.nextSessionId();
		return id;
	}

	public static SessionIdentifierGenerator getIdentifierGenerator() {
		return IDENTIFIER_GENERATOR;
	}
}

final class SessionIdentifierGenerator {
	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		return new BigInteger(70, random).toString(32);
	}
}
