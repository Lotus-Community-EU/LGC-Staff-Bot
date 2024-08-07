//Created by Chris Wille at 15.02.2024
package eu.lotusgaming.bot.command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SetRulesCommand extends ListenerAdapter{
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("setrules")) {
			if(event.getOption("channel") != null) {
				if(event.getOption("channel").getAsChannel().getType() == ChannelType.TEXT) {
					TextChannel ruleChannel = event.getOption("channel").getAsChannel().asTextChannel();
					event.reply("Rules are sent into " + ruleChannel.getAsMention()).queue();
					ruleChannel.sendMessage(transform(ruleBlock1())).queue();
					ruleChannel.sendMessage(transform(ruleBlock2())).queue();
					ruleChannel.sendMessage(transform(ruleBlock3())).queue();
					ruleChannel.sendMessage(transform(ruleBlock4())).queue();
					SimpleDateFormat time = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
			        String stime = time.format(new Date());
					ruleChannel.sendMessage("\n**We wish you all a lot of fun and a nice stay in the Lotus Gaming Discord**\n*The Lotus Staff Team*\n \nRules has been updated last at: ***" + stime + "***").queue();
				}else {
					event.deferReply(true).addContent("The Channel must be a text channel!").queue();
				}
			}
		}
	}
	
	List<String> ruleBlock1() {
		List<String> rules = new ArrayList<>();
		rules.add("# Lotus Gaming Community Discord Rules");
		rules.add("Read the rules in different languages: https://lotuscommunity.eu/");
		rules.add("\n");
		rules.add("## Section 1 - Following rulesets and policies apply");
		rules.add("<:redarrow:1208890097039314955> §1.1.) Discord [Terms of Service](<https://discord.com/terms>) & [Community Guidelines](<https://discord.com/guidelines>)");
		rules.add("<:redarrow:1208890097039314955> §1.2.) Lotus Gaming Community Global Rules");
		rules.add("\n");
		return rules;
	}
	
	List<String> ruleBlock2() {
		List<String> rules = new ArrayList<>();
		rules.add("## Section 2 - Content & Usage");
		rules.add("<:redarrow:1208890097039314955> §2.1.) English is the only language permitted unless stated otherwise.");
		rules.add("<:redarrow:1208890097039314955> §2.2.1.) Be respectful and polite to everyone - treat others how you would want to be treated.");
		rules.add("<:redarrow:1208890097039314955> §2.2.2.) Let other players finish speaking and don't interrupt them.");
		rules.add("<:redarrow:1208890097039314955> §2.3.) Channels, threads & forums should only be used for their intended purpose.");
		rules.add("<:redarrow:1208890097039314955> §2.4.) Channel descriptions consitute as part of the discord rules.");
		rules.add("### Prohibited:");
		rules.add("<:redarrow:1208890097039314955> §2.5.) Swearing, insulting, offending, provoking or aggravating others.");
		rules.add("<:redarrow:1208890097039314955> §2.6.) Asking or begging others to buy or gift products for you.");
		rules.add("<:redarrow:1208890097039314955> §2.7.) NSFW, epilepsy-inducing, sexually suggestive, racist, homophobic, sexist or other discriminatory & inappropiate content.");
		rules.add("<:redarrow:1208890097039314955> §2.8.) Starting or contributing to political or other sensitive discussions which may lead to arguments.");
		rules.add("<:redarrow:1208890097039314955> §2.9.) Spamming, such as but not limited to: messages, emotes, symbols, images, reactions, rejoining, (ghost) mentioning, tickets, ASCII art, etc..");
		rules.add("<:redarrow:1208890097039314955> §2.10.) Discussing punishments, reports, appeals, applications & complaints about team members");
		rules.add("<:redarrow:1208890097039314955> §2.11.) Requesting team members to moderate in-game. Use the in-game or web report system instead.");
		rules.add("<:redarrow:1208890097039314955> §2.12.) Any mention (e.g. emojis, channels, forums, user) within the Minecraft Chat Bridge.");
		rules.add("\n");
		return rules;
	}
	
	List<String> ruleBlock3() {
		List<String> rules = new ArrayList<>();
		rules.add("## Section 3 - Advertisments");
		rules.add("<:redarrow:1208890097039314955> §3.1.) Advertising isn't allowed anywhere, including nicknames and unsolicited direct messages.");
		rules.add(" ");
		rules.add("## Section 4 - Profile");
		rules.add("<:redarrow:1208890097039314955> §4.1.) Your profile must be appropiate, including nickname, avatar, banner, status, pronouns & about me.");
		rules.add("<:redarrow:1208890097039314955> §4.2.) Your nickname must only consist of alphanumeric characters. Emojis are allowed when combined with alphanumeric characters.");
		rules.add("\n");
		return rules;
	}
	
	List<String> ruleBlock4() {
		List<String> rules = new ArrayList<>();
		rules.add("## Section 5 - Punishments");
		rules.add("<:redarrow:1208890097039314955> §5.1.) Punishments are structured as followed:");
		rules.add("- 2x verbal warnings");
		rules.add("- 2x logged warnings");
		rules.add("- 1 week timeout");
		rules.add("- 1 week ban");
		rules.add("- 4 week ban");
		rules.add("- consecutive bans are 90 days");
		rules.add("<:redarrow:1208890097039314955> §5.2.) Punishments that expired 12 month ago are ignored.");
		rules.add("<:redarrow:1208890097039314955> §5.3.) Punishments carry across discord accounts.");
		rules.add("<:redarrow:1208890097039314955> §5.4.) We reserve the right to revoke your access to our ticket system");
		rules.add("<:redarrow:1208890097039314955> §5.5.) Management reserve the right to override the punishment ladder.");
		rules.add("<:redarrow:1208890097039314955> §5.6.) You can contest any punishments you have received via the feedback system, selecting the Discord Punishments category when creating a ticket.");
		rules.add("\n");
		return rules;
	}
	
	String transform(List<String> input) {
		StringBuilder sb = new StringBuilder();
		for(String s : input) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}

}