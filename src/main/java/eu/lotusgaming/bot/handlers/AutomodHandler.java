//Created by Chris Wille at 23.02.2024
package eu.lotusgaming.bot.handlers;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutomodHandler extends ListenerAdapter {
	
	static long publicGuild = 1153419306789507125l;
	static long publicModlog = 1201222774107148428l;
	static long staffGuild = 1066812641768640542l;
	static long staffModlog = 1211661756477214750l;
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.isFromGuild()) {
			if(!event.getAuthor().isBot()) {
				Guild guild = event.getGuild();
				Member member = event.getMember();
				double messagePercentage = getAverageCaps(event.getMessage().getContentRaw());
				if(guild.getIdLong() == publicGuild) {
					if(messagePercentage >= 0.50) {
						EmbedBuilder eb = defaultEmbed(guild, "Caps", member);
						DecimalFormat dF = new DecimalFormat("#.##");
						eb.addField("Text", "Caps Percentage: " + dF.format(messagePercentage) + "% \n" + event.getMessage().getContentDisplay(), true);
						guild.getTextChannelById(publicModlog).sendMessageEmbeds(eb.build()).queue();
						event.getMessage().delete().queue();
						event.getChannel().sendMessage("Slow down with caps, " + member.getAsMention() + "!").queue(ra -> {
							ra.delete().queueAfter(10, TimeUnit.SECONDS);
						});
					}
					if(event.getMessage().getMentions().getMembers().size() >= 4) {
						EmbedBuilder eb = defaultEmbed(guild, "Mass Mention", member);
						eb.addField("Mentions", "Individual People mentioned: " + event.getMessage().getMentions().getMembers().size() + "\n" + event.getMessage().getContentRaw(), true);
						guild.getTextChannelById(publicModlog).sendMessageEmbeds(eb.build()).queue();
						event.getMessage().delete().queue();
						event.getChannel().sendMessage("Slow down with mentions, " + member.getAsMention() + "!").queue(ra -> {
							ra.delete().queueAfter(10, TimeUnit.SECONDS);
						});
					}
					if(event.getMessage().getAttachments().size() >= 6) {
						EmbedBuilder eb = defaultEmbed(guild, "Mass Mention", member);
						eb.addField("Attachments", "Attachments count: " + event.getMessage().getAttachments().size() + "\n" + event.getMessage().getContentDisplay(), true);
						guild.getTextChannelById(publicModlog).sendMessageEmbeds(eb.build()).queue();
						event.getMessage().delete().queue();
						event.getChannel().sendMessage("Maximal 6 Attachments are per message permitted, " + member.getAsMention() + "!").queue(ra -> {
							ra.delete().queueAfter(10, TimeUnit.SECONDS);
						});
					}
				}
			}
		}
	}
	
	EmbedBuilder defaultEmbed(Guild guild, String automodType, Member member) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setAuthor("Lotus Automod", null, guild.getJDA().getSelfUser().getEffectiveAvatarUrl());
		eb.setTitle("Triggered: " + automodType);
		eb.setFooter("Triggered by " + member.getEffectiveName(), member.getEffectiveAvatarUrl());
		return eb;
	}

	private double getAverageCaps(String input) {
		if(input == null || input.isEmpty()) {
			return 0.0;
		}
		
		int totalCaps = 0;
		int totalChars = 0;
		
		for(int i = 0; i < input.length(); i++) {
			char current = input.charAt(i);
			if(Character.isUpperCase(current)) {
				totalCaps++;
			}
			totalChars++;
		}
		
		if(totalChars == 0) return 0.0;
		
		double percentage = (double) totalCaps / totalChars;
		return percentage;
	}
}