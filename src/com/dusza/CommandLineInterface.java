package com.dusza;

import java.util.ArrayList;
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
                        break;
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
            //TODO interaction menu
        } else {
            System.out.println("Valami baj van, próbáld újra");
            System.out.println("Nyomjon meg egy gombot a menübe visszatéréshez!");
            input.nextLine();
        }
        return true;
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

        String optionNumber;
        while(true) {
            optionNumber = input.nextLine();
            switch (optionNumber) {
                case "1":
                    viewEmails();

                    break;
                case "2":
                    // TODO: levél írása
                    break;
                case "3":
                    System.out.print("Köszönjük, hogy a mi alkalmazásunkat válaszotta! Legyen szép napja! \n \n");
                    start();
                    return;
                default:
                    System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
                    break;
            }
        }

    }

    private void viewEmails() {

        String sortBy = "";
        System.out.println("Kérem adja meg a beérkező levelek rendezési szempontját:");
        List<String> options = new ArrayList<>();
        options.add("Nincs rendezés");
        options.add("A küldő email címe");
        options.add("A levél tárgya");
        options.add("A levél érkezési dátuma");
        printOptions(options, false);

        String optionNumber;
        boolean optionSelected = false;
        while (!optionSelected) {
            optionNumber = input.nextLine();
            switch (optionNumber) {
                case "1":
                    sortBy = "";
                    optionSelected = true;
                    break;
                case "2":
                    sortBy = "sender";
                    optionSelected = true;
                    break;
                case "3":
                    sortBy = "object";
                    optionSelected = true;
                    break;
                case "4":
                    sortBy = "date";
                    break;
                case "5":
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

        switch (sortBy) {
            case "sender":
                // by sender
                int n = emails.size();
                for (int i = n - 1; i > 0; i--) {
                    for (int j = 0; j < i; j++) {
                        String addr1 = emails.get(j).getSenderEmailAddress();
                        String addr2 = emails.get(j + 1).getSenderEmailAddress();
                        if (addr1.compareTo(addr2) > 0) {
                            Email tempMail = emails.get(j);
                            emails.set(j, emails.get(j+1));
                            emails.set(j+1, tempMail);
                        }
                    }
                }
                break;
            case "object":
                break;
            case "date":
                break;
        }


        options = new ArrayList<>();
        for (Email mail : emails) {
            if (mail.isRead()) {
                options.add(mail.getSenderEmailAddress() + "\t" + mail.getObject() + "\t" + mail.getReceivedDate() + "\t Olvasott");
            } else {
                options.add(mail.getSenderEmailAddress() + "\t" + mail.getObject() + "\t" + mail.getReceivedDate() + "\t Olvasatlan");
            }
        }
        printOptions(options, false);

        optionNumber = "";
    }

}
