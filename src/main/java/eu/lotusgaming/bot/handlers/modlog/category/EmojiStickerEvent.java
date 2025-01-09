//Created by Maurice H. at 02.08.2024
package eu.lotusgaming.bot.handlers.modlog.category;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateNameEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerRemovedEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateNameEvent;
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
		eb.setDescription("Old Emoji Name: " + event.getOldName() + "\nNew Emoji Name: " + event.getNewName());
		eb.setColor(ModlogController.yellow);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildStickerAdded(GuildStickerAddedEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Serversticker has been added.");
		eb.setThumbnail(event.getSticker().getIcon().getUrl());
		eb.setDescription("Sticker Name: " + event.getSticker().getName());
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildStickerRemoved(GuildStickerRemovedEvent event) {
		Guild guild = event.getGuild();
        EmbedBuilder eb = ModlogController.baseEmbed(guild);
        eb.setTitle("Serversticker has been removed.");
        eb.setThumbnail(event.getSticker().getIcon().getUrl());
        eb.setDescription("Sticker Name: " + event.getSticker().getName());
        eb.setColor(ModlogController.red);
        ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildStickerUpdateDescription(GuildStickerUpdateDescriptionEvent event) {
		Guild guild = event.getGuild();
        EmbedBuilder eb = ModlogController.baseEmbed(guild);
        eb.setTitle("Serversticker has been updated.");
        eb.setThumbnail(event.getSticker().getIcon().getUrl());
        eb.setDescription("Old Sticker Description: " + event.getOldValue() + "\nNew Sticker Description: " + event.getNewValue());
        eb.setColor(ModlogController.yellow);
        ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onGuildStickerUpdateName(GuildStickerUpdateNameEvent event) {
		Guild guild = event.getGuild();
        EmbedBuilder eb = ModlogController.baseEmbed(guild);
        eb.setTitle("Serversticker has been updated.");
        eb.setThumbnail(event.getSticker().getIcon().getUrl());
        eb.setDescription("Old Sticker Name: " + event.getOldValue() + "\nNew Sticker Name: " + event.getNewValue());
        eb.setColor(ModlogController.yellow);
        ModlogController.sendMessage(eb, guild);
	}
}