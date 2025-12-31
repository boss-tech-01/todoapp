package com.myproject.todoapp.service.user;

import com.myproject.todoapp.entity.user.User;
import com.myproject.todoapp.enums.Role;
import com.myproject.todoapp.payload.mappers.UserMapper;
import com.myproject.todoapp.payload.request.user.UserRegisterRequestDTO;
import com.myproject.todoapp.repository.user.UserRepository;
import com.myproject.todoapp.service.validator.UniqueUserPropertyChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UniqueUserPropertyChecker propertyChecker;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public String register(UserRegisterRequestDTO dto) {
        propertyChecker.checkAllUniqueProps(dto.getUsername(), dto.getEmail());

        User user = userMapper.mapRegisterRequestDTOToUser(dto);

        Set<Role> roles = Set.of(Role.USER);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setCreatedAt(Instant.now());

        userRepository.save(user);

        return "Register successful.";
    }
}
