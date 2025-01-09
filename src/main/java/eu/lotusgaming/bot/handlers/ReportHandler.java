//Created by Maurice H. at 05.12.2024
package eu.lotusgaming.bot.handlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReportHandler extends ListenerAdapter {
	
	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event) {
		if(event.isFromGuild()) {
			Guild guild = event.getGuild();
			Member member = event.getMember();
			if(event.getName().equals("Report this!")) {
				if(guild.getIdLong() == 1153419306789507125L) {
					String message = event.getTarget().getContentRaw();
					TextChannel targetChannel = event.getGuild().getTextChannelById(1314028518618697848L);
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("A member has reported a message!");
					eb.setColor(ModlogController.red);
					eb.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
					eb.addField("Message Author", event.getTarget().getAuthor().getAsMention(), false);
					if(message.length() >= 1536) {
						message = message.substring(0, 1536);
						message += " ...\n[Jump to message](" + event.getTarget().getJumpUrl() + ")";
					}else {
						message += " \n[Jump to message](" + event.getTarget().getJumpUrl() + ")";
					}
					eb.addField("Message", message, false);
					eb.addField("Channel", event.getTarget().getChannel().getAsMention(), false);
					String dateTime = new SimpleDateFormat("dd.MM.yy HH:mm").format(new Date());
					eb.setFooter(guild.getName() + " ● " + dateTime, guild.getIconUrl());
					targetChannel.sendMessageEmbeds(eb.build()).queue();
					event.deferReply(true).setContent("The message has been reported!").queue();
				}else {
					event.deferReply(true).setContent("This command is only available on the public Lotus Gaming Discord Server!").queue();
				}
			}
		}
	}

}

