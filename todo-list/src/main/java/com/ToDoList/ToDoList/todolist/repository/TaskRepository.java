package com.ToDoList.ToDoList.todolist.repository;

import com.ToDoList.ToDoList.security.model.UserEntity;
import com.ToDoList.ToDoList.todolist.model.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Page<TaskEntity> findByUser(UserEntity user, Pageable page);
}
