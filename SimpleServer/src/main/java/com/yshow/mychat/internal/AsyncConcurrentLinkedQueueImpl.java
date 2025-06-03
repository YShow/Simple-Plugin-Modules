package com.yshow.mychat.internal;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yshow.mychat.events.AsyncEvent;
import com.yshow.mychat.events.Event;

public final class AsyncConcurrentLinkedQueueImpl implements AsyncEventManager {
    private final EventManagerImpl eventManager;

    private final Logger log;

    private final ConcurrentLinkedQueue<AsyncEvent> queue;
    private final ExecutorService asyncExecutor;

    public AsyncConcurrentLinkedQueueImpl(EventManagerImpl eventManager) {

	Objects.requireNonNull(eventManager);

	this.log = LoggerFactory.getLogger(EventManagerImpl.class);
	this.eventManager = eventManager;
	this.queue = new ConcurrentLinkedQueue<AsyncEvent>();

	final int poolSize = Integer.getInteger("eventManagerPoolSize", 1); // Adjustable pool size
	this.asyncExecutor = Executors.newFixedThreadPool(poolSize);

	for (int i = 0; i < poolSize; i++) {
	    asyncExecutor.submit(this::asyncEventProcessor);
	}
    }

    @Override
    public Event queueEvent(final AsyncEvent event) {

	queue.offer(event);

	return event;
    }

    private void asyncEventProcessor() {
	while (true) {
	    try {
		Event event;
		while ((event = queue.poll()) == null) {
		    Thread.onSpinWait();
		}

		eventManager.callEvent(event);
	    } catch (Exception e) {
		Thread.currentThread().interrupt();
		log.atError().setMessage("Error at thread {}, exception was: ").addArgument(Thread.currentThread())
			.setCause(e).log();
	    }
	}
    }
}
