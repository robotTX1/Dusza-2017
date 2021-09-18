package com.dusza;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RWHandler {

    // constructors
    public RWHandler() {

    }

    // Methods

    private List<String> readFile(String file){
        Path path = FileSystems.getDefault().getPath("Data", file);
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
            if (splt.length == 2) {
                users.add(new User(splt[0], Integer.parseInt(splt[1]), ""));
            } else {
                users.add(new User(splt[0], Integer.parseInt(splt[1]), splt[2]));
            }
        }

        return users;
    }

    public List<Email> readMailsFromUser(User user) throws ParseException {
        List<Email> mails = new ArrayList<>();

        List<String> data = readFile(user.getUsername()+".txt");

        for (String line : data)
        {
            String[] splt = line.split(" ");
            // String senderEmailAddress, String object, Date receivedDate, boolean isRead
            Date date = new SimpleDateFormat("MM.dd").parse(splt[2]);

            if (Objects.equals(splt[3], "olvasott"))
            {
                mails.add(new Email(splt[0], splt[1], date, true));
            } else {
                mails.add(new Email(splt[0], splt[1], date, false));
            }

        }

        return mails;
    }

    public void addUser(User user) {
        if (Objects.equals(user.getPasswordReminder(), "")) {
            String line = user.getUsername() + " " + user.getPassword();
        } else {
            String line = user.getUsername() + " " + user.getPassword() + " " + user.getPasswordReminder();
        }
    }

    // getters & setters



}
