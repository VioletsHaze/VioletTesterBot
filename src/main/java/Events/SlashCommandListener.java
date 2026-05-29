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
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.ImageFormat;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class SlashCommandListener extends ListenerAdapter {
    public EmbedBuilder userProfileEmbed(Member user) {
        VioBotUser botUser = new VioBotUser(user);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(user.getNickname())
                .setAuthor(user.getUser().getName())
                .setColor(Color.MAGENTA)
                .setDescription(botUser.description)
                .setImage(user.getEffectiveAvatarUrl(ImageFormat.PNG))
                .setFooter(botUser.userid)
                .addField("VioCoin","$"+botUser.currency,true);

        return eb;
    }

    public EmbedBuilder magicEightEmbed(Member user, String answer, String question) throws IOException {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(user.getNickname() + " asks: \""+question+"\"")
                .setAuthor("Magic 8-Ball of Wisdom")
                .setColor(Color.BLACK)
                .setDescription(answer)
                .setImage("attachment://eightballresponse.jpg")
                //.setImage("attachment://"+imagepath)
                .setFooter(user.getId());

        return eb;
    }

    // returns the path to
    public String createEightballImage(String answer, int imageid) throws IOException {
        File file = new File("Images/eightballtemplate.jpg");
        BufferedImage image = null;

        try { image = ImageIO.read(file); } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Graphics g = image.getGraphics();
        g.setFont(new Font("Arial", Font.PLAIN, 14));

        FontMetrics fm = g.getFontMetrics(g.getFont());

        g.drawString(
                answer,
                (image.getWidth() -  fm.stringWidth(answer)) / 2,
                (image.getHeight() -  fm.getHeight()) / 2 + fm.getAscent());
        g.dispose();

        String path = "Images/eightballtemplate_"+imageid+".jpg";
        ImageIO.write(image, "jpg", new File(path));

        return path;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Member calledBy = event.getMember();
        Member user = calledBy;

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
                if (event.getOption("user") != null) {
                    user = event.getOption("user").getAsMember();
                }

                assert calledBy != null;
                assert user != null;
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

            // Magic 8-ball command
            case "8ball":
                if (event.getOption("question") == null || Objects.requireNonNull(event.getOption("question")).getAsString().isEmpty()) {
                    event.reply("The 8-ball cannot answer your question if you have no question to ask, silly.").setEphemeral(true).queue();
                    break;
                }

                String askedQuestion = Objects.requireNonNull(event.getOption("question")).getAsString();

                File manifest = new File("magiceight.txt");
                try {
                    BufferedReader reader = new BufferedReader(new FileReader("magiceight.txt"));
                    int max = 0;

                    while (reader.readLine() != null) max++;
                    reader.close();

                    Random rng = new Random();
                    int line = rng.nextInt(0,max);

                    Scanner sc = new Scanner(manifest);
                    String finalLine = "Something went wrong.";
                    int finalindex = 0;
                    for (int i=0; i<max; i++) {
                        String newline = sc.nextLine();
                        if (i==line) {
                            finalLine = newline;
                            finalindex = i;
                            break;
                        }
                    }

                    assert user != null;
                    EmbedBuilder responseEmbed = magicEightEmbed(user, finalLine, askedQuestion);

                    String imagepath = createEightballImage(finalLine, finalindex);
                    InputStream file = new FileInputStream(imagepath);

                    event.replyFiles(FileUpload.fromData(file, "eightballresponse.jpg")).setEmbeds(responseEmbed.build()).queue();
                } catch (FileNotFoundException e) {
                    System.out.println("Where's my wisdom?");
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
