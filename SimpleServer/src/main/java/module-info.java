module mychat {
    exports com.yshow.mychat.events;
    exports com.yshow.mychat.events.impl;
    exports com.yshow.mychat.plugin;
    exports com.yshow.mychat.server;

    // plugin interface for service providers
    uses com.yshow.mychat.plugin.Plugin;

    requires transitive org.slf4j;

}