//Created by Maurice H. at 08.10.2024
package eu.lotusgaming.bot.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import eu.lotusgaming.bot.main.Main;
import eu.lotusgaming.bot.misc.MCPlayer;
import eu.lotusgaming.bot.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

public class Playerlookup extends ListenerAdapter {
	
	static List<String> savedPlayers = new ArrayList<>();
	
	@Override
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
		if(event.getName().equals("mclookup") && event.getFocusedOption().getName().equals("player")) {
			List<Command.Choice> options = Stream.of(savedPlayers.stream().toArray(String[] ::new))
					.filter(word -> word.startsWith(event.getFocusedOption().getValue()))
					.map(word -> new Command.Choice(word, word))
					.collect(Collectors.toList());
			event.replyChoices(options).queue();
		}
	}
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.isFromGuild()) {
			if(event.getName().equals("mclookup")) {
				event.deferReply().queue();
				Guild guild = event.getGuild();
				Member member = event.getMember();
				String player = event.getOption("player").getAsString();
				MCPlayer mcp = getMCPlayerData(player);
				EmbedBuilder eb = new EmbedBuilder();
				if(mcp != null) {
					eb.addField("Account Data", "Name: " + mcp.getName() + "\nUUID: " + mcp.getUuid() + "\nRole: " + mcp.getPlayerGroup() + "\nLotusID: " + mcp.getLgcid(), false);
					eb.addField("Finance Information", "Pocket Money: " + mcp.getMoney_pocket() + " Loti\nBank Money: " + mcp.getMoney_bank() + " Loti\nInterest Level: " + mcp.getMoney_interestLevel(), false);
					eb.addField("Playtime Data", "First Join: " + translateLongIntoDate(mcp.getFirstJoin(), "dd.MM.yyyy - HH:mm:ss") + "\nLast Join: " + translateLongIntoDate(mcp.getLastJoin(), "dd.MM.yyyy - HH:mm:ss") + "\nPlaytime: " + getPlayTime(mcp.getPlaytime()), false);
					if(mcp.isOnline()) {
						eb.addField("Online", "yes, on " + mcp.getCurrentLastServer(), false);
					}else {
						eb.addField("Online", "no, left from " + mcp.getCurrentLastServer(), false);
					}
					if(mcp.isStaff()) {
						eb.addField("Staff", "yes", false);
					}else {
						eb.addField("Staff", "no", false);
					}
					eb.addField("Primary Language", "Language: " + mcp.getLanguage() + "\nCountry Code: " + mcp.getCountryCode(), false);
					if(mcp.getClan() != null && !mcp.getClan().equalsIgnoreCase("none")) {
						eb.addField("Clan", "Name: " + mcp.getClan() + "\nOwner: <name to replace> \nMembers: 0", false);
					}
					long discordSnowflakeUser = mcp.getDiscordSnowflake();
					if(discordSnowflakeUser != 0) {
						User savedUser = event.getJDA().getUserById(discordSnowflakeUser);
						if(savedUser != null) {
							eb.addField("Discord", savedUser.getAsMention(), false);
						}
					}
					eb.setColor(member.getColor());
					eb.setThumbnail("https://minotar.net/helm/" + mcp.getName() + "/256.png");
					eb.setFooter("MC-Avatar provided by Minotar.net!", guild.getIconUrl());
				}else {
					eb.setColor(ModlogController.red);
					eb.setDescription("The Player **" + player + "** has not yet played on Lotus!");
				}
				event.getHook().sendMessageEmbeds(eb.build()).queue();
			}
		}else {
			event.deferReply(true).addContent("Please execute this command on a guild.").queue();
		}
	}
	
	public static void initPlayers() {
		savedPlayers.clear();
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT name FROM mc_users");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				savedPlayers.add(rs.getString("name"));
			}
			rs.close();
			ps.close();
			Main.logger.info("Loaded " + savedPlayers.size() + " Users.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private MCPlayer getMCPlayerData(String playername) {
		MCPlayer mcp = null;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_users WHERE name = ?");
			ps.setString(1, playername);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				String uuid = rs.getString("mcuuid");
				String clan = rs.getString("clan");
				String name = rs.getString("name");
				String nick = rs.getString("nick");
				String language = rs.getString("language");
				String playerGroup = rs.getString("playerGroup");
				String countryCode = rs.getString("countryCode");
				String currentLastServer = rs.getString("currentLastServer");
				
				int lgcid = rs.getInt("lgcid");
				int playtime = rs.getInt("playTime");
				int sb_state = rs.getInt("scoreboardState");
				int money_bank = rs.getInt("money_bank");
				int money_pocket = rs.getInt("money_pocket");
				int money_interestLevel = rs.getInt("money_interestLevel");
				int killedPlayers = rs.getInt("playerKillsPlayer");
				int killedEntities = rs.getInt("playerKillsEntity");
				int killedByPlayers = rs.getInt("playerKilledByPlayer");
				int killedByEntities = rs.getInt("playerKilledByEntity");
				
				boolean isOnline = rs.getBoolean("isOnline");
				boolean isStaff = rs.getBoolean("isStaff");
				boolean allowPM = rs.getBoolean("allowMSG");
				boolean allowTPA = rs.getBoolean("allowTPA");
				
				long firstJoin = rs.getLong("firstJoin");
				long lastJoin = rs.getLong("lastJoin");
				long discordSnowflake = rs.getLong("discordId");
				
				mcp = new MCPlayer(uuid, clan, name, nick, language, playerGroup, countryCode, currentLastServer, lgcid, playtime, sb_state, money_bank, money_pocket, money_interestLevel, killedPlayers, killedEntities, killedByPlayers, killedByEntities, isOnline, isStaff, allowTPA, allowPM, discordSnowflake, firstJoin, lastJoin);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mcp;
	}
	
	private String getPlayTime(long timeInSec) {
		long hours = TimeUnit.SECONDS.toHours(timeInSec);
		long minutes = TimeUnit.SECONDS.toMinutes(timeInSec) - (hours * 60);
		long seconds = timeInSec - (hours * 3600) - (minutes * 60);
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	private String translateLongIntoDate(long input, String pattern) {
		return new SimpleDateFormat(pattern).format(new Date(input));
	}
}