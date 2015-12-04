package de.mreturkey.authyou.message;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthYou;

public enum Messages {

	NOT_LOGGED_IN,
	USAGE_LOG,
	WRONG_PWD,
	UNREGISTERED,
	REG_DISABLED,
	VALID_SESSION,
	LOGIN,
	USER_REGGED,
	USAGE_REG,
	MAX_REG,
	NO_PERM,
	LOGIN_MSG,
	REG_MSG,
	PWD_CHANGED,
	USER_UNKNOWN,
	PASSWORD_ERROR,
	PASSWORD_ERROR_NICK,
	LOGGED_IN,
	LOGOUT,
	REGISTERED,
	TIMEOUT,
	USAGE_CHANGEPASSWORD,
	INVALID_SESSION;
	
	private static File messageFile;
	private static YamlConfiguration yaml;
	private static boolean loaded = false;
	
	public static void loadMessages() {
		messageFile = new File(AuthYou.getInstance().getDataFolder(), "messages.yml");
		yaml = YamlConfiguration.loadConfiguration(messageFile);
		if(!messageFile.exists()) {
			yaml.set("not_logged_in", "&cYou''re not logged in!");
			yaml.set("usage_log", "&cUsage: /login password");
			yaml.set("wrong_pwd", "&cWrong password");
			yaml.set("unregistered", "&cSuccessfully unregistered!");
			yaml.set("reg_disabled", "&cRegistration is disabled");
			yaml.set("valid_session", "&cSession login");
			yaml.set("login", "&cSuccessful login!");
			yaml.set("user_regged", "&cYou have already registered this username");
			yaml.set("usage_reg", "&cUsage: /register password ConfirmPassword");
			yaml.set("max_reg", "&fYou have exceeded the max number of registrations for your account");
			yaml.set("no_perm", "&cYou don''t have the permission to execute this command");
			yaml.set("login_msg", "&cPlease login with \"/login password\"");
			yaml.set("reg_msg", "&cPlease register with \"/register password ConfirmPassword\"");
			yaml.set("pwd_changed", "&cPassword changed!");
			yaml.set("user_unknown", "&cUsername not registered");
			yaml.set("password_error", "&fPassword doesn''t match");
			yaml.set("password_error_nick", "&fYou can''t use your name as password, please choose another one");
			yaml.set("logged_in", "&cYou''re already logged in!");
			yaml.set("logout", "&cSuccessfully logged out");
			yaml.set("registered", "&cSuccessfully registered!");
			yaml.set("timeout", "&fLogin timeout, please try again");
			yaml.set("usage_changepassword", "&fUsage: /changepassword oldPassword newPassword");
			yaml.set("invalid_session", "&fSession datas doesn''t match. Please wait until the end of the current session");
			
			try {
				yaml.save(messageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		loaded = true;
	}
	
	private final String msg;
	
	private Messages() {
		if(!isLoaded()) loadMessages();
		this.msg = getYaml().getString(this.toString().toLowerCase());
	}
	
	private static boolean isLoaded() {
		return loaded;
	}
	
	private static YamlConfiguration getYaml() {
		return yaml;
	}
	
	private static String replaceCode(String msg){
		return msg.replace("&", "§");
	}
	
	public String getMessage(boolean altColorCode) {
		return altColorCode ? replaceCode(msg) : msg;
	}
	
	public void msg(Player p){
		p.sendMessage(replaceCode(msg));
	}
}
