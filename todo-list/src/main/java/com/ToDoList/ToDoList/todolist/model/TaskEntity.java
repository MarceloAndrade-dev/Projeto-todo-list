package com.ToDoList.ToDoList.todolist.model;

import com.ToDoList.ToDoList.security.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    private String status = "PENDING";
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
