package de.mreturkey.authyou.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.message.Messages;
import de.mreturkey.authyou.util.JoinHandler;

public class PlayerEventListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLoignAsync(AsyncPlayerPreLoginEvent e){
		if(!e.getName().matches("[a-zA-Z0-9_?]*")) e.disallow(Result.KICK_OTHER, Messages.REGEX.getMessage(true));
		if(e.getName().length() < 3 || e.getName().length() >= 20) e.disallow(Result.KICK_OTHER, Messages.NAME_LEN.getMessage(true));
		if(AuthYou.getAuthManager().preValidAuthentication(e.getUniqueId(), e.getAddress())) {
			e.allow();
		} else {
			e.disallow(Result.KICK_OTHER, Messages.INVALID_SESSION.getMessage(true));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerJoinEvent e) {
		new JoinHandler(e.getPlayer());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e) {
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(authPlayer == null || !authPlayer.isLoggedIn()) {
			e.getPlayer().setVelocity(new Vector().zero());
			e.getPlayer().teleport(e.getPlayer().getLocation());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(authPlayer == null || !authPlayer.isLoggedIn()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onInv(InventoryClickEvent e) {
		AuthPlayer authPlayer = AuthYou.getAuthPlayer((Player) e.getWhoClicked());
		if(authPlayer == null || !authPlayer.isLoggedIn()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onInvOpen(InventoryOpenEvent e){
		AuthPlayer authPlayer = AuthYou.getAuthPlayer((Player) e.getPlayer());
		if(authPlayer == null || !authPlayer.isLoggedIn()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent e) {
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(authPlayer == null) return;
		if(authPlayer.getSession() == null) return;
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent e) {
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(authPlayer == null || !authPlayer.isLoggedIn()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onEntityClick(PlayerInteractAtEntityEvent e){
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(authPlayer == null || !authPlayer.isLoggedIn()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onEntityClick(PlayerInteractEntityEvent e){
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(authPlayer == null || !authPlayer.isLoggedIn()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().startsWith("/login") || e.getMessage().startsWith("/l") || e.getMessage().startsWith("/register")) return;
		AuthPlayer authPlayer = AuthYou.getAuthPlayer(e.getPlayer());
		if(authPlayer == null || !authPlayer.isLoggedIn()) {
			e.setCancelled(true);
		}
	}
}
