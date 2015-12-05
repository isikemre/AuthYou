package de.mreturkey.authyou.util;

import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.message.Messages;

public class JoinHandler extends Thread implements Runnable {

	private final Player player;
	private AuthPlayer authPlayer;

	public JoinHandler(Player player) {
		this.player = player;
		this.start();
	}

	@Override
	public void run() {
		this.authPlayer = AuthYou.getAuthManager().authenticatePlayer(player);
		if (authPlayer != null) {
			if (!authPlayer.isLoggedIn()) {
				new WaitForLoginThread(player, 5);
			} else {
				Messages.VALID_SESSION.msg(player);
				LogUtil.consoleSenderLog("--- DEBUG --- ["+player.getName()+"] Session login with IP ["+player.getAddress().getAddress()+"] at ("
						+System.currentTimeMillis()+") with SessionID = '"+authPlayer.getSession().getId()+"'.");
			}
		} else new WaitForRegisterThread(player, 7);
	}

}
