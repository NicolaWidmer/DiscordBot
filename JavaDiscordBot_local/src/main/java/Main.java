import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {

public static void main(String[] args)throws LoginException, InterruptedException {
		
		String token="NjU1MDgwMzU0NTM3MDEzMjQ4.XmPebw.F5yB6b8VZ0Jjg82JJtnZCtnF-yM";
		JDA jda= new JDABuilder(token).addEventListeners(new Bot()).build();
		
	}

}
