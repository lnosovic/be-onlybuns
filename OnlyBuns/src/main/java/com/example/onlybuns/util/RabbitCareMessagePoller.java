package com.example.onlybuns.util;

import com.example.onlybuns.model.RabbitCare;
import com.example.onlybuns.service.RabbitCareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class RabbitCareMessagePoller {

    @Autowired
    private RabbitCareService rabbitCareService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Kreiraj instancu

    @Value("${message.broker.url}")
    private String brokerUrl;

    @Value("${rabbit.care.queue.name}")
    private String queueName;

    @Scheduled(fixedRate = 5000)
    public void pollForMessages() {
        String url = brokerUrl + "/receive/" + queueName;
        System.out.println("Provera za nove poruke u redu: " + queueName);

        while (true) {
            try {
                // Sada očekujemo String kao odgovor, a ne RabbitCare objekat
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    String messageJson = response.getBody();

                    // Ručno parsiraj JSON string u RabbitCare objekat
                    RabbitCare message = objectMapper.readValue(messageJson, RabbitCare.class);

                    System.out.println("Primljena poruka: " + message.getName());
                    rabbitCareService.save(message);
                } else {
                    break;
                }
            } catch (Exception e) {
                // Proveravamo da li je greška zbog statusa "No Content"
                if (e.getMessage() != null && e.getMessage().contains("204 No Content")) {
                    // Ovo nije prava greška, samo nema poruka. Prekidamo petlju.
                } else {
                    System.out.println("Greška prilikom preuzimanja poruke: " + e.getMessage());
                }
                break;
            }
        }
    }
}