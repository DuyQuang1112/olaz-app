package com.myproject.service;

import com.myproject.dto.RoomDTO;
import com.myproject.dto.user.UserResponseDTO;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Room;
import com.myproject.model.User;
import com.myproject.model.UserRoom;
import com.myproject.repository.RoomRepository;
import com.myproject.repository.UserRepository;
import com.myproject.repository.UserRoomRepository;
import com.myproject.validate.RoomRoleValidate;
import com.myproject.validate.UserRoomValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.myproject.constant.RoleConst.HOST_ROOM;

@Service
@RequiredArgsConstructor
public class UserRoomService {

    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomValidate userRoomValidate;
    private final RoomRoleValidate roomRoleValidate;

    public String getUserRoleInRoom(Integer userId, Integer roomId){
        UserRoom userRoom =  userRoomRepository.findByUserIdAndRoomId(userId, roomId);
        if(userRoom != null) {
            return userRoom.getRole();
        } else {
            throw new ResourceNotFoundException("User ID or room ID is not exist");
        }
    }

    public UserResponseDTO getHostInRoom(Integer roomId){
        UserRoom userRoom = userRoomRepository.findByRoleAndRoomId(HOST_ROOM, roomId);
        Optional<User> userOptional = userRepository.findById(userRoom.getUser().getId());
        if(userOptional.isPresent()){
            return new UserResponseDTO(userOptional.get());
        } else {
            throw new ResourceNotFoundException("User is not exist");
        }
    }

    public void addUserToChatRoom(User user, Room room, String role) {
        userRoomValidate.isUserJoinedRoom(user.getId(), room.getId());
        UserRoom userRoom = new UserRoom();
        userRoom.setUser(user);
        userRoom.setRoom(room);
        userRoom.setJoinedAt(LocalDateTime.now());
        userRoom.setRole(role);
        userRoomRepository.save(userRoom);
    }

    public List<RoomDTO> getAllRoomByUser(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        List<RoomDTO> roomDTOS = new ArrayList<>();
        if(userOptional.isPresent()){
            List<UserRoom> userRooms = userRoomRepository.findRoomByUserId(userOptional.get().getId());
            for(UserRoom userRoom : userRooms){
                Optional<Room> roomOptional = roomRepository.findById(userRoom.getRoom().getId());
                roomOptional.ifPresent(room -> roomDTOS.add(new RoomDTO(room)));
            }
        }
        return roomDTOS;
    }

    public List<UserResponseDTO> getAllUserByRoom(Integer Roomid) {
        Optional<Room> room = roomRepository.findById(Roomid);
        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
        if(room.isPresent()){
            List<UserRoom> userRooms = userRoomRepository.findUserByRoomId(room.get().getId());
            for(UserRoom userRoom : userRooms){
                Optional<User> userOptional = userRepository.findById(userRoom.getUser().getId());
                userOptional.ifPresent(user -> userResponseDTOS.add(new UserResponseDTO(user)));
            }
        }
        return userResponseDTOS;
    }

    public String deleteUserFromRoom(Integer userId , Integer roomId, Principal principal){
        roomRoleValidate.isHost(principal, roomId);
        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(userId, roomId);
        if(userRoom != null){
            userRoomRepository.delete(userRoom);
            return "Delete success";
        } else {
            throw new ResourceNotFoundException("User id is not joined room");
        }
    }

    public void leaveRoom(Integer userId , Integer roomId){
        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(userId, roomId);
        if(userRoom != null){
            userRoomRepository.delete(userRoom);
        } else {
            throw new ResourceNotFoundException("User id is not joined room");
        }
    }

}
