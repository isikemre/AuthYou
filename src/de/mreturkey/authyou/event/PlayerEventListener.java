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

import de.mreturkey.authyou.AuthManager;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.session.Session;

public class PlayerEventListener implements Listener {

	private final AuthManager authManager = AuthYou.getAuthManager();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLoignAsync(AsyncPlayerPreLoginEvent e){
		if(e.getName().length() < Config.minNicknameLength || e.getName().length() >= Config.maxNicknameLength) {
			e.disallow(Result.KICK_OTHER, Message.NAME_LEN.getMessage(true));
		}
		
		if(!Config.allowedNicknameCharacters.matcher(e.getName()).matches()) {
			e.disallow(Result.KICK_OTHER, Message.REGEX.getMessage(true));
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		authManager.runAsync(new JoinHandler(e));
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		final Session s = AuthYou.getSession(e.getPlayer());
		if(s != null) {
			s.setPlayer(null);
			s.getCache().close();
			s.setCache(null);
			if(s.getAuthPlayer() != null) {
				s.getAuthPlayer().setLoggedIn(false);
				s.getAuthPlayer().update();
			}
			s.update();
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent e) {
		if(Config.allowMovement) return;
		final Session s = AuthYou.getSession(e.getPlayer());
		if(s == null || !s.isPlayerLoggedIn()) e.getPlayer().teleport(e.getPlayer().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		final Session s = AuthYou.getSession(e.getPlayer());
		if(s == null || !s.isPlayerLoggedIn()) e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInvClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			final Player p = (Player) e.getWhoClicked();
			final Session s = AuthYou.getSession(p);
			if(s == null || !s.isPlayerLoggedIn()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInvOpen(InventoryOpenEvent e){
		if(e.getPlayer() instanceof Player) {
			final Player p = (Player) e.getPlayer();
			final Session s = AuthYou.getSession(p);
			if(s == null || !s.isPlayerLoggedIn()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent e) {
		if(!Config.allowChat) {
			final Session s = AuthYou.getSession(e.getPlayer());
			if(s == null || !s.isPlayerLoggedIn()) e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityClickAtEntity(PlayerInteractAtEntityEvent e){
		final Session s = AuthYou.getSession(e.getPlayer());
		if(s == null || !s.isPlayerLoggedIn()) e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityClick(PlayerInteractEntityEvent e){
		final Session s = AuthYou.getSession(e.getPlayer());
		if(s == null || !s.isPlayerLoggedIn()) e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().startsWith("/login") || e.getMessage().startsWith("/l") || e.getMessage().startsWith("/register")) return;
		final Session s = AuthYou.getSession(e.getPlayer());
		if(s == null || !s.isPlayerLoggedIn()) e.setCancelled(true);
	}
	
}
