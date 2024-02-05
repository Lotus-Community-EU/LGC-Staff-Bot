package eu.lotusgaming.bot.main;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.simpleyaml.configuration.file.YamlFile;

import eu.lotusgaming.bot.handlers.ReadyClass;
import eu.lotusgaming.bot.misc.InfoUpdater;
import eu.lotusgaming.bot.misc.MySQL;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
	
	public static Logger logger;

	public static void main(String[] args) {
		logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.ALL);
		
		LotusManager lm = new LotusManager();
		lm.preInit();
		
		YamlFile cfg = null;
		try {
			cfg = YamlFile.loadConfiguration(LotusManager.mainConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
		configLogger();
		if(cfg != null) {
			connectSQL(cfg);
			startBot(cfg);
		}else {
			System.out.println("Error! Shutting down bot.");
			System.exit(0);
		}
		enableShutdownHook();
	}
	
	private static void startBot(YamlFile cfg) {
		JDABuilder builder = JDABuilder.createDefault(cfg.getString("Bot.token"));
		builder.enableIntents(GatewayIntent.GUILD_MODERATION, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS);
		builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOJI, CacheFlag.FORUM_TAGS, CacheFlag.ONLINE_STATUS, CacheFlag.SCHEDULED_EVENTS, CacheFlag.VOICE_STATE);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.addEventListeners(new ReadyClass());
		builder.setActivity(Activity.watching("my capabilities grow."));
		builder.setStatus(OnlineStatus.IDLE);
		builder.build();
	}
	
	private static void connectSQL(YamlFile cfg) {
		if(cfg.getBoolean("MySQL.UseSQL")) {
			logger.info("Bot is using SQL!");
			try {
				MySQL.connect(cfg.getString("MySQL.Host"), cfg.getString("MySQL.Port"), cfg.getString("MySQL.Database"), cfg.getString("MySQL.Username"), cfg.getString("MySQL.Password"));
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}else {
			logger.info("Bot is not using SQL!");
		}
	}
	
	private static void configLogger() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		String date = sdf.format(new Date());
		try {
			FileHandler fileHandler = new FileHandler(LotusManager.configFolderName + "/logs/log-" + date + ".txt");
			fileHandler.setFormatter(new SimpleFormatter());
			
			ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setLevel(Level.ALL);
			
			logger.addHandler(fileHandler);
			//logger.addHandler(consoleHandler);
			
			logger.setLevel(Level.ALL);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void closeLogger() {
		for(Handler handler : logger.getHandlers()) {
			handler.close();
		}
	}
	
	private static void enableShutdownHook() {
		Thread printingHook = new Thread(() -> {
			InfoUpdater.setOnlineStatus(false);
			MySQL.disconnect();
			logger.info("Bot is in shutdownprogress, byebye...");
			closeLogger();
		});
		Runtime.getRuntime().addShutdownHook(printingHook);
	}
}