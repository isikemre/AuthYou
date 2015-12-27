package de.mreturkey.authyou.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.security.session.Session;

public class LogUtil {
		
	public static ConsoleCommandSender getCommandSender() {
		return Bukkit.getConsoleSender();
	}
	
	public static void consoleSenderLog(String log){
		Bukkit.getConsoleSender().sendMessage(log);
	}
	
	public static void sessionLogToFile(Session session, String opendOrDestroy) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String log = "LOG";
					File file = new File(AuthYou.getInstance().getDataFolder(), "session.log");
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(log);
					bw.newLine();
					bw.flush();
					bw.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void waitForAnyKeyPress() {
		try { System.in.read(); } catch (Exception e){ }	
	}

}
