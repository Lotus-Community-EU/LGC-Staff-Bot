//Created by Christopher at 16.07.2024
package eu.lotusgaming.bot.handlers.modlog;

import java.awt.Color;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;

import eu.lotusgaming.bot.handlers.modlog.category.EmojiStickerEvent;
import eu.lotusgaming.bot.handlers.modlog.category.GuildEvents;
import eu.lotusgaming.bot.handlers.modlog.category.MessageLogging;
import eu.lotusgaming.bot.handlers.modlog.category.RoleEvents;
import eu.lotusgaming.bot.handlers.modlog.category.UserEvents;
import eu.lotusgaming.bot.misc.MySQL;
import eu.lotusgaming.bot.misc.VCDuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

public class ModlogController {
	
	public static Color red = Color.decode("#c91a1a");
	public static Color yellow = Color.decode("#fce303");
	public static Color green = Color.decode("#25c235");
	
	public static void registerClasses(JDA jda) {
		//TimerTask for Class:DatabaseMessageTimer
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new DatabaseMessageTimer(), 0, 3600000);
		
		//Register Classes for Modlogs
		jda.addEventListener(new UserEvents());
		jda.addEventListener(new GuildEvents());
		jda.addEventListener(new MessageLogging());
		jda.addEventListener(new RoleEvents());
		jda.addEventListener(new EmojiStickerEvent());
	}
	
	public static String odtToString(OffsetDateTime odt, String pattern) {
		return odt.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static String dateToString(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	static long getTextChannelByGuildId(long guildId) {
		if(guildId == 1153419306789507125l) { //main guild
			return 1201222774107148428l;
		}else if(guildId == 1066812641768640542l) { //staff guild
			return 1211661756477214750l;
		}else { 
			return 0;
		}
	}
	
	public static void sendMessage(EmbedBuilder eb, Guild guild) {
		TextChannel channel = guild.getTextChannelById(getTextChannelByGuildId(guild.getIdLong()));
		channel.sendMessageEmbeds(eb.build()).queue();
	}
	
	public static void sendMessageWithFile(EmbedBuilder eb, Guild guild, File file, String filename) {
		TextChannel channel = guild.getTextChannelById(getTextChannelByGuildId(guild.getIdLong()));
		channel.sendMessageEmbeds(eb.build())
		.addFiles(FileUpload.fromData(file, filename)).queue();
	}
	
	public static EmbedBuilder baseEmbed(Guild guild) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setFooter(guild.getName() + " ● " + dateToString(new Date(), "dd.MM.yy - HH:mm"), guild.getIconUrl());
		return eb;
	}
	
	public static void addMember(long guildId, long channelId, long memberId) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO bot_s_voicedurations(guildId,memberId,channelId,timeJoined,timeLastMoved) VALUES (?,?,?,?,?)");
			ps.setLong(1, guildId);
			ps.setLong(2, memberId);
			ps.setLong(3, channelId);
			ps.setLong(4, System.currentTimeMillis());
			ps.setLong(5, 0);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void moveMember(long guildId, long memberId) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE bot_s_voicedurations SET timeLastMoved = ? WHERE guildId = ? AND memberId = ?");
			ps.setLong(1, System.currentTimeMillis());
			ps.setLong(2, guildId);
			ps.setLong(3, memberId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static VCDuration removeMember(long guildId, long memberId) {
		VCDuration vcd = null;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_voicedurations WHERE guildId = ? AND memberId = ?");
			ps.setLong(1, guildId);
			ps.setLong(2, memberId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				long guildID = rs.getLong("guildId");
				long channelId = rs.getLong("channelId");
				long memberID = rs.getLong("memberId");
				long timeJoin = rs.getLong("timeJoined");
				long timeLastMoved = rs.getLong("timeLastMoved");
				vcd = new VCDuration(guildID, memberID, channelId, timeJoin, timeLastMoved);
				deleteMember(guildID, memberID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vcd;
	}
	
	private static void deleteMember(long guildId, long memberId) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM bot_s_voicedurations WHERE guildId = ? AND memberId = ?");
			ps.setLong(1, guildId);
			ps.setLong(2, memberId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}