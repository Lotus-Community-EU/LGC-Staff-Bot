//Created by Chris Wille at 21.02.2024
package eu.lotusgaming.bot.command;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.lotusgaming.bot.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PunishmentsCommands extends ListenerAdapter {
	
	static long nextPunishmentId = 0;
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		Member issuer = event.getMember();
		if(event.getName().equals("ban")) {
			
		}else if(event.getName().equals("mute")) {
			
		}else if(event.getName().equals("kick")) {
			User target = event.getOption("user").getAsUser();
			String reason = event.getOption("reason").getAsString();
			if(guild.isMember(target)) {
				Member targetMember = guild.getMember(target);
				if(targetMember.isOwner()) {
					event.reply("You can't kick the Owner of the server!").queue();
				}else {
					if(targetMember.getRoles().get(0).getPosition() > issuer.getRoles().get(0).getPosition()) {
						event.reply("You can't kick someone being above you in role hierarchy.").queue();
					}else {
						event.deferReply(true).addContent("Warned " + target.getEffectiveName() + " for " + reason).queue();
						addKick(issuer, target, guild, reason);
						sendModlogInfo(issuer, target, guild, reason, event.getMessageChannel().getAsMention(), "Kick");
					}
				}
			}
		}else if(event.getName().equals("warn")) {
			User target = event.getOption("user").getAsUser();
			String reason = event.getOption("reason").getAsString();
			if(guild.isMember(target)) {
				Member targetMember = guild.getMember(target);
				if(targetMember.isOwner()) {
					event.reply("You can't warn the Owner of the server!").queue();
				}else {
					if(targetMember.getRoles().get(0).getPosition() > issuer.getRoles().get(0).getPosition()) {
						event.reply("You can't warn someone being above you in role hierarchy.").queue();
					}else {
						event.deferReply(true).addContent("Warned " + target.getEffectiveName() + " for " + reason).queue();
						addWarn(issuer, target, guild, reason);
						sendModlogInfo(issuer, target, guild, reason, event.getMessageChannel().getAsMention(), "Warn");
					}
				}
			}
		}
	}
	
	void addWarn(Member issuer, User receiver, Guild guild, String reason){
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO bot_s_punishments(issuerId,createdAt,receiverId,reason,punishmentType) VALUES (?,?,?,?,?)");
			ps.setLong(1, issuer.getIdLong());
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, receiver.getIdLong());
			ps.setString(4, reason);
			ps.setString(5, "Warn");
			ps.executeUpdate();
			receiver.openPrivateChannel().queue(ra -> {
				ra.sendMessage("You were warned in " + guild.getName() + " for: " + reason).queue();
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void addKick(Member issuer, User receiver, Guild guild, String reason) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO bot_s_punishments(issuerId,createdAt,receiverId,reason,punishmentType) VALUES (?,?,?,?,?)");
			ps.setLong(1, issuer.getIdLong());
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, receiver.getIdLong());
			ps.setString(4, reason);
			ps.setString(5, "Kick");
			ps.executeUpdate();
			receiver.openPrivateChannel().queue(ra -> {
				ra.sendMessage("You were kicked from " + guild.getName() + " for: " + reason).queue();
			});
			guild.kick(receiver).reason(reason).queue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void sendModlogInfo(Member issuer, User receiver, Guild guild, String reason, String channelMention, String type) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor("Case " + nextPunishmentId + " | " + type + " | " + receiver.getEffectiveName(), null, guild.getIconUrl());
		loadLastPunishmentId();
		eb.setDescription("Issuer: " + issuer.getAsMention() + " / " + issuer.getEffectiveName() + "\nReceiver: " + receiver.getAsMention() + " / " + receiver.getEffectiveName() + "\nChannel: " + channelMention + "\nReason: " + reason);
		eb.setColor(Color.yellow);
		guild.getTextChannelById(1201222774107148428l).sendMessageEmbeds(eb.build()).queue();
	}
	
	public static void loadLastPunishmentId() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT id FROM bot_s_punishments ORDER BY id DESC");
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				nextPunishmentId = (rs.getInt("id") + 1);
			}else {
				nextPunishmentId = 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}