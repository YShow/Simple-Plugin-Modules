package testeeee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yshow.mychat.events.Event;
import com.yshow.mychat.events.EventHandler;
import com.yshow.mychat.events.impl.HiEvent;
import com.yshow.mychat.events.impl.HiNoAsyncEvent;
import com.yshow.mychat.plugin.Plugin;
import com.yshow.mychat.server.Server;


public class HelloWorldPlugin implements Plugin, EventHandler {

    private Server server;

    private static final Logger log = LoggerFactory.getLogger(HelloWorldPlugin.class);

    @Override
    public void initialize(Server server) {
	log.info("im on hello world plugin");
	this.server = server;

	server.getEventManager().registerListener(this, HiEvent.class);

	for (int i = 0; i < 50; i++) {

	    server.getEventManager().registerListener(new GenericEvent(), HiEvent.class);

	}

	for (int i = 0; i < 50; i++) {
	    server.getEventManager().registerListener(new GenericEvent(), HiNoAsyncEvent.class);
	}

    }

    @Override
    public Event onEvent(Event event) {

	if (event instanceof HiEvent ev) {
	    // log.atInfo().log("hi from event onEvent");

	    if (ev.hi().equalsIgnoreCase("hi")) {
		return ev.hi("no Hi around here").cancelEvent();
	    }

	    return ev;
	}

	return event;
    }

    @Override
    public boolean disable() {

	return false;
    }

}