//Created by Maurice H. at 01.08.2024
package eu.lotusgaming.bot.handlers.modlog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import eu.lotusgaming.bot.main.Main;
import eu.lotusgaming.bot.misc.MySQL;

public class DatabaseMessageTimer extends TimerTask{
	
	// 14 days in MS: 1 209 600 000

	@Override
	public void run() {
		List<Long> overdueIDs = getMarkedAndOverdueMessages();
		if(overdueIDs.size() >= 1) deleteIDFromDisk(overdueIDs);
		
		List<Long> overdueIDs2 = getOverdueMessages();
		if(overdueIDs2.size() >= 32) deleteIDFromDisk(overdueIDs2);
	}
	
	
	private List<Long> getMarkedAndOverdueMessages() {
		List<Long> ids = new ArrayList<>();
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT id FROM bot_s_messagelog WHERE markedAsDeleted = ?");
			ps.setBoolean(1, true);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				ids.add(rs.getLong("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		List<Long> overdueIDs = new ArrayList<>();
		for(long id : ids) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT lastUpdatedTime FROM bot_s_messagelog WHERE id = ?");
				ps.setLong(1, id);
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					long lastUpdated = rs.getLong("lastUpdatedTime");
					long current = System.currentTimeMillis();
					long dist = (current - lastUpdated);
					long overdueTime = 43200000;
					if(overdueTime <= dist) {
						overdueIDs.add(id);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return overdueIDs;
	}
	
	private List<Long> getOverdueMessages() {
		List<Long> overdueIDs = new ArrayList<>();
		try {
			PreparedStatement ps = MySQL.getConnection().prepareStatement("SELECT lastUpdatedTime,id FROM bot_s_messagelog");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				long lastUpdated = rs.getLong("lastUpdatedTime");
				long current = System.currentTimeMillis();
				long id = rs.getLong("id");
				long dist = (current - lastUpdated);
				long overdueTime = 1209600000;
				if(overdueTime <= dist) {
					overdueIDs.add(id);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return overdueIDs;
	}
	
	private void deleteIDFromDisk(List<Long> overdueMessages) {
		Main.logger.info("About to delete " + overdueMessages.size() + " Messages from Disk!");
		for(Long id : overdueMessages) {
			try {
				PreparedStatement ps = MySQL.getConnection().prepareStatement("DELETE FROM bot_s_messagelog WHERE id = ?");
				ps.setLong(1, id);
				ps.executeUpdate();
				Main.logger.info("Removed ID " + id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}