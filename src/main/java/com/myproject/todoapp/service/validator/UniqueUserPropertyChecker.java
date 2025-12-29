package com.myproject.todoapp.service.validator;

import com.myproject.todoapp.entity.user.User;
import com.myproject.todoapp.exception.ConflictException;
import com.myproject.todoapp.payload.messages.ErrorMessages;
import com.myproject.todoapp.payload.request.user.UserUpdateByUserRequestDto;
import com.myproject.todoapp.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueUserPropertyChecker {
    private final UserRepository userRepository;

    public void checkAllUniqueProps(String username, String email){
        boolean emailExists = userRepository.existsByEmail(email);
        boolean usernameExists = userRepository.existsByUsername(username);

        if (emailExists && usernameExists){
            throw new ConflictException(ErrorMessages.USERNAME_EMAIL_ALREADY_EXISTS);
        }else if (usernameExists){
            throw new ConflictException(ErrorMessages.USERNAME_ALREADY_EXISTS);
        } else if (emailExists){
            throw new ConflictException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }
    }

    public void checkUniquePropsForUpdate(User user, UserUpdateByUserRequestDto dto){
        boolean usernameChanged = false;
        boolean emailChanged = false;

        if (!user.getUsername().equals(dto.getUsername())) usernameChanged = true;
        if (!user.getEmail().equals(dto.getEmail())) emailChanged = true;

        if (usernameChanged && userRepository.existsByUsername(dto.getUsername())){
            throw new ConflictException(ErrorMessages.USERNAME_ALREADY_EXISTS);
        } else if (emailChanged && userRepository.existsByEmail(dto.getEmail())){
            throw new ConflictException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }
    }
}
