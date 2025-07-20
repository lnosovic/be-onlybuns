package com.example.onlybuns.service.impl;

import com.example.onlybuns.dto.RabbitCareDTO;
import com.example.onlybuns.model.RabbitCare;
import com.example.onlybuns.repository.RabbitCareRepository;
import com.example.onlybuns.service.RabbitCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RabbitCareServiceImpl implements RabbitCareService {
    @Autowired
    private RabbitCareRepository rabbitCareRepository;

    public void save(RabbitCare rabbitCare){
        rabbitCareRepository.save(rabbitCare);
    }
    public List<RabbitCareDTO> findAll(){
        List<RabbitCare> rabbitCares = rabbitCareRepository.findAll();
        List<RabbitCareDTO> rabbitCareDTOs = new ArrayList<>();
        for(RabbitCare rabbitCare : rabbitCares){
            RabbitCareDTO rabbitCareDTO = new RabbitCareDTO();
            rabbitCareDTO.setId(rabbitCare.getId());
            rabbitCareDTO.setName(rabbitCare.getName());
            rabbitCareDTO.setLongitude(rabbitCare.getLongitude());
            rabbitCareDTO.setLatitude(rabbitCare.getLatitude());
            rabbitCareDTO.setCountry(rabbitCare.getCountry());
            rabbitCareDTO.setCity(rabbitCare.getCity());
            rabbitCareDTOs.add(rabbitCareDTO);
        }
        return rabbitCareDTOs;
    }
}
