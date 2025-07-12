package com.example.onlybuns.repository;

import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.onlybuns.model.User;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // Metoda koja pronalazi sve čet sobe u kojima je određeni korisnik učesnik
    // "Participants_Id" znači da pretražujemo po ID-u unutar liste učesnika
    List<ChatRoom> findByParticipantsId(Long userId);
    List<ChatRoom> findByParticipantsContains(User user);
    List<ChatRoom> findByParticipantsContainsAndParticipantsContains(User user1, User user2);
    @Query("SELECT u FROM ChatRoom cr JOIN cr.participants u WHERE cr.id = :chatRoomId")
    List<User> findParticipantsByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}