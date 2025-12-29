package com.myproject.todoapp.payload.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myproject.todoapp.enums.Role;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {

    private String id;

    private String username;

    private String email;

    private Set<Role> roles;

    private Boolean builtIn;

    private Boolean softDeleted;

    private Instant softDeletedAt;

    private Instant createdAt;

    private Instant updatedAt;
}
