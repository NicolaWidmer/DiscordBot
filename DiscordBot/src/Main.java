import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {

public static void main(String[] args)throws LoginException, InterruptedException {
		
		String token="";
		JDA jda= new JDABuilder(token).addEventListeners(new Bot()).build();
		
	}

}
