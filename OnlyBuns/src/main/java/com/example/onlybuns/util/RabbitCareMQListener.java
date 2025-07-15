package com.example.onlybuns.util;

import com.example.onlybuns.model.RabbitCare;
import com.example.onlybuns.service.RabbitCareService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitCareMQListener {
    @Autowired
    private RabbitCareService rabbitCareService;

    @RabbitListener(queues="${myqueue}")
    private void handler(RabbitCare message){
        System.out.println("Primljena poruka: " + message.getName());
        rabbitCareService.save(message);
    }
}
