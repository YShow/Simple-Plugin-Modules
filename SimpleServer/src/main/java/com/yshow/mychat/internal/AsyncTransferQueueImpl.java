package com.yshow.mychat.internal;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yshow.mychat.events.AsyncEvent;
import com.yshow.mychat.events.Event;

public final class AsyncTransferQueueImpl implements AsyncEventManager {

    private final EventManagerImpl eventManager;

    private final Logger log;

    private final TransferQueue<AsyncEvent> queue;
    private final ExecutorService asyncExecutor;

    public AsyncTransferQueueImpl(EventManagerImpl eventManager) {

	Objects.requireNonNull(eventManager);

	this.log = LoggerFactory.getLogger(EventManagerImpl.class);
	this.eventManager = eventManager;
	this.queue = new LinkedTransferQueue<AsyncEvent>();

	final int poolSize = Integer.getInteger("eventManagerPoolSize", 2); // Adjustable pool size
	this.asyncExecutor = Executors.newFixedThreadPool(poolSize);

	for (int i = 0; i < poolSize; i++) {
	    asyncExecutor.submit(this::asyncEventProcessor);
	}
    }

    @Override
    public final Event queueEvent(final AsyncEvent event) {
	final boolean isInserted = queue.tryTransfer(event);
	if (!isInserted) {
	    queue.offer(event);
	}

	return null;
    }

    private final void asyncEventProcessor() {
	while (true) {
	    try {
		final Event event = queue.take();
		eventManager.callEvent(event);
	    } catch (InterruptedException e) {
		Thread.currentThread().interrupt();
		log.atError().setMessage("Error at thread {}, exception was: ").addArgument(Thread.currentThread())
			.setCause(e).log();
	    }
	}
    }

}
