package de.mreturkey.authyou.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import de.mreturkey.authyou.AuthYou;

public final class Config extends YamlConfiguration {

	public static File configFile;
	public static YamlConfiguration instance;
	
	public static Database getDatabase;
	public static String getSQLColumnUsername, getSQLTableName, getSQLColumnLastLogin,
		getSQLColumnIp, getSQLColumnPassword, getSQLColumnLastLocX, getSQLColumnLastLocY, getSQLColumnLastLocZ,
		getSQLColumnLastLocWorld, getSQLColumnId, getSQLColumnLogged;
	public static boolean getSessionsEnabled, getSessionExpireOnIpChange;
	public static Date getSessionTimeOut;
	public static boolean allowChat, kickNonRegistered, kickOnWrongPassword, kickViaBungeeCord, allowMovement;
	public static List<String> allowCommands;
	public static int maxRegPerIp, maxNicknameLength, minNicknameLength, timeout, minPasswordLength;
	public static Pattern allowedNicknameCharacters;
	
	
	public Config() {
//		super();
		
		File file = new File(AuthYou.getInstance().getDataFolder(), "config.yml");
		
        try {
            this.load(file);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file , ex);
        } finally {
        	configFile = file;
        	instance = this;
        	loadConfig();
        }
	}
	
	public static void loadConfig() {
		getDatabase = new Database(
				instance.getString("DataSource.mySQLHost", "localhost"),
				instance.getInt("DataSource.mySQLPort", 3306),
				instance.getString("DataSource.mySQLDatabase", "authme"),
				instance.getString("DataSource.mySQLUsername", "username"),
				instance.getString("DataSource.mySQLPassword", "pass1234"));
        getSQLTableName = instance.getString("DataSource.mySQLTablename", "authme");
        getSQLColumnUsername = instance.getString("DataSource.mySQLColumnName", "username");
        getSQLColumnPassword = instance.getString("DataSource.mySQLColumnPassword", "password");
        getSQLColumnIp = instance.getString("DataSource.mySQLColumnIp", "ip");
        getSQLColumnLastLogin = instance.getString("DataSource.mySQLColumnLastLogin", "lastlogin");
        getSQLColumnLastLocX = instance.getString("DataSource.mySQLlastlocX", "x");
        getSQLColumnLastLocY = instance.getString("DataSource.mySQLlastlocY", "y");
        getSQLColumnLastLocZ = instance.getString("DataSource.mySQLlastlocZ", "z");
        getSQLColumnLastLocWorld = instance.getString("DataSource.mySQLlastlocWorld", "world");
        
        getSessionsEnabled= instance.getBoolean("settings.sessions.enabled", true);
        final int sessionTimeout = instance.getInt("settings.sessions.timeout", 3);
        final TimeUnit sessionTimeUnit = convertTimeUnit(instance.getString("settings.sessions.TimeUnit", "DAYS"), TimeUnit.DAYS);
        getSessionTimeOut = new Date(sessionTimeUnit.toMillis(sessionTimeout));
        getSessionExpireOnIpChange = instance.getBoolean("settings.sessions.sessionExpireOnIpChange", true);
        
        allowChat = instance.getBoolean("settings.restrictions.allowChat", false);
        allowCommands = instance.getStringList("settings.restrictions.allowCommands");
        
        maxRegPerIp = instance.getInt("settings.restrictions.maxRegPerIp", 1);
        maxNicknameLength = instance.getInt("settings.restrictions.maxNicknameLength", 20);
        kickNonRegistered = instance.getBoolean("settings.restrictions.kickNonRegistered", false);
        kickOnWrongPassword = instance.getBoolean("settings.restrictions.kickOnWrongPassword");
        kickViaBungeeCord = instance.getBoolean("settings.restrictions.kickViaBungeeCord", false);
        minNicknameLength = instance.getInt("settings.restrictions.minNicknameLength", 3);
        allowMovement = instance.getBoolean("settings.restrictions.allowMovement", false);
        timeout = instance.getInt("settings.restrictions.timeout", 30);
        allowedNicknameCharacters = Pattern.compile(instance.getString("settings.restrictions.allowedNicknameCharacters"));
        minPasswordLength = instance.getInt("settings.security.minPasswordLength");
        
        if(!configFile.exists())
			try {
				instance.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	private static TimeUnit convertTimeUnit(String val, TimeUnit defaultUnit) {
		switch (val.toUpperCase()) {
		case "DAYS":
			return TimeUnit.DAYS;
		case "HOURS":
			return TimeUnit.HOURS;
		case "MICROSECONDS":
			return TimeUnit.MICROSECONDS;
		case "MILLISECONDS":
			return TimeUnit.MILLISECONDS;
		case "MINUTES":
			return TimeUnit.MINUTES;
		case "NANOSECONDS":
			return TimeUnit.NANOSECONDS;
		case "SECONDS":
			return TimeUnit.SECONDS;
		default:
			throw new RuntimeException("");
		}
	}
	
	
}
