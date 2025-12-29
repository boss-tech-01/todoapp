package com.myproject.todoapp.payload.request.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserChangePasswordRequestDTO {
    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 8, max = 64, message = "Password must be between {min}-{max} characters.")
    private String newPassword;
}
