package me.croxford.SkylinesGuild;

import org.apache.commons.logging.impl.Log4JLogger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Lisa on 09/09/2015.
 */
@Component
public class ClientConnectionManager {

    static Logger log = Logger.getLogger("ClientConnection");
    static int port = 8132;

    static ClientConnectionManager instance;

    private Set<ClientConnection> allConnections = new HashSet<>();

    AsynchronousServerSocketChannel server;

    ClientConnectionManager() {

        try {
            server = AsynchronousServerSocketChannel.open();
            String host = "localhost";

            InetSocketAddress address = new InetSocketAddress(host, port);
            server.bind(address);
            log.info("Server is listening at" + address.toString());

            acceptNextConnection();

        } catch (Exception e) {
            log.info(e.toString());
        }

        instance = this;
    }

    void acceptNextConnection() {
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                try {
                    log.info("New connection from " + channel.getRemoteAddress());
                    allConnections.add(new ClientConnection(channel, instance));
                } catch (IOException e) {
                    log.severe(e.toString());
                }
                acceptNextConnection();
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                log.severe(exc.toString());
            }
        });
    }


    public static ClientConnectionManager getInstance() {
        return instance;
    }

    public ClientConnection getConnection(String clientSecret) {
        for(ClientConnection c : allConnections) {
            if(c.getClientSecret().equals(clientSecret)) {
                return c;
            }
        }

        return null;
    }


    public void onConnectionClosed(ClientConnection connection) {
        allConnections.remove(connection);
    }
}
