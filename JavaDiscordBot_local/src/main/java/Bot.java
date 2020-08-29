
import java.io.*;
import java.util.*;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import Evalator.ExprEvaluator;
import Musik.GuildMusicManager;
import Musik.AudioPlayerSendHandler;
import Musik.TrackScheduler;
import TicTacToe.TicTacToe;
import TicTacToe.TicTacToeAi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import Viergewinnt.ViergewinntAi;

import Game.Game;

public class Bot extends ListenerAdapter{
	
	 private final AudioPlayerManager playerManager;
	 private final Map<Long, GuildMusicManager> musicManagers;
	 private ExprEvaluator ee;
	 
	 private Map<String,Set<IDNameGame>>  nameToGame;
	
	public Bot() {
		this.musicManagers = new HashMap<>();
	    this.playerManager = new DefaultAudioPlayerManager();
	    AudioSourceManagers.registerRemoteSources(playerManager);
	    AudioSourceManagers.registerLocalSource(playerManager);
	    ee= new ExprEvaluator();
	    nameToGame = new HashMap<String,Set<IDNameGame>>();
	    
	}
	
	public void onReady(ReadyEvent event) {
		System.out.println("The Bot is Ready");
	}
	
	public void onMessageReceived(MessageReceivedEvent event) {
		
		if(event.getAuthor().isBot()) {
			return;
		}
		else {
			String message=event.getMessage().getContentDisplay();
			
			if(!message.startsWith("nb!")) {
				return;
			}
			message=message.substring(3);
			if(message.startsWith("play")) {
				String url =message.replace("play","");
				loadAndPlay(event.getTextChannel(),url);
			}
			message=message.toLowerCase();
			if(message.equals("help")) {
				help(event);
			}
			else if(message.equals("join")) {
				joinChanal(event);
			}
			else if(message.equals("skip")) {
				skipTrack(event.getTextChannel());
			}
			else if(message.startsWith("leave")) {
				leaveChanal(event);
			}
			else if(message.startsWith("ev")||message.startsWith("evaluate")) {
				
				String expresion =message.replace("ev","");
				expresion =expresion.replace("aluate","");
				expresion =expresion.replace(" ","");
				evaluate(event,expresion);
				
			}
			else if(message.startsWith("new viergewinnt pvb")) {
				makeViergewinntAi(event.getChannel(),event.getAuthor().getId());
			}
			else if(message.startsWith("new viergewinnt bvp")) {
				makeViergewinntAiBotStart(event.getChannel(),event.getAuthor().getId());
			}
			else if(message.startsWith("new viergewinnt")) {
				List<User> mentioned =event.getMessage().getMentionedUsers();
				if(mentioned.size()==0) {
					event.getChannel().sendMessage("You need to @ your oponnent").queue();
				}
				else if(mentioned.size()>1) {
					event.getChannel().sendMessage("You can only challenge one oponnent").queue();
				}
				else {
					String player2=mentioned.get(0).getId();
					makeViergewinntPvP(event.getAuthor().getId(),player2,event.getChannel());
				}
			}
			else if(message.startsWith("new tictactoe pvb")) {
				makeTicTacToeAi(event.getChannel(),event.getAuthor().getId());
			}
			else if(message.startsWith("new tictactoe bvp")) {
				makeTicTacToeAiBotStart(event.getChannel(),event.getAuthor().getId());
			}
			else if(message.startsWith("new tictactoe")){
				List<User> mentioned =event.getMessage().getMentionedUsers();
				if(mentioned.size()==0) {
					event.getChannel().sendMessage("You need to @ your oponnent").queue();
				}
				else if(mentioned.size()>1) {
					event.getChannel().sendMessage("You can only challenge one oponnent").queue();
				}
				else {
					String player2=mentioned.get(0).getId();
					makeTicTacToe(event.getAuthor().getId(),player2,event.getChannel());
				}
			}
			else if(message.startsWith("gamble")) {
				String number=message.replace("gamble","");
				number=number.replace(" ", "");
				try {
					int num=Integer.valueOf(number);
					gamble(num,event);
				}
				catch(Exception e){
					event.getChannel().sendMessage(number+" is not a number").queue();
				}
				
			}
			else if(message.startsWith("points")) {
				displayPoints(event);
			}
			else if(message.startsWith("ping")) {
				ping(event);
			}
			
		}
	}
	
	
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		if(event.getMember().getUser().equals(event.getJDA().getSelfUser()))return;
		String id=event.getMessageId();
		String name=event.getUserId();
		if(isCurrentGame(name,id)) {
			Set<IDNameGame> cur= nameToGame.get(name);
			for(IDNameGame v:cur) {
				if(v.id.equals(id)) {
					if(v.game instanceof ViergewinntAi)
						if(v.name.equals("AI"))reactOnViergewinntAi(name,v,event);
						else reactOnViergewinntPvP(name,v,event);
					else if(v.game instanceof TicTacToeAi) {
						if(v.name.equals("AI"))reactOnTicTacToeAi(name,v,event);
						else reactOnTicTacToePvP(name,v,event);
					}
				}
			}
		}
		
	}
	
	//returns true if the name and the id match and the id is from the last state of the game
	private boolean isCurrentGame(String name,String id) {
		Set<IDNameGame> cur= nameToGame.get(name);
		if(cur==null) {
			 nameToGame.put(name, new HashSet<IDNameGame>());
			return false;
		}
		for(IDNameGame v:cur) {
			if(v.id.equals(id))return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	//react on Messages
	private void joinChanal(MessageReceivedEvent e) {
		AudioManager audioManager = e.getGuild().getAudioManager();
		audioManager.openAudioConnection(e.getMember().getVoiceState().getChannel());
	}
	
	private void leaveChanal(MessageReceivedEvent e) {
		AudioManager audioManager = e.getGuild().getAudioManager();
		audioManager.closeAudioConnection();
	}
	
	private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
	    long guildId = Long.parseLong(guild.getId());
	    GuildMusicManager musicManager = musicManagers.get(guildId);

	    if (musicManager == null) {
	      musicManager = new GuildMusicManager(playerManager);
	      musicManagers.put(guildId, musicManager);
	    }

	    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

	    return musicManager;
	  }


	  private void loadAndPlay(final TextChannel channel, final String trackUrl) {
	    final GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

	    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
	      @Override
	      public void trackLoaded(AudioTrack track) {
	    	  
	        channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

	        play(channel.getGuild(), musicManager, track);
	      }

	      @Override
	      public void playlistLoaded(AudioPlaylist playlist) {
	        AudioTrack firstTrack = playlist.getSelectedTrack();

	        if (firstTrack == null) {
	          firstTrack = playlist.getTracks().get(0);
	        }

	        channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

	        play(channel.getGuild(), musicManager, firstTrack);
	      }

	      @Override
	      public void noMatches() {
	        channel.sendMessage("Nothing found by " + trackUrl).queue();
	      }

	      @Override
	      public void loadFailed(FriendlyException exception) {
	        channel.sendMessage("Could not play: " + exception.getMessage()).queue();
	      }
	    });
	  }

	  private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
	    connectToFirstVoiceChannel(guild.getAudioManager());

	    musicManager.scheduler.queue(track);
	  }

	  private void skipTrack(TextChannel channel) {
	    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
	    musicManager.scheduler.nextTrack();

	    channel.sendMessage("Skipped to next track.").queue();
	  }

	  private static void connectToFirstVoiceChannel(AudioManager audioManager) {
	    if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
	      for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
	        audioManager.openAudioConnection(voiceChannel);
	        break;
	      }
	    }
	  }
	
	
	
	public void help(MessageReceivedEvent event)  {
		String ans="";
		try {
			Scanner sc=new Scanner(new File("helpFile"));
			while(sc.hasNext()) {
				ans+="nb!"+sc.nextLine()+"\n";
				ans+=sc.nextLine()+"\n\n";
			}
			sc.close();
		}
		catch(Exception e) {
			ans=e.getMessage();
		}
		event.getChannel().sendMessage(ans).queue();
	}
	
	public void gamble(int num,MessageReceivedEvent event) {
		if(num<0) {
			event.getChannel().sendMessage("You cant gamble negative points").queue();
		}
		String name = event.getAuthor().toString();
		try {
			File originalFile = new File("Points");
			BufferedReader br = new BufferedReader(new FileReader(originalFile));
			File tempFile = new File("tempfile");
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
			String line=br.readLine();
			int cur=-1;
			while(line!=null){
				if(line.startsWith(name)) {
					cur=Integer.valueOf(line.replace(name+" ", ""));
				}
				else {
					pw.println(line);
					pw.flush();
				}
				line=br.readLine();
			}
			if(cur==-1) {
				cur=1000;
			}
			if(cur<num) {
				event.getChannel().sendMessage("You dont have enough points to gamble").queue();
				line=name+" "+cur;
			}
			else {
				int bonus=(int)((1-2*Math.random())*(num+1));
				int n=bonus+cur;
				if(bonus>=0) {
					event.getChannel().sendMessage("You won "+bonus+" points and you now have "+n+" points").queue();
				}
				else {
					event.getChannel().sendMessage("You lost "+bonus+" points and you now have "+Math.abs(n)+" points").queue();
				}
				line=name+" "+n;
			}
			pw.println(line);
			pw.flush();
			pw.close();
	        br.close();
	        if (!originalFile.delete()) {
	            System.out.println("Could not delete file");
	            return;
	        }
	        if (!tempFile.renameTo(originalFile)) {
	            System.out.println("Could not rename file");
	        }

		}
		catch(Exception e) {
			event.getChannel().sendMessage(e.toString()).queue();
		}
	}
	
	public void displayPoints(MessageReceivedEvent event) {
		String name=event.getAuthor().toString();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("Points")));
			String line=br.readLine();
			while(line!=null) {
				if(line.startsWith(name)) {
					event.getChannel().sendMessage(event.getAuthor().getAsMention()+" you have"+line.replace(name, "")+" points").queue();
				}
				line=br.readLine();
			}
			br.close();
		} catch (Exception e) {
			event.getChannel().sendMessage(e.toString()).queue();
		}
	}
	
	public void evaluate(MessageReceivedEvent event,String s) {
		if(s.isEmpty()) {
			event.getChannel().sendMessage("You need to give me a funktion to evaluate").queue();
			return;
		}
		String ans="";
		try {
			ans+=ee.evaluate(s);
		}
		catch(Exception e){
			ans=e.getMessage();
		}
		
		event.getChannel().sendMessage(ans).queue();
	}
	
	
	private void makeViergewinntPvP(String player1,String player2,MessageChannel channal) {
		if(nameToGame.get(player1)==null) {
			 nameToGame.put(player1, new HashSet<IDNameGame>());
		}
		if(nameToGame.get(player2)==null) {
			nameToGame.put(player2, new HashSet<IDNameGame>());
		}
		IDNameGame inv =new IDNameGame("0",player2,new ViergewinntAi(":blue_circle:",":red_circle:",":white_large_square:",0));
		nameToGame.get(player1).add(inv);
		channal.sendMessage(inv.game.toStringDiscord()).queue(message -> {
        	message.addReaction("\u0031\u20E3").queue();
        	message.addReaction("\u0032\u20E3").queue();
        	message.addReaction("\u0033\u20E3").queue();
        	message.addReaction("\u0034\u20E3").queue();
        	message.addReaction("\u0035\u20E3").queue();
        	message.addReaction("\u0036\u20E3").queue();
        	message.addReaction("\u0037\u20E3").queue();
        	inv.id=message.getId();
        	});	
	}
	private void viergewinntMovePvP(int num,MessageChannel channal,String name,IDNameGame inv) {
		try {
			Game game=inv.game;
			game.makeMove(num);
			channal.sendMessage(game.toStringDiscord()).queue(message -> {
	        	message.addReaction("\u0031\u20E3").queue();
	        	message.addReaction("\u0032\u20E3").queue();
	        	message.addReaction("\u0033\u20E3").queue();
	        	message.addReaction("\u0034\u20E3").queue();
	        	message.addReaction("\u0035\u20E3").queue();
	        	message.addReaction("\u0036\u20E3").queue();
	        	message.addReaction("\u0037\u20E3").queue();
	        	inv.id=message.getId();
			});
			String opponent=inv.name;
			nameToGame.get(name).remove(inv);
			if(!game.hasWinner()) {
				nameToGame.get(opponent).add(inv);
				inv.name=name;
			}
		}
		catch(Exception e) {
			channal.sendMessage(e.getMessage()).queue();
		}
	}
	
	private void makeViergewinntAi(MessageChannel channal,String name) {
	
		if(nameToGame.get(name)==null) {
			nameToGame.put(name, new HashSet<IDNameGame>());
		}
		
		IDNameGame inv =new IDNameGame("0","AI",new ViergewinntAi(":blue_circle:",":red_circle:",":white_large_square:",6));
		nameToGame.get(name).add(inv);
		channal.sendMessage(inv.game.toStringDiscord()).queue(message -> {
        	message.addReaction("\u0031\u20E3").queue();
        	message.addReaction("\u0032\u20E3").queue();
        	message.addReaction("\u0033\u20E3").queue();
        	message.addReaction("\u0034\u20E3").queue();
        	message.addReaction("\u0035\u20E3").queue();
        	message.addReaction("\u0036\u20E3").queue();
        	message.addReaction("\u0037\u20E3").queue();
        	inv.id=message.getId();
        	});	
	}
	
	private void makeViergewinntAiBotStart(MessageChannel channal,String name) {
		if(nameToGame.get(name)==null) {
			nameToGame.put(name, new HashSet<IDNameGame>());
		}
		IDNameGame inv =new IDNameGame("0","AI",new ViergewinntAi(":blue_circle:",":red_circle:",":white_large_square:",6));
		nameToGame.get(name).add(inv);
		try {
			inv.game.aiMove();
			channal.sendMessage(inv.game.toStringDiscord()).queue(message -> {
	        	message.addReaction("\u0031\u20E3").queue();
	        	message.addReaction("\u0032\u20E3").queue();
	        	message.addReaction("\u0033\u20E3").queue();
	        	message.addReaction("\u0034\u20E3").queue();
	        	message.addReaction("\u0035\u20E3").queue();
	        	message.addReaction("\u0036\u20E3").queue();
	        	message.addReaction("\u0037\u20E3").queue();
	        	message.addReaction("🆕").queue();
	        	inv.id=message.getId();
	        });
		}
		catch(Exception e) {
			try {
				channal.sendMessage(e.getMessage()).queue();
			}
			catch(Exception p) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private void viergewinntMoveAi(int num,MessageChannel channal,String name,IDNameGame inv) {
		Game game=inv.game;
		try {
			game.makeMove(num);
			channal.sendMessage(game.toString()).queue();
			try {
				game.aiMove();
				channal.sendMessage(game.toStringDiscord()).queue(message -> {
		        	message.addReaction("\u0031\u20E3").queue();
		        	message.addReaction("\u0032\u20E3").queue();
		        	message.addReaction("\u0033\u20E3").queue();
		        	message.addReaction("\u0034\u20E3").queue();
		        	message.addReaction("\u0035\u20E3").queue();
		        	message.addReaction("\u0036\u20E3").queue();
		        	message.addReaction("\u0037\u20E3").queue();
		        	message.addReaction("🆕").queue();
		        	inv.id=message.getId();
		        });
			}
			catch(Exception e) {
				try {
					channal.sendMessage(e.getMessage()).queue();
				}
				catch(Exception p) {
					System.out.println(e.getMessage());
				}
			}
		}
		catch(Exception e) {
			try {
				channal.sendMessage(e.getMessage()).queue();
			}
			catch(Exception p) {
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	
	
	private void makeTicTacToe(String player1,String player2,MessageChannel channal) {
		if(nameToGame.get(player1)==null) {
			 nameToGame.put(player1, new HashSet<IDNameGame>());
		}
		if(nameToGame.get(player2)==null) {
			nameToGame.put(player2, new HashSet<IDNameGame>());
		}
		IDNameGame inv =new IDNameGame("0",player2,new TicTacToeAi(":x:",":o:",":white_large_square:"));
		nameToGame.get(player1).add(inv);
		channal.sendMessage(inv.game.toStringDiscord()).queue(message -> {
       	message.addReaction("\u0031\u20E3").queue();
       	message.addReaction("\u0032\u20E3").queue();
       	message.addReaction("\u0033\u20E3").queue();
       	message.addReaction("\u0034\u20E3").queue();
       	message.addReaction("\u0035\u20E3").queue();
       	message.addReaction("\u0036\u20E3").queue();
       	message.addReaction("\u0037\u20E3").queue();
       	message.addReaction("\u0038\u20E3").queue();
       	message.addReaction("\u0039\u20E3").queue();
       	inv.id=message.getId();
       	});	
	}
	
	private void tictactoeMovePvP(int num,MessageChannel channal,String name,IDNameGame inv) {
		try {
			Game game=inv.game;
			game.makeMove(num);
			channal.sendMessage(game.toStringDiscord()).queue(message -> {
	        	message.addReaction("\u0031\u20E3").queue();
	        	message.addReaction("\u0032\u20E3").queue();
	        	message.addReaction("\u0033\u20E3").queue();
	        	message.addReaction("\u0034\u20E3").queue();
	        	message.addReaction("\u0035\u20E3").queue();
	        	message.addReaction("\u0036\u20E3").queue();
	        	message.addReaction("\u0037\u20E3").queue();
	        	message.addReaction("\u0038\u20E3").queue();
	        	message.addReaction("\u0039\u20E3").queue();
	        	inv.id=message.getId();
			});
			String opponent=inv.name;
			nameToGame.get(name).remove(inv);
			if(!game.hasWinner()) {
				nameToGame.get(opponent).add(inv);
				inv.name=name;
			}
		}
		catch(Exception e) {
			channal.sendMessage(e.getMessage()).queue();
		}
	}
	
	
	private void makeTicTacToeAi(MessageChannel channal,String name) {
		if(nameToGame.get(name)==null) {
			nameToGame.put(name, new HashSet<IDNameGame>());
		}
		
		IDNameGame inv =new IDNameGame("0","AI",new TicTacToeAi(":x:",":o:",":white_large_square:"));
		nameToGame.get(name).add(inv);
		channal.sendMessage(inv.game.toStringDiscord()).queue(message -> {
        	message.addReaction("\u0031\u20E3").queue();
        	message.addReaction("\u0032\u20E3").queue();
        	message.addReaction("\u0033\u20E3").queue();
        	message.addReaction("\u0034\u20E3").queue();
        	message.addReaction("\u0035\u20E3").queue();
        	message.addReaction("\u0036\u20E3").queue();
        	message.addReaction("\u0037\u20E3").queue();
        	message.addReaction("\u0038\u20E3").queue();
        	message.addReaction("\u0039\u20E3").queue();
        	inv.id=message.getId();
        	});	
	}
	
	private void makeTicTacToeAiBotStart(MessageChannel channal,String name) {
		if(nameToGame.get(name)==null) {
			nameToGame.put(name, new HashSet<IDNameGame>());
		}
		IDNameGame inv =new IDNameGame("0","AI",new TicTacToeAi(":x:",":o:",":white_large_square:"));
		nameToGame.get(name).add(inv);
		try {
			inv.game.aiMove();
			channal.sendMessage(inv.game.toStringDiscord()).queue(message -> {
	        	message.addReaction("\u0031\u20E3").queue();
	        	message.addReaction("\u0032\u20E3").queue();
	        	message.addReaction("\u0033\u20E3").queue();
	        	message.addReaction("\u0034\u20E3").queue();
	        	message.addReaction("\u0035\u20E3").queue();
	        	message.addReaction("\u0036\u20E3").queue();
	        	message.addReaction("\u0037\u20E3").queue();
	        	message.addReaction("\u0038\u20E3").queue();
	        	message.addReaction("\u0039\u20E3").queue();
	        	message.addReaction("🆕").queue();
	        	inv.id=message.getId();
	        });
		}
		catch(Exception e) {
			try {
				channal.sendMessage(e.getMessage()).queue();
			}
			catch(Exception p) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private void tictactoeMoveAi(int num,MessageChannel channal,String name,IDNameGame inv) {
		Game game=inv.game;
		try {
			game.makeMove(num);
			channal.sendMessage(game.toString()).queue();
			try {
				game.aiMove();
				channal.sendMessage(game.toStringDiscord()).queue(message -> {
		        	message.addReaction("\u0031\u20E3").queue();
		        	message.addReaction("\u0032\u20E3").queue();
		        	message.addReaction("\u0033\u20E3").queue();
		        	message.addReaction("\u0034\u20E3").queue();
		        	message.addReaction("\u0035\u20E3").queue();
		        	message.addReaction("\u0036\u20E3").queue();
		        	message.addReaction("\u0037\u20E3").queue();
		        	message.addReaction("\u0038\u20E3").queue();
		        	message.addReaction("\u0039\u20E3").queue();
		        	message.addReaction("🆕").queue();
		        	inv.id=message.getId();
		        });
			}
			catch(Exception e) {
				try {
					channal.sendMessage(e.getMessage()).queue();
				}
				catch(Exception p) {
					System.out.println(e.getMessage());
				}
			}
		}
		catch(Exception e) {
			try {
				channal.sendMessage(e.getMessage()).queue();
			}
			catch(Exception p) {
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	private void ping(MessageReceivedEvent event) {
		
        event.getChannel().sendMessage("hi "+event.getAuthor().getAsMention()).queue(message -> {
        	message.addReaction("\u0031\u20E3").queue();
        	message.addReaction("\u0032\u20E3").queue();
        	message.addReaction("\u0033\u20E3").queue();
        	message.addReaction("\u0034\u20E3").queue();
        	message.addReaction("\u0035\u20E3").queue();
        	message.addReaction("\u0036\u20E3").queue();
        	message.addReaction("\u0037\u20E3").queue();
        	message.addReaction("🆕").queue();
        });
	}
	
	
	
	//react on Discord reactions
	private void reactOnViergewinntAi(String name,IDNameGame inv,GuildMessageReactionAddEvent event){
		String reaktion= event.getReactionEmote().getName();
		if(reaktion.equals("1⃣")) {
			viergewinntMoveAi(1,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("2⃣")) {
			viergewinntMoveAi(2,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("3⃣")) {
			viergewinntMoveAi(3,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("4⃣")) {
			viergewinntMoveAi(4,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("5⃣")) {
			viergewinntMoveAi(5,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("6⃣")) {
			viergewinntMoveAi(6,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("7⃣")) {
			viergewinntMoveAi(7,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("🆕")) {
			makeViergewinntAiBotStart(event.getChannel(),name);
		}
		
	}
	

	private void reactOnViergewinntPvP(String name,IDNameGame inv,GuildMessageReactionAddEvent event) {
		String reaktion= event.getReactionEmote().getName();
		if(reaktion.equals("1⃣")) {
			viergewinntMovePvP(1,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("2⃣")) {
			viergewinntMovePvP(2,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("3⃣")) {
			viergewinntMovePvP(3,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("4⃣")) {
			viergewinntMovePvP(4,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("5⃣")) {
			viergewinntMovePvP(5,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("6⃣")) {
			viergewinntMovePvP(6,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("7⃣")) {
			viergewinntMovePvP(7,event.getChannel(),name,inv);
		}
	}
	
	private void reactOnTicTacToeAi(String name,IDNameGame inv,GuildMessageReactionAddEvent event){
		String reaktion= event.getReactionEmote().getName();
		if(reaktion.equals("1⃣")) {
			tictactoeMoveAi(1,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("2⃣")) {
			tictactoeMoveAi(2,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("3⃣")) {
			tictactoeMoveAi(3,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("4⃣")) {
			tictactoeMoveAi(4,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("5⃣")) {
			tictactoeMoveAi(5,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("6⃣")) {
			tictactoeMoveAi(6,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("7⃣")) {
			tictactoeMoveAi(7,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("8⃣")) {
			tictactoeMoveAi(8,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("9⃣")) {
			tictactoeMoveAi(9,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("🆕")) {
			makeTicTacToeAiBotStart(event.getChannel(),name);
		}
		
	}
	
	private void reactOnTicTacToePvP(String name,IDNameGame inv,GuildMessageReactionAddEvent event){
		String reaktion= event.getReactionEmote().getName();
		if(reaktion.equals("1⃣")) {
			tictactoeMovePvP(1,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("2⃣")) {
			tictactoeMovePvP(2,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("3⃣")) {
			tictactoeMovePvP(3,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("4⃣")) {
			tictactoeMovePvP(4,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("5⃣")) {
			tictactoeMovePvP(5,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("6⃣")) {
			tictactoeMovePvP(6,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("7⃣")) {
			tictactoeMovePvP(7,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("8⃣")) {
			tictactoeMovePvP(8,event.getChannel(),name,inv);
		}
		else if(reaktion.equals("9⃣")) {
			tictactoeMovePvP(9,event.getChannel(),name,inv);
		}
		
		
	}
	
	
		
	
}

//has the ID, the game itself and the opponent of a game as a field
class IDNameGame{
	String id;
	String name;
	Game game;
	public IDNameGame(String i,String n,Game v) {
		id=i;
		name=n;
		game=v;
	}
	public String toString() {
		return "id: "+id+"\n"+"name: "+name;
	}
	
}
