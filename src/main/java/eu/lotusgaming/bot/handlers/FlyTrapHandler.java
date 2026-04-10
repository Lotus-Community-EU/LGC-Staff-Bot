package eu.lotusgaming.bot.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.lotusgaming.bot.handlers.modlog.ModlogController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class FlyTrapHandler extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        if(event.getName().equals("sendflytrapmsg")) {
            if(event.getOption("channel").getAsChannel().getType() == ChannelType.TEXT){
                TextChannel channel = event.getOption("channel").getAsChannel().asTextChannel();
                channel.sendMessage("""
                    # FLY TRAP
                    
                    This channel is used to catch automated spam from compromised accounts.

                    Compromised accounts include but not limited to:
                    - Downloading fake games with malware
                    - Scanning fake verify QR codes
                    - Clicking on fake links for free crypto or NSFW content

                    ***DO NOT*** type here or you will be automatically quarantined for 24 hours.
                    Any user message posted in this channel triggers an automatic timeout and deletion of recent messages across the server.
                    """).queue();
                    event.deferReply(true).addContent("Fly Trap Info Message has been sent into " + channel.getAsMention() + ".").queue();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        if(member == null) return;
        if(guild.getIdLong() == 1153419306789507125L) { //main lotus server
            if(event.getChannel().getIdLong() == 1475886618945654959L) { //fly trap channel
                Role staffRole = guild.getRoleById(1203440776760266822L); //staff role
                if(!member.getRoles().contains(staffRole)) {
                    event.getMessage().delete().queue();
                    member.timeoutFor(1, TimeUnit.DAYS).reason("Autoquarantined by FlyTrap-Channel").queue();
                    deleteUserMessagesInAllChannels(event, member, 5);
                }else if(member.getRoles().contains(staffRole)) {
                    EmbedBuilder eb = ModlogController.baseEmbed(guild);
                    eb.setTitle("Staff Member " + member.getEffectiveName() + " posted in the Fly Trap Channel");
                    Role upperStaffRole = guild.getRoleById(1203440790081380443L);
                    eb.setColor(ModlogController.red);
                    ModlogController.sendMessageWithPing(eb, guild, upperStaffRole.getAsMention());
                    member.timeoutFor(1, TimeUnit.DAYS).reason("Autoquarantined by FlyTrap-Channel").queue();
                }
            }
        }
    }

    void deleteUserMessagesInAllChannels(MessageReceivedEvent event, Member member, int msgToDelete){
        List<TextChannel> channels = event.getGuild().getTextChannels();

        for(TextChannel channel : channels) {
            if(!event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_HISTORY, Permission.MESSAGE_MANAGE)){
                continue;
            }

            channel.getHistory().retrievePast(100).queue(msgs -> {
                List<Message> userMessages = msgs.stream()
                    .filter(msg -> msg.getAuthor().getId().equals(member.getId()))
                    .limit(msgToDelete)
                    .toList();
                
                if(userMessages.isEmpty()) return;

                deleteMessages(channel, userMessages);
            }, error -> {
                System.err.println("Failed to retrieve messages in channel " + channel.getName() + ": " + error.getMessage());
            });
        }
    }

    void deleteMessages(TextChannel channel, List<Message> messages) {
        if(messages.isEmpty()) return;

        List<Message> recentMessages = new ArrayList<>();
        List<Message> oldMessages = new ArrayList<>();

        long twoWeeksAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(14);

        for(Message msg : messages) {
            if(msg.getTimeCreated().toInstant().toEpochMilli() >= twoWeeksAgo) {
                recentMessages.add(msg);
            } else {
                oldMessages.add(msg);
            }
        }

        if(recentMessages.size() > 1) {
            channel.deleteMessages(recentMessages).queue();
        }else if(recentMessages.size() == 1) {
            recentMessages.get(0).delete().queue();
        }

        for(Message oldMsg : oldMessages) {
            oldMsg.delete().queue();
        }
    }
}