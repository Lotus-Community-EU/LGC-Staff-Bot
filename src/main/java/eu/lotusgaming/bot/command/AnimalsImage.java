//Created by Maurice H. at 21.04.2025
package eu.lotusgaming.bot.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AnimalsImage extends ListenerAdapter{
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if(event.getName().equals("cat")) {
			JsonObject jo = retrieveAPI("https://cataas.com/cat?json=true");
			String imageUrl = jo.get("url").getAsString();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(ModlogController.green);
			eb.setImage(imageUrl);
			event.replyEmbeds(eb.build()).queue();
		}else if(event.getName().equals("dog")) {
			JsonObject jo = retrieveAPI("https://random.dog/woof.json");
			String imageUrl = jo.get("url").getAsString();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(ModlogController.green);
			eb.setImage(imageUrl);
			event.replyEmbeds(eb.build()).queue();
		}else if(event.getName().equals("fox")) {
			JsonObject jo = retrieveAPI("https://randomfox.ca/floof/?utm_source=JSON%20API%20App&utm_medium=referral&utm_campaign=RandomFox&utm_term=RandomFox");
			String imageUrl = jo.get("image").getAsString();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(ModlogController.green);
			eb.setImage(imageUrl);
			event.replyEmbeds(eb.build()).queue();
		}
	}
	
	JsonObject retrieveAPI(String inputURL) {
		try {
			URL url = new URL(inputURL);
			URLConnection conn = url.openConnection();
			conn.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine = "";
			String jsonS = "";
			while((inputLine = in.readLine()) != null) {
				jsonS += inputLine;
			}
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(jsonS, JsonObject.class);
			return jsonObject;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}