package com.example.demo.mapper;


import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMapper {
    private User toEntity(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(user.getEmail());
        user.setRole(userDto.getRole());
        return user;
    }

    private UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        return userDto;
    }
}
