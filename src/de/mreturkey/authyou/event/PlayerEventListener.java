package de.mreturkey.authyou.event;

import org.bukkit.GameMode;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerEventListener implements Listener {

	public void onPreLoignAsync(AsyncPlayerPreLoginEvent e){
		
	}
	
	public void onPlayerLogin(PlayerJoinEvent e) {
		e.getPlayer().setGameMode(GameMode.SPECTATOR);
	}
	
	public void onPlayerMove(PlayerMoveEvent e) {
		
	}
	
}
