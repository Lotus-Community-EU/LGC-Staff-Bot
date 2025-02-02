//Created by Chris Wille at 15.02.2024
package eu.lotusgaming.bot.handlers;

import java.awt.Color;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import eu.lotusgaming.bot.main.Main;
import eu.lotusgaming.bot.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class Restarter extends TimerTask{
	
	JDA jda;
	public Restarter(JDA jda) {
		this.jda = jda;
	}
	
	private static long guildId = 1153419306789507125L;
	private static long birthdayRoleId = 1224426533851238442L;

	@Override
	public void run() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(new java.util.Date());
		if(time.equals("03:00:00")) {
			System.exit(1);
		}else if(time.equals("12:14:00")) {
			checkAndAssignBirthdayRoles();
			removeExpiredBirthdayRoles();
		}
	}
	
	private void checkAndAssignBirthdayRoles() {
        LocalDate today = LocalDate.now();
        List<Member> list = new ArrayList<>();
        Guild target = jda.getGuildById(guildId);
        try (PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT user_id FROM bot_s_birthdays WHERE birthday = ?")) {
            ps.setString(1, today.format(DateTimeFormatter.ofPattern("dd/MM")));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long userId = rs.getLong("user_id");
                Main.logger.info("checkAndAssignBirthdayRoles() -> long userId: " + userId);
                if(jda.getUserById(userId) == null) return;
                Main.logger.info("checkAndAssignBirthdayRoles() -> User is not null!");
                if(target.isMember(jda.getUserById(userId))) {
                	Main.logger.info("checkAndAssignBirthdayRoles() -> User is Member on Server!");
                	target.addRoleToMember(jda.getUserById(userId), target.getRoleById(birthdayRoleId)).queue();
                	list.add(target.getMember(jda.getUserById(userId)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#ac1cfe"));
        eb.setTitle("Happy Birthday!");
        if(list.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
		for (Member m : list) {
			sb.append(m.getAsMention()).append(" ");
		}
		if(list.size() >= 2) {
			eb.setDescription("Today, a few fellow members do celebrate their special day! Congratulations to: " + sb.toString() + " 🎉");
		}else {
			eb.setDescription("Today, a fellow member celebrate their special day! Congratulations to " + sb.toString() + "! 🎉");
		}
        target.getTextChannelById(1201229752992809040L).sendMessageEmbeds(eb.build()).queue();
    }

    private void removeExpiredBirthdayRoles() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Guild target = jda.getGuildById(guildId);
        try (PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT user_id FROM bot_s_birthdays WHERE birthday = ?");) {
            ps.setString(1, yesterday.format(DateTimeFormatter.ofPattern("dd/MM")));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	long userId = rs.getLong("user_id");
                    if(jda.getUserById(userId) == null) return;
                    if(target.isMember(jda.getUserById(userId))) {
                    	target.removeRoleFromMember(jda.getUserById(userId), target.getRoleById(birthdayRoleId)).queue();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}