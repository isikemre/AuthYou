package de.mreturkey.authyou;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.Password;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.util.KickReason;
import de.mreturkey.authyou.util.LogUtil;
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
		ResultSet rs = MySQL.query("SELECT "+Config.getSQLColumnIp+" FROM "+Config.getSQLTableName+" WHERE "+Config.getSQLColumnUsername+" = '"+s.getUniqueId().toString()+"'");
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
	
	public AuthPlayer registerNewAuthPlayer(Session session, Player p, String passwordHash) throws InterruptedException, ExecutionException {
		Future<Object> future = MySQL.insertAuthPlayer(session, p, passwordHash, true);
		try {
			int id = (int) future.get(30, TimeUnit.SECONDS);
			return new AuthPlayer(id, session, p, new Password(passwordHash), null, true);
		} catch (TimeoutException e) {
			e.printStackTrace();
			LogUtil.consoleSenderLog("MySQL Thread timed out.");
		}
		return null;
	}
	
	public void kickPlayer(final Session s, final KickReason kickReason) {
		final Player p = s.getPlayer();
		if(Config.kickViaBungeeCord) {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("KickPlayer");
				out.writeUTF(p.getName());
				out.writeUTF(kickReason.getReason());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if(p == null || p.isOnline())
					p.kickPlayer(kickReason.getReason());
			}
		}.runTaskLater(AuthYou.getInstance(), 60);
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
