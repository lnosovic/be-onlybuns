package com.example.onlybuns.controller;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.mapper.LocationDTOMapper;
import com.example.onlybuns.model.Location;
import com.example.onlybuns.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
public class LocationController {
    @Autowired
    private LocationService locationService;
    @GetMapping("/all")
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationService.findAll();
        return ResponseEntity.ok(locations);
    }
    @PostMapping("/create")
    public ResponseEntity<Location> createLocation(@RequestBody LocationDTO locationDTO) {
        Location location = this.locationService.createLocation(locationDTO);
        return new ResponseEntity<>(location, HttpStatus.CREATED);
    }
}
