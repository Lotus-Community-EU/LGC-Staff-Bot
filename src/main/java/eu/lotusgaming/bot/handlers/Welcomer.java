//Created by Chris Wille at 11.02.2024
package eu.lotusgaming.bot.handlers;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.lotusgaming.bot.misc.MiscUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Welcomer extends ListenerAdapter{
	
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		if(guild.getIdLong() == MiscUtils.public_guild) {
			Role user = guild.getRoleById(1155584988004225044l);
			guild.addRoleToMember(member, user).complete();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Color.green);
			eb.setTitle("Welcome to Lotus Gaming, " + member.getEffectiveName());
			eb.setDescription("Welcome, Välkommen, Hallo, Hola, Merhaba, Salut on Lotus Gaming.\n \nWe all wish you a nice stay in here!\n \nDon't forget to read the <#1155411589428690975> and <#1153419307716452454>!");
			eb.setThumbnail(member.getEffectiveAvatarUrl());
			eb.setFooter(new SimpleDateFormat("dd/MM/yy - HH:mm").format(new Date()), guild.getIconUrl());
			guild.getTextChannelById(1203764541792583780l).sendMessageEmbeds(eb.build()).queue(ra -> {
				ra.addReaction(Emoji.fromFormatted("<a:catwave:1206318331779489792>")).queue();
			});
		}
	}

}

