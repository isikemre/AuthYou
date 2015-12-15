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
		Session session = AuthYou.getSession(e.getPlayer());
		if(!Config.getSessionsEnabled || session == null) {
			session = AuthYou.getSessionManager().getNewSession(e.getPlayer());
		} else {
			if(session.isValid(e.getPlayer())) {
				Message.VALID_SESSION.msg(e.getPlayer());
				return;
			}
		}
		if(session.isPlayerRegisterd()) {
			AuthYou.getAuthManager().waitForAuth(Message.LOGIN_MSG, session);
		} else {
			AuthYou.getAuthManager().waitForAuth(Message.REG_MSG, session);
		}
	}

}
