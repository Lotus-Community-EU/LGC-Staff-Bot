//Created by Chris Wille at 15.02.2024
package eu.lotusgaming.bot.command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SetInfoCommand extends ListenerAdapter{
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("setinfo")) {
			if(event.getOption("channel") != null) {
				if(event.getOption("channel").getAsChannel().getType() == ChannelType.TEXT) {
					TextChannel infoChannel = event.getOption("channel").getAsChannel().asTextChannel();
					event.reply("Infos are sent into " + infoChannel.getAsMention()).queue();
					infoChannel.sendMessage(transform(info_block())).queue();
					info_blockTeam(infoChannel, event.getGuild());
				}else {
					event.deferReply(true).addContent("The Channel must be a text channel!").queue();
				}
			}
		}
	}
	
	List<String> info_block() {
		List<String> info = new ArrayList<>();
		info.add("Question: How can I add my friends in here?\nAnswer: That's easy, just use this invite link: <https://discord.gg/7XZ2AR9A9z>");
		info.add("Question: Do you guys accept partnerships?\nAnswer: As of now, no, we don't. That might be a subject for later.\nStill want to give it a shot? Create a ticket in <#1203720766366027866> under the category **General Support**");
		info.add("Question: I want to apply! What do I have to do now?\nAnswer: Currently, we do recruit people, however we can do it only via ticket system. Create a ticket in <#1203720766366027866> under the category **General Support**");
		info.add("Question: Beta-Tester - I want to be one too!\nAnswer: Glad you'd like to help us, however we pick Beta Testers. There won't be a recruitment process. If you want, you can create a ticket in <#1203720766366027866> under the category **General Support** and elaborate, why we should pick you!");
		info.add("Question: What about social media?\nAnswer: We actually don't have social media yet. But it is planned. Stay tuned!");
		return info;
	}
	
	void info_blockTeam(TextChannel targetChannel, Guild guild) {
		List<Long> staffRoles = new ArrayList<>();
		staffRoles.add(1155572809356038154l); //project leader
		staffRoles.add(1155573840651485246l); //vice project leader
		staffRoles.add(1155573844199870574l); //staff manager
		staffRoles.add(1155573847270113321l); //human resources
		staffRoles.add(1155573850776535061l); //quality assurance manager
		staffRoles.add(1155573853972594861l); //head of community
		staffRoles.add(1155573857130905650l); //developer
		staffRoles.add(1208893851033407539l); //service data analyst
		staffRoles.add(1155573860343746630l); //staff supervisor
		staffRoles.add(1155573863015514153l); //administrator
		staffRoles.add(1155573867029467316l); //moderator
		staffRoles.add(1201834197149552640l); //addon team
		staffRoles.add(1155573869827072022l); //support
		staffRoles.add(1155573873216069632l); //translator
		staffRoles.add(1155573876579905696l); //designer
		staffRoles.add(1155573879964700802l); //builder
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.decode("#e53c50"));
		eb.setThumbnail(guild.getIconUrl());
		for(long l : staffRoles) {
			Role role = guild.getRoleById(l);
			if(role != null) {
				List<Member> members = guild.getMembersWithRoles(role);
				if(members.size() != 0) {
					eb.addField(role.getName() + "(" + members.size() + ")", getFromList(members), false);
				}
			}
		}
		targetChannel.sendMessageEmbeds(eb.build()).queue();
	}
	
	String getFromList(List<Member> input) {
		StringBuilder sb = new StringBuilder();
		for(Member member : input) {
			sb.append(member.getAsMention());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	String transform(List<String> input) {
		StringBuilder sb = new StringBuilder();
		for(String s : input) {
			sb.append(s);
			sb.append("\n \n");
		}
		return sb.toString();
	}
}