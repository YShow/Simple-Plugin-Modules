package com.yshow.mychat.internal;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yshow.mychat.events.AsyncEvent;
import com.yshow.mychat.events.CancellableEvent;
import com.yshow.mychat.events.Event;
import com.yshow.mychat.events.EventHandler;
import com.yshow.mychat.events.EventManager;

public final class EventManagerImpl implements EventManager {

    private final Logger log;

    private final Map<Class<? extends Event>, List<EventHandler>> listeners;

    private final ReentrantLock lock;

    private final AsyncEventManager asyncEventManager;

    private static final String queueImplementation = System.getProperty("asyncManager", "transferqueue");


    public EventManagerImpl() {
	this.log = LoggerFactory.getLogger(EventManagerImpl.class);
	this.listeners = new ConcurrentHashMap<>();
	this.lock = new ReentrantLock();
	this.asyncEventManager = AsyncQueueFactory.of(queueImplementation, this);



    }



    @Override
    public void registerListener(final EventHandler listener, final Class<? extends Event> eventType) {
	Objects.requireNonNull(listener, "Listener cannot be null");
	Objects.requireNonNull(eventType, "EventType cannot be null");

	log.atDebug().setMessage("Registering Listener {} to eventType {}")
		.addArgument(() -> listener.getClass().getSimpleName()).addArgument(() -> eventType.getSimpleName())
		.log();

	listeners.computeIfAbsent(eventType, e -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @Override
    public Event fireEvent(final Event event) {
	Objects.requireNonNull(event, "event is null");

	log.atTrace().setMessage("Event toString(): {}").addArgument(() -> event).log();

	if (event instanceof AsyncEvent e) {

	    return fireAsync(e);

	}

	return fireSync(event);
    }

    private Event fireSync(final Event event) {
	try {
	    lock.lock();

	    log.atTrace().setMessage("Locking sync thread {}").addArgument(() -> Thread.currentThread()).log();

	    log.atDebug().setMessage("Firing Event {} as sync").addArgument(() -> event.getClass()).log();

	    return callEvent(event);
	} finally {
	    lock.unlock();
	    log.atTrace().setMessage("Unlocking sync thread").log();
	}
    }

    private Event fireAsync(final AsyncEvent e) {
	
	return asyncEventManager.queueEvent(e);
    }



    Event callEvent(final Event event) {

	Objects.requireNonNull(event, "event is null");

	final var handlers = listeners.getOrDefault(event.getClass(), List.<EventHandler>of());

	if (handlers.isEmpty()) {
	    log.atDebug().setMessage("Event {} didnt have any handlers")
		    .addArgument(() -> event.getClass().getSimpleName()).log();

	    return event;
	}

	log.atDebug().setMessage("Firing events on thread {}").addArgument(() -> Thread.currentThread()).log();

	log.atTrace().setMessage("Event {} has {} handlers ").addArgument(() -> event.getClass().getSimpleName())
		.addArgument(() -> handlers.size()).log();

	var eventLoop = event;

	for (final var eventHandler : handlers) {

	    log.atTrace().setMessage("Firing Handler {} for event {}")
		    .addArgument(() -> eventHandler.getClass().getSimpleName())
		    .addArgument(() -> event.getClass().getSimpleName()).log();

	    try {
		eventLoop = eventHandler.onEvent(eventLoop);
		if (isCanceled(eventLoop)) {
		    log.atTrace().setMessage("Event {} was canceled by eventHandler: {} returning")
			    .addArgument(() -> event.getClass().getSimpleName()).addArgument(() -> eventHandler).log();

		    return eventLoop;
		}
	    } catch (Exception e) {
		log.atError().setMessage("Plugin {} caused exception").addArgument(eventHandler).setCause(e).log();

		throw new Error(e);
	    }

	    log.atTrace().setMessage("Finished Handler {} for event {}")
		    .addArgument(() -> eventHandler.getClass().getSimpleName())
		    .addArgument(() -> event.getClass().getSimpleName()).log();
	}

	log.atTrace().setMessage("End of event loop for event: {}").addArgument(() -> event.getClass().getSimpleName())
		.log();

	return eventLoop;
    }

    private boolean isCanceled(final Event event) {
	if (event instanceof CancellableEvent cancellableEvent && cancellableEvent.canceled()) {
	    log.atDebug().setMessage("Event {} was canceled").addArgument(() -> event.getClass().getSimpleName()).log();

	    return true;
	}

	log.atTrace().setMessage("Event was not canceled {}").addArgument(() -> event.getClass().getSimpleName()).log();

	return false;
    }

}
