package com.agaldanaw.reto3;

import java.util.UUID;

public class RandomstringUUID {
    public static String GetGUID() {
        UUID uuid = UUID.randomUUID();
        System.out.println("UUID=" + uuid.toString() );
        return uuid.toString();
    }
}
