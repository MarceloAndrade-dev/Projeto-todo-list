package com.ToDoList.ToDoList.todolist.service;

import com.ToDoList.ToDoList.security.model.UserEntity;
import com.ToDoList.ToDoList.todolist.dto.TaskRequestDTO;
import com.ToDoList.ToDoList.todolist.dto.TaskResponseDTO;
import com.ToDoList.ToDoList.todolist.exceptions.TaskNotFoundException;
import com.ToDoList.ToDoList.todolist.model.TaskEntity;
import com.ToDoList.ToDoList.todolist.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // 🔹 GET ALL
    public Page<TaskResponseDTO> getAll(Pageable pageable){
        return taskRepository.findAll(pageable)
                .map(task -> new TaskResponseDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getStatus()
                ));
    }

    // 🔹 GET BY ID
    public TaskResponseDTO getTaskByID(Long id){
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(TaskNotFoundException::new);

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getStatus()
        );
    }

    // 🔹 CREATE
    public TaskResponseDTO postTask(TaskRequestDTO dto){
        TaskEntity task = new TaskEntity();
        task.setTitle(dto.title());
        task.setStatus(dto.status());

        TaskEntity saved = taskRepository.save(task);

        return new TaskResponseDTO(
                saved.getId(),
                saved.getTitle(),
                saved.getStatus()
        );
    }

    // 🔹 UPDATE
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto, UserEntity user) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado: Você não é o dono desta tarefa.");
        }

        task.setTitle(dto.title());
        task.setStatus(dto.status());
        taskRepository.save(task);

        return new TaskResponseDTO(task);
    }

    // 🔹 DELETE
    public void deleteTask(Long id, UserEntity user) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado: Você não é o dono desta tarefa.");
        }

        taskRepository.delete(task);
    }
}