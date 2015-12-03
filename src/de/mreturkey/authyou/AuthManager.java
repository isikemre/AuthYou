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
	
	public AuthPlayer authenticatePlayer(Player player) {
		if(player == null) throw new NullPointerException("player is null");
		AuthPlayer authPlayer = getAuthPlayer(player);
		if(authPlayer != null) {
			Session session = authPlayer.getSession();
			if(!session.getIP().equals(player.getAddress().getAddress())) kickPlayer(player, authPlayer, KickReason.IP_FALSE);
		} else authPlayer = queryAuthPlayer(player);
		return authPlayer;
	}
	
	public void kickPlayer(Player p, AuthPlayer authPlayer, KickReason kickReason) {
		authPlayer.getSession().destroy(SessionDestroyReason.DESTROYED);
		System.out.println("Kick player simulate");
	}
	
	public AuthPlayer getAuthPlayer(Player player) {
		return AUTH_PLAYERS.get(player.getUniqueId());
	}
	
	private AuthPlayer queryAuthPlayer(Player p) {
		try {
			final ResultSet rs = MySQL.query("SELECT * FROM authme WHERE username = '"+p.getName()+"';");
			if(!rs.first()) return null;
			final Password password = new Password(rs.getString("password"));
			final long lastLogin = rs.getLong("lastLogin");
			final InetAddress ip = InetAddress.getByName(rs.getString("ip"));
			AuthPlayer authPlayer = new AuthPlayer(p.getName(), password, lastLogin, ip);
			this.putAuthPlayer(p.getUniqueId(), authPlayer);
			return authPlayer;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void putAuthPlayer(UUID uuid, AuthPlayer authPlayer) {
		AUTH_PLAYERS.put(uuid, authPlayer);
	}
	
}
