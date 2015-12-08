package de.mreturkey.authyou.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

	public static Connection con;

	public static Connection openConnection() {
		DataBase database = DataBase.DATABASE;
	    try {
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §6Conntecting... ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+")");
	    	Connection con = DriverManager.getConnection("jdbc:mysql://" + database.getHost() + ":" + database.getPort() + "/" + database.getDatabase() + "?user=" + database.getUser() + "&password=" + database.getPassword() + "&autoReconnect=true");
	    	MySQL.con = con;
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §2Connected! ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+")");
	    	return con;
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §4Failed to connect ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+")");
	    } finally {
	    	createTables();
	    }
		return con;
	}
	
	public static void createTables() {
		MySQL.update("CREATE TABLE `sessions` ( `id` VARCHAR(14) NULL DEFAULT NULL COMMENT 'Session ID' , `uuid` VARCHAR(36) NULL DEFAULT NULL COMMENT 'UUID of player' , `ip` VARCHAR(15) NULL DEFAULT NULL COMMENT 'IP of player' , `last_login` TIMESTAMP NULL DEFAULT NULL COMMENT 'Timestamp of the last login' , `state` VARCHAR(50) NULL DEFAULT NULL COMMENT 'State of Session' , `destroy_reason` VARCHAR(50) NULL DEFAULT NULL COMMENT 'The Reason why this Session is destroyed, or not.' , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
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
	
	public static int convertBooleanToInteger(boolean bool) {
		return bool ? 1 : 0;
	}
	
}
