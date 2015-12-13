package de.mreturkey.authyou.event;

import org.bukkit.event.player.PlayerJoinEvent;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.security.session.Session;

public class JoinHandler implements Runnable {

	private PlayerJoinEvent e;
	
	public JoinHandler(PlayerJoinEvent e) {
		this.e = e;
	}
	
	@Override
	public void run() {
		try {
			if(Config.getSessionsEnabled) {
				Session session = AuthYou.getSession(e.getPlayer());
			} else {
				//wait for... thead
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
