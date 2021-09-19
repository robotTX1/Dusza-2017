package com.dusza;

import java.text.SimpleDateFormat;
import java.util.*;

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
                    Email email = new Email(sessionManager.getCurrentUser().getUsername() + "@dusza.hu", object, message, new Date(System.currentTimeMillis()), false);
                    sessionManager.sendEmail(addressee.split("@")[0],email);
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
        System.out.println("\nÜdv újra nálunk, "+ sessionManager.getCurrentUser().getUsername()  + "!");
        System.out.println("Kérem válasszon az alábbi lehetőségek közül: ");

        List<String> options = new ArrayList<>();
        options.add("Levélírás");
        options.add("Beérkező levelek (levelek olvasása, törlése, rendezése)");

        printOptions(options, true);

        boolean rePrintMenu;

        String optionNumber;
        while(true) {
            optionNumber = input.nextLine();
            rePrintMenu = false;
            switch (optionNumber) {
                case "1" -> rePrintMenu = sendEmail();
                case "2" -> rePrintMenu = viewEmails();
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

    private void answearEmail(Email email) {
        System.out.println("Email válasz küldése erre: " + email.getObject());

        String addressee = email.getSenderEmailAddress();
        System.out.println("Címzett: " + addressee);

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
                    Email tmpEmail = new Email(sessionManager.getCurrentUser().getUsername() + "@dusza.hu", object, message, new Date(System.currentTimeMillis()), false);
                    sessionManager.sendEmail(addressee.split("@")[0], tmpEmail);
                    return;
                }
                case "2" -> {
                    System.out.println("A levél nem lett elküldve.");
                    return;
                }
                default -> System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
            }
        }
    }

    private boolean viewEmails()
    {
        System.out.println("Kérem adja meg a beérkező levelek rendezési szempontját:");
        List<String> options = new ArrayList<>();
        options.add("Nincs rendezés");
        options.add("A küldő email címe");
        options.add("A levél tárgya");
        options.add("A levél érkezési dátuma");
        printOptions(options, false);

        List<Email> emails = new ArrayList<>(sessionManager.getCurrentUser().getEmailList());

        String optionNumber;
        boolean optionSelected = false;
        while (!optionSelected) {
            optionNumber = input.nextLine();
            switch (optionNumber) {
                case "1":
                    optionSelected = true;
                    break;
                case "2":
                    Collections.sort(emails, (o1, o2) -> o1.getSenderEmailAddress().compareTo(o2.getSenderEmailAddress()));
                    optionSelected = true;
                    break;
                case "3":
                    Collections.sort(emails, (o1, o2) -> o1.getObject().compareTo(o2.getObject()));
                    optionSelected = true;
                    break;
                case "4":
                    Collections.sort(emails, (o1, o2) -> o1.getReceivedDate().compareTo(o2.getReceivedDate()));
                    optionSelected = true;
                    break;
                case "5":
                    return true;
                default:
                    System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
                    break;
            }
        }

        // Levelek kiírása

        System.out.println("Kérem válassza ki, hogy melyik e-maillel szeretne műveletet végezni: ");
        String emptyLine = "#".repeat(50);
        // 30 + s + 15 + s + 5 + olvasott
        int tab = 5;
        String header = "   Küldő " + " ".repeat(30-"Küldő ".length() + tab)
                + "Tárgy" + " ".repeat(15-"Tárgy".length() + tab)
                + "Dátum" + " ".repeat(tab-1) + "Olvasott/Olvasatlan";
        System.out.println(header);

        options = new ArrayList<>();
        for (Email mail : emails) {
            String date = new SimpleDateFormat(RWHandler.DATEPATTERN).format(mail.getReceivedDate());


            if (mail.isRead()) {
                options.add(mail.getSenderEmailAddress() + " ".repeat(30-mail.getSenderEmailAddress().length() + tab)
                        + mail.getObject() + " ".repeat(15-mail.getObject().length() + tab) + date + " ".repeat(5) + "Olvasott");
            } else {
                options.add(mail.getSenderEmailAddress() + " ".repeat(30-mail.getSenderEmailAddress().length() + tab)
                        + mail.getObject() + " ".repeat(15-mail.getObject().length() + tab) + date + " ".repeat(5) + "Olvasatlan");
            }
        }
        printOptions(options, false);


        boolean rePrintMenu;


        while(true) {
            optionNumber = input.nextLine();
            rePrintMenu = false;

            try {
                int option = Integer.parseInt(optionNumber)-1;
                if (option < options.size()) {
                    Email selectedEmail = emails.get(option);
                    System.out.println(selectedEmail.getSenderEmailAddress() + ":" + selectedEmail.getObject() + " kiválasztva.");
                    List<String> eoptions = new ArrayList<>();
                    eoptions.add("Megtekintés");
                    eoptions.add("Válasz írása");
                    eoptions.add("Törlés");

                    String selectedEmailOption;

                    printOptions(eoptions);

                    boolean back = false;
                    while (! back) {
                        optionNumber = input.nextLine();
                        boolean rePrintEmailSelectedMenu = false;

                        switch (optionNumber) {
                            case "1":
                                // Email megtekintése
                                sessionManager.readEmail(option);
                                options.get(option).replace("Olvasatlan", "Olvasott");
                                System.out.println("-".repeat(50));

                                System.out.println(selectedEmail.getSenderEmailAddress() + ":" + selectedEmail.getObject());


                                String date = new SimpleDateFormat(RWHandler.DATEPATTERN).format(selectedEmail.getReceivedDate());
//                                Calendar cal = Calendar.getInstance();
//                                cal.setTime(selectedEmail.getReceivedDate());
//                                int month = cal.get(Calendar.MONTH);
//                                int day = cal.get(Calendar.DAY_OF_MONTH);

                                System.out.println(date);
                                System.out.println("Az email tartalma: \n[");
                                System.out.println(selectedEmail.getMessage());
                                System.out.println("]");
                                rePrintEmailSelectedMenu = true;
                                break;
                            case "2":
                                // Emailre válaszolás
                                answearEmail(selectedEmail);
                                rePrintEmailSelectedMenu = true;
                                back = true;
                                break;
                            case "3":
                                // Email törtése
                                sessionManager.deleteEmail(option);
                                options.remove(option);
                                back = true;
                                rePrintMenu = true;
                                break;
                            case "4":
                                back = true;
                                rePrintMenu = true;
                                break;
                            default:
                                System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
                                rePrintEmailSelectedMenu = true;
                                break;
                        }

                        if(rePrintEmailSelectedMenu) printOptions(eoptions, true);

                    }

                } else {
                    // vissza a mainMenu-be
                    return true;
                }

            } catch (NumberFormatException e) {
                System.out.printf("Nincs ilyen opció: %s\n", optionNumber);
                rePrintMenu = true;
            }


            if(rePrintMenu) {
                System.out.println(header);
                printOptions(options, false);
            };
        }

    }

}
