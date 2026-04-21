package com.pragma.plazoleta.infrastructure.input.controller;

import com.pragma.plazoleta.application.dto.UserDTO;
import com.pragma.plazoleta.application.handler.IUserHandler;
import com.pragma.plazoleta.domain.model.Role;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final IUserHandler userHandler;

    @PostMapping("/owner")
    public ResponseEntity<Void> CreateOwner(@Valid @RequestBody UserDTO dto) {
        userHandler.creteOwner(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/employee")
    public ResponseEntity<Void> CreateEmployee(@Valid @RequestBody UserDTO dto) {
        userHandler.createEmployee(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/client")
    public ResponseEntity<Void> CreateClient(@Valid @RequestBody UserDTO dto) {
        userHandler.createClient(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userHandler.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{id}/role")
    public ResponseEntity<Role> getUserRole(@PathVariable Long id) {
        UserDTO user= userHandler.getUserById(id);
        return ResponseEntity.ok(user.getRole());
    }
}
