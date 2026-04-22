package com.ToDoList.ToDoList.todolist.controller;

import com.ToDoList.ToDoList.security.model.UserEntity;
import com.ToDoList.ToDoList.todolist.dto.TaskRequestDTO;
import com.ToDoList.ToDoList.todolist.dto.TaskResponseDTO;
import com.ToDoList.ToDoList.todolist.model.TaskEntity;
import com.ToDoList.ToDoList.todolist.repository.TaskRepository;
import com.ToDoList.ToDoList.todolist.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    // 🔹 LISTAR
    @GetMapping
    public ResponseEntity<Page<TaskEntity>> getAllTasks(Pageable pageable) {
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var tasks = taskRepository.findByUser(user, pageable);
        return ResponseEntity.ok(tasks);
    }

    // 🔹 BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id){
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskResponseDTO task = taskService.getTaskByID(id);

        return ResponseEntity.ok(task);
    }

    // 🔹 CRIAR
    @PostMapping
    public ResponseEntity<TaskEntity> saveTask(@RequestBody @Valid TaskRequestDTO data) {
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TaskEntity newTask = new TaskEntity();
        newTask.setTitle(data.title());

        newTask.setStatus(data.status() != null ? data.status() : "PENDING");
        newTask.setUser(user);

        taskRepository.save(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    // 🔹 ATUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequestDTO taskDTO){

        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(taskService.updateTask(id, taskDTO, user));
    }

    // 🔹 DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        taskService.deleteTask(id, user);

        return ResponseEntity.noContent().build();
    }
}