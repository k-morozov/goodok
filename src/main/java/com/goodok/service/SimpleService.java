package com.goodok.service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleService implements IService {
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    private ServerSocket serverSocket;

    public ArrayList<Socket> getSocketsClients() {
        return socketsClients;
    }

    private ArrayList<Socket> socketsClients;

    public static SimpleService create() {
        return new SimpleService();
    }

    @Override
    public void init(int port) {
        try {
            serverSocket = createServerSocket(port);
            socketsClients = new ArrayList<>();
        } catch (Exception ex) {
            System.out.println("Exception when create socket");
            throw new IllegalArgumentException(
                    "Port value out of range: " + port);
        }
    }

    @Override
    public void run() {
        try {
            ArrayList<Thread> threads = new ArrayList<>();
            while (true) {
                Socket socketClient = getSocketConnection(serverSocket);
                if (socketClient == null) {
                    break;
                }
                getSocketsClients().add(socketClient);

                HandlerConnection handlerConnection = new HandlerConnection(socketClient, this::notifyClients, this::removeClient);
                Thread thread = new Thread(handlerConnection);
                threads.add(thread);
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (Exception ex) {
            System.out.println("Exception when client connecting");
            ex.printStackTrace();
        }
    }

    @Override
    public void notifyClients(Socket fromClient, String msg) {
        for (Socket sock: getSocketsClients()) {
            try{
                if (sock == fromClient) {
                    continue;
                }
                if (sock.isConnected()) {
                    OutputStream os = sock.getOutputStream();
                    DataOutputStream out = new DataOutputStream(os);
                    out.writeUTF(msg + '\n');
                    out.flush();
                }
            } catch (Exception ex) {
                System.out.println("SimpleService.send");
            }
        }
    }

    private void removeClient(Socket socketClient) {
        try {
            socketsClients.remove(socketClient);
        } catch (Exception ex) {
            System.out.println("SimpleService.removeClient");
        }
    }

    protected ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    protected Socket getSocketConnection(ServerSocket socket) throws IOException {
        return socket.accept();
    }


}
