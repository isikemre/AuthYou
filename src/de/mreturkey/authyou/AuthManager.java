package de.mreturkey.authyou;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.session.DestroyReason;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.security.session.SessionState;
import de.mreturkey.authyou.util.KickReason;
import de.mreturkey.authyou.util.MySQL;

public class AuthManager {

	private static final ExecutorService CACHED_POOL = Executors.newCachedThreadPool();
	
	public void runAsync(Runnable task) {
		CACHED_POOL.execute(task);
	}
	
	public AuthPlayer getQueryedAuthPlayer(Player p) {
		ResultSet rs = MySQL.query("SELECT * FROM "+Config.getSQLTableName+" WHERE uuid = '"+p.getUniqueId().toString()+"'");
		try {
			if(!rs.first()) return null;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void kickPlayer(final AuthPlayer authPlayer, final KickReason kickReason) {
		if(Config.kickViaBungeeCord) {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("KickPlayer");
				out.writeUTF(authPlayer.getPlayer().getName());
				out.writeUTF(kickReason.getReason());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				authPlayer.getPlayer().kickPlayer(kickReason.getReason());
			}
		}.runTaskLater(AuthYou.getInstance(), 20);
	}
	
	public void waitForAuth(final Message message, final AuthPlayer ap) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final long startTime = System.currentTimeMillis();
				while(System.currentTimeMillis() < (startTime + TimeUnit.SECONDS.toMillis(Config.timeout))) {
					if(ap.isLoggedIn()) return;
					message.msg(ap.getPlayer());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				AuthYou.getAuthManager().kickPlayer(ap, KickReason.TIMEOUT);
			}
		}).start();
	}
	
}
