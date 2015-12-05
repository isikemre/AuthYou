package de.mreturkey.authyou.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.message.Messages;

public class RegisterCommand implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			if(label.equalsIgnoreCase("register")) {
				if(args.length != 2) {Messages.REG_MSG.msg((Player) sender);return false;}
				Player p = (Player) sender;
					
				if(AuthYou.getAuthManager().isRegistered(p)) {
					Messages.USER_REGGED.msg(p);
					return true;
				}
				if(!args[0].equals(args[1])) {
					Messages.WRONG_PWD.msg(p);
					return true;
				}
				
				if(!args[0].matches("[\\x21-\\x7E]*")) {
					Messages.PASSWORD_ERROR_UNSAFE.msg(p);
					return true;
				}
				
				if(args[0].length() < 5 || args[0].length() >= 30) { //min5:max30
					Messages.PASS_LEN.msg(p);
					return true;
				}
				
				if(args[0].equalsIgnoreCase(p.getName())) {
					Messages.PASSWORD_ERROR_NICK.msg(p);
					return true;
				}
				
				if(!AuthYou.getAuthManager().checkRegistrations(p.getAddress().getAddress(), 1)) {
					Messages.MAX_REG.msg(p);
					return true;
				}
				
				AuthYou.getAuthManager().registerPlayer(p, args[0]);
				Messages.REGISTERED.msg(p);
				return true;
			}
		}
		return false;
	}

}
