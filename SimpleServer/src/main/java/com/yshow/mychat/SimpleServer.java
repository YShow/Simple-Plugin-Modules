package com.yshow.mychat;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.random.RandomGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yshow.mychat.events.EventManager;
import com.yshow.mychat.events.impl.HiEvent;
import com.yshow.mychat.events.impl.HiNoAsyncEvent;
import com.yshow.mychat.internal.EventManagerImpl;
import com.yshow.mychat.internal.PluginLoader;
import com.yshow.mychat.server.Server;

public final class SimpleServer implements Server {

    private final Logger log;

    private final EventManager eventManager;

    private final PluginLoader pluginLoader;

    private static final int port = Integer.getInteger("myserver.port", 8080);

    // private final List<ClientHandler> clientList;

    public SimpleServer() {
	this.eventManager = new EventManagerImpl();
	this.pluginLoader = new PluginLoader(this);
	this.log = LoggerFactory.getLogger(Server.class);
	// this.clientList = new CopyOnWriteArrayList<ClientHandler>();
    }

    public static void main(String[] args) throws InterruptedException {

	final var server = new SimpleServer();

	server.run();

    }

    private final void run() throws InterruptedException {

	getPluginLoader().loadPlugins();

	// log.atDebug().setMessage("Opening ServerSocket with port
	// {}").addArgument(port).log();

	/*
	 * final var scheduler = Executors.newScheduledThreadPool(5);
	 * 
	 * scheduler.scheduleWithFixedDelay(() -> {
	 * 
	 * eventManager.fireEvent(new HiEvent(generateRandomGarbo()));
	 * 
	 * }, 5000, 10, TimeUnit.MILLISECONDS);
	 */
	// Thread.sleep(500000L);

	final var rand = RandomGenerator.of("L32X64MixRandom");

	log.atInfo().setMessage("thread main: {}").addArgument(Thread.currentThread()).log();

	for (int i = 0; i < 10_000; i++) {
	    
	    Thread.sleep(1000L);
	    
	    final var init = Instant.now();
	    for (int j = 0; j < 10_000; j++) {
		eventManager.fireEvent(new HiEvent("asdasdasdasdasd"));
	    }
	    log.atInfo().setMessage("event async fired took {}ns")
		    .addArgument(Duration.between(init, Instant.now()).toNanos()).log();
	}

	Thread.sleep(5000000L);

	for (int i = 0; i < 5_000_000; i++) {

	    // Thread.sleep(rand.nextLong(1, 20));

	    if (rand.nextBoolean()) {
		final var init = Instant.now();
		eventManager.fireEvent(new HiEvent(generateRandomGarbo()));

		log.atInfo().setMessage("event async fired took {}ns")
			.addArgument(Duration.between(init, Instant.now()).toNanos()).log();
	    } else {
		final var init = Instant.now();
		eventManager.fireEvent(new HiNoAsyncEvent(generateRandomGarbo()));
		log.atInfo().setMessage("event no async fired took {}ns")
			.addArgument(Duration.between(init, Instant.now()).toNanos()).log();

	    }

	}

	Thread.sleep(5000L);

	// extracted();

    }

    private static String generateRandomGarbo() {
	final var rand = RandomGenerator.of("L32X64MixRandom");
	final var b = new byte[4];

	rand.nextBytes(b);

	return Base64.getEncoder().encodeToString(b);
    }

    /*
     * private void extracted() { try (ServerSocketChannel serverSocketChannel =
     * ServerSocketChannel.open()) { serverSocketChannel.bind(new
     * InetSocketAddress(port));
     * 
     * log.atTrace().setMessage("ServerSocket created  {}").addArgument(
     * serverSocketChannel).log();
     * 
     * try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
     * 
     * log.atInfo().setMessage("Server listening on port {}").addArgument(port).log(
     * );
     * 
     * while (true) {
     * 
     * var socket = serverSocketChannel.accept();
     * 
     * eventManager.fireEvent(new HiEvent("asd"));
     * socket.write(ByteBuffer.wrap("Hello ".getBytes()));
     * 
     * log.atTrace().setMessage("Accepted socket {} ").addArgument(socket).log();
     * 
     * var clientHandler = new ClientHandler(getServer(), socket);
     * 
     * clientList.add(clientHandler);
     * 
     * executor.execute(() -> clientHandler.run()); } } } catch (IOException e1) {
     * log.atError().setMessage(e1.getMessage()).setCause(e1).log(); } }
     */

    @Override
    public EventManager getEventManager() {
	return eventManager;
    }

    @Override
    public Server getServer() {
	return this;
    }

    @Override
    public Logger getServerLogger() {
	return log;
    }

    /*
     * @Override public void broadCastEveryone(byte[] message) {
     * 
     * var buffer = ByteBuffer.wrap(message).flip();
     * 
     * clientList.forEach(e -> e.writeToClient(buffer)); }
     */

    private final PluginLoader getPluginLoader() {
	return this.pluginLoader;
    }
}