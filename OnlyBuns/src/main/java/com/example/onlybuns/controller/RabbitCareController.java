package com.example.onlybuns.controller;

import com.example.onlybuns.dto.RabbitCareDTO;
import com.example.onlybuns.service.RabbitCareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rabbitCare")
public class RabbitCareController {
    @Autowired
    private RabbitCareService rabbitCareService;

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RabbitCareDTO>> getAll(){
        List<RabbitCareDTO> rabbitCareDTOs = rabbitCareService.findAll();
        return ResponseEntity.ok(rabbitCareDTOs);
    }
}
