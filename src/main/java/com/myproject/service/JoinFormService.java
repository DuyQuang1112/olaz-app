package com.myproject.service;

import com.myproject.dto.JoinFormDTO;
import com.myproject.dto.RoomDTO;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.JoinForm;
import com.myproject.model.Room;
import com.myproject.model.User;
import com.myproject.model.UserRoom;
import com.myproject.repository.JoinFormRepository;
import com.myproject.repository.RoomRepository;
import com.myproject.repository.UserRepository;
import com.myproject.repository.UserRoomRepository;
import com.myproject.validate.JoinFomValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.myproject.constant.RoleConst.HOST_ROOM;
import static com.myproject.constant.RoleConst.ROOM_MEMBER;
import static com.myproject.constant.StatusConstant.JoinFormStatus;
import static com.myproject.constant.StatusConstant.JoinFormStatus.ACCEPT;

@Service
@RequiredArgsConstructor
public class JoinFormService {

    private final JoinFormRepository joinFormRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final UserRoomService userRoomService;

    public List<JoinFormDTO> getAllPendingForm(Integer userId){
        List<JoinForm> joinForms = joinFormRepository.findAllByReceiverIdAndStatus(userId, JoinFormStatus.PENDING);
        List<JoinFormDTO> joinFormDTOs = new ArrayList<>();
        for(JoinForm joinForm : joinForms){
            joinFormDTOs.add(new JoinFormDTO(joinForm));
        }
        return joinFormDTOs;
    }

    public RoomDTO create(String code, Integer userId){
        //sender user
        Optional<User> userOptional = userRepository.findById(userId);

        Room room = roomRepository.findByCode(code);

        if(userOptional.isPresent() && room != null){
            JoinFomValidate.isExist(room.getId(), userOptional.get().getId(), joinFormRepository);
            //get host room
            UserRoom userRoom = userRoomRepository.findByRoleAndRoomId(HOST_ROOM, room.getId());

            JoinForm joinForm = new JoinForm();
            joinForm.setRoom(room);
            joinForm.setSender(userOptional.get());
            joinForm.setReceiver(userRoom.getUser());
            joinForm.setStatus(JoinFormStatus.PENDING);
            joinForm.setTimestamp(LocalDateTime.now());
            joinFormRepository.save(joinForm);
            return new RoomDTO(room);
        } else {
            throw new ResourceNotFoundException("User id is not exist");
        }
    }

    public void handleForm(Integer id, String action){
        Optional<JoinForm> joinFormOptional = joinFormRepository.findById(id);

        if(Objects.equals(action, ACCEPT)){
            if(joinFormOptional.isPresent()){
                Optional<Room> roomOptional = roomRepository.findById(joinFormOptional.get().getRoom().getId());
                Optional<User> userOptional = userRepository.findById(joinFormOptional.get().getSender().getId());
                if (userOptional.isPresent() && roomOptional.isPresent()) {
                    //change join form status to accept
                    JoinForm joinForm = joinFormOptional.get();
                    joinForm.setStatus(ACCEPT);
                    joinFormRepository.save(joinForm);
                    userRoomService.addUserToChatRoom(userOptional.get(), roomOptional.get(), ROOM_MEMBER);
                } else {
                    throw new ResourceNotFoundException("User or room not found");
                }
            } else {
                throw new ResourceNotFoundException("Join Form id not found");
            }
        }

    }
}
