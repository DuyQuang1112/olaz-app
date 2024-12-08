package com.myproject.dto;

import com.myproject.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class MessageDTO {

    String content;
    String name;
    String type;
    Integer userId;
    LocalDateTime timestamp;

    public MessageDTO(Message message){
        if(message != null){
            this.content = message.getContent();
            this.name = message.getUser().getName();
            this.userId = message.getUser().getId();
            this.timestamp = message.getTimestamp();
        }
    }

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
