package com.myproject.controller;

import com.myproject.dto.user.UserResponseDTO;
import com.myproject.service.UserRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user-room")
@RequiredArgsConstructor
public class UserRoomController {

    private final UserRoomService userRoomService;

    @DeleteMapping
    public ResponseEntity<String> deleteUserFromRoom(@RequestParam("userId") Integer userId, @RequestParam("roomId") Integer roomId, Principal principal){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userRoomService.deleteUserFromRoom(userId, roomId, principal));
    }

    @GetMapping("/room-role")
    public ResponseEntity<String> getUserRoleInRoom(@RequestParam("userId") Integer userId, @RequestParam("roomId") Integer roomId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userRoomService.getUserRoleInRoom(userId, roomId));
    }

    @GetMapping("/host")
    public ResponseEntity<UserResponseDTO> getHostInRoom(@RequestParam("roomId") Integer roomId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userRoomService.getHostInRoom(roomId));
    }

    @DeleteMapping("/leave-room")
    public ResponseEntity<String> leaveRoom(@RequestParam("userId") Integer userId, @RequestParam("roomId") Integer roomId){
        userRoomService.leaveRoom(userId, roomId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Leave success");
    }
}
