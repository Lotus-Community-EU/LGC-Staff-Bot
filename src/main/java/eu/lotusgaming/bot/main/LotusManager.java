package eu.lotusgaming.bot.main;

import java.io.File;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.bot.command.SuggestionBoard;
import eu.lotusgaming.bot.command.TicketSCommands;
import eu.lotusgaming.bot.handlers.Welcomer;
import net.dv8tion.jda.api.JDA;

public class LotusManager {
	
	public static String configFolderName = "LotusStaffBot";
	public static File configurationFolder = new File(configFolderName);
	public static String mainConfigName = "botconfig.yml";
	public static File mainConfig = new File(configFolderName + "/" + mainConfigName);
	private static File logFolder = new File(configFolderName + "/logs");
	
	//must be initialized before bot startup!
	public void preInit() {
		long current = System.currentTimeMillis();
		
		if(!configurationFolder.exists()) {
			configurationFolder.mkdir();
		}
		if(!logFolder.exists()) {
			logFolder.mkdir();
		}
		if(!mainConfig.exists()) {
			try { mainConfig.createNewFile(); } catch (Exception ex) {}
		}
		
		try {
			YamlFile cfg = YamlFile.loadConfiguration(mainConfig);
			//general bot configuration
			cfg.set("Bot.onlineTime", System.currentTimeMillis());
			cfg.addDefault("Bot.token", "YourBotTokenGoesThere");
			cfg.addDefault("Bot.HashPassword", "Just123A456Password789");
			//mysql logon data
			cfg.addDefault("MySQL.Host", "hostname");
			cfg.addDefault("MySQL.Database", "databaseName");
			cfg.addDefault("MySQL.Username", "username");
			cfg.addDefault("MySQL.Password", "pass123word456");
			cfg.addDefault("MySQL.Port", "3306");
			cfg.addDefault("MySQL.UseSQL", false);
			cfg.options().copyDefaults(true);
			cfg.save();
		}catch (Exception ex) {}
		
		Main.logger.info("Pre-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	
	//Must be initialised with the ReadyEvent!
	public void init(JDA jda) {
		long current = System.currentTimeMillis();
		
		jda.addEventListener(new TicketSCommands());
		jda.addEventListener(new SuggestionBoard());
		jda.addEventListener(new Welcomer());
		
		Main.logger.info("Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	public void postInit() {
		long current = System.currentTimeMillis();
		
		Main.logger.info("Post-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}

}
