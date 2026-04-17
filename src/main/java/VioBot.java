import Events.MessageEventListener;
import Events.ReadyEventListener;
import Events.SlashCommandListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;

public class VioBot {
    private final Dotenv CONFIG = Dotenv.configure().load();
    private final JDA BOT_JDA;

    public VioBot() throws LoginException {
        final String TOKEN = CONFIG.get("TOKEN");

        JDABuilder builder = JDABuilder.createDefault(TOKEN);

        // Builds the application.
        BOT_JDA = builder.enableIntents(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_TYPING
        ).addEventListeners(
                new ReadyEventListener(),
                new MessageEventListener(),
                //new UserTypingListener(),
                new SlashCommandListener()
        ).setActivity(Activity.listening("Weezer")).build();

        // Command Building
        CommandListUpdateAction botCommands = BOT_JDA.updateCommands();

        botCommands.addCommands(
                Commands.slash("ping","Test command - Replies ephemerally with pong.")
                        .setContexts(InteractionContextType.GUILD)
        ).addCommands(
                Commands.slash("wisdom", "The bot will bestow upon you ancient wisdom from the philosopher Socrates.")
                        .setContexts(InteractionContextType.GUILD)
        ).addCommands(
                Commands.slash("lepi", "Get a conspicuous prompt.")
                        .setContexts(InteractionContextType.GUILD)
        ).addCommands(
                Commands.slash("profile", "View/Edit your VioletBot profile.")
                        .setContexts(InteractionContextType.GUILD)
        ).queue();
    }

    public JDA getJDA() {
        return BOT_JDA;
    }

    static void main() throws LoginException, FileNotFoundException {
        try {
            VioBot bot = new VioBot();
        } catch (LoginException e) {
            System.out.println("Login Error has occurred. :<");
            e.printStackTrace();
        }
    }
}
