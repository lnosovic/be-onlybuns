package com.example.onlybuns.mapper;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.model.Location;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationDTOMapper {
    private static ModelMapper modelMapper;
    @Autowired
    public LocationDTOMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }
    public static Location fromDTOtoLocation(LocationDTO dto) { return modelMapper.map(dto, Location.class); }
    public static LocationDTO fromLocationToDTO(Location dto) { return modelMapper.map(dto, LocationDTO.class); }

}
