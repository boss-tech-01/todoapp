package com.myproject.todoapp.payload.mappers;

import com.myproject.todoapp.entity.business.Todo;
import com.myproject.todoapp.payload.request.business.TodoRequestDTO;
import com.myproject.todoapp.payload.response.business.TodoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public Todo mapTodoRequestDTOToTodo(TodoRequestDTO dto){
        return Todo.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dueDate(dto.getDueDate())
                .priority(dto.getPriority())
                .completed(Boolean.FALSE)
                .softDeleted(Boolean.FALSE)
                .build();
    }

    public TodoResponseDTO mapTodoToTodoResponseDTO(Todo todo){
        return TodoResponseDTO.builder()
                .id(todo.getId())
                .userId(todo.getUserId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .dueDate(todo.getDueDate())
                .priority(todo.getPriority())
                .completed(todo.getCompleted())
                .completedAt(todo.getCompletedAt())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }

}
