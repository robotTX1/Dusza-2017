package com.dusza;

import java.util.Date;

public class Email {
    private String senderEmailAddress;
    private String object;
    private Date receivedDate;
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

    public boolean isRead() {
        return isRead;
    }
}
