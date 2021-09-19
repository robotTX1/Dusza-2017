package com.dusza;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RWHandler {
    public static String DATEPATTERN = "MM.dd";
    public static RWHandler single_instance = null;

    Path rwpath;


    // constructors
    private RWHandler(Path path) {
        rwpath = path;
    }

    public static RWHandler init(Path path) {
        if (single_instance != null)
        {
            throw new AssertionError("You already initialized me");
        }

        single_instance = new RWHandler(path);
        return single_instance;
    }

    public static RWHandler getInstance() {
        if(single_instance == null) {
            throw new AssertionError("You have to call init first");
        }

        return single_instance;
    }


    // Methods
    private List<String> readFile(String file){
        Path path = rwpath.resolve(file);
        List<String> data = new ArrayList<>();
        try(Scanner input = new Scanner(Files.newBufferedReader(path)))
        {
            while(input.hasNextLine()) {
                data.add(input.nextLine());
            }
        } catch (IOException e) {
            System.out.println("File named " + file + " not found.");
        }

        return data;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        List<String> data = readFile("adatok.txt");

        for (String line : data)
        {
            String[] splt = line.split(" ");
            User tempUser;
            if (splt.length == 2) {
                tempUser= new User(splt[0], Integer.parseInt(splt[1]), "");
            } else {
                tempUser = new User(splt[0], Integer.parseInt(splt[1]), splt[2]);
            }

            tempUser.setEmailList(readEmailsFromUser(tempUser));
            users.add(tempUser);
        }

        return users;
    }

    public List<Email> readEmailsFromUser(User user) {
        List<Email> mails = new ArrayList<>();

        List<String> data = readFile(user.getUsername()+".txt");

        for (String line : data)
        {
            String[] splt = line.split(" ");

            StringBuilder builder = new StringBuilder();
            for (int i = 4; i < splt.length; i++) {
                builder.append(splt[i]).append(" ");
            }

            String message = builder.toString();
            message = message.replaceAll("\\{", "");
            message = message.replaceAll("\\}", "");

            try {
                DateFormat formatter = new SimpleDateFormat(DATEPATTERN);
                Date date = (Date)formatter.parse(splt[2]);

                String sender = splt[0].replace("[kukac]", "@");


                if (Objects.equals(splt[3], "olvasott"))
                {
                    mails.add(new Email(sender, splt[1], message ,date, true));
                } else {
                    mails.add(new Email(sender, splt[1], message, date, false));
                }

            } catch (ParseException e) {
                System.out.println("Date can't be converted: " + splt[2]);
            }

        }

        return mails;
    }


    public void saveNewUser(User user) {
        String line;
        if (Objects.equals(user.getPasswordReminder(), "")) {
            line = "\n" + user.getUsername() + " " + user.getPassword();
        } else {
            line = "\n" + user.getUsername() + " " + user.getPassword() + " " + user.getPasswordReminder();
        }

        // save the new line to the file
        Path p = rwpath.resolve("adatok.txt");

        try(BufferedWriter writer = Files.newBufferedWriter(p, StandardOpenOption.APPEND)) {
            writer.append(line);

        } catch (IOException e) {
            System.out.println("Adatok.txt nem létezik");
        }

        // username.txt létrehozása
        p = rwpath.resolve(user.getUsername() + ".txt");

        try(BufferedWriter writer = Files.newBufferedWriter(p)) {
            writer.append("");

        } catch (IOException e) {
            System.out.println("Adatok.txt nem létezik");
        }

    }


    public void saveEmails(User user) {
        Path p = rwpath.resolve(user.getUsername()+".txt");

        try(BufferedWriter writer = Files.newBufferedWriter(p)) {
            for (Email mail : user.getEmailList()) {
                String line;

                String date = new SimpleDateFormat(DATEPATTERN).format(mail.getReceivedDate());

                String message = "{" + mail.getMessage() + "}";
                String sender = mail.getSenderEmailAddress().replace("@", "[kukac]");
                if (mail.isRead()) {
                    line = sender + " " + mail.getObject() + " " + date
                            + " olvasott " + message;
                } else {
                    line = sender + " " + mail.getObject() + " " + date
                            + " olvasatlan " + message;
                }
                writer.append(line).append("\n");
            }

        } catch (IOException e) {
            System.out.println(user.getUsername() + ".txt nem létezik");
        }
    }
}
