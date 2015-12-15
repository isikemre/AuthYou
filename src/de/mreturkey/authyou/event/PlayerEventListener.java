package de.mreturkey.authyou.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import de.mreturkey.authyou.AuthManager;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.security.session.Session;

public class PlayerEventListener implements Listener {

	private final AuthManager authManager = AuthYou.getAuthManager();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		authManager.runAsync(new JoinHandler(e));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent e) {
		final Session s = AuthYou.getSession(e.getPlayer());
		if(s == null || !s.isPlayerLoggedIn(e.getPlayer())) e.getPlayer().teleport(e.getPlayer().getLocation());
		
	}
}
