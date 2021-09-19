package com.dusza;

public class SessionManager {
    private User currentUser;
    private boolean isLoggedIn;

    public SessionManager() {
    }

    public User getUser(String username) {
        for(User user : RWHandler.getInstance().getUsers()) {
            if(user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public boolean logIn(String username, String password) {
        User user = getUser(username);

        if(user != null) {
            if(user.getPassword() == User.passwordToHash(password)) {
                currentUser = user;
                isLoggedIn = true;
                return true;
            }
        }

        return false;
    }

    public boolean registerNewUser(String username, String password, String passwordReminder) {
        User tmp = getUser(username);

        if(tmp == null) {
            User newUser = new User(username, User.passwordToHash(password), passwordReminder);
            RWHandler.getInstance().saveNewUser(newUser);
            currentUser = newUser;
            isLoggedIn = true;
            return true;
        }
        return false;
    }

    public void leaveSession() {
        isLoggedIn = false;
        currentUser = null;
    }

    public Email readEmail(int index) {
        Email email = currentUser.getEmail(index);
        email.setRead(true);
        RWHandler.getInstance().saveEmails(currentUser);
        return email;
    }

    public Email deleteEmail(int index) {
        Email ret = currentUser.removeEmail(index);
        RWHandler.getInstance().saveEmails(currentUser);
        return ret;

    }

    public boolean deleteEmail(Email email) {
        boolean ret = currentUser.removeEmail(email);
        RWHandler.getInstance().saveEmails(currentUser);
        return ret;
    }

    public void sendEmail(String addressee, Email email) {
        User user = getUser(addressee);
        user.addEmail(email);
        RWHandler.getInstance().saveEmails(user);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
