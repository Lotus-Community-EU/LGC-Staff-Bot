//Created by Maurice H. at 02.08.2024
package eu.lotusgaming.bot.command;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PurgeCommand extends ListenerAdapter{
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		if(event.getName().equals("purge")) {
			int count = event.getOption("messages").getAsInt();
			User targetUser = null;
			if(event.getOption("member") != null) {
				targetUser = event.getOption("member").getAsUser();
			}
			
			if(count >= 101) {
				event.deferReply(true).addContent("You can't delete more than 100 messages at once!").queue();
			}else {
				OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minus(14, ChronoUnit.DAYS);
				List<Message> messages = event.getChannel().getHistory().retrievePast(count).complete();
				messages.removeIf(m -> m.getTimeCreated().isBefore(twoWeeksAgo));
				if(targetUser != null) {
					User target2 = targetUser;
					messages.removeIf(m -> !m.getAuthor().getId().equals(target2.getId()));
				}
				if(messages.isEmpty()) {
					event.deferReply(true).addContent("Done deleting: " + event.getChannel().getAsMention()).queue();
				}else {
					event.deferReply(true).queue();
					List<String> msgs = new ArrayList<>();
					messages.forEach(m -> msgs.add(m.getAuthor().getAsMention() + ": " + m.getContentDisplay()));
					if(event.getChannel().getType() == ChannelType.TEXT) {
						event.getChannel().asTextChannel().deleteMessages(messages).complete();
					}else {
						for(Message msg : messages) {
							event.getChannel().deleteMessageById(msg.getIdLong()).queue();
						}
					}
					EmbedBuilder eb = ModlogController.baseEmbed(guild);
					eb.setTitle("Attempted to delete " + count + " Messages");
					eb.setAuthor(member.getUser().getEffectiveName(), null, member.getEffectiveAvatarUrl());
					StringBuilder sb = new StringBuilder();
					for(String s : msgs) {
						sb.append(s);
						sb.append("\n");
					}
					if(sb.toString().length() >= 1800) {
						eb.setDescription("Messages: \n" + sb.toString().substring(0, 1799));
					}else {
						eb.setDescription("Messages: \n" + sb.toString());
					}
					eb.setColor(ModlogController.red);
					event.getChannel().sendMessageEmbeds(eb.build()).queue(ra -> {
						ra.delete().queueAfter(20, TimeUnit.SECONDS);
					});
				}
			}
		}
	}

}

