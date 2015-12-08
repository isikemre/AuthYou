package de.mreturkey.authyou.config;

public class Database {
	
	private String host;
	private int port;
	private String database;
	private String user;
	private String password;
	private boolean autoConnect;
	
	/**
	 * Creates a new database instance.<br>
	 * As default <code>autoConnect</code> is true
	 * @param host
	 * @param port
	 * @param database
	 * @param user
	 * @param password
	 */
	
	public Database(String host, int port, String database, String user, String password){
		this(host, port, database, user, password, true);
	}
	
	/**
	 * Creates a new database instance.
	 * @param host
	 * @param port
	 * @param database
	 * @param user
	 * @param password
	 * @param autoConntect
	 */
	
	public Database(String host, int port, String database, String user, String password, boolean autoConntect) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.autoConnect = autoConntect;
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

	public boolean isAutoConnect() {
		return autoConnect;
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

	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
	}
}
