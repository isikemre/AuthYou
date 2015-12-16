package de.mreturkey.authyou.event;

import org.bukkit.event.player.PlayerJoinEvent;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.session.Session;

public class JoinHandler implements Runnable {

	private final PlayerJoinEvent e;
	
	public JoinHandler(final PlayerJoinEvent e) {
		this.e = e;
	}
	
	/** <pre>
JOINED -> SESSION_CHECK ->
	IF NO SESSION = LOAD AUTHPLAYER AND CHECK REGISTERED ->
		IF REGISTERED -> WAIT FOR LOGIN -> NEW SESSION -> END;
		IF NOT-REGISTERED -> WAIT FOR REGISTER -> NEW AUTHPLAYER -> NEW SESSION -> END;
	IF SESSION = SESSION_LOGIN -> LOAD AUTHPLAYER -> END;
	</pre>	
	 **/
	
	@Override
	public void run() {
		boolean ignoreReload = false;
		Session session = AuthYou.getSessionManager().getCachedSession(e.getPlayer());
		if(session == null) {
			session = AuthYou.getSessionManager().getQueryedSession(e.getPlayer());
			ignoreReload = true;
			System.out.println("1");
		}
		if(!Config.getSessionsEnabled || session == null) {
			session = AuthYou.getSessionManager().getNewSession(e.getPlayer());
			session.onPlayerJoin(e.getPlayer(), true);
			System.out.println("2");
		} else {
			session.onPlayerJoin(e.getPlayer(), ignoreReload);
			System.out.println("3");
			if(session.isValid(e.getPlayer())) {
				Message.VALID_SESSION.msg(e.getPlayer());
				session.getAuthPlayer().setLoggedIn(true);
				session.getAuthPlayer().update();
				System.out.println("4");
				return;
			} else {
				if(Config.getSessionExpireOnIpChange) {
					session.close();
					session = AuthYou.getSessionManager().getNewSession(e.getPlayer());
					session.onPlayerJoin(e.getPlayer(), true);
					System.out.println("5");
				}
				System.out.println("6");
			}
		}
		if(session.isPlayerRegisterd()) {
			AuthYou.getAuthManager().waitForAuth(Message.LOGIN_MSG, session);
			System.err.println("7");
		} else {
			AuthYou.getAuthManager().waitForAuth(Message.REG_MSG, session);
			System.out.println("8");
		}
	}

}
