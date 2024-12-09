package com.myproject.dto;

import com.myproject.model.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    Integer id;
    String name;
    String roomStatus;
    String code;

    public RoomDTO(Room room){
        this.id = room.getId();
        this.name = room.getName();
        this.roomStatus = room.getStatus().trim();
        this.code = room.getCode();
    }
}
