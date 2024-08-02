//Created by Christopher at 17.07.2024
package eu.lotusgaming.bot.handlers.modlog.category;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import eu.lotusgaming.bot.main.LotusManager;
import eu.lotusgaming.bot.main.Main;
import eu.lotusgaming.bot.misc.MySQL;
import eu.lotusgaming.bot.misc.TextCryptor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
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
			eb.setColor(ModlogController.yellow);
			eb.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
			eb.setDescription("Channel: " + event.getChannel().getAsMention() + "\n[Message Jump URL](" + event.getJumpUrl() + ")");
			if(originalText.length() >= 750) {
				eb.addField("Original Message:", originalText.substring(0, 750) + " ", false);
			}else {
				eb.addField("Original Message:", originalText + " ", false);
			}
			updateLastUpdatedMessage(guild.getIdLong(), event.getMessageIdLong(), TextCryptor.encrypt(event.getMessage().getContentRaw(), getPassword()));
			if(!lastUpdatedText.equalsIgnoreCase(originalText)) {
				if(lastUpdatedText.length() >= 750) {
					eb.addField("Previously Edited Message:", lastUpdatedText.substring(0, 750) + " ", false);
				}else {
					eb.addField("Previously Edited Message:", lastUpdatedText + " ", false);
				}
			}
			if(event.getMessage().getContentDisplay().length() >= 750) {
				eb.addField("New Message:", event.getMessage().getContentDisplay().substring(0, 750) + " ", false);
			}else {
				eb.addField("New Message:", event.getMessage().getContentDisplay() + " ", false);
			}
			ModlogController.sendMessage(eb, guild);
		}
	}
	
	@Override
	public void onMessageDelete(MessageDeleteEvent event) {
		Guild guild = event.getGuild();
		if(event.isFromGuild()) {
			EmbedBuilder eb = ModlogController.baseEmbed(guild);
			User messageAuthor = returnUserFromMessage(event.getJDA(), guild.getIdLong(), event.getMessageIdLong());
			eb.setDescription("Channel: " + event.getChannel().getAsMention());
			eb.setColor(ModlogController.red);
			if(messageAuthor != null) {
				eb.setAuthor(messageAuthor.getName(), null, messageAuthor.getEffectiveAvatarUrl());
			}else {
				eb.setAuthor("Member not cached.", null, guild.getIconUrl());
			}
			String lastUpdatedText = TextCryptor.decrypt(returnMessage(guild.getIdLong(), event.getMessageIdLong(), TextType.LastUpdatedText), getPassword());
			if(lastUpdatedText.length() >= 1024) {
				eb.addField("Message: ", lastUpdatedText.substring(0, 1023), false);
			}else {
				eb.addField("Message: ", lastUpdatedText, false);
			}
			ModlogController.sendMessage(eb, guild);
			markMessageAsDeleted(guild.getIdLong(), event.getMessageIdLong());
		}
	}
	
	@Override
	public void onMessageBulkDelete(MessageBulkDeleteEvent event) {
		Guild guild = event.getGuild();
		List<String> messageIds = event.getMessageIds();
		for(String string : messageIds) {
			Main.logger.info("BULKDELETE: " + string);
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
	
	private void markMessageAsDeleted(long guildId, long messageId) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE bot_s_messagelog SET markedAsDeleted = ? WHERE guildId = ? AND messageId = ?");
			ps.setBoolean(1, true);
			ps.setLong(2, guildId);
			ps.setLong(3, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private User returnUserFromMessage(JDA jda, long guildId, long messageId) {
		User user = null;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT memberId FROM bot_s_messagelog WHERE guildId = ? AND messageId = ?");
			ps.setLong(1, guildId);
			ps.setLong(2, messageId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				long memberId = rs.getLong("memberId");
				user = jda.getUserById(memberId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
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
	
	private void updateLastUpdatedMessage(long guildId, long messageId, String obfuscatedText) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE bot_s_messagelog SET lastUpdatedText = ?, lastUpdatedTime = ? WHERE guildId = ? AND messageId = ?");
			ps.setString(1, obfuscatedText);
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, guildId);
			ps.setLong(4, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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