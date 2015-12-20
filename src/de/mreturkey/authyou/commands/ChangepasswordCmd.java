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
import de.mreturkey.authyou.util.LogUtil;

public class ChangepasswordCmd implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("changepassword")) {
			if(!(sender instanceof Player)) { LogUtil.consoleSenderLog("§4You can't execute this command as console."); return true; }
			
			final Player p = (Player) sender;
			if(args.length != 2) {
				Message.USAGE_CHANGEPASSWORD.msg(p);
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
			
			if(!session.isPlayerLoggedIn()) {
				Message.NOT_LOGGED_IN.msg(p);
				return true;
			}
			
			if(!args[0].equals(args[1])) {
				Message.PASSWORD_ERROR.msg(p);
				return true;
			}
		
			if(args[0].equalsIgnoreCase(p.getName())) {
				Message.PASSWORD_ERROR_NICK.msg(p);
				return true;
			}
			
			if(args[0].length() < Config.minPasswordLength) {
				Message.PASS_LEN.msg(p);
				return true;
			}
			
			final String argPass = args[0].toLowerCase();
			if(argPass.contains("select") || argPass.contains("delete") || argPass.contains("drop") || argPass.contains("insert") || argPass.contains("update")) {
				Message.PASSWORD_ERROR_UNSAFE.msg(p);
				return true;
			}
			
			if(!Config.allowedPasswordCharacters.matcher(args[0]).matches()) {
				Message.PASSWORD_ERROR_UNSAFE.msg(p);
				return true;
			}
			
			if(!session.getAuthPlayer().getPassword().changePassword(args[0])) throw new IllegalArgumentException("Error while changing password");
			return true;
		}
		return false;
	}

}
