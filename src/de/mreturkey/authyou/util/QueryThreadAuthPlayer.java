package de.mreturkey.authyou.util;

import org.bukkit.Location;

import de.mreturkey.authyou.AuthPlayer;

public class QueryThreadAuthPlayer extends Thread implements Runnable {
	
	private final AuthPlayer authPlayer;
	private final SQLQueryType queryType;
	
	public QueryThreadAuthPlayer(AuthPlayer authPlayer, SQLQueryType queryType) {
		this.authPlayer = authPlayer;
		this.queryType = queryType;
		this.start();
	}
	
	@Override
	public void run() {
		switch (queryType) {
		case INSERT: {
			final Location loc = authPlayer.getPlayer().getLocation();
			final double x = loc.getX();
			final double y = loc.getY();
			final double z = loc.getZ();
			MySQL.update("INSERT INTO authme (usernamelobby, password, iplobby, lastloginlobby, x, y, z, mz_lobby, emaillobby1, isLogged, realname) "
					+ "VALUES ('"+authPlayer.getUsername()+"', '"+authPlayer.getPasswordHash()+"', "
					+ "'"+authPlayer.getIP().getHostAddress()+"', '"+authPlayer.getLastLogin()+"', "
					+ "'"+x+"', '"+y+"', '"+z+"', 'mz_lobby', 'your@email.com', '"+MySQL.convertBooleanToInteger(authPlayer.isLoggedIn())+"', '"+authPlayer.getPlayer().getName()+"')");
			break;
		}

		case REFRESH:
			MySQL.update("UPDATE authme SET "
					+ "iplobby = '"+authPlayer.getIP().getHostAddress()+"', "
					+ "lastloginlobby = '"+authPlayer.getLastLogin()+"', "
					+ "isLogged = "+MySQL.convertBooleanToInteger(authPlayer.isLoggedIn())
					
					+ " WHERE usernamelobby = '"+authPlayer.getUsername()+"';");
			break;
			
		case UPDATE: {
			final Location loc = authPlayer.getPlayer().getLocation();
			final double x = loc.getX();
			final double y = loc.getY();
			final double z = loc.getZ();
			MySQL.update("UPDATE authme SET "
					+ "usernamelobby = '"+authPlayer.getUsername()+"', "
					+ "password = '"+authPlayer.getPasswordHash()+"', "
					+ "iplobby = '"+authPlayer.getIP().getHostAddress()+"', "
					+ "lastloginlobby = '"+authPlayer.getLastLogin()+"', "
					+ "x = '"+x+"', "
					+ "y = '"+y+"', "
					+ "z = '"+z+"', "
					+ "mz_lobby = '"+ "mz_lobby" +"', "
					+ "emaillobby1 = '"+ "your@email.com" +"', "
					+ "isLogged = "+MySQL.convertBooleanToInteger(authPlayer.isLoggedIn())+", "
					+ "realname = '"+authPlayer.getPlayer().getName()+"'"
					
					+ " WHERE usernamelobby = '"+authPlayer.getUsername()+"';");
			break;
		}
			
		case DELETE:
			MySQL.update("DELETE FROM mz_friends WHERE usernamelobby = '"+authPlayer.getUsername()+"';");
			break;
			
		default:
			break;
		}
	}

}
