package com.example.custommq.controller;

import com.example.custommq.service.MessageQueueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/broker")
public class MessageQueueController {
    @Autowired
    private MessageQueueService messageQueueService;

    @PostMapping("/send/{queueName}")
    public ResponseEntity<Void> sendMessage(@PathVariable String queueName, @RequestBody Object message) {
        messageQueueService.sendMessage(queueName, message);
        return ResponseEntity.ok().build();
    }

    // Vraćaćemo JSON string, pa je tip ResponseEntity<String>
    @GetMapping("/receive/{queueName}")
    public ResponseEntity<String> receiveMessage(@PathVariable String queueName) {
        Optional<String> message = messageQueueService.receiveMessage(queueName);

        // Ako je poruka pronađena, vrati je sa statusom 200 OK.
        // Ako nije, vrati status 204 No Content.
        return message
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }
}
