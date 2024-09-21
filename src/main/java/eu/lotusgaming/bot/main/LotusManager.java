package eu.lotusgaming.bot.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.bot.command.LevelSystem;
import eu.lotusgaming.bot.command.PunishmentsCommands;
import eu.lotusgaming.bot.command.PurgeCommand;
import eu.lotusgaming.bot.command.SayCommand;
import eu.lotusgaming.bot.command.SetInfoCommand;
import eu.lotusgaming.bot.command.SetRulesCommand;
import eu.lotusgaming.bot.command.StatusActivityCommand;
import eu.lotusgaming.bot.command.SuggestionBoard;
import eu.lotusgaming.bot.command.TicketSCommands;
import eu.lotusgaming.bot.handlers.Welcomer;
import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import eu.lotusgaming.bot.misc.MySQL;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;

public class LotusManager {
	
	public static String configFolderName = "LotusStaffBot";
	public static File configurationFolder = new File(configFolderName);
	public static String mainConfigName = "botconfig.yml";
	public static File mainConfig = new File(configFolderName + "/" + mainConfigName);
	private static File logFolder = new File(configFolderName + "/logs");
	public static File tmpFolder = new File(configFolderName + "/tmp");
	
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
		if(!tmpFolder.exists()) tmpFolder.mkdir();
		
		List<Long> owners = new ArrayList<>();
		owners.add(228145889988837385L);
		owners.add(342866830656012296L);
		
		try {
			YamlFile cfg = YamlFile.loadConfiguration(mainConfig);
			//general bot configuration
			cfg.set("Bot.onlineTime", System.currentTimeMillis());
			cfg.addDefault("Bot.token", "YourBotTokenGoesThere");
			cfg.addDefault("Bot.HashPassword", "Just123A456Password789");
			cfg.addDefault("Bot.Owners", owners);
			cfg.addDefault("Bot.Activity.Onlinestatus", "IDLE");
			cfg.addDefault("Bot.Activity.Type", "WATCHING");
			cfg.addDefault("Bot.Activity.Text", "my capabilities grow.");
			//webspace logon data
			cfg.addDefault("FTP.Host", "127.0.0.1");
			cfg.addDefault("FTP.Port", 21);
			cfg.addDefault("FTP.Username", "user");
			cfg.addDefault("FTP.Password", "pass");
			cfg.addDefault("FTP.enabled", false);
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
		ModlogController.registerClasses(jda);
		jda.addEventListener(new PurgeCommand());
		jda.addEventListener(new StatusActivityCommand());
		jda.addEventListener(new LevelSystem());
		
		Main.logger.info("Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
		displayLogo(jda);
	}
	
	public void postInit() {
		long current = System.currentTimeMillis();
		
		Main.logger.info("Post-Initialisation took " + (System.currentTimeMillis() - current) + "ms.");
	}
	
	private void displayLogo(JDA jda) {
		System.out.println("##################################################");
		System.out.println("#                                                #");
		System.out.println("#  ###              #########       #########    #");
		System.out.println("#  ###             ###########     ###########   #");
		System.out.println("#  ###            ###       ###   ###       ###  #");
		System.out.println("#  ###            ###             ###            #");
		System.out.println("#  ###            ###             ###            #");
		System.out.println("#  ###            ###      #####  ###            #");
		System.out.println("#  ###            ###      #####  ###            #");
		System.out.println("#  ###            ###        ###  ###       ###  #");
		System.out.println("#  #############   #############   ###########   #");
		System.out.println("#  #############    ###########     #########    #");
		System.out.println("#                                                #");
		System.out.println("##################################################");
		System.out.println("#                                                #");
		System.out.println("#  Date: " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()) + "                              #");
		System.out.println("#  Time: " + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "                                #");
		System.out.println("#  Java Version: v" + System.getProperty("java.version") + "                        #");
		System.out.println("#  JDA Version: v" + JDAInfo.VERSION_MAJOR + "." + JDAInfo.VERSION_MINOR + "." + JDAInfo.VERSION_REVISION + "                           #");
		if(MySQL.isConnected()) {
			System.out.println("#  MySQL Connected: yes                          #");
		}else {
			System.out.println("#  MySQL Connected: no                           #");
		}
		System.out.println("#                                                #");
		System.out.println("##################################################");
	}

}
