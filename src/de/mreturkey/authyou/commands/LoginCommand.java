package de.mreturkey.authyou.commands;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.message.Messages;
import de.mreturkey.authyou.util.HashUtils;

public class LoginCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			if(label.equalsIgnoreCase("login") || label.equalsIgnoreCase("l")) {
				if(args.length != 1) {Messages.LOGIN_MSG.msg((Player) sender);return true;}
				Player p = (Player) sender;
				AuthPlayer authPlayer = AuthYou.getAuthManager().getAuthPlayer(p);
				
				if(authPlayer == null) {
					return true;
				}
				
				if(authPlayer.isLoggedIn()) {
					Messages.LOGGED_IN.msg(p);
					return true;
				}
				
				try {
					if(HashUtils.comparePassword(authPlayer.getPasswordHash(), args[0], p.getName())) {
						AuthYou.getAuthPlayer(p).setLoggedIn(true);
						Messages.LOGIN.msg(p);
					} else {
						Messages.WRONG_PWD.msg(p);
					}
					return true;
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
		} else sender.sendMessage("You can only login as player");
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
