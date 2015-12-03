package de.mreturkey.authyou.commands;

import java.security.NoSuchAlgorithmException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.util.HashUtils;

public class LoginCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			if(label.equalsIgnoreCase("login") || label.equalsIgnoreCase("l")) {
				if(args.length != 1) return true;
				Player p = (Player) sender;
				AuthPlayer authPlayer = AuthYou.getAuthManager().getAuthPlayer(p);
				p.sendMessage("SessionID: "+authPlayer.getSession().getId());
				p.sendMessage("Password: "+authPlayer.getPassword().getPassword());
				p.sendMessage("LastLogin: "+authPlayer.getLastLogin());
				p.sendMessage("IP: "+authPlayer.getIP());
				
				try {
					boolean bool = HashUtils.comparePassword(authPlayer.getPasswordHash(), args[0], p.getName());
					p.sendMessage("Dein Password ist: "+bool);
					if(bool) {
						AuthYou.getAuthPlayer(p).setLoggedIn(true);
						p.sendMessage("§aYou logged in!");
					}
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
		} else sender.sendMessage("You can only login as player");
		return true;
	}

}
