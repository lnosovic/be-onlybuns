package com.example.onlybuns.service;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.model.Location;

import java.util.List;

public interface LocationService {
    Location findById(Integer id);
    List<Location> findAll();
    Location createLocation(LocationDTO locationDTO);
}
