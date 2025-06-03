package com.yshow.mychat.plugin;

import com.yshow.mychat.server.Server;

public interface Plugin {
    void initialize(Server server);

    boolean disable();

}
