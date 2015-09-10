
package me.croxford.SkylinesGuild;

import me.croxford.SkylinesGuild.model.City;
import me.croxford.SkylinesGuild.model.SaveGame;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Logger;

public class ClientConnection {

    private static Logger log = Logger.getLogger("ClientConnection");
    private ByteBuffer buffer;
    private AsynchronousSocketChannel channel;

    private String clientSecret;
    private StringBuffer lineBuffer;
    private ClientConnectionManager manager;


    ClientConnection(AsynchronousSocketChannel _channel, ClientConnectionManager owner) {

        buffer = ByteBuffer.allocate(2048);
        lineBuffer = new StringBuffer();
        clientSecret = null; //No registered
        channel = _channel;
        manager = owner;
        doRead();
    }

    private void doRead() {
        channel.read(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                for(int i=0; i < result; i++) {
                    char c = (char)buffer.get(i);
                    if(c == '\r') {
                        String line = lineBuffer.toString();
                        lineBuffer = new StringBuffer();
                        handleLine(line);
                    }else{
                        lineBuffer.append(c);
                    }
                }
                buffer.rewind();
                doRead();
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                manager.onConnectionClosed(ClientConnection.this);
            }
        });
    }

    private void send(String method, String[] args) {

        try {
            StringBuffer sendBuffer = new StringBuffer();

            sendBuffer.append(method);
            sendBuffer.append(':');

            for(String a : args) {
                sendBuffer.append(a);
                sendBuffer.append(',');
            }

            channel.write(ByteBuffer.wrap(sendBuffer.toString().getBytes("utf8")));

        }catch(Exception e) {
            log.severe("Error sending message o client");
            manager.onConnectionClosed(this);
        }
    }

    public String getClientSecret() {
        return clientSecret;
    }

    private void handleLine(String line) {
        log.info("Got line: " + line);

        int firstColon = line.indexOf(':');
        if(firstColon == -1) {
            log.warning("Invalid message:" + line);
            return;
        }

        String method = line.substring(0,firstColon);
        String[] arguments = line.substring(firstColon+1).split(",");


        switch (method) {
            case "auth":
                handleAuth(arguments[0]);
        }
    }

    private void handleAuth(String clientSecret) {
        log.info("Client authed as " + clientSecret);
        this.clientSecret = clientSecret;
        send("auth_sucess", new String[0]);
    }

    public void playCity(City city, SaveGame save) {
        send("load_city", new String[]{city.getCityId(), save.getDownloadUrl()});
    }



}