package com.example.onlybuns.service;

import com.example.onlybuns.dto.RabbitCareDTO;
import com.example.onlybuns.model.RabbitCare;

import java.util.List;

public interface RabbitCareService {
    void save(RabbitCare rabbitCare);

    List<RabbitCareDTO> findAll();
}
