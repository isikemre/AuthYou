package de.mreturkey.authyou.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Database;
import de.mreturkey.authyou.security.session.Session;

public class MySQL {

	public static Connection con;
	public static Database database = Config.getDatabase;

	public static Connection openConnection() {
	    try {
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §6Conntecting... ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+"/"+database.getDatabase()+")");
	    	Connection con = DriverManager.getConnection("jdbc:mysql://" + database.getHost() + ":" + database.getPort() + "/" + database.getDatabase() + "?user=" + database.getUser() + "&password=" + database.getPassword() + "&autoReconnect=true");
	    	MySQL.con = con;
	    	createTables();
	    	checkAuthTableIsValid();
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §2Connected! ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+"/"+database.getDatabase()+")");
	    	return con;
	    } catch(Exception e) {
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §4Failed to connect ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+")");
	    	if(e instanceof SQLTableValidException) {
	    		LogUtil.consoleSenderLog("§r");
	    		LogUtil.consoleSenderLog("§r");
	    		LogUtil.consoleSenderLog("§4************* MYSQL TABLE NOT VALID *****************");
	    		LogUtil.consoleSenderLog("§r");
	    		LogUtil.consoleSenderLog("§e[MySQL]§r §4"+e.getMessage());
	    		LogUtil.consoleSenderLog("§r");
	    		LogUtil.consoleSenderLog("§4************* MYSQL TABLE NOT VALID *****************");
	    		LogUtil.consoleSenderLog("§r");
	    		LogUtil.consoleSenderLog("§r");
	    	} else LogUtil.consoleSenderLog("§e[MySQL]§r §4Error: "+e.getMessage() +"("+e.getClass().getName()+")");
	    	e.printStackTrace();
	    	try {
	    		LogUtil.consoleSenderLog("Press §cany key §rto continue with server shutdown... (sometimes you need to press twice)");
				LogUtil.waitForAnyKeyPress();
				LogUtil.consoleSenderLog("§cServer is shutting down...");
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				if(Config.stopServerOnSQLError) Bukkit.shutdown();
			}
	    }
		return con;
	}
	
	public static void checkAuthTableIsValid() throws SQLException, InterruptedException, SQLTableValidException {
		ResultSet rs = MySQL.query("SHOW COLUMNS FROM `"+Config.getSQLTableName+"`");
		
		HashMap<String, Integer> cols = new HashMap<>();
		while(rs.next()) {
			cols.put(rs.getString(1), rs.getRow());
		}
		
		if(cols.isEmpty()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" not exists!");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnId)  == null ? 100000 : cols.get(Config.getSQLColumnId))) throw new SQLTableValidException("MYSQL TABLE \""+Config.getSQLTableName+"\" HAS NOT COLUMNS!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnId)) throw new SQLTableValidException("The ID Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnId+")");
		if(!rs.getString(2).substring(0, 3).equalsIgnoreCase("int")) throw new SQLTableValidException("The ID Column is not a Integer type");
		if(!rs.getString(6).equalsIgnoreCase("auto_increment")) throw new SQLTableValidException("The ID Column is not auto increment");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnUsername)  == null ? 100000 : cols.get(Config.getSQLColumnUsername))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Username Column!\nPlease create a new column \""+Config.getSQLColumnUsername+"\" with VARCHAR(255) or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnUsername)) throw new SQLTableValidException("The Username Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnUsername+")");
		if(!rs.getString(2).equalsIgnoreCase("varchar(255)")) throw new SQLTableValidException("The Username Column Type is not VARCHAR(255)");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnUUID)  == null ? 100000 : cols.get(Config.getSQLColumnUUID))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Username Column!\nPlease create a new column \""+Config.getSQLColumnUUID+"\" with VARCHAR(36) or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnUUID)) throw new SQLTableValidException("The UUID Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnUUID+")");
		if(!rs.getString(2).equalsIgnoreCase("varchar(36)")) throw new SQLTableValidException("The UUID Column Type is not VARCHAR(36)");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnPassword)  == null ? 100000 : cols.get(Config.getSQLColumnPassword))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Password Column!\nPlease create a new column \""+Config.getSQLColumnPassword+"\" with VARCHAR(255) or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnPassword)) throw new SQLTableValidException("The Password Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnPassword+")");
		if(!rs.getString(2).equalsIgnoreCase("varchar(255)")) throw new SQLTableValidException("The Password Column Type is not VARCHAR(255)");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnIp)  == null ? 100000 : cols.get(Config.getSQLColumnIp))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no IP Column!\nPlease create a new column \""+Config.getSQLColumnIp+"\" with VARCHAR(40) or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnIp)) throw new SQLTableValidException("The IP Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnIp+")");
		if(!rs.getString(2).equalsIgnoreCase("varchar(40)")) throw new SQLTableValidException("The IP Column Type is not VARCHAR(40)");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnLastLogin) == null ? 100000 : cols.get(Config.getSQLColumnLastLogin))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no LastLogin Column!\nPlease create a new column \""+Config.getSQLColumnLastLogin+"\" with BIGINT(20) or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLogin)) throw new SQLTableValidException("The LastLogin Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLogin+")");
		if(!rs.getString(2).equalsIgnoreCase("bigint(20)")) throw new SQLTableValidException("The LastLogin Column Type is not BIGINT(20)");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnLastLocX)  == null ? 100000 : cols.get(Config.getSQLColumnLastLocX))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no X Column!\nPlease create a new column \""+Config.getSQLColumnLastLocX+"\" with DOUBLE or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLocX)) throw new SQLTableValidException("The X Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLocX+")");
		if(!rs.getString(2).equalsIgnoreCase("double")) throw new SQLTableValidException("The X Column Type is not DOUBLE");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnLastLocY)  == null ? 100000 : cols.get(Config.getSQLColumnLastLocY))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Y Column!\nPlease create a new column \""+Config.getSQLColumnLastLocY+"\" with DOUBLE or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLocY)) throw new SQLTableValidException("The Y Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLocY+")");
		if(!rs.getString(2).equalsIgnoreCase("double")) throw new SQLTableValidException("The X Column Type is not DOUBLE");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnLastLocZ) == null ? 100000 : cols.get(Config.getSQLColumnLastLocZ))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Z Column!\nPlease create a new column \""+Config.getSQLColumnLastLocY+"\" with DOUBLE or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLocZ)) throw new SQLTableValidException("The Z Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLocZ+")");
		if(!rs.getString(2).equalsIgnoreCase("double")) throw new SQLTableValidException("The X Column Type is not DOUBLE");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnLastLocWorld)  == null ? 100000 : cols.get(Config.getSQLColumnLastLocWorld))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no World Column!\nPlease create a new column \""+Config.getSQLColumnLastLocWorld+"\" with VARCHAR(255) or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLocWorld)) throw new SQLTableValidException("The World Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLocWorld+")");
		if(!rs.getString(2).equalsIgnoreCase("varchar(255)")) throw new SQLTableValidException("The World Column Type is not VARCHAR(255)");
		
		if(!rs.absolute(cols.get(Config.getSQLColumnLogged)  == null ? 100000 : cols.get(Config.getSQLColumnLogged))) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no isLogged Column!\nPlease create a new column \""+Config.getSQLColumnLogged+"\" with TINYINT(1) or BOOLEAN or change the the column-name in the config.yml");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLogged)) throw new SQLTableValidException("The isLogged Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLogged+")");
		if(!rs.getString(2).equalsIgnoreCase("tinyint(1)")) throw new SQLTableValidException("The isLogged Column Type is not TINYINT(1) or BOOLEAN");
	}
	
	public static void createTables() {
		MySQL.update("CREATE TABLE IF NOT EXISTS `session` ( `id` VARCHAR(14) NOT NULL COMMENT 'Session ID' , `username` VARCHAR(255) NOT NULL COMMENT 'Username of player', `uuid` VARCHAR(36) NOT NULL COMMENT 'UUID of player', `ip` VARCHAR(15) NULL DEFAULT NULL COMMENT 'IP of player' , `last_login` TIMESTAMP NULL DEFAULT NULL COMMENT 'Timestamp of the last login' , `state` VARCHAR(50) NULL DEFAULT NULL COMMENT 'State of Session' , `destroyed` BOOLEAN NOT NULL COMMENT 'Is Session destroyed?', `destroy_reason` VARCHAR(50) NULL DEFAULT NULL COMMENT 'The Reason why this Session is destroyed, or not.' , PRIMARY KEY (`id`), UNIQUE `uuid` (`uuid`), UNIQUE `username` (`username`)) ENGINE = MyISAM;");
		MySQL.update("CREATE TABLE IF NOT EXISTS `"+Config.getSQLTableName+"` ( `"+Config.getSQLColumnId+"` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'The Registration ID' , `"+Config.getSQLColumnUsername+"` VARCHAR(255) NOT NULL COMMENT 'Username of the player' , `"+Config.getSQLColumnUUID+"` VARCHAR(36) NOT NULL COMMENT 'UUID of the player' , `"+Config.getSQLColumnPassword+"` VARCHAR(255) NOT NULL COMMENT 'Password-Hash of this registration' , `"+Config.getSQLColumnIp+"` VARCHAR(40) NOT NULL COMMENT 'Last IP of the player' , `"+Config.getSQLColumnLastLogin+"` BIGINT(20) NULL DEFAULT NULL COMMENT 'The Last Login of the player' , `"+Config.getSQLColumnLastLocX+"` DOUBLE NOT NULL DEFAULT '0' COMMENT 'X Coord of player''s last location' , `"+Config.getSQLColumnLastLocY+"` DOUBLE NOT NULL DEFAULT '0' COMMENT 'Y Coord of player''s last location' , `"+Config.getSQLColumnLastLocZ+"` DOUBLE NOT NULL DEFAULT '0' COMMENT 'Z Coord of player''s last location' , `"+Config.getSQLColumnLastLocWorld+"` VARCHAR(255) NULL DEFAULT 'world' COMMENT 'World-Name of player''s last location' , `"+Config.getSQLColumnLogged+"` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Is player logged in?' , PRIMARY KEY (`"+Config.getSQLColumnId+"`), UNIQUE (`"+Config.getSQLColumnUUID+"`), UNIQUE (`"+Config.getSQLColumnUsername+"`)) ENGINE = MyISAM CHARACTER SET utf8 COLLATE utf8_general_ci;");
	}

	public static void close() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void update(String qry) {
		update(qry, false);
	}
	
	public static boolean update(String qry, boolean withFeedback) {
		boolean feedback = false;
		try {
			Statement stmt = con.createStatement();
			int rowsAffected = stmt.executeUpdate(qry);
			if(withFeedback && rowsAffected > 0) feedback = true;
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return feedback;
	}

	public static ResultSet query(String qry) {
		ResultSet rs = null;
		try {
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;

	}
	
	public static PreparedStatement prepareStmt(String sql) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ps;
	}
	
	public static int convertBooleanToInteger(boolean bool) {
		return bool ? 1 : 0;
	}
	
	public static void changePasswordAsync(final UUID uuid, final String newPasswordHash) {
		AuthYou.getAuthManager().runAsync(new Runnable() {
			
			@Override
			public void run() {
				try {
					Statement stmt = con.createStatement();
					stmt.executeUpdate("UPDATE "+Config.getSQLTableName+" SET "+Config.getSQLColumnPassword+" = '"+newPasswordHash+"' WHERE "+Config.getSQLColumnUUID+" = '"+uuid.toString()+"'");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static boolean adminChangePassword(String username, String newPasswordHash) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("UPDATE "+Config.getSQLTableName+" SET "+Config.getSQLColumnPassword+" = '"+newPasswordHash+"' WHERE "+Config.getSQLColumnUsername+" = '"+username+"'");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void insertSession(final Session session) {
		insertSession(session, true);
	}
	
	public static void insertSession(final Session session, boolean async) {
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = con.prepareStatement("INSERT INTO session "
							+ "(id, username, uuid, ip, last_login, state, destroyed, destroy_reason) VALUES "
							+ "(?,?,?,?,?,?,?)");
					
					ps.setString(1, session.getId());
					ps.setString(2, session.getUsername());
					ps.setString(3, session.getUniqueId().toString());
					ps.setString(4, session.getIp().getHostAddress());
					ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
					ps.setString(6, session.getState().toString());
					ps.setBoolean(7, session.isDestroyed());
					ps.setString(8, session.getDestroyReason().toString());
					
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		if(async) AuthYou.getAuthManager().runAsync(r);
		else r.run();
	}
	
	public static String insertSessionAndGetID(final Session session) {
		String id = session.getId();
		try {
			PreparedStatement ps = con.prepareStatement("INSERT INTO session "
					+ "(id, username, uuid, ip, last_login, state, destroyed, destroy_reason) VALUES "
					+ "(?,?,?,?,?,?,?)");
			
			ps.setString(1, id);
			ps.setString(2, session.getUsername());
			ps.setString(3, session.getUniqueId().toString());
			ps.setString(4, session.getIp().getHostAddress());
			ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			ps.setString(6, session.getState().toString());
			ps.setBoolean(7, session.isDestroyed());
			ps.setString(8, session.getDestroyReason().toString());
			
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			if(e.getErrorCode() == 1062) {
				AuthYou.getSessionManager().generateId();
			}
			e.printStackTrace();
		}
		return id;
	}
	
	/**
	 * Updates the given session asynchronously
	 * @param session
	 */
	public static void updateSession(final Session session) {
		updateSession(session, true);
	}
	
	public static void updateSession(final Session session, boolean async) {
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = con.prepareStatement("UPDATE session SET uuid = ?, ip = ?, last_login = ?, state = ?, destroyed = ?, destroy_reason = ? "
							+ "WHERE id = '"+session.getId()+"'");
					
					ps.setString(1, session.getUniqueId().toString());
					ps.setString(2, session.getIp().getHostAddress());
					ps.setTimestamp(3, new Timestamp(session.getLastLogin()));
					ps.setString(4, session.getState().toString());
					ps.setBoolean(5, session.isDestroyed());
					ps.setString(6, session.getDestroyReason().toString());
					
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		if(async) AuthYou.getAuthManager().runAsync(r);
		else r.run();
	}
	
	public static void insertOrUpdateSession(final Session session) {
		insertOrUpdateSession(session, true);
	}
	
	public static void insertOrUpdateSession(final Session session, boolean async) {
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = con.prepareStatement("INSERT INTO session "
							+ "(id, username, uuid, ip, last_login, state, destroyed, destroy_reason) VALUES "
							+ "(?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
							+ "username = ?, uuid = ?, ip = ?, last_login = ?, state = ?, destroyed = ?, destroy_reason = ?");
					
					final Timestamp timestamp = new Timestamp(session.getLastLogin());
					final String uuid = session.getUniqueId().toString();
					final String ip = session.getIp().getHostAddress();
					final String state = session.getState().toString();
					final String destroyReason = session.getDestroyReason().toString();
					
					ps.setString(1, session.getId());
					ps.setString(2, session.getUsername());
					ps.setString(3, uuid);
					ps.setString(4, ip);
					ps.setTimestamp(5, timestamp);
					ps.setString(6, state);
					ps.setBoolean(7, session.isDestroyed());
					ps.setString(8, destroyReason);
					
					ps.setString(9, session.getUsername());
					ps.setString(10, uuid);
					ps.setString(11, ip);
					ps.setTimestamp(12, timestamp);
					ps.setString(13, state);
					ps.setBoolean(14, session.isDestroyed());
					ps.setString(15, destroyReason);
					
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		if(async) AuthYou.getAuthManager().runAsync(r);
		else r.run();
	}
	
	public static void deleteSession(final Session session) {
		deleteSession(session, true);
	}
	
	public static void deleteSession(final Session session, boolean async) {
		final Runnable r = new Runnable() {

			@Override
			public void run() {
				MySQL.update("DELETE FROM session WHERE id = '"+session.getId()+"'", true);
			}
		};
		if(async) AuthYou.getAuthManager().runAsync(r);
		else r.run();
	}

	public static Object insertAuthPlayer(final Session session, final Player p, final String passwordHash, final boolean loggedIn) throws Exception {
		return insertAuthPlayer(session, p, passwordHash, loggedIn, true);
	}
	
	/**
	 * Returns a Future object which will contain the last inserted id.
	 * @param session
	 * @param player
	 * @return
	 * @throws Exception 
	 */
	public static Object insertAuthPlayer(final Session session, final Player p, final String passwordHash, final boolean loggedIn, boolean async) throws Exception {
		Callable<Object> c = new Callable<Object>() {
			
			@Override
			public Integer call() throws Exception {
				try {
					PreparedStatement ps = con.prepareStatement("INSERT INTO "+Config.getSQLTableName+" "
							
							+ "("
							+ Config.getSQLColumnUsername + ", "
							+ Config.getSQLColumnUUID + ", "
							+ Config.getSQLColumnPassword + ", "
							+ Config.getSQLColumnIp + ", "
							+ Config.getSQLColumnLastLogin + ", "
							+ Config.getSQLColumnLastLocX + ", "
							+ Config.getSQLColumnLastLocY + ", "
							+ Config.getSQLColumnLastLocZ + ", "
							+ Config.getSQLColumnLastLocWorld + ", "
							+ Config.getSQLColumnLogged +
							
							") VALUES "
							+ "(?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
					
					final Location loc = p.getLocation();
					
					ps.setString(1, p.getName());
					ps.setString(2, p.getUniqueId().toString());
					ps.setString(3, passwordHash);
					ps.setString(4, p.getAddress().getAddress().getHostAddress());
					ps.setLong(5, System.currentTimeMillis());
					ps.setDouble(6, loc.getX());
					ps.setDouble(7, loc.getY());
					ps.setDouble(8, loc.getZ());
					ps.setString(9, loc.getWorld().getName());
					ps.setBoolean(10, loggedIn);
					
					ps.executeUpdate();
					
					ResultSet keys = ps.getGeneratedKeys();
					keys.first();
					int lastId = keys.getInt(1);
					
					ps.close();
					return lastId;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return -1;
			}
		};
		if(async) return AuthYou.getAuthManager().submitAsync(c).get();
		else return c.call();
	}
	
	public static void updateAuthPlayer(final AuthPlayer authPlayer) {
		updateAuthPlayer(authPlayer, true);
	}
	
	public static void updateAuthPlayer(final AuthPlayer authPlayer, boolean async) {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					PreparedStatement ps = con.prepareStatement("UPDATE "+Config.getSQLTableName+" SET "
							+ Config.getSQLColumnUsername + " = ?, "
							+ Config.getSQLColumnUUID + " = ?, "
							+ Config.getSQLColumnPassword + " = ?,"
							+ Config.getSQLColumnIp + " = ?,"
							+ Config.getSQLColumnLastLogin + " = ?,"
							+ Config.getSQLColumnLastLocX + " = ?,"
							+ Config.getSQLColumnLastLocY + " = ?,"
							+ Config.getSQLColumnLastLocZ + " = ?,"
							+ Config.getSQLColumnLastLocWorld + " = ?,"
							+ Config.getSQLColumnLogged + " = ? WHERE "+Config.getSQLColumnId+" = "+authPlayer.getId()+"", Statement.RETURN_GENERATED_KEYS);
					
					final Player p = authPlayer.getPlayer();
					final Location loc = p.getLocation();
					
					ps.setString(1, authPlayer.getUsername());
					ps.setString(2, authPlayer.getUniqueId().toString());
					ps.setString(3, authPlayer.getPassword().getHash());
					ps.setString(4, p.getAddress().getAddress().getHostAddress());
					ps.setLong(5, System.currentTimeMillis());
					ps.setDouble(6, loc.getX());
					ps.setDouble(7, loc.getY());
					ps.setDouble(8, loc.getZ());
					ps.setString(9, loc.getWorld().getName());
					ps.setBoolean(10, authPlayer.isLoggedIn());
					
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		if(async) AuthYou.getAuthManager().runAsync(r);
		else r.run();
	}
}
