package com.yshow.mychat.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yshow.mychat.server.Server;

public class ClientHandler {

    private final Server server;

    private final SocketChannel clientSocket;

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private final ByteBuffer readBuffer;

    public ClientHandler(Server server, SocketChannel client) {
	this.server = server;
	this.clientSocket = client;
	this.readBuffer = ByteBuffer.allocate(1024);
    }


    public void run() {
	try (clientSocket) {


	    while (true) {

		int bytesRead = clientSocket.read(readBuffer);

		log.atTrace().setMessage("Read {} Bytes from Socket: {}").addArgument(() -> bytesRead)
			.addArgument(() -> clientSocket).log();

		if (bytesRead == -1) {
		    // Client disconnected
		    log.atInfo().setMessage("Client {} disconnected").addArgument(clientSocket).log();
		    clientSocket.close();
		    break;
		}

		readBuffer.flip();


		//server.broadCastEveryone(Arrays.copyOf(readBuffer.array(), bytesRead));

		readBuffer.clear();

	    }
	} catch (IOException e) {
	    log.atWarn().setMessage("Client had problems with socket {} disconnecting").addArgument(clientSocket)
		    .setCause(e).log();
	}

    }

    public SocketChannel getClientSocket() {
	return clientSocket;
    }

    public Server getServer() {
	return server;
    }

    public void writeToClient(ByteBuffer bb) {
	try {
	    while (bb.hasRemaining()) {
		clientSocket.write(bb);
	    }
	} catch (IOException e) {
	    log.atWarn().setMessage("Problem sending message to {} ").setCause(e).addArgument(clientSocket).log();
	}
    }

}
