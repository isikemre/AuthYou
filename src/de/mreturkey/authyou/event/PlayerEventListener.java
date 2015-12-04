package de.mreturkey.authyou.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.message.Messages;
import de.mreturkey.authyou.util.WaitForLoginThread;
import de.mreturkey.authyou.util.WaitForRegisterThread;

public class PlayerEventListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPreLoignAsync(AsyncPlayerPreLoginEvent e){
		System.out.println("AsyncPlayerPreLoginEvent called");
		if(AuthYou.getAuthManager().preValidAuthentication(e.getUniqueId(), e.getAddress())) {
			e.allow();
		} else {
			e.disallow(Result.KICK_OTHER, Messages.INVALID_SESSION.getMessage(true));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerJoinEvent e) {
		System.out.println("PlayerJoinEvent called");
		AuthPlayer authPlayer = AuthYou.getAuthManager().authenticatePlayer(e.getPlayer());
		if(authPlayer != null) {
			if(!authPlayer.isLoggedIn()) new WaitForLoginThread(e.getPlayer(), 5);
			else Messages.VALID_SESSION.msg(e.getPlayer());
		} else new WaitForRegisterThread(e.getPlayer(), 7);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(authPlayer == null || !authPlayer.isLoggedIn()) {
			e.getPlayer().setVelocity(new Vector().zero());
			e.getPlayer().teleport(e.getPlayer().getLocation());
//			if(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockY() != e.getTo().getBlockY()) {
//				e.getPlayer().teleport(e.getFrom());
//			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		try{
			AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
			if(authPlayer == null || !authPlayer.isLoggedIn()) e.setCancelled(true);
		} catch(Exception ignore){
			
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeave(PlayerQuitEvent e) {
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(authPlayer == null) return;
		if(authPlayer.getSession() == null) return;
		if(!authPlayer.getSession().geExpireThread().isAlive()) authPlayer.getSession().geExpireThread().start();
	}
}
