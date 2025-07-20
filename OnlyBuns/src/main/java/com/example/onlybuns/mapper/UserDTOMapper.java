package com.example.onlybuns.mapper;

import com.example.onlybuns.dto.LocationDTO;
import com.example.onlybuns.dto.UserDTO;
import com.example.onlybuns.dto.UserRequest;
import com.example.onlybuns.dto.UserViewDTO;
import com.example.onlybuns.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper {
    private static ModelMapper modelMapper;
    @Autowired
    public UserDTOMapper(ModelMapper modelMapper) {
       this.modelMapper = modelMapper;
    }
    public static User fromDTOtoUser(UserRequest dto) {
        return modelMapper.map(dto, User.class);
    }
    public static UserRequest fromUsertoDTO(User dto) {
        return modelMapper.map(dto,UserRequest.class);
    }
    public UserViewDTO toDTO(User user) {
        if (user == null) return null;

        UserViewDTO dto = new UserViewDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setRole(user.getRole());
        dto.setEmail(user.getEmail());

        if (user.getAddress() != null) {
            dto.setLocation(new LocationDTO(user.getAddress()));
        }

        dto.setPostCount(user.getPostCount());
        dto.setFollowerCount(user.getFollowerCount());
        dto.setFollowingCount(user.getFollowingCount());

        return dto;
    }
}
