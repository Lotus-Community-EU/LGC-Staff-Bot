//Created by Maurice H. at 02.08.2024
package eu.lotusgaming.bot.handlers.modlog.category;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import eu.lotusgaming.bot.main.LotusManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateIconEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleEvents extends ListenerAdapter {
	
	// Will include: RoleCreate, RoleDelete, RoleUpdateColor, RoleUpdateName, RoleUpdateIcon
	
	@Override
	public void onRoleCreate(RoleCreateEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Created a new Role");
		eb.setDescription("Role Name: " + event.getRole().getAsMention() + " \nRole ID: " + event.getRole().getIdLong());
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onRoleDelete(RoleDeleteEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Deleted a Role");
		eb.setDescription("Role Name: " + event.getRole().getAsMention() + " \nRole ID: " + event.getRole().getIdLong());
		eb.setThumbnail(event.getRole().getIcon().getIconUrl());
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onRoleUpdateColor(RoleUpdateColorEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Updated Role Color");
		eb.setDescription("Role Name: " + event.getRole().getAsMention() + " \nRole Old Color: " + hexColor(event.getOldColorRaw()) + "\nRole New Color: " + hexColor(event.getNewColorRaw()));
		File compareImage = compareColors(event.getOldColor(), event.getNewColor(), event.getRole().getIdLong());
		eb.setThumbnail("attachment://colorComparison.png");
		eb.setColor(ModlogController.green);
		ModlogController.sendMessageWithFile(eb, guild, compareImage, "colorComparison.png");
	}
	
	@Override
	public void onRoleUpdateName(RoleUpdateNameEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Updated Role Name");
		eb.setDescription("Role Name: " + event.getRole().getAsMention() + " \nRole ID: " + event.getOldName());
		eb.setThumbnail(event.getRole().getIcon().getIconUrl());
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	@Override
	public void onRoleUpdateIcon(RoleUpdateIconEvent event) {
		Guild guild = event.getGuild();
		EmbedBuilder eb = ModlogController.baseEmbed(guild);
		eb.setTitle("Updated Role Name");
		eb.setDescription("Role Name: " + event.getRole().getAsMention());
		eb.setThumbnail(event.getNewIcon().getIconUrl());
		eb.setColor(ModlogController.green);
		ModlogController.sendMessage(eb, guild);
	}
	
	private File compareColors(Color colorOld, Color colorNew, long roleId) {
		int width = 512;
		int height = 512;
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2d = image.createGraphics();
		
		//first half
		g2d.setColor(colorOld);
		g2d.fillRect(0, 0, width / 2, height);
		
		//second half
		g2d.setColor(colorNew);
		g2d.fillRect(width / 2, 0, width / 2, height);
		
		g2d.dispose();
		
		File file = new File(LotusManager.configFolderName + "/tmp/colComp_" + roleId + ".png");
		file.mkdir();
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private String hexColor(int color) {
		return String.format("#%06X", (0xFFFFFF & color)).toLowerCase();
	}

}