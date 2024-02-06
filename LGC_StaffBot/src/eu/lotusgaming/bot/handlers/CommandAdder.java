package eu.lotusgaming.bot.handlers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandAdder {
	
	public static void addCommands(JDA jda) {
		for(Guild guild : jda.getGuilds()) {
			guild.updateCommands().addCommands(
					//Commands regarding the Ticket System.
					Commands.slash("setticketchannel", "Sets the Ticket Channel")
					.addOption(OptionType.CHANNEL, "channel", "The Channel where the message should be sent to.")
					.setGuildOnly(true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					Commands.slash("tickets", "See all tickets an user has ever made.")
					.setGuildOnly(true)
					.addOption(OptionType.USER, "user", "The user to lookup")
					.addOption(OptionType.INTEGER, "userid", "The user id to lookup")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					Commands.slash("tickethistory", "See the chat of that ticket")
					.setGuildOnly(true)
					.addOption(OptionType.INTEGER, "ticketid", "The ticket id to lookup")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					Commands.slash("ticketban", "Bans a user/id from using the ticket system")
					.setGuildOnly(true)
					.addOption(OptionType.INTEGER, "userid", "The userid to ban")
					.addOption(OptionType.USER, "user", "The user to ban")
					.addOption(OptionType.STRING, "reason", "The Reason for the ban")
					.addOption(OptionType.BOOLEAN, "opt", "Whether to ban or unban the user specified")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					//Commands regarding the Suggestion System.
					Commands.slash("setsuggestionboardchannel", "Sets the Suggestionboard Channel")
					.setGuildOnly(true)
					.addOption(OptionType.CHANNEL, "channel", "The channel where the message should be sent to")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					Commands.slash("setsuggestionmessagechannel", "Sets the Suggestion Info Channel")
					.setGuildOnly(true)
					.addOption(OptionType.CHANNEL, "channel", "The channel where the message should be sent to")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
					).queue();
		}
	}

}
