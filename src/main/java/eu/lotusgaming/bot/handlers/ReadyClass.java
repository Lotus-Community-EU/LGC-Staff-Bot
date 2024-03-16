package eu.lotusgaming.bot.handlers;

import java.util.Timer;

import eu.lotusgaming.bot.command.PunishmentsCommands;
import eu.lotusgaming.bot.command.TicketSCommands;
import eu.lotusgaming.bot.main.LotusManager;
import eu.lotusgaming.bot.main.Main;
import eu.lotusgaming.bot.misc.InfoUpdater;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyClass extends ListenerAdapter{
	
	public void onReady(ReadyEvent event) {
		Main.logger.info("Bot is now online as " + event.getJDA().getSelfUser().getName());
		LotusManager lm = new LotusManager();
		lm.init(event.getJDA());
		TicketSCommands.loadLastTicketId();
		PunishmentsCommands.loadLastPunishmentId();
		CommandAdder.addCommands(event.getJDA());
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new InfoUpdater(event.getJDA()), 1000, 1000*60);
		timer.scheduleAtFixedRate(new UserCounter(event.getJDA()), 1000, 1000*3600);
		timer.scheduleAtFixedRate(new Restarter(), 0, 1000);
		InfoUpdater.setOnlineStatus(true);
	}

}
