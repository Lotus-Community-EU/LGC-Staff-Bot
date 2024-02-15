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
					ruleChannel.sendMessage(transform(ruleBlock1())).queue();
					ruleChannel.sendMessage(transform(ruleBlock2())).queue();
					ruleChannel.sendMessage(transform(ruleBlock3())).queue();
					ruleChannel.sendMessage(transform(ruleBlock4())).queue();
					SimpleDateFormat time = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
			        String stime = time.format(new Date());
					ruleChannel.sendMessage("**We wish you all a lot of fun and a nice stay in the Lotus Gaming Discord**\n*The Lotus Staff Team*\n \nRules has been updated last at: ***" + stime + "***").queue();
				}
			}
		}
	}
	
	List<String> ruleBlock1() {
		List<String> rules = new ArrayList<>();
		rules.add("# Lotus Gaming Community Discord Rules");
		//rules.add("Read the rules in different languages: https://lotuscommunity.eu/");
		rules.add("\n");
		rules.add("## Section 1 - Following rulesets and policies apply");
		rules.add("§1.1.) Discord Terms of Service & Community Guidelines");
		//rules.add("§1.2.) Lotus Gaming Community Rules");
		rules.add("\n");
		return rules;
	}
	
	List<String> ruleBlock2() {
		List<String> rules = new ArrayList<>();
		rules.add("## Section 2 - Content & Usage");
		rules.add("<:red_dot:1207673800666255390> §2.1.) English is the only language permitted unless stated otherwise.");
		rules.add("<:red_dot:1207673800666255390> §2.2.) Be respectful and polite to everyone - treat others how you would want to be treated.");
		rules.add("<:red_dot:1207673800666255390> §2.3.) Channels, threads & forums should only be used for their intended purpose.");
		rules.add("<:red_dot:1207673800666255390> §2.4.) Channel descriptions consitute as part of the discord rules.");
		rules.add("### Prohibited:");
		rules.add("<:red_dot:1207673800666255390> §2.5.) Swearing, insulting, offending, provoking or aggravating others.");
		rules.add("<:red_dot:1207673800666255390> §2.6.) Asking or begging others to buy or gift products for you.");
		rules.add("<:red_dot:1207673800666255390> §2.7.) NSFW, epilepsy-inducing, sexually suggestive, racist, homophobic, sexist or other discriminatory & inappropiate content.");
		rules.add("<:red_dot:1207673800666255390> §2.8.) Starting or contributing to political or other sensitive discussions which may lead to arguments.");
		rules.add("<:red_dot:1207673800666255390> §2.9.) Spamming, such as but not limited to: messages, emotes, symbols, images, reactions, rejoining, (ghost) mentioning, tickets, ASCII art, etc..");
		rules.add("<:red_dot:1207673800666255390> §2.10.) Discussing punishments, reports, appeals, applications & complaints about team members");
		rules.add("<:red_dot:1207673800666255390> §2.11.) Requesting team members to moderate in-game. Use the in-game or web report system instead.");
		rules.add("<:red_dot:1207673800666255390> §2.12.) Any mention (e.g. emojis, channels, forums, user) within the Minecraft Chat Bridge.");
		rules.add("\n");
		return rules;
	}
	
	List<String> ruleBlock3() {
		List<String> rules = new ArrayList<>();
		rules.add("## Section 3 - Advertisments");
		rules.add("<:green_dot:1207673798753648711> §3.1.) Advertising isn't allowed anywhere, including nicknames and unsolicited direct messages.");
		rules.add("\n");
		rules.add("## Section 4 - Profile");
		rules.add("<:rose_dot:1207673802327199744> §4.1.) Your profile must be appropiate, including nickname, avatar, banner, status, pronouns & about me.");
		rules.add("<:rose_dot:1207673802327199744> §4.2.) You nickname must only consist of alphanumeric characters. Emojis are allowed when combined with alphanumeric characters.");
		rules.add("\n");
		return rules;
	}
	
	List<String> ruleBlock4() {
		List<String> rules = new ArrayList<>();
		rules.add("## Section 5 - Punishments");
		rules.add("<:red_dot:1207673800666255390> §5.1.) Punishments are structured as followed:");
		rules.add("- 2x verbal warnings");
		rules.add("- 2x logged warnings");
		rules.add("- 1 week timeout");
		rules.add("- 1 week ban");
		rules.add("- 4 week ban");
		rules.add("- consecutive bans are 90 days");
		rules.add("<:red_dot:1207673800666255390> §5.2.) Punishments that expired 12 month ago are ignored.");
		rules.add("<:red_dot:1207673800666255390> §5.3.) Punishments carry across discord accounts.");
		rules.add("<:red_dot:1207673800666255390> §5.4.) We reserve the right to revoke your access to our ticket system");
		rules.add("<:red_dot:1207673800666255390> §5.5.) Management reserve the right to override the punishment ladder.");
		rules.add("<:red_dot:1207673800666255390> §5.6.) You can contest any punishments you have received via the feedback system, selecting the Discord Punishments category when creating a ticket.");
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