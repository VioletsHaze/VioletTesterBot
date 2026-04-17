package Interactives;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class VioBotUser {
    public String userid;
    public String description;
    public int currency;

    @JsonIgnore
    User user;

    // Constructors
    public VioBotUser() {} // Default constructor for JSON encoding.
    public VioBotUser(@NotNull Member userPointer) {
        this.user = userPointer.getUser();
        this.userid = userPointer.getId();
        this.currency = 0;
        this.description = "A new user!";

        // Now attempts to fill in the data.
        this.fetchDataById(this.userid);
    }

    // Looks for a JSON file containing the user's data based on userid and fills it in.
    // Otherwise, add them.
    public void fetchDataById(String userid) {
        File[] violetDatabase = new File("Userbase").listFiles();
        boolean found = false;
        if (violetDatabase != null) {
            System.out.println("Found directory.");
            for (File json : violetDatabase) {
                if (json.getName().equals(this.userid+".json")) {
                    System.out.println("User found.");
                    found = true;
                    try {
                        String contents = new Scanner(json).nextLine();
                        ObjectMapper mapper = new ObjectMapper();

                        VioBotUser temporaryUser = mapper.readValue(contents, VioBotUser.class);
                        this.description = temporaryUser.description;
                        this.currency = temporaryUser.currency;

                        System.out.println("Read successful.");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            System.out.println("Issue with fetching user.");
        }

        if (!found) {
            System.out.println("User not in database.");
            String jsonEncode = this.toJSON();

            if (!jsonEncode.isEmpty()) {
                try {
                    FileWriter writer = new FileWriter("Userbase/"+userid+".json");
                    writer.write(jsonEncode);
                    writer.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Converts the data of this user into a JSON string.
    public String toJSON() {
        String output = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            output = mapper.writeValueAsString(this);

            System.out.println(output);
        } catch (JsonProcessingException e) {
            System.out.println("Json encoding failed.");
            e.printStackTrace();
        }

        return output;
    }

    // Converts a user into a string for debugging purposes.
    public String toString() {
        String output = "User doesn't exist.";

        try {
            output = this.user.getName()+" - "+this.userid+"\n"+this.description;
        } catch (Exception e) {
            output = "There was an error fetching user "+this.userid;
        }

        return output;
    }
}
