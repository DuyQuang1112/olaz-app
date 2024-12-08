package com.myproject.validate;

import com.myproject.exception.RoleNotAllowedException;
import com.myproject.model.User;
import com.myproject.model.UserRoom;
import com.myproject.repository.UserRepository;
import com.myproject.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Objects;

import static com.myproject.constant.RoleConst.HOST_ROOM;

@Component
@RequiredArgsConstructor
public class RoomRoleValidate {

    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;

    public void isHost(Principal principal, Integer roomId){
        User user = userRepository.findUserByUsername(principal.getName());
        UserRoom userRoom = userRoomRepository.findByUserIdAndRoomId(user.getId(), roomId);
        if(!Objects.equals(userRoom.getRole(), HOST_ROOM)){
            throw new RoleNotAllowedException("Only host room can access this resources");
        }
    }
}
