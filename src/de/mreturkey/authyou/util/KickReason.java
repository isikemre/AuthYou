package de.mreturkey.authyou.util;

import de.mreturkey.authyou.message.Messages;

public enum KickReason {

	IP_FALSE(Messages.INVALID_SESSION),
	TIMEOUT(Messages.TIMEOUT),
	LOGOUT(Messages.LOGOUT);
	
	private final Messages message;
	
	private KickReason(Messages message) {
		this.message = message;
	}
	
	public String getReason() {
		return message.getMessage(true);
	}
	
	public Messages getMessage() {
		return message;
	}
}
