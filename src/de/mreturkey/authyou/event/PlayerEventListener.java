package de.mreturkey.authyou.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;

public class PlayerEventListener implements Listener {

	@EventHandler
	public void onPreLoignAsync(AsyncPlayerPreLoginEvent e){
		System.out.println("AsyncPlayerPreLoginEvent called");
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e) {
		System.out.println("PlayerJoinEvent called");
		AuthPlayer authPlayer = AuthYou.getAuthManager().authenticatePlayer(e.getPlayer());
		if(authPlayer != null) {
			System.out.println("is registered");
		} else System.out.println("is not registered");
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(!authPlayer.isLoggedIn()) {
			e.getPlayer().setVelocity(new Vector().zero());
			if(e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ() || e.getFrom().getBlockY() != e.getTo().getBlockY()) {
				e.getPlayer().teleport(e.getFrom());
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(!AuthYou.getAuthPlayer(e.getPlayer()).isLoggedIn()) e.setCancelled(true);
	}
}
