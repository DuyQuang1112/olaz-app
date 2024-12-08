package com.myproject.controller;

import com.myproject.dto.RoomDTO;
import com.myproject.service.JoinFormService;
import com.myproject.service.RoomService;
import com.myproject.service.UserRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import com.myproject.constant.StatusConstant.RoomStatus;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final JoinFormService joinFormService;
    private final UserRoomService userRoomService;

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> accessRoom(@PathVariable("id") Integer roomId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(roomService.accessRoom(roomId));
    }

    @GetMapping("/joined-room/{userId}")
    public ResponseEntity<List<RoomDTO>> getAllByUserId(@PathVariable("userId") Integer userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userRoomService.getAllRoomByUser(userId));
    }

    @PostMapping("/join-room")
    public ResponseEntity<RoomDTO> joinRoom(@RequestParam("code") String code, @RequestParam("userId") Integer userId) {

        String roomStatus = roomService.checkRoomStatus(code);

        if (roomStatus.equals(RoomStatus.PUBLIC)) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(roomService.joinRoom(code, userId));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(joinFormService.create(code, userId));
        }
    }

    @PostMapping("/create-room")
    public ResponseEntity<RoomDTO> createRoom(@RequestParam("userId") Integer userId, @RequestParam("roomName") String roomName, @RequestParam("roomType") String roomType) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(roomService.create(roomName, userId, roomType));
    }

    @PutMapping("/rename")
    public ResponseEntity<RoomDTO> renameRoom(@RequestParam("roomId") Integer roomId, @RequestParam("roomName") String roomName, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(roomService.rename(roomId, roomName, principal));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRoom(@RequestParam("roomId") Integer roomId, Principal principal) {
        roomService.deleteById(roomId, principal);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Delete success");
    }


}
