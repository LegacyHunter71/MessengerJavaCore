package org.example.model;

import java.time.LocalDate;

public class Message {

    private String text;
    private String author;
    private String recipient;
    private String date;

    public Message() {
    }

    public Message(String text, String author, String recipient, String date) {
        this.text = text;
        this.author = author;
        this.recipient = recipient;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", author='" + author + '\'' +
                ", recipient='" + recipient + '\'' +
                ", date=" + date +
                '}';
    }
}
