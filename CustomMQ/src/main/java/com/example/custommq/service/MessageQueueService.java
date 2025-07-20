package com.example.custommq.service;

import com.example.custommq.model.Message;
import com.example.custommq.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class MessageQueueService {


    @Autowired
    private MessageRepository messageRepository;

    // ObjectMapper koristimo za konverziju objekta u JSON string i obrnuto
    private final ObjectMapper objectMapper = new ObjectMapper();


    public void sendMessage(String queueName, Object message) {
        try {
            // 1. Konvertuj dolazeći objekat u JSON string
            String messageJson = objectMapper.writeValueAsString(message);

            // 2. Kreiraj novi entitet za bazu
            Message persistentMessage = new Message();
            persistentMessage.setQueueName(queueName);
            persistentMessage.setMessageContent(messageJson);

            // 3. Sačuvaj entitet u bazu
            messageRepository.save(persistentMessage);
            System.out.println("Poruka sačuvana u bazu za red '" + queueName + "'");

        } catch (JsonProcessingException e) {
            // U slučaju greške pri konverziji u JSON
            throw new RuntimeException("Greška pri serijalizaciji poruke u JSON.", e);
        }
    }

    // Metoda za prijem poruke
    // @Transactional osigurava da su čitanje i brisanje jedna atomska operacija.
    // Ako brisanje ne uspe, cela operacija se poništava (rollback).
    @Transactional
    public Optional<String> receiveMessage(String queueName) {
        // 1. Pronađi najstariju poruku za dati red
        Optional<Message> optionalMessage = messageRepository.findFirstByQueueNameOrderByCreatedAtAsc(queueName);

        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();

            // 2. Obriši poruku iz baze da ne bi bila ponovo pročitana
            messageRepository.delete(message);
            System.out.println("Poruka ID=" + message.getId() + " pročitana i obrisana iz baze.");

            // 3. Vrati sadržaj poruke (JSON string)
            return Optional.of(message.getMessageContent());
        }

        // Ako nema poruka, vrati prazan Optional
        return Optional.empty();
    }
}
