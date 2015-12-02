package de.mreturkey.authyou.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import de.mreturkey.authyou.AuthYou;

public class DataBase {
	
	public static final DataBase DATABASE = new DataBase("localhost", 3306, "mc", "mc", "pass1234", "database");

	private String host;
	private int port;
	private String database;
	private String user;
	private String password;

	private String filename;
	private File databaseFile;
	private YamlConfiguration databaseYML;

	public DataBase(String host, int port, String database, String user, String password, String filename) {
		if (!AuthYou.getInstance().getDataFolder().exists()) {
			AuthYou.getInstance().getDataFolder().mkdir();
		}

		this.filename = filename;
		databaseFile = new File(AuthYou.getInstance().getDataFolder(), filename + ".yml");
		try {
			if (!databaseFile.exists()) {
				//databaseFile.createNewFile(); //wegen Spigot: Er macht das alleine
				databaseYML = YamlConfiguration.loadConfiguration(databaseFile);
				
				databaseYML.set("database.host", host);
				databaseYML.set("database.port", port);
				databaseYML.set("database.database", database);
				databaseYML.set("database.user", user);
				databaseYML.set("database.password", password);

				this.host = host;
				this.port = port;
				this.database = database;
				this.user = user;
				this.password = password;
				
				databaseYML.save(databaseFile);
			} else {
				databaseYML = YamlConfiguration.loadConfiguration(databaseFile);
				this.host = databaseYML.getString("database.host");
				this.port = databaseYML.getInt("database.port");
				this.database = databaseYML.getString("database.database");
				this.user = databaseYML.getString("database.user");
				this.password = databaseYML.getString("database.password");
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to create configuration file", e);
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getDatabase() {
		return database;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public YamlConfiguration getDatabaseYML() {
		return databaseYML;
	}

	public File getDatabaseFile() {
		return databaseFile;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
