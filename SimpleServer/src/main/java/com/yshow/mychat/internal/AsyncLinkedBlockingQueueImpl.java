package com.yshow.mychat.internal;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yshow.mychat.events.AsyncEvent;
import com.yshow.mychat.events.Event;

public final class AsyncLinkedBlockingQueueImpl implements AsyncEventManager {

    private final EventManagerImpl eventManager;


    private final Logger log;

    private final BlockingQueue<AsyncEvent> queue;
    private final ExecutorService asyncExecutor;

    public AsyncLinkedBlockingQueueImpl(EventManagerImpl eventManager) {

	Objects.requireNonNull(eventManager);

	this.log = LoggerFactory.getLogger(EventManagerImpl.class);
	this.eventManager = eventManager;
	this.queue = new LinkedBlockingQueue<AsyncEvent>(
		Integer.getInteger("eventManagerQueueSize", Integer.MAX_VALUE));

	final int poolSize = Integer.getInteger("eventManagerPoolSize", 2); // Adjustable pool size
	this.asyncExecutor = Executors.newFixedThreadPool(poolSize);

	for (int i = 0; i < poolSize; i++) {
	    asyncExecutor.submit(this::asyncEventProcessor);
	}
    }

    @Override
    public Event queueEvent(final AsyncEvent event) {

	boolean success = queue.offer(event); // Non-blocking; only succeeds if a
	// consumer is ready
	if (!success) {
	    try {
		queue.put(event);
	    } catch (InterruptedException e1) {
		Thread.currentThread().interrupt();
		log.atError().setCause(e1).log();
	    }
	}

	return event;
    }

    private void asyncEventProcessor() {
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
