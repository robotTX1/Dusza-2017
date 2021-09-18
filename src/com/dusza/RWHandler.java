package com.dusza;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RWHandler {
    public static RWHandler single_instance = null;

    Path rwpath;


    // constructors
    private RWHandler(Path path) {
        rwpath = path;
    }

    public synchronized static RWHandler init(Path path) {
        if (single_instance != null)
        {
            // in my opinion this is optional, but for the purists it ensures
            // that you only ever get the same instance when you call getInstance
            throw new AssertionError("You already initialized me");
        }

        single_instance = new RWHandler(path);
        return single_instance;
    }

    public RWHandler getInstance() {
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
            // String username, int password, String passwordReminder
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
            // String senderEmailAddress, String object, Date receivedDate, boolean isRead

            try {
                Date date = new SimpleDateFormat("MM.dd").parse(splt[2]);

                if (Objects.equals(splt[3], "olvasott"))
                {
                    mails.add(new Email(splt[0], splt[1], date, true));
                } else {
                    mails.add(new Email(splt[0], splt[1], date, false));
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
                if (mail.isRead()) {
                    line = mail.getSenderEmailAddress() + " " + mail.getObject() + " " + mail.getReceivedDate()
                            + " olvasott";
                } else {

                }
            }

        } catch (IOException e) {
            System.out.println(user.getUsername() + ".txt nem létezik");
        }


    }
    // getters & setters



}
