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

	public static Connection openConnection() {
		Database database = Config.getDatabase;
	    try {
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §6Conntecting... ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+")");
	    	Connection con = DriverManager.getConnection("jdbc:mysql://" + database.getHost() + ":" + database.getPort() + "/" + database.getDatabase() + "?user=" + database.getUser() + "&password=" + database.getPassword() + "&autoReconnect=true");
	    	MySQL.con = con;
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §2Connected! ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+")");
	    	checkUUIDSupport();
	    	createTables();
	    	return con;
	    } catch(Exception e) {
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §4Failed to connect ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+")");
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §4Error: "+e.getMessage());
	    }
		return con;
	}
	
	public static void checkUUIDSupport() throws SQLException, InterruptedException {
		ResultSet rs = MySQL.query("SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_NAME = '"+Config.getSQLTableName+"' AND COLUMN_NAME = '"+Config.getSQLColumnUsername+"'");
		if(rs.first()) {
			LogUtil.consoleSenderLog("ß?ß?ß?------ = "+rs.toString());
		} else {
			LogUtil.consoleSenderLog("**** MYSQL TABLE HAS NO UUID COLUMN. PLEASE UPDATE MYSQL TABLE \""+Config.getSQLTableName+"\" ****");
			LogUtil.consoleSenderLog("**** SERVER WILL SHUTDOWN NOW ****");
			Thread.sleep(5000);
			Bukkit.shutdown();
		}
	}
	
	public static void createTables() {
		MySQL.update("CREATE TABLE IF NOT EXISTS `session` ( `id` VARCHAR(14) NULL DEFAULT NULL COMMENT 'Session ID' , `uuid` VARCHAR(36) NULL DEFAULT NULL COMMENT 'UUID of player' , `ip` VARCHAR(15) NULL DEFAULT NULL COMMENT 'IP of player' , `last_login` TIMESTAMP NULL DEFAULT NULL COMMENT 'Timestamp of the last login' , `state` VARCHAR(50) NULL DEFAULT NULL COMMENT 'State of Session' , `destroy_reason` VARCHAR(50) NULL DEFAULT NULL COMMENT 'The Reason why this Session is destroyed, or not.' , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
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
