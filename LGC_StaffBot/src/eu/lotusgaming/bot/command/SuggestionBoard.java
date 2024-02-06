//Created by Chris Wille at 06.02.2024
package eu.lotusgaming.bot.command;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class SuggestionBoard extends ListenerAdapter {
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("setsuggestionboardchannel")) {
			if(event.getOption("channel") == null) {
				event.deferReply(true).addContent("Hey, you must mention the text channel you want to have the Suggestion Board in.").queue();
			}else {
				if(event.getOption("channel").getAsChannel().getType() == ChannelType.TEXT) {
					TextChannel channel = event.getOption("channel").getAsChannel().asTextChannel();
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("Welcome to the Suggestionboard of Lotus Gaming");
					eb.setColor(Color.decode("#6cb547"));
					eb.setDescription("Thanks for creating a new suggestion via our suggestion board!\n"
							+ "You can select one of 4 Topics to suggest stuff in.\n"
							+ "Just choose the nearest topic for your suggestion.\n \n"
							+ "Fill out the Modal and your suggestion will appear in <#1204203118867513364>!\n"
							+ "\n"
							+ "Users can vote there as well in the dedicated thread of said suggestion they can discuss the suggestion.");
					channel.sendMessageEmbeds(eb.build()).addActionRow(
							Button.secondary("gamesugg", "Game Suggestion"),
							Button.secondary("botsugg", "Bot Suggestion"),
							Button.secondary("websugg", "Website Suggestion"),
							Button.danger("miscsugg", "Other Suggestions")
							).queue();
					event.reply("The suggestion info board has been sent into " + channel.getAsMention()).queue();
				}else {
					event.deferReply(true).addContent("Hey, the channel must be a text channel!").queue();
				}
			}
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Member member = event.getMember();
		if(event.getComponentId().equals("gamesugg")) {
			event.deferReply(true).addContent("Button gamesugg executed").queue();
		}else if(event.getComponentId().equals("botsugg")) {
			event.deferReply(true).addContent("Button botsugg executed").queue();
		}else if(event.getComponentId().equals("websugg")) {
			event.deferReply(true).addContent("Button websugg executed").queue();
		}else if(event.getComponentId().equals("miscsugg")) {
			event.deferReply(true).addContent("Button miscsugg executed").queue();
		}
	}

}

