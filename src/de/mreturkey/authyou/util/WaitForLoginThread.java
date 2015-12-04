package de.mreturkey.authyou.util;

import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.message.Messages;

public class WaitForLoginThread extends Thread implements Runnable {

	private final Player player;
	private final int warnLimit;
	
	private int warned = 0;
	
	public WaitForLoginThread(Player player, int warnLimit) {
		this.player = player;
		this.warnLimit = warnLimit;
		this.start();
	}
	
	 @Override
	 public void run() {
		 while(warned <= warnLimit) {
			 try {
				Messages.LOGIN_MSG.msg(player);
				Thread.sleep(TimeUnit.SECONDS.toMillis(5));
				AuthPlayer authPlayer = AuthYou.getAuthPlayer(player);
				if(player == null || !player.isOnline() || authPlayer == null) return;
				if(authPlayer.isLoggedIn()) {
					return;
				}
				warned++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }
		 AuthYou.getAuthManager().kickPlayer(player, AuthYou.getAuthPlayer(player), KickReason.TIMEOUT);
	 }
}
