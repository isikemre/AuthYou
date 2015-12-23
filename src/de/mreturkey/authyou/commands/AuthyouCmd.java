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

public class AuthyouCmd implements TabExecutor {

	private static final List<String> AUTHYOU_CMDS = new ArrayList<>(Collections.unmodifiableList(Arrays.asList("changepassword")));
	
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
				case "changepassword":
					if(sender.hasPermission("authyou.admin.changepassword")) {
						if(args.length != 3) {
							sender.sendMessage("§cUsage: /authyou changepassword <username> <password>");
							return true;
						}
					}
					break;

				default:
					break;
				}
			}
			
		}
		return false;
	}

	
}
