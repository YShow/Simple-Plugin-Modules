package com.yshow.mychat.internal;

import java.util.Objects;

public final class AsyncQueueFactory {

    private AsyncQueueFactory() {
    }

    public static final AsyncEventManager of(String manager, EventManagerImpl impl) {
	Objects.requireNonNull(manager);
	Objects.requireNonNull(impl);
	
	
	return switch (manager) {
	case "arrayqueue" -> new AsyncArrayBlockingQueueImpl(impl);
	case "concurrentqueue" -> new AsyncConcurrentLinkedQueueImpl(impl);
	case "linkedqueue" -> new AsyncLinkedBlockingQueueImpl(impl);
	case "transferqueue" -> new AsyncTransferQueueImpl(impl);
	default -> throw new IllegalArgumentException("Unexpected value: " + manager);
	};
	
	
    }
    
}
