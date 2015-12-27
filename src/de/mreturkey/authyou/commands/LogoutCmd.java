package de.mreturkey.authyou.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.util.KickReason;
import de.mreturkey.authyou.util.LogUtil;

public class LogoutCmd implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("logout")) {
			if(!(sender instanceof Player)) { LogUtil.consoleSenderLog("§4You can't execute this command as console."); return true; }
			
			final Player p = (Player) sender;
			
			final Session session = AuthYou.getSession(p);
			if(session == null) {
				Message.INVALID_SESSION.msg(p);
				return true;
			}
			
			if(!session.isPlayerRegisterd()) {
				Message.USER_UNKNOWN.msg(p);
				return true;
			}
			
			if(!session.isPlayerLoggedIn()) {
				Message.NOT_LOGGED_IN.msg(p);
				return true;
			}
			
			try {
				session.logout(p);
				AuthYou.getAuthManager().kickPlayer(session, KickReason.LOGOUT);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		return false;
	}

}
