//Created by Chris Wille at 06.02.2024
package eu.lotusgaming.bot.command;

import java.awt.Color;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.bot.main.LotusManager;
import eu.lotusgaming.bot.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

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
					eb.setTitle("Welcome to the Suggestion Board of Lotus Gaming");
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
		}else if(event.getName().equals("setsuggestionmessagechannel")) {
			if(event.getOption("channel") == null) {
				event.deferReply(true).addContent("Hey, you must mention the text channel you want to have the Suggestion Board in.").queue();
			}else {
				if(event.getOption("channel").getAsChannel().getType() == ChannelType.TEXT) {
					TextChannel channel = event.getOption("channel").getAsChannel().asTextChannel();
					try {
						YamlFile cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
						cfg.set("Suggestion.PostChannel", channel.getIdLong());
						cfg.save();
					} catch (IOException e) {
						e.printStackTrace();
					}
					event.reply("The channel where newly made suggestions are sent in has been set to " + channel.getAsMention()).queue();
				}else {
					event.deferReply(true).addContent("Hey, the channel must be a text channel!").queue();
				}
			}
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		Guild guild = event.getGuild();
		if(event.getComponentId().equals("gamesugg")) {
			TextInput title = TextInput.create("gametitle", "Title", TextInputStyle.SHORT)
					.setRequiredRange(10, 100)
					.build();
			TextInput desc = TextInput.create("gamedesc", "Description", TextInputStyle.PARAGRAPH)
					.setRequiredRange(50, 1000)
					.setPlaceholder("Explain your suggestion as good as you can. Upload Pictures in it's created thread afterward.")
					.build();
			TextInput whyToAdd = TextInput.create("gamewta", "Why should we add it?", TextInputStyle.PARAGRAPH)
					.setRequiredRange(50, 1000)
					.setPlaceholder("Give us a reason, why we should add that suggestion!")
					.build();
			Modal modal = Modal.create("gamesuggmodal", "Game Suggestion")
					.addComponents(ActionRow.of(title), ActionRow.of(desc), ActionRow.of(whyToAdd))
					.build();
			event.replyModal(modal).queue();
		}else if(event.getComponentId().equals("botsugg")) {
			TextInput title = TextInput.create("bottitle", "Title", TextInputStyle.SHORT)
					.setRequiredRange(10, 100)
					.build();
			TextInput desc = TextInput.create("botdesc", "Description", TextInputStyle.PARAGRAPH)
					.setRequiredRange(50, 1000)
					.setPlaceholder("Explain your suggestion as good as you can. Upload Pictures in it's created thread afterward.")
					.build();
			TextInput whyToAdd = TextInput.create("botwta", "Why should we add it?", TextInputStyle.PARAGRAPH)
					.setRequiredRange(50, 1000)
					.setPlaceholder("Give us a reason, why we should add that suggestion!")
					.build();
			Modal modal = Modal.create("botsuggmodal", "Bot Suggestion")
					.addComponents(ActionRow.of(title), ActionRow.of(desc), ActionRow.of(whyToAdd))
					.build();
			event.replyModal(modal).queue();
		}else if(event.getComponentId().equals("websugg")) {
			TextInput title = TextInput.create("webtitle", "Title", TextInputStyle.SHORT)
					.setRequiredRange(10, 100)
					.build();
			TextInput desc = TextInput.create("webdesc", "Description", TextInputStyle.PARAGRAPH)
					.setRequiredRange(50, 1000)
					.setPlaceholder("Explain your suggestion as good as you can. Upload Pictures in it's created thread afterward.")
					.build();
			TextInput whyToAdd = TextInput.create("webwta", "Why should we add it?", TextInputStyle.PARAGRAPH)
					.setRequiredRange(50, 1000)
					.setPlaceholder("Give us a reason, why we should add that suggestion!")
					.build();
			Modal modal = Modal.create("websuggmodal", "Website Suggestion")
					.addComponents(ActionRow.of(title), ActionRow.of(desc), ActionRow.of(whyToAdd))
					.build();
			event.replyModal(modal).queue();
		}else if(event.getComponentId().equals("miscsugg")) {
			TextInput title = TextInput.create("misctitle", "Title", TextInputStyle.SHORT)
					.setRequiredRange(10, 100)
					.build();
			TextInput desc = TextInput.create("miscdesc", "Description", TextInputStyle.PARAGRAPH)
					.setRequiredRange(50, 1000)
					.setPlaceholder("Explain your suggestion as good as you can. Upload Pictures in it's created thread afterward.")
					.build();
			TextInput whyToAdd = TextInput.create("miscwta", "Why should we add it?", TextInputStyle.PARAGRAPH)
					.setRequiredRange(50, 1000)
					.setPlaceholder("Give us a reason, why we should add that suggestion!")
					.build();
			Modal modal = Modal.create("miscsuggmodal", "Other Suggestion")
					.addComponents(ActionRow.of(title), ActionRow.of(desc), ActionRow.of(whyToAdd))
					.build();
			event.replyModal(modal).queue();
		}else if(event.getComponentId().equals("suggaccept")) {
			long msgId = event.getMessageIdLong();
			Role upperStaff = guild.getRoleById(1203440790081380443l);
			if(isSuggestionMessage(msgId)) {
				if(event.getMember().getRoles().contains(upperStaff)) {
					MessageEmbed me = event.getMessage().getEmbeds().get(0);
					EmbedBuilder eb = new EmbedBuilder(me);
					eb.addField("This Suggestion has been accepted!", "Thanks for discussing and voting!", false);
					event.getMessage().editMessageEmbeds(eb.build()).queue();
					event.getMessage().getActionRows().forEach(ar -> {
						ar.getButtons().forEach(ar1 -> {
							ar1.asDisabled();
						});
					});
					event.deferReply(true).addContent("The suggestion has been marked as accepted.").queue();
				}else {
					event.deferReply(true).addContent("Hey, it seems you are lacking the permission to mark suggestions as accepted.").queue();
				}
			}else {
				event.deferReply(true).addContent("An error occured whilst pressing Accept!").queue();
			}
		}else if(event.getComponentId().equalsIgnoreCase("")) {
			
		}
	}
	
	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		TextChannel target = null;
		if(event.getModalId().equals("gamesuggmodal")) {
			try {
				YamlFile cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
				target = guild.getTextChannelById(cfg.getLong("Suggestion.PostChannel"));
			} catch (IOException e) {
				e.printStackTrace(); 
			}
			if(target != null) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.decode("#fe779a"));
				eb.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
				eb.setTitle("Game Suggestion");
				eb.setFooter("Suggestion System by Lotus Gaming", guild.getIconUrl());
				eb.addField("Suggestion Title", event.getValue("gametitle").getAsString(), true);
				eb.addField("Suggestion Description", event.getValue("gamedesc").getAsString(), false);
				eb.addField("Why we should add this", event.getValue("gamewta").getAsString(), false);
				target.sendMessageEmbeds(eb.build()).addActionRow(
						Button.primary("suggaccept", "Accept").withEmoji(Emoji.fromFormatted("<:accept:1204482009355911168>")),
						Button.danger("suggreject", "Reject").withEmoji(Emoji.fromFormatted("<:deny:1204482005065146428>")),
						Button.danger("suggLockClose", "Close").withEmoji(Emoji.fromFormatted("U+1F512"))
						).queue(ra -> {
					ra.addReaction(Emoji.fromFormatted("<:plus:1204436968268767262>")).queue();
					ra.addReaction(Emoji.fromFormatted("<:minus:1204436966276603934>")).queue();
					long messageId = ra.getIdLong();
					ra.createThreadChannel(event.getValue("gametitle").getAsString()).queue(ra1 -> {
						addSuggestion(member.getIdLong(), ra1.getIdLong(), messageId, event.getValue("gametitle").getAsString(), event.getValue("gamedesc").getAsString(), event.getValue("gamewta").getAsString(), "Game Suggestion");
					});
				});
				event.deferReply(true).addContent("Thank you for your suggestion.").queue();
			}else {
				event.deferReply(true).addContent("It seems there is an error! Please contact the Server Administrator via Ticket!").queue();
			}
		}else if(event.getModalId().equals("botsuggmodal")) {
			try {
				YamlFile cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
				target = guild.getTextChannelById(cfg.getLong("Suggestion.PostChannel"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(target != null) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.decode("#fe779a"));
				eb.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
				eb.setTitle("Bot Suggestion");
				eb.setFooter("Suggestion System by Lotus Gaming", guild.getIconUrl());
				eb.addField("Suggestion Title", event.getValue("bottitle").getAsString(), true);
				eb.addField("Suggestion Description", event.getValue("botdesc").getAsString(), false);
				eb.addField("Why we should add this", event.getValue("botwta").getAsString(), false);
				target.sendMessageEmbeds(eb.build()).addActionRow(
						Button.primary("suggaccept", "Accept").withEmoji(Emoji.fromFormatted("<:accept:1204482009355911168>")),
						Button.danger("suggreject", "Reject").withEmoji(Emoji.fromFormatted("<:deny:1204482005065146428>")),
						Button.danger("suggLockClose", "Close").withEmoji(Emoji.fromFormatted("U+1F512"))
						).queue(ra -> {
					ra.addReaction(Emoji.fromFormatted("<:plus:1204436968268767262>")).queue();
					ra.addReaction(Emoji.fromFormatted("<:minus:1204436966276603934>")).queue();
					long messageId = ra.getIdLong();
					ra.createThreadChannel(event.getValue("bottitle").getAsString()).queue(ra1 -> {
						addSuggestion(member.getIdLong(), ra1.getIdLong(), messageId, event.getValue("bottitle").getAsString(), event.getValue("botdesc").getAsString(), event.getValue("botwta").getAsString(), "Bot Suggestion");
					});
				});
				event.deferReply(true).addContent("Thank you for your suggestion.").queue();
			}else {
				event.deferReply(true).addContent("It seems there is an error! Please contact the Server Administrator via Ticket!").queue();
			}
		}else if(event.getModalId().equals("websuggmodal")) {
			try {
				YamlFile cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
				target = guild.getTextChannelById(cfg.getLong("Suggestion.PostChannel"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(target != null) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.decode("#fe779a"));
				eb.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
				eb.setTitle("Website Suggestion");
				eb.setFooter("Suggestion System by Lotus Gaming", guild.getIconUrl());
				eb.addField("Suggestion Title", event.getValue("webtitle").getAsString(), true);
				eb.addField("Suggestion Description", event.getValue("webdesc").getAsString(), false);
				eb.addField("Why we should add this", event.getValue("webwta").getAsString(), false);
				target.sendMessageEmbeds(eb.build()).addActionRow(
						Button.primary("suggaccept", "Accept").withEmoji(Emoji.fromFormatted("<:accept:1204482009355911168>")),
						Button.danger("suggreject", "Reject").withEmoji(Emoji.fromFormatted("<:deny:1204482005065146428>")),
						Button.danger("suggLockClose", "Close").withEmoji(Emoji.fromFormatted("U+1F512"))
						).queue(ra -> {
					ra.addReaction(Emoji.fromFormatted("<:plus:1204436968268767262>")).queue();
					ra.addReaction(Emoji.fromFormatted("<:minus:1204436966276603934>")).queue();
					long messageId = ra.getIdLong();
					ra.createThreadChannel(event.getValue("webtitle").getAsString()).queue(ra1 -> {
						addSuggestion(member.getIdLong(), ra1.getIdLong(), messageId, event.getValue("webtitle").getAsString(), event.getValue("webdesc").getAsString(), event.getValue("webwta").getAsString(), "Website Suggestion");
					});
				});
				event.deferReply(true).addContent("Thank you for your suggestion.").queue();
			}else {
				event.deferReply(true).addContent("It seems there is an error! Please contact the Server Administrator via Ticket!").queue();
			}
		}else if(event.getModalId().equals("miscsuggmodal")) {
			try {
				YamlFile cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
				target = guild.getTextChannelById(cfg.getLong("Suggestion.PostChannel"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(target != null) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.decode("#fe779a"));
				eb.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
				eb.setTitle("Other Suggestion");
				eb.setFooter("Suggestion System by Lotus Gaming", guild.getIconUrl());
				eb.addField("Suggestion Title", event.getValue("misctitle").getAsString(), true);
				eb.addField("Suggestion Description", event.getValue("miscdesc").getAsString(), false);
				eb.addField("Why we should add this", event.getValue("miscwta").getAsString(), false);
				target.sendMessageEmbeds(eb.build()).addActionRow(
						Button.primary("suggaccept", "Accept").withEmoji(Emoji.fromFormatted("<:accept:1204482009355911168>")),
						Button.danger("suggreject", "Reject").withEmoji(Emoji.fromFormatted("<:deny:1204482005065146428>")),
						Button.danger("suggLockClose", "Close").withEmoji(Emoji.fromFormatted("U+1F512"))
						).queue(ra -> {
					ra.addReaction(Emoji.fromFormatted("<:plus:1204436968268767262>")).queue();
					ra.addReaction(Emoji.fromFormatted("<:minus:1204436966276603934>")).queue();
					long messageId = ra.getIdLong();
					ra.createThreadChannel(event.getValue("misctitle").getAsString()).queue(ra1 -> {
						addSuggestion(member.getIdLong(), ra1.getIdLong(), messageId, event.getValue("misctitle").getAsString(), event.getValue("miscdesc").getAsString(), event.getValue("miscwta").getAsString(), "Other Suggestion");
					});
				});
				event.deferReply(true).addContent("Thank you for your suggestion.").queue();
			}else {
				event.deferReply(true).addContent("It seems there is an error! Please contact the Server Administrator via Ticket!").queue();
			}
		}
	}
	
	boolean isSuggestionMessage(long messageId) {
		boolean isSuggestionMessage = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT messageId FROM bot_s_suggestions WHERE messageId = ?");
			ps.setLong(1, messageId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				isSuggestionMessage = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuggestionMessage;
	}
	
	void addSuggestion(long creatorId, long threadId, long messageId, String suggestionTitle, String suggestionDescription, String suggestionWhyToAdd, String topic) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO bot_s_suggestions(creatorid,createdAt,threadId,suggTitle,suggDesc,suggWTA,topic, messageId) VALUES (?,?,?,?,?,?,?,?)");
			ps.setLong(1, creatorId);
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, threadId);
			ps.setString(4, suggestionTitle);
			ps.setString(5, suggestionDescription);
			ps.setString(6, suggestionWhyToAdd);
			ps.setString(7, topic);
			ps.setLong(8, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}