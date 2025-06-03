package com.yshow.mychat.events;

/**
 * This interface extends {@link Event} so it is a syncronized event unless you
 * also mark your event with {@link AsyncEvent} then the {@link EventManager}
 * will it on another thread to execute at a later time
 */
public interface CancellableEvent extends Event {

    /**
     * check if the event has been canceled
     * 
     * @return true or false
     */
    boolean canceled();

    /**
     * Cancel the event and stops the loop for all event handlers registered
     * 
     * @return
     */
    Event cancelEvent();

}
