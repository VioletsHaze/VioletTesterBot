package Interactives;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class VioBotUser {
    String userid;
    String description;

    public VioBotUser(String id) {
        this.userid = id;
    }

    public void updateProfile(String desc) {
        this.description = desc;
    }

    public String toString() {
        String output = "User doesn't exist.";

        try {
            User user = User;
        } catch (Exception e) {
            output = "There was an error fetching user "+this.userid;
        }

        return output;
    }
}
