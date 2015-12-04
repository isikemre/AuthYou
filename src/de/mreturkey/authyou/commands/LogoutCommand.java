package de.mreturkey.authyou.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.message.Messages;
import de.mreturkey.authyou.util.WaitForLoginThread;

public class LogoutCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			if(label.equalsIgnoreCase("logout")) {
				Player p = (Player) sender;
				AuthYou.getAuthPlayer(p).logout();
				Messages.LOGOUT.msg(p);
				AuthYou.getAuthManager().authenticatePlayer(p); //TODO PLEASEEEEEEEEEEE
				new WaitForLoginThread(p, 5);
				return true;
			}
		}
		return false;
	}

}
