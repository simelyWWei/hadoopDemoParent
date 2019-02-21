package com.example.hivedemo.syncEvent;

import org.springframework.context.ApplicationEvent;

public class SerchHiveEvent extends ApplicationEvent {
    public SerchHiveEvent (SerchHiveParams eventParams){
        super(eventParams);
    }
}
