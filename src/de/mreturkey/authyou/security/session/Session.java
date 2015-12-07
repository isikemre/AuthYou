package de.mreturkey.authyou.security.session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.util.LogUtil;

public class Session {

	private final String id;
	private final Date created;
	
	private SessionState state;
	private SessionDestroyReason destroyReason;
	
	private AuthPlayer authPlayer;
	private boolean destroyed;
	
	private InetAddress ip;
	
	protected Session(final String id) {
		this(id, null);
	}
	
	protected Session(final String id, final AuthPlayer authPlayer) {
		this.id = id;
		this.authPlayer = authPlayer;
		this.created = new Date();
		this.state = authPlayer == null ? SessionState.NOT_IN_USE : SessionState.IN_USE;
		this.destroyReason = SessionDestroyReason.NOT_DESTROYED;
		LogUtil.sessionLogToFile(this, "OPEN");
	}

	public String getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	public SessionState getState() {
		return state;
	}

	public SessionDestroyReason getDestroyReason() {
		return destroyReason;
	}

	public AuthPlayer getAuthPlayer() {
		return authPlayer;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public InetAddress getIP() {
		return ip;
	}

	public void setState(SessionState state) {
		this.state = state;
	}

	public void setIP(InetAddress ip) {
		this.ip = ip;
	}
	
	public void setAuthPlayer(AuthPlayer authPlayer) {
		this.authPlayer = authPlayer;
	}

	public void destroy(SessionDestroyReason destroyReason) {
		this.destroyed = true;
		this.destroyReason = destroyReason;
		this.state = SessionState.DESTROYED;
		if(this.authPlayer != null) this.authPlayer.setSession(null);
		AuthYou.getSessionManager().removeSession(id);
		LogUtil.sessionLogToFile(this, "DESTROY");
	}
	
	@SuppressWarnings("deprecation")
	public String toLog(String opendOrDestroy) {
		final String username = authPlayer.getUsername();
		final String realname = authPlayer.getPlayer().getName() == null ? "NULL" : authPlayer.getPlayer().getName();
		final String isLogged = authPlayer.isLoggedIn()+"";
		final Date d = new Date();
		final String date = d.getDay() +"." + d.getMonth() + "." + d.getYear()+" - "+d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
		if(ip == null)
			try {
				ip = InetAddress.getByName("0.0.0.0");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		return date+": SESSION["+id+"] \""+opendOrDestroy+"\" = IP["+ip.getHostAddress()+"] / AP["+username+", "+realname+", "+isLogged+"] / E["+destroyed+", "+state.toString()+", ["+destroyReason.toString()+"]";
	}
	
}
