package com.myproject.dto;

import com.myproject.model.JoinForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinFormDTO {

    Integer id;
    String sender;
    String roomName;

    public JoinFormDTO(JoinForm joinForm){
        this.id = joinForm.getId();
        this.sender = joinForm.getSender().getName();
        this.roomName = joinForm.getRoom().getName();
    }
}
