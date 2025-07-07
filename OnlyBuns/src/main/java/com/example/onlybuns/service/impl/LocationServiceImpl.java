package com.example.onlybuns.service.impl;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.mapper.LocationDTOMapper;
import com.example.onlybuns.model.Location;
import com.example.onlybuns.repository.LocationRepository;
import com.example.onlybuns.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    private LocationRepository locationRepository;

    @Override
    public Location findById(Integer id) {
        return locationRepository.findById(id).get();
    }

    @Override
    public List<Location> findAll() throws AccessDeniedException {
        return locationRepository.findAll();
    }

    @Override
    public Location createLocation(LocationDTO locDto) {
        Location loc = LocationDTOMapper.fromDTOtoLocation(locDto);
        loc = locationRepository.save(loc);
        return loc;
    }

}
