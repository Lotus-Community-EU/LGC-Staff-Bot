//Created by Chris Wille at 05.02.2024
package eu.lotusgaming.bot.misc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import eu.lotusgaming.bot.main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class InfoUpdater extends TimerTask{
	
	JDA jda;
	public InfoUpdater(JDA jda) {
		this.jda = jda;
	}

	@Override
	public void run() {
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("UPDATE bot_status SET servingGuilds = ?, servingMembers = ?, lastUpdated = ?, ram_usage = ?, ram_alloc = ?, isOnline = ?, servingUniqueMembers = ? WHERE botKey = ?");
			int guilds = 0;
			int members = 0;
			List<Long> uniqueMembers = new ArrayList<>();
			for(Guild guild : jda.getGuilds()) {
				guilds++;
				for(Member member : guild.getMembers()) {
					members++;
					if(!uniqueMembers.contains(member.getIdLong())) {
						uniqueMembers.add(member.getIdLong());
					}
				}
			}
			ps.setInt(1, guilds);
			ps.setInt(2, members);
			ps.setLong(3, System.currentTimeMillis());
			ps.setString(4, getRAMInfo(RAMInfo.USING));
			ps.setString(5, getRAMInfo(RAMInfo.ALLOCATED));
			ps.setBoolean(6, true);
			ps.setInt(7, uniqueMembers.size());
			ps.setString(8, "staffBot");
			ps.executeUpdate();
			Main.logger.info("InfoUpdater has been triggered.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getRAMInfo(RAMInfo type) {
		String toReturn = "";
		Runtime runtime = Runtime.getRuntime();
		if(type == RAMInfo.ALLOCATED) {
			toReturn = runtime.totalMemory() / 1048576L + "";
		}else if(type == RAMInfo.USING) {
			toReturn = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + "";
		}else if(type == RAMInfo.FREE) {
			toReturn = runtime.freeMemory() / 1048576L + "";
		}
		return toReturn;
	}
	
	public static void setOnlineStatus(boolean status) {
		PreparedStatement ps;
		if(status) {
			try {
				ps = MySQL.getConnection().prepareStatement("UPDATE bot_status SET isOnline = ?, onlineSince = ? WHERE botKey = ?");
				ps.setBoolean(1, status);
				ps.setLong(2, System.currentTimeMillis());
				ps.setString(3, "staffBot");
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {
			try {
				ps = MySQL.getConnection().prepareStatement("UPDATE bot_status SET isOnline = ? WHERE botKey = ?");
				ps.setBoolean(1, status);
				ps.setString(2, "staffBot");
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	enum RAMInfo {
		ALLOCATED,
		USING,
		FREE;
	}
	
}