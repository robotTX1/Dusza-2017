package com.dusza;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RWHandler {
    public static RWHandler single_instance = null;

    Path RWPath;

    // constructors
    private RWHandler(Path path) {
        RWPath = FileSystems.getDefault().getPath("Data");
    }

    // Methods

    public RWHandler getInstance(Path path) {
        if (single_instance == null)
            single_instance = new RWHandler(path);

        return single_instance;
    }

    private List<String> readFile(String file){
        Path path = RWPath.resolve(file);
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

    public void saveUser(User user) {
        //
        String line;
        if (Objects.equals(user.getPasswordReminder(), "")) {
            line = "\n" + user.getUsername() + " " + user.getPassword();
        } else {
            line = "\n" + user.getUsername() + " " + user.getPassword() + " " + user.getPasswordReminder();
        }

        // save the new line to the file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("adatok.txt", true));
            writer.append(line);
            writer.close();

        } catch (IOException e) {
            System.out.println("Adatok.txt nem létezik");
        }
    }

    // getters & setters



}
