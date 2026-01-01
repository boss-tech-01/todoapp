package com.myproject.todoapp.config;

import com.myproject.todoapp.entity.user.User;
import com.myproject.todoapp.enums.Role;
import com.myproject.todoapp.payload.mappers.UserMapper;
import com.myproject.todoapp.payload.request.user.UserCreateRequestDTO;
import com.myproject.todoapp.repository.user.UserRepository;
import com.myproject.todoapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DefaultAdminCreator implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void run(String... args) throws Exception {

        // If the built-in admin already exists, do not proceed with the operation.
        if (userRepository.existsByBuiltInTrue()) {
            System.out.println("Built-in Admin already exists. Skipping creation.");
            return;
        }

        final String DEFAULT_ADMIN_USERNAME = "defaultadmin";
        final String DEFAULT_ADMIN_EMAIL = "admin@todoapp.com";
        final String DEFAULT_ADMIN_PASSWORD = "Password123!";

        UserCreateRequestDTO adminDto = new UserCreateRequestDTO();
        adminDto.setUsername(DEFAULT_ADMIN_USERNAME);
        adminDto.setEmail(DEFAULT_ADMIN_EMAIL);
        adminDto.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
        adminDto.setRoles(Set.of(Role.ADMIN, Role.USER));

//      userService.create(adminDto);

        User user = userMapper.mapUserCreateRequestDTOToUser(adminDto);
        user.setBuiltIn(Boolean.TRUE);

        userRepository.save(user);

        System.out.println("Default Built-in Admin created successfully.");
    }
}
