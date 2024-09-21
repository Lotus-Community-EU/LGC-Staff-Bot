//Created by Maurice H. at 21.09.2024
package eu.lotusgaming.bot.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import eu.lotusgaming.bot.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LevelSystem extends ListenerAdapter {
	
	private final int basePoints = 1;
	private final int pointsPerAttachment = 2;
	private final int pointsPerKeyword = 2;
	private final int levelUpMultiplier = 100;
	private final int spamThresholdMilliseconds = 2000;
	private final List<String> bonusKeywords = new ArrayList<>();
	private final Map<Long, Long> userLastMessageTime = new HashMap<>();
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Guild guild = event.getGuild();
		Member member = event.getMember();
		
		if(event.getName().equals("leaderboard")) {
			int topCount = 10;
			if(event.getOption("top") != null) {
				topCount = event.getOption("top").getAsInt();
				if(topCount >= 26) {
					topCount = 25;
				}
			}
			
			StringBuilder sb = new StringBuilder();
			int i = 0;
			
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_chatlevel WHERE guildId = ? ORDER by level DESC LIMIT " + topCount);
				ps.setLong(1, guild.getIdLong());
				ResultSet rs = ps.executeQuery();
				sb.append(String.format("%-6s | %-24s | %-6s | %-5s%n", "Rank", "Name", "Points", "Level"));
				sb.append("----------------------------------------------\n");
				while(rs.next()) {
					i++;
					Member cachedMember = guild.getMemberById(rs.getLong("memberId"));
					String memberName = "";
					if(cachedMember == null) {
						memberName = "unknown member / " + rs.getLong("memberId");
					}else {
						memberName = cachedMember.getEffectiveName();
					}
					int level = rs.getInt("level");
					int points = rs.getInt("points");
					sb.append(String.format("%02d.    | %-24s | %-6d | %-5d%n", i, truncate(memberName, 20), points, level));
				}
				sb.append("\nListed the Top " + i + " Chatters from " + guild.getName());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setDescription("```" + sb.toString() + "```");
			eb.setColor(ModlogController.green);
			eb.setTitle("Top " + i + " chatters on " + guild.getName());
			event.replyEmbeds(eb.build()).queue();
		}else if(event.getName().equals("level")) {
			
			User target = null;
			if(event.getOption("member") != null) {
				target = event.getOption("member").getAsUser();
			}else {
				target = member.getUser();
			}
			
			int points = getCurrentPoints(guild, target);
			int currentLevel = getCurrentLevel(guild, target);
			int nextLevel = currentLevel + 1;
			int nextLevelPoints = getPointsForNextLevel(nextLevel);
			int currentLevelPoints = getPointsForCurrentLevel(currentLevel);
			
			int pointsIntoCurrentLevel = points - currentLevelPoints;
			int pointsNeededForNextLevel = nextLevelPoints - currentLevelPoints;
			double progress = (double) pointsIntoCurrentLevel / pointsNeededForNextLevel * 100;
			String progressbar = generateProgressBar(progress);
			
			List<String> list = new ArrayList<>();
			list.add(target.getEffectiveName() + " is currently on Level " + currentLevel + ".");
			list.add(currentLevel + " " + progressbar + " " + + nextLevel);
			StringBuilder sb = new StringBuilder();
			for(String s : list) {
				sb.append(s);
				sb.append("\n");
			}
			String result = sb.toString();
			
			event.reply(result).queue();
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.isFromGuild()) {
			Guild guild = event.getGuild();
			Member member = event.getMember();
			
			if(member.getUser().isBot()) return;
			if(member.getIdLong() == 1203717266990960760L) return;
			
			addUserIfNotExists(guild, member.getUser());
			
			long currentTime = System.currentTimeMillis();
			if(userLastMessageTime.containsKey(member.getIdLong())) {
				long lastMessageTime = userLastMessageTime.get(member.getIdLong());
				if(currentTime - lastMessageTime < spamThresholdMilliseconds) {
					return;
				}
			}
			userLastMessageTime.put(member.getIdLong(), currentTime);
			
			int points = basePoints;
			int attachmentPoints = pointsPerAttachment * event.getMessage().getAttachments().size();
			int boniKeyWrds = 0;
			for(@SuppressWarnings("unused") String s : bonusKeywords) {
				boniKeyWrds += pointsPerKeyword;
			}
			points = points + attachmentPoints;
			points = points + boniKeyWrds;
			int currentPoints = getCurrentPoints(guild, member.getUser());
			int newPoints = currentPoints + points;
			setNewPoints(guild, member.getUser(), newPoints);
			
			int currentLevel = getCurrentLevel(guild, member.getUser());
			int nextLevelPoints = getPointsForNextLevel(currentLevel + 1);
			if(getCurrentPoints(guild, member.getUser()) >= nextLevelPoints) {
				setNewLevel(guild, member.getUser(), currentLevel + 1);
				//TODO send template with Username, Avatar and new Level into channel - autodelete after 20 seconds
				event.getChannel().sendMessage(member.getAsMention() + "leveled up to Level " + (currentLevel + 1) + "!").queue(rA -> {
					rA.delete().queueAfter(20, TimeUnit.SECONDS);
				});
			}
		}
	}
	
	private String truncate(String name, int length) {
		if(name.length() > length) {
			return name.substring(0, length - 3) + "...";
		}
		return name;
	}
	
	public void addUserIfNotExists(Guild guild, User member) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_chatlevel WHERE guildId = ? AND memberId = ?");
			ps.setLong(1, guild.getIdLong());
			ps.setLong(2, member.getIdLong());
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) {
				PreparedStatement ps1 = MySQL.getConnection().prepareStatement("INSERT INTO bot_s_chatlevel (guildId,memberId,points,level) VALUES (?,?,?,?)");
				ps1.setLong(1, guild.getIdLong());
				ps1.setLong(2, member.getIdLong());
				ps1.setInt(3, 1);
				ps1.setInt(4, 0);
				ps1.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getCurrentPoints(Guild guild, User member) {
		int points = 0;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_chatlevel WHERE guildId = ? AND memberId = ?");
			ps.setLong(1, guild.getIdLong());
			ps.setLong(2, member.getIdLong());
			ResultSet rs = ps.executeQuery();
			rs.next();
			points = rs.getInt("points");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return points;
	}
	
	public int getCurrentLevel(Guild guild, User member) {
		int level = 0;
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * FROM bot_s_chatlevel WHERE guildId = ? AND memberId = ?");
			ps.setLong(1, guild.getIdLong());
			ps.setLong(2, member.getIdLong());
			ResultSet rs = ps.executeQuery();
			rs.next();
			level = rs.getInt("level");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return level;
	}
	
	public void setNewPoints(Guild guild, User member, int points) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE bot_s_chatlevel SET points = ? WHERE guildId = ? AND memberId = ?");
			ps.setInt(1, points);
			ps.setLong(2, guild.getIdLong());
			ps.setLong(3, member.getIdLong());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setNewLevel(Guild guild, User member, int level) {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE bot_s_chatlevel SET level = ? WHERE guildId = ? AND memberId = ?");
			ps.setInt(1, level);
			ps.setLong(2, guild.getIdLong());
			ps.setLong(3, member.getIdLong());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getPointsForCurrentLevel(int level) {
		switch(level) {
		case 1: return 0;
		case 2: return 25;
		case 3: return 50;
		case 4: return 75;
		case 5: return 100;
		case 6: return 150;
		case 7: return 200;
		case 8: return 250;
		case 9: return 300;
		case 10: return 350;
		case 11: return 400;
		case 12: return 450;
		case 13: return 500;
		case 14: return 600;
		case 15: return 700;
		case 16: return 800;
		case 17: return 900;
		case 18: return 1000;
		case 19: return 1250;
		case 20: return 1500;
		case 21: return 1750;
		case 22: return 2000;
		case 23: return 2250;
		case 24: return 2500;
		case 25: return 2750;
		case 26: return 3000;
		case 27: return 3250;
		case 28: return 3500;
		case 29: return 3750;
		case 30: return 4000;
		case 31: return 4250;
		case 32: return 4500;
		case 33: return 4750;
		case 34: return 5000;
		case 35: return 5500;
		case 36: return 6000;
		case 37: return 6500;
		case 38: return 7000;
		case 39: return 7500;
		case 40: return 8000;
		case 41: return 8500;
		case 42: return 9000;
		case 43: return 9500;
		case 44: return 10000;
		case 45: return 11000;
		case 46: return 12000;
		case 47: return 13000;
		case 48: return 14000;
		case 49: return 15000;
		case 50: return 16000;
		case 51: return 17000;
		case 52: return 18000;
		case 53: return 19000;
		case 54: return 20000;
		case 55: return 22000;
		case 56: return 24000;
		case 57: return 26000;
		case 58: return 28000;
		case 59: return 30000;
		default: return 0;
		}
	}
	
	public int getPointsForNextLevel(int level) {
		switch(level) {
		case 2: return 25;
		case 3: return 50;
		case 4: return 75;
		case 5: return 100;
		case 6: return 150;
		case 7: return 200;
		case 8: return 250;
		case 9: return 300;
		case 10: return 350;
		case 11: return 400;
		case 12: return 450;
		case 13: return 500;
		case 14: return 600;
		case 15: return 700;
		case 16: return 800;
		case 17: return 900;
		case 18: return 1000;
		case 19: return 1250;
		case 20: return 1500;
		case 21: return 1750;
		case 22: return 2000;
		case 23: return 2250;
		case 24: return 2500;
		case 25: return 2750;
		case 26: return 3000;
		case 27: return 3250;
		case 28: return 3500;
		case 29: return 3750;
		case 30: return 4000;
		case 31: return 4250;
		case 32: return 4500;
		case 33: return 4750;
		case 34: return 5000;
		case 35: return 5500;
		case 36: return 6000;
		case 37: return 6500;
		case 38: return 7000;
		case 39: return 7500;
		case 40: return 8000;
		case 41: return 8500;
		case 42: return 9000;
		case 43: return 9500;
		case 44: return 10000;
		case 45: return 11000;
		case 46: return 12000;
		case 47: return 13000;
		case 48: return 14000;
		case 49: return 15000;
		case 50: return 16000;
		case 51: return 17000;
		case 52: return 18000;
		case 53: return 19000;
		case 54: return 20000;
		case 55: return 22000;
		case 56: return 24000;
		case 57: return 26000;
		case 58: return 28000;
		case 59: return 30000;
		default: return 0;
		}
	}
	
	private String generateProgressBar(double progress) {
		int totalBars = 20;
		int filledBars = (int) (totalBars * (progress / 100));
		StringBuilder sb = new StringBuilder("[");
		
		for(int i = 0; i < filledBars; i++) {
			sb.append("█");
		}
		for(int i = filledBars; i < totalBars; i++) {
			sb.append("░");
		}
		sb.append("]");
		return sb.toString();
	}
}