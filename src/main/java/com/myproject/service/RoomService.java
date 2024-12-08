package com.myproject.service;

import com.myproject.dto.RoomDTO;
import com.myproject.enums.RoleEnum;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Room;
import com.myproject.model.User;
import com.myproject.repository.*;
import com.myproject.utils.RoomCodeGenerate;
import com.myproject.validate.RoomRoleValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

import static com.myproject.constant.StatusConstant.RoomStatus.PRIVATE;
import static com.myproject.constant.StatusConstant.RoomStatus.PUBLIC;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserRoomService userRoomService;
    private final RoomRoleValidate roomRoleValidate;

    public String checkRoomStatus(String code) {
        Room room = roomRepository.findByCode(code);
        if (room != null) {
            return room.getStatus().trim();
        } else {
            throw new ResourceNotFoundException("Room not found");
        }
    }

    public RoomDTO create(String roomName, Integer userId, String roomType) {
        Optional<User> userOptional = userRepository.findById(userId);
        Room room = new Room();
        if(userOptional.isPresent()){
            room.setName(roomName);
            room.setCode(RoomCodeGenerate.generateRoomCode());
            room.setStatus(roomType.equals(PUBLIC) ? PUBLIC : PRIVATE);
            roomRepository.save(room);
            //save joined room
            userRoomService.addUserToChatRoom(userOptional.get(), room, RoleEnum.HOST.getValue());
            return new RoomDTO(room);
        } else {
            throw new ResourceNotFoundException("User is not exist");
        }

    }

    public RoomDTO joinRoom(String code, Integer userId) {
        Room room = roomRepository.findByCode(code);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent() && room != null) {
            //save joined room
            userRoomService.addUserToChatRoom(userOptional.get(), room, RoleEnum.MEMBER.getValue());
            return new RoomDTO(room);
        } else {
            throw new ResourceNotFoundException("Room not found");
        }
    }

    public RoomDTO accessRoom(Integer roomId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            return new RoomDTO(room);
        } else {
            throw new ResourceNotFoundException("Room not found");
        }
    }

    public RoomDTO rename(Integer roomId, String roomName, Principal principal) {
        roomRoleValidate.isHost(principal, roomId);
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if(roomOptional.isPresent()){
            Room room = roomOptional.get();
            room.setName(roomName);
            roomRepository.save(room);
            return new RoomDTO(room);
        } else {
            throw new ResourceNotFoundException("Room not found");
        }
    }

    public void deleteById(Integer roomId, Principal principal) {
        roomRoleValidate.isHost(principal, roomId);
        if(roomRepository.existsById(roomId)){
            roomRepository.deleteById(roomId);
        } else {
            throw new ResourceNotFoundException("Room not found");
        }
    }

}