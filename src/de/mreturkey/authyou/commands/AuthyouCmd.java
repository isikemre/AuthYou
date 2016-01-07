package de.mreturkey.authyou.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginDescriptionFile;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.Password;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.util.MySQL;

public class AuthyouCmd implements TabExecutor {

	private static final List<String> AUTHYOU_CMDS = new ArrayList<>(Collections.unmodifiableList(Arrays.asList("changepassword","reload")));
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(command.getName().equalsIgnoreCase("authyou") && sender.hasPermission("authyou.admin")) {
			final List<String> response = new ArrayList<>();
			
			for(String subcmd : AUTHYOU_CMDS) {
				if(subcmd.startsWith(args[0])) {
					response.add(subcmd);
				}
			}
			
			return response;
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("authyou") && sender.hasPermission("authyou.admin")) {
			if(args.length <= 0) {
				PluginDescriptionFile pdf = AuthYou.getInstance().getDescription();
				sender.sendMessage("AuthYou v" + pdf.getVersion() + " - developed by mReTurkey");
				return true;
			} else {
				switch (args[0]) {
				case "changepassword": {
					if(sender.hasPermission("authyou.admin.changepassword")) {
						if(args.length != 3) {
							sender.sendMessage("§cUsage: /authyou changepassword <username> <password>");
							return true;
						}
						
						if(args[2].equalsIgnoreCase(args[1])) {
							Message.PASSWORD_ERROR_NICK.msgSender(sender);
							return true;
						}
						
						if(args[2].length() < Config.minPasswordLength) {
							Message.PASS_LEN.msgSender(sender);
							return true;
						}
						
						if(args[2].equalsIgnoreCase("select") || args[2].equalsIgnoreCase("delete") || args[2].equalsIgnoreCase("drop") || args[2].equalsIgnoreCase("insert") || args[2].equalsIgnoreCase("update")) {
							Message.PASSWORD_ERROR_UNSAFE.msgSender(sender);
							return true;
						}
						
						if(!Config.allowedPasswordCharacters.matcher(args[2]).matches()) {
							Message.PASSWORD_ERROR_UNSAFE.msgSender(sender);
							return true;
						}
						
						String newPasswordHash = Password.getNewPasswordOnlyHash(args[2]);
						if(!MySQL.adminChangePassword(args[1], newPasswordHash)) {
							sender.sendMessage("Error while changing password");
							return true;
						}
						
						Session s = AuthYou.getSession(args[1]);
						if(s != null && s.getAuthPlayer() != null) {
							s.getAuthPlayer().getPassword().setPasswordHash(newPasswordHash);
						}
						Message.PWD_CHANGED.msgSender(sender);
						return true;
					} else {
						sender.sendMessage("§cYou don't have permission to use this command");
						return false;
					}
				}
				
				case "reload": {
					if(sender.hasPermission("authyou.admin.reload")) {
						AuthYou.getConf().reload();
						Message.RELOAD.msgSender(sender);
					} else {
						sender.sendMessage("§cYou don't have permission to use this command");
						return false;
					}
				}
				
				default:
					break;
				}
			}
			
		}
		return false;
	}

	
}
