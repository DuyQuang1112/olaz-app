package com.myproject.controller;

import com.myproject.dto.JwtResponse;
import com.myproject.dto.user.UserDTO;
import com.myproject.dto.user.UserLogin;
import com.myproject.dto.user.UserResponseDTO;
import com.myproject.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(userService.register(userDTO));
    }


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> logIn(@RequestBody UserLogin userLogin){
        return ResponseEntity.ok(userService.logIn(userLogin));
    }



}
