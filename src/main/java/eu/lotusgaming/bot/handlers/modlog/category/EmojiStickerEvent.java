//Created by Maurice H. at 02.08.2024
package eu.lotusgaming.bot.handlers.modlog.category;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmojiStickerEvent extends ListenerAdapter {
	
	/*
	 * will include
	 *  Emoji: EmojiAdd, EmojiRemoved, EmojiUpdateName
	 *  Sticker: GuildStickerAdded, GuildStickerRemoved, GuildStickerUpdateDescription, GuildStickerUpdateName
	 */
	
	@Override
	public void onEmojiAdded(EmojiAddedEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Serveremoji has been added.");
		eb.setThumbnail(event.getEmoji().getImageUrl());
		eb.setDescription("Emoji Name: " + event.getEmoji().getName());
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onEmojiRemoved(EmojiRemovedEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Serveremoji has been removed.");
		eb.setThumbnail(event.getEmoji().getImageUrl());
		eb.setDescription("Emoji Name: " + event.getEmoji().getName());
		eb.setColor(ModlogController.red);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onEmojiUpdateName(EmojiUpdateNameEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Serveremoji has been updated");
		eb.setThumbnail(event.getEmoji().getImageUrl());
		eb.setDescription("Old Emoji Name: " + event.getOldName());
		eb.setColor(ModlogController.red);
		ModlogController.sendMessage(eb, guild);
	}

}