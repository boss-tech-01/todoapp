package com.myproject.todoapp.payload.request.user;

import com.myproject.todoapp.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDTO {
    @NotBlank(message = "Username must be entered.")
    @Size(min = 3, max = 32, message = "Username must be between {min}-{max} characters.")
    private String username;

    @Email
    @NotBlank(message = "Email must be entered.")
    private String email;

    @NotBlank(message = "Password must be entered.")
    @Size(min = 8, max = 64, message = "Password must be between {min}-{max} characters.")
    private String password;

    @NotEmpty(message = "At least one role must be selected.")
    private Set<Role> roles;

}
