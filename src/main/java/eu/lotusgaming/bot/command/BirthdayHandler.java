//Created by Maurice H. at 31.01.2025
package eu.lotusgaming.bot.command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.lotusgaming.bot.misc.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BirthdayHandler extends ListenerAdapter {
	
	
	private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
			.parseCaseInsensitive()
			.appendPattern("dd/MM")
			.toFormatter(Locale.ENGLISH)
			.localizedBy(Locale.ENGLISH)
			.withResolverStyle(ResolverStyle.SMART);
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Member member = event.getMember();
		Guild guild = event.getGuild();
		if(event.getName().equals("birthday")) {
			String scn = event.getSubcommandName();
			if(scn.equalsIgnoreCase("set")) {
				String date = event.getOption("date").getAsString();
				MonthDay localDate;
				try {
					localDate = MonthDay.parse(date, formatter);
				}catch (Exception e) {
					e.printStackTrace();
					event.reply("Invalid date format!").setEphemeral(true).queue();
					return;
				}
				
				long userId = member.getIdLong();
				
				try (PreparedStatement ps = MySQL.getConnection().prepareStatement("REPLACE INTO bot_s_birthdays (user_id, birthday) VALUES (?, ?)")) {
					ps.setLong(1, userId);
					ps.setString(2, localDate.format(formatter));
					ps.executeUpdate();
					ps.close();
					event.reply("Your birthday has been set to " + date + "!").setEphemeral(true).queue();
				}catch (SQLException e) {
					e.printStackTrace();
					event.reply("An error occured whilst attempting to save the data. Please contact the developers.").setEphemeral(true).queue();
				}
			}else if(scn.equalsIgnoreCase("remove")) {
				try (PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM bot_s_birthdays WHERE user_id = ?")){
					ps.setLong(1, member.getIdLong());
					ps.executeUpdate();
					event.reply("Your birthday has been removed!").setEphemeral(true).queue();
				}catch (SQLException e) {
					e.printStackTrace();
					event.reply("An error occured whilst attempting to delete the data. Please contact the developers.").setEphemeral(true).queue();
				}
			}else if(scn.equalsIgnoreCase("next")) {
				MonthDay today = MonthDay.now();
				MonthDay sixMonths = MonthDay.from(LocalDate.now().plusMonths(6));
				List<String> birthdayList = new ArrayList<>();
				
				try (PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT * from bot_s_birthdays")) {
					ResultSet rs = ps.executeQuery();
					while(rs.next()) {
						Member m = guild.getMemberById(rs.getLong("user_id"));
						MonthDay birthday = MonthDay.parse(rs.getString("birthday"), formatter);
						if (m != null) {
							if(isBirthdayInRange(today, sixMonths, birthday)) {
								birthdayList.add(m.getAsMention() + " - " + rs.getString("birthday"));
							}
						}
					}
					rs.close();
					ps.close();
				}catch (SQLException e) {
					e.printStackTrace();
					event.reply("An error occured whilst fetching upcoming birthdays!").setEphemeral(true).queue();
				}
				if(birthdayList.isEmpty()) {
					event.reply("No upcoming birthdays in the next 6 months!").queue();
				}else {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle("Upcoming Birthdays");
					eb.setFooter(guild.getName(), guild.getIconUrl());
					eb.setDescription(String.join("\n", birthdayList));
					eb.setColor(member.getColor());
					event.replyEmbeds(eb.build()).queue();
				}
			}
		}
	}
	
	private boolean isBirthdayInRange(MonthDay start, MonthDay end, MonthDay birthday) {
		if(start.isBefore(end)) {
			return !birthday.isBefore(start) && !birthday.isAfter(end);
		}else {
			return !birthday.isBefore(start) || !birthday.isAfter(end);
		}
	}
}