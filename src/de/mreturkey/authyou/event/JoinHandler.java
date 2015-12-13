package de.mreturkey.authyou.event;

import org.bukkit.event.player.PlayerJoinEvent;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.security.session.Session;

public class JoinHandler implements Runnable {

	private final PlayerJoinEvent e;
	
	public JoinHandler(final PlayerJoinEvent e) {
		this.e = e;
	}
	
	@Override
	public void run() {
		try {
			if(Config.getSessionsEnabled) {
				Session session = AuthYou.getSession(e.getPlayer());
				session.getId();
			} else {
				//wait for... thead
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
