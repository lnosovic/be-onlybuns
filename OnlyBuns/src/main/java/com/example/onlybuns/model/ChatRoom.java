package com.example.onlybuns.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Naziv grupe, može biti null za 1-na-1 četove
    private String name;

    // Da znamo da li je grupni ili 1-na-1
    @Column(nullable = false)
    private boolean isGroupChat;

    // Korisnici koji su deo ovog četa
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chatroom_participants",
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> participants = new HashSet<>();


    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonManagedReference("chatroom-messages")
    private Set<ChatMessage> messages = new HashSet<>();

    // Admin grupe, samo za grupne četove
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    // Getteri i Setteri

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}