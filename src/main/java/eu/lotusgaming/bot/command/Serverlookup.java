//Created by Maurice H. at 09.10.2024
package eu.lotusgaming.bot.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import eu.lotusgaming.bot.main.Main;
import eu.lotusgaming.bot.misc.MCServer;
import eu.lotusgaming.bot.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class Serverlookup extends ListenerAdapter{
	
	static List<String> savedServers = new ArrayList<>();
	
	@Override
	public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
		if(event.getName().equals("serverinfo") && event.getFocusedOption().getName().equals("server")) {
			List<Command.Choice> options = Stream.of(savedServers.stream().toArray(String[] ::new))
					.filter(word -> word.startsWith(event.getFocusedOption().getValue()))
					.map(word -> new Command.Choice(word,  word))
					.collect(Collectors.toList());
			event.replyChoices(options).queue();
		}
	}
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.isFromGuild()) {
			if(event.getName().equals("serverinfo")) {
				OptionMapping om = event.getOption("server");
				if(om == null) {
					//outputs all data into one embed
					event.replyEmbeds(allServerData(event.getMember())).queue();
				}else {
					String servername = om.getAsString();
					MCServer mcs = getServerDetails(servername);
					if(mcs == null) {
						event.reply("The Server ``" + servername + "`` does not exist or is hidden from API").queue();
					}else {
						if(!mcs.isHiddenAPI()) {
							EmbedBuilder eb = new EmbedBuilder();
							eb.setColor(ModlogController.green);
							List<String> list = new ArrayList<>();
							if(mcs.isOnline()) {
								list.add("Server is ***online***!");
								eb.setColor(ModlogController.green);
							}else {
								list.add("Server is ***offline***!");
								eb.setColor(ModlogController.red);
							}
							if(mcs.isMonitored()) {
								list.add("**Server is being monitored!**");
								eb.setColor(ModlogController.yellow);
							}
							if(mcs.isLocked()) {
								list.add("***Server is locked!***");
								eb.setColor(ModlogController.red);
							}
							list.add("Players: " + mcs.getCurrentPlayers() + " (" + mcs.getCurrentStaffs() + " Staffs) / " + mcs.getMaxPlayers() + " Players (" + mcs.getPlayerCapacity() + "%)");
							list.add("Players: " + getPlayersByServer(mcs.getServername()));
							if(mcs.isMinigame()) {
								list.add("Minigame Server\nMinigame: " + mcs.getMinigameType() + "\nMap Name: " + mcs.getMinigameMapName() + "\nSlots: " + mcs.getMinigameMaxSlots() + " Players");
							}else if(mcs.isHybrid()){
								list.add("Hybrid Server");
							}else {
								list.add("Normal Server");
							}
							if(mcs.isHasDynmap()) {
								list.add("[Online Map available](http://map.lotuscommunity.eu:" + mcs.getMapPort() + ")");
							}
							if(mcs.isHasJobs()) {
								list.add("Server has Jobs available.");
							}
							StringBuilder sb = new StringBuilder();
							for(String s : list) {
								sb.append(s);
								sb.append("\n");
							}
							eb.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl());
							eb.addField(mcs.getServername() + " / " + mcs.getServerId(), sb.toString(), false);
							event.replyEmbeds(eb.build()).queue();
						}else {
							event.reply("Server " + servername + " does not exist or is hidden from API.").queue();
						}
					}
				}
			}
		}
	}
	
	public static void initServers() {
		savedServers.clear();
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT servername,isHiddenAPI FROM mc_serverstats");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				if(!rs.getBoolean("isHiddenAPI")) {
					savedServers.add(rs.getString("servername"));
				}else {
					Main.logger.info("Server " + rs.getString("servername") + " has not been included!");
				}
			}
			rs.close();
			ps.close();
			Main.logger.info("Loaded " + savedServers.size() + " Servers.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	String getPlayersByServer(String server) {
		String players = "";
		StringBuilder sb = new StringBuilder();
		int count = 0;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT name,lgcid,isOnline FROM mc_users WHERE currentLastServer = ?");
			ps.setString(1, server);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				if(rs.getBoolean("isOnline")) {
					count++;
					sb.append(rs.getString("name") + " (" + rs.getInt("lgcid") + "), ");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(count == 0) {
			return "No players online";
		}else {
			players = sb.toString();
			players = players.substring(0, players.length() - 2);
			return players;
		}
	}
	
	private MessageEmbed allServerData(Member member) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(ModlogController.green);
		eb.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT servername FROM mc_serverstats");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				MCServer mcs = getServerDetails(rs.getString("servername"));
				if(!mcs.isHiddenAPI()) {
					List<String> list = new ArrayList<>();
					if(mcs.isOnline()) {
						list.add("Server is ***online***!");
					}else {
						list.add("Server is ***offline***!");
					}
					if(mcs.isMonitored()) {
						list.add("**Server is being monitored!**");
					}
					if(mcs.isLocked()) {
						list.add("***Server is locked!***");
					}
					list.add("Players: " + mcs.getCurrentPlayers() + " (" + mcs.getCurrentStaffs() + " Staffs) / " + mcs.getMaxPlayers() + " Players (" + mcs.getPlayerCapacity() + "%)");
					if(mcs.isMinigame()) {
						list.add("Minigame Server\nMinigame: " + mcs.getMinigameType() + "\nMap Name: " + mcs.getMinigameMapName() + "\nSlots: " + mcs.getMinigameMaxSlots() + " Players");
					}else if(mcs.isHybrid()){
						list.add("Hybrid Server");
					}else {
						list.add("Normal Server");
					}
					if(mcs.isHasDynmap()) {
						list.add("[Online Map available](http://map.lotuscommunity.eu:" + mcs.getMapPort() + ")");
					}
					if(mcs.isHasJobs()) {
						list.add("Server has Jobs available.");
					}
					StringBuilder sb = new StringBuilder();
					for(String s : list) {
						sb.append(s);
						sb.append("\n");
					}
					eb.addField(mcs.getServername() + " / " + mcs.getServerId(), sb.toString(), false);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eb.build();
	}
	
	private MCServer getServerDetails(String server) {
		MCServer mcs = null;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM mc_serverstats WHERE servername = ?");
			ps.setString(1, server);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				String servername = rs.getString("servername");
				String displayname = rs.getString("displayname");
				String version = rs.getString("version");
				String requiredJoinlevel = rs.getString("req_joinlevel");
				String minigameType = rs.getString("minigameType");
				String minigameMapName = rs.getString("mg_mapName");
				String tps = rs.getString("tps");
				
				int serverId = rs.getInt("serverid");
				int currentPlayers = rs.getInt("currentPlayers");
				int currentStaffs = rs.getInt("currentStaffs");
				int maxPlayers = rs.getInt("maxPlayers");
				int playerCapacity = rs.getInt("playerCapacity");
				int ram_usage = rs.getInt("ram_usage");
				int ram_alloc = rs.getInt("ram_alloc");
				int minigameMaxSlots = rs.getInt("mg_maxSlots");
				int maxHomes = rs.getInt("maxHomes");
				int mapPort = rs.getInt("mapPort");
				
				boolean isStaffOnly = rs.getBoolean("isStaffOnly");
				boolean isOnline = rs.getBoolean("isOnline");
				boolean isMonitored = rs.getBoolean("isMonitored");
				boolean isLocked = rs.getBoolean("isLocked");
				boolean isHybrid = rs.getBoolean("isHybrid");
				boolean isHiddenAPI = rs.getBoolean("isHiddenAPI");
				boolean isHiddenGame = rs.getBoolean("isHiddenGame");
				boolean isMinigame = rs.getBoolean("isMinigame");
				boolean allowInvSync = rs.getBoolean("allowInvSync");
				boolean hasDynmap = rs.getBoolean("hasDynmap");
				boolean hasJobs = rs.getBoolean("hasJobs");
				
				long lastUpdated = rs.getLong("lastUpdated");
				long onlineSince = rs.getLong("onlineSince");
				
				mcs = new MCServer(servername, displayname, version, requiredJoinlevel, minigameType, minigameMapName, tps, serverId, currentPlayers, currentStaffs, maxPlayers, playerCapacity, ram_usage, ram_alloc, minigameMaxSlots, maxHomes, mapPort, isStaffOnly, isOnline, isMonitored, isLocked, isHybrid, isHiddenAPI, isHiddenGame, isMinigame, allowInvSync, hasDynmap, hasJobs, onlineSince, lastUpdated);
				rs.close();
				ps.close();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return mcs;
	}
}