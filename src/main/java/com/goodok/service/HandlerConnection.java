package com.goodok.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class HandlerConnection implements Runnable {
    private final Socket _socketClient;
    private final BiConsumer<Socket, String> callbackSend;

    private final Consumer<Socket> callbackRemoveClient;

    public HandlerConnection(Socket socketClient, BiConsumer<Socket, String> funcSend, Consumer<Socket> funcRemoveClient) {
        _socketClient = socketClient;
        callbackSend = funcSend;
        callbackRemoveClient = funcRemoveClient;
    }

    @Override
    public void run() {
        if (_socketClient == null) {
            throw new IllegalArgumentException("socket == null");
        }

        System.out.println("New client from: " +
                _socketClient.getRemoteSocketAddress());

        try (BufferedReader in = getReaderFromSocket()) {
            while (true) {
                processCurrentConnection(in);
            }
        } catch (Exception ex) {
            System.err.println("finish HandlerConnection.run");
        }
    }

    protected void processCurrentConnection(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            System.out.println("Close connection");
            sendRemoveClient(_socketClient);
            throw new IllegalArgumentException("Empty line: close connection");
        }
        System.out.println("read: " + line);
        sendToClient(_socketClient, line);
    }

    protected BufferedReader getReaderFromSocket() throws IOException {
        InputStream sin = _socketClient.getInputStream();
        return new BufferedReader(new InputStreamReader(sin));
    }

    protected void sendToClient(Socket socketClient, String msg) {
        callbackSend.accept(_socketClient, msg);
    }

    protected void sendRemoveClient(Socket socketClient) {
        callbackRemoveClient.accept(_socketClient);
    }
}
