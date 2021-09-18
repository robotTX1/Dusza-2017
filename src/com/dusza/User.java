package com.dusza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    private String username;
    private int password;
    private String passwordReminder;
    private final List<Email> emailList = new ArrayList<>();

    public User(String username, int password, String passwordReminder) {
        this.username = username;
        this.password = password;
        this.passwordReminder = passwordReminder;
    }

    public String getUsername() {
        return username;
    }

    public int getPassword() {
        return password;
    }

    public String getPasswordReminder() {
        return passwordReminder;
    }

    public List<Email> getEmailList() {
        return Collections.unmodifiableList(emailList);
    }
}
