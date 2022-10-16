import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

public static void main(String[] args)throws LoginException, InterruptedException {
		
		String token="NjU1MDgwMzU0NTM3MDEzMjQ4.G2KG-D.o7oiqcxcFA1G_8uoGv7OvaQ9mK43RK7GNv0VOo";
		if(args.length!=0)token=args[0];
		JDA jda= JDABuilder.createDefault(token)
				.enableIntents(GatewayIntent.GUILD_MESSAGES,GatewayIntent.GUILD_MESSAGE_REACTIONS)
				.addEventListeners(new Bot())
				.setActivity(Activity.playing("Type nb!help"))
				.build();
		
	}

}
