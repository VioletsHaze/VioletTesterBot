package Events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageEventListener extends ListenerAdapter {
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
        }

        if (content.toLowerCase().contains("raid") && (content.toLowerCase().contains("shadow") || content.toLowerCase().contains("legends") || content.toLowerCase().contains("legend"))) {
            msg.reply("<:raid:1494129983969296395>").queue();
            msg.delete().queue();
        }
    }
}
