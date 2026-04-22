package com.ToDoList.ToDoList.todolist.dto;

import com.ToDoList.ToDoList.todolist.model.TaskEntity;

public record TaskResponseDTO(Long id, String title, String status) {

    public TaskResponseDTO(TaskEntity task) {
        this(task.getId(), task.getTitle(), task.getStatus());;
    }
}
