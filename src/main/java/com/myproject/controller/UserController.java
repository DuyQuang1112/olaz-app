package com.myproject.controller;

import com.myproject.dto.user.UserResponseDTO;
import com.myproject.service.UserRoomService;
import com.myproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRoomService userRoomService;

    @GetMapping
    public ResponseEntity<UserResponseDTO> getUserById(@RequestParam("userId") Integer userId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserById(userId));
    }

    @GetMapping("/room-id")
    public ResponseEntity<List<UserResponseDTO>> getAllUserByRoom(@RequestParam("roomId") Integer roomId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userRoomService.getAllUserByRoom(roomId));
    }

    @PutMapping("/avatar-upload")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<UserResponseDTO> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("userId") Integer userId) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.uploadAvatar(file, userId));
    }

    @PutMapping("/update-name")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<Void> updateName(@RequestParam("userId") Integer userId, @RequestParam("name") String name) {
        userService.updateName(userId, name);

        return ResponseEntity.noContent().build();
    }

}

