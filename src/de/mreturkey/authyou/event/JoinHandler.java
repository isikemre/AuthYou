package de.mreturkey.authyou.event;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.security.session.cache.Cache;

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
		try {
			final Player p = e.getPlayer();
			Session session;
			
			// Get Session in RAM or MySQL, but no generate new Session!
			if(Config.getSessionsEnabled) {
				Session s = AuthYou.getSessionManager().getCachedSession(p);
				
				if(s == null) {
					s = AuthYou.getSessionManager().getQueryedSession(p);
				} else {
					if(!s.reload(p.getUniqueId(), p)) {
						s.close(); //H‰‰‰hh
						s = null;
					}
				}
				
				session = s;
			} else {
				session = AuthYou.getSessionManager().getCachedSession(p);
				if(session != null) session.reload();
			}
			
			//Now check if is null, to generate a new session if is null or not if is not null
			if(session == null) {
				session = AuthYou.getSessionManager().getNewSession(p);
			}
			
			session.setPlayer(p);
			session.setCache(new Cache(session));
			
			//If not registered, player need to register. And returns.
			if(!session.isPlayerRegisterd()) {
				AuthYou.getAuthManager().waitForAuth(Message.REG_MSG, session);
				return;
			}
			
			//Check is Valid
			if(session.isValid(p)) {
				Message.VALID_SESSION.msg(p);
				session.login(p);
			} else {
				session.loadAuthPlayer(p);
				AuthYou.getAuthManager().waitForAuth(Message.LOGIN_MSG, session);
			}
			
		} catch(Exception e) {
			
		}
	}

}
