package com.example.onlybuns.repository;

import com.example.onlybuns.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoom_IdOrderByTimestampDesc(Long chatRoomId);
    List<ChatMessage> findTop10ByChatRoom_IdOrderByTimestampDesc(Long chatRoomId);

}