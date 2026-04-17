package Events;

import Interactives.VioBotUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.ImageFormat;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class SlashCommandListener extends ListenerAdapter {
    public EmbedBuilder userProfileEmbed(Member user) {
        VioBotUser botUser = new VioBotUser(user);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(user.getNickname());
        eb.setAuthor(user.getUser().getName());
        eb.setColor(Color.MAGENTA);
        eb.setDescription(botUser.description);
        eb.setImage(user.getEffectiveAvatarUrl(ImageFormat.PNG));
        eb.setFooter(botUser.userid);

        eb.addField("VioCoin","$"+botUser.currency,true);

        return eb;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            // Ping - Replies with pong. Very basic.
            case "ping":
                event.reply("Pong!").setEphemeral(true).queue();
                break;

            // Replies with a random line of text from the manifest in "ancientwisdom.txt"
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

            // Inside joke command.
            // If user clicks "Kill." and has move members permissions, and "Lepi" is in the server, DC them.
            case "lepi":
                event.reply("Disconnect Lepi's router?").addComponents(ActionRow.of(
                                Button.danger("killLepisRouter","Kill."),
                                Button.success("spareLepisRouter","Mercy :)")
                        )).queue();
                break;

            // View and edit a user's profile saved by the bot.
            case "profile":
                Member calledBy = event.getMember();
                Member user = calledBy;

                if (event.getOption("user") != null) {
                    user = event.getOption("user").getAsMember();
                }

                assert calledBy != null;
                VioBotUser botUser = new VioBotUser(user);
                System.out.println(botUser);

                EmbedBuilder eb = userProfileEmbed(user);

                // Only give the option to update profile if the viewed user and user who called are same.
                if (calledBy.getId().equals(user.getId())) {
                    event.replyEmbeds(eb.build()).addComponents(ActionRow.of(
                            Button.primary("profileEditDesc","Update Profile"),
                            Button.link(user.getEffectiveAvatarUrl(ImageFormat.PNG),"Avatar")
                    )).queue();
                } else {
                    event.replyEmbeds(eb.build()).addComponents(ActionRow.of(
                            Button.link(user.getEffectiveAvatarUrl(ImageFormat.PNG),"Avatar")
                    )).queue();
                }

                //event.reply("This feature appears to be work in progress. Thank you for your patience!").setEphemeral(true).queue();
                break;

            // Mandatory default case. Only occurs if an invalid command is somehow sent and I missed it up.
            default:
                System.out.println("No command???");
                event.reply("There appears to have been an error. Please contact Violet.").setEphemeral(true).queue();
        }
    }

    // Buttons for the above commands.
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            // "/lepi"
            // Disconnect Lepi if the user has VOICE_MOVE_OTHERS perms.
            case "killLepisRouter":
                event.getMessage().delete().queue();
                event.reply("It has been done.").queue();

                boolean canKick = event.getMember().hasPermission(Permission.VOICE_MOVE_OTHERS);
                Member lepi = event.getGuild().getMemberById("376369563073249280");

                if (lepi != null && canKick) {
                    try {
                        event.getGuild().kickVoiceMember(lepi).queue();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                break;

            // "/lepi"
            // Does nothing but replies to the original.
            case "spareLepisRouter":
                event.getMessage().delete().queue();
                event.reply("ok :)").queue();
                break;

            // "/profile"
            // Prompts the user to enter an about me via Modal.
            case "profileEditDesc":
                if (event.getMessage().getEmbeds().getFirst().getFooter().getText().equals(event.getMember().getId())) {
                    TextInput aboutme = TextInput.create("biography", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("Enter in a short about me.")
                            .setMinLength(0)
                            .setMaxLength(400)
                            .build();

                    Modal modal = Modal.create("profileEditor", "VioBot Profile")
                            .addComponents(Label.of("Biography", aboutme))
                            .build();

                    event.replyModal(modal).queue();
                } else {
                    event.reply("You cannot edit other users' profiles!").setEphemeral(true).queue();
                }
                break;

            // Mandatory default case.
            default:
                System.out.println("A button is broken");
        }
    }

    // Handles modals
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("profileEditor")) {
            Member user = event.getMember();
            String newdesc = event.getValue("biography").getAsString();

            VioBotUser botUser = new VioBotUser(user);
            botUser.description = newdesc;
            botUser.update();

            EmbedBuilder eb = userProfileEmbed(user);
            event.editMessageEmbeds(eb.build()).queue();
            //event.reply("Profile updated successfully!").setEphemeral(true).queue();
        }
    }
}
