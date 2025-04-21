//Created by Maurice H. at 13.01.2025
package eu.lotusgaming.bot.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;

import eu.lotusgaming.bot.misc.CustomCommand;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CustomCommandHandler extends ListenerAdapter {
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		String command = event.getName();
		if(command.equals("customcommands")) {
			String subcommand = event.getSubcommandName();
			if(subcommand.equals("add")) {
				Attachment attachment = event.getOption("commanddata").getAsAttachment();
				if(attachment.getFileExtension().equals("json")) {
					//File file = new File(attachment.getFileName() + "." + attachment.getFileExtension());
					//public static CustomCommand parseCommand(attachment.getProxy().downloadToFile(file).join());
				}else {
					event.reply("The file must be a ``.json`` file!").queue();
				}
			}else if(subcommand.equals("remove")) {
				
			}
		}
	}
	
	public static CustomCommand parseCommand(String file) throws IOException {
		String jsonContent = new String(Files.readAllBytes(Paths.get(file)));
		Gson gson = new Gson();
		return gson.fromJson(jsonContent, CustomCommand.class);
	}
}