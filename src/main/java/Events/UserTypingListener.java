package Events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class UserTypingListener extends ListenerAdapter {
    @Override
    public void onUserTyping(@NotNull UserTypingEvent event) {

        super.onUserTyping(event);

        /*
        Member user = event.getMember();
        MessageChannel channel = event.getChannel();

        assert user != null;
        if (user.getId().equals("323854028242944011")) {
            channel.sendMessage("shut up violet").queue();
        }
         */
    }
}
