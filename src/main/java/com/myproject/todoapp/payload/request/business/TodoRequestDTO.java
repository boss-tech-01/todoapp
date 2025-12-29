package com.myproject.todoapp.payload.request.business;

import com.myproject.todoapp.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoRequestDTO {

    @NotBlank(message = "A title must be entered.")
    @Size(min = 3, max = 128, message = "Title must be between {min}-{max} characters.")
    private String title;

    @Size(min = 3, max = 2000, message = "Description must be between {min}-{max} characters.")
    private String description;

    @FutureOrPresent
    private LocalDate dueDate;

    @NotNull(message = "A priority must be selected.")
    private Priority priority;
}
