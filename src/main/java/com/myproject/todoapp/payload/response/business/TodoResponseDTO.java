package com.myproject.todoapp.payload.response.business;

import com.myproject.todoapp.enums.Priority;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoResponseDTO {
    private String id;

    private String userId;

    private String title;

    private String description;

    private LocalDate dueDate;

    private Priority priority;

    private Boolean completed;

    private Instant completedAt;

    private Instant createdAt;

    private Instant updatedAt;
}
