package com.dusza;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RWHandler {
    private String path;

    // constructors
    public RWHandler(String path) {
        this.path = path;
    }

    // Methods

    private String readFile(String p){
        //try(Scanner scanner = new Scanner())

        try {
            File f = new File(path + p);
            Scanner myReader = new Scanner(f);
            List<String> data = new ArrayList<String>();

            while (myReader.hasNextLine())
            {
                data.add(myReader.nextLine());
            }
            myReader.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File named " + p + " not found.");
        }
        return "";
    }

    public List<Email> readMailsFromUser(User user)
    {
        List<Email> mails = new ArrayList<Email>();

        return mails;
    }

    public List<User> getUsers()
    {
        List<User> users = new ArrayList<User>();

        return users;
    }

    // getters & setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
