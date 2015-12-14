package de.mreturkey.authyou.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.bukkit.Bukkit;

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
	    	checkAuthTableIsValid();
	    	createTables();
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
	    	} else LogUtil.consoleSenderLog("§e[MySQL]§r §4Error: "+e.getMessage());
	    	try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} finally {
				if(Config.stopServerOnSQLError) Bukkit.shutdown();
			}
	    }
		return con;
	}
	
	public static void checkAuthTableIsValid() throws SQLException, InterruptedException, SQLTableValidException {
		ResultSet rs = MySQL.query("SHOW COLUMNS FROM `"+Config.getSQLTableName+"`");
		
		if(!rs.next()) throw new SQLTableValidException("MYSQL TABLE \""+Config.getSQLTableName+"\" HAS NOT COLUMNS!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnId)) throw new SQLTableValidException("The ID Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnId+")");
		if(!rs.getString(2).substring(0, 3).equalsIgnoreCase("int")) throw new SQLTableValidException("The ID Column is not a Integer type");
		if(!rs.getString(6).equalsIgnoreCase("auto_increment")) throw new SQLTableValidException("The ID Column is not auto increment");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Username Column!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnUsername)) throw new SQLTableValidException("The Username Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnUsername+")");
		if(!rs.getString(2).equalsIgnoreCase("varchar(36)")) throw new SQLTableValidException("The UUID Column Type is not VARCHAR(36)");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Password Column!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnPassword)) throw new SQLTableValidException("The Password Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnPassword+")");
		if(!rs.getString(2).equalsIgnoreCase("varchar(255)")) throw new SQLTableValidException("The Password Column Type is not VARCHAR(255)");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no IP Column!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnIp)) throw new SQLTableValidException("The IP Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnIp+")");
		if(!rs.getString(2).equalsIgnoreCase("varchar(40)")) throw new SQLTableValidException("The IP Column Type is not VARCHAR(40)");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no LastLogin Column!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLogin)) throw new SQLTableValidException("The LastLogin Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLogin+")");
		if(!rs.getString(2).equalsIgnoreCase("bigint(20)")) throw new SQLTableValidException("The LastLogin Column Type is not BIGINT(20)");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no X Column!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLocX)) throw new SQLTableValidException("The X Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLocX+")");
		if(!rs.getString(2).equalsIgnoreCase("double")) throw new SQLTableValidException("The X Column Type is not DOUBLE");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Y Column!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLocY)) throw new SQLTableValidException("The Y Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLocY+")");
		if(!rs.getString(2).equalsIgnoreCase("double")) throw new SQLTableValidException("The X Column Type is not DOUBLE");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Z Column!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLocZ)) throw new SQLTableValidException("The Z Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLocZ+")");
		if(!rs.getString(2).equalsIgnoreCase("double")) throw new SQLTableValidException("The X Column Type is not DOUBLE");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no World Column!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLastLocWorld)) throw new SQLTableValidException("The World Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLastLocWorld+")");
		if(!rs.getString(2).equalsIgnoreCase("varchar(255)")) throw new SQLTableValidException("The World Column Type is not VARCHAR(255)");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no Email Column!");
		
		if(!rs.next()) throw new SQLTableValidException("MySQL Table \""+Config.getSQLTableName+"\" has no isLogged Column!");
		
		if(!rs.getString(1).equalsIgnoreCase(Config.getSQLColumnLogged)) throw new SQLTableValidException("The isLogged Column-Name not equals with the name in the config.yml ("+rs.getString(1)+" != "+Config.getSQLColumnLogged+")");
		if(!rs.getString(2).equalsIgnoreCase("smallint(6)")) throw new SQLTableValidException("The isLogged Column Type is not VARCHAR(255)");
	}
	
	public static void createTables() {
		MySQL.update("CREATE TABLE IF NOT EXISTS `session` ( `id` VARCHAR(14) NULL DEFAULT NULL COMMENT 'Session ID' , `uuid` VARCHAR(36) NULL DEFAULT NULL COMMENT 'UUID of player' , `ip` VARCHAR(15) NULL DEFAULT NULL COMMENT 'IP of player' , `last_login` TIMESTAMP NULL DEFAULT NULL COMMENT 'Timestamp of the last login' , `state` VARCHAR(50) NULL DEFAULT NULL COMMENT 'State of Session' , `destroy_reason` VARCHAR(50) NULL DEFAULT NULL COMMENT 'The Reason why this Session is destroyed, or not.' , PRIMARY KEY (`id`)) ENGINE = MyISAM;");
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
		System.out.println("DEBUG: "+qry);
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(qry);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet query(String qry) {
		ResultSet rs = null;
		System.out.println("DEBUG: "+qry);
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
		System.out.println("DEBUG: "+sql);
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
	
	public static void insertSession(final Session session) {
		AuthYou.getAuthManager().runAsync(new Runnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = con.prepareStatement("INSERT INTO session "
							+ "(id, uuid, ip, last_login, state, destroyed, destroy_reason) VALUES "
							+ "(?,?,?,?,?,?,?)");
					
					ps.setString(1, session.getId());
					ps.setString(2, session.getUniqueId().toString());
					ps.setString(3, session.getIp().getHostAddress());
					ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
					ps.setString(5, session.getState().toString());
					ps.setBoolean(6, session.isDestroyed());
					ps.setString(7, session.getDestroyReason().toString());
					
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void updateSession(final Session session) {
		AuthYou.getAuthManager().runAsync(new Runnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = con.prepareStatement("UPDATE session SET uuid = ?, ip = ?, last_login = ?, state = ?, destroyed = ?, destroy_reason = ? "
							+ "WHERE id = '"+session.getId()+"'");
					
					ps.setString(1, session.getUniqueId().toString());
					ps.setString(2, session.getIp().getHostAddress());
					ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
					ps.setString(4, session.getState().toString());
					ps.setBoolean(5, session.isDestroyed());
					ps.setString(6, session.getDestroyReason().toString());
					
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void insertOrUpdateSession(final Session session) {
		AuthYou.getAuthManager().runAsync(new Runnable() {
			@Override
			public void run() {
				try {
					PreparedStatement ps = con.prepareStatement("INSERT INTO session "
							+ "(id, uuid, ip, last_login, state, destroyed, destroy_reason) VALUES "
							+ "(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE "
							+ "uuid = ?, ip = ?, last_login = ?, state = ?, destroyed = ?, destroy_reason = ?");
					
					final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					final String uuid = session.getUniqueId().toString();
					final String ip = session.getIp().getHostAddress();
					final String state = session.getState().toString();
					final String destroyReason = session.getDestroyReason().toString();
					
					ps.setString(1, session.getId());
					ps.setString(2, uuid);
					ps.setString(3, ip);
					ps.setTimestamp(4, timestamp);
					ps.setString(5, state);
					ps.setBoolean(6, session.isDestroyed());
					ps.setString(7, destroyReason);
					
					ps.setString(8, uuid);
					ps.setString(9, ip);
					ps.setTimestamp(10, timestamp);
					ps.setString(11, state);
					ps.setBoolean(12, session.isDestroyed());
					ps.setString(13, destroyReason);
					
					ps.executeUpdate();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void deleteSession(final Session session) {
		AuthYou.getAuthManager().runAsync(new Runnable() {
			@Override
			public void run() {
				MySQL.update("DELETE FROM session WHERE id = '"+session.getId()+"'");
			}
		});
	}
}
