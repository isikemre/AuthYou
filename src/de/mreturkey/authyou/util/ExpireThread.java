package de.mreturkey.authyou.util;

import java.util.concurrent.TimeUnit;

import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.security.session.SessionDestroyReason;

public class ExpireThread extends Thread implements Runnable {

	private final Session session;
	
	public ExpireThread(Session session) {
		this.session = session;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(TimeUnit.HOURS.toMillis(4));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(!session.isDestroyed()) session.destroy(SessionDestroyReason.EXPIRED);
	}
	
}
