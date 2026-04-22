package com.ToDoList.ToDoList.security.controller;

import com.ToDoList.ToDoList.security.dto.LoginDTO;
import com.ToDoList.ToDoList.security.dto.RegisterDTO;
import com.ToDoList.ToDoList.security.dto.TokenResponseDTO;
import com.ToDoList.ToDoList.security.model.UserEntity;
import com.ToDoList.ToDoList.security.repository.UserRepository;
import com.ToDoList.ToDoList.security.service.SecurityService;
import com.ToDoList.ToDoList.security.service.TokenService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final SecurityService securityService;

    public SecurityController(TokenService tokenService, AuthenticationManager authenticationManager, SecurityService securityService) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.securityService = securityService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginDTO data){

        var usenamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());

        var auth = this.authenticationManager.authenticate(usenamePassword);

        var token = tokenService.generateToken((UserEntity) Objects.requireNonNull(auth.getPrincipal()));

        return ResponseEntity.ok(new TokenResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(securityService.register(data));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
