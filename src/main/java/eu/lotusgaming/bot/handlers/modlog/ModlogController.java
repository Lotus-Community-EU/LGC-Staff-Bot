//Created by Christopher at 16.07.2024
package eu.lotusgaming.bot.handlers.modlog;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import eu.lotusgaming.bot.handlers.modlog.category.GuildEvents;
import eu.lotusgaming.bot.handlers.modlog.category.UserEvents;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ModlogController {
	
	public static Color red = Color.decode("#c91a1a");
	public static Color yellow = Color.decode("#fce303");
	public static Color green = Color.decode("#25c235");
	
	public static void registerClasses(JDA jda) {
		jda.addEventListener(new UserEvents());
		jda.addEventListener(new GuildEvents());
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
	
	public static EmbedBuilder baseEmbed(Guild guild) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor("Beta - Errors may appear!");
		eb.setFooter(guild.getName() + " ● " + dateToString(new Date(), "dd.MM.yy - HH:mm"), guild.getIconUrl());
		return eb;
	}

}

