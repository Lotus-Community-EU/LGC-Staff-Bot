//Created by Maurice H. at 22.08.2024
package eu.lotusgaming.bot.handlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TranslateHandler extends ListenerAdapter {
	
	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event) {
		Guild guild = event.getGuild();
		
	}

}

