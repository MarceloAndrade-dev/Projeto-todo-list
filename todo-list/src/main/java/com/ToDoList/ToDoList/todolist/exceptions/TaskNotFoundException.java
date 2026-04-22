package com.ToDoList.ToDoList.todolist.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(){
        super("Not Found");
    }

    public TaskNotFoundException(String message) {super(message);}
}
