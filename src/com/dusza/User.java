package com.dusza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    private static final String REGEX = "^[a-zA-Z0-9]*$";
    private static final int USERNAME_MAX_LENGTH = 15;

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 10;

    private static final int PASSWORD_REMINDER_MAX_LENGTH = 15;

    private final String username;
    private final int password;
    private final String passwordReminder;
    private final List<Email> emailList = new ArrayList<>();

    public User(String username, int password, String passwordReminder) {
        this.username = username;
        this.password = password;
        this.passwordReminder = passwordReminder;
    }

    public static boolean validateUsername(String username) throws Exception {
        if(username == null) throw new Exception("A felhasználó név valahogy null lett :/");
        if(username.equals("")) throw new Exception("A felhasználó név nem lehet üres!");
        if(username.length() > USERNAME_MAX_LENGTH) throw new Exception(String.format("A felhasználó név nem lehet %d karakternél hoszabb!", USERNAME_MAX_LENGTH));
        if(username.contains(" ")) throw new Exception("A felhasználó név csak 1 szó lehet!");
        if(!username.matches(REGEX)) throw new Exception("A felhasználó név csak az angol ABC betűit és számaokat tartalmazhat!");

        return true;
    }

    public static boolean validatePassword(String password) throws Exception {
        if(password == null) throw new Exception("A jelszó valahogy null lett :/");
        if(password.length() < PASSWORD_MIN_LENGTH) throw new Exception(String.format("A jelszó minimum %d karakter hosszú lehet!", PASSWORD_MIN_LENGTH));
        if(password.length() > PASSWORD_MAX_LENGTH) throw new Exception(String.format("A jelszó maximum %d karakter hosszú lehet!", PASSWORD_MAX_LENGTH));
        if(password.contains(" ")) throw new Exception("A jelszó csak 1 szó lehet!");
        if(!password.matches(REGEX)) throw new Exception("A jelszó csak az angol ABC betűit és számaokat tartalmazhat!");

        return true;
    }

    public static boolean validatePasswordReminder(String passwordReminder) throws Exception {
        if(passwordReminder == null) throw new Exception("A jelszó emlékeztető valahogy null lett :/");
        if(passwordReminder.length() > PASSWORD_REMINDER_MAX_LENGTH) throw new Exception(String.format("A jelszó emlékeztető nem lehet %d karakternél hoszabb!", PASSWORD_REMINDER_MAX_LENGTH));
        if(passwordReminder.contains(" ")) throw new Exception("A jelszó emlékeztető csak 1 szó lehet!");
        if(!passwordReminder.matches(REGEX)) throw new Exception("A jelszó emlékeztető csak az angol ABC betűit és számaokat tartalmazhat!");

        return true;
    }

    public void addEmail(Email email) {
        emailList.add(email);
    }

    public boolean removeEmail(Email email) {
        return emailList.remove(email);
    }

    public Email removeEmail(int index) {
        return emailList.remove(index);
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
