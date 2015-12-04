package de.mreturkey.authyou.util;

import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.message.Messages;

public class WaitForRegisterThread extends Thread implements Runnable {
	
	private final Player player;
	private final int warnLimit;
	
	private int warned;
	
	public WaitForRegisterThread(Player player, int warnLimit) {
		this.player = player;
		this.warnLimit = warnLimit;
		this.start();
	}
	
	 @Override
	 public void run() {
		 while(warned <= warnLimit) {
			 try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(5));
				AuthPlayer authPlayer = AuthYou.getAuthPlayer(player);
				if(authPlayer != null) {
					if(authPlayer.isLoggedIn()) {
						return;
					}
				}
				Messages.REG_MSG.msg(player);
				warned++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 }
		 AuthYou.getAuthManager().kickPlayer(player, AuthYou.getAuthPlayer(player), KickReason.TIMEOUT);
	 }
}
