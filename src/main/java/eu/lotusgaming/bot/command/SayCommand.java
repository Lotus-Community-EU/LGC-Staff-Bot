//Created by Christopher at 13.07.2024
package eu.lotusgaming.bot.command;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SayCommand extends ListenerAdapter {

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("say")) {
			String text = event.getOption("text").getAsString();
			if(event.getOption("targetchannel").getAsChannel().getType() == ChannelType.TEXT) {
				TextChannel channel = event.getOption("targetchannel").getAsChannel().asTextChannel();
				channel.sendMessage(text).queue();
				event.deferReply(true).addContent("The message has been sent into " + channel.getAsMention()).queue();
				EmbedBuilder eb = ModlogController.baseEmbed(event.getGuild());
				eb.setTitle(event.getUser().getName() + " used /say command");
				eb.setDescription("Message Content: " + text);
				eb.setColor(ModlogController.green);
				ModlogController.sendMessage(eb, event.getGuild());
			}else {
				event.deferReply(true).addContent("The channel you have mentioned is not a text channel!").queue();
			}
		}
	}
	
}

