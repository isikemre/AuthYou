package de.mreturkey.authyou;

import java.net.InetAddress;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.mreturkey.authyou.security.Password;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.security.session.SessionDestroyReason;
import de.mreturkey.authyou.util.KickReason;
import de.mreturkey.authyou.util.MySQL;

public class AuthManager {
	
	private static final HashMap<UUID, AuthPlayer> AUTH_PLAYERS = new HashMap<>();
	
	protected AuthManager() {
		
	}
	
	/**
	 * Handles the whole authentication of the given player.
	 * Returns the AuthPlayer which is assigned to player<br>
	 * If the AuthPlayer doesn't cached it will query the AuthPlayer from the mysql database.
	 * And If the player don't have a entry in the database, it will return null.<br><br>
	 * 
	 * If this method returns null, the player is not registered.
	 * @param player
	 * @return {@link AuthPlayer}
	 */
	public AuthPlayer authenticatePlayer(Player player) {
		if(player == null) throw new NullPointerException("player is null");
		AuthPlayer authPlayer = getAuthPlayer(player);
		if(authPlayer != null) {
			Session session = authPlayer.getSession();
			if(!session.getIP().equals(player.getAddress().getAddress())) kickPlayer(player, authPlayer, KickReason.IP_FALSE);
		} else authPlayer = queryAuthPlayer(player);
		return authPlayer;
	}
	
	/**
	 * Kicks the player via BungeeCord
	 * @param p
	 * @param authPlayer
	 * @param kickReason
	 */
	public void kickPlayer(Player p, AuthPlayer authPlayer, KickReason kickReason) {
		authPlayer.getSession().destroy(SessionDestroyReason.DESTROYED);
		System.out.println("Kick player simulate");
	}
	
	/**
	 * Returns the AuthPlayer which is assigned to player<br>
	 * or if not exist null.
	 * @param player
	 * @return {@link AuthPlayer}
	 */
	public AuthPlayer getAuthPlayer(Player player) {
		return AUTH_PLAYERS.get(player.getUniqueId());
	}
	
	/**
	 * Querys the given player, to check whether the player is registered.<br>
	 * If the player exists in the mysql database, it will create a new AuthPlayer instance, generates and assigns a new Session to/for the AuthPlayer<br>
	 * If the player not exists it will return null.
	 * @param p - Player
	 * @return {@link AuthPlayer} authPlayer
	 */
	private AuthPlayer queryAuthPlayer(Player p) {
		try {
			final ResultSet rs = MySQL.query("SELECT * FROM authme WHERE usernamelobby = '"+p.getName()+"';");
			if(!rs.first()) return null;
			final Password password = new Password(p.getName(), rs.getString("password"));
			final String passwordHash = rs.getString("password");
			final long lastLogin = rs.getLong("lastloginlobby");
			final InetAddress ip = InetAddress.getByName(rs.getString("iplobby"));
			AuthPlayer authPlayer = new AuthPlayer(p.getName(), password, passwordHash, lastLogin, ip);
			authPlayer.setSession(AuthYou.getSessionManager().generateNewSession());
			authPlayer.getSession().setIP(p.getAddress().getAddress());
			this.putAuthPlayer(p.getUniqueId(), authPlayer);
			return authPlayer;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Puts the AuthPlayer to the <code>AUTH_PLAYERS</code> HashMap
	 * @param uuid
	 * @param authPlayer
	 */
	private void putAuthPlayer(UUID uuid, AuthPlayer authPlayer) {
		AUTH_PLAYERS.put(uuid, authPlayer);
	}
	
}
