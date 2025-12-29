package com.myproject.todoapp.payload.request.user;

import com.myproject.todoapp.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDTO {

    @NotBlank(message = "Username must be provided.")
    @Size(min = 3, max = 32, message = "Username must be between {min}-{max} characters.")
    private String username;

    @Email
    @NotBlank(message = "Email must be provided.")
    private String email;

    @Size(min = 8, max = 32, message = "Password must be between {min}-{max} characters.")
    private String password;

    @NotNull(message = "Roles must be provided.")
    private Set<Role> roles;

}
