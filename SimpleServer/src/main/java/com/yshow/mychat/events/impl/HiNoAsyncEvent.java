package com.yshow.mychat.events.impl;

import com.yshow.mychat.events.CancellableEvent;
import com.yshow.mychat.events.Event;

public record HiNoAsyncEvent(String hi, boolean canceled) implements CancellableEvent {

    public HiNoAsyncEvent() {
	this("", false);
    }

    public HiNoAsyncEvent(String hi) {
	this(hi, false);
    }

    @Override
    public boolean canceled() {
	return canceled;
    }

    @Override
    public Event cancelEvent() {
	
	return new HiNoAsyncEvent(hi, true);
    }
    

}
