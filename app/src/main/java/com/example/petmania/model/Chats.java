package com.example.petmania.model;

public class Chats {
    private String message;
    int sender,reciever;
    String timestamp;
    boolean isseen;

    public Chats(String message, int sender, int reciever, String timestamp,boolean isseen) {
        this.message = message;
        this.sender = sender;
        this.reciever = reciever;
        this.timestamp = timestamp;
        this.isseen = isseen;
    }

    public Chats() {

    }


    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReciever() {
        return reciever;
    }

    public void setReciever(int reciever) {
        this.reciever = reciever;
    }
}
