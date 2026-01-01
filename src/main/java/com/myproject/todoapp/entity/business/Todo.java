package com.myproject.todoapp.entity.business;

import com.myproject.todoapp.enums.Priority;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Todo {
    @Id
    private String id;

    private String userId;

    private String title;

    private String description;

    private LocalDate dueDate;

    private Priority priority;

    private Boolean completed;

    private Instant completedAt;

    private Boolean softDeleted;

    private Instant softDeletedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
