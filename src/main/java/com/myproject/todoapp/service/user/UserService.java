package com.myproject.todoapp.service.user;

import com.myproject.todoapp.entity.user.User;
import com.myproject.todoapp.exception.BadRequestException;
import com.myproject.todoapp.exception.ObjectNotFoundException;
import com.myproject.todoapp.payload.mappers.UserMapper;
import com.myproject.todoapp.payload.messages.ErrorMessages;
import com.myproject.todoapp.payload.messages.SuccessMessages;
import com.myproject.todoapp.payload.request.user.UserChangePasswordRequestDTO;
import com.myproject.todoapp.payload.request.user.UserCreateRequestDTO;
import com.myproject.todoapp.payload.request.user.UserUpdateByUserRequestDto;
import com.myproject.todoapp.payload.request.user.UserUpdateRequestDTO;
import com.myproject.todoapp.payload.response.user.UserResponseDTO;
import com.myproject.todoapp.repository.user.UserRepository;
import com.myproject.todoapp.service.validator.UniqueUserPropertyChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UniqueUserPropertyChecker propertyChecker;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ObjectNotFoundException(String.format(ErrorMessages.USERNAME_NOT_FOUND, username))
        );
    }

    public String create(UserCreateRequestDTO dto) {

        //boolean builtInAdminExists = userRepository.existsByBuiltInTrue();
        propertyChecker.checkAllUniqueProps(dto.getUsername(), dto.getEmail());

        User mapped = userMapper.mapUserCreateRequestDTOToUser(dto);
        /*
        if (!builtInAdminExists) {
            mapped.setBuiltIn(Boolean.TRUE);
            mapped.setRoles(Set.of(Role.ADMIN));
        } else {
            mapped.setBuiltIn(Boolean.FALSE);
            mapped.setRoles(dto.getRoles());
        }
        */
        mapped.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(mapped);

        return SuccessMessages.USER_CREATE_SUCCESS;
    }

    public String updateUserByAdmin(String id, UserUpdateRequestDTO dto) {
        User found = findUserByIdInternal(id);

        found.setUsername(dto.getUsername());
        found.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()){
            found.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        found.setRoles(dto.getRoles());
        found.setUpdatedAt(Instant.now());

        userRepository.save(found);

        return SuccessMessages.USER_UPDATE_SUCCESS;

    }

    public String updateSelf(UserUpdateByUserRequestDto dto, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User currentUser = findByUsername(username);

        //check builtin
        checkIfBuiltIn(currentUser);

        //check duplicate
        propertyChecker.checkUniquePropsForUpdate(currentUser, dto);

        currentUser.setUsername(dto.getUsername());
        currentUser.setEmail(dto.getEmail());
        currentUser.setUpdatedAt(Instant.now());

        userRepository.save(currentUser);

        return SuccessMessages.USER_UPDATE_SUCCESS;
    }

    private static void checkIfBuiltIn(User user) {
        if (Boolean.TRUE.equals(user.getBuiltIn())) {
            throw new BadRequestException(ErrorMessages.BUILT_IN_ADMIN_CANNOT_BE_UPDATED);
        }
    }

    public UserResponseDTO getUserById(String id) {
        return userMapper.mapUserToUserResponseDTO(findUserByIdInternal(id));
    }

    private User findUserByIdInternal(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_BY_ID, id))
        );
    }


    public String delete(String id) {
        User user = findUserByIdInternal(id);
        checkIfBuiltIn(user);

        if (user.getSoftDeleted()){
            //The user will be deleted from the database. Therefore, their to-dos should also be deleted.
            userRepository.delete(user);
            //! todoService.deleteAll(todoService.findAllTodosByUserId(id));
        }else {
            //Soft delete means that if the user returns, the to-dos should remain as they were when they left them.
            user.setSoftDeleted(true);
            user.setSoftDeletedAt(Instant.now());
            userRepository.save(user);
        }

        return SuccessMessages.USER_DELETE_SUCCESS;
    }

    public Page<UserResponseDTO> findAll(String q, Boolean softDeleted, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();
        if (q != null && !q.isEmpty()) {
            criteriaList.add(
                    new Criteria().orOperator(
                            Criteria.where("username").regex(q, "i"),
                            Criteria.where("email").regex(q, "i")
                    )
            );
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        if (softDeleted != null) {
            criteriaList.add(Criteria.where("softDeleted").is(softDeleted));
        }

        long total = mongoTemplate.count(query, User.class);

        query.with(pageable);
        List<User> users = mongoTemplate.find(query, User.class);

        return new PageImpl<>(
                users.stream().map(userMapper::mapUserToUserResponseDTO).toList(),
                pageable,
                total
        );
    }

    public String changePassword(HttpServletRequest request, UserChangePasswordRequestDTO dto) {
        String username = (String) request.getAttribute("username");

        User foundUser = findByUsername(username);

        //matches should be used
        if (!dto.getOldPassword().equals(foundUser.getPassword())) {
            throw new BadRequestException(ErrorMessages.PASSWORD_INVALID);
        }

        foundUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(foundUser);

        return "Password updated successfully.";
    }
}
