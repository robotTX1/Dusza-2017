package com.dusza;

import java.util.Date;

public class Email {
    private static final String OBJECT_REGEX = "^[a-zA-Z0-9]*$";

    private static final int OBJECT_MAX_LENGTH = 15;

    private static final String MESSAGE_REGEX = "^[a-zA-Z0-9.,;:\\-!? ]*$";
    private static final int MESSAGE_MAX_LENGTH = 100;

    private static final String ADDRESSEE_REGEX = "^[a-zA-Z0-9@.]*$";

    private final String senderEmailAddress;
    private final String object;
    private final String message;
    private final Date receivedDate;
    private boolean isRead;

    public Email(String senderEmailAddress, String object, String message, Date receivedDate, boolean isRead) {
        this.senderEmailAddress = senderEmailAddress;
        this.object = object;
        this.message = message;
        this.receivedDate = receivedDate;
        this.isRead = isRead;
    }

    public static boolean validateObject(String object) throws Exception {
        if(object == null) throw new Exception("A tárgy valahogy null lett :/");
        if(object.length() > OBJECT_MAX_LENGTH) throw new Exception(String.format("A tárgy nem lehet %d karakternél hoszabb!", OBJECT_MAX_LENGTH));
        if(object.contains(" ")) throw new Exception("A tárgy csak 1 szó lehet!");
        if(!object.matches(OBJECT_REGEX)) throw new Exception("A tárgy csak az angol ABC betűit és számaokat tartalmazhat!");

        return true;
    }

    public static boolean validateMessage(String message) throws Exception {
        if(message == null) throw new Exception("Az üzenet valahogy null lett :/");
        if(message.length() > MESSAGE_MAX_LENGTH) throw new Exception(String.format("Az üzenet nem lehet %d karakternél hoszabb!", MESSAGE_MAX_LENGTH));
        if(!message.matches(MESSAGE_REGEX)) throw  new Exception("Az üzenet csak az angol ABC betűit, számokat és írásjeleket tartalmazhat.");

        return true;
    }

    public static boolean validateAddressee(String addressee) throws Exception {
        if(addressee == null) throw new Exception("A címzett valahogy null lett :/");
        if(addressee.isEmpty()) throw new Exception("A címzett nem lehet üres!");
        if(!addressee.contains("@")) throw  new Exception("Érvénytelen címzett, nincs benne @");
        if(!addressee.contains(".")) throw  new Exception("Érvénytelen címzett, nincs benne .");
        if(!addressee.matches(ADDRESSEE_REGEX)) throw  new Exception("A címzett csak az angol ABC betűit, számokat, pontot és kukacot tartalmazhat!");

        return true;
    }

    public String getSenderEmailAddress() {
        return senderEmailAddress;
    }

    public String getObject() {
        return object;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }
}
