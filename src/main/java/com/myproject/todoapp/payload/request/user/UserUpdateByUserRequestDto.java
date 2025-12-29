package com.myproject.todoapp.payload.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateByUserRequestDto {
    @Size(min = 3, max = 32, message = "Username must be between {min}-{max} characters.")
    private String username;

    @Email
    private String email;
}
