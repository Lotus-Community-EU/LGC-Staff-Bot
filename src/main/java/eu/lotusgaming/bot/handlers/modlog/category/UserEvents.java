//Created by Christopher at 16.07.2024
package eu.lotusgaming.bot.handlers.modlog.category;

import java.util.List;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserEvents extends ListenerAdapter {
	
	@Override
	public void onUserUpdateAvatar(UserUpdateAvatarEvent event) {
		List<Guild> guilds = event.getUser().getMutualGuilds();
		User user = event.getUser();
		if(!guilds.isEmpty() && !user.isBot()) {
			for(Guild guild : guilds) {
				EmbedBuilder eb = ModlogController.baseEmbed(guild);
				eb.setTitle(user.getName() + " has updated their avatar.");
				if(event.getNewAvatarUrl() != null) {
					eb.setImage(event.getNewAvatarUrl());
				}else {
					eb.setImage(user.getDefaultAvatarUrl());
				}
				eb.setColor(ModlogController.green);
				ModlogController.sendMessage(eb, guild);
			}
		}
	}
	
	@Override
	public void onUserUpdateName(UserUpdateNameEvent event) {
		User user = event.getUser();
		List<Guild> guilds = user.getMutualGuilds();
		if(!guilds.isEmpty() && !user.isBot()) {
			for(Guild guild : guilds) {
				EmbedBuilder eb = ModlogController.baseEmbed(guild);
				eb.setTitle(user.getName() + " has updated their username.");
				eb.setDescription("Old Username: ``" + event.getOldName() + "``\nNew Username: ``" + event.getNewName() + "``");
				eb.setColor(ModlogController.green);
				ModlogController.sendMessage(eb, guild);
			}
		}
	}

}