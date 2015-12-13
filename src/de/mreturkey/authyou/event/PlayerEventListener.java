package de.mreturkey.authyou.event;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.mreturkey.authyou.AuthManager;
import de.mreturkey.authyou.AuthYou;

public class PlayerEventListener implements Listener {

	private final AuthManager authManager = AuthYou.getAuthManager();
	
	public void onPlayerJoin(PlayerJoinEvent e) {
		authManager.runAsync(new JoinHandler(e));
	}
}
