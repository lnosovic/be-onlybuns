package com.example.onlybuns.mapper;

import com.example.onlybuns.dto.UserDTO;
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
    public static User fromDTOtoUser(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }
    public static UserDTO fromUsertoDTO(User dto) {
        return modelMapper.map(dto,UserDTO.class);
    }

}
