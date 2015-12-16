package de.mreturkey.authyou.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.util.KickReason;
import de.mreturkey.authyou.util.LogUtil;

public class LoginCmd implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("login") || label.equalsIgnoreCase("l")) {
			if(!(sender instanceof Player)) { LogUtil.consoleSenderLog("§4You can't execute this command as console."); return true; }
			
			final Player p = (Player) sender;
			if(args.length != 1) {
				Message.LOGIN_MSG.msg(p);
				return true;
			}
			
			final Session session = AuthYou.getSession(p);
			if(session == null) {
				Message.INVALID_SESSION.msg(p);
				return true;
			}
			
			if(!session.isPlayerRegisterd()) {
				Message.USER_UNKNOWN.msg(p);
				return true;
			}
			
			if(session.getAuthPlayer().getPassword().compare(args[0])) {
				session.login(p);
				Message.LOGIN.msg(p);
			} else {
				if(Config.kickOnWrongPassword) AuthYou.getAuthManager().kickPlayer(session, KickReason.WRONG_PASSWORD);
				else Message.WRONG_PWD.msg(p);
			}
			return true;
		}
		return false;
	}

}
