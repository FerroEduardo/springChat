package com.ferroeduardo.springchat.Chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "mensagens_springchat")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    @Column(name = "usuario", nullable = false, length = 20)
    private String author;

    @Column(name = "data", nullable = false)
    private Date date;

    @Column(name = "mensagem", nullable = false, length = 200)
    private String message;

    @Column(name = "canal", nullable = false, length = 35)
    private String channel;

    @Column(name = "ip", nullable = false, length = 21)
    @JsonIgnore
    private String ip;

    @Transient
    private String color;

    public ChatMessage() {
    }

    public ChatMessage(String author, String message, Date date) {
        this.author = author;
        this.message = message;
        this.date = date;
    }

    public ChatMessage(String author, String message, Date date, String channel, String ip) {
        this.author = author;
        this.message = message;
        this.date = date;
        this.channel = channel;
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return author.equals(that.author) &&
                // date.equals() gets wrong result if compared to timestamp
                // date.compareTo() fixes that
                date.compareTo(that.date) == 0 &&
                message.equals(that.message) &&
                channel.equals(that.channel) &&
                ip.equals(that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, date, message, channel, ip);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", date=" + date +
                ", message='" + message + '\'' +
                ", channel='" + channel + '\'' +
                ", ip='" + ip + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
