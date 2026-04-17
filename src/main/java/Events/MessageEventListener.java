package Events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageEventListener extends ListenerAdapter {
    private boolean seventhanker = false;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        Message msg = event.getMessage();
        String content = msg.getContentRaw();

        // Ignore bot messages
        if (event.getAuthor().isBot()) return;

        MessageChannel channel = msg.getChannel();

        if (content.equalsIgnoreCase("six")) {
            channel.sendMessage("seven").queue();
            seventhanker = true;
        }

        if (content.toLowerCase().contains("raid") && (content.toLowerCase().contains("shadow") || content.toLowerCase().contains("legend"))) {
            msg.reply("<:raid:1494129983969296395>").queue();
            msg.delete().queue();
        }

        if (content.toLowerCase().contains("thank") && seventhanker) {
            channel.sendMessage("you're welcome :3").queue();
            seventhanker = false;
        }
    }
}
