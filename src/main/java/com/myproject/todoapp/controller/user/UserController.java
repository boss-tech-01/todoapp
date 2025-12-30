package com.myproject.todoapp.controller.user;

import com.myproject.todoapp.payload.request.user.UserChangePasswordRequestDTO;
import com.myproject.todoapp.payload.request.user.UserCreateRequestDTO;
import com.myproject.todoapp.payload.request.user.UserUpdateByUserRequestDto;
import com.myproject.todoapp.payload.request.user.UserUpdateRequestDTO;
import com.myproject.todoapp.payload.response.user.UserResponseDTO;
import com.myproject.todoapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //1-create user
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> create(@RequestBody @Valid UserCreateRequestDTO dto) {
        System.out.println("SA");
        return new ResponseEntity<>(userService.create(dto), HttpStatus.CREATED);
    }

    //2-update user by admin
    @PutMapping("/update-user/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> updateUserByAdmin(
            @PathVariable String id,
            @RequestBody UserUpdateRequestDTO dto
    ) {
        return ResponseEntity
                .ok(userService.updateUserByAdmin(id, dto));
    }

    //3-update user by user
    @PatchMapping("/update-self")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<String> updateSelf(
            @RequestBody @Valid UserUpdateByUserRequestDto dto, HttpServletRequest request
    ) {
        return ResponseEntity
                .ok(userService.updateSelf(dto, request));
    }

    //4-get user by id
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // 5- Delete User By Admin
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.delete(id));
    }

    //6-List All Users
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> findAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean softDeleted,
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size,
            @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(defaultValue = "ASC", required = false) Sort.Direction order
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, order, sortBy);
        return ResponseEntity.ok(userService.findAll(q, softDeleted, pageable));
    }

    //7-Change Password
    @PostMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<String> changePassword(HttpServletRequest request,
                                                 @RequestBody @Valid UserChangePasswordRequestDTO dto) {
        return ResponseEntity.ok(userService.changePassword(request, dto));
    }
}
