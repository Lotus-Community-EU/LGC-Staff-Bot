//Created by Christopher at 17.07.2024
package eu.lotusgaming.bot.handlers.modlog.category;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import eu.lotusgaming.bot.misc.VCDuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildEvents extends ListenerAdapter{
	
	/*
	 * Following Events will be added: *GuildBanEvent, *GuildUnbanEvent, *GuildMemberRemoveEvent,
	 * * GuildUpdateIconEvent, *GuildUpdateNameEvent, *GuildMemberJoinEvent, *GuildMemberRoleAddEvent,
	 * * GuildMemberRoleRemoveEvent, * GuildMemberUpdateNicknameEvent, * GuildMemberUpdateAvatarEvent,
	 * * GuildVoiceJoinEvent, *GuildVoiceLeaveEvent, *GuildVoiceMoveEvent
	 */
	
	static List<User> checkList = new ArrayList<>();
	
	@Override
	public void onGuildBan(GuildBanEvent event) {
		User user = event.getUser();
		Guild guild = event.getGuild();
		
		checkList.add(user);
		
		Executors.newScheduledThreadPool(1).schedule(() -> {
			if(checkList.add(user)) {
				EmbedBuilder eb = ModlogController.baseEmbed(guild);
				eb.setColor(ModlogController.red);
				eb.setTitle("Member has been banned.");
				eb.setThumbnail(user.getEffectiveAvatarUrl());
				eb.setDescription(user.getName() + " has been banned from " + guild.getName() + ".");
				ModlogController.sendMessage(eb, guild);
			}
		}, 500, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void onGuildUnban(GuildUnbanEvent event) {
		User user = event.getUser();
		Guild guild = event.getGuild();
		
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setColor(ModlogController.yellow);
		eb.setTitle("Member has been unbanned");
		eb.setThumbnail(user.getEffectiveAvatarUrl());
		eb.setDescription(user.getName() + " has been unbanned from " + guild.getName());
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		User user = event.getUser();
		Guild guild = event.getGuild();
		
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setThumbnail(user.getEffectiveAvatarUrl());
		StringBuilder sb = new StringBuilder();
		for(Role role : event.getMember().getRoles()) {
			sb.append(role.getName());
			sb.append(" ");
		}
		int roleCount = event.getMember().getRoles().size();
		String roles = "";
		if(roleCount != 0) {
			roles = sb.toString().substring(0, sb.toString().length() - 1);
		}else {
			roles = "none";
		}
		
		eb.addField("Roles (" + roleCount + ")", "The user has these roles: " + roles, false);
		eb.addField("Joined " + guild.getName(), Objects.requireNonNullElse(ModlogController.odtToString(event.getMember().getTimeJoined(), "dd/MM/yyyy - HH:mm:ss"), "not cached"), false);
		eb.setColor(ModlogController.red);
		if(checkList.contains(user)) {
			eb.setTitle("Member has been banned and left.");
			eb.setDescription(user.getName() + " has been banned from " + guild.getName() + " and has left the guild.");
		}else {
			eb.setTitle("Member left");
			eb.setDescription(user.getName() + " has left the guild.");
		}
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildUpdateIcon(GuildUpdateIconEvent event) {
		Guild guild = event.getGuild();
		
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Guild has a new icon.", event.getNewIconUrl());
		eb.setImage(event.getNewIconUrl());
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildUpdateName(GuildUpdateNameEvent event) {
		Guild guild = event.getGuild();
		
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Guild has a new name.");
		eb.setDescription("Old Name: " + event.getOldName() + "\nNew Name: " + event.getNewName());
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Member joined the Guild.");
		eb.setColor(ModlogController.green);
		eb.setThumbnail(member.getEffectiveAvatarUrl());
		if(event.getUser().isBot()) {
			eb.setDescription(member.getAsMention() + " has joined " + guild.getName() + ".\n \nAccount Creation: " + ModlogController.odtToString(member.getTimeCreated(), "dd.MM.yyyy - HH:mm:ss") + "\n" + member.getEffectiveName() + " is a bot account.");
		}else {
			eb.setDescription(member.getAsMention() + " has joined " + guild.getName() + ".\n \nAccount Creation: " + ModlogController.odtToString(member.getTimeCreated(), "dd.MM.yyyy - HH:mm:ss") + "\n" + member.getEffectiveName() + " is an user account.");
		}
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Added Role to Member");
		eb.setColor(ModlogController.yellow);
		if(event.getRoles().size() == 1) {
			eb.setDescription(member.getAsMention() + " has been assigned the role " + event.getRoles().get(0).getAsMention());
		}else {
			StringBuilder sb = new StringBuilder();
			for(Role role : event.getRoles()) {
				sb.append(role.getAsMention());
				sb.append(" ");
			}
			String roles = sb.toString().substring(0, sb.toString().length() - 1);
			eb.setDescription(member.getAsMention() + " has been assigned the roles " + roles);
		}
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Removed Role from Member");
		eb.setColor(ModlogController.yellow);
		if(event.getRoles().size() == 1) {
			eb.setDescription(member.getAsMention() + " has been removed from the role " + event.getRoles().get(0).getAsMention());
		}else {
			StringBuilder sb = new StringBuilder();
			for(Role role : event.getRoles()) {
				sb.append(role.getAsMention());
				sb.append(" ");
			}
			String roles = sb.toString().substring(0, sb.toString().length() - 1);
			eb.setDescription(member.getAsMention() + " has been removed the roles " + roles);
		}
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Member updated Nickname");
		eb.setColor(ModlogController.yellow);
		String oldNick, newNick;
		if(event.getOldNickname() == null) {
			oldNick = "no Nick";
		}else {
			oldNick = event.getOldNickname();
		}
		if(event.getNewNickname() == null) {
			newNick = "no Nick";
		}else {
			newNick = event.getNewNickname();
		}
		eb.setDescription("Member: " + member.getAsMention() + " \nOld Nickname: " + oldNick + "\nNew Nickname: " + newNick);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildMemberUpdateAvatar(GuildMemberUpdateAvatarEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setDescription(member.getAsMention() + " updated their Guildspecific Avatar");
		if(event.getNewAvatarUrl() != null) {
			eb.setImage(event.getNewAvatarUrl());
		}else {
			eb.setImage(member.getDefaultAvatarUrl());
		}
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		if(event.getChannelJoined() != null && event.getChannelLeft() == null) {
			//Join Event
			eb.setDescription(member.getAsMention() + " has joined " + event.getChannelJoined().getAsMention());
			eb.setColor(ModlogController.green);
			ModlogController.addMember(guild.getIdLong(), event.getChannelJoined().getIdLong(), member.getIdLong());
		}
		if(event.getChannelLeft() != null) {
			//Leave Event
			VCDuration vcd = ModlogController.removeMember(guild.getIdLong(), event.getChannelLeft().getIdLong(), member.getIdLong());
			
			long joinTime = vcd.getTimeJoin();
			long actualTime = (System.currentTimeMillis() / 1000) - joinTime;
			long hours = actualTime / 3600;
			long minutes = (actualTime % 3600) / 60;
			long seconds = actualTime % 60;
			String formattedTime = String.format("%02dh %02dm %02ds", hours, minutes, seconds);
			
			eb.setDescription(member.getAsMention() + " has left " + event.getChannelLeft().getAsMention() + "\nJoined at " + timestampToHRT(joinTime) + "\nCall Duration: " + formattedTime);
			eb.setColor(ModlogController.red);
		}
		if(event.getChannelJoined() != null && event.getChannelLeft() != null) {
			//Moved Event
			eb.setTitle(member.getEffectiveName() + " has moved channels");
			eb.setColor(ModlogController.yellow);
			eb.setDescription("The member moved from " + event.getChannelLeft().getAsMention() + " to " + event.getChannelJoined().getAsMention());
		}
		ModlogController.sendMessage(eb, guild);
	}
	
	String timestampToHRT(long input) {
		input = input * 1000;
		return new SimpleDateFormat("dd.MM - HH:mm:ss").format(new Date(input));
	}
}