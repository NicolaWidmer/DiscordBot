import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {

public static void main(String[] args)throws LoginException, InterruptedException {
		
		String token="NjU1MDgwMzU0NTM3MDEzMjQ4.XfO5Hw.ZV84a7q0mjDqlx3QXeED8GL9X-A";
		JDA jda= new JDABuilder(token).addEventListeners(new Bot()).build();
		
	}

}
