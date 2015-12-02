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
	    	createTables();
	    	return con;
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	LogUtil.consoleSenderLog("§e[MySQL]§r §4Failed to connect ("+database.getUser()+"@"+database.getHost()+":"+database.getPort()+")");
	    }
		return con;
	}

	public static void createTables(){
		update("CREATE TABLE IF NOT EXISTS mz_players (uuid VARCHAR(36) PRIMARY KEY NOT NULL, name VARCHAR(255) UNIQUE KEY, request_allowed TINYINT(1), game_invite_allowed TINYINT(1), mails_allowed TINYINT(1), offline_mails_allowed TINYINT(1))");
		update("CREATE TABLE IF NOT EXISTS mz_friends (player_uuid VARCHAR(36) NOT NULL, friend_uuid VARCHAR(36), player_name VARCHAR(255), friend_name VARCHAR(255), request_accepted TINYINT(1), blocked TINYINT(1))");
		update("CREATE TABLE IF NOT EXISTS mz_mails (uuid VARCHAR(36) NOT NULL, friend_uuid VARCHAR(36), mail VARCHAR(120), unread TINYINT(1))");
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
			LogUtil.consoleSenderLog("DEBUG: "+qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet query(String qry) {
		ResultSet rs = null;

		try {
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(qry);
			LogUtil.consoleSenderLog("DEBUG: "+qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;

	}
	
}
