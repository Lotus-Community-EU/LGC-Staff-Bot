package eu.lotusgaming.bot.handlers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

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
					.addOption(OptionType.INTEGER, "ticketid", "The ticket id to lookup", true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					Commands.context(Type.MESSAGE, "Start Ticket")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS, Permission.KICK_MEMBERS)),
					
					Commands.slash("ticket", "Main command for the Ticket System")
					.setGuildOnly(true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS, Permission.KICK_MEMBERS))
					.addSubcommands(
                            new SubcommandData("adduser", "Adds an user to this ticket")
                            .addOption(OptionType.USER, "user", "The user to add", true),
                            
                            new SubcommandData("removeuser", "Removes an user from this ticket")
                            .addOption(OptionType.USER, "user", "The user to remove from this ticket", true),
                            
                            new SubcommandData("close", "Closes the ticket")
                            .addOption(OptionType.STRING, "reason", "The reason to close this ticket", true),
                            
                            new SubcommandData("closerequest", "Requests the ticket creator to close the ticket")
                            .addOption(OptionType.STRING, "reason", "The reason to close this ticket", true)
                            .addOption(OptionType.INTEGER, "close_delay", "How many hours to pass if the user does not respond to auto-close it.")
                        ),
					
					Commands.context(Type.MESSAGE, "Translate"),
					
					Commands.context(Type.MESSAGE, "Report this!"),
					
					Commands.slash("ticketban", "Bans a user/id from using the ticket system")
					.setGuildOnly(true)
					.addOption(OptionType.INTEGER, "userid", "The userid to ban")
					.addOption(OptionType.USER, "user", "The user to ban")
					.addOption(OptionType.STRING, "reason", "The Reason for the ban")
					.addOption(OptionType.BOOLEAN, "opt", "Whether to ban or unban the user specified")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					//Commands regarding the Suggestion System.
					Commands.slash("setsuggestionboardchannel", "Sets the Suggestion Info Channel")
					.setGuildOnly(true)
					.addOption(OptionType.CHANNEL, "channel", "The channel where the message should be sent to")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					Commands.slash("setsuggestionmessagechannel", "Sets the Suggestionboard Channel")
					.setGuildOnly(true)
					.addOption(OptionType.CHANNEL, "channel", "The channel where the message should be sent to")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					Commands.slash("setrules", "Sends the rules")
					.setGuildOnly(true)
					.addOption(OptionType.CHANNEL, "channel", "The channel where the rules should be sent to.", true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					Commands.slash("setinfo", "Sends the info")
					.setGuildOnly(true)
					.addOption(OptionType.CHANNEL, "channel", "The channel where the rules should be sent to.", true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
					
					Commands.slash("kick", "Kicks a member")
					.setGuildOnly(true)
					.addOption(OptionType.USER, "user", "The user to kick", true)
					.addOption(OptionType.STRING, "reason", "The reason why the user gets kicked", true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.KICK_MEMBERS)),
					
					Commands.slash("ban", "Bans a member")
					.setGuildOnly(true)
					.addOption(OptionType.USER, "user", "The user to ban", true)
					.addOption(OptionType.STRING, "reason", "The reason why the user gets banned", true)
					.addOption(OptionType.INTEGER, "time", "Time for the ban's persistiency (0 for Permanent)", true)
					.addOption(OptionType.STRING, "timeunit", "Time Unit for the ban (seconds, minutes, hours, days)", true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.BAN_MEMBERS)),
					
					Commands.slash("warn", "Warns a member")
					.setGuildOnly(true)
					.addOption(OptionType.USER, "user", "The user to warn", true)
					.addOption(OptionType.STRING, "reason", "The reason why the user gets warned", true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.BAN_MEMBERS, Permission.KICK_MEMBERS)),
					
					Commands.slash("mute", "Mutes a member (Utilising timeout)")
					.setGuildOnly(true)
					.addOption(OptionType.USER, "user", "The user to mute", true)
					.addOption(OptionType.STRING, "reason", "The reason why the user got muted", true)
					.addOption(OptionType.INTEGER, "time", "Time for the ban's persistiency (0 for Permanent)", true)
					.addOption(OptionType.STRING, "timeunit", "Time Unit for the ban (seconds, minutes, hours, days)", true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.BAN_MEMBERS, Permission.KICK_MEMBERS)),
					
					Commands.slash("say", "Let the bot talk")
					.setGuildOnly(true)
					.addOption(OptionType.CHANNEL, "targetchannel", "The Channel the bot should write in", true)
					.addOption(OptionType.STRING, "text", "The text the bot should write", true)
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR, Permission.MANAGE_CHANNEL, Permission.MANAGE_SERVER)),
					
					Commands.slash("purge", "Purge messages from in this channel.")
					.setGuildOnly(true)
					.addOption(OptionType.INTEGER, "messages", "The count of messages to be deleted (max 100)", true)
					.addOption(OptionType.USER, "member", "Only delete the messages from this user (optional)")
					.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE, Permission.MANAGE_SERVER)),
					
					Commands.slash("set-status", "Modify the online status for the bot")
					.setGuildOnly(false),
					
					Commands.slash("set-activity", "Modify the activity status for the bot")
					.setGuildOnly(false)
					.addOption(OptionType.STRING, "option", "Choose what kind of activity the bot should display", true, true)
					.addOption(OptionType.STRING, "text", "The text followed up", true),
					
					Commands.slash("leaderboard", "See the Top 10 of Chatters in this Discord Guild.")
					.addOption(OptionType.INTEGER, "top", "Override how many Entries you'd like to see. Up to 25 possible."),
					
					Commands.slash("level", "See your or someone else's current Level")
					.addOption(OptionType.USER, "member", "The member to lookup."),
					
					Commands.slash("mclookup", "Player Lookup on Lotus for Minecraft")
					.setGuildOnly(true)
					.addOption(OptionType.STRING, "player", "The player to lookup", true, true),
					
					Commands.slash("serverinfo", "Looking up specific game server")
					.setGuildOnly(true)
					.addOptions(new OptionData(OptionType.STRING, "server", "Specify the Game Server").setAutoComplete(true))
					).queue();
		}
	}

}
