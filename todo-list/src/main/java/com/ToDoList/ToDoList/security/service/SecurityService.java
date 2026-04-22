package com.ToDoList.ToDoList.security.service;

import com.ToDoList.ToDoList.security.dto.RegisterDTO;
import com.ToDoList.ToDoList.security.model.UserEntity;
import com.ToDoList.ToDoList.security.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class SecurityService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public SecurityService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public UserEntity register(RegisterDTO registerDTO) {

        if(userRepository.findByUsername(registerDTO.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if(userRepository.findByEmail(registerDTO.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registerDTO.username());
        userEntity.setEmail(registerDTO.email());
        userEntity.setPassword(passwordEncoder.encode(registerDTO.password()));
        userEntity.setRole(registerDTO.role());

        return userRepository.save(userEntity);
    }
}
