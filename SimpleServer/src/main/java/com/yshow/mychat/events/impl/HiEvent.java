package com.yshow.mychat.events.impl;

import com.yshow.mychat.events.AsyncEvent;
import com.yshow.mychat.events.CancellableEvent;
import com.yshow.mychat.events.Event;

public record HiEvent(String hi, boolean canceled) implements CancellableEvent, AsyncEvent {

    public HiEvent() {
	this("", false);
    }

    public HiEvent(String hi) {
	this(hi, false);
    }

    public HiEvent hi(String newHi) {

	return new HiEvent(newHi, false);
    }

    @Override
    public Event cancelEvent() {

	return new HiEvent(hi, true);
    }

}
