package com.myproject.validate;

import com.myproject.model.UserRoom;
import com.myproject.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserRoomValidate {

    private final UserRoomRepository userRoomRepository;

    public void isUserJoinedRoom(Integer userId, Integer roomId){
        List<UserRoom> userRooms = userRoomRepository.findRoomByUserId(userId);
        for(UserRoom userRoom : userRooms){
            if(Objects.equals(userRoom.getRoom().getId(), roomId)){
                throw new IllegalArgumentException("You already a member of  this room");
            }
        }
    }

}
