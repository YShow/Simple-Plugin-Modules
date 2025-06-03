package com.yshow.mychat.internal;

import com.yshow.mychat.events.AsyncEvent;
import com.yshow.mychat.events.Event;
import com.yshow.mychat.events.EventManager;

/**
 * interface used to decouple the async part from the {@link EventManager}
 * interface, how to handle and how to {@link EventManagerImpl#callEvent} is
 * upon you to implement
 */
public sealed interface AsyncEventManager
	permits AsyncArrayBlockingQueueImpl, AsyncLinkedBlockingQueueImpl, AsyncConcurrentLinkedQueueImpl, AsyncTransferQueueImpl {

    /**
     * this method should always implement a safe way to queue all events, make sure
     * to know whick implementation to use
     * 
     * @param {@link AsyncEvent} event
     * @return {@link Event}
     */
    public Event queueEvent(final AsyncEvent event);

}
