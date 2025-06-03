package com.yshow.mychat.server;

import org.slf4j.Logger;

import com.yshow.mychat.events.EventManager;

public interface Server {

    Server getServer();

    EventManager getEventManager();

    Logger getServerLogger();

    // void broadCastEveryone(byte[] bs);

}
