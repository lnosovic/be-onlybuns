package com.example.custommq.repository;

import com.example.custommq.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.Optional;
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Message> findFirstByQueueNameOrderByCreatedAtAsc(String queueName);
}