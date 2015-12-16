package de.mreturkey.authyou.util;

import de.mreturkey.authyou.config.Message;

public enum KickReason {

	IP_FALSE(Message.INVALID_SESSION),
	TIMEOUT(Message.TIMEOUT),
	LOGOUT(Message.LOGOUT),
	WRONG_PASSWORD(Message.WRONG_PWD);
	
	private final Message message;
	
	private KickReason(Message message) {
		this.message = message;
	}
	
	public String getReason() {
		return message.getMessage(true);
	}
	
	public Message getMessage() {
		return message;
	}
}
