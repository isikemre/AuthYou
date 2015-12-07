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
			MySQL.update("INSERT INTO authme (username, password, iplobby, lastloginlobby, x, y, z, mz_lobby, emaillobby1, isLogged, uuid) "
					+ "VALUES ('"+authPlayer.getUsername()+"', '"+authPlayer.getPasswordHash()+"', "
					+ "'"+authPlayer.getIP().getHostAddress()+"', '"+authPlayer.getLastLogin()+"', "
					+ "'"+x+"', '"+y+"', '"+z+"', 'mz_lobby', 'your@email.com', '"+MySQL.convertBooleanToInteger(authPlayer.isLoggedIn())+"', '"+authPlayer.getPlayer().getUniqueId()+"')");
			break;
		}

		case REFRESH:
			MySQL.update("UPDATE authme SET "
					+ "iplobby = '"+authPlayer.getIP().getHostAddress()+"', "
					+ "lastloginlobby = '"+authPlayer.getLastLogin()+"', "
					+ "isLogged = "+MySQL.convertBooleanToInteger(authPlayer.isLoggedIn())
					
					+ " WHERE username = '"+authPlayer.getUsername()+"';");
			break;
			
		case UPDATE: {
			final Location loc = authPlayer.getPlayer().getLocation();
			final double x = loc.getX();
			final double y = loc.getY();
			final double z = loc.getZ();
			MySQL.update("UPDATE authme SET "
					+ "username = '"+authPlayer.getUsername()+"', "
					+ "password = '"+authPlayer.getPasswordHash()+"', "
					+ "iplobby = '"+authPlayer.getIP().getHostAddress()+"', "
					+ "lastloginlobby = '"+authPlayer.getLastLogin()+"', "
					+ "x = '"+x+"', "
					+ "y = '"+y+"', "
					+ "z = '"+z+"', "
					+ "mz_lobby = '"+ "mz_lobby" +"', "
					+ "emaillobby1 = '"+ "your@email.com" +"', "
					+ "isLogged = "+MySQL.convertBooleanToInteger(authPlayer.isLoggedIn())+", "
					+ "uuid = '"+authPlayer.getPlayer().getUniqueId()+"'"
					
					+ " WHERE username = '"+authPlayer.getUsername()+"';");
			break;
		}
		
		case LOGGED_CHANGE: {
			MySQL.update("UPDATE authme SET "
					+ "isLogged = "+MySQL.convertBooleanToInteger(authPlayer.isLoggedIn())
					
					+ " WHERE username = '"+authPlayer.getUsername()+"';");
			break;
		}
			
		case DELETE:
			MySQL.update("DELETE FROM authme WHERE username = '"+authPlayer.getUsername()+"';");
			break;
			
		default:
			break;
		}
	}

}
