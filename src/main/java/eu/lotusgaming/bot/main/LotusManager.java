package eu.lotusgaming.bot.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.bot.command.PunishmentsCommands;
import eu.lotusgaming.bot.command.SayCommand;
import eu.lotusgaming.bot.command.SetInfoCommand;
import eu.lotusgaming.bot.command.SetRulesCommand;
import eu.lotusgaming.bot.command.SuggestionBoard;
import eu.lotusgaming.bot.command.TicketSCommands;
import eu.lotusgaming.bot.handlers.Welcomer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;

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
		jda.addEventListener(new SetRulesCommand());
		jda.addEventListener(new SetInfoCommand());
		jda.addEventListener(new PunishmentsCommands());
		//jda.addEventListener(new AutomodHandler()); Deactivated until a proper system has been developed.
		jda.addEventListener(new SayCommand());
		
		Main.logger.info("Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
		displayLogo(jda);
	}
	
	public void postInit() {
		long current = System.currentTimeMillis();
		
		Main.logger.info("Post-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	private void displayLogo(JDA jda) {
		Main.logger.info("##################################################");
		Main.logger.info("#                                                #");
		Main.logger.info("#  ###              #########       #########    #");
		Main.logger.info("#  ###             ###########     ###########   #");
		Main.logger.info("#  ###            ###       ###   ###       ###  #");
		Main.logger.info("#  ###            ###             ###            #");
		Main.logger.info("#  ###            ###             ###            #");
		Main.logger.info("#  ###            ###      #####  ###            #");
		Main.logger.info("#  ###            ###      #####  ###            #");
		Main.logger.info("#  ###            ###        ###  ###       ###  #");
		Main.logger.info("#  #############   #############   ###########   #");
		Main.logger.info("#  #############    ###########     #########    #");
		Main.logger.info("#                                                #");
		Main.logger.info("##################################################");
		Main.logger.info("#                                                #");
		Main.logger.info("#  Date: " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()) + "                              #");
		Main.logger.info("#  Time: " + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "                                #");
		Main.logger.info("#  Java Version: v" + System.getProperty("java.version") + "                        #");
		Main.logger.info("#  JDA Version: v" + JDAInfo.VERSION + "                       #");
		Main.logger.info("#                                                #");
		Main.logger.info("##################################################");
	}

}
