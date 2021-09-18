package com.dusza;

import java.util.Date;

public class Email {
    private final String senderEmailAddress;
    private final String object;
    private final Date receivedDate;
    private boolean isRead;

    public Email(String senderEmailAddress, String object, Date receivedDate, boolean isRead) {
        this.senderEmailAddress = senderEmailAddress;
        this.object = object;
        this.receivedDate = receivedDate;
        this.isRead = isRead;
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

    public boolean isRead() {
        return isRead;
    }
}
