package eu.lotusgaming.bot.handlers;

import eu.lotusgaming.bot.command.TicketSCommands;
import eu.lotusgaming.bot.main.LotusManager;
import eu.lotusgaming.bot.main.Main;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyClass extends ListenerAdapter{
	
	public void onReady(ReadyEvent event) {
		Main.logger.info("Bot is now online as " + event.getJDA().getSelfUser().getName());
		LotusManager lm = new LotusManager();
		lm.init(event.getJDA());
		TicketSCommands.loadLastTicketId();
		CommandAdder.addCommands(event.getJDA());
	}

}
