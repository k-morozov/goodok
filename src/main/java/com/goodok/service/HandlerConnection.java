package com.goodok.service;

import java.io.BufferedReader;
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
            return;
        }

        try {
            System.out.println("New client from: " +
                    _socketClient.getRemoteSocketAddress());
            InputStream sin = _socketClient.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(sin));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    System.out.println("Close connection");
                    callbackRemoveClient.accept(_socketClient);
                    break;
                }
                System.out.println("read: " + line);
                callbackSend.accept(_socketClient, line);
            }
        } catch (Exception ex) {
            System.out.println("Exception in thread");
        }
    }
}
