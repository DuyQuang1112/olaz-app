package com.myproject.controller;

import com.myproject.dto.MessageDTO;
import com.myproject.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/{roomId}")
    public MessageDTO sendMessage(@DestinationVariable Integer roomId, MessageDTO messageDTO, Principal principal) {
        return messageService.create(messageDTO, roomId, principal);
    }

//    @MessageMapping("/chat.addUser/{roomId}")
//    @SendTo("/topic/{roomId}")
//    public MessageDTO addUser(@DestinationVariable Integer roomId, MessageDTO messageDTO) {
//        return messageDTO;
//    }

    @GetMapping("/message/room-id/{roomId}")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Integer roomId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageService.getMessagesByRoomId(roomId));
    }
}
