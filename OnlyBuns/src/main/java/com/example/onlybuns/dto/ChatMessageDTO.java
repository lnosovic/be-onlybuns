package com.example.onlybuns.dto;

// Koristimo za prijem poruke sa frontenda
public class ChatMessageDTO {
    private String content;
    private Long roomId;
    //private String senderUsername;

    // Getteri i Setteri
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

   //public String getSenderUsername() { return senderUsername; }
    // void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
}