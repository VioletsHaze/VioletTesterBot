import Events.MessageEventListener;
import Events.ReadyEventListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Bot {
    static void main(String[] args) throws LoginException {
        final String TOKEN = "MTQ5NDAzMzc3NDg2NTAyMzE0Nw.Gws8IR.iX6HmDCeAJKBocrYWLjtezmT303RXrJy9ixKzw";
        JDABuilder builder = JDABuilder.createDefault(TOKEN);

        builder.enableIntents(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_TYPING
        );
        builder.addEventListeners(
                new ReadyEventListener(),
                new MessageEventListener()
        );
        builder.build();
    }
}
