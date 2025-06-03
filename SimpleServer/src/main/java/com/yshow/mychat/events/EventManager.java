package com.yshow.mychat.events;

import com.yshow.mychat.internal.EventManagerImpl;

public sealed interface EventManager permits EventManagerImpl
{

    public Event fireEvent(final Event event);

    public void registerListener(final EventHandler listener, final Class<? extends Event> eventType);


}
