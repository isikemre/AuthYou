package de.mreturkey.authyou;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.mreturkey.authyou.security.Password;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.security.session.SessionDestroyReason;
import de.mreturkey.authyou.util.KickReason;
import de.mreturkey.authyou.util.LogUtil;
import de.mreturkey.authyou.util.MySQL;
import de.mreturkey.authyou.util.QueryThreadAuthPlayer;
import de.mreturkey.authyou.util.SQLQueryType;

public class AuthManager {
	
	private static final HashMap<UUID, AuthPlayer> AUTH_PLAYERS = new HashMap<>();
	
	protected AuthManager() {}
	
	/**
	 * Checks the PreLoginEvent.<br>
	 * Returns true if all data is valid. false if not
	 * @param uuid
	 * @param ip
	 * @return
	 */
	public boolean preValidAuthentication(UUID uuid, InetAddress ip) {
		AuthPlayer authPlayer = getAuthPlayer(uuid);
		if(authPlayer != null) {
			Session session = authPlayer.getSession();
			if(session == null || session.isDestroyed()) {
				clearAuthPlayer(uuid, authPlayer);
				return true;
			}
			if(!session.getIP().equals(ip)) {
				authPlayer.logout(false);
				clearAuthPlayer(uuid, authPlayer);
				return false;
			}
		}
		return true;
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
			if(session == null || !session.isDestroyed()) {
				if(!session.getIP().equals(player.getAddress().getAddress())) kickPlayer(player, authPlayer, KickReason.IP_FALSE);
			} else authPlayer.renewSession();
			if(new Date().after(new Date(authPlayer.getLastLogin() + TimeUnit.DAYS.toMillis(3)))) authPlayer.setLoggedIn(false);
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
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("KickPlayer");
			out.writeUTF(p.getName());
			out.writeUTF(kickReason.getReason());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			new BukkitRunnable() {
				public void run() {
					if(p != null && kickReason != null) p.kickPlayer(kickReason.getReason());
				}
			}.runTaskLater(AuthYou.getInstance(), 40);
		}
		LogUtil.consoleSenderLog("--- DEBUG --- ["+p.getName()+"] Player kicked with IP ["+p.getAddress().getAddress()+"] at ("
				+System.currentTimeMillis()+") for "+kickReason.toString()+".");
		p.sendPluginMessage(AuthYou.getInstance(), "BungeeCord", b.toByteArray());
		if(authPlayer == null) return;
		if(authPlayer.isLoggedIn()) authPlayer.setLoggedIn(false);
	}
	
	public AuthPlayer registerPlayer(Player player, String password) {
		LogUtil.consoleSenderLog("--- DEBUG --- ["+player.getName()+"] Registered with IP ["+player.getAddress().getAddress()+"] at ("
				+System.currentTimeMillis()+").");
		Password pw = new Password(player.getName(), password);
		AuthPlayer authPlayer = new AuthPlayer(player, player.getName(), pw, pw.generateHash(), new Date().getTime(),
				player.getAddress().getAddress(), true);
		
		this.insertAuthPlayerToSQL(authPlayer);
		
		this.putAuthPlayer(player.getUniqueId(), authPlayer);
		return authPlayer;
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
	
	public AuthPlayer getAuthPlayer(UUID uuid) {
		return AUTH_PLAYERS.get(uuid);
	}
	
	protected void removeAuthPlayer(AuthPlayer authPlayer) {
		if(authPlayer.getPlayer() != null) {
			AUTH_PLAYERS.remove(authPlayer.getPlayer().getUniqueId());
		}
	}
	
	public boolean isRegistered(Player p) {
		if(this.getAuthPlayer(p) != null) return true;
		try {
			final ResultSet rs = MySQL.query("SELECT * FROM authme WHERE username = '"+p.getName()+"';");
			if(rs.first()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Returns true if registrations not reached the given maxReg.<br>
	 * false if player have exceeded the max number of registrations for player's account
	 * @param username
	 * @param maxReg
	 * @return
	 */
	public boolean checkRegistrations(InetAddress ip, int maxReg) {
		try {
			final ResultSet rs = MySQL.query("SELECT username FROM authme WHERE iplobby = '"+ip.getHostAddress()+"';");
			
			rs.last();
			final int rows = rs.getRow();
			rs.beforeFirst();
			
			if(rows > maxReg) return false;
			return true;
		} catch(SQLException e){
			e.printStackTrace();
			return false;
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
	
	private void insertAuthPlayerToSQL(final AuthPlayer ap) {
		new QueryThreadAuthPlayer(ap, SQLQueryType.INSERT);
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
			final ResultSet rs = MySQL.query("SELECT * FROM authme WHERE username = '"+p.getName()+"';");
			if(!rs.first()) return null;
			
			final Password password = new Password(p.getName(), rs.getString("password"));
			final String passwordHash = rs.getString("password");
			final long lastLogin = rs.getLong("lastloginlobby");
			final InetAddress lastip = InetAddress.getByName(rs.getString("iplobby"));
			boolean loggedIn = rs.getBoolean("isLogged");
			
			if(loggedIn) {
				if(!lastip.equals(p.getAddress().getAddress())) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							MySQL.update("UPDATE authme SET isLogged = 0;");
							LogUtil.consoleSenderLog("--- DEBUG --- ["+p.getName()+"] Query AuthPlayer with IP ["+p.getAddress().getAddress()+"] at ("
									+System.currentTimeMillis()+").");
						}
					}).start();
					loggedIn = false;
				}
			}
			
			AuthPlayer authPlayer = new AuthPlayer(p, p.getName(), password, passwordHash, lastLogin, p.getAddress().getAddress(), loggedIn);
			
			this.putAuthPlayer(p.getUniqueId(), authPlayer);
			LogUtil.consoleSenderLog("--- DEBUG --- ["+p.getName()+"] Query AuthPlayer with IP ["+p.getAddress().getAddress()+"] at ("
					+System.currentTimeMillis()+") AND is logged in = "+authPlayer.isLoggedIn()+".");
			return authPlayer;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void clearAuthPlayer(UUID uuid, AuthPlayer authPlayer) {
		Session session = authPlayer.getSession();
		if(session != null) {
			session.destroy(SessionDestroyReason.DESTROYED);
		}
		authPlayer.clear();
		AUTH_PLAYERS.remove(uuid);
	}
	
}
