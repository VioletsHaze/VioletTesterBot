package Events;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Widget;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import okhttp3.EventListener;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping":
                event.reply("Pong!").setEphemeral(true).queue();
                break;
            case "wisdom":
                File wisdom = new File("ancientwisdom.txt");
                try {
                    BufferedReader reader = new BufferedReader(new FileReader("ancientwisdom.txt"));
                    int max = 0;

                    while (reader.readLine() != null) max++;
                    reader.close();

                    Random rng = new Random();
                    int line = rng.nextInt(0,max);

                    Scanner sc = new Scanner(wisdom);
                    for (int i=0; i<max; i++) {
                        String newline = sc.nextLine();
                        if (i==line) {
                            event.reply(newline).queue();
                            break;
                        }
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Where's my wisdom?");
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "lepi":
                event.reply("Disconnect Lepi's router?").addComponents(ActionRow.of(
                                Button.danger("killLepisRouter","Kill."),
                                Button.success("spareLepisRouter","Mercy :)")
                        )).queue();
                break;
            default:
                System.out.println("No command???");
                event.reply("There appears to have been an error. Please contact Violet.").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "killLepisRouter":
                event.getMessage().delete().queue();
                event.reply("It has been done.").queue();

                //Member userperms = event.getMember();
                //Member lepi = event.getGuild().getMemberById("376369563073249280");

                /*
                if (lepi != null) {
                    try {
                        event.getGuild().kickVoiceMember(lepi).queue();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                */

                break;
            case "spareLepisRouter":
                event.reply("ok :)").queue();
                break;
            default:
                System.out.println("A button is broken");
        }
    }
}
