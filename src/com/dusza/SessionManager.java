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
        return currentUser.getEmail(index);
    }

    public Email deleteEmail(int index) {
        return currentUser.removeEmail(index);
    }

    public boolean deleteEmail(Email email) {
        return currentUser.removeEmail(email);
    }

    public void sendEmail(Email email) {
        currentUser.addEmail(email);
        RWHandler.getInstance().saveEmails(currentUser);
    }


    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
