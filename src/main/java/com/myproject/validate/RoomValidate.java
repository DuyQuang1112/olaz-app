package com.myproject.validate;

import com.myproject.exception.ResourceNotFoundException;
import com.myproject.repository.RoomRepository;

public class RoomValidate {
    public static void checkStatus(Integer roomId, RoomRepository roomRepository){
        if(!roomRepository.existsById(roomId)){
            throw new ResourceNotFoundException("Room not found");
        }
    }
}
