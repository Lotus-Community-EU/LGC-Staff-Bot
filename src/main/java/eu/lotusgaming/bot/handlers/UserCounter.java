//Created by Chris Wille at 15.02.2024
package eu.lotusgaming.bot.handlers;

import java.util.TimerTask;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class UserCounter extends TimerTask{
	
	JDA jda;
	public UserCounter(JDA jda) {
		this.jda = jda;
	}
	
	
	@Override
	public void run() {
		Guild guild = jda.getGuildById(1153419306789507125l);
		int members = 0;
		for(Member member : guild.getMembers()) {
			if(!member.getUser().isBot()) {
				members++;
			}
		}
		guild.getVoiceChannelById(1207666072657076245l).getManager().setName("Users: " + members).queue();
	}
}