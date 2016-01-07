package de.mreturkey.authyou.commands;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import de.mreturkey.authyou.AuthPlayer;
import de.mreturkey.authyou.AuthYou;
import de.mreturkey.authyou.config.Config;
import de.mreturkey.authyou.config.Message;
import de.mreturkey.authyou.security.session.Session;
import de.mreturkey.authyou.util.HashUtils;
import de.mreturkey.authyou.util.LogUtil;

public class RegisterCmd implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("register") || label.equalsIgnoreCase("reg")) {
			if(!(sender instanceof Player)) { LogUtil.consoleSenderLog("§4You can't execute this command as console."); return true; }
			
			final Player p = (Player) sender;
			
			if(!Config.getRegisterEnabled) {
				Message.REG_DISABLED.msg(p);
				return true;
			}
			
			if(args.length != 2) {
				Message.USAGE_REG.msg(p);
				return true;
			}
			
			final Session session = AuthYou.getSession(p);
			if(session == null) {
				Message.INVALID_SESSION.msg(p);
				return true;
			}
			
			if(session.isPlayerRegisterd()) {
				Message.USER_REGGED.msg(p);
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
			
			if(args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("drop") || args[0].equalsIgnoreCase("insert") || args[0].equalsIgnoreCase("update")) {
				Message.PASSWORD_ERROR_UNSAFE.msg(p);
				return true;
			}
			
			if(!Config.allowedPasswordCharacters.matcher(args[0]).matches()) {
				Message.PASSWORD_ERROR_UNSAFE.msg(p);
				return true;
			}
			
			if(Config.maxRegPerIp <= AuthYou.getAuthManager().getAmountOfRegIPs(session)) {
				Message.MAX_REG.msg(p);
				return true;
			}
			
			try {
				final String passwordHash = HashUtils.getHash(args[0], HashUtils.createSalt(16));
				AuthPlayer ap = AuthYou.getAuthManager().registerNewAuthPlayer(session, p, passwordHash);
				session.setAuthPlayer(ap);
				session.login(p);
				Message.LOGIN.msg(p);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				p.sendMessage("§4Error. Please contact the admin.");
			}
		}
		return false;
	}

}
