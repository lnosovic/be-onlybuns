package com.example.onlybuns.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sadržaj poruke
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Vreme kada je poruka poslata
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    // Korisnik koji je poslao poruku
    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonBackReference("user-messages")
    private User sender;

    // Čet soba kojoj poruka pripada
    @ManyToOne(optional = false)
    @JoinColumn(name = "chatroom_id", nullable = false)
    @JsonBackReference("chatroom-messages")
    private ChatRoom chatRoom;

    // Getteri i Setteri

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}