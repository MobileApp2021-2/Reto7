package com.agaldanaw.reto3;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class SingletonHub {

    private static final Object STATIC_LOCK = new Object();
    private static HubConnection _instance;
    public static String URL_HUB = "http://tictactoeapi-dev.us-east-1.elasticbeanstalk.com/tictactoe";

    public static HubConnection GetInstance()
    {
        synchronized (STATIC_LOCK) {
            if(_instance == null )
            {
                _instance = HubConnectionBuilder.create(URL_HUB).build();
            }
            return _instance;
        }

    }
}
