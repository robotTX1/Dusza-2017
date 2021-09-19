package com.dusza;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    private final Scanner input = new Scanner(System.in);
    private final SessionManager sessionManager = new SessionManager();

    public void start() {
        System.out.println("Üdv a Dusza Email Kliensben!");
        List<String> options = new ArrayList<>();
        options.add("Bejelentkezés");
        options.add("Regisztráció");

        printOptions(options);

        String optionNumber;
        boolean rePrintMenu;

        while(true) {
            optionNumber = input.nextLine();
            rePrintMenu = false;

            switch (optionNumber) {
                case "1" -> rePrintMenu = logIn();
                case "2" -> rePrintMenu = register();
                case "3" -> {
                    System.out.println("Köszönöm, hogy használtad a programot!");
                    System.exit(0);
                }
                default -> System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
            }
            if(rePrintMenu) printOptions(options);
        }
    }

    private boolean logIn() {
        System.out.println("\n\n\nBejelentkezés");
        System.out.println("Felhasználó név: ");

        String username;
        username = input.nextLine().strip();

        System.out.println("Jelszó: ");
        String password;
        password = input.nextLine().strip();

        if(sessionManager.logIn(username, password)) {
            System.out.println("Sikeres bejelentkezés!");
            mainMenu();

        } else {
            System.out.println("Felhasználó név/jelszó nem egyezik!");
            List<String> options = new ArrayList<>();
            options.add("Újrapróbálkozom");
            options.add("Jelszóemlékeztetőt kérek");

            printOptions(options, false);
            String optionNumber;
            while(true) {
                optionNumber = input.nextLine();
                switch (optionNumber) {
                    case "1":
                        logIn();
                        return true;
                    case "2":
                        User user = sessionManager.getUser(username);
                        if(user == null) System.out.printf("Nincs ilyen regisztrált felhasználó: %s!\n", username);
                        else System.out.printf("Jelszóemlékeztető: %s\n", user.getPasswordReminder().isEmpty() ? "Nincs emlékezető" : user.getPasswordReminder());
                        break;
                    case "3":
                        return true;
                    default:
                        System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
                        break;
                }
            }
        }
        return true;
    }

    private boolean register() {
        System.out.println("\n\n\nRegisztráció");
        System.out.println("Felhasználó név: ");

        String username;
        User tmpUser;
        while(true) {
            try{
                username = input.nextLine().strip();
                tmpUser = sessionManager.getUser(username);
                if(tmpUser != null) {
                    System.out.println("Hiba: Ez a felhasználó már létezik!");
                    continue;
                }
                if(User.validateUsername(username)) break;
            }catch (Exception e) {
                System.out.println("Hiba: " + e.getMessage());
            }
        }

        System.out.println("Jelszó: ");

        String password;
        while(true) {
            try {
                password = input.nextLine().strip();
                if(User.validatePassword(password)) {
                    System.out.println("Jelszó még egyszer");
                    while(true) {
                        if(password.equals(input.nextLine().strip())) break;
                        else System.out.println("Hiba: A jelszavak nem egyeznek!");
                    }
                    break;
                }
            } catch (Exception e) {
                System.out.println("Hiba: " + e.getMessage());
            }
        }

        System.out.println("Jelszó emlékeztető (opcionális): ");

        String passwordReminder;
        while(true) {
            try {
                passwordReminder = input.nextLine();
                if(User.validatePasswordReminder(passwordReminder)) break;
            } catch (Exception e) {
                System.out.println("Hiba: " + e.getMessage());
            }
        }

        if(sessionManager.registerNewUser(username, password, passwordReminder)) {
            System.out.println("Sikeres regisztráció!");
            sessionManager.logIn(username, password);
            mainMenu();
        } else {
            System.out.println("Valami baj van, próbáld újra");
            System.out.println("Nyomjon meg egy gombot a menübe visszatéréshez!");
            input.nextLine();
        }
        return true;
    }

    private boolean sendEmail() {
        System.out.println("Email küldése");
        System.out.println("Címzett:");

        String addressee;

        while(true) {
            try {
                addressee = input.nextLine().strip();

                if(Email.validateAddressee(addressee)) {
                    User tmpUser = sessionManager.getUser(addressee.split("@")[0]);
                    if(tmpUser == null) throw new Exception("Nem létezik ilyen címzett: " + addressee);
                    break;
                }
            }catch (Exception e) {
                System.out.println("Hiba: " + e.getMessage());
            }
        }

        System.out.println("Tárgy (opcionális)");
        String object;

        while(true) {
            try{
                object = input.nextLine().strip();
                if(Email.validateObject(object)) break;
            }catch (Exception e) {
                System.out.println("Hiba: " + e.getMessage());
            }
        }

        System.out.println("Üzenet");
        String message;

        while(true) {
            try{
                message = input.nextLine().strip();
                if(Email.validateMessage(message)) break;
            }catch (Exception e) {
                System.out.println("Hiba: " + e.getMessage());
            }
        }

        System.out.println("Biztosan elakarja küldeni a levelet?");
        List<String> optionList = new ArrayList<>();
        optionList.add("Küldés");
        printOptions(optionList, false);

        String optionNumber;
        while(true) {
            optionNumber = input.nextLine().strip();
            switch (optionNumber) {
                case "1" -> {
                    System.out.println("A levél el lett elküldve.");
                    Email email = new Email(addressee, object, message, new Date(System.currentTimeMillis()), false);
                    sessionManager.sendEmail(email);
                    return true;
                }
                case "2" -> {
                    System.out.println("A levél nem lett elküldve.");
                    return true;
                }
                default -> System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
            }
        }
    }

    private void printOptions(List<String> options, boolean exit) {
        for(int i=0; i<options.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, options.get(i));
        }
        if(exit)
            System.out.printf("%d. Kilépés\n", options.size()+1);
        else
            System.out.printf("%d. Vissza\n", options.size()+1);
    }

    private void printOptions(List<String> options) {
        printOptions(options, true);
    }

    private void mainMenu() {
        System.out.println("\nÜdv újra nálunk,"+ sessionManager.getCurrentUser().getUsername()  + "!");
        System.out.println("Kérem válasszon az alábbi lehetőségek közül: ");

        List<String> options = new ArrayList<>();
        options.add("Beérkező levelek (levelek olvasása, törlése, rendezése)");
        options.add("Levélírás");

        printOptions(options, true);

        boolean rePrintMenu;

        String optionNumber;
        while(true) {
            optionNumber = input.nextLine();
            rePrintMenu = false;
            switch (optionNumber) {
                case "1" -> rePrintMenu = sendEmail();
                case "2" -> readEmail();
                case "3" -> {
                    System.out.print("Kijelentkezés... Legyen szép napja! \n \n");
                    sessionManager.leaveSession();
                    return;
                }
                default -> System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
            }
            if(rePrintMenu) printOptions(options, true);
        }

    }

    private void viewEmails()
    {

        String sortBy = "";
        System.out.println("Kérem adja meg a beérkező levelek rendezési szempontját:");
        List<String> options = new ArrayList<>();
        options.add("A küldő email címe");
        options.add("A levél tárgya");
        options.add("A levél érkezési dátuma");
        printOptions(options, false);

        String optionNumber;
        boolean optionSelected = false;
        while(! optionSelected) {
            optionNumber = input.nextLine();
            switch (optionNumber) {
                case "1":
                    sortBy = "sender";
                    optionSelected = true;
                    break;
                case "2":
                    sortBy = "object";
                    optionSelected = true;
                    break;
                case "3":
                    sortBy = "date";
                    break;
                case "4":
                    mainMenu();
                    return;
                default:
                    System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
                    break;
            }
        }

        // Levelek kiírása

        System.out.println("Kérem válassza ki, hogy melyik e-maillel szeretne műveletet végezni: ");
        String emptyLine = "#".repeat(50);
        System.out.println("Küldő        Tárgy        Érkezés dátuma        Olvasott/Olvasatlan");

        List<Email> emails = sessionManager.getCurrentUser().getEmailList();
        // sort the list of emails

        for (int i=1; i < emails.size(); i++) {
            for (int j=0; j<emails.size(); j++) {

            }
        }

        options = new ArrayList<>();
        for (Email mail : sessionManager.getCurrentUser().getEmailList()) {
            options.add("");
        }
        printOptions(options, false);

        optionNumber = "";
    }

}
