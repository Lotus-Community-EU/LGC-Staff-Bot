//Created by Maurice H. at 20.04.2025
package eu.lotusgaming.bot.command;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import eu.lotusgaming.bot.misc.MiscUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserCommands extends ListenerAdapter{
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		String command = event.getName();
		if(command.equals("whois")) {
			event.deferReply().queue();
			Member targetMember = null;
			if(event.getOption("user") != null) {
				targetMember = event.getOption("user").getAsMember();
			}else {
				targetMember = member;
			}
			List<String> list = new ArrayList<>();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(member.getColor());
			if(targetMember.getUser().getDiscriminator().equalsIgnoreCase("0000")) {
				eb.setAuthor(targetMember.getUser().getName(), targetMember.getEffectiveAvatarUrl());
			}else {
				eb.setAuthor(targetMember.getUser().getName() + "#" + targetMember.getUser().getDiscriminator(), targetMember.getEffectiveAvatarUrl());
			}
			eb.addField("Dates", "Joined Discord: " + MiscUtils.retDate(targetMember.getUser().getTimeCreated(), "dd.MM.yy - HH:mm") + "\nJoined " + guild.getName() + ": " + MiscUtils.retDate(targetMember.getTimeJoined(), "dd.MM.yy - HH:mm"), false);
			
			eb.setThumbnail(targetMember.getEffectiveAvatarUrl());
			if(targetMember.isOwner()) {
				list.add("Guildowner: yes");
			}else {
				list.add("Guildowner: no");
			}
			if(targetMember.isTimedOut()) {
				list.add("Timed out: yes");
				list.add("Timed out until: " + MiscUtils.retDate(targetMember.getTimeOutEnd(), "dd.MM.yy - HH:mm:ss"));
			}else {
				list.add("Timed out: no");
			}
			if(targetMember.getActivities().size() != 0) {
				StringBuilder sb = new StringBuilder();
				for(Activity activity : targetMember.getActivities()) {
					sb.append(activity.getName());
					sb.append(", ");
				}
				String result = sb.toString().substring(0, sb.toString().length() - 2);
				list.add("Activities: " + result);
			}
			if(targetMember.getVoiceState().getChannel() != null) {
				list.add("Chilling in " + targetMember.getVoiceState().getChannel().getAsMention());
			}
			if(targetMember.isBoosting()) {
				list.add("Boosts the server: yes, since " + MiscUtils.retDate(targetMember.getTimeBoosted(), "dd.MM.yy"));
			}else {
				list.add("Boosts the server: no");
			}
			if(targetMember.getOnlineStatus() == OnlineStatus.ONLINE) {
				list.add("Online status: ``online`` <:online:1221278970046185553>");
			}else if(targetMember.getOnlineStatus() == OnlineStatus.IDLE) {
				list.add("Online status: ``idle`` <:idle:1221278972051062856>");
			}else if(targetMember.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB) {
				list.add("Online status: ``do not disturb`` <:dnd:1221278973074346045>");
			}else if(targetMember.getOnlineStatus() == OnlineStatus.OFFLINE) {
				list.add("Online status: ``offline`` <:offline:1221279431809699950>");
			}
			StringBuilder sbI = new StringBuilder();
			for(String s : list) {
				sbI.append(s);
				sbI.append("\n");
			}
			eb.setDescription(sbI.toString());
			List<Role> roles = targetMember.getRoles();
			StringBuilder sb = new StringBuilder();
			for(Role rs : roles) {
				sb.append(rs.getAsMention());
				sb.append(" ");
			}
			if(roles.size() <= 17) {
				eb.addField("Roles (" + roles.size() + ")", sb.toString(), true);
			}else {
				eb.addField("Roles (" + roles.size() + ")", "Too many roles to list.", true);
			}
			event.getHook().sendMessageEmbeds(eb.build()).queue();
		}else if(command.equals("guildinfo")) {
			Member guildOwner = guild.getOwner();
			Role staff = guild.getRoleById(1203440776760266822L);
			int categories, textchannels, voicechannels, stagechannels, forumChannels, newsChannels, emojis, stickers, members1 = 0, bots = 0, staffs = 0, onlineTotal = 0, onlineMembers = 0, idleMembers = 0, dndMembers = 0, offlineMembers = 0, booster = 0, nbooster = 0;
			categories = guild.getCategories().size();
			textchannels = guild.getTextChannels().size();
			voicechannels = guild.getVoiceChannels().size();
			stagechannels = guild.getStageChannels().size();
			forumChannels = guild.getForumChannels().size();
			newsChannels = guild.getNewsChannels().size();
			String verificationLevel = guild.getVerificationLevel().toString();
			emojis = guild.getEmojis().size();
			stickers = guild.getStickers().size();
			nbooster = guild.getBoosters().size();
			booster = guild.getBoostCount();
			List<Role> roles = guild.getRoles();
			StringBuilder sbRole = new StringBuilder();
			for(Role role : roles) {
				sbRole.append(role.getAsMention()).append(" ");
			}
			String rolesString = sbRole.toString();
			String guildCreated = guild.getTimeCreated().format(DateTimeFormatter.ofPattern("dd.MM.yy - HH:mm"));
			for(Member members : guild.getMembers()) {
				if(members.getUser().isBot()) {
					bots++;
				}else {
					members1++;
					if(members.getOnlineStatus() == OnlineStatus.ONLINE) {
                        onlineMembers++;
					}else if(members.getOnlineStatus() == OnlineStatus.IDLE) {
						idleMembers++;
					}else if(members.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB) {
						dndMembers++;
					}else if(members.getOnlineStatus() == OnlineStatus.OFFLINE) {
						offlineMembers++;
					}
					if(members.getRoles().contains(staff)) {
						staffs++;
					}
				}
				if(members.getOnlineStatus() != OnlineStatus.OFFLINE) {
					onlineTotal++;
				}
			}
			List<String> memberStatus = new ArrayList<>();
			memberStatus.add("Online: " + onlineMembers);
			memberStatus.add("Idle: " + idleMembers);
			memberStatus.add("Do not Disturb: " + dndMembers);
			memberStatus.add("Offline: " + offlineMembers);
			memberStatus.add("Online Total: " + onlineTotal);
			memberStatus.add("Total: " + members1);
			memberStatus.add("Bots: " + bots);
			memberStatus.add("Staffs: " + staffs);
			memberStatus.add(booster + " Nitro Boosts from " + nbooster + " Members");
			
			List<String> channelStatus = new ArrayList<>();
			channelStatus.add("Categories: " + categories);
			channelStatus.add("Text Channels: " + textchannels);
			channelStatus.add("Voice Channels: " + voicechannels);
			channelStatus.add("Stage Channels: " + stagechannels);
			channelStatus.add("Forum Channels: " + forumChannels);
			channelStatus.add("News Channels: " + newsChannels);
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(ModlogController.green);
			eb.setTitle("Guildinfo for " + guild.getName());
			eb.setThumbnail(guild.getIconUrl());
			eb.setDescription("Displays all discord-guild relevant information\n \nVerification Level: " + verificationLevel + "\nGuild Created: " + guildCreated);
			eb.addField("Serverowner", guildOwner.getAsMention(), false);
			eb.addField("Member Status", transformList(memberStatus), false);
			eb.addField("Channels", transformList(channelStatus), false);
			eb.addField("Expressions", "Emojis: " + emojis + "\nStickers: " + stickers, false);
			if(roles.size() > 32) {
				eb.addField("Roles (" + roles.size() + ")", "Too many roles to list.", false);
			}else {
				eb.addField("Roles (" + roles.size() + ")", rolesString, false);
			}
			event.replyEmbeds(eb.build()).queue();
		}
	}
	
	String transformList(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for(String s : list) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}
}