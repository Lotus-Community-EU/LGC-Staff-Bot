//Created by Christopher at 17.07.2024
package eu.lotusgaming.bot.handlers.modlog.category;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import eu.lotusgaming.bot.main.LotusManager;
import eu.lotusgaming.bot.misc.MySQL;
import eu.lotusgaming.bot.misc.TextCryptor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageLogging extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		User user = event.getAuthor();
		if(event.isFromGuild()) {
			Guild guild = event.getGuild();
			if(!user.isBot() && !event.isWebhookMessage()) {
				String message = TextCryptor.encrypt(event.getMessage().getContentRaw(), getPassword());
				insertInDB(guild.getIdLong(), event.getMessageIdLong(), user.getIdLong(), message, user.isBot());
			}
		}
	}
	
	@Override
	public void onMessageUpdate(MessageUpdateEvent event) {
		User user = event.getAuthor();
		if(event.isFromGuild()) {
			Guild guild = event.getGuild();
			String originalText = TextCryptor.decrypt(returnMessage(guild.getIdLong(), event.getMessageIdLong(), TextType.OriginalText), getPassword());
			String lastUpdatedText = TextCryptor.decrypt(returnMessage(guild.getIdLong(), event.getMessageIdLong(), TextType.LastUpdatedText), getPassword());
			EmbedBuilder eb = ModlogController.baseEmbed(guild);
			eb.setTitle("Message updated.");
			eb.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
			eb.setDescription("Channel: " + event.getChannel().getAsMention() + "\n[Message Jump URL](" + event.getJumpUrl() + ")");
			if(originalText.length() >= 750) {
				eb.addField("Original Message:", originalText.substring(0, 750) + " ", false);
			}else {
				eb.addField("Original Message:", originalText + " ", false);
			}
			
		}
	}
	
	private void insertInDB(long guildId, long messageId, long memberId, String message, boolean isBot) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO bot_s_messagelog(guildId, messageId, originalText, lastUpdatedText, originalInsertedTime, lastUpdatedTime, isBot, memberId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setLong(1, guildId);
			ps.setLong(2, messageId);
			ps.setString(3, message);
			ps.setString(4, message);
			ps.setLong(5, System.currentTimeMillis());
			ps.setLong(6, System.currentTimeMillis());
			ps.setBoolean(7, isBot);
			ps.setLong(8, memberId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String returnMessage(long guildId, long messageId, TextType type) {
		String toReturn = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_messagelog WHERE guildId = ? AND messageId = ?");
			ps.setLong(1, guildId);
			ps.setLong(2, messageId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				if(type == TextType.LastUpdatedText) {
					toReturn = rs.getString("lastUpdatedText");
				}else if(type == TextType.OriginalText) {
					toReturn = rs.getString("originalText");
				}
			}else {
				toReturn = "Message is not in DB saved!";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	private char[] getPassword() {
		YamlFile cfg = new YamlFile(LotusManager.mainConfig);
		try {
			cfg.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cfg.getString("Bot.HashPassword").toCharArray();
	}
	
	private enum TextType {
		OriginalText,
		LastUpdatedText;
	}
}