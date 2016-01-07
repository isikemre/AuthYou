package de.mreturkey.authyou.security.session;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.mreturkey.authyou.util.MySQL;

public final class SessionManager {

	private final SessionIdentifierGenerator identifierGenerator = new SessionIdentifierGenerator();
	private final HashMap<UUID, Session> sessions = new HashMap<>();
	private final HashMap<String, Object> sessionIds = new HashMap<>(); //Object (value) is always NULL
	
	public SessionManager() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Returns the cached Session wich is linked with the given player.<br>
	 * If the returned object is null, the player doesn't exist in the cache.<br><br>
	 * You need to check wether the cached Session is valid.
	 * @param p
	 * @return
	 */
	public Session getCachedSession(Player p) {
		return sessions.get(p.getUniqueId());
	}
	
	public HashMap<UUID, Session> getSessions() {
		return sessions;
	}
	
	/**
	 * Returns the session which is stored in mysql.<br>
	 * If the returned object is null, the player doesn't exist in the mysql database or the session is destroyed.
	 * @param p
	 * @return
	 */
	public Session getQueryedSession(Player p) {
		ResultSet rs = MySQL.query("SELECT * FROM session WHERE uuid = '"+p.getUniqueId().toString()+"'");
		try {
			if(!rs.first()) return null;
			
			final boolean destroyed = rs.getBoolean("destroyed");
			
			final UUID uuid = UUID.fromString(rs.getString("uuid"));
			final String id = rs.getString("id");
			final InetAddress ip = InetAddress.getByName(rs.getString("ip"));
			final long lastLogin = rs.getTimestamp("last_login").getTime();

			final DestroyReason destroyReason = DestroyReason.valueOf(rs.getString("destroy_reason"));
			final SessionState state = SessionState.valueOf(rs.getString("state"));
			return new Session(uuid, id, ip, lastLogin, destroyed, destroyReason, state, p);
		} catch (SQLException | UnknownHostException e) {
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
		return new Session(p);
	}
	
	public void addCachedSession(Session session) {
		sessions.put(session.getUniqueId(), session);
	}
	
	public void removeCachedSession(Session session) {
		sessions.remove(session.getUniqueId(), session);
	}
	
	public void removeCachedSession(UUID uuid) {
		sessions.remove(uuid);
	}
	
	/**
	 * Generates a new ID for a Session, which doesn't exist.<br>
	 * The length of Session ID's is always 14.
	 * @return a new ID for a Session
	 */
	public String generateId() {
		String id = identifierGenerator.nextSessionId();
		while(sessionIds.containsKey(id)) id = identifierGenerator.nextSessionId();
		sessionIds.put(id, null);
		return id;
	}

	public SessionIdentifierGenerator getIdentifierGenerator() {
		return identifierGenerator;
	}
}

final class SessionIdentifierGenerator {
	private SecureRandom random = new SecureRandom();

	public String nextSessionId() {
		return new BigInteger(70, random).toString(32);
	}
}
