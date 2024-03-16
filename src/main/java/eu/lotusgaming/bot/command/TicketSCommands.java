package eu.lotusgaming.bot.command;

import java.awt.Color;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.bot.main.LotusManager;
import eu.lotusgaming.bot.misc.MySQL;
import eu.lotusgaming.bot.misc.TextCryptor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class TicketSCommands extends ListenerAdapter{
	
	static int nextTicketId = 0;
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("setticketchannel")) {
			//Creating the embed
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Welcome to the Helpdesk of Lotus Gaming");
			eb.setColor(Color.decode("#6cb547"));
			eb.setDescription("Before you open a ticket, take a look on our [Knowledge Base](https://lotusgaming.eu/kb).\n \n"
					+ "We can't help you if:\n"
					+ "1.) You don't elaborate your issue\n"
					+ "2.) You refuse giving us all details so we can investigate properly\n"
					+ "3.) You give us bad attitude\n"
					+ "\n"
					+ "Click on one of three buttons below to open a ticket.");
			eb.addField("General Support", "Get support for all services we do offer. All user account related inquiries you also have to submit your username.", true);
			eb.addField("Report a user", "You get harassed, tried to be scammed or just got spammed? Please send us a screenshot as well the User ID.\nWithout evidence we won't take actions!", false);
			eb.addField("Premium Support", "You purchased Premium and you still didn't got the perks or have questions about a premium feature you are unsure about? You are right here. \n \n \n"
					+ "***Our Support / Moderation Team will answer as quickly as possible. Please do not ping them, they will get back to you as soon as possible!***", false);
			
			if(event.getOption("channel").getAsChannel().getType() == ChannelType.TEXT) {
				TextChannel channel = event.getOption("channel").getAsChannel().asTextChannel();
				channel.sendMessageEmbeds(eb.build()).addActionRow(
						Button.primary("gensupp", "General Support").withEmoji(Emoji.fromFormatted("<:lgc_logo:1203440133659959326>")),
						Button.secondary("premsupp", "Premium Support").withEmoji(Emoji.fromFormatted("U+2B50")),
						Button.danger("repuser", "Report a User").withEmoji(Emoji.fromFormatted("U+1F46E"))
						).queue();
				event.reply("Support Ticket Info Channel has been sent and set to " + channel.getAsMention()).queue();
			}else {
				event.deferReply(true).queue();
				event.getHook().sendMessage("Sorry, but the channel must be a text channel!").queue();
			}
		}else if(event.getName().equals("tickets")) {
			event.deferReply().queue();
			long userid = 0;
			if(event.getOption("userid") != null) {
				userid = event.getOption("userid").getAsLong();
			}else if(event.getOption("user") != null) {
				User target = event.getOption("user").getAsUser();
				userid = target.getIdLong();
			}
			List<String> ids = new ArrayList<>();
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT id,isClosed FROM bot_s_tickets WHERE creatorId = ?");
				ps.setLong(1, userid);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					if(rs.getBoolean("isClosed")) {
						ids.add(rs.getInt("id") + "");
					}else {
						ids.add(rs.getInt("id") + "*");
					}
				}
			} catch (SQLException e) { e.printStackTrace(); }
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for(String s : ids) {
				count++;
				sb.append("``" + s + "``, ");
			}
			if(count == 0) {
				event.getHook().sendMessage("The user ``" + userid + "`` has no tickets created yet.").queue();
			}else {
				event.getHook().sendMessage("The user ``" + userid + "`` has ``" + count + "`` Tickets: " + sb.toString().substring(0, (sb.toString().length() - 2))).queue();
			}
		}else if(event.getName().equals("tickethistory")) {
			Guild guild = event.getGuild();
			event.deferReply().queue();
			int ticketId = event.getOption("ticketid").getAsInt();
			long creatorId = 0;
			long createdAt = 0;
			String topic = "";
			long closedAt = 0;
			long closedById = 0;
			String closeReason = "";
			String msgHistory = "";
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_tickets WHERE id = ?");
				ps.setInt(1, ticketId);
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					if(rs.getBoolean("isClosed")) {
						creatorId = rs.getLong("creatorId");
						createdAt = rs.getLong("createdAt");
						topic = rs.getString("topic");
						closedAt = rs.getLong("closedAt");
						closedById = rs.getLong("closedBy");
						closeReason = rs.getString("closeReason");
						msgHistory = rs.getString("msg_history");
					}else {
						event.getHook().sendMessage("Hey, it seems that this ticket is still in progress.").queue();
						return;
					}
				}else {
					event.getHook().sendMessage("Hey, it seems that the Ticket ID ``" + ticketId + "`` doesn't exist.").queue();
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			YamlFile cfg = null;
			try {
				cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Member creator = guild.getMemberById(creatorId);
			Member closer = guild.getMemberById(closedById);
			String history = translateListIntoProperSentences(translateIntoHumanReadableMessages(msgHistory, cfg.getString("Bot.HashPassword")), event.getJDA());
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy - HH:mm:ss");
			String created = sdf.format(new Date(createdAt));
			String closed = sdf.format(new Date(closedAt));
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(event.getMember().getColor());
			eb.setTitle("Ticket Transcription");
			if(creator != null) {
				eb.addField("Ticket created", "by: " + creator.getAsMention() + "\nat: " + created, true);
			}else {
				eb.addField("Ticket created", "by: " + creatorId + "\nat: " + created, true);
			}
			if(closer != null) {
				eb.addField("Ticket closed", "by: " + closer.getAsMention() + "\nwith Reason: " + closeReason + "\nat: " + closed, false);
			}else {
				eb.addField("Ticket closed", "by: " + closedById + "\nwith Reason: " + closeReason + "\nat: " + closed, false);
			}
			eb.addField("Topic", topic, false);
			if(history.length() >= 1023) {
				history = history.substring(0, 1023);
			}
			eb.addField("Message History", history, false);
			event.getHook().sendMessageEmbeds(eb.build()).queue();
		}else if(event.getName().equals("ticketban")) {
			event.deferReply().queue();
			Guild guild = event.getGuild();
			Member member = event.getMember();
			long userid = 0;
			User target = null;
			if(event.getOption("userid") != null) {
				userid = event.getOption("userid").getAsLong();
			}else if(event.getOption("user") != null) {
				target = event.getOption("user").getAsUser();
				userid = target.getIdLong();
			}
			String reason = "";
			boolean opt = false;
			if(event.getOption("opt") == null) {
				opt = true;
			}else {
				opt = event.getOption("opt").getAsBoolean();
			}
			if(event.getOption("reason") == null) {
				reason = "No Reason specified.";
			}else {
				reason = event.getOption("reason").getAsString();
			}
			if(opt) {
				if(addBan(member.getIdLong(), userid, reason)) {
					event.getHook().sendMessage("Hey, it seems this user is already banned from creating tickets.").queue();
				}else {
					if(guild.isMember(target)) {
						event.getHook().sendMessage(target.getAsMention() + " is now banned from the ticket system.").queue();
					}else {
						event.getHook().sendMessage(userid + " is now banned from the ticket system.").queue();
					}
				}
			}else {
				if(removeBan(userid)) {
					event.getHook().sendMessage("Hey, it seems this user is already unbanned.").queue();
				}else {
					if(guild.isMember(target)) {
						event.getHook().sendMessage(target.getAsMention() + " is now unbanned from the ticket system.").queue();
					}else {
						event.getHook().sendMessage(userid + " is now unbanned from the ticket system.").queue();
					}
				}
			}
			
		}
	}
	
	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event) {
		if(event.getName().equals("Start Ticket")) {
			if(hasActiveTicket(event.getTarget().getAuthor().getIdLong())) {
				event.deferReply(true).addContent("The User already has an active ticket.").queue();
			}else {
				Guild guild = event.getGuild();
				Category ticketsCategory = guild.getCategoryById(1203709412460470398l);
				Member member = event.getMember();
				if(guild.isMember(event.getTarget().getAuthor())) {
					Member target = guild.getMember(event.getTarget().getAuthor());
					guild.createTextChannel("ticket-" + nextTicketId, ticketsCategory).queue(ra -> {
						sendTicketLogCreated(ra.getIdLong(), member, "Context Menu Ticket", ra, guild);
						ra.upsertPermissionOverride(target).grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EXT_STICKER).queue();
						ra.getManager().setTopic("Ticket #" + nextTicketId + " created by " + member.getEffectiveName() + " for " + target.getEffectiveName() + " - Topic: Context Menu Ticket").queue();
						addTicketToDB(member.getIdLong(), ra.getIdLong(), "Context Menu Ticket");
						ra.sendTyping().queue();
						EmbedBuilder eb = new EmbedBuilder();
						eb.setDescription("You've opened a new ticket.\n"
								+ "A staff member will be with you in touch shortly.");
						eb.addField("Opened by:", member.getAsMention(), true);
						eb.addField("Opened for:", target.getAsMention(), false);
						eb.addField("Target Message", event.getTarget().getContentRaw(), false);
						eb.addField("Need support in your language?", "Add a reaction with your countries flag and we'll try to answer in that language.", false);
						eb.addField("Rules", "We'll try to offer support as good as we can, however don't mention anyone from our staff team. We'll get to you as soon as we can!", false);
						ra.sendMessage("" + member.getAsMention() + " " + target.getAsMention()).queue(ra1 -> {
							ra1.delete().queueAfter(2, TimeUnit.SECONDS);
						});
						ra.sendMessageEmbeds(eb.build()).addActionRow(
								Button.danger("closereason", "Close with Reason").withEmoji(Emoji.fromFormatted("U+1F512"))
								).queue();
					});
				}else {
					event.deferReply(true).addContent("The user is not present on this guild!").queue();
				}
			}
		}
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		if(event.getComponentId().equals("gensupp")) {
			boolean isBanned = false;
			String reason = "";
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT banReason FROM bot_s_ticketban WHERE bannedId = ?");
				ps.setLong(1, event.getMember().getIdLong());
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					isBanned = true;
					reason = rs.getString("banReason");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(isBanned) {
				event.deferReply(true).addContent("You're banned from the ticket system for following reason: " + reason).queue();
			}else {
				if(hasActiveTicket(event.getUser().getIdLong())) {
					event.deferReply(true).addContent("You've already an active ticket. Finalise this first!").queue();
				}else {
					event.deferReply(true).addContent("The bot will ping you in your ticket channel.").queue();
					Guild guild = event.getGuild();
					Category ticketsCategory = guild.getCategoryById(1203709412460470398l);
					Member member = event.getMember();
					guild.createTextChannel("ticket-" + nextTicketId, ticketsCategory).queue(chan -> {
						sendTicketLogCreated(nextTicketId, member, "General Support", chan, guild);
						chan.upsertPermissionOverride(member).grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EXT_STICKER).queue();
						chan.getManager().setTopic("Ticket #" + nextTicketId + " created by " + member.getEffectiveName() + " - Topic: General Support").queue();
						addTicketToDB(member.getIdLong(), chan.getIdLong(), "General Support");
						chan.sendTyping().queue();
						EmbedBuilder eb = new EmbedBuilder();
						eb.setDescription("You've opened a new ticket.\n"
								+ "A staff member will be with you in touch shortly.");
						eb.addField("Opened by:", member.getAsMention(), true);
						eb.addField("Need support in your language?", "Add a reaction with your countries flag and we'll try to answer in that language.", false);
						eb.addField("Rules", "We'll try to offer support as good as we can, however don't mention anyone from our staff team. We'll get to you as soon as we can!", false);
						chan.sendMessage("" + member.getAsMention()).queue(ra -> {
							ra.delete().queueAfter(2, TimeUnit.SECONDS);
						});
						chan.sendMessageEmbeds(eb.build()).addActionRow(
								Button.danger("closenoreason", "Close").withEmoji(Emoji.fromFormatted("U+1F512")),
								Button.danger("closereason", "Close with Reason").withEmoji(Emoji.fromFormatted("U+1F512"))
								).queue();
					});
				}
			}
		}else if(event.getComponentId().equals("premsupp")) {
			boolean isBanned = false;
			String reason = "";
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT banReason FROM bot_s_ticketban WHERE bannedId = ?");
				ps.setLong(1, event.getMember().getIdLong());
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					isBanned = true;
					reason = rs.getString("banReason");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(isBanned) {
				event.deferReply(true).addContent("You're banned from the ticket system for following reason: " + reason).queue();
			}else {
				if(hasActiveTicket(event.getUser().getIdLong())) {
					event.deferReply(true).addContent("You've already an active ticket. Finalise this first!").queue();
				}else {
					TextInput mcuuid = TextInput.create("uuid", "Minecraft Name", TextInputStyle.SHORT)
							.setPlaceholder("Your Minecraft Name")
							.setRequiredRange(3, 16)
							.build();
					TextInput mail = TextInput.create("mail", "E-Mail", TextInputStyle.SHORT)
							.setPlaceholder("Your E-Mail Adress")
							.setRequiredRange(5, 48)
							.build();
					Modal modal = Modal.create("premsuppmodal", "Premium Support")
							.addComponents(ActionRow.of(mcuuid), ActionRow.of(mail))
							.build();
					event.replyModal(modal).queue();
				}
			}
		}else if(event.getComponentId().equals("repuser")) {
			boolean isBanned = false;
			String reason = "";
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT banReason FROM bot_s_ticketban WHERE bannedId = ?");
				ps.setLong(1, event.getMember().getIdLong());
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					isBanned = true;
					reason = rs.getString("banReason");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(isBanned) {
				event.deferReply(true).addContent("You're banned from the ticket system for following reason: " + reason).queue();
			}else {
				if(hasActiveTicket(event.getUser().getIdLong())) {
					event.deferReply(true).addContent("You've already an active ticket. Finalise this first!").queue();
				}else {
					TextInput uid = TextInput.create("uid", "UserID", TextInputStyle.SHORT)
							.setPlaceholder("The UserID from the user")
							.setRequiredRange(2, 32)
							.build();
					TextInput desc = TextInput.create("reason", "Reason", TextInputStyle.PARAGRAPH)
							.setPlaceholder("A brief description what the user did.")
							.setRequired(false)
							.setRequiredRange(0, 256)
							.build();
					Modal modal = Modal.create("repusermodal", "Report a User")
							.addComponents(ActionRow.of(uid), ActionRow.of(desc))
							.build();
					event.replyModal(modal).queue();
				}
			}
		}else if(event.getComponentId().equals("closereason")) {
			TextInput reason = TextInput.create("closereason", "Reason", TextInputStyle.SHORT)
					.setPlaceholder("Reason")
					.setValue("Ticket resolved.")
					.setRequiredRange(10, 128)
					.build();
			Modal modal = Modal.create("closeticketmodal", "Close Ticket")
					.addComponents(ActionRow.of(reason))
					.build();
			event.replyModal(modal).queue();
			
		}else if(event.getComponentId().equals("closenoreason")) {
			event.deferReply(true).addContent("Ticket will be closed in 5 seconds...").queue();
			TextChannel channel = event.getChannel().asTextChannel();
			String ticketId = channel.getName().substring(7);
			sendTicketLogDeleted(event.getGuild(), ticketId, event.getMember(), "Closed without reason.");
			closeTicket(channel.getIdLong(), event.getMember().getIdLong(), "Ticket has been closed with no supplied reason.");
			channel.delete().queueAfter(4, TimeUnit.SECONDS);
		}
	}
	
	@Override
	public void onModalInteraction(ModalInteractionEvent event) {
		Guild guild = event.getGuild();
		Category ticketsCategory = guild.getCategoryById(1203709412460470398l);
		Member member = event.getMember();
		if(event.getModalId().equals("repusermodal")) {
			event.deferReply(true).queue();
			guild.createTextChannel("ticket-" + nextTicketId, ticketsCategory).queue(chan -> {
				sendTicketLogCreated(nextTicketId, member, "Report a User", chan, guild);
				chan.getManager().setTopic("Ticket #" + nextTicketId + " created by " + member.getEffectiveName() + " - Topic: Report a User").queue();
				addTicketToDB(member.getIdLong(), chan.getIdLong(), "Report a User");
				Role discMod = guild.getRoleById(1201941339122716672l);
				String uid = event.getValue("uid").getAsString();
				long id = 0;
				if(uid.matches("[0-9]+$")) {
					id = Long.parseLong(uid);
				}
				Member target = guild.getMemberById(id);
				chan.sendTyping().queue();
				EmbedBuilder eb = new EmbedBuilder();
				eb.setDescription("You've opened a new ticket.\n"
						+ "A staff member will be with you in touch shortly.");
				eb.addField("Opened by:", member.getAsMention(), true);
				eb.addField("Scam or Spam?", "Please provide a proof of the messages you received and if possible, the Discord User ID - this would speed up the lookup of the attacker.", false);
				eb.addField("Need support in your language?", "Add a reaction with your countries flag and we'll try to answer in that language.", false);
				eb.addField("Rules", "We'll try to offer support as good as we can, however don't mention anyone from our staff team. We'll get to you as soon as we can!", false);
				if(target != null) {
					eb.addField("User Lookup resulted this member.", "Please approve if it's this user: " + target.getAsMention(), false);
				}else {
					eb.addField("User Discord ID", uid, false);
				}
				eb.addField("Reason", event.getValue("reason").getAsString(), false);
				chan.sendMessage("" + discMod.getAsMention() + " " + member.getAsMention()).queue(ra -> {
					ra.delete().queueAfter(10, TimeUnit.SECONDS);
				});
				chan.sendMessageEmbeds(eb.build()).addActionRow(
						Button.danger("closenoreason", "Close").withEmoji(Emoji.fromFormatted("U+1F512")),
						Button.danger("closereason", "Close with Reason").withEmoji(Emoji.fromFormatted("U+1F512"))
						).queue();
			});
		}else if(event.getModalId().equals("premsuppmodal")) {
			event.deferReply(true).queue();
			guild.createTextChannel("ticket-" + nextTicketId, ticketsCategory).queue(chan -> {
				sendTicketLogCreated(nextTicketId, member, "Premium Support", chan, guild);
				chan.getManager().setTopic("Ticket #" + nextTicketId + " created by " + member.getEffectiveName() + " - Topic: Premium Support").queue();
				addTicketToDB(member.getIdLong(), chan.getIdLong(), "Premium Support");
				Role support = guild.getRoleById(1155573869827072022l);
				chan.sendTyping().queue();
				EmbedBuilder eb = new EmbedBuilder();
				eb.setDescription("You've opened a new ticket.\n"
						+ "A staff member will be with you in touch shortly.");
				eb.addField("Opened by:", member.getAsMention(), true);
				eb.addField("Need support in your language?", "Add a reaction with your countries flag and we'll try to answer in that language.", false);
				eb.addField("Rules", "We'll try to offer support as good as we can, however don't mention anyone from our staff team. We'll get to you as soon as we can!", false);
				eb.addField("Minecraft Name", event.getValue("uuid").getAsString(), false);
				eb.addField("E-Mail", event.getValue("mail").getAsString(), false);
				chan.sendMessage("" + support.getAsMention() + " " + member.getAsMention()).queue(ra -> {
					ra.delete().queueAfter(10, TimeUnit.SECONDS);
				});
				chan.sendMessageEmbeds(eb.build()).addActionRow(
						Button.danger("closenoreason", "Close").withEmoji(Emoji.fromFormatted("U+1F512")),
						Button.danger("closereason", "Close with Reason").withEmoji(Emoji.fromFormatted("U+1F512"))
						).queue();
			});
		}else if(event.getModalId().equals("closeticketmodal")) {
			TextChannel channel = event.getChannel().asTextChannel();
			closeTicket(channel.getIdLong(), member.getIdLong(), event.getValue("closereason").getAsString());
			String ticketId = channel.getName().substring(7);
			sendTicketLogDeleted(event.getGuild(), ticketId, event.getMember(), event.getValue("closereason").getAsString());
			event.deferReply(true).addContent("Thank you, the ticket will be closed now.").queue();
			channel.delete().queueAfter(5, TimeUnit.SECONDS);
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.isFromGuild()) {
			if(event.isFromType(ChannelType.TEXT)) {
				TextChannel channel = event.getChannel().asTextChannel();
				if(isTicketChannel(channel.getIdLong())) {
					if(!event.getAuthor().isBot()) {
						YamlFile cfg = null;
						try {
							cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
						} catch (IOException e) {
							e.printStackTrace();
						}
						addMessageHistory(event.getMember().getIdLong(), event.getMessage().getContentRaw(), channel.getIdLong(), cfg.getString("Bot.HashPassword"));
					}
				}
			}
		}
	}
	
	boolean hasActiveTicket(long userId) {
		boolean hasTicket = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_tickets WHERE creatorId = ? AND isClosed = ?");
			ps.setLong(1, userId);
			ps.setBoolean(2, false);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				hasTicket = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hasTicket;
	}
	
	void sendTicketLogCreated(long ticketId, Member creator, String topic, TextChannel ticketChannel, Guild guild) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.green);
		eb.setDescription("A new ticket has been created!\n"
				+ "Creator: " + creator.getAsMention() + " / " + creator.getEffectiveName() + "\n"
				+ "Channel: " + ticketChannel.getAsMention() + "\n"
				+ "Ticket ID: " + ticketId + "\n"
				+ "Topic: " + topic);
		guild.getTextChannelById(1208894257289756705l).sendMessageEmbeds(eb.build()).queue();
	}
	
	void sendTicketLogDeleted(Guild guild, String ticketId, Member closer, String reason) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.red);
		eb.setDescription("Ticket " + ticketId + " has been closed.\n"
				+ "Closer: " + closer.getAsMention() + " / " + closer.getEffectiveName() + "\n"
				+ "Reason: " + reason);
		guild.getTextChannelById(1208894257289756705l).sendMessageEmbeds(eb.build()).queue();
	}
	
	void closeTicket(long channelId, long closer, String reason) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE bot_s_tickets SET closedAt = ?, closedBy = ?, closeReason = ?, isClosed = ? WHERE channelId = ?");
			ps.setLong(1, System.currentTimeMillis());
			ps.setLong(2, closer);
			ps.setString(3, reason);
			ps.setBoolean(4, true);
			ps.setLong(5, channelId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	boolean isTicketChannel(long channelId) {
		boolean isTicket = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT channelId FROM bot_s_tickets WHERE channelId = ?");
			ps.setLong(1, channelId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				isTicket = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isTicket;
	}
	
	void addTicketToDB(long creatorId, long channelid, String topic) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO bot_s_tickets(creatorId,createdAt,topic,channelId) VALUES (?,?,?,?)");
			ps.setLong(1, creatorId);
			ps.setLong(2, System.currentTimeMillis());
			ps.setString(3, topic);
			ps.setLong(4, channelid);
			ps.executeUpdate();
			nextTicketId++;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void addMessageHistory(long messager, String message, long channelId, String pass) {
		String oldMessage = getMessageHistory(channelId);
		List<String> history = translateIntoHumanReadableMessages(oldMessage, pass);
		history.add(messager + ";-" + System.currentTimeMillis() + ";-" + message);
		String newMessage = translateIntoHashedMessage(history, pass);
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE bot_s_tickets SET msg_history = ?, lastMessage = ? WHERE channelId = ?");
			ps.setString(1, newMessage);
			ps.setLong(2, System.currentTimeMillis());
			ps.setLong(3, channelId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	String getMessageHistory(long channelId) {
		String toReturn = "";
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT msg_history FROM bot_s_tickets WHERE channelId = ?");
			ps.setLong(1, channelId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				toReturn = rs.getString("msg_history");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	
	List<String> translateIntoHumanReadableMessages(String input, String pass) {
		String[] enc = TextCryptor.decrypt(input, pass.toCharArray()).split(";;");
		List<String> history = new ArrayList<>();
		for(String string : enc) {
			history.add(string);
		}
		return history;
	}
	
	String translateListIntoProperSentences(List<String> input, JDA jda) {
		input.remove(0);
		StringBuilder sb = new StringBuilder();
		for(String string : input) {
			if(string.split(";-").length == 3) {
				User user = jda.getUserById(string.split(";-")[0]);
				long timestamp = Long.parseLong(string.split(";-")[1]);
				String msg = string.split(";-")[2];
				//SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy - HH:mm:ss");
				//String date = sdf.format(new Date(timestamp));
				if(user != null) {
					sb.append("<t:" + (timestamp / 1000) + ":R> | " + user.getEffectiveName() + ": " + msg);
				}else {
					sb.append("<t:" + (timestamp / 1000) + ":R> | " + string.split(";-")[0] + ": " + msg);
				}
				sb.append("\n");
			}
		}
		if(sb.toString().isBlank()) {
			return "Error in Message History.";
		}else {
			return sb.toString();
		}
	}
	
	String translateIntoHashedMessage(List<String> input, String pass) {
		String toReturn = "";
		StringBuilder sb = new StringBuilder();
		for(String string : input) {
			sb.append(string);
			sb.append(";;");
		}
		toReturn = sb.toString().substring(0, (sb.toString().length() - 2));
		toReturn = TextCryptor.encrypt(toReturn, pass.toCharArray());
		return toReturn;
	}
	
	//If a user is already banned, true will be returned, otherwise false.
	boolean addBan(long bannerId, long bannedId, String reason) {
		boolean isBanned = false;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_ticketban WHERE bannedId = ?");
			ps.setLong(1, bannedId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				isBanned = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(!isBanned) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO bot_s_ticketban(bannerId,bannedAt,banReason,bannedId) VALUES (?,?,?,?)");
				ps.setLong(1, bannerId);
				ps.setLong(2, System.currentTimeMillis());
				ps.setString(3, reason);
				ps.setLong(4, bannedId);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return isBanned;
	}
	
	//if a user is already unbanned, true will be returned, otherwise false.
	boolean removeBan(long bannedId) {
		boolean isUnbanned = true;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_ticketban WHERE bannedId = ?");
			ps.setLong(1, bannedId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				isUnbanned = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(!isUnbanned) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM bot_s_ticketban WHERE bannedId = ?");
				ps.setLong(1, bannedId);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return isUnbanned;
	}
	
	public static void loadLastTicketId() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT id FROM bot_s_tickets ORDER BY id DESC");
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				nextTicketId = (rs.getInt("id") + 1);
			}else {
				nextTicketId = 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
