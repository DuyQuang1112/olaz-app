package com.myproject.utils;

import java.util.Random;

public class RoomCodeGenerate {
    public static String generateRoomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}