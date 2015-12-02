package de.mreturkey.authyou.util;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class LogUtil {
	
	public static ConsoleCommandSender getCommandSender() {
		return Bukkit.getConsoleSender();
	}
	
	public static void consoleSenderLog(String log){
		Bukkit.getConsoleSender().sendMessage(log);
	}

}
