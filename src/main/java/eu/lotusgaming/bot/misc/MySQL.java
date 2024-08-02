package eu.lotusgaming.bot.misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import eu.lotusgaming.bot.main.Main;

public class MySQL {
	
	private static Connection con;
	
	public static void connect(String host, String port, String db, String user, String pw) throws SQLException, ClassNotFoundException {
		if(!isConnected()) {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true", user, pw);
			Main.logger.info("MySQL has established a connection to " + host + ":" + port + " on " + db);
		}
	}
	
	public static void disconnect() {
		if(isConnected()) {
			try {
				con.close();
				Main.logger.info("MySQL disconnected from database!");
			}catch (SQLException ex) {
				Main.logger.severe("Couldn't disconnect from database...");
				ex.printStackTrace();
			}
		}
	}

	public static boolean isConnected() {
		return (con == null ? false : true);
	}
	
	public static Connection getConnection() {
		if(isConnected()) {
			return con;
		}else {
			try { con.prepareStatement("SELECT * FROM core_ranks"); } catch (SQLException e) { }
			return con;
		}
		
	}

}
