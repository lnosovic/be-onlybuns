package com.example.custommq.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "queue_name")
    private String queueName;

    @Column(nullable = false, columnDefinition = "TEXT") // TEXT tip za duže JSON poruke
    private String messageContent;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    public Message() {
        this.createdAt = LocalDateTime.now();
    }

    // Getteri i Setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}