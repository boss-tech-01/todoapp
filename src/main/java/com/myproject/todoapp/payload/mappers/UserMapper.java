package com.myproject.todoapp.payload.mappers;

import com.myproject.todoapp.entity.user.User;
import com.myproject.todoapp.payload.request.user.UserCreateRequestDTO;
import com.myproject.todoapp.payload.request.user.UserRegisterRequestDTO;
import com.myproject.todoapp.payload.response.user.UserResponseDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserMapper {
    public User mapRegisterRequestDTOToUser(UserRegisterRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .softDeleted(Boolean.FALSE)
                .builtIn(Boolean.FALSE)
                .build();
    }

    public User mapUserCreateRequestDTOToUser(UserCreateRequestDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .softDeleted(Boolean.FALSE)
                .password(dto.getPassword())
                .builtIn(Boolean.FALSE)
                .createdAt(Instant.now())
                .roles(dto.getRoles())
                .build();
    }

    public UserResponseDTO mapUserToUserResponseDTO(User found) {
        return UserResponseDTO.builder()
                .id(found.getId())
                .username(found.getUsername())
                .email(found.getEmail())
                .roles(found.getRoles())
                .builtIn(found.getBuiltIn())
                .softDeleted(found.getSoftDeleted())
                .softDeletedAt(found.getSoftDeletedAt())
                .createdAt(found.getCreatedAt())
                .updatedAt(found.getUpdatedAt())
                .build();

    }
}
