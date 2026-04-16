import Events.MessageEventListener;
import Events.ReadyEventListener;
import Events.SlashCommandListener;
import Events.UserTypingListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Bot {
    static void main() throws LoginException, FileNotFoundException {
        File file = new File("token.txt");
        Scanner scanner = new Scanner(file);
        final String TOKEN = scanner.nextLine();

        JDABuilder builder = JDABuilder.createDefault(TOKEN);

        // Builds the application.
        JDA jda = builder.enableIntents(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_TYPING
        ).addEventListeners(
                new ReadyEventListener(),
                new MessageEventListener(),
                //new UserTypingListener(),
                new SlashCommandListener()
        ).build();

        // Command Building
        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                Commands.slash("ping","Test command - Replies ephemerally with pong.")
                        .setContexts(InteractionContextType.GUILD)
        );

        commands.addCommands(
                Commands.slash("wisdom", "The bot will bestow upon you ancient wisdom from the philosopher Socrates.")
                        .setContexts(InteractionContextType.GUILD)
        );

        commands.addCommands(
                Commands.slash("lepi", "Get a conspicuous prompt.")
                        .setContexts(InteractionContextType.GUILD)
        );

        commands.queue();
    }
}
