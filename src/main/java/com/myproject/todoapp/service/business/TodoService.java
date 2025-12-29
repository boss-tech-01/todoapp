package com.myproject.todoapp.service.business;

import com.myproject.todoapp.entity.business.Todo;
import com.myproject.todoapp.entity.user.User;
import com.myproject.todoapp.enums.Priority;
import com.myproject.todoapp.enums.Role;
import com.myproject.todoapp.exception.BadRequestException;
import com.myproject.todoapp.exception.ObjectNotFoundException;
import com.myproject.todoapp.payload.mappers.TodoMapper;
import com.myproject.todoapp.payload.messages.ErrorMessages;
import com.myproject.todoapp.payload.messages.SuccessMessages;
import com.myproject.todoapp.payload.request.business.TodoRequestDTO;
import com.myproject.todoapp.payload.response.business.TodoResponseDTO;
import com.myproject.todoapp.repository.business.TodoRepository;
import com.myproject.todoapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserService userService;
    private final TodoMapper todoMapper;
    private final MongoTemplate mongoTemplate;

    public Map<String, Object> save(TodoRequestDTO dto, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username);

        Todo todo = todoMapper.mapTodoRequestDTOToTodo(dto);
        todo.setUserId(user.getId());

        todo.setCreatedAt(Instant.now());
        Todo savedTodo = todoRepository.save(todo);

        Map<String, Object> map = new HashMap<>();
        map.put("message", SuccessMessages.TODO_CREATE_SUCCESS);
        map.put("todo", todoMapper.mapTodoToTodoResponseDTO(savedTodo));

        return map;
    }

    public Page<TodoResponseDTO> findAll(String q, Boolean completed, LocalDate dueFrom, LocalDate dueTo,
                                         Priority priority, Pageable pageable, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username);

        List<Criteria> criteriaList = new ArrayList<>();

        if (!user.getRoles().contains(Role.ADMIN)){
            criteriaList.add(Criteria.where("userId").is(user.getId()));
        }

        if (q != null && !q.isBlank()) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("title").regex(q, "i"),
                    Criteria.where("description").regex(q, "i")
            ));
        }

        if (completed != null) {
            criteriaList.add(Criteria.where("completed").is(completed));
        }

        if (dueFrom != null && dueTo != null) {
            criteriaList.add(Criteria.where("dueDate").gte(dueFrom).lte(dueTo));
        } else if (dueFrom != null) {
            criteriaList.add(Criteria.where("dueDate").gte(dueFrom));
        } else if (dueTo != null) {
            criteriaList.add(Criteria.where("dueDate").lte(dueTo));
        }

        if (priority != null) {
            criteriaList.add(Criteria.where("priority").is(priority));
        }

        Query query = new Query();

        if (!criteriaList.isEmpty()){
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Todo.class);

        query.with(pageable);

        List<Todo> foundTodos = mongoTemplate.find(query, Todo.class);

        List<TodoResponseDTO> todoResponseDTOs = foundTodos.stream().map(todoMapper::mapTodoToTodoResponseDTO).toList();

        return new PageImpl<>(todoResponseDTOs, pageable, total);
    }

    private Todo findById(String id){
        return todoRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(String.format(ErrorMessages.TODO_ID_NOT_FOUND, id))
        );
    }

    public List<Todo> findAllTodosByUserId(String userId){
        return todoRepository.findByUserId(userId);
    }

    private void checkIfUserOwnsTodo(User user, Todo todo){
        if (!user.getRoles().contains(Role.ADMIN) && !todo.getUserId().equals(user.getId())){
            throw new BadRequestException(ErrorMessages.TODO_OWNER_DIFFERENT);
        }
    }

    public TodoResponseDTO findById(String id, HttpServletRequest request) {
        Todo todo = findById(id);

        String username = (String) request.getAttribute("username");
        User user = userService.findByUsername(username);

        checkIfUserOwnsTodo(user, todo);

        return todoMapper.mapTodoToTodoResponseDTO(todo);
    }

    public TodoResponseDTO update(String id, TodoRequestDTO dto, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        User user = userService.findByUsername(username);

        Todo todo = findById(id);

        checkIfUserOwnsTodo(user, todo);

        //We are currently at the point where we can update the to-do list.

        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setDueDate(dto.getDueDate());
        todo.setPriority(dto.getPriority());

        todo.setUpdatedAt(Instant.now());
        Todo updated = todoRepository.save(todo);

        //A shorter version of what was done above.
        /*
        Criteria criteria = Criteria.where("_id").is(id);
        if (!user.getRoles().contains(Role.ADMIN)) {
            criteria = criteria.and("userId").is(user.getId());
        }

        Query q = new Query(criteria);
        To.do to.do = mongoTemplate.findOne(q, To.do.class);

        to.do.setTitle(dto.getTitle());
        to.do.setDescription(dto.getDescription());
        to.do.setDueDate(dto.getDueDate());
        to.do.setPriority(dto.getPriority());

        mongoTemplate.save(to.do);
        */

        return todoMapper.mapTodoToTodoResponseDTO(updated);
    }

    public TodoResponseDTO completeToggle(String id, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        User user = userService.findByUsername(username);

        Todo todo = findById(id);

        checkIfUserOwnsTodo(user, todo);

        if (todo.getCompleted()){
            todo.setCompleted(Boolean.FALSE);
            todo.setCompletedAt(null);
        }else{
            todo.setCompleted(Boolean.TRUE);
            todo.setCompletedAt(Instant.now());
        }

        Todo updated = todoRepository.save(todo);

        return todoMapper.mapTodoToTodoResponseDTO(updated);
    }

    public void delete(String id, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        User user = userService.findByUsername(username);

        Todo todo = findById(id);

        checkIfUserOwnsTodo(user, todo);

        if (todo.getSoftDeleted()){
            todoRepository.delete(todo);
        } else {
            todo.setSoftDeleted(Boolean.TRUE);
            todo.setSoftDeletedAt(Instant.now());
        }
    }

    public String undoDelete(String id, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        User user = userService.findByUsername(username);

        Todo todo = findById(id);

        checkIfUserOwnsTodo(user, todo);

        if (todo.getSoftDeleted()){
            todo.setSoftDeleted(Boolean.FALSE);
            todo.setSoftDeletedAt(null);
        }else{
            throw new BadRequestException(ErrorMessages.TODO_ALREADY_NOT_DELETED);
        }

        return "Todo undeleted successfully.";
    }

    public void deleteAll(Collection<Todo> todos){
        todoRepository.deleteAll(todos);
    }
}
