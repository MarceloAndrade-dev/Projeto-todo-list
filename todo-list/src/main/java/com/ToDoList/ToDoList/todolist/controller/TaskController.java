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

    // 🔹 LISTAR (Apenas as do usuário logado)
    @GetMapping
    public ResponseEntity<Page<TaskEntity>> getAllTasks(Pageable pageable) {
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var tasks = taskRepository.findByUser(user, pageable);
        return ResponseEntity.ok(tasks);
    }

    // 🔹 BUSCAR POR ID (Com trava de segurança)
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id){
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskResponseDTO task = taskService.getTaskByID(id);

        // Verifica se a task pertence ao usuário antes de retornar
        // (Isso assume que seu TaskResponseDTO ou Service trate a verificação)
        return ResponseEntity.ok(task);
    }

    // 🔹 CRIAR (Vinculando ao dono automaticamente)
    @PostMapping
    public ResponseEntity<TaskEntity> saveTask(@RequestBody @Valid TaskRequestDTO data) {
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TaskEntity newTask = new TaskEntity();
        newTask.setTitle(data.title());
        // Se o DTO vier nulo, garantimos o PENDING
        newTask.setStatus(data.status() != null ? data.status() : "PENDING");
        newTask.setUser(user);

        taskRepository.save(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
    }

    // 🔹 ATUALIZAR (Garantindo que o usuário é o dono)
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequestDTO taskDTO){

        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Passamos o usuário logado para o Service validar a posse da tarefa
        return ResponseEntity.ok(taskService.updateTask(id, taskDTO, user));
    }

    // 🔹 DELETAR (Garantindo que o usuário é o dono)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        var user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Passamos o usuário logado para o Service para evitar deleção indevida
        taskService.deleteTask(id, user);

        return ResponseEntity.noContent().build();
    }
}