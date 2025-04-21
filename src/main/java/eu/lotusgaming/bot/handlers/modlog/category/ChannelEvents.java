//Created by Maurice H. at 21.04.2025
package eu.lotusgaming.bot.handlers.modlog.category;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateBitrateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateRegionEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateSlowmodeEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTopicEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateUserLimitEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelEvents extends ListenerAdapter{
	
	/*
	 *  * ChannelCreateEvent, * ChannelDeleteEvent, * ChannelUpdateBitrateEvent
	 *  * ChannelUpdateNameEvent, * ChannelUpdateNSFWEvent, * ChannelUpdateParentEvent
	 *  * ChannelUpdateRegionEvent, * ChannelUpdateSlowmodeEvent, * ChannelUpdateTopicEvent
	 *  * ChannelUpdateUserLimitEvent
	 */
	
	@Override
	public void onChannelCreate(ChannelCreateEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Created a new Channel, Type" + event.getChannelType().toString());
		eb.setDescription("Channel Name: " + event.getChannel().getAsMention() + " \nChannel ID: " + event.getChannel().getIdLong());
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onChannelDelete(ChannelDeleteEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Deleted a Channel, Type" + event.getChannelType().toString());
		eb.setDescription("Channel Name: " + event.getChannel().getName() + " \nChannel ID: " + event.getChannel().getIdLong());
		eb.setColor(ModlogController.red);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onChannelUpdateBitrate(ChannelUpdateBitrateEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Updated a Channel's Bitrate");
		eb.setDescription("Channel Name: " + event.getChannel().getName() + " \nOld Value: " + event.getOldValue() + "\nNew Value: " + event.getNewValue());
		eb.setColor(ModlogController.yellow);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onChannelUpdateName(ChannelUpdateNameEvent event) {
		Guild guild = event.getGuild();
        EmbedBuilder eb = ModlogController.baseEmbed(guild);
        eb.setTitle("Updated a Channel's Name");
        eb.setDescription("Channel Name: " + event.getChannel().getAsMention() + " \nOld Value: " + event.getOldValue() + "\nNew Value: " + event.getNewValue());
        eb.setColor(ModlogController.yellow);
        ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onChannelUpdateNSFW(ChannelUpdateNSFWEvent event) {
		Guild guild = event.getGuild();
        EmbedBuilder eb = ModlogController.baseEmbed(guild);
        eb.setTitle("Updated a Channel's NSFW Status");
        eb.setDescription("Channel Name: " + event.getChannel().getAsMention() + " \nOld Value: " + event.getOldValue() + "\nNew Value: " + event.getNewValue());
        eb.setColor(ModlogController.yellow);
        ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onChannelUpdateParent(ChannelUpdateParentEvent event) {
		Guild guild = event.getGuild();
        EmbedBuilder eb = ModlogController.baseEmbed(guild);
        eb.setTitle("Updated a Channel's Parent Category");
        eb.setDescription("Channel Name: " + event.getChannel().getAsMention() + " \nOld Value: " + event.getOldValue().getName() + "\nNew Value: " + event.getNewValue().getName());
        eb.setColor(ModlogController.yellow);
        ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onChannelUpdateRegion(ChannelUpdateRegionEvent event) {
		Guild guild = event.getGuild();
        EmbedBuilder eb = ModlogController.baseEmbed(guild);
        eb.setTitle("Updated a Channel's Region");
        eb.setDescription("Channel Name: " + event.getChannel().getAsMention() + " \nOld Value: " + event.getOldValue().getName() + "\nNew Value: " + event.getNewValue().getName());
        eb.setColor(ModlogController.yellow);
        ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onChannelUpdateSlowmode(ChannelUpdateSlowmodeEvent event) {
		Guild guild = event.getGuild();
        EmbedBuilder eb = ModlogController.baseEmbed(guild);
        eb.setTitle("Updated a Channel's Slowmode");
        eb.setDescription("Channel Name: " + event.getChannel().getAsMention() + " \nOld Value: " + event.getOldValue() + "s \nNew Value: " + event.getNewValue() + "s");
        eb.setColor(ModlogController.yellow);
        ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onChannelUpdateTopic(ChannelUpdateTopicEvent event) {
		Guild guild = event.getGuild();
        EmbedBuilder eb = ModlogController.baseEmbed(guild);
        eb.setTitle("Updated a Channel's Topic");
        eb.setDescription("Channel Name: " + event.getChannel().getAsMention() + " \nOld Value: " + event.getOldValue() + "\nNew Value: " + event.getNewValue());
        eb.setColor(ModlogController.yellow);
        ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onChannelUpdateUserLimit(ChannelUpdateUserLimitEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Updated a Channel's User Limit");
		eb.setDescription("Channel Name: " + event.getChannel().getAsMention() + " \nOld Value: " + event.getOldValue()+ "\nNew Value: " + event.getNewValue());
		eb.setColor(ModlogController.yellow);
		ModlogController.sendMessage(eb, guild);
	}
}