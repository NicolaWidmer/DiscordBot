
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

import Viergewinnt.Viergewinnt;
import Viergewinnt.ViergewinntAi;

public class Bot extends ListenerAdapter{
	
	 private final AudioPlayerManager playerManager;
	 private final Map<Long, GuildMusicManager> musicManagers;
	 private ExprEvaluator ee;
	 
	 private String viergewinntId;
	 private Viergewinnt viergewinnt;
	 
	 private Map<String,Set<IDNameViergewinnt>> nameToViergewinnt;
	 
	 private Map<String,ViergewinntAi> nameToViergewinntAi;
	 private Map<String,String> nameToViergewinntAiId;
	 
	 private TicTacToe tictactoe;
	 
	 private TicTacToeAi tictactoeai;
	
	public static void main(String[] args)throws LoginException, InterruptedException {
		
		JDA jda= new JDABuilder("NjU1MDgwMzU0NTM3MDEzMjQ4.Xlzmmw.s7FTtulznxArLZ56tWYm1c3BZrg").addEventListeners(new Bot()).build();
		
	}
	
	public Bot() {
		this.musicManagers = new HashMap<>();
	    this.playerManager = new DefaultAudioPlayerManager();
	    AudioSourceManagers.registerRemoteSources(playerManager);
	    AudioSourceManagers.registerLocalSource(playerManager);
	    ee= new ExprEvaluator();
	    nameToViergewinntAi = new HashMap<String,ViergewinntAi>();
	    nameToViergewinntAiId = new HashMap<String,String>();
	    nameToViergewinnt = new HashMap<String,Set<IDNameViergewinnt>>();
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
			if(message.equals("help")) {
				help(event);
			}
			else if(message.equals("join")||message.equals("Join")) {
				joinChanal(event);
			}
			else if(message.equals("skip")||message.equals("Skip")) {
				skipTrack(event.getTextChannel());
			}
			else if(message.startsWith("play")||message.startsWith("Play")) {
				String url =message.substring(5);
				loadAndPlay(event.getTextChannel(),url);
			}
			else if(message.startsWith("leave")||message.startsWith("Leave")) {
				leaveChanal(event);
			}
			else if(message.startsWith("ev")||message.startsWith("evaluate")) {
				
				String expresion =message.replace("ev","");
				expresion =expresion.replace("aluate","");
				expresion =expresion.replace(" ","");
				evaluate(event,expresion);
				
			}
			else if(message.startsWith("new Viergewinnt PvB")||message.startsWith("new viergewinnt pvb")) {
				makeViergewinntAi(event.getChannel(),event.getAuthor().getId());
			}
			else if(message.startsWith("new Viergewinnt BvP")||message.startsWith("new viergewinnt bvp")) {
				makeViergewinntAiBotStart(event.getChannel(),event.getAuthor().getId());
			}
			else if(message.startsWith("new Viergewinnt")||message.startsWith("new viergewinnt")) {
				List<User> mentioned =event.getMessage().getMentionedUsers();
				if(mentioned.size()==0) {
					makeViergewinnt(event.getChannel());
				}
				else {
					String player2=mentioned.get(0).getId();
					makeViergewinntMovePvP(event.getAuthor().getId(),player2,event.getChannel());
				}
			}
			else if(message.startsWith("Viergewinnt move")||message.startsWith("viergewinnt move")) {
				String number=message.replace("Viergewinnt move", "");
				number= number.replace("viergewinnt move", "");
				number= number.replace(" ","");
				int num=Integer.valueOf(number);
				viergewinntMove(num,event.getChannel());
			}
			else if(message.startsWith("new TicTacToe PvB")||message.startsWith("new tictactoe pvb")) {
				makeTicTacToeAi(event);
			}
			else if(message.startsWith("new TicTacToe BvP")||message.startsWith("new tictactoe bvp")) {
				makeTicTacToeAiBotStart(event);
			}
			else if(message.startsWith("TicTacToe move PvB")||message.startsWith("tictactoe move PvB")) {
				String number=message.replace("TicTacToe move PvB", "");
				number= number.replace("tictactoe move pvb", "");
				number= number.replace(" ","");
				int num=Integer.valueOf(number);
				tictactoeMoveAi(num,event);
			}
			else if(message.startsWith("new TicTacToe")||message.startsWith("new tictactoe")){
				makeTicTacToe(event);
			}
			else if(message.startsWith("TicTacToe move")||message.startsWith("tictactoe move")) {
				String number=message.replace("TicTacToe move", "");
				number= number.replace("tictactoe move", "");
				number= number.replace(" ","");
				int num=Integer.valueOf(number);
				tictactoeMove(num,event);
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
		if(id.equals(viergewinntId)) {
			reactOnViergewinnt(event);
		}
		else if(nameToViergewinntAiId.containsKey(name)&&nameToViergewinntAiId.get(name).equals(id)) {
			reactOnViergewinntAi(event,name);
		}
		else if(isCurrentViergewinnt(name,id)) {
			Set<IDNameViergewinnt> cur=nameToViergewinnt.get(name);
			for(IDNameViergewinnt v:cur) {
				if(v.id.equals(id))reactOnViergewinntPvP(name,v,event);
			}
		}
		
		
	}
	
	private boolean isCurrentViergewinnt(String name,String id) {
		Set<IDNameViergewinnt> cur=nameToViergewinnt.get(name);
		for(IDNameViergewinnt v:cur) {
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
			
		}
		catch(Exception e) {
			ans=e.getMessage();
		}
		event.getChannel().sendMessage(ans).queue();
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
	
	private void makeViergewinnt(MessageChannel channal) {
		viergewinnt= new Viergewinnt(":blue_circle:",":red_circle:",":white_large_square:");
		channal.sendMessage(viergewinnt.toStringDiscord()).queue(message -> {
        	message.addReaction("\u0031\u20E3").queue();
        	message.addReaction("\u0032\u20E3").queue();
        	message.addReaction("\u0033\u20E3").queue();
        	message.addReaction("\u0034\u20E3").queue();
        	message.addReaction("\u0035\u20E3").queue();
        	message.addReaction("\u0036\u20E3").queue();
        	message.addReaction("\u0037\u20E3").queue();
        	message.addReaction("🆕").queue();
        	viergewinntId=message.getId();
        	});
	}
	
	private void viergewinntMove(int num,MessageChannel channal) {
		try {
			viergewinnt.insert(num);
			channal.sendMessage(viergewinnt.toStringDiscord()).queue(message -> {
	        	message.addReaction("\u0031\u20E3").queue();
	        	message.addReaction("\u0032\u20E3").queue();
	        	message.addReaction("\u0033\u20E3").queue();
	        	message.addReaction("\u0034\u20E3").queue();
	        	message.addReaction("\u0035\u20E3").queue();
	        	message.addReaction("\u0036\u20E3").queue();
	        	message.addReaction("\u0037\u20E3").queue();
	        	message.addReaction("🆕").queue();
	        	viergewinntId=message.getId();
			});
		}
		catch(Exception e) {
			channal.sendMessage(e.getMessage()).queue();
		}
	}
	
	private void makeViergewinntMovePvP(String player1,String player2,MessageChannel channal) {
		if(nameToViergewinnt.get(player1)==null) {
			nameToViergewinnt.put(player1, new HashSet<IDNameViergewinnt>());
		}
		IDNameViergewinnt inv =new IDNameViergewinnt("0",player2,new Viergewinnt(":blue_circle:",":red_circle:",":white_large_square:"));
		nameToViergewinnt.get(player1).add(inv);
		if(nameToViergewinnt.get(player2)==null) {
			nameToViergewinnt.put(player2, new HashSet<IDNameViergewinnt>());
		}
		channal.sendMessage(inv.viergewinnt.toStringDiscord()).queue(message -> {
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
	private void viergewinntMovePvP(int num,MessageChannel channal,String name,IDNameViergewinnt inv) {
		try {
			Viergewinnt viergewinnt=inv.viergewinnt;
			viergewinnt.insert(num);
			channal.sendMessage(viergewinnt.toStringDiscord()).queue(message -> {
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
			nameToViergewinnt.get(name).remove(inv);
			if(!viergewinnt.hasWinner()) {
				nameToViergewinnt.get(opponent).add(inv);
				inv.name=name;
			}
		}
		catch(Exception e) {
			channal.sendMessage(e.getMessage()).queue();
		}
	}
	
	private void makeViergewinntAi(MessageChannel channal,String name) {
		ViergewinntAi viergewinntai=new ViergewinntAi(":blue_circle:",":red_circle:",":white_large_square:",6);
		nameToViergewinntAi.put(name,viergewinntai);
		channal.sendMessage(viergewinntai.toStringDiscord()).queue(message -> {
        	message.addReaction("\u0031\u20E3").queue();
        	message.addReaction("\u0032\u20E3").queue();
        	message.addReaction("\u0033\u20E3").queue();
        	message.addReaction("\u0034\u20E3").queue();
        	message.addReaction("\u0035\u20E3").queue();
        	message.addReaction("\u0036\u20E3").queue();
        	message.addReaction("\u0037\u20E3").queue();
        	message.addReaction("🆕").queue();
        	nameToViergewinntAiId.put(name,message.getId());
        });
	}
	
	private void makeViergewinntAiBotStart(MessageChannel channal,String name) {
		ViergewinntAi viergewinntai=new ViergewinntAi(":blue_circle:",":red_circle:",":white_large_square:",6);
		nameToViergewinntAi.put(name,viergewinntai);
		try {
			viergewinntai.aiMove();
			channal.sendMessage(viergewinntai.toStringDiscord()).queue(message -> {
	        	message.addReaction("\u0031\u20E3").queue();
	        	message.addReaction("\u0032\u20E3").queue();
	        	message.addReaction("\u0033\u20E3").queue();
	        	message.addReaction("\u0034\u20E3").queue();
	        	message.addReaction("\u0035\u20E3").queue();
	        	message.addReaction("\u0036\u20E3").queue();
	        	message.addReaction("\u0037\u20E3").queue();
	        	message.addReaction("🆕").queue();
	        	nameToViergewinntAiId.put(name,message.getId());
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
	
	private void viergewinntMoveAi(int num,MessageChannel channal,String name) {
		ViergewinntAi viergewinntai=nameToViergewinntAi.get(name);
		try {
			viergewinntai.insert(num);
			channal.sendMessage(viergewinntai.toString()).queue();
			try {
				viergewinntai.aiMove();
				channal.sendMessage(viergewinntai.toStringDiscord()).queue(message -> {
		        	message.addReaction("\u0031\u20E3").queue();
		        	message.addReaction("\u0032\u20E3").queue();
		        	message.addReaction("\u0033\u20E3").queue();
		        	message.addReaction("\u0034\u20E3").queue();
		        	message.addReaction("\u0035\u20E3").queue();
		        	message.addReaction("\u0036\u20E3").queue();
		        	message.addReaction("\u0037\u20E3").queue();
		        	message.addReaction("🆕").queue();
		        	nameToViergewinntAiId.put(name,message.getId());
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
	
	
	
	private void makeTicTacToe(MessageReceivedEvent event) {
		tictactoe= new TicTacToe(":x:",":o:",":white_large_square:");
		event.getChannel().sendMessage(tictactoe.toStringDiscord()).queue();
	}
	
	private void tictactoeMove(int num,MessageReceivedEvent event) {
		try {
			tictactoe.makeMovePlayer(num);
			event.getChannel().sendMessage(tictactoe.toStringDiscord()).queue();
		}
		catch(Exception e) {
			event.getChannel().sendMessage(e.getMessage()).queue();
		}
	}
	
	private void makeTicTacToeAi(MessageReceivedEvent event) {
		tictactoeai= new TicTacToeAi(":x:",":o:",":white_large_square:");
		event.getChannel().sendMessage(tictactoeai.toStringDiscord()).queue();
	}
	
	private void makeTicTacToeAiBotStart(MessageReceivedEvent event) {
		tictactoeai= new TicTacToeAi(":x:",":o:",":white_large_square:");
		try {
			tictactoeai.aiMove();
			event.getChannel().sendMessage(tictactoeai.toStringDiscord()).queue();
		}
		catch(Exception e) {
			event.getChannel().sendMessage(e.getMessage()).queue();
		}
	}
	
	private void tictactoeMoveAi(int num,MessageReceivedEvent event) {
		try {
			tictactoeai.makeMovePlayer(num);
			event.getChannel().sendMessage(tictactoeai.toString()).queue();
			try {
				tictactoeai.aiMove();
				event.getChannel().sendMessage(tictactoeai.toStringDiscord()).queue();
			}
			catch(Exception e) {
				event.getChannel().sendMessage(e.getMessage()).queue();;
			}
		}
		catch(Exception e) {
			event.getChannel().sendMessage(e.getMessage()).queue();
		}
		
	}
	
	private void ping(MessageReceivedEvent event) {
		
        event.getChannel().sendMessage("hi").queue(message -> {
        	message.addReaction("\u0031\u20E3").queue();
        	message.addReaction("\u0032\u20E3").queue();
        	message.addReaction("\u0033\u20E3").queue();
        	message.addReaction("\u0034\u20E3").queue();
        	message.addReaction("\u0035\u20E3").queue();
        	message.addReaction("\u0036\u20E3").queue();
        	message.addReaction("\u0037\u20E3").queue();
        });
	}
	
	
	//react on Discord reactions
	
	private void reactOnViergewinntAi(GuildMessageReactionAddEvent event,String name){
		String reaktion= event.getReactionEmote().getName();
		if(reaktion.equals("1⃣")) {
			viergewinntMoveAi(1,event.getChannel(),name);
		}
		else if(reaktion.equals("2⃣")) {
			viergewinntMoveAi(2,event.getChannel(),name);
		}
		else if(reaktion.equals("3⃣")) {
			viergewinntMoveAi(3,event.getChannel(),name);
		}
		else if(reaktion.equals("4⃣")) {
			viergewinntMoveAi(4,event.getChannel(),name);
		}
		else if(reaktion.equals("5⃣")) {
			viergewinntMoveAi(5,event.getChannel(),name);
		}
		else if(reaktion.equals("6⃣")) {
			viergewinntMoveAi(6,event.getChannel(),name);
		}
		else if(reaktion.equals("7⃣")) {
			viergewinntMoveAi(7,event.getChannel(),name);
		}
		else if(reaktion.equals("🆕")) {
			makeViergewinntAiBotStart(event.getChannel(),name);
		}
		
	}
	
	private void reactOnViergewinnt(GuildMessageReactionAddEvent event){
		String reaktion= event.getReactionEmote().getName();
		if(reaktion.equals("1⃣")) {
			viergewinntMove(1,event.getChannel());
		}
		else if(reaktion.equals("2⃣")) {
			viergewinntMove(2,event.getChannel());
		}
		else if(reaktion.equals("3⃣")) {
			viergewinntMove(3,event.getChannel());
		}
		else if(reaktion.equals("4⃣")) {
			viergewinntMove(4,event.getChannel());
		}
		else if(reaktion.equals("5⃣")) {
			viergewinntMove(5,event.getChannel());
		}
		else if(reaktion.equals("6⃣")) {
			viergewinntMove(6,event.getChannel());
		}
		else if(reaktion.equals("7⃣")) {
			viergewinntMove(7,event.getChannel());
		}
		else if(reaktion.equals("🆕")) {
			makeViergewinnt(event.getChannel());
		}
	}
	

	private void reactOnViergewinntPvP(String name,IDNameViergewinnt inv,GuildMessageReactionAddEvent event) {
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
	
	
		
	
}

//has the ID, the game itself and the opponent of a game as a field
class IDNameViergewinnt{
	String id;
	String name;
	Viergewinnt viergewinnt;
	public IDNameViergewinnt(String i,String n,Viergewinnt v) {
		id=i;
		name=n;
		viergewinnt=v;
	}
	public String toString() {
		return "id: "+id+"\n"+"name: "+name;
	}
	
}
