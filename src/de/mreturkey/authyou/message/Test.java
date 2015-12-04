package de.mreturkey.authyou.message;

public class Test {

	public static void main(String[] args){
		String test = "not_logged_in,\n"+
				"usage_log,\n"+
				"wrong_pwd,\n"+
				"unregistered,\n"+
				"reg_disabled,\n"+
				"valid_session,\n"+
				"login,\n"+
				"user_regged,\n"+
				"usage_reg,\n"+
				"max_reg,\n"+
				"no_perm,\n"+
				"login_msg,\n"+
				"reg_msg,\n"+
				"pwd_changed,\n"+
				"user_unknown,\n"+
				"password_error,\n"+
				"password_error_nick,\n"+
				"logged_in,\n"+
				"logout,\n"+
				"registered,\n"+
				"timeout,\n"+
				"usage_changepassword;";
		System.out.println(test.toUpperCase());
	}
}
