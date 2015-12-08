package de.mreturkey.authyou.security.session;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.mreturkey.authyou.util.MySQL;

public class SessionManager {

	protected static final SessionIdentifierGenerator IDENTIFIER_GENERATOR = new SessionIdentifierGenerator();
	public static final HashMap<UUID, Session> SESSIONS = new HashMap<>();
	
	public SessionManager() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Returns the cached Session wich is linked with the given player.<br>
	 * You need to check wether the cached Session is valid.
	 * @param p
	 * @return
	 */
	public Session getCachedSession(Player p) {
		return SESSIONS.get(p.getUniqueId());
	}
	
	public Session querySession(Player p) {
		ResultSet rs = MySQL.query("SELECT * FROM sessions WHERE uuid = '"+p.getUniqueId().toString()+"'");
		try {
			rs.getString("");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	 * Generates a new ID for a Session, which doesn't exist.<br>
	 * The length of Session ID's is always 14.
	 * @return a new ID for a Session
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
