package com.myproject.service;

import com.myproject.dto.MessageDTO;
import com.myproject.model.Message;
import com.myproject.model.Room;
import com.myproject.model.User;
import com.myproject.repository.MessageRepository;
import com.myproject.repository.RoomRepository;
import com.myproject.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public MessageDTO create(MessageDTO messageDTO, Integer roomId, Principal principal){
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        Room room = new Room();
        if(roomOptional.isPresent()){
            room = roomOptional.get();
        }

        String username = principal.getName();
        System.out.println(username);
        User user = userRepository.findUserByUsername(username);
        //crate new message and save
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setUser(user);
        message.setTimestamp(LocalDateTime.now());
        message.setRoom(room);
        messageRepository.save(message);
        messageDTO.setTimestamp(LocalDateTime.now());
        messageDTO.setName(user.getName());
        messageDTO.setUserId(user.getId());
        return messageDTO;
    }

    public List<MessageDTO> getMessagesByRoomId(Integer roomId){
        List<Message> messages = messageRepository.getMessageByRoomId(roomId);
        List<MessageDTO> messageDTOS = new ArrayList<>();
        for(Message message : messages){
            if(message != null){
                MessageDTO messageDTO = new MessageDTO(message);
                messageDTOS.add(messageDTO);
            }
        }
        return messageDTOS;

    }
}
