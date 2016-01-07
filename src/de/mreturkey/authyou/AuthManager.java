package de.mreturkey.authyou;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.Password;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.util.KickReason;
import de.mreturkey.authyou.util.MySQL;

public final class AuthManager {

	private final ExecutorService cachedPool = Executors.newCachedThreadPool();
	private final HashMap<UUID, AuthPlayer> authPlayers = new HashMap<>();
	
	public void runAsync(Runnable task) {
		cachedPool.execute(task);
	}
	
	public Future<Object> submitAsync(Callable<Object> callable) {
		return cachedPool.submit(callable);
	}
	
	public AuthPlayer getQueryedAuthPlayer(Session s, Player p) {
		Validate.notNull(s, "Session is null");
		Validate.notNull(p, "Player is null");
		if(!s.getUniqueId().equals(p.getUniqueId())) throw new IllegalArgumentException("user of the session doesn't equals with the given player");
		ResultSet rs = MySQL.query("SELECT * FROM "+Config.getSQLTableName+" WHERE "+Config.getSQLColumnUUID+" = '"+p.getUniqueId().toString()+"' OR "+Config.getSQLColumnUsername+" = '"+p.getName()+"'");
		
		try {
			if(!rs.first()) return null;
			
			final int id = rs.getInt(Config.getSQLColumnId);
			
			final Password password = new Password(rs.getString(Config.getSQLColumnPassword));
			final Location loc = new Location(
					Bukkit.getWorld(rs.getString(Config.getSQLColumnLastLocWorld)),
					rs.getDouble(Config.getSQLColumnLastLocX),
					rs.getDouble(Config.getSQLColumnLastLocY),
					rs.getDouble(Config.getSQLColumnLastLocZ));
			final boolean isLogged = rs.getBoolean(Config.getSQLColumnLogged);
			
			return new AuthPlayer(id, s, p, password, loc, isLogged);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int getAmountOfRegIPs(Session s) {
		ResultSet rs = MySQL.query("SELECT "+Config.getSQLColumnIp+" FROM "+Config.getSQLTableName+" WHERE "+Config.getSQLColumnUUID+" = '"+s.getUniqueId().toString()+"' OR "+Config.getSQLColumnUsername+" = '"+s.getPlayer().getName()+"'");
		try {
			int i = 0;
			while(rs.next()) {
				i++;
			}
			return i;
		} catch(SQLException e){
			e.printStackTrace();
		}
		return -1;
	}
	
	public boolean isPlayerRegistered(String username) {
		ResultSet rs = MySQL.query("SELECT "+Config.getSQLColumnId+" FROM "+Config.getSQLTableName+" WHERE "+Config.getSQLColumnUsername+" = '"+username+"'");
		try {
			if(rs.first()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isPlayerRegistered(UUID uuid) {
		ResultSet rs = MySQL.query("SELECT "+Config.getSQLColumnId+" FROM "+Config.getSQLTableName+" WHERE "+Config.getSQLColumnUUID+" = '"+uuid.toString()+"'");
		try {
			if(rs.first()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public AuthPlayer registerNewAuthPlayer(Session session, Player p, String passwordHash) {
		try {
			Object objId = MySQL.insertAuthPlayer(session, p, passwordHash, true);
			int id = 0;
			if(objId instanceof Integer) id = (int) objId;
			else throw new IllegalArgumentException("Cannot cast to Integer");
			return new AuthPlayer(id, session, p, new Password(passwordHash), null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void kickPlayer(final Session s, final KickReason kickReason) {
		if(!Config.kickViaBungeeCord) {
			syncKickPlayer(s.getPlayer(), kickReason.getReason());
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				final Player p = s.getPlayer();
				if(Config.kickViaBungeeCord) {
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("KickPlayer");
					out.writeUTF(p.getName());
					out.writeUTF(kickReason.getReason());
					p.sendPluginMessage(AuthYou.getInstance(), "BungeeCord", out.toByteArray());
				}
			}
		}.runTaskLater(AuthYou.getInstance(), 10);
	}
	
	private void syncKickPlayer(final Player p, final String msg) {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.kickPlayer(msg);
			}
		}.runTaskLater(AuthYou.getInstance(), 10);
	}
	
	public void waitForAuth(final Message message, final Session s) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final long startTime = System.currentTimeMillis();
				while(System.currentTimeMillis() < (startTime + TimeUnit.SECONDS.toMillis(Config.timeout))) {
					if(s.getPlayer() == null || !s.getPlayer().isOnline()) return;
					if(s.isPlayerLoggedIn()) return;
					message.msg(s.getPlayer());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(s.getPlayer() == null || !s.getPlayer().isOnline()) return;
				AuthYou.getAuthManager().kickPlayer(s, KickReason.TIMEOUT);
			}
		}).start();
	}
	
	public HashMap<UUID, AuthPlayer> getAuthPlayers() {
		return authPlayers;
	}
	
	public ExecutorService getCachedThreadPool() {
		return cachedPool;
	}
	
	public void addAuthPlayer(AuthPlayer authPlayer) {
		authPlayers.put(authPlayer.getUniqueId(), authPlayer);
	}
	
	public void removeAuthPlayer(UUID uuid) {
		authPlayers.remove(uuid);
	}
	
	public void removeAuthPlayer(AuthPlayer authPlayer) {
		authPlayers.remove(authPlayer.getUniqueId(), authPlayer);
	}
}
