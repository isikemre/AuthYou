package de.mreturkey.authyou.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.message.Messages;
import de.mreturkey.authyou.util.KickReason;

public class LogoutCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			if(label.equalsIgnoreCase("logout")) {
				Player p = (Player) sender;
				AuthPlayer authPlayer = AuthYou.getAuthPlayer(p);
				if(authPlayer.isLoggedIn()) {
					authPlayer.logout();
					AuthYou.getAuthManager().kickPlayer(p, authPlayer, KickReason.LOGOUT);
				} else {
					Messages.NOT_LOGGED_IN.msg(p);
				}
			}
		}
		return true;
	}

}
