package com.myproject.todoapp.controller.business;

import com.myproject.todoapp.enums.Priority;
import com.myproject.todoapp.payload.request.business.TodoRequestDTO;
import com.myproject.todoapp.payload.response.business.TodoResponseDTO;
import com.myproject.todoapp.service.business.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    //1 - Create To-do
    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<Map<String, Object>> saveTodo(@RequestBody @Valid TodoRequestDTO dto,
                                                        HttpServletRequest request){
        return new ResponseEntity<>(todoService.save(dto, request), HttpStatus.CREATED);
    }

    //2 - List To-dos
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<Page<TodoResponseDTO>> findAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction order,
            HttpServletRequest request
    ){
        Pageable pageable = PageRequest.of(page - 1, size, order, sortBy);
        return ResponseEntity.ok(todoService.findAll(q, completed, dueFrom, dueTo, priority, pageable, request));
    }

    //3 - Find by Id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<TodoResponseDTO> findTodoById(@PathVariable String id, HttpServletRequest request){
        return ResponseEntity.ok(todoService.findById(id, request));
    }

    //4 - PUT Update
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<TodoResponseDTO> updateTodoById(@PathVariable String id,
                                                          @RequestBody @Valid TodoRequestDTO dto,
                                                          HttpServletRequest request){
        return ResponseEntity.ok(todoService.update(id, dto, request));
    }

    //5 - PATCH CompleteToggle
    @PatchMapping("/toggle-complete/{id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<TodoResponseDTO> toggleCompleted(@PathVariable String id, HttpServletRequest request){
        return ResponseEntity.ok(todoService.completeToggle(id, request));
    }

    //6 - DELETE
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<Void> delete(@PathVariable String id, HttpServletRequest request){

        todoService.delete(id, request);

        return ResponseEntity.noContent().build();
    }

    //7 - PATCH Undo Delete
    @PatchMapping("/undo-delete/{id}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<String> undoDelete(@PathVariable String id, HttpServletRequest request){
        return ResponseEntity.ok(todoService.undoDelete(id, request));
    }
}
