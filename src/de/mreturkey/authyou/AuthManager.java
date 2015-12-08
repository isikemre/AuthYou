package de.mreturkey.authyou;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthManager {

	private static final ExecutorService CACHED_POOL = Executors.newCachedThreadPool();
	
	public void runAsync(Runnable task) {
		CACHED_POOL.execute(task);
	}
	
	
}
